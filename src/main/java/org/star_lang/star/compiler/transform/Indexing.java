package org.star_lang.star.compiler.transform;

import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.canonical.*;
import org.star_lang.star.compiler.type.TypeCheckerUtils;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.type.Location;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
/*
 * Copyright (c) 2015. Francis G. McCabe
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

class Indexing {

  private static void pullOutIndexTermsFromPtn(IContentPattern ptn, ICondition cond, List<Variable> definedVars,
                                               List<IContentExpression> keyExps, List<IContentExpression> columns) {
    if (ptn instanceof ConstructorPtn) {
      ConstructorPtn psCon = (ConstructorPtn) ptn;
      Location loc = ptn.getLoc();

      for (int ix = 0; ix < psCon.arity(); ix++) {
        IContentPattern arg = psCon.getArg(ix);
        IContentExpression ixCol = TypeCheckerUtils.integerLiteral(loc, ix);
        if (arg instanceof ScalarPtn) {
          keyExps.add(((ScalarPtn) arg).asScalar());
          columns.add(ixCol);
        } else if (arg instanceof Variable) {
          Variable var = (Variable) arg;
          if (VarAnalysis.isDefined(var, definedVars)) {
            keyExps.add(var);
            columns.add(ixCol);
          } else
            Indexing.pulloutFromCond(var, ixCol, cond, keyExps, columns);
        }
      }
    } else if (ptn instanceof TuplePtn) {
      TuplePtn tpl = (TuplePtn) ptn;
      Location loc = ptn.getLoc();

      for (int ix = 0; ix < tpl.arity(); ix++) {
        IContentPattern arg = tpl.getArg(ix);
        IContentExpression ixCol = TypeCheckerUtils.integerLiteral(loc, ix);
        if (arg instanceof ScalarPtn) {
          keyExps.add(((ScalarPtn) arg).asScalar());
          columns.add(ixCol);
        } else if (arg instanceof Variable) {
          Variable var = (Variable) arg;
          if (VarAnalysis.isDefined(var, definedVars)) {
            keyExps.add(var);
            columns.add(ixCol);
          } else
            Indexing.pulloutFromCond(var, ixCol, cond, keyExps, columns);
        }
      }
    } else if (ptn instanceof RecordPtn) {
      RecordPtn agCon = (RecordPtn) ptn;

      Map<String, Integer> memberIndex = agCon.getIndex();

      argLoop:
      for (Entry<String, IContentPattern> entry : agCon.getElements().entrySet()) {
        IContentPattern argPtn = entry.getValue();

        IContentExpression column = TypeCheckerUtils.integerLiteral(ptn.getLoc(), memberIndex.get(entry.getKey()));
        if (argPtn instanceof Variable) {
          Variable arg = (Variable) argPtn;
          Indexing.pulloutFromCond(arg, column, cond, keyExps, columns);

          if (VarAnalysis.isDefined(arg, definedVars)) {
            for (Variable vr : definedVars)
              if (arg.equals(vr)) {
                Indexing.addColumnIndex(column, vr, keyExps, columns);
                continue argLoop;
              }
          }
        } else if (argPtn instanceof ScalarPtn) {
          ScalarPtn lit = (ScalarPtn) argPtn;
          Indexing.addColumnIndex(column, new Scalar(lit.getLoc(), lit.getValue()), keyExps, columns);
        }
      }
    } else if (ptn instanceof Variable && TypeUtils.isTypeInterface(ptn.getType()))
      pulloutIndexTermsFromCond(cond, definedVars, (Variable) ptn, keyExps, columns, TypeUtils.getMemberIndex(ptn
          .getType()));
    else if (ptn instanceof WherePattern) {
      WherePattern where = (WherePattern) ptn;
      pullOutIndexTermsFromPtn(where.getPtn(), CompilerUtils.conjunction(cond, where.getCond()), definedVars, keyExps,
          columns);
    }
  }

  private static void pulloutIndexTermsFromCond(ICondition cond, List<Variable> definedVars, Variable var,
                                                List<IContentExpression> keyExps, List<IContentExpression> columns, Map<String, Integer> memberIndex) {
    if (cond instanceof Conjunction) {
      pulloutIndexTermsFromCond(((Conjunction) cond).getLhs(), definedVars, var, keyExps, columns, memberIndex);
      pulloutIndexTermsFromCond(((Conjunction) cond).getRhs(), definedVars, var, keyExps, columns, memberIndex);
    } else if (CompilerUtils.isEquality(cond)) {
      // Do this twice, for symmetry
      Indexing.pulloutAccessTerms(var, memberIndex, CompilerUtils.equalityLhs(cond), CompilerUtils.equalityRhs(cond),
          keyExps, columns);
      Indexing.pulloutAccessTerms(var, memberIndex, CompilerUtils.equalityRhs(cond), CompilerUtils.equalityLhs(cond),
          keyExps, columns);
    } else if (cond instanceof Matches) {
      Matches match = (Matches) cond;
      IContentExpression lhs = match.getExp();
      IContentPattern rhs = match.getPtn();

      if (lhs instanceof Variable && lhs.equals(var)) {
        if (rhs instanceof ConstructorPtn || rhs instanceof TuplePtn || rhs instanceof RecordPtn)
          pullOutIndexTermsFromPtn(rhs, null, definedVars, keyExps, columns);
      }
    }
  }

  static private void pulloutAccessTerms(IContentExpression var, Map<String, Integer> memberIndex, IContentExpression lhs,
                                         IContentExpression exp, List<IContentExpression> keyExps, List<IContentExpression> columns) {
    if (lhs instanceof FieldAccess) {
      // Maybe this the only way to get more indexing?
      FieldAccess acc = (FieldAccess) lhs;

      if (acc.getRecord().equals(var))
        Indexing.addColumnIndex(TypeCheckerUtils.integerLiteral(acc.getLoc(), memberIndex.get(acc.getField())), exp,
            keyExps, columns);
    }
  }

  static private void pulloutFromCond(Variable var, IContentExpression column, ICondition cond,
                                      List<IContentExpression> keyExps, List<IContentExpression> columns) {
    if (cond instanceof Conjunction) {
      pulloutFromCond(var, column, ((Conjunction) cond).getLhs(), keyExps, columns);
      pulloutFromCond(var, column, ((Conjunction) cond).getRhs(), keyExps, columns);
    } else if (CompilerUtils.isEquality(cond)) {
      IContentExpression args[] = Indexing.equalArgs(cond);

      assert args.length == 2;
      if (args[0].equals(var))
        Indexing.addColumnIndex(column, args[1], keyExps, columns);
      else if (args[1].equals(var))
        Indexing.addColumnIndex(column, args[0], keyExps, columns);
    }
  }

  private static void addColumnIndex(IContentExpression column, IContentExpression exp, List<IContentExpression> keyExps,
                                     List<IContentExpression> columns) {
    if (!columns.contains(column)) {
      columns.add(column);
      keyExps.add(exp);
    }
  }

  static IContentExpression[] equalArgs(ICondition query) {
    assert CompilerUtils.isEquality(query);
    return new IContentExpression[]{CompilerUtils.equalityLhs(query), CompilerUtils.equalityRhs(query)};
  }
}
