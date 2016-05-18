package org.star_lang.star.compiler.type;

import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.ast.*;
import org.star_lang.star.compiler.canonical.*;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.type.TypeChecker.PtnVarHandler;
import org.star_lang.star.compiler.util.GenSym;
import org.star_lang.star.compiler.util.StringUtils;
import org.star_lang.star.compiler.util.Wrapper;
import org.star_lang.star.data.IList;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.operators.arrays.runtime.ArrayIndexSlice.ArrayEl;
import org.star_lang.star.operators.arrays.runtime.ArrayOps.ArrayHasSize;

import java.util.Stack;

import static org.star_lang.star.data.type.StandardTypes.*;

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

public class PtnQuoter implements IAbstractVisitor {
  private final Stack<IContentPattern> stack = new Stack<>();
  private final Wrapper<ICondition> cond;
  private final Dictionary cxt;
  private final Dictionary outer;
  private final TypeChecker checker;
  private final PtnVarHandler varHandler;
  private final ErrorReport errors;

  public PtnQuoter(Dictionary cxt, Dictionary outer, Wrapper<ICondition> cond, PtnVarHandler varHandler,
                   TypeChecker checker, ErrorReport errors) {
    this.cxt = cxt;
    this.outer = outer;
    this.checker = checker;
    this.cond = cond;
    this.varHandler = varHandler;
    this.errors = errors;
  }

  IContentPattern quoted(IAbstract term) {
    term.accept(this);
    assert stack.size() == 1;
    return stack.pop();
  }

  private PtnQuoter fork(Wrapper<ICondition> cond) {
    return new PtnQuoter(cxt, outer, cond, varHandler, checker, errors);
  }

  @Override
  public void visitApply(Apply app) {
    Location loc = app.getLoc();
    if (CompilerUtils.isUnQuoted(app) || CompilerUtils.isQuestion(app))
      stack.push(checker.typeOfPtn(CompilerUtils.unquotedExp(app), astType, cond, cxt, outer, varHandler));
    else if (Abstract.isBinary(app, StandardNames.QUESTION)) {
      IAbstract lhs = Abstract.binaryLhs(app);
      IAbstract rhs = Abstract.binaryRhs(app);

      IAbstract matchTest = null;

      if (lhs instanceof Name) {
        String sym = Abstract.getId(lhs);
        Name anon = new Name(loc, "_");

        if (sym.equals(StandardTypes.INTEGER))
          matchTest = Abstract.binary(loc, StandardNames.MATCHING, Abstract
              .binary(loc, IntegerLiteral.name, anon, anon), rhs);
        else if (sym.equals(StandardTypes.LONG))
          matchTest = Abstract.binary(loc, StandardNames.MATCHING, Abstract.binary(loc, LongLiteral.name, anon, anon),
              rhs);
        else if (sym.equals(StandardTypes.FLOAT))
          matchTest = Abstract.binary(loc, StandardNames.MATCHING, Abstract.binary(loc, FloatLiteral.name, anon, anon),
              rhs);
        else if (sym.equals(StandardTypes.DECIMAL))
          matchTest = Abstract.binary(loc, StandardNames.MATCHING, Abstract.binary(loc, BigDecimalLiteral.name, anon,
              anon), rhs);
        else if (sym.equals(StandardNames.IDENTIFIER))
          matchTest = Abstract.binary(loc, StandardNames.MATCHING, Abstract.binary(loc, Name.name, anon, anon), rhs);
        else if (sym.equals(StandardTypes.STRING))
          matchTest = Abstract.binary(loc, StandardNames.MATCHING,
              Abstract.binary(loc, StringLiteral.name, anon, anon), rhs);
        else
          errors.reportError(StringUtils.msg("invalid quote pattern: ", lhs), lhs.getLoc());
        if (matchTest != null)
          stack.push(checker.typeOfPtn(matchTest, astType, cond, cxt, outer, varHandler));
        else
          stack.push(checker.typeOfPtn(rhs, astType, cond, cxt, outer, varHandler));
      } else {
        errors.reportError(StringUtils.msg("expecting an identifier, not: `", lhs, "'"), lhs.getLoc());
        stack.push(checker.typeOfPtn(rhs, astType, cond, cxt, outer, varHandler));
      }
    } else if (Abstract.isBinary(app, StandardNames.MACRO_APPLY)) {
      Abstract.binaryLhs(app).accept(this);
      IAbstract rhs = Abstract.binaryRhs(app);
      if (!Abstract.isUnary(rhs, StandardNames.QUESTION)) {
        errors.reportError(StringUtils.msg("expecting a ? after ", StandardNames.MACRO_APPLY), rhs.getLoc());
      } else {
        IContentPattern args = checker.typeOfPtn(Abstract.unaryArg(rhs), TypeUtils.arrayType(astType), cond, cxt,
            outer, varHandler);
        IContentPattern op = stack.pop();
        IContentPattern lcPtn = Variable.anonymous(app.getLoc(), Location.type);
        stack.push(new ConstructorPtn(app.getLoc(), Apply.name, astType, lcPtn, op, args));
      }
    } else {
      app.getOperator().accept(this);
      IContentPattern op = stack.pop();

      IList args = app.getArgs();
      int arity = args.size();

      Variable argsVar = new Variable(loc, TypeUtils.arrayType(astType), GenSym.genSym("__args"));
      Variable hasSize = new Variable(loc, ArrayHasSize.type(), ArrayHasSize.name);
      CompilerUtils.extendCondition(cond, new IsTrue(loc, Application.apply(loc, StandardTypes.rawBoolType, hasSize,
          argsVar, rawInt(loc, arity))));

      for (int ix = 0; ix < arity; ix++) {
        Wrapper<ICondition> subCond = Wrapper.create(CompilerUtils.truth);
        Variable get = new Variable(loc, ArrayEl.type(), ArrayEl.name);
        IContentExpression getter = new Application(loc, astType, get, argsVar, rawInt(loc, ix));
        PtnQuoter fork = fork(subCond);
        ((IAbstract) args.getCell(ix)).accept(fork);
        CompilerUtils.extendCondition(cond, new Matches(loc, getter, fork.stack.pop()));
        CompilerUtils.extendCondition(cond, subCond.get());
      }

      IContentPattern lcPtn = Variable.anonymous(app.getLoc(), Location.type);
      stack.push(new ConstructorPtn(app.getLoc(), Apply.name, astType, lcPtn, op, argsVar));
    }
  }

  private static IContentExpression rawInt(Location loc, int ix) {
    return new Scalar(loc, rawIntegerType, ix);
  }

  @Override
  public void visitBooleanLiteral(BooleanLiteral lit) {
    IContentPattern loc = Variable.anonymous(lit.getLoc(), Location.type);
    IContentPattern bool = new ScalarPtn(lit.getLoc(), booleanType, Factory.newBool(lit.getLit()));
    stack.push(new ConstructorPtn(lit.getLoc(), BooleanLiteral.name, astType, loc, bool));
  }

  @Override
  public void visitStringLiteral(StringLiteral lit) {
    IContentPattern loc = Variable.anonymous(lit.getLoc(), Location.type);
    IContentPattern str = TypeCheckerUtils.stringPtn(lit.getLoc(), lit.getLit());

    stack.push(new ConstructorPtn(lit.getLoc(), StringLiteral.name, astType, loc, str));
  }

  @Override
  public void visitFloatLiteral(FloatLiteral lit) {
    IContentPattern loc = Variable.anonymous(lit.getLoc(), Location.type);
    IContentPattern flt = TypeCheckerUtils.floatPtn(lit.getLoc(), lit.getLit());
    stack.push(new ConstructorPtn(lit.getLoc(), FloatLiteral.name, astType, loc, flt));
  }

  @Override
  public void visitIntegerLiteral(IntegerLiteral lit) {
    IContentPattern loc = Variable.anonymous(lit.getLoc(), Location.type);
    IContentPattern ix = TypeCheckerUtils.integerPtn(lit.getLoc(), lit.getLit());
    stack.push(new ConstructorPtn(lit.getLoc(), IntegerLiteral.name, astType, loc, ix));
  }

  @Override
  public void visitLongLiteral(LongLiteral lit) {
    IContentPattern loc = Variable.anonymous(lit.getLoc(), Location.type);
    IContentPattern lx = TypeCheckerUtils.longPtn(lit.getLoc(), lit.getLit());
    stack.push(new ConstructorPtn(lit.getLoc(), LongLiteral.name, astType, loc, lx));
  }

  @Override
  public void visitBigDecimal(BigDecimalLiteral lit) {
    IContentPattern loc = Variable.anonymous(lit.getLoc(), Location.type);
    IContentPattern big = TypeCheckerUtils.decimalPtn(lit.getLoc(), lit.getLit());
    stack.push(new ConstructorPtn(lit.getLoc(), BigDecimalLiteral.name, astType, loc, big));
  }

  @Override
  public void visitName(Name name) {
    IContentPattern loc = Variable.anonymous(name.getLoc(), Location.type);
    IContentPattern str = TypeCheckerUtils.stringPtn(name.getLoc(), name.getId());
    stack.push(new ConstructorPtn(name.getLoc(), Name.name, astType, loc, str));
  }
}