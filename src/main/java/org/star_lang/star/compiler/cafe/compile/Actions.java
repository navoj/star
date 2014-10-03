package org.star_lang.star.compiler.cafe.compile;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.star_lang.star.code.repository.CodeCatalog;
import org.star_lang.star.code.repository.CodeRepository;
import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.ast.Apply;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.ast.Name;
import org.star_lang.star.compiler.cafe.CafeSyntax;
import org.star_lang.star.compiler.cafe.Names;
import org.star_lang.star.compiler.cafe.compile.CaseCompile.ICaseCompile;
import org.star_lang.star.compiler.cafe.compile.Theta.IThetaBody;
import org.star_lang.star.compiler.cafe.compile.cont.AssignmentCont;
import org.star_lang.star.compiler.cafe.compile.cont.CallCont;
import org.star_lang.star.compiler.cafe.compile.cont.CheckCont;
import org.star_lang.star.compiler.cafe.compile.cont.ComboCont;
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
import org.star_lang.star.compiler.util.GenSym;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IList;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.operators.ICafeBuiltin;
import org.star_lang.star.operators.Intrinsics;

/**
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
public class Actions
{
  private static final Map<String, ICompileAction> handlers = new HashMap<String, ICompileAction>();
  public static final String ASSERT_ENABLED = "$assertionsDisabled";

  static {
    handlers.put(Names.ASSIGN, new CompileAssignment());
    handlers.put(Names.ASSERT, new CompileAssert());
    handlers.put(Names.IGNORE, new CompileIgnore());
    handlers.put(Names.BLOCK, new CompileBlock());
    handlers.put(Names.SWITCH, new CompileSwitchAction());
    handlers.put(Names.SYNC, new CompileSyncAction());
    handlers.put(Names.FCALL, new CompileCall());
    handlers.put(Names.ESCAPE, new CompileEscape());
    handlers.put(Names.CATCH, new CompileCatchAction());
    handlers.put(Names.IF, new CompileConditional());
    handlers.put(Names.IS, new CompileIsDecl());
    handlers.put(Names.LABELED, new CompileLabeled());
    handlers.put(Names.LEAVE, new CompileLeave());
    handlers.put(Names.LET, new CompileLet());
    handlers.put(Names.LOOP, new CompileLoop());
    handlers.put(Names.THROW, new CompileThrow());
    handlers.put(Names.VALIS, new CompileValis());
    handlers.put(Names.VAR, new CompileVarDecl());
    handlers.put(Names.WHILE, new CompileWhile());
    handlers.put(Names.YIELD, new CompileYield());
  }

  private Actions()
  {
  }

  public static void compileAction(IAbstract term, ErrorReport errors, Exit exit, CafeDictionary dict,
      CafeDictionary outer, LabelNode endLabel, String inFunction, IContinuation cont, CodeContext ccxt)
  {
    if (term instanceof Apply) {
      Apply app = (Apply) term;
      ICompileAction handler = handlers.get(app.getOp());
      if (handler != null)
        handler.handleAction(term, errors, exit, dict, outer, endLabel, inFunction, cont, ccxt);
      else
        errors.reportError("cannot find compiler for action:" + term, term.getLoc());
    } else if (Abstract.isName(term, Names.NOTHING))
      cont.cont(SrcSpec.prcSrc, dict, term.getLoc(), errors, ccxt);
    else
      errors.reportError("invalid form of action", term.getLoc());
  }

  // A call to a escape looks like:
  // (op args...);

  private static class CompileEscape implements ICompileAction
  {
    @Override
    public void handleAction(IAbstract call, ErrorReport errors, Exit exit, CafeDictionary dict, CafeDictionary outer,
        LabelNode endLabel, String inFunction, IContinuation cont, CodeContext ccxt)
    {
      MethodNode mtd = ccxt.getMtd();
      HWM hwm = ccxt.getMtdHwm();
      CodeCatalog bldCat = ccxt.getBldCat();
      CodeRepository repository = ccxt.getRepository();

      InsnList ins = mtd.instructions;
      String funName = CafeSyntax.escapeOp(call);
      Location loc = call.getLoc();
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
            String javaName = Expressions.escapeReference(var.getName(), dict, var.getJavaType(), var.getJavaSig(),
                errors);

            ins.add(new FieldInsnNode(Opcodes.GETSTATIC, Expressions.escapeOwner(var.getName(), dict), javaName, var
                .getJavaSig()));
          }
          if (var.getJavaInvokeSig().equals(IFuncImplementation.IFUNCTION_INVOKE_SIG)) {
            argSpecs = SrcSpec.generics(varType, dict, bldCat, repository, errors, loc);

            Expressions.argArray(args, argSpecs, errors, dict, outer, inFunction, exit, ccxt);
          } else {
            argSpecs = SrcSpec.typeSpecs(var.getJavaInvokeSig(), dict, bldCat, errors, loc);

            Expressions.compileArgs(args, argSpecs, errors, dict, outer, inFunction, exit, ccxt);
          }

          // actually invoke the escape
          if (builtin instanceof Inliner) {
            ((Inliner) builtin).inline(dict.getOwner(), mtd, hwm, loc);
            hwm.reset(mark);
            cont.cont(SrcSpec.voidSrc, dict, loc, errors, ccxt);
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

          cont.cont(resltSpec, dict, loc, errors, ccxt);
        }
      } else
        errors.reportError("tried to invoke non-function: " + funName + ":" + varType, loc);
    }
  }

  private static class CompileCall implements ICompileAction
  {
    @Override
    public void handleAction(IAbstract app, ErrorReport errors, Exit exit, CafeDictionary dict, CafeDictionary outer,
        LabelNode endLabel, String inFunction, IContinuation cont, CodeContext ccxt)
    {
      Location loc = app.getLoc();

      assert CafeSyntax.isFunCall(app);

      MethodNode mtd = ccxt.getMtd();
      String funName = CafeSyntax.funCallName(app);
      VarInfo var = Theta.varReference(funName, dict, outer, loc, errors);
      Actions.doLineNumber(loc, mtd);
      InsnList ins = mtd.instructions;
      CallCont callCont = new CallCont(ins, cont);

      if (var != null) {
        switch (var.getKind()) {
        case builtin:
          Expressions.invokeEscape(loc, var, app, errors, dict, outer, inFunction, callCont, exit, ccxt);
          return;
        case general:
          Expressions.compileFunCall(loc, var, CafeSyntax.funCallArgs(app), errors, dict, outer, inFunction, callCont,
              exit, ccxt);
          return;
        default:
          errors.reportError(var.getName() + " is not a function", loc);
        }
      } else
        errors.reportError(funName + " not declared", loc);

      cont.cont(SrcSpec.prcSrc, dict, loc, errors, ccxt);
    }
  }

  // An assignment looks like:
  // (assign <lval> <expression>)
  private static class CompileAssignment implements ICompileAction
  {
    @Override
    public void handleAction(IAbstract term, ErrorReport errors, Exit exit, CafeDictionary dict, CafeDictionary outer,
        LabelNode endLabel, String inFunction, IContinuation cont, CodeContext ccxt)
    {
      assert CafeSyntax.isAssignment(term);

      IAbstract lval = CafeSyntax.assignmentLval(term);
      IAbstract exp = CafeSyntax.assignmentRval(term);
      MethodNode mtd = ccxt.getMtd();
      HWM hwm = ccxt.getMtdHwm();
      InsnList ins = mtd.instructions;
      Location loc = lval.getLoc();

      doLineNumber(loc, mtd);
      int mark = hwm.bump(0);

      if (lval instanceof Name) {
        VarInfo var = dict.find(((Name) lval).getId());
        if (var == null)
          errors.reportError("variable " + lval + " not declared", loc);
        else if (var.getAccess() == AccessMode.readOnly)
          errors.reportError("not permitted to assign to " + lval, loc);
        else
          Expressions.compileExp(exp, errors, dict, outer, inFunction, new StoreCont(var, dict), exit, ccxt);
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
            Expressions.compileExp(rec, errors, dict, outer, inFunction, new CheckCont(nxLbl, var, dict), exit, ccxt);
            Utils.jumpTarget(ins, nxLbl);

            if (TypeUtils.isTypeVar(var.getType())) {
              ins.add(new TypeInsnNode(Opcodes.CHECKCAST, Types.IRECORD));
              hwm.probe(1);
              ins.add(new LdcInsnNode(Utils.javaIdentifierOf(field)));
              LabelNode nxLbl2 = new LabelNode();
              Expressions.compileExp(exp, errors, dict, outer, inFunction, new CheckCont(nxLbl2, SrcSpec.generalSrc,
                  dict), exit, ccxt);
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
              Expressions.compileExp(exp, errors, dict, outer, inFunction, new CheckCont(nxLbl2, fieldSpec, dict),
                  exit, ccxt);
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

        Expressions.compileExp(exp, errors, dict, outer, inFunction, new AssignmentCont(lval, dict, outer,
            AccessMode.readWrite, exLabel, succ, fail), exit, ccxt);
        Utils.jumpTarget(mtd.instructions, exLabel);
        cont.cont(SrcSpec.prcSrc, dict, loc, errors, ccxt);

        hwm.reset(mark);
        cont.cont(SrcSpec.prcSrc, dict, loc, errors, ccxt);
      }
    }
  }

  // A block looks like
  // {A1,...,An}
  private static class CompileBlock implements ICompileAction
  {
    @Override
    public void handleAction(IAbstract term, ErrorReport errors, Exit exit, CafeDictionary dict, CafeDictionary outer,
        LabelNode endLabel, String inFunction, IContinuation cont, CodeContext ccxt)
    {
      assert CafeSyntax.isBlock(term);
      MethodNode mtd = ccxt.getMtd();
      LabelNode endLbl = new LabelNode();
      CafeDictionary inner = dict.fork();

      IList actions = CafeSyntax.blockContents(term);

      for (int ix = 0; ix < actions.size(); ix++) {
        IAbstract act = (IAbstract) actions.getCell(ix);
        if (ix < actions.size() - 1) {
          LabelNode exLabel = new LabelNode();

          compileAction(act, errors, exit, inner, outer, endLbl, inFunction, new JumpCont(exLabel), ccxt);

          Utils.jumpTarget(mtd.instructions, exLabel);
          dict.migrateFreeVars(inner);
        } else
          compileAction(act, errors, exit, inner, outer, endLabel, inFunction, cont, ccxt);
      }
      dict.migrateFreeVars(inner);
      // inner.dictUndo();
      mtd.instructions.add(endLbl);
    }
  }

  // A case action looks like:
  // switch <sel> in {<cases>} else <deflt>
  private static class CompileSwitchAction implements ICompileAction
  {
    @Override
    public void handleAction(IAbstract term, final ErrorReport errors, final Exit exit, final CafeDictionary dict,
        final CafeDictionary outer, final LabelNode endLabel, final String inFunction, final IContinuation cont,
        final CodeContext ccxt)
    {
      assert CafeSyntax.isSwitch(term);

      IAbstract sel = CafeSyntax.switchSel(term);
      IAbstract deflt = CafeSyntax.switchDeflt(term);

      MethodNode mtd = ccxt.getMtd();
      doLineNumber(sel.getLoc(), mtd);

      assert sel instanceof Name;
      VarInfo var = Theta.varReference(((Name) sel).getId(), dict, outer, sel.getLoc(), errors);

      ICaseCompile handler = new ICaseCompile() {
        @Override
        public ISpec compile(IAbstract term, CafeDictionary dict, IContinuation cont)
        {
          compileAction(term, errors, exit, dict, outer, endLabel, inFunction, cont, ccxt);
          return SrcSpec.prcSrc;
        }
      };
      CaseCompile.compileSwitch(term.getLoc(), var, CafeSyntax.switchCases(term), deflt, dict, outer, errors, handler,
          cont, ccxt);
    }
  }

  // A simple catch action looks like:
  // <action> catch <handler>
  private static class CompileCatchAction implements ICompileAction
  {
    @Override
    public void handleAction(IAbstract action, ErrorReport errors, Exit exit, CafeDictionary dict,
        CafeDictionary outer, LabelNode endLabel, String inFunction, IContinuation cont, CodeContext ccxt)
    {
      assert CafeSyntax.isCatchAction(action);

      Location loc = action.getLoc();

      MethodNode mtd = ccxt.getMtd();
      CodeCatalog bldCat = ccxt.getBldCat();

      InsnList ins = mtd.instructions;

      doLineNumber(loc, mtd);

      CafeDictionary forked = dict.fork();

      IAbstract body = CafeSyntax.catchBody(action);
      IAbstract handler = CafeSyntax.catchHandler(action);

      LabelNode start = new LabelNode();
      LabelNode except = new LabelNode();
      LabelNode exceptExit = new LabelNode();

      ins.add(start);

      compileAction(body, errors, exit, forked, outer, except, inFunction, new JumpCont(exceptExit), ccxt);

      ins.add(except);

      // Start code for checking exception itself
      ins.add(new TypeInsnNode(Opcodes.CHECKCAST, Types.EVALUATION_EXCEPTION));

      ISpec desc = SrcSpec.typeSpec(loc, StandardTypes.exceptionType, dict, bldCat, errors);

      DeclareLocal declare = new DeclareLocal(loc, Names.EXCEPTION_VAR, desc, AccessMode.readOnly, forked, endLabel);

      declare.cont(desc, outer, loc, errors, ccxt);

      compileAction(handler, errors, exit, forked, outer, endLabel, inFunction, new JumpCont(exceptExit), ccxt);

      dict.migrateFreeVars(forked);

      ins.add(exceptExit);
      cont.cont(SrcSpec.prcSrc, dict, loc, errors, ccxt);

      mtd.tryCatchBlocks.add(new TryCatchBlockNode(start, except, except, Types.EVALUATION_EXCEPTION));
    }
  }

  // A conditional looks like:
  // if <test> then <then> else <else>
  private static class CompileConditional implements ICompileAction
  {

    @Override
    public void handleAction(IAbstract action, ErrorReport errors, Exit exit, CafeDictionary dict,
        CafeDictionary outer, LabelNode endLabel, String inFunction, IContinuation cont, CodeContext ccxt)
    {
      assert CafeSyntax.isConditional(action);
      IAbstract condition = CafeSyntax.conditionalTest(action);
      IAbstract th = CafeSyntax.conditionalThen(action);
      IAbstract el = CafeSyntax.conditionalElse(action);
      LabelNode thLabel = new LabelNode();
      LabelNode elLabel = new LabelNode();
      MethodNode mtd = ccxt.getMtd();
      InsnList ins = mtd.instructions;

      doLineNumber(condition.getLoc(), mtd);

      CafeDictionary thDict = dict.fork();
      Conditions.compileCond(condition, Sense.jmpOnFail, elLabel, errors, thDict, outer, inFunction, exit, ccxt);
      Utils.jumpTarget(ins, thLabel);
      compileAction(th, errors, exit, thDict, outer, endLabel, inFunction, cont, ccxt);
      dict.migrateFreeVars(thDict);
      ins.add(elLabel);
      CafeDictionary elDict = dict.fork();
      compileAction(el, errors, exit, elDict, outer, endLabel, inFunction, cont, ccxt);
      dict.migrateFreeVars(elDict);
    }
  }

  // A labeled action looks like:
  // <label> :: <action>
  private static class CompileLabeled implements ICompileAction
  {

    @Override
    public void handleAction(IAbstract action, ErrorReport errors, Exit exit, CafeDictionary dict,
        CafeDictionary outer, LabelNode endLabel, String inFunction, IContinuation cont, CodeContext ccxt)
    {
      assert CafeSyntax.isLabeled(action);
      MethodNode mtd = ccxt.getMtd();
      Location loc = action.getLoc();
      CafeDictionary forked = dict.fork();
      LabelNode exitLbl = new LabelNode();
      String lbl = CafeSyntax.labeledLabel(action);
      IAbstract labeled = CafeSyntax.labeledAction(action);

      compileAction(labeled, errors, new Exit(lbl, new JumpCont(exitLbl), exit), forked, outer, exitLbl, inFunction,
          cont, ccxt);
      dict.migrateFreeVars(forked);
      mtd.instructions.add(exitLbl);
      cont.cont(SrcSpec.prcSrc, dict, loc, errors, ccxt);
    }
  }

  // A leave action looks like:
  // (leave lbl)
  private static class CompileLeave implements ICompileAction
  {

    @Override
    public void handleAction(IAbstract leave, ErrorReport errors, Exit exit, CafeDictionary dict, CafeDictionary outer,
        LabelNode endLabel, String inFunction, IContinuation cont, CodeContext ccxt)
    {
      Location loc = leave.getLoc();
      assert CafeSyntax.isLeave(leave);
      Exit tgt = exit.find(CafeSyntax.leaveLabel(leave));
      if (tgt == null)
        errors.reportError(leave + " not in scope", loc);
      else {
        tgt.getCont().cont(SrcSpec.prcSrc, dict, loc, errors, ccxt);
        if (!tgt.getCont().isJump())
          cont.cont(SrcSpec.prcSrc, dict, loc, errors, ccxt);
      }
    }
  }

  // A loop action looks like:
  // (loop Action)
  private static class CompileLoop implements ICompileAction
  {

    @Override
    public void handleAction(IAbstract action, ErrorReport errors, Exit exit, CafeDictionary dict,
        CafeDictionary outer, LabelNode endLabel, String inFunction, IContinuation cont, CodeContext ccxt)
    {
      assert CafeSyntax.isLoop(action);
      MethodNode mtd = ccxt.getMtd();
      InsnList ins = mtd.instructions;
      LabelNode lbl = new LabelNode();
      ins.add(lbl);

      doLineNumber(action.getLoc(), mtd);

      compileAction(CafeSyntax.loopAction(action), errors, exit, dict, outer, endLabel, inFunction, new JumpCont(lbl),
          ccxt);
    }
  }

  // A let action looks like:
  // (let (block defs) action)
  private static class CompileLet implements ICompileAction
  {

    @Override
    public void handleAction(final IAbstract action, ErrorReport errors, final Exit exit, CafeDictionary dict,
        final CafeDictionary outer, LabelNode endLabel, final String inFunction, final IContinuation cont,
        final CodeContext ccxt)
    {
      if (CafeSyntax.isLetExp(action)) {
        final LabelNode endDefLabel = new LabelNode();

        IList theta = CafeSyntax.letDefs(action);

        Theta.compileDefinitions(theta, dict, outer, endDefLabel, inFunction, new LocalDefiner(endLabel, ccxt),
            new IThetaBody() {

              @Override
              public ISpec compile(CafeDictionary thetaDict, CodeCatalog bldCat, ErrorReport errors,
                  CodeRepository repository)
              {
                compileAction(CafeSyntax.letBound(action), errors, exit, thetaDict, outer, endDefLabel, inFunction,
                    cont, ccxt);
                return null;
              }

              @Override
              public void introduceType(CafeTypeDescription type)
              {
              }
            }, action.getLoc(), errors, ccxt);

        ccxt.getMtd().instructions.add(endDefLabel);
      } else
        errors.reportError("expecting a let action", action.getLoc());
    }
  }

  private static class CompileAssert implements ICompileAction
  {

    @Override
    public void handleAction(IAbstract action, ErrorReport errors, Exit exit, CafeDictionary dict,
        CafeDictionary outer, LabelNode endLabel, String inFunction, IContinuation cont, CodeContext ccxt)
    {
      MethodNode mtd = ccxt.getMtd();
      HWM hwm = ccxt.getMtdHwm();
      InsnList ins = mtd.instructions;

      String exceptionType = Type.getInternalName(AssertionError.class);

      Location loc = action.getLoc();
      String msg = "assert failed at " + loc.toString();

      LabelNode nxLabel = new LabelNode();

      doLineNumber(loc, mtd);

      CafeDictionary asDict = dict.fork();

      Conditions.compileCond(CafeSyntax.assertedCond(action), Sense.jmpOnOk, nxLabel, errors, asDict, outer,
          inFunction, exit, ccxt);

      hwm.probe(3);
      ins.add(new TypeInsnNode(Opcodes.NEW, exceptionType));
      ins.add(new InsnNode(Opcodes.DUP));
      ins.add(new LdcInsnNode(msg));
      ins.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, exceptionType, Types.INIT, "(" + Types.OBJECT_SIG + ")V"));
      ins.add(new InsnNode(Opcodes.ATHROW));

      Utils.jumpTarget(ins, nxLabel);
      cont.cont(SrcSpec.voidSrc, dict, loc, errors, ccxt);
      dict.migrateFreeVars(asDict);
    }
  }

  private static class CompileIgnore implements ICompileAction
  {
    @Override
    public void handleAction(IAbstract action, ErrorReport errors, Exit exit, CafeDictionary dict,
        CafeDictionary outer, LabelNode endLabel, String inFunction, IContinuation cont, CodeContext ccxt)
    {
      MethodNode mtd = ccxt.getMtd();
      InsnList ins = mtd.instructions;

      Location loc = action.getLoc();

      doLineNumber(loc, mtd);

      Expressions.compileExp(CafeSyntax.ignoredExp(action), errors, dict, outer, inFunction, new CallCont(ins, cont),
          exit, ccxt);
    }
  }

  private static class CompileThrow implements ICompileAction
  {

    @Override
    public void handleAction(IAbstract term, ErrorReport errors, Exit exit, CafeDictionary dict, CafeDictionary outer,
        LabelNode endLabel, String inFunction, IContinuation cont, CodeContext ccxt)
    {
      assert CafeSyntax.isThrow(term);

      String exceptionType = Type.getInternalName(EvaluationException.class);
      MethodNode mtd = ccxt.getMtd();
      HWM hwm = ccxt.getMtdHwm();
      InsnList ins = mtd.instructions;

      doLineNumber(term.getLoc(), mtd);

      int mark = hwm.bump(0);

      Expressions.compileExp(CafeSyntax.thrownExp(term), errors, dict, outer, inFunction, new NullCont(), exit, ccxt);

      ins.add(new TypeInsnNode(Opcodes.CHECKCAST, exceptionType));
      ins.add(new InsnNode(Opcodes.ATHROW));
      hwm.reset(mark);
    }
  }

  private static class CompileValis implements ICompileAction
  {

    @Override
    public void handleAction(IAbstract action, ErrorReport errors, Exit exit, CafeDictionary dict,
        CafeDictionary outer, LabelNode endLabel, String inFunction, IContinuation cont, CodeContext ccxt)
    {
      assert CafeSyntax.isValis(action);
      MethodNode mtd = ccxt.getMtd();
      doLineNumber(action.getLoc(), mtd);
      Exit tgt = exit.find(Names.VALIS);

      if (tgt != null)
        Expressions.compileExp(CafeSyntax.valisExp(action), errors, dict, outer, inFunction, tgt.getCont(), exit, ccxt);
      else
        errors.reportError("valis not in a valof", action.getLoc());
    }
  }

  // A constant var declaration looks like:
  // <id>:<type> is <expression>
  private static class CompileIsDecl implements ICompileAction
  {

    @Override
    public void handleAction(IAbstract term, ErrorReport errors, Exit exit, CafeDictionary dict, CafeDictionary outer,
        LabelNode endLabel, String inFunction, IContinuation cont, CodeContext ccxt)
    {
      assert CafeSyntax.isIsDeclaration(term);
      IAbstract lVal = CafeSyntax.isDeclLval(term);
      IAbstract exp = CafeSyntax.isDeclValue(term);
      Location loc = term.getLoc();
      MethodNode mtd = ccxt.getMtd();
      HWM hwm = ccxt.getMtdHwm();
      int mark = hwm.getDepth();
      CodeRepository repository = ccxt.getRepository();

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

          Expressions.compileExp(exp, errors, dict, outer, inFunction, declare, exit, ccxt);
        }
        cont.cont(SrcSpec.prcSrc, dict, loc, errors, ccxt);
      } else if (CafeSyntax.isTypedTerm(lVal) && Abstract.isName(CafeSyntax.typedTerm(lVal))) {
        String vrName = Abstract.getId(CafeSyntax.typedTerm(lVal));

        IType varType = TypeAnalyser.parseType(CafeSyntax.typedType(lVal), dict, errors);

        doLineNumber(loc, mtd);

        if (endLabel == null)
          errors.reportError("invalid location for variable declaration", term.getLoc());
        else {
          ISpec desc = SrcSpec.generic(lValLoc, varType, dict, repository, errors);
          DeclareLocal declare = new DeclareLocal(loc, vrName, desc, AccessMode.readOnly, dict, endLabel);

          Expressions.compileExp(exp, errors, dict, outer, inFunction, declare, exit, ccxt);
        }
        cont.cont(SrcSpec.prcSrc, dict, loc, errors, ccxt);
      } else {
        LabelNode exLabel = new LabelNode();
        IContinuation succ = new JumpCont(exLabel);
        IContinuation fail = new ThrowContinuation("initialization failed");

        Expressions.compileExp(exp, errors, dict, outer, inFunction, new PatternCont(lVal, dict, outer,
            AccessMode.readOnly, mtd, exLabel, errors, succ, fail), exit, ccxt);
        Utils.jumpTarget(mtd.instructions, exLabel);
        cont.cont(SrcSpec.prcSrc, dict, loc, errors, ccxt);
      }
      hwm.reset(mark);
    }
  }

  // A reassignable var declaration looks like:
  // var <id>:<type> := <expression>
  private static class CompileVarDecl implements ICompileAction
  {

    @Override
    public void handleAction(IAbstract term, ErrorReport errors, Exit exit, CafeDictionary dict, CafeDictionary outer,
        LabelNode endLabel, String inFunction, IContinuation cont, CodeContext ccxt)
    {
      assert CafeSyntax.isVarDeclaration(term);
      IAbstract lVal = CafeSyntax.varDeclLval(term);
      IAbstract exp = CafeSyntax.varDeclValue(term);
      MethodNode mtd = ccxt.getMtd();
      HWM hwm = ccxt.getMtdHwm();
      int mark = hwm.getDepth();
      CodeRepository repository = ccxt.getRepository();

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

          Expressions.compileExp(exp, errors, dict, outer, inFunction, declare, exit, ccxt);
        }
        cont.cont(desc, dict, loc, errors, ccxt);
      } else {
        LabelNode exLabel = new LabelNode();
        IContinuation succ = new JumpCont(exLabel);
        IContinuation fail = new ThrowContinuation("initialization failed");

        Expressions.compileExp(exp, errors, dict, outer, inFunction, new PatternCont(lVal, dict, outer,
            AccessMode.readWrite, mtd, exLabel, errors, succ, fail), exit, ccxt);
        Utils.jumpTarget(mtd.instructions, exLabel);
        cont.cont(SrcSpec.prcSrc, dict, loc, errors, ccxt);
      }
      hwm.reset(mark);
    }
  }

  // A While loop looks like:
  // while(<cond>,<body>)
  private static class CompileWhile implements ICompileAction
  {
    @Override
    public void handleAction(IAbstract action, ErrorReport errors, Exit exit, CafeDictionary dict,
        CafeDictionary outer, LabelNode endLabel, String inFunction, IContinuation cont, CodeContext ccxt)
    {
      Location loc = action.getLoc();
      assert CafeSyntax.isWhile(action);
      MethodNode mtd = ccxt.getMtd();
      InsnList ins = mtd.instructions;
      LabelNode loopLbl = new LabelNode();
      LabelNode endLbl = new LabelNode();

      CafeDictionary forked = dict.fork();
      ins.add(loopLbl);

      doLineNumber(loc, mtd);

      if (!CompilerUtils.isTrivial(CafeSyntax.whileTest(action)))
        // We do it this way because we want to allow the condition to bind variables. Slightly
        // slower in theory but probably makes no difference these days.
        Conditions.compileCond(CafeSyntax.whileTest(action), Sense.jmpOnFail, endLbl, errors, forked, outer,
            inFunction, exit, ccxt);

      compileAction(CafeSyntax.whileBody(action), errors, exit, forked, outer, endLbl, inFunction,
          new JumpCont(loopLbl), ccxt);

      dict.migrateFreeVars(forked);
      ins.add(endLbl);

      cont.cont(SrcSpec.prcSrc, dict, loc, errors, ccxt);
    }
  }

  private static class CompileYield implements ICompileAction
  {
    private static final String THREAD = Utils.javaInternalClassName(Thread.class);
    private static final String YIELD = "yield";
    private static String YIELD_SIG;

    static {
      Method yieldMethod;
      try {
        yieldMethod = Thread.class.getDeclaredMethod(YIELD);
        YIELD_SIG = org.objectweb.asm.Type.getMethodDescriptor(yieldMethod);
      } catch (NoSuchMethodException | SecurityException e) {
      }
    }

    @Override
    public void handleAction(IAbstract term, ErrorReport errors, Exit exit, CafeDictionary dict, CafeDictionary outer,
        LabelNode endLabel, String inFunction, IContinuation cont, CodeContext ccxt)
    {
      IAbstract yielded = CafeSyntax.yieldedAction(term);

      MethodNode mtd = ccxt.getMtd();
      InsnList ins = mtd.instructions;
      ins.add(new MethodInsnNode(Opcodes.INVOKESTATIC, THREAD, YIELD, YIELD_SIG));

      compileAction(yielded, errors, exit, dict, outer, endLabel, inFunction, cont, ccxt);
    }
  }

  // A synchronized action looks like:
  // sync(<obj>){<body>}
  private static class CompileSyncAction implements ICompileAction
  {

    @Override
    public void handleAction(IAbstract action, ErrorReport errors, Exit exit, CafeDictionary dict,
        CafeDictionary outer, LabelNode endLabel, String inFunction, final IContinuation cont, CodeContext ccxt)
    {
      Location loc = action.getLoc();
      assert CafeSyntax.isSync(action);
      MethodNode mtd = ccxt.getMtd();
      HWM hwm = ccxt.getMtdHwm();
      InsnList ins = mtd.instructions;

      doLineNumber(loc, mtd);

      IAbstract sel = CafeSyntax.syncObject(action);
      IAbstract body = CafeSyntax.syncAction(action);

      LabelNode nxLbl = new LabelNode();
      Expressions.compileExp(sel, errors, dict, outer, inFunction, new JumpCont(nxLbl), exit, ccxt);
      Utils.jumpTarget(ins, nxLbl);

      ins.add(new InsnNode(Opcodes.DUP));
      hwm.probe(1);
      LabelNode syncStart = new LabelNode();
      LabelNode syncEnd = new LabelNode();
      LabelNode syncExcept = new LabelNode();
      LabelNode syncExit = new LabelNode();

      final VarInfo syncVar = dict.declareLocal(loc, GenSym.genSym("__sync"), true, StandardTypes.anyType,
          Types.OBJECT, Types.OBJECT_SIG, null, null, AccessMode.readOnly);
      mtd.localVariables.add(new LocalVariableNode(syncVar.getJavaSafeName(), syncVar.getJavaSig(), null, syncStart,
          syncExit, syncVar.getOffset()));
      ins.add(new VarInsnNode(Opcodes.ASTORE, syncVar.getOffset()));

      ins.add(new InsnNode(Opcodes.MONITORENTER));
      ins.add(syncStart);

      MonitorExitCont monitorExit = new MonitorExitCont(syncVar);
      Exit exitSync = adjustTheExit(exit, monitorExit);

      compileAction(body, errors, exitSync, dict, outer, endLabel, inFunction, new ComboCont(monitorExit, new JumpCont(
          syncEnd)), ccxt);
      Utils.jumpTarget(ins, syncEnd);
      hwm.probe(1);
      // monitorExit.cont(mtd, hwm, null, bldCat, dict, loc, errors);
      cont.cont(SrcSpec.prcSrc, dict, loc, errors, ccxt);
      if (!cont.isJump())
        ins.add(new JumpInsnNode(Opcodes.GOTO, syncExit));
      ins.add(syncExcept);
      monitorExit.cont(null, dict, loc, errors, ccxt);
      ins.add(new InsnNode(Opcodes.ATHROW));

      mtd.tryCatchBlocks.add(new TryCatchBlockNode(syncStart, syncEnd, syncExcept, null));

      ins.add(syncExit);
    }

    private class MonitorExitCont implements IContinuation
    {
      private final VarInfo syncVar;

      MonitorExitCont(VarInfo syncVar)
      {
        this.syncVar = syncVar;
      }

      @Override
      public ISpec cont(ISpec src, CafeDictionary cxt, Location loc, ErrorReport errors, CodeContext ccxt)
      {
        MethodNode mtd = ccxt.getMtd();
        InsnList ins = mtd.instructions;

        ins.add(new VarInsnNode(Opcodes.ALOAD, syncVar.getOffset()));
        ins.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, Types.OBJECT, "notify", "()V"));

        ins.add(new VarInsnNode(Opcodes.ALOAD, syncVar.getOffset()));
        ins.add(new InsnNode(Opcodes.MONITOREXIT));
        return src;
      }

      @Override
      public boolean isJump()
      {
        return false;
      }

    }

    private Exit adjustTheExit(Exit exit, MonitorExitCont monitorExit)
    {
      if (exit == null)
        return null;
      else {
        Exit parent = adjustTheExit(exit.getParent(), monitorExit);
        return new Exit(exit.getLabel(), new ComboCont(monitorExit, exit.getCont()), parent);
      }
    }
  }

  public static void doLineNumber(Location loc, MethodNode mtd)
  {
    if (!loc.equals(Location.nullLoc)) {
      LabelNode lnLbl = new LabelNode();
      mtd.instructions.add(lnLbl);
      mtd.instructions.add(new LineNumberNode(loc.getLineCnt(), lnLbl));
    }
  }
}
