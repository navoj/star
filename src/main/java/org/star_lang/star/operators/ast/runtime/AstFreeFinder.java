package org.star_lang.star.operators.ast.runtime;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.ast.Apply;
import org.star_lang.star.compiler.ast.DefaultAbstractVisitor;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.ast.Name;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.data.IValue;

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

final class AstFreeFinder extends DefaultAbstractVisitor {
  private final Set<Name> found;
  private final Stack<Set<Name>> excludes = new Stack<>();

  AstFreeFinder(Set<Name> found, Set<Name> excludes) {
    this.found = found;
    this.excludes.push(excludes);
  }

  @Override
  public void visitApply(Apply app) {
    if (CompilerUtils.isQuoted(app)) {

    } else if (CompilerUtils.isAnonAggConLiteral(app)) {
      for (IAbstract el : CompilerUtils.unWrap(CompilerUtils.anonAggEls(app))) {
        if (Abstract.isBinary(el, StandardNames.EQUAL) || Abstract.isBinary(el, StandardNames.ASSIGN))
          Abstract.binaryRhs(el).accept(this);
      }
    } else if (CompilerUtils.isBraceTerm(app)) {
      for (IAbstract el : CompilerUtils.unWrap(CompilerUtils.braceArg(app))) {
        if (Abstract.isBinary(el, StandardNames.EQUAL) || Abstract.isBinary(el, StandardNames.ASSIGN))
          Abstract.binaryRhs(el).accept(this);
      }
    } else if (CompilerUtils.isLabeledSequenceTerm(app)) {
      for (IAbstract el : CompilerUtils.unWrap(CompilerUtils.labeledContent(app)))
        el.accept(this);
    } else if (CompilerUtils.isSquareSequenceTerm(app)) {
      for (IAbstract el : CompilerUtils.unWrap(CompilerUtils.squareContent(app)))
        el.accept(this);
    } else if (CompilerUtils.isQueryTerm(app)) {
      CompilerUtils.queryQuantifier(app).accept(this);
      CompilerUtils.queryCondition(app).accept(this);
      IAbstract orderBy = CompilerUtils.queryOrderBy(app);
      if (orderBy != null)
        orderBy.accept(this);
      IAbstract using = CompilerUtils.queryOrderUsing(app);
      if (using != null)
        using.accept(this);
    } else if (CompilerUtils.isFieldAccess(app))
      CompilerUtils.fieldRecord(app).accept(this);
    else if (CompilerUtils.isLambdaExp(app)) {
      Set<Name> ptnExclusions = exclude(CompilerUtils.lambdaPtn(app));
      excludes.push(ptnExclusions);
      CompilerUtils.lambdaExp(app).accept(this);
      excludes.pop();
    } else if (CompilerUtils.isEquation(app)) {
      Set<Name> ptnExclusions = exclude(CompilerUtils.equationLhs(app));
      excludes.push(ptnExclusions);
      CompilerUtils.equationRhs(app).accept(this);
      excludes.pop();
    } else if (CompilerUtils.isActionRule(app)) {
      Set<Name> ptnExclusions = exclude(CompilerUtils.actionRuleLhs(app));
      excludes.push(ptnExclusions);
      CompilerUtils.actionRuleBody(app).accept(this);
      excludes.pop();
    } else if (CompilerUtils.isPatternRule(app)) {
      Set<Name> ptnExclusions = exclude(CompilerUtils.patternRuleBody(app));
      excludes.push(ptnExclusions);
      CompilerUtils.patternRuleHead(app).accept(this);
      excludes.pop();
    } else if (CompilerUtils.isFunctionStatement(app))
      CompilerUtils.functionRules(app).accept(this);
    else if (CompilerUtils.isProcedureStatement(app))
      CompilerUtils.procedureRules(app).accept(this);
    else if (CompilerUtils.isPatternStatement(app))
      CompilerUtils.patternRules(app).accept(this);
    else if (Abstract.isBinary(app, StandardNames.PIPE)) {
      Abstract.binaryLhs(app).accept(this);
      Abstract.binaryRhs(app).accept(this);
    } else if (CompilerUtils.isIsStatement(app)) {
      Set<Name> ptnExclusions = exclude(CompilerUtils.isStmtPattern(app));
      excludes.push(ptnExclusions);
      CompilerUtils.isStmtValue(app).accept(this);
      excludes.pop();
    } else if (CompilerUtils.isVarDeclaration(app)) {
      Set<Name> ptnExclusions = exclude(CompilerUtils.varDeclarationPattern(app));
      excludes.push(ptnExclusions);
      CompilerUtils.varDeclarationExpression(app).accept(this);
      excludes.pop();
    } else if (CompilerUtils.isLetTerm(app)) {
      Set<Name> letExcludes = excludeDefs(CompilerUtils.letDefs(app));
      excludes.push(letExcludes);
      for (IAbstract stmt : CompilerUtils.letDefs(app))
        stmt.accept(this);
      CompilerUtils.letBound(app).accept(this);
      excludes.pop();
    } else {
      for (IValue arg : app.getArgs())
        ((IAbstract) arg).accept(this);
    }
  }

  @Override
  public void visitName(Name name) {
    if (!excludes.peek().contains(name) && !StandardNames.isKeyword(name) && !StandardNames.isStandard(name))
      found.add(name);
  }

  private Set<Name> exclude(IAbstract term) {
    Set<Name> exclusions = new HashSet<>(excludes.peek());
    AstFreeFinder finder = new AstFreeFinder(exclusions, found);
    term.accept(finder);
    return exclusions;
  }

  private Set<Name> excludeDefs(Iterable<IAbstract> stmts) {
    Set<Name> exclusions = new HashSet<>(excludes.peek());
    AstFreeFinder finder = new AstFreeFinder(exclusions, found);

    for (IAbstract stmt : stmts) {
      if (CompilerUtils.isFunctionStatement(stmt)) {
        exclusions.add(CompilerUtils.functionName(stmt));
      } else if (CompilerUtils.isProcedureStatement(stmt)) {
        exclusions.add(CompilerUtils.procedureName(stmt));
      } else if (CompilerUtils.isPatternStatement(stmt)) {
        exclusions.add(CompilerUtils.patternName(stmt));
      } else if (CompilerUtils.isIsStatement(stmt)) {
        CompilerUtils.isStmtPattern(stmt).accept(finder);
      } else if (CompilerUtils.isVarDeclaration(stmt)) {
        CompilerUtils.varDeclarationPattern(stmt).accept(finder);
      }
    }

    return exclusions;
  }
}