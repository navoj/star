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

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.ast.Name;
import org.star_lang.star.compiler.cafe.CafeSyntax;
import org.star_lang.star.compiler.cafe.compile.*;
import org.star_lang.star.compiler.cafe.compile.cont.*;
import org.star_lang.star.compiler.canonical.*;
import org.star_lang.star.compiler.generate.CContext;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.compiler.util.FixedList;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.operators.assignment.runtime.Assignments;

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
    IContentExpression lval = act.getLValue();
    IContentExpression exp = act.getValue();

    Location loc = act.getLoc();
    IType type = act.getValue().getType();
    String assignmentEscape;
    if (TypeUtils.isRawBoolType(type))
      assignmentEscape = Assignments.AssignRawBool.name;
    else if (TypeUtils.isRawCharType(type))
      assignmentEscape = Assignments.AssignRawChar.name;
    else if (TypeUtils.isRawIntType(type))
      assignmentEscape = Assignments.AssignRawInteger.name;
    else if (TypeUtils.isRawLongType(type))
      assignmentEscape = Assignments.AssignRawLong.name;
    else if (TypeUtils.isRawFloatType(type))
      assignmentEscape = Assignments.AssignRawFloat.name;
    else
      assignmentEscape = Assignments.Assign.name;

    return ExpressionCompile.compileEscape(loc, assignmentEscape, new IContentExpression[]{lval, exp}, cont, cxt);
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

  private MethodNode getMtd() {
    return cxt.getMtd();
  }

  private HWM getHWM() {
    return cxt.getMtdHwm();
  }

  private ErrorReport getErrors() {
    return cxt.getErrors();
  }

  private CafeDictionary getDict() {
    return cxt.getDict();
  }

  private CafeDictionary getOuter() {
    return cxt.getOuter();
  }
}
