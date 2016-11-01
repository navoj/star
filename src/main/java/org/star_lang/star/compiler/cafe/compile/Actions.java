package org.star_lang.star.compiler.cafe.compile;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.star_lang.star.code.repository.CodeCatalog;
import org.star_lang.star.code.repository.CodeRepository;
import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.ast.AApply;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.ast.Name;
import org.star_lang.star.compiler.cafe.CafeSyntax;
import org.star_lang.star.compiler.cafe.Names;
import org.star_lang.star.compiler.cafe.compile.CaseCompile.ICaseCompile;
import org.star_lang.star.compiler.cafe.compile.Theta.IThetaBody;
import org.star_lang.star.compiler.cafe.compile.cont.AssignmentCont;
import org.star_lang.star.compiler.cafe.compile.cont.CallCont;
import org.star_lang.star.compiler.cafe.compile.cont.CheckCont;
import org.star_lang.star.compiler.cafe.compile.cont.DeclareLocal;
import org.star_lang.star.compiler.cafe.compile.cont.IContinuation;
import org.star_lang.star.compiler.cafe.compile.cont.JumpCont;
import org.star_lang.star.compiler.cafe.compile.cont.NullCont;
import org.star_lang.star.compiler.cafe.compile.cont.PatternCont;
import org.star_lang.star.compiler.cafe.compile.cont.StoreCont;
import org.star_lang.star.compiler.cafe.compile.cont.ThrowContinuation;
import org.star_lang.star.compiler.cafe.type.CafeTypeDescription;
import org.star_lang.star.compiler.type.Freshen;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IList;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.operators.ICafeBuiltin;
import org.star_lang.star.operators.Intrinsics;

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

public class Actions {
  private static final Map<String, ICompileAction> handlers = new HashMap<>();
  public static final String ASSERT_ENABLED = "$assertionsDisabled";

  static {
    handlers.put(Names.ASSIGN, new CompileAssignment());
    handlers.put(Names.ASSERT, new CompileAssert());
    handlers.put(Names.IGNORE, new CompileIgnore());
    handlers.put(Names.BLOCK, new CompileBlock());
    handlers.put(Names.SWITCH, new CompileSwitchAction());
    handlers.put(Names.FCALL, new CompileCall());
    handlers.put(Names.ESCAPE, new CompileEscape());
    handlers.put(Names.CATCH, new CompileCatchAction());
    handlers.put(Names.IF, new CompileConditional());
    handlers.put(Names.IS, new CompileIsDecl());
    handlers.put(Names.LET, new CompileLet());
    handlers.put(Names.LOOP, new CompileLoop());
    handlers.put(Names.THROW, new CompileThrow());
    handlers.put(Names.VALIS, new CompileValis());
    handlers.put(Names.VAR, new CompileVarDecl());
    handlers.put(Names.WHILE, new CompileWhile());
  }

  private Actions() {
  }

  public static void compileAction(IAbstract term, IContinuation cont, CodeContext ccxt) {
    ErrorReport errors = ccxt.getErrors();
    CafeDictionary dict = ccxt.getDict();

    if (term instanceof AApply) {
      AApply app = (AApply) term;
      ICompileAction handler = handlers.get(app.getOp());
      if (handler != null)
        handler.handleAction(term, cont, ccxt);
      else
        errors.reportError("cannot find compiler for action:" + term, term.getLoc());
    } else if (Abstract.isName(term, Names.NOTHING))
      cont.cont(SrcSpec.prcSrc, dict, term.getLoc(), ccxt);
    else
      errors.reportError("invalid form of action", term.getLoc());
  }

  // A call to a escape looks like:
  // (op args...);

  private static class CompileEscape implements ICompileAction {
    @Override
    public void handleAction(IAbstract call, IContinuation cont, CodeContext ccxt) {
      MethodNode mtd = ccxt.getMtd();
      HWM hwm = ccxt.getMtdHwm();
      CodeCatalog bldCat = ccxt.getBldCat();
      CodeRepository repository = ccxt.getRepository();
      CafeDictionary dict = ccxt.getDict();
      CafeDictionary outer = ccxt.getOuter();

      InsnList ins = mtd.instructions;
      String funName = CafeSyntax.escapeOp(call);
      Location loc = call.getLoc();
      final ErrorReport errors = ccxt.getErrors();

      VarInfo var = Theta.varReference(funName, dict, outer, loc, errors);

      IType varType = Freshen.freshenForUse(var.getType());
      if (TypeUtils.isFunType(varType)) {
        int mark = hwm.getDepth();
        IList args = CafeSyntax.escapeArgs(call);

        if (TypeUtils.arityOfFunctionType(varType) != args.size())
          errors.reportError("expecting " + TypeUtils.arityOfFunctionType(varType) + " arguments", loc);
        else {
          // preamble to access the appropriate value
          assert var.getKind() == JavaKind.builtin;

          final ISpec[] argSpecs;

          ICafeBuiltin builtin = Intrinsics.getBuiltin(funName);

          if (builtin instanceof Inliner)
            ((Inliner) builtin).preamble(mtd, hwm);
          else if (!var.isStatic()) {
            hwm.bump(1);
            String javaName = Expressions.escapeReference(var.getName(), dict, var.getJavaType(), var.getJavaSig()
            );

            ins.add(new FieldInsnNode(Opcodes.GETSTATIC, Expressions.escapeOwner(var.getName(), dict), javaName, var
                .getJavaSig()));
          }
          if (var.getJavaInvokeSig().equals(IFuncImplementation.IFUNCTION_INVOKE_SIG)) {
            argSpecs = SrcSpec.generics(varType, dict, bldCat, repository, errors, loc);

            Expressions.argArray(args, argSpecs, ccxt);
          } else {
            argSpecs = SrcSpec.typeSpecs(var.getJavaInvokeSig(), dict, bldCat, errors, loc);

            Expressions.compileArgs(args, argSpecs, ccxt);
          }

          // actually invoke the escape
          if (builtin instanceof Inliner) {
            ((Inliner) builtin).inline(dict.getOwner(), mtd, hwm, loc);
            hwm.reset(mark);
            cont.cont(SrcSpec.voidSrc, dict, loc, ccxt);
          } else if (var.isStatic()) {
            String funSig = var.getJavaInvokeSig();
            String classSig = var.getJavaType();

            ins.add(new MethodInsnNode(Opcodes.INVOKESTATIC, classSig, var.getJavaInvokeName(), funSig));
          } else if (var.getJavaInvokeSig().equals(IFuncImplementation.IFUNCTION_INVOKE_SIG)) {
            ins.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Types.IFUNC, Names.ENTERFUNCTION,
                IFuncImplementation.IFUNCTION_INVOKE_SIG));
          } else {
            String methodName = var.getJavaInvokeName();
            String funSig = var.getJavaInvokeSig();
            String classSig = var.getJavaType();

            ins.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, classSig, methodName, funSig));
          }
          hwm.reset(mark);

          ISpec resltSpec = argSpecs[argSpecs.length - 1];
          hwm.bump(Types.stackAmnt(Types.varType(resltSpec.getType())));

          cont.cont(resltSpec, dict, loc, ccxt);
        }
      } else
        errors.reportError("tried to invoke non-function: " + funName + ":" + varType, loc);
    }
  }

  private static class CompileCall implements ICompileAction {
    @Override
    public void handleAction(IAbstract app, IContinuation cont, CodeContext ccxt) {
      Location loc = app.getLoc();
      CafeDictionary dict = ccxt.getDict();
      CafeDictionary outer = ccxt.getOuter();

      assert CafeSyntax.isFunCall(app);

      MethodNode mtd = ccxt.getMtd();
      String funName = CafeSyntax.funCallName(app);
      final ErrorReport errors = ccxt.getErrors();
      VarInfo var = Theta.varReference(funName, dict, outer, loc, errors);
      Actions.doLineNumber(loc, mtd);
      InsnList ins = mtd.instructions;
      CallCont callCont = new CallCont(ins, cont);

      if (var != null) {
        switch (var.getKind()) {
          case builtin:
            Expressions.invokeEscape(loc, var, app, callCont, ccxt);
            return;
          case general:
            Expressions.compileFunCall(loc, var, CafeSyntax.funCallArgs(app), callCont,
                ccxt);
            return;
          default:
            errors.reportError(var.getName() + " is not a function", loc);
        }
      } else
        errors.reportError(funName + " not declared", loc);

      cont.cont(SrcSpec.prcSrc, dict, loc, ccxt);
    }
  }

  // An assignment looks like:
  // (assign <lval> <expression>)
  private static class CompileAssignment implements ICompileAction {
    @Override
    public void handleAction(IAbstract term, IContinuation cont, CodeContext ccxt) {
      assert CafeSyntax.isAssignment(term);

      IAbstract lval = CafeSyntax.assignmentLval(term);
      IAbstract exp = CafeSyntax.assignmentRval(term);
      MethodNode mtd = ccxt.getMtd();
      HWM hwm = ccxt.getMtdHwm();
      final ErrorReport errors = ccxt.getErrors();
      InsnList ins = mtd.instructions;
      Location loc = lval.getLoc();
      CafeDictionary dict = ccxt.getDict();
      CafeDictionary outer = ccxt.getOuter();

      doLineNumber(loc, mtd);
      int mark = hwm.bump(0);

      if (lval instanceof Name) {
        VarInfo var = dict.find(((Name) lval).getId());
        if (var == null)
          errors.reportError("variable " + lval + " not declared", loc);
        else if (var.getAccess() == AccessMode.readOnly)
          errors.reportError("not permitted to assign to " + lval, loc);
        else
          Expressions.compileExp(exp, new StoreCont(var, dict), ccxt);
      } else if (CafeSyntax.isDot(lval)) {
        IAbstract rec = CafeSyntax.dotRecord(lval);
        String field = Abstract.getId(CafeSyntax.dotField(lval));

        if (rec instanceof Name) {
          Name name = (Name) rec;
          VarInfo var = Theta.varReference(name.getId(), dict, outer, loc, errors);

          if (var == null)
            errors.reportError("variable " + rec + " not declared", rec.getLoc());
          else if (var.getAccess() == AccessMode.readOnly)
            errors.reportError("not permitted to modify " + lval, loc);
          else if (!TypeUtils.isTypeVar(var.getType()) && !dict.hasAttribute(var.getType(), field))
            errors.reportError(rec + " does not have field " + field, loc);
          else {
            LabelNode nxLbl = new LabelNode();
            Expressions.compileExp(rec, new CheckCont(nxLbl, var, dict), ccxt);
            Utils.jumpTarget(ins, nxLbl);

            if (TypeUtils.isTypeVar(var.getType())) {
              ins.add(new TypeInsnNode(Opcodes.CHECKCAST, Types.IRECORD));
              hwm.probe(1);
              ins.add(new LdcInsnNode(Utils.javaIdentifierOf(field)));
              LabelNode nxLbl2 = new LabelNode();
              Expressions.compileExp(exp, new CheckCont(nxLbl2, SrcSpec.generalSrc, dict), ccxt);
              Utils.jumpTarget(ins, nxLbl2);

              ins.add(new TypeInsnNode(Opcodes.CHECKCAST, Types.IVALUE));
              ins.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Types.IRECORD, Constructors.SET_MEMBER,
                  Constructors.SET_MEMBER_SIG));
            } else {
              String javaType = dict.javaName(var.getType());
              ins.add(new TypeInsnNode(Opcodes.CHECKCAST, javaType));
              LabelNode nxLbl2 = new LabelNode();
              ISpec fieldSpec = dict.getFieldSpec(var.getType(), field);
              if (fieldSpec == null)
                fieldSpec = SrcSpec.generalSrc;
              Expressions.compileExp(exp, new CheckCont(nxLbl2, fieldSpec, dict),
                  ccxt);
              Utils.jumpTarget(ins, nxLbl2);

              String setter = Types.setterName(dict.fieldJavaName(var.getType(), field));
              ins.add(new TypeInsnNode(Opcodes.CHECKCAST, Types.IVALUE));
              ins.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, javaType, setter, "(" + Types.IVALUE_SIG + ")V"));
            }
          }
        } else
          errors.reportError("may only access fields of variables, not :" + rec, rec.getLoc());
      } else {
        LabelNode exLabel = new LabelNode();
        IContinuation succ = new JumpCont(exLabel);
        IContinuation fail = new ThrowContinuation("initialization failed");

        Expressions.compileExp(exp, new AssignmentCont(lval, dict, outer,
            AccessMode.readWrite, exLabel, succ, fail), ccxt);
        Utils.jumpTarget(mtd.instructions, exLabel);
        cont.cont(SrcSpec.prcSrc, dict, loc, ccxt);

        hwm.reset(mark);
        cont.cont(SrcSpec.prcSrc, dict, loc, ccxt);
      }
    }
  }

  // A block looks like
  // {A1,...,An}
  private static class CompileBlock implements ICompileAction {
    @Override
    public void handleAction(IAbstract term, IContinuation cont, CodeContext ccxt) {
      assert CafeSyntax.isBlock(term);
      MethodNode mtd = ccxt.getMtd();
      CafeDictionary dict = ccxt.getDict();
      CafeDictionary outer = ccxt.getOuter();
      LabelNode endLbl = new LabelNode();
      CafeDictionary inner = dict.fork();

      IList actions = CafeSyntax.blockContents(term);
      ccxt = ccxt.fork(inner, outer);

      for (int ix = 0; ix < actions.size(); ix++) {
        IAbstract act = (IAbstract) actions.getCell(ix);
        if (ix < actions.size() - 1) {
          LabelNode exLabel = new LabelNode();

          compileAction(act, new JumpCont(exLabel), ccxt.fork(exLabel));

          Utils.jumpTarget(mtd.instructions, exLabel);
          dict.migrateFreeVars(inner);
        } else
          compileAction(act, cont, ccxt);
      }
      dict.migrateFreeVars(inner);
      // inner.dictUndo();
      mtd.instructions.add(endLbl);
    }
  }

  // A case action looks like:
  // switch <sel> in {<cases>} else <deflt>
  private static class CompileSwitchAction implements ICompileAction {
    @Override
    public void handleAction(IAbstract term, final IContinuation cont, final CodeContext ccxt) {
      assert CafeSyntax.isSwitch(term);

      IAbstract sel = CafeSyntax.switchSel(term);
      IAbstract deflt = CafeSyntax.switchDeflt(term);
      CafeDictionary dict = ccxt.getDict();
      CafeDictionary outer = ccxt.getOuter();

      MethodNode mtd = ccxt.getMtd();
      doLineNumber(sel.getLoc(), mtd);
      final ErrorReport errors = ccxt.getErrors();

      assert sel instanceof Name;
      VarInfo var = Theta.varReference(((Name) sel).getId(), dict, outer, sel.getLoc(), errors);

      ICaseCompile handler = (term1, cont1, hcxt) -> {
        compileAction(term1, cont1, hcxt);
        return SrcSpec.prcSrc;
      };
      CaseCompile.compileSwitch(term.getLoc(), var, CafeSyntax.switchCases(term), deflt, handler,
          cont, ccxt);
    }
  }

  // A simple catch action looks like:
  // <action> catch <handler>
  private static class CompileCatchAction implements ICompileAction {
    @Override
    public void handleAction(IAbstract action, IContinuation cont, CodeContext ccxt) {
      assert CafeSyntax.isCatchAction(action);

      Location loc = action.getLoc();

      MethodNode mtd = ccxt.getMtd();
      CodeCatalog bldCat = ccxt.getBldCat();
      ErrorReport errors = ccxt.getErrors();
      CafeDictionary dict = ccxt.getDict();
      CafeDictionary outer = ccxt.getOuter();

      InsnList ins = mtd.instructions;

      doLineNumber(loc, mtd);

      CafeDictionary forked = dict.fork();

      IAbstract body = CafeSyntax.catchBody(action);
      IAbstract handler = CafeSyntax.catchHandler(action);

      LabelNode start = new LabelNode();
      LabelNode except = new LabelNode();
      LabelNode exceptExit = new LabelNode();

      ins.add(start);

      final CodeContext fCxt = ccxt.fork(except).fork(forked, outer);
      compileAction(body, new JumpCont(exceptExit), fCxt);

      ins.add(except);

      // Start code for checking exception itself
      ins.add(new TypeInsnNode(Opcodes.CHECKCAST, Types.EVALUATION_EXCEPTION));

      ISpec desc = SrcSpec.typeSpec(loc, StandardTypes.exceptionType, dict, bldCat, errors);

      DeclareLocal declare = new DeclareLocal(loc, Names.EXCEPTION_VAR, desc, AccessMode.readOnly, forked, exceptExit);

      declare.cont(desc, outer, loc, ccxt);

      compileAction(handler, new JumpCont(exceptExit), fCxt);

      dict.migrateFreeVars(forked);

      ins.add(exceptExit);
      cont.cont(SrcSpec.prcSrc, dict, loc, ccxt);

      mtd.tryCatchBlocks.add(new TryCatchBlockNode(start, except, except, Types.EVALUATION_EXCEPTION));
    }
  }

  // A conditional looks like:
  // if <test> then <then> else <else>
  private static class CompileConditional implements ICompileAction {

    @Override
    public void handleAction(IAbstract action, IContinuation cont, CodeContext ccxt) {
      assert CafeSyntax.isConditional(action);
      IAbstract condition = CafeSyntax.conditionalTest(action);
      IAbstract th = CafeSyntax.conditionalThen(action);
      IAbstract el = CafeSyntax.conditionalElse(action);
      LabelNode thLabel = new LabelNode();
      LabelNode elLabel = new LabelNode();
      MethodNode mtd = ccxt.getMtd();
      InsnList ins = mtd.instructions;
      CafeDictionary dict = ccxt.getDict();
      CafeDictionary outer = ccxt.getOuter();

      doLineNumber(condition.getLoc(), mtd);

      CafeDictionary thDict = dict.fork();
      CodeContext fCxt = ccxt.fork(thDict,outer);
      Conditions.compileCond(condition, Sense.jmpOnFail, elLabel, fCxt);
      Utils.jumpTarget(ins, thLabel);
      compileAction(th, cont, fCxt);
      dict.migrateFreeVars(thDict);
      ins.add(elLabel);
      CafeDictionary elDict = dict.fork();
      compileAction(el, cont, ccxt.fork(elDict, outer));
      dict.migrateFreeVars(elDict);
    }
  }

  // A loop action looks like:
  // (loop Action)
  private static class CompileLoop implements ICompileAction {

    @Override
    public void handleAction(IAbstract action, IContinuation cont, CodeContext ccxt) {
      assert CafeSyntax.isLoop(action);
      MethodNode mtd = ccxt.getMtd();
      InsnList ins = mtd.instructions;
      LabelNode lbl = new LabelNode();
      ins.add(lbl);

      doLineNumber(action.getLoc(), mtd);

      compileAction(CafeSyntax.loopAction(action), new JumpCont(lbl), ccxt);
    }
  }

  // A let action looks like:
  // (let (block defs) action)
  private static class CompileLet implements ICompileAction {

    @Override
    public void handleAction(IAbstract action, IContinuation cont, CodeContext ccxt) {
      final ErrorReport errors = ccxt.getErrors();
      if (CafeSyntax.isLetExp(action)) {
        final LabelNode endDefLabel = new LabelNode();
        CafeDictionary outer = ccxt.getOuter();

        IList theta = CafeSyntax.letDefs(action);

        Theta.compileDefinitions(theta, endDefLabel, new LocalDefiner(ccxt),
            new IThetaBody() {

              @Override
              public ISpec compile(CafeDictionary thetaDict, CodeCatalog bldCat, ErrorReport errors,
                                   CodeRepository repository) {
                compileAction(CafeSyntax.letBound(action), cont, ccxt.fork(endDefLabel).fork(thetaDict, outer));
                return null;
              }

              @Override
              public void introduceType(CafeTypeDescription type) {
              }
            }, action.getLoc(), ccxt);

        ccxt.getMtd().instructions.add(endDefLabel);
      } else
        errors.reportError("expecting a let action", action.getLoc());
    }
  }

  private static class CompileAssert implements ICompileAction {

    @Override
    public void handleAction(IAbstract action, IContinuation cont, CodeContext ccxt) {
      MethodNode mtd = ccxt.getMtd();
      HWM hwm = ccxt.getMtdHwm();
      InsnList ins = mtd.instructions;
      CafeDictionary dict = ccxt.getDict();
      CafeDictionary outer = ccxt.getOuter();

      String exceptionType = Type.getInternalName(AssertionError.class);

      Location loc = action.getLoc();
      String msg = "assert failed at " + loc.toString();

      LabelNode nxLabel = new LabelNode();

      doLineNumber(loc, mtd);

      CafeDictionary asDict = dict.fork();

      Conditions.compileCond(CafeSyntax.assertedCond(action), Sense.jmpOnOk, nxLabel, ccxt.fork(asDict,outer));

      hwm.probe(3);
      ins.add(new TypeInsnNode(Opcodes.NEW, exceptionType));
      ins.add(new InsnNode(Opcodes.DUP));
      ins.add(new LdcInsnNode(msg));
      ins.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, exceptionType, Types.INIT, "(" + Types.OBJECT_SIG + ")V"));
      ins.add(new InsnNode(Opcodes.ATHROW));

      Utils.jumpTarget(ins, nxLabel);
      cont.cont(SrcSpec.voidSrc, dict, loc, ccxt);
      dict.migrateFreeVars(asDict);
    }
  }

  private static class CompileIgnore implements ICompileAction {
    @Override
    public void handleAction(IAbstract action, IContinuation cont, CodeContext ccxt) {
      MethodNode mtd = ccxt.getMtd();
      InsnList ins = mtd.instructions;

      Location loc = action.getLoc();

      doLineNumber(loc, mtd);

      Expressions.compileExp(CafeSyntax.ignoredExp(action), new CallCont(ins, cont), ccxt);
    }
  }

  private static class CompileThrow implements ICompileAction {

    @Override
    public void handleAction(IAbstract term,
                             IContinuation cont, CodeContext ccxt) {
      assert CafeSyntax.isThrow(term);

      String exceptionType = Type.getInternalName(EvaluationException.class);
      MethodNode mtd = ccxt.getMtd();
      HWM hwm = ccxt.getMtdHwm();
      InsnList ins = mtd.instructions;

      doLineNumber(term.getLoc(), mtd);

      int mark = hwm.bump(0);

      Expressions.compileExp(CafeSyntax.thrownExp(term), new NullCont(), ccxt);

      ins.add(new TypeInsnNode(Opcodes.CHECKCAST, exceptionType));
      ins.add(new InsnNode(Opcodes.ATHROW));
      hwm.reset(mark);
    }
  }

  private static class CompileValis implements ICompileAction {

    @Override
    public void handleAction(IAbstract action,
                             IContinuation cont, CodeContext ccxt) {
      assert CafeSyntax.isValis(action);
      MethodNode mtd = ccxt.getMtd();
      doLineNumber(action.getLoc(), mtd);
      Expressions.compileExp(CafeSyntax.valisExp(action), ccxt.getValisCont(), ccxt);
    }
  }

  // A constant var declaration looks like:
  // <id>:<type> is <expression>
  private static class CompileIsDecl implements ICompileAction {

    @Override
    public void handleAction(IAbstract term, IContinuation cont, CodeContext ccxt) {
      assert CafeSyntax.isIsDeclaration(term);
      IAbstract lVal = CafeSyntax.isDeclLval(term);
      IAbstract exp = CafeSyntax.isDeclValue(term);
      Location loc = term.getLoc();
      MethodNode mtd = ccxt.getMtd();
      HWM hwm = ccxt.getMtdHwm();
      int mark = hwm.getDepth();
      CodeRepository repository = ccxt.getRepository();
      ErrorReport errors = ccxt.getErrors();
      LabelNode endLabel = ccxt.getEndLabel();
      CafeDictionary dict = ccxt.getDict();
      CafeDictionary outer = ccxt.getOuter();

      Location lValLoc = lVal.getLoc();
      if (CafeSyntax.termHasType(lVal) && Abstract.isName(lVal)) {
        String vrName = Abstract.getId(lVal);

        IType varType = CafeSyntax.termType(lVal);

        doLineNumber(loc, mtd);

        if (endLabel == null)
          errors.reportError("invalid location for variable declaration", term.getLoc());
        else {
          ISpec desc = SrcSpec.generic(lValLoc, varType, dict, repository, errors);
          DeclareLocal declare = new DeclareLocal(loc, vrName, desc, AccessMode.readOnly, dict, endLabel);

          Expressions.compileExp(exp, declare, ccxt);
        }
        cont.cont(SrcSpec.prcSrc, dict, loc, ccxt);
      } else if (CafeSyntax.isTypedTerm(lVal) && Abstract.isName(CafeSyntax.typedTerm(lVal))) {
        String vrName = Abstract.getId(CafeSyntax.typedTerm(lVal));

        IType varType = TypeAnalyser.parseType(CafeSyntax.typedType(lVal), dict, errors);

        doLineNumber(loc, mtd);

        if (endLabel == null)
          errors.reportError("invalid location for variable declaration", term.getLoc());
        else {
          ISpec desc = SrcSpec.generic(lValLoc, varType, dict, repository, errors);
          DeclareLocal declare = new DeclareLocal(loc, vrName, desc, AccessMode.readOnly, dict, endLabel);

          Expressions.compileExp(exp, declare, ccxt);
        }
        cont.cont(SrcSpec.prcSrc, dict, loc, ccxt);
      } else {
        LabelNode exLabel = new LabelNode();
        IContinuation succ = new JumpCont(exLabel);
        IContinuation fail = new ThrowContinuation("initialization failed");

        Expressions.compileExp(exp,  new PatternCont(lVal, dict, outer,
            AccessMode.readOnly, mtd, exLabel, succ, fail), ccxt);
        Utils.jumpTarget(mtd.instructions, exLabel);
        cont.cont(SrcSpec.prcSrc, dict, loc, ccxt);
      }
      hwm.reset(mark);
    }
  }

  // A reassignable var declaration looks like:
  // var <id>:<type> := <expression>
  private static class CompileVarDecl implements ICompileAction {

    @Override
    public void handleAction(IAbstract term, IContinuation cont, CodeContext ccxt) {
      assert CafeSyntax.isVarDeclaration(term);
      IAbstract lVal = CafeSyntax.varDeclLval(term);
      IAbstract exp = CafeSyntax.varDeclValue(term);
      MethodNode mtd = ccxt.getMtd();
      HWM hwm = ccxt.getMtdHwm();
      int mark = hwm.getDepth();
      CodeRepository repository = ccxt.getRepository();
      ErrorReport errors = ccxt.getErrors();
      LabelNode endLabel = ccxt.getEndLabel();
      CafeDictionary dict = ccxt.getDict();
      CafeDictionary outer = ccxt.getOuter();

      Location loc = lVal.getLoc();
      if (CafeSyntax.isTypedTerm(lVal) && Abstract.isName(CafeSyntax.typedTerm(lVal))) {
        String vrId = Abstract.getId(CafeSyntax.typedTerm(lVal));

        IType varType = TypeAnalyser.parseType(CafeSyntax.typedType(lVal), dict, errors);
        ISpec desc = SrcSpec.generic(loc, varType, dict, repository, errors);

        doLineNumber(loc, mtd);

        if (endLabel == null)
          errors.reportError("invalid location for variable declaration", term.getLoc());
        else {
          DeclareLocal declare = new DeclareLocal(loc, vrId, desc, AccessMode.readWrite, dict, endLabel);

          Expressions.compileExp(exp, declare, ccxt);
        }
        cont.cont(desc, dict, loc, ccxt);
      } else {
        LabelNode exLabel = new LabelNode();
        IContinuation succ = new JumpCont(exLabel);
        IContinuation fail = new ThrowContinuation("initialization failed");

        Expressions.compileExp(exp, new PatternCont(lVal, dict, outer,
            AccessMode.readWrite, mtd, exLabel, succ, fail), ccxt);
        Utils.jumpTarget(mtd.instructions, exLabel);
        cont.cont(SrcSpec.prcSrc, dict, loc, ccxt);
      }
      hwm.reset(mark);
    }
  }

  // A While loop looks like:
  // while(<cond>,<body>)
  private static class CompileWhile implements ICompileAction {
    @Override
    public void handleAction(IAbstract action, IContinuation cont, CodeContext ccxt) {
      Location loc = action.getLoc();
      assert CafeSyntax.isWhile(action);
      MethodNode mtd = ccxt.getMtd();
      InsnList ins = mtd.instructions;
      LabelNode loopLbl = new LabelNode();
      LabelNode endLbl = new LabelNode();
      CafeDictionary dict = ccxt.getDict();
      CafeDictionary outer = ccxt.getOuter();

      CafeDictionary forked = dict.fork();
      ins.add(loopLbl);

      doLineNumber(loc, mtd);

      if (!CompilerUtils.isTrivial(CafeSyntax.whileTest(action)))
        // We do it this way because we want to allow the condition to bind variables. Slightly
        // slower in theory but probably makes no difference these days.
        Conditions.compileCond(CafeSyntax.whileTest(action), Sense.jmpOnFail, endLbl, ccxt.fork(forked,outer));

      compileAction(CafeSyntax.whileBody(action), new JumpCont(loopLbl), ccxt.fork(endLbl).fork(forked,outer));

      dict.migrateFreeVars(forked);
      ins.add(endLbl);

      cont.cont(SrcSpec.prcSrc, dict, loc, ccxt);
    }
  }

  public static void doLineNumber(Location loc, MethodNode mtd) {
    if (!loc.equals(Location.nullLoc)) {
      LabelNode lnLbl = new LabelNode();
      mtd.instructions.add(lnLbl);
      mtd.instructions.add(new LineNumberNode(loc.getLineCnt(), lnLbl));
    }
  }
}
