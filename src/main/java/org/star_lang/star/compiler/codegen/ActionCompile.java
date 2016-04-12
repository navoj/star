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
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.cafe.Names;
import org.star_lang.star.compiler.cafe.compile.*;
import org.star_lang.star.compiler.cafe.compile.cont.CallCont;
import org.star_lang.star.compiler.cafe.compile.cont.DeclareLocal;
import org.star_lang.star.compiler.cafe.compile.cont.IContinuation;
import org.star_lang.star.compiler.cafe.compile.cont.JumpCont;
import org.star_lang.star.compiler.canonical.*;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.operators.assignment.runtime.Assignments;

import java.util.List;

public class ActionCompile implements TransformAction<ISpec, ISpec, ISpec, ISpec, ISpec, IContinuation> {
  private final CodeContext cxt;

  public ActionCompile(CodeContext cxt) {
    this.cxt = cxt;
  }

  public static ISpec compile(IContentAction act, CodeContext cxt, IContinuation cont) {
    ActionCompile compiler = new ActionCompile(cxt);
    return act.transform(compiler, cont);
  }

  @Override
  public ISpec transformAssertAction(AssertAction act, IContinuation cont) {
    MethodNode mtd = getMtd();
    HWM hwm = getHWM();
    InsnList ins = mtd.instructions;
    CafeDictionary dict = getDict();
    CafeDictionary outer = getOuter();

    String exceptionType = Type.getInternalName(AssertionError.class);

    Location loc = act.getLoc();
    String msg = "assert failed at " + loc.toString();

    LabelNode badLabel = new LabelNode();

    doLineNumber(loc, mtd);

    CafeDictionary asDict = dict.fork();

    IContinuation fail = new JumpCont(badLabel);

    ConditionCompile.compile(new IsTrue(loc, act.getAssertion()), cxt.fork(asDict, outer), fail, cont);

    Utils.jumpTarget(ins, badLabel);
    hwm.probe(3);
    ins.add(new TypeInsnNode(Opcodes.NEW, exceptionType));
    ins.add(new InsnNode(Opcodes.DUP));
    ins.add(new LdcInsnNode(msg));
    ins.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, exceptionType, Types.INIT, "(" + Types.OBJECT_SIG + ")V"));
    ins.add(new InsnNode(Opcodes.ATHROW));

    dict.migrateFreeVars(asDict);

    return SrcSpec.prcSrc;
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
    MethodNode mtd = getMtd();
    InsnList ins = mtd.instructions;
    CafeDictionary dict = getDict();
    CafeDictionary outer = getOuter();

    Location loc = act.getLoc();

    LabelNode thenLabel = new LabelNode();
    LabelNode elseLabel = new LabelNode();

    doLineNumber(loc, mtd);

    CafeDictionary asDict = dict.fork();

    IContinuation fail = new JumpCont(elseLabel);
    IContinuation then = new JumpCont(thenLabel);
    CodeContext thenCxt = cxt.fork(asDict, outer);

    ConditionCompile.compile(act.getCond(), thenCxt, fail, then);
    Utils.jumpTarget(ins, thenLabel);

    compile(act.getThPart(), thenCxt, cont);

    Utils.jumpTarget(ins, elseLabel);
    act.getElPart().transform(this, cont);

    dict.migrateFreeVars(asDict);

    return SrcSpec.prcSrc;
  }

  @Override
  public ISpec transformExceptionHandler(ExceptionHandler handler, IContinuation cont) {
    Location loc = handler.getLoc();
    MethodNode mtd = getMtd();
    InsnList ins = mtd.instructions;
    doLineNumber(loc);

    CafeDictionary forked = getDict().fork();

    LabelNode start = new LabelNode();
    LabelNode except = new LabelNode();
    LabelNode exceptExit = new LabelNode();

    ins.add(start);

    CodeContext bodyCxt = cxt.fork(except).fork(forked, getOuter());
    compile(handler.getBody(), bodyCxt, new JumpCont(exceptExit));

    ins.add(except);

    ISpec desc = SrcSpec.typeSpec(loc, StandardTypes.exceptionType, getDict(), cxt.getBldCat(), getErrors());

    DeclareLocal declare = new DeclareLocal(loc, Names.EXCEPTION_VAR, desc, AccessMode.readOnly, forked, exceptExit);

    declare.cont(desc, getOuter(), loc, cxt);

    compile(handler.getHandler(), bodyCxt, new JumpCont(exceptExit));

    getDict().migrateFreeVars(forked);

    ins.add(exceptExit);
    mtd.tryCatchBlocks.add(new TryCatchBlockNode(start, except, except, Types.EVALUATION_EXCEPTION));

    return cont.cont(SrcSpec.prcSrc, getDict(), loc, cxt);
  }

  @Override
  public ISpec transformForLoop(ForLoopAction loop, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformIgnored(Ignore ignore, IContinuation cont) {

    MethodNode mtd = getMtd();
    InsnList ins = mtd.instructions;

    Location loc = ignore.getLoc();

    doLineNumber(loc);

    return ExpressionCompile.compile(ignore.getIgnored(), new CallCont(ins, cont), cxt);
  }

  @Override
  public ISpec transformLetAction(LetAction let, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformWhileLoop(WhileAction act, IContinuation cont) {
    CafeDictionary forked = getDict().fork();
    LabelNode loopLbl = new LabelNode();
    IContinuation aboutTurn = new JumpCont(loopLbl);
    InsnList ins = getIns();
    final CodeContext condCxt = cxt.fork(forked, getOuter());

    Utils.jumpTarget(ins, loopLbl);

    if (!CompilerUtils.isTrivial(act.getControl())) {
      ConditionCompile.compile(act.getControl(), condCxt, cont, aboutTurn);
    }

    compile(act.getBody(), condCxt, aboutTurn);
    getDict().migrateFreeVars(forked);

    return SrcSpec.prcSrc;
  }

  @Override
  public ISpec transformNullAction(NullAction act, IContinuation cont) {
    return cont.cont(SrcSpec.prcSrc, getDict(), act.getLoc(), cxt);
  }

  @Override
  public ISpec transformProcedureCallAction(ProcedureCallAction call, IContinuation cont) {
    Location loc = call.getLoc();

    MethodNode mtd = cxt.getMtd();
    InsnList ins = mtd.instructions;
    doLineNumber(call.getLoc(), mtd);
    CallCont callCont = new CallCont(ins, cont);

    return ExpressionCompile.compileFunCall(loc, call.getProc(), call.getArgs(), callCont, cxt);
  }

  @Override
  public ISpec transformRaiseAction(AbortAction raise, IContinuation cont) {
    doLineNumber(raise.getLoc());

    HWM hwm = getHWM();
    int mark = hwm.mark();
    InsnList ins = getIns();
    LabelNode nxt = new LabelNode();
    IContinuation nxtCont = new JumpCont(nxt);
    String exceptionType = Type.getInternalName(EvaluationException.class);

    ExpressionCompile.compile(raise.getABort(), nxtCont, cxt);
    Utils.jumpTarget(ins, nxt);
    ins.add(new TypeInsnNode(Opcodes.CHECKCAST, exceptionType));
    ins.add(new InsnNode(Opcodes.ATHROW));

    hwm.reset(mark);
    return cont.cont(SrcSpec.prcSrc, getDict(), raise.getLoc(), cxt);
  }

  @Override
  public ISpec transformSequence(Sequence sequence, IContinuation cont) {
    List<IContentAction> body = sequence.getActions();
    if (!body.isEmpty()) {
      int length = body.size();
      LabelNode endLabel = new LabelNode();
      CodeContext innerCxt = cxt.fork(endLabel);
      InsnList ins = getIns();

      for (int ix = 0; ix < length - 1; ix++) {
        LabelNode nxt = new LabelNode();
        IContinuation nxtCont = new JumpCont(nxt);

        compile(body.get(ix), innerCxt, nxtCont);

        Utils.jumpTarget(ins, nxt);
      }

      compile(body.get(length - 1), innerCxt, cont);
      Utils.jumpTarget(ins, endLabel);
      return SrcSpec.prcSrc;
    } else
      return cont.cont(SrcSpec.prcSrc, getDict(), sequence.getLoc(), cxt);
  }

  @Override
  public ISpec transformValisAction(ValisAction act, IContinuation cont) {
    IContentExpression value = act.getValue();
    doLineNumber(act.getLoc());
    return ExpressionCompile.compile(value, cxt.getValisCont(), cxt);
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

  private void doLineNumber(Location loc) {
    doLineNumber(loc, getMtd());
  }

  private MethodNode getMtd() {
    return cxt.getMtd();
  }

  private InsnList getIns() {
    MethodNode mtd = getMtd();
    return mtd.instructions;
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
