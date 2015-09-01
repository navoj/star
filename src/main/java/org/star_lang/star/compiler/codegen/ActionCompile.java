package org.star_lang.star.compiler.codegen;

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

import org.star_lang.star.compiler.cafe.compile.ISpec;
import org.star_lang.star.compiler.cafe.compile.cont.IContinuation;
import org.star_lang.star.compiler.canonical.*;

/**
 * Created by fgm on 8/26/15.
 */
public class ActionCompile implements TransformAction<ISpec, ISpec, ISpec, ISpec, ISpec, IContinuation> {

  @Override
  public ISpec transformAssertAction(AssertAction act, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformAssignment(Assignment act, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformCaseAction(CaseAction exp, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformConditionalAction(ConditionalAction act, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformExceptionHandler(ExceptionHandler handler, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformForLoop(ForLoopAction loop, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformIgnored(Ignore ignore, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformLetAction(LetAction let, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformWhileLoop(WhileAction act, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformNullAction(NullAction act, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformProcedureCallAction(ProcedureCallAction call, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformRaiseAction(RaiseAction raise, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformSequence(Sequence sequence, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformValisAction(ValisAction act, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformVarDeclaration(VarDeclaration var, IContinuation cont) {
    return null;
  }
}
