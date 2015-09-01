package org.star_lang.star.compiler.canonical;


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

public interface TransformAction<A, E, P, C, D, T> {
  A transformAssertAction(AssertAction act, T context);

  A transformAssignment(Assignment act, T context);

  A transformCaseAction(CaseAction exp, T context);

  A transformConditionalAction(ConditionalAction act, T context);

  A transformExceptionHandler(ExceptionHandler handler, T context);

  A transformForLoop(ForLoopAction loop, T context);

  A transformIgnored(Ignore ignore, T context);

  A transformLetAction(LetAction let, T context);

  A transformWhileLoop(WhileAction act, T context);

  A transformNullAction(NullAction act, T context);

  A transformProcedureCallAction(ProcedureCallAction call, T context);

  A transformRaiseAction(RaiseAction raise, T context);

  A transformSequence(Sequence sequence, T context);

  A transformValisAction(ValisAction act, T context);

  A transformVarDeclaration(VarDeclaration var, T context);
}
