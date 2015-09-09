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

import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodNode;
import org.star_lang.star.compiler.cafe.compile.CodeContext;
import org.star_lang.star.compiler.cafe.compile.ISpec;
import org.star_lang.star.compiler.cafe.compile.cont.CallCont;
import org.star_lang.star.compiler.cafe.compile.cont.IContinuation;
import org.star_lang.star.compiler.canonical.*;
import org.star_lang.star.data.type.Location;

public class ActionCompile implements TransformAction<ISpec, ISpec, ISpec, ISpec, ISpec, IContinuation> {
  private final CodeContext cxt;

  public ActionCompile(CodeContext cxt) {
    this.cxt = cxt;
  }

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
    Location loc = call.getLoc();

    MethodNode mtd = cxt.getMtd();
    InsnList ins = mtd.instructions;

    CallCont callCont = new CallCont(ins, cont);

    return ExpressionCompile.compileFunCall(loc, call.getProc(), call.getArgs(), callCont, cxt);
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

  public static void doLineNumber(Location loc, MethodNode mtd) {
    if (!loc.equals(Location.nullLoc)) {
      LabelNode lnLbl = new LabelNode();
      mtd.instructions.add(lnLbl);
      mtd.instructions.add(new LineNumberNode(loc.getLineCnt(), lnLbl));
    }
  }
}
