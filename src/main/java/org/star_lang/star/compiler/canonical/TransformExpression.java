package org.star_lang.star.compiler.canonical;

/**
 * 
 * Copyright (C) 2013 Starview Inc
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA
 * 
 * @author fgm
 *
 */
public interface TransformExpression<A, E, P, C, D, T>
{
  E transformApplication(Application appl, T context);

  E transformRecord(RecordTerm record, T context);

  E transformRecordSubstitute(RecordSubstitute update, T context);

  E transformCaseExpression(CaseExpression exp, T context);

  E transformCastExpression(CastExpression exp, T context);

  E transformConditionalExp(ConditionalExp act, T context);

  E transformContentCondition(ContentCondition cond, T context);

  E transformFieldAccess(FieldAccess dot, T context);

  E transformMemo(MemoExp memo, T context);

  E transformMethodVariable(MethodVariable var, T context);

  E transformNullExp(NullExp nil, T context);

  E transformFunctionLiteral(FunctionLiteral f, T context);

  E transformLetTerm(LetTerm let, T context);

  E transformOverloaded(Overloaded over, T context);

  E transformOverloadedFieldAccess(OverloadedFieldAccess over, T context);

  E transformOverloadVariable(OverloadedVariable var, T context);

  E transformPatternAbstraction(PatternAbstraction pattern, T context);

  E transformRaiseExpression(RaiseExpression exp, T context);

  E transformReference(Shriek reference, T context);

  E transformResolved(Resolved res, T context);

  E transformScalar(Scalar scalar, T context);

  E transformConstructor(ConstructorTerm tuple, T context);

  E transformValofExp(ValofExp val, T context);

  E transformVariable(Variable variable, T context);

  E transformVoidExp(VoidExp exp, T context);
}
