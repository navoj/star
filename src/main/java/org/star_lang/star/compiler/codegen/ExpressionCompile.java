package org.star_lang.star.compiler.codegen;

import java.util.List;

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

import org.star_lang.star.compiler.cafe.compile.CodeContext;
import org.star_lang.star.compiler.cafe.compile.ISpec;
import org.star_lang.star.compiler.cafe.compile.cont.IContinuation;
import org.star_lang.star.compiler.canonical.AbortExpression;
import org.star_lang.star.compiler.canonical.Application;
import org.star_lang.star.compiler.canonical.CaseExpression;
import org.star_lang.star.compiler.canonical.CastExpression;
import org.star_lang.star.compiler.canonical.ConditionalExp;
import org.star_lang.star.compiler.canonical.ConstructorTerm;
import org.star_lang.star.compiler.canonical.ContentCondition;
import org.star_lang.star.compiler.canonical.FieldAccess;
import org.star_lang.star.compiler.canonical.FunctionLiteral;
import org.star_lang.star.compiler.canonical.IContentExpression;
import org.star_lang.star.compiler.canonical.LetTerm;
import org.star_lang.star.compiler.canonical.MemoExp;
import org.star_lang.star.compiler.canonical.MethodVariable;
import org.star_lang.star.compiler.canonical.NullExp;
import org.star_lang.star.compiler.canonical.Overloaded;
import org.star_lang.star.compiler.canonical.OverloadedFieldAccess;
import org.star_lang.star.compiler.canonical.OverloadedVariable;
import org.star_lang.star.compiler.canonical.PatternAbstraction;
import org.star_lang.star.compiler.canonical.RecordSubstitute;
import org.star_lang.star.compiler.canonical.RecordTerm;
import org.star_lang.star.compiler.canonical.Resolved;
import org.star_lang.star.compiler.canonical.Scalar;
import org.star_lang.star.compiler.canonical.Shriek;
import org.star_lang.star.compiler.canonical.TransformExpression;
import org.star_lang.star.compiler.canonical.TupleTerm;
import org.star_lang.star.compiler.canonical.ValofExp;
import org.star_lang.star.compiler.canonical.Variable;
import org.star_lang.star.compiler.canonical.VoidExp;
import org.star_lang.star.data.type.Location;

public class ExpressionCompile implements TransformExpression<ISpec, ISpec, ISpec, ISpec, ISpec, IContinuation> {
  private final CodeContext ccxt;

  public ExpressionCompile(CodeContext ccxt) {
    this.ccxt = ccxt;
  }

  public static ISpec compile(IContentExpression exp,IContinuation cont,CodeContext cxt){
    return exp.transform(new ExpressionCompile(cxt),cont);
  }

  @Override
  public ISpec transformApplication(Application appl, IContinuation cont) {
    return compileFunCall(appl.getLoc(), appl.getFunction(), appl.getArgs(), cont, ccxt);
  }

  @Override
  public ISpec transformRecord(RecordTerm record, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformRecordSubstitute(RecordSubstitute update, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformCaseExpression(CaseExpression exp, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformCastExpression(CastExpression exp, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformConditionalExp(ConditionalExp act, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformContentCondition(ContentCondition cond, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformFieldAccess(FieldAccess dot, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformMemo(MemoExp memo, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformMethodVariable(MethodVariable var, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformNullExp(NullExp nil, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformFunctionLiteral(FunctionLiteral f, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformLetTerm(LetTerm let, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformOverloaded(Overloaded over, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformOverloadedFieldAccess(OverloadedFieldAccess over, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformOverloadVariable(OverloadedVariable var, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformPatternAbstraction(PatternAbstraction pattern, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformRaiseExpression(AbortExpression exp, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformReference(Shriek reference, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformResolved(Resolved res, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformScalar(Scalar scalar, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformConstructor(ConstructorTerm tuple, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformTuple(TupleTerm tupleTerm, IContinuation context) {
    return null;
  }

  @Override
  public ISpec transformValofExp(ValofExp val, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformVariable(Variable variable, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformVoidExp(VoidExp exp, IContinuation cont) {
    return null;
  }

  public static ISpec compileFunCall(Location loc, IContentExpression proc, IContentExpression args, IContinuation callCont, CodeContext cxt) {
    return null;
  }

  public static ISpec compileEscape(Location loc,String name,IContentExpression[] args,IContinuation callCont, CodeContext cxt){
    return null;
  }
  
  public static void argArray(List<IContentExpression> args,ISpec[] argSpecs,CodeContext cxt){
    
  }
}
