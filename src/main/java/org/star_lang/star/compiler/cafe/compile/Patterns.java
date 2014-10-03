package org.star_lang.star.compiler.cafe.compile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.star_lang.star.code.repository.CodeCatalog;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.ast.BigDecimalLiteral;
import org.star_lang.star.compiler.ast.CharLiteral;
import org.star_lang.star.compiler.ast.FloatLiteral;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.ast.IntegerLiteral;
import org.star_lang.star.compiler.ast.LongLiteral;
import org.star_lang.star.compiler.ast.Name;
import org.star_lang.star.compiler.ast.StringLiteral;
import org.star_lang.star.compiler.cafe.CafeSyntax;
import org.star_lang.star.compiler.cafe.Names;
import org.star_lang.star.compiler.cafe.compile.cont.IContinuation;
import org.star_lang.star.compiler.cafe.compile.cont.JumpCont;
import org.star_lang.star.compiler.cafe.compile.cont.StoreCont;
import org.star_lang.star.compiler.cafe.type.ICafeConstructorSpecifier;
import org.star_lang.star.compiler.type.Freshen;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.compiler.util.GenSym;
import org.star_lang.star.compiler.util.StringUtils;
import org.star_lang.star.data.IList;
import org.star_lang.star.data.type.ConstructorSpecifier;
import org.star_lang.star.data.type.IAlgebraicType;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.ITypeDescription;
import org.star_lang.star.data.type.IValueSpecifier;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.type.TypeDescription;
import org.star_lang.star.operators.ICafeBuiltin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.string.RegexpOps;
import org.star_lang.star.operators.string.runtime.Regexp;

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

public class Patterns
{
  /**
   * On entry to a generated pattern code the top of the stack contains a value that is to be
   * compared against a pattern.
   * 
   * On successful completion of the pattern the matched element is removed from the stack; and the
   * succ continuation is invoked. Otherwise the fail continuation will be invoked.
   * 
   * @param ptn
   *          the pattern to test the input against
   * @param access
   *          the read/write mode to declare new variables in the pattern
   * @param termSpec
   *          what type is the pattern matching against?
   * @param dict
   *          the current dictionary
   * @param outer
   *          the dictionary for the outer scope
   * @param endLabel
   *          a label identifying when new variables will lose their scope
   * @param errors
   *          error reporter
   * @param succ
   *          This code is run at the point the pattern succeeds
   * @param fail
   *          This code is run at the point the pattern has failed
   * @param ccxt
   *          compilation context
   */
  public static void compilePttrn(IAbstract ptn, AccessMode access, ISpec termSpec, CafeDictionary dict,
      CafeDictionary outer, LabelNode endLabel, ErrorReport errors, IContinuation succ, IContinuation fail,
      CodeContext ccxt)
  {
    compilePtn(ptn, access, termSpec, dict, outer, endLabel, errors, new NamePtn(), succ, fail, ccxt);
  }

  public static void compilePtn(IAbstract ptn, AccessMode access, ISpec termSpec, CafeDictionary dict,
      CafeDictionary outer, LabelNode endLabel, ErrorReport errors, VarPattern varHandler, IContinuation succ,
      IContinuation fail, CodeContext ccxt)
  {
    if (CafeSyntax.isNullPtn(ptn))
      handleNullPtn(termSpec, errors, dict, fail, ptn.getLoc(), ccxt);
    else if (ptn instanceof Name)
      varHandler.varPttrn(termSpec, ptn.getLoc(), Abstract.getId(ptn), errors, dict, succ, fail, ccxt);
    else if (ptn instanceof IntegerLiteral)
      handleInteger(termSpec, (IntegerLiteral) ptn, errors, dict, succ, fail, ccxt);
    else if (ptn instanceof LongLiteral)
      handleLong(termSpec, (LongLiteral) ptn, errors, dict, succ, fail, ccxt);
    else if (ptn instanceof FloatLiteral)
      handleFloat(termSpec, (FloatLiteral) ptn, errors, dict, succ, fail, ccxt);
    else if (ptn instanceof BigDecimalLiteral)
      handleBignum(termSpec, (BigDecimalLiteral) ptn, errors, dict, succ, fail, ccxt);
    else if (ptn instanceof CharLiteral)
      handleCharLiteral(termSpec, (CharLiteral) ptn, errors, dict, succ, fail, ccxt);
    else if (ptn instanceof StringLiteral)
      handleStringLiteral(termSpec, (StringLiteral) ptn, errors, dict, succ, fail, ccxt);
    else if (CafeSyntax.isTuple(ptn))
      tuplePtn(ptn, access, dict, outer, endLabel, errors, varHandler, succ, fail, ccxt);
    else if (CafeSyntax.isConstructor(ptn))
      constructorPtn(termSpec, ptn, access, dict, outer, endLabel, errors, varHandler, succ, fail, ccxt);
    else if (CafeSyntax.isTypedTerm(ptn))
      defineNewVar(termSpec, ptn, errors, access, dict, endLabel, succ, ccxt);
    else if (CafeSyntax.isPtnCall(ptn))
      patternCall(ptn, access, errors, dict, outer, varHandler, succ, fail, endLabel, ccxt);
    else if (CafeSyntax.isRegexp(ptn))
      handleRegexp(termSpec, ptn, errors, endLabel, access, dict, outer, varHandler, succ, fail, ccxt);
    else if (CafeSyntax.isFace(ptn))
      handleFacePtn(ptn, access, dict, outer, endLabel, errors, varHandler, succ, fail, ccxt);
    else
      errors.reportError("invalid pattern: " + ptn, ptn.getLoc());
  }

  private static void handleNullPtn(ISpec termSpec, ErrorReport errors, CafeDictionary dict, IContinuation fail,
      Location loc, CodeContext ccxt)
  {
    fail.cont(termSpec, dict, loc, errors, ccxt);
  }

  private static void patternCall(IAbstract ptn, AccessMode access, ErrorReport errors, CafeDictionary dict,
      CafeDictionary outer, VarPattern varHandler, IContinuation succ, IContinuation fail, LabelNode endLabel,
      CodeContext ccxt)
  {
    assert CafeSyntax.isPtnCall(ptn);
    IAbstract callPtn = CafeSyntax.callPtnPtn(ptn);
    MethodNode mtd = ccxt.getMtd();
    HWM hwm = ccxt.getMtdHwm();
    CodeCatalog bldCat = ccxt.getBldCat();

    if (!(callPtn instanceof Name))
      errors.reportError("require pattern name here, not " + callPtn, callPtn.getLoc());
    else {
      Location loc = ptn.getLoc();
      String pttrnName = Abstract.getId(callPtn);
      VarInfo var = Theta.varReference(pttrnName, dict, outer, loc, errors);
      InsnList ins = mtd.instructions;

      if (var != null) {
        String ptnName = var.getName();
        IType ptnType = var.getType();

        if (TypeUtils.isPatternType(ptnType)) {
          // Set up the preamble to access the pattern code
          // Note that at this point, the TOS contains the value to match
          // against
          int mark = hwm.bump(1);

          switch (var.getKind()) {
          case builtin: {
            ICafeBuiltin builtin = Intrinsics.getBuiltin(ptnName);

            String funSig = var.getJavaInvokeSig();
            String classSig = var.getJavaType();
            String invokeName = var.getJavaInvokeName();

            if (builtin instanceof Inliner) {
              ((Inliner) builtin).preamble(mtd, hwm);
              ((Inliner) builtin).inline(dict.getOwner(), mtd, hwm, loc);
            } else if (var.isStatic())
              ins.add(new MethodInsnNode(Opcodes.INVOKESTATIC, classSig, invokeName, funSig));
            else {
              hwm.bump(1);
              String javaName = Expressions.escapeReference(var.getName(), dict, classSig, var.getJavaSig(), errors);

              ins.add(new FieldInsnNode(Opcodes.GETSTATIC, Expressions.escapeOwner(var.getName(), dict), javaName, var
                  .getJavaSig()));
              // swap the value to be matched with the matcher code
              ins.add(new InsnNode(Opcodes.SWAP));

              ins.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, classSig, invokeName, funSig));
            }

            break;
          }
          case general: {
            switch (var.getWhere()) {
            case freeVar:
              ins.add(new VarInsnNode(Opcodes.ALOAD, dict.find(Names.PRIVATE_THIS).getOffset()));
              ins.add(new FieldInsnNode(Opcodes.GETFIELD, dict.getOwnerName(), var.getJavaSafeName(), var.getJavaSig()));
              break;
            case localVar:
              ins.add(new VarInsnNode(Opcodes.ALOAD, var.getOffset()));
              break;
            case staticField:
              ins.add(new FieldInsnNode(Opcodes.GETSTATIC, var.getJavaOwner(), var.getJavaSafeName(), var.getJavaSig()));
              break;
            default:
              errors.reportError("(internal) unexpected location for " + var, loc);
            }

            ISpec funSpec = SrcSpec.generic(loc, var.getType(), dict, ccxt.getRepository(), errors);
            String methodType = funSpec.getJavaType();

            if (!methodType.equals(var.getJavaType()))
              ins.add(new TypeInsnNode(Opcodes.CHECKCAST, methodType));

            // swap the value to be matched with the matcher code
            ins.add(new InsnNode(Opcodes.SWAP));

            if (methodType.startsWith("pattern"))
              ins.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, methodType, Names.MATCH, var.getJavaInvokeSig()));
            else
              ins.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, methodType, Names.MATCH, var.getJavaInvokeSig()));

            break;
          }
          default:
            errors.reportError("invalid kind for : " + ptnName + ":" + var.getType(), loc);
            return;
          }

          hwm.bump(1);
          ins.add(new InsnNode(Opcodes.DUP));

          LabelNode okLbl = new LabelNode();
          ins.add(new JumpInsnNode(Opcodes.IFNONNULL, okLbl));
          ins.add(new InsnNode(Opcodes.POP));
          fail.cont(SrcSpec.prcSrc, dict, loc, errors, ccxt);
          Utils.jumpTarget(ins, okLbl);

          ISpec resSpec = SrcSpec.typeSpec(loc, TypeUtils.getPtnResultType(ptnType), dict, bldCat, errors);
          compilePtn(CafeSyntax.ptnCallResult(ptn), access, resSpec, dict, outer, endLabel, errors, varHandler, succ,
              fail, ccxt);

          hwm.reset(mark);
        } else if (TypeUtils.isConstructorType(ptnType)) {
          LabelNode okLbl = new LabelNode();

          VarInfo src = dict.declareLocal(GenSym.genSym("__"), SrcSpec.constructorSrc, true, AccessMode.readOnly);
          LabelNode start = new LabelNode();
          LabelNode end = new LabelNode();
          ins.add(start);
          ins.add(new VarInsnNode(Opcodes.ASTORE, src.getOffset()));
          mtd.localVariables.add(new LocalVariableNode(src.getJavaSafeName(), src.getJavaSig(), null, start, end, src
              .getOffset()));

          var.loadValue(mtd, hwm, dict);
          ins.add(new TypeInsnNode(Opcodes.CHECKCAST, Types.ICONSTRUCTOR_FUNCTION));
          src.loadValue(mtd, hwm, dict);
          ins.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Types.ICONSTRUCTOR_FUNCTION, Constructors.VERIFY,
              Constructors.VERIFY_INVOKE_SIG));
          ins.add(new JumpInsnNode(Opcodes.IFNE, okLbl));
          fail.cont(var, outer, loc, errors, ccxt);
          Utils.jumpTarget(ins, okLbl);

          tuplePtnArgs(src, CafeSyntax.ptnCallResult(ptn), access, dict, outer, varHandler, endLabel, succ, fail,
              errors, ccxt);
          ins.add(end);
        } else
          errors.reportError("expecting a call pattern, not " + ptn, ptn.getLoc());
      } else
        errors.reportError("variable " + callPtn + " not declared", ptn.getLoc());
    }
  }

  @SuppressWarnings("unused")
  private static void newPatternCall(IAbstract ptn, AccessMode access, ErrorReport errors, CafeDictionary dict,
      CafeDictionary outer, VarPattern varHandler, IContinuation succ, IContinuation fail, LabelNode endLabel,
      CodeContext ccxt)
  {
    assert CafeSyntax.isPtnCall(ptn);
    IAbstract callPtn = CafeSyntax.callPtnPtn(ptn);
    MethodNode mtd = ccxt.getMtd();
    HWM hwm = ccxt.getMtdHwm();
    CodeCatalog bldCat = ccxt.getBldCat();

    if (!(callPtn instanceof Name))
      errors.reportError("require pattern name here, not " + callPtn, callPtn.getLoc());
    else {
      Location loc = ptn.getLoc();
      String pttrnName = Abstract.getId(callPtn);
      VarInfo var = Theta.varReference(pttrnName, dict, outer, loc, errors);
      InsnList ins = mtd.instructions;

      Actions.doLineNumber(loc, mtd);

      LabelNode start = new LabelNode();
      LabelNode exceptLbl = new LabelNode();
      LabelNode exceptExit = new LabelNode();

      ins.add(start);

      if (var != null) {
        String ptnName = var.getName();
        IType ptnType = var.getType();

        if (TypeUtils.isPatternType(ptnType)) {
          // Set up the preamble to access the pattern code
          // Note that at this point, the TOS contains the value to match
          // against
          int mark = hwm.bump(1);

          switch (var.getKind()) {
          case builtin: {
            ICafeBuiltin builtin = Intrinsics.getBuiltin(ptnName);

            String funSig = var.getJavaInvokeSig();
            String classSig = var.getJavaType();
            String invokeName = var.getJavaInvokeName();

            if (builtin instanceof Inliner) {
              ((Inliner) builtin).preamble(mtd, hwm);
              ((Inliner) builtin).inline(dict.getOwner(), mtd, hwm, loc);
            } else if (var.isStatic())
              ins.add(new MethodInsnNode(Opcodes.INVOKESTATIC, classSig, invokeName, funSig));
            else {
              hwm.bump(1);
              String javaName = Expressions.escapeReference(var.getName(), dict, classSig, var.getJavaSig(), errors);

              ins.add(new FieldInsnNode(Opcodes.GETSTATIC, Expressions.escapeOwner(var.getName(), dict), javaName, var
                  .getJavaSig()));
              // swap the value to be matched with the matcher code
              ins.add(new InsnNode(Opcodes.SWAP));

              ins.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, classSig, invokeName, funSig));
            }

            break;
          }
          case general: {
            switch (var.getWhere()) {
            case freeVar:
              ins.add(new VarInsnNode(Opcodes.ALOAD, dict.find(Names.PRIVATE_THIS).getOffset()));
              ins.add(new FieldInsnNode(Opcodes.GETFIELD, dict.getOwnerName(), var.getJavaSafeName(), var.getJavaSig()));
              break;
            case localVar:
              ins.add(new VarInsnNode(Opcodes.ALOAD, var.getOffset()));
              break;
            case staticField:
              ins.add(new FieldInsnNode(Opcodes.GETSTATIC, var.getJavaOwner(), var.getJavaSafeName(), var.getJavaSig()));
              break;
            default:
              errors.reportError("(internal) unexpected location for " + var, loc);
            }

            ISpec funSpec = SrcSpec.generic(loc, var.getType(), dict, ccxt.getRepository(), errors);
            String methodType = funSpec.getJavaType();

            if (!methodType.equals(var.getJavaType()))
              ins.add(new TypeInsnNode(Opcodes.CHECKCAST, methodType));

            // swap the value to be matched with the matcher code
            ins.add(new InsnNode(Opcodes.SWAP));

            if (methodType.startsWith("pattern"))
              ins.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, methodType, Names.MATCH, var.getJavaInvokeSig()));
            else
              ins.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, methodType, Names.MATCH, var.getJavaInvokeSig()));

            break;
          }
          default:
            errors.reportError("invalid kind for : " + ptnName + ":" + var.getType(), loc);
            return;
          }

          ISpec resSpec = SrcSpec.typeSpec(loc, TypeUtils.getPtnResultType(ptnType), dict, bldCat, errors);
          compilePtn(CafeSyntax.ptnCallResult(ptn), access, resSpec, dict, outer, endLabel, errors, varHandler,
              new JumpCont(exceptExit), new JumpCont(exceptLbl), ccxt);

          hwm.reset(mark);

          ins.add(exceptExit);

          succ.cont(resSpec, outer, loc, errors, ccxt);

          ins.add(exceptLbl);

          // Start code for checking exception itself
          ins.add(new TypeInsnNode(Opcodes.CHECKCAST, Types.FAILURE_EXCEPTION));

          fail.cont(SrcSpec.prcSrc, dict, loc, errors, ccxt);

          mtd.tryCatchBlocks.add(new TryCatchBlockNode(start, exceptLbl, exceptLbl, Types.FAILURE_EXCEPTION));
        } else if (TypeUtils.isConstructorType(ptnType)) {
          LabelNode okLbl = new LabelNode();

          VarInfo src = dict.declareLocal(GenSym.genSym("__"), SrcSpec.constructorSrc, true, AccessMode.readOnly);
          LabelNode end = new LabelNode();
          ins.add(new VarInsnNode(Opcodes.ASTORE, src.getOffset()));
          mtd.localVariables.add(new LocalVariableNode(src.getJavaSafeName(), src.getJavaSig(), null, start, end, src
              .getOffset()));

          var.loadValue(mtd, hwm, dict);
          ins.add(new TypeInsnNode(Opcodes.CHECKCAST, Types.ICONSTRUCTOR_FUNCTION));
          src.loadValue(mtd, hwm, dict);
          ins.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Types.ICONSTRUCTOR_FUNCTION, Constructors.VERIFY,
              Constructors.VERIFY_INVOKE_SIG));
          ins.add(new JumpInsnNode(Opcodes.IFNE, okLbl));
          fail.cont(var, outer, loc, errors, ccxt);
          Utils.jumpTarget(ins, okLbl);

          tuplePtnArgs(src, CafeSyntax.ptnCallResult(ptn), access, dict, outer, varHandler, endLabel, succ, fail,
              errors, ccxt);
          ins.add(end);
        } else
          errors.reportError("expecting a call pattern, not " + ptn, ptn.getLoc());
      } else
        errors.reportError("variable " + callPtn + " not declared", ptn.getLoc());
    }
  }

  private static void ptnArgs(IList args, ISpec[] argSpecs, CafeDictionary dict, CafeDictionary outer,
      AccessMode access, LabelNode endLabel, ErrorReport errors, VarPattern varHandler, IContinuation fail,
      IContinuation succ, CodeContext ccxt)
  {
    int nonAnons = countNonAnons(args);

    MethodNode mtd = ccxt.getMtd();
    HWM hwm = ccxt.getMtdHwm();
    InsnList ins = mtd.instructions;

    VarInfo argsSrc;
    LabelNode argEnd;

    if (nonAnons > 1) {
      argsSrc = dict.declareLocal(((IAbstract) args.getCell(0)).getLoc(), GenSym.genSym("__A"), true,
          StandardTypes.voidType, null, "[" + Types.JAVA_STRING_SIG, null, null, AccessMode.readOnly);
      LabelNode argStart = new LabelNode();
      ins.add(argStart);
      ins.add(new VarInsnNode(Opcodes.ASTORE, argsSrc.getOffset()));
      argEnd = new LabelNode();
      mtd.localVariables.add(new LocalVariableNode(argsSrc.getJavaSafeName(), argsSrc.getJavaSig(), argsSrc
          .getJavaSig(), argStart, argEnd, argsSrc.getOffset()));
    } else {
      argsSrc = null;
      argEnd = null;
    }

    if (nonAnons > 0) {
      for (int ix = 0; ix < args.size(); ix++) {
        IAbstract arg = (IAbstract) args.getCell(ix);
        int mark = hwm.getDepth();
        if (!CafeSyntax.isAnonymous(arg)) {
          if (argsSrc != null) {
            ins.add(new VarInsnNode(Opcodes.ALOAD, argsSrc.getOffset()));
            hwm.bump(1);
          }
          Expressions.genIntConst(ins, hwm, ix);
          ins.add(new InsnNode(Opcodes.AALOAD));

          if (--nonAnons == 0)
            compilePtn(arg, access, argSpecs[ix], dict, outer, endLabel, errors, varHandler, succ, fail, ccxt);
          else {
            LabelNode next = new LabelNode();

            compilePtn(arg, access, argSpecs[ix], dict, outer, endLabel, errors, varHandler, new JumpCont(next), fail,
                ccxt);
            Utils.jumpTarget(ins, next);
          }
        }
        hwm.reset(mark);
      }
      if (argEnd != null)
        ins.add(argEnd);
    } else
      ins.add(new InsnNode(Opcodes.POP));
  }

  private static int countNonAnons(IList args)
  {
    int nonAnons = 0;
    for (int ix = 0; ix < args.size(); ix++)
      if (!CafeSyntax.isAnonymous((IAbstract) args.getCell(ix)))
        nonAnons++;
    return nonAnons;
  }

  private static void handleInteger(ISpec termSpec, IntegerLiteral lit, ErrorReport errors, CafeDictionary dict,
      IContinuation succ, IContinuation fail, CodeContext ccxt)
  {
    Location loc = lit.getLoc();

    MethodNode mtd = ccxt.getMtd();
    HWM hwm = ccxt.getMtdHwm();
    CodeCatalog bldCat = ccxt.getBldCat();

    Expressions.checkType(termSpec, SrcSpec.rawIntSrc, mtd, dict, hwm, loc, errors, bldCat);

    int mark = hwm.getDepth();
    InsnList ins = mtd.instructions;

    Expressions.genIntConst(ins, hwm, lit.getLit());

    LabelNode okLabel = new LabelNode();
    ins.add(new JumpInsnNode(Opcodes.IF_ICMPEQ, okLabel));
    fail.cont(SrcSpec.prcSrc, dict, loc, errors, ccxt);
    ins.add(okLabel);
    succ.cont(SrcSpec.prcSrc, dict, loc, errors, ccxt);
    hwm.reset(mark);
  }

  private static void handleLong(ISpec termSpec, LongLiteral lit, ErrorReport errors, CafeDictionary dict,
      IContinuation succ, IContinuation fail, CodeContext ccxt)
  {
    Location loc = lit.getLoc();
    MethodNode mtd = ccxt.getMtd();
    HWM hwm = ccxt.getMtdHwm();
    CodeCatalog bldCat = ccxt.getBldCat();

    Expressions.checkType(termSpec, SrcSpec.rawLngSrc, mtd, dict, hwm, loc, errors, bldCat);

    int mark = hwm.getDepth();
    InsnList ins = mtd.instructions;
    Expressions.genLongConst(ins, hwm, lit.getLit());

    LabelNode okLabel = new LabelNode();
    ins.add(new InsnNode(Opcodes.LCMP));
    ins.add(new JumpInsnNode(Opcodes.IFEQ, okLabel));
    fail.cont(SrcSpec.prcSrc, dict, loc, errors, ccxt);
    ins.add(okLabel);
    succ.cont(SrcSpec.prcSrc, dict, loc, errors, ccxt);
    hwm.reset(mark);
  }

  private static void handleFloat(ISpec termSpec, FloatLiteral lit, ErrorReport errors, CafeDictionary dict,
      IContinuation succ, IContinuation fail, CodeContext ccxt)
  {
    Location loc = lit.getLoc();
    MethodNode mtd = ccxt.getMtd();
    HWM hwm = ccxt.getMtdHwm();
    CodeCatalog bldCat = ccxt.getBldCat();

    Expressions.checkType(termSpec, SrcSpec.rawDblSrc, mtd, dict, hwm, loc, errors, bldCat);

    int mark = hwm.bump(2);
    InsnList ins = mtd.instructions;
    double dx = lit.getLit();

    if (dx == 0.0)
      ins.add(new InsnNode(Opcodes.DCONST_0));
    else if (dx == 1.0)
      ins.add(new InsnNode(Opcodes.DCONST_1));
    else
      ins.add(new LdcInsnNode(dx));

    LabelNode okLabel = new LabelNode();
    ins.add(new InsnNode(Opcodes.DCMPG));
    ins.add(new JumpInsnNode(Opcodes.IFEQ, okLabel));
    fail.cont(SrcSpec.prcSrc, dict, loc, errors, ccxt);
    ins.add(okLabel);
    succ.cont(SrcSpec.prcSrc, dict, loc, errors, ccxt);
    hwm.reset(mark);
  }

  private static void handleBignum(ISpec termSpec, BigDecimalLiteral lit, ErrorReport errors, CafeDictionary dict,
      IContinuation succ, IContinuation fail, CodeContext ccxt)
  {
    Location loc = lit.getLoc();
    MethodNode mtd = ccxt.getMtd();
    HWM hwm = ccxt.getMtdHwm();
    CodeCatalog bldCat = ccxt.getBldCat();

    Expressions.checkType(termSpec, SrcSpec.rawDecimalSrc, mtd, dict, hwm, loc, errors, bldCat);

    int mark = hwm.getDepth();
    InsnList ins = mtd.instructions;
    BigDecimal big = lit.getLit();

    Expressions.genDecimalConst(ins, hwm, big);

    ins.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Types.OBJECT, Expressions.EQUALS, Expressions.EQUAL_SIG));

    LabelNode okLabel = new LabelNode();
    ins.add(new JumpInsnNode(Opcodes.IFEQ, okLabel));
    fail.cont(SrcSpec.prcSrc, dict, loc, errors, ccxt);
    ins.add(okLabel);
    succ.cont(SrcSpec.prcSrc, dict, loc, errors, ccxt);
    hwm.reset(mark);
  }

  private static void handleCharLiteral(ISpec termSpec, CharLiteral lit, ErrorReport errors, CafeDictionary dict,
      IContinuation succ, IContinuation fail, CodeContext ccxt)
  {
    Location loc = lit.getLoc();
    MethodNode mtd = ccxt.getMtd();
    HWM hwm = ccxt.getMtdHwm();
    CodeCatalog bldCat = ccxt.getBldCat();

    Expressions.checkType(termSpec, SrcSpec.rawCharSrc, mtd, dict, hwm, loc, errors, bldCat);

    int mark = hwm.getDepth();
    InsnList ins = mtd.instructions;

    Expressions.genIntConst(ins, hwm, lit.getLit());

    LabelNode okLabel = new LabelNode();
    ins.add(new JumpInsnNode(Opcodes.IF_ICMPEQ, okLabel));
    fail.cont(SrcSpec.prcSrc, dict, loc, errors, ccxt);
    ins.add(okLabel);
    succ.cont(SrcSpec.prcSrc, dict, loc, errors, ccxt);
    hwm.reset(mark);
  }

  private static void handleStringLiteral(ISpec termSpec, StringLiteral lit, ErrorReport errors, CafeDictionary dict,
      IContinuation succ, IContinuation fail, CodeContext ccxt)
  {
    Location loc = lit.getLoc();
    MethodNode mtd = ccxt.getMtd();
    HWM hwm = ccxt.getMtdHwm();
    CodeCatalog bldCat = ccxt.getBldCat();

    Expressions.checkType(termSpec, SrcSpec.rawStringSrc, mtd, dict, hwm, loc, errors, bldCat);

    int mark = hwm.bump(1);
    InsnList ins = mtd.instructions;

    ins.add(new LdcInsnNode(lit.getLit()));
    ins.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, Types.OBJECT, Expressions.EQUALS, Expressions.EQUAL_SIG));

    LabelNode okLabel = new LabelNode();
    ins.add(new JumpInsnNode(Opcodes.IFNE, okLabel));
    fail.cont(SrcSpec.prcSrc, dict, loc, errors, ccxt);
    ins.add(okLabel);
    succ.cont(SrcSpec.prcSrc, dict, loc, errors, ccxt);
    hwm.reset(mark);
  }

  private static void handleRegexp(ISpec termSpec, IAbstract regexp, ErrorReport errors, LabelNode endLabel,
      AccessMode access, CafeDictionary dict, CafeDictionary outer, VarPattern varHandler, IContinuation succ,
      IContinuation fail, CodeContext ccxt)
  {
    Location loc = regexp.getLoc();
    assert CafeSyntax.isRegexp(regexp);
    MethodNode mtd = ccxt.getMtd();
    HWM hwm = ccxt.getMtdHwm();
    CodeCatalog bldCat = ccxt.getBldCat();

    Expressions.checkType(termSpec, SrcSpec.rawStringSrc, mtd, dict, hwm, loc, errors, bldCat);

    InsnList ins = mtd.instructions;

    ICafeBuiltin matcher = Intrinsics.getBuiltin(RegexpOps.name);
    String regPtn = CafeSyntax.regexpExp(regexp);
    // Each regular expression will have its own name - because it encodes the
    // pattern also
    String localName = Utils.javaIdentifierOf("regexp" + regPtn);
    String javaSig = Utils.javaTypeSig(Regexp.class);
    dict.addReference(localName, new RegexpInliner(localName, regPtn, javaSig));

    hwm.probe(1);
    ins.add(new FieldInsnNode(Opcodes.GETSTATIC, dict.getOwnerName(), localName, matcher.getJavaSig()));
    ins.add(new InsnNode(Opcodes.SWAP));
    ins.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, matcher.getJavaType(), Names.MATCH, Types.MATCH_SIG));

    LabelNode okLabel = new LabelNode();
    hwm.probe(1);
    ins.add(new InsnNode(Opcodes.DUP));
    ins.add(new JumpInsnNode(Opcodes.IFNONNULL, okLabel));
    ins.add(new InsnNode(Opcodes.POP));
    fail.cont(SrcSpec.prcSrc, dict, loc, errors, ccxt);
    ins.add(okLabel);
    IList regexpArgs = CafeSyntax.regexpArgs(regexp);
    ISpec[] argTypes = new ISpec[regexpArgs.size()];
    for (int ix = 0; ix < argTypes.length; ix++)
      argTypes[ix] = SrcSpec.rawStringSrc;
    ptnArgs(regexpArgs, argTypes, dict, outer, access, endLabel, errors, varHandler, fail, succ, ccxt);
    succ.cont(SrcSpec.prcSrc, dict, loc, errors, ccxt);
  }

  private static class RegexpInliner implements Inliner
  {
    private final String regexp;
    private final String name;
    private final String javaSig;

    RegexpInliner(String name, String regexp, String javaSig)
    {
      this.name = name;
      this.regexp = regexp;
      this.javaSig = javaSig;
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM)
    {
    }

    @Override
    public void inline(ClassNode klass, MethodNode mtd, HWM hwm, Location loc)
    {
      int mark = hwm.bump(3);
      InsnList ins = new InsnList();

      // This bit of hackery allows us to do regexp a bit more efficiently
      String regexpClass = Utils.javaInternalClassName(Regexp.class);
      ins.add(new TypeInsnNode(Opcodes.NEW, regexpClass));
      ins.add(new InsnNode(Opcodes.DUP));

      ins.add(new LdcInsnNode(regexp));

      // Theta.setupClassTypeContext(klass.name, ins, hwm);
      ins.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, regexpClass, Types.INIT, "(" + Types.JAVA_STRING_SIG + ")V"));

      ins.add(new FieldInsnNode(Opcodes.PUTSTATIC, klass.name, name, javaSig));
      hwm.reset(mark);

      mtd.instructions.insert(ins);
      Theta.addField(klass, name, javaSig, Opcodes.ACC_STATIC);
    }
  }

  public static class NamePtn implements VarPattern
  {
    @Override
    public void varPttrn(ISpec src, Location loc, String name, ErrorReport errors, CafeDictionary dict,
        IContinuation succ, IContinuation fail, CodeContext ccxt)
    {
      handleName(src, loc, name, errors, dict, succ, fail, ccxt);
    }

  }

  private static void handleName(ISpec termSpec, Location loc, String name, ErrorReport errors, CafeDictionary dict,
      IContinuation succ, IContinuation fail, CodeContext ccxt)
  {
    VarInfo var = dict.find(name);
    MethodNode mtd = ccxt.getMtd();
    HWM hwm = ccxt.getMtdHwm();
    CodeCatalog bldCat = ccxt.getBldCat();

    InsnList ins = mtd.instructions;
    LabelNode okLabel = new LabelNode();
    int mark = hwm.getDepth();

    if (var != null) // Already declared?
    {
      Expressions.checkType(termSpec, var, mtd, dict, hwm, loc, errors, bldCat);

      switch (var.getKind()) {
      case builtin:
        errors.reportError(name + "is a builtin name, not permitted as a pattern", loc);
        break;
      case constructor: {
        TypeDescription desc = (TypeDescription) dict.findType(var.getType().typeLabel());
        if (desc == null)
          errors.reportError("type " + var.getType() + " not declared", loc);
        else {
          IValueSpecifier spec = desc.getValueSpecifier(var.getName());
          if (!(spec instanceof ConstructorSpecifier))
            errors.reportError("unknown constructor: " + var.getName(), loc);
          else if (((ConstructorSpecifier) spec).arity() != 0)
            errors.reportError(name + " is a " + ((ConstructorSpecifier) spec).arity() + "constructor", loc);
          else {
            ConstructorSpecifier cons = (ConstructorSpecifier) spec;
            hwm.bump(1);
            ins.add(new TypeInsnNode(Opcodes.CHECKCAST, Types.ICONSTRUCTOR));
            ins.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Types.ICONSTRUCTOR, Constructors.CONIX, "()I"));
            ins.add(new IntInsnNode(Opcodes.ILOAD, cons.getConIx()));
            ins.add(new JumpInsnNode(Opcodes.IF_ICMPEQ, okLabel));
            fail.cont(SrcSpec.prcSrc, dict, loc, errors, ccxt);
          }
        }
        break;
      }
      case rawBool:
        var.loadValue(mtd, hwm, dict);
        ins.add(new JumpInsnNode(Opcodes.IF_ICMPEQ, okLabel));
        fail.cont(SrcSpec.prcSrc, dict, loc, errors, ccxt);
        break;

      case rawChar:
        var.loadValue(mtd, hwm, dict);
        ins.add(new JumpInsnNode(Opcodes.IF_ICMPEQ, okLabel));
        fail.cont(SrcSpec.prcSrc, dict, loc, errors, ccxt);
        break;
      case rawInt:
        var.loadValue(mtd, hwm, dict);
        ins.add(new JumpInsnNode(Opcodes.IF_ICMPEQ, okLabel));
        fail.cont(SrcSpec.prcSrc, dict, loc, errors, ccxt);
        break;
      case rawLong:
        var.loadValue(mtd, hwm, dict);
        ins.add(new InsnNode(Opcodes.LCMP));
        ins.add(new JumpInsnNode(Opcodes.IFEQ, okLabel));
        fail.cont(SrcSpec.prcSrc, dict, loc, errors, ccxt);
        break;
      case rawFloat:
        var.loadValue(mtd, hwm, dict);
        ins.add(new InsnNode(Opcodes.DCMPG));
        ins.add(new JumpInsnNode(Opcodes.IFEQ, okLabel));
        fail.cont(SrcSpec.prcSrc, dict, loc, errors, ccxt);
        break;
      case rawBinary:
      case rawString:
      case general:
      case rawDecimal:
        var.loadValue(mtd, hwm, dict);
        ins.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, Types.OBJECT, "equals", Types.EQUALS_SIG));
        ins.add(new JumpInsnNode(Opcodes.IFNE, okLabel));
        fail.cont(SrcSpec.prcSrc, dict, loc, errors, ccxt);
        break;
      case userJava:
        assert false : "invalid kind of pattern";
      }
    } else if (Utils.isAnonymous(name))
      ins.add(new InsnNode(Opcodes.POP));
    else
      errors.reportError(name + " not declared", loc);

    ins.add(okLabel);
    succ.cont(SrcSpec.prcSrc, dict, loc, errors, ccxt);
    hwm.reset(mark);
  }

  private static void constructorPtn(ISpec termSpec, IAbstract ptn, AccessMode access, CafeDictionary dict,
      CafeDictionary outer, LabelNode endLabel, ErrorReport errors, VarPattern varHandler, IContinuation succ,
      IContinuation fail, CodeContext ccxt)
  {
    assert CafeSyntax.isConstructor(ptn);
    Location loc = ptn.getLoc();
    MethodNode mtd = ccxt.getMtd();
    InsnList ins = mtd.instructions;
    HWM hwm = ccxt.getMtdHwm();

    String lbl = CafeSyntax.constructorOp(ptn);

    VarInfo var = Theta.varReference(lbl, dict, outer, loc, errors);

    if (var != null) {
      switch (var.getKind()) {
      case constructor: {
        IType type = TypeUtils.getConstructorResultType(var.getType());
        TypeDescription desc = (TypeDescription) dict.findType(type.typeLabel());
        if (desc != null && desc.getValueSpecifier(lbl) != null) {
          ICafeConstructorSpecifier con = (ICafeConstructorSpecifier) desc.getValueSpecifier(lbl);
          if (desc.getValueSpecifiers().size() > 1)
            checkConIx(con, ptn.getLoc(), dict, errors, fail, ccxt);
          String conJavaType = con.getJavaType();
          VarInfo src = dict.declareLocal(GenSym.genSym("__"), termSpec, true, AccessMode.readOnly);
          LabelNode start = new LabelNode();
          LabelNode end = new LabelNode();
          ins.add(new TypeInsnNode(Opcodes.CHECKCAST, conJavaType));
          ins.add(start);
          ins.add(new VarInsnNode(Opcodes.ASTORE, src.getOffset()));
          mtd.localVariables.add(new LocalVariableNode(src.getJavaSafeName(), src.getJavaSig(), null, start, end, src
              .getOffset()));
          constructorPtnArgs(src, lbl, CafeSyntax.constructorArgs(ptn), desc, dict, outer, access, endLabel, errors,
              varHandler, fail, succ, ccxt);
          ins.add(end);
        } else
          errors.reportError(StringUtils.msg(lbl, " is not a constructor of ", type), ptn.getLoc());
        return;
      }

      case general: {
        LabelNode okLbl = new LabelNode();

        VarInfo src = dict.declareLocal(GenSym.genSym("__"), SrcSpec.constructorSrc, true, AccessMode.readOnly);
        LabelNode start = new LabelNode();
        LabelNode end = new LabelNode();
        ins.add(start);
        ins.add(new VarInsnNode(Opcodes.ASTORE, src.getOffset()));
        mtd.localVariables.add(new LocalVariableNode(src.getJavaSafeName(), src.getJavaSig(), null, start, end, src
            .getOffset()));

        var.loadValue(mtd, hwm, dict);
        ins.add(new TypeInsnNode(Opcodes.CHECKCAST, Types.ICONSTRUCTOR_FUNCTION));
        src.loadValue(mtd, hwm, dict);
        ins.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Types.ICONSTRUCTOR_FUNCTION, Constructors.VERIFY,
            Constructors.VERIFY_INVOKE_SIG));
        ins.add(new JumpInsnNode(Opcodes.IFNE, okLbl));
        fail.cont(var, outer, loc, errors, ccxt);
        Utils.jumpTarget(ins, okLbl);

        tuplePtnArgs(src, ptn, access, dict, outer, varHandler, endLabel, succ, fail, errors, ccxt);
        ins.add(end);
        return;
      }

      default:
        errors.reportError(StringUtils.msg(var.getName(), " is not a valid pattern name"), loc);
      }
    } else
      errors.reportError(StringUtils.msg("pattern constructor ", lbl, " not known"), loc);
  }

  public static void checkConIx(ICafeConstructorSpecifier con, Location loc, CafeDictionary dict, ErrorReport errors,
      IContinuation fail, CodeContext ccxt)
  {
    // Check we have the right constructor, but do not bother if there only
    // is one constructor
    int conIx = con.getConIx();
    LabelNode nx = new LabelNode();
    MethodNode mtd = ccxt.getMtd();
    HWM hwm = ccxt.getMtdHwm();
    InsnList ins = mtd.instructions;
    ins.add(new InsnNode(Opcodes.DUP));
    ins.add(new TypeInsnNode(Opcodes.CHECKCAST, Types.ICONSTRUCTOR));
    ins.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Types.ICONSTRUCTOR, Constructors.CONIX, "()I"));
    Expressions.genIntConst(ins, hwm, conIx);
    ins.add(new JumpInsnNode(Opcodes.IF_ICMPEQ, nx));
    ins.add(new InsnNode(Opcodes.POP));
    fail.cont(SrcSpec.prcSrc, dict, loc, errors, ccxt);
    Utils.jumpTarget(ins, nx);
  }

  public static void tuplePtn(IAbstract ptn, AccessMode access, CafeDictionary dict, CafeDictionary outer,
      LabelNode endLabel, ErrorReport errors, VarPattern varHandler, IContinuation succ, IContinuation fail,
      CodeContext ccxt)
  {
    MethodNode mtd = ccxt.getMtd();
    HWM hwm = ccxt.getMtdHwm();
    InsnList ins = mtd.instructions;
    IList args = CafeSyntax.constructorArgs(ptn);
    // Check we have the right size tuple
    int conIx = args.size();
    LabelNode nx = new LabelNode();

    if (conIx == 0) {
      ins.add(new TypeInsnNode(Opcodes.CHECKCAST, Types.ICONSTRUCTOR));
      ins.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Types.ICONSTRUCTOR, Constructors.CONIX, "()I"));
      Expressions.genIntConst(ins, hwm, conIx);
      ins.add(new JumpInsnNode(Opcodes.IF_ICMPEQ, nx));
      fail.cont(SrcSpec.prcSrc, dict, ptn.getLoc(), errors, ccxt);
      Utils.jumpTarget(ins, nx);
      succ.cont(SrcSpec.prcSrc, dict, ptn.getLoc(), errors, ccxt);
    } else {
      ins.add(new InsnNode(Opcodes.DUP));
      ins.add(new TypeInsnNode(Opcodes.CHECKCAST, Types.ICONSTRUCTOR));
      ins.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Types.ICONSTRUCTOR, Constructors.CONIX, "()I"));
      Expressions.genIntConst(ins, hwm, conIx);
      ins.add(new JumpInsnNode(Opcodes.IF_ICMPEQ, nx));
      ins.add(new InsnNode(Opcodes.POP));
      fail.cont(SrcSpec.prcSrc, dict, ptn.getLoc(), errors, ccxt);
      Utils.jumpTarget(ins, nx);

      VarInfo src = dict.declareLocal(GenSym.genSym("__"), SrcSpec.constructorSrc, true, AccessMode.readOnly);
      LabelNode start = new LabelNode();
      LabelNode end = new LabelNode();
      ins.add(start);
      ins.add(new VarInsnNode(Opcodes.ASTORE, src.getOffset()));
      mtd.localVariables.add(new LocalVariableNode(src.getJavaSafeName(), src.getJavaSig(), null, start, end, src
          .getOffset()));

      for (int ix = 0; ix < args.size(); ix++) {
        IAbstract arg = (IAbstract) args.getCell(ix);
        if (!CafeSyntax.isAnonymous(arg)) {

          src.loadValue(mtd, hwm, dict);
          Expressions.genIntConst(ins, hwm, ix);

          ins.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Types.ICONSTRUCTOR, "getCell", "(I)" + Types.IVALUE_SIG));

          LabelNode next = new LabelNode();
          compilePtn(arg, access, SrcSpec.generalSrc, dict, outer, endLabel, errors, varHandler, new JumpCont(next),
              fail, ccxt);
          Utils.jumpTarget(ins, next);
        }
      }
      succ.cont(SrcSpec.prcSrc, dict, ptn.getLoc(), errors, ccxt);
      ins.add(end);
    }
  }

  public static void handleFacePtn(IAbstract ptn, AccessMode access, CafeDictionary dict, CafeDictionary outer,
      LabelNode endLabel, ErrorReport errors, VarPattern varHandler, IContinuation succ, IContinuation fail,
      CodeContext ccxt)
  {
    assert CafeSyntax.isFace(ptn);

    MethodNode mtd = ccxt.getMtd();
    HWM hwm = ccxt.getMtdHwm();
    InsnList ins = mtd.instructions;

    IList args = CafeSyntax.faceContents(ptn);

    // Check we have an anonymous record

    ins.add(new TypeInsnNode(Opcodes.CHECKCAST, Types.ANON_RECORD_TYPE));

    if (!args.isEmpty()) {
      LabelNode start = new LabelNode();
      LabelNode end = new LabelNode();
      VarInfo src = dict.declareLocal(GenSym.genSym("__"), SrcSpec.anonSrc, true, AccessMode.readOnly);
      ins.add(start);
      ins.add(new VarInsnNode(Opcodes.ASTORE, src.getOffset()));
      mtd.localVariables.add(new LocalVariableNode(src.getJavaSafeName(), src.getJavaSig(), null, start, end, src
          .getOffset()));

      for (int ix = 0; ix < args.size(); ix++) {
        IAbstract arg = (IAbstract) args.getCell(ix);
        if (CafeSyntax.isField(arg)) {

          src.loadValue(mtd, hwm, dict);

          ins.add(new LdcInsnNode(CafeSyntax.fieldName(arg)));

          ins.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Types.IRECORD, "getMember", "(" + Types.JAVA_STRING_SIG
              + ")" + Types.IVALUE_SIG));

          LabelNode next = new LabelNode();
          compilePtn(arg, access, SrcSpec.generalSrc, dict, outer, endLabel, errors, varHandler, new JumpCont(next),
              fail, ccxt);
          Utils.jumpTarget(ins, next);
        }
      }
      ins.add(end);
    }
    succ.cont(SrcSpec.prcSrc, dict, ptn.getLoc(), errors, ccxt);
  }

  public static void tuplePtnArgs(VarInfo src, IAbstract tpl, AccessMode access, CafeDictionary dict,
      CafeDictionary outer, VarPattern varHandler, LabelNode endLabel, IContinuation succ, IContinuation fail,
      ErrorReport errors, CodeContext ccxt)
  {
    MethodNode mtd = ccxt.getMtd();
    HWM hwm = ccxt.getMtdHwm();
    InsnList ins = mtd.instructions;

    Location loc = tpl.getLoc();

    if (CafeSyntax.isConstructor(tpl)) {
      IList args = CafeSyntax.constructorArgs(tpl);
      for (int ix = 0; ix < args.size(); ix++) {
        IAbstract arg = (IAbstract) args.getCell(ix);
        if (!CafeSyntax.isAnonymous(arg)) {

          src.loadValue(mtd, hwm, dict);
          Expressions.genIntConst(ins, hwm, ix);

          ins.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Types.ICONSTRUCTOR, "getCell", "(I)" + Types.IVALUE_SIG));

          LabelNode next = new LabelNode();
          compilePtn(arg, access, SrcSpec.generalSrc, dict, outer, endLabel, errors, varHandler, new JumpCont(next),
              fail, ccxt);
          Utils.jumpTarget(ins, next);
        }
      }
    } else
      errors.reportError(StringUtils.msg("invalid form of constructor argument: ", tpl), loc);
    succ.cont(SrcSpec.prcSrc, dict, loc, errors, ccxt);
  }

  public static void constructorPtnArgs(VarInfo var, String lbl, IList args, ITypeDescription desc,
      CafeDictionary dict, CafeDictionary outer, AccessMode access, LabelNode endLabel, ErrorReport errors,
      VarPattern varHandler, IContinuation fail, IContinuation succ, CodeContext ccxt)
  {
    MethodNode mtd = ccxt.getMtd();
    HWM hwm = ccxt.getMtdHwm();
    CodeCatalog bldCat = ccxt.getBldCat();

    ICafeConstructorSpecifier con = (ICafeConstructorSpecifier) ((IAlgebraicType) desc).getValueSpecifier(lbl);
    String conJavaType = con.getJavaType();
    IType conType = Freshen.freshenForUse(con.getConType());
    if (TypeUtils.isConstructorType(conType)) {
      ISpec[] conArgSpecs = SrcSpec.genericConstructorSpecs(conType, dict, bldCat, ccxt.getRepository(), errors, var
          .getLoc());

      InsnList ins = mtd.instructions;

      for (int ix = 0; ix < args.size(); ix++) {
        IAbstract arg = (IAbstract) args.getCell(ix);
        if (!CafeSyntax.isAnonymous(arg)) {
          String id = Utils.javaIdentifierOf(con.memberName(ix));
          String conArgJavaType = conArgSpecs[ix].getJavaSig();

          var.loadValue(mtd, hwm, dict);
          if (!conJavaType.equals(var.getJavaType()))
            ins.add(new TypeInsnNode(Opcodes.CHECKCAST, conJavaType));

          ins.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, conJavaType, Types.getterName(id), "()" + conArgJavaType));

          LabelNode next = new LabelNode();
          compilePtn(arg, access, conArgSpecs[ix], dict, outer, endLabel, errors, varHandler, new JumpCont(next), fail,
              ccxt);
          Utils.jumpTarget(ins, next);
        }
      }
    }
    succ.cont(SrcSpec.prcSrc, dict, var.getLoc(), errors, ccxt);
  }

  private static void defineNewVar(ISpec termSpec, IAbstract trm, ErrorReport errors, AccessMode access,
      CafeDictionary dict, LabelNode endLabel, IContinuation succ, CodeContext ccxt)
  {
    assert CafeSyntax.isTypedTerm(trm);

    Location loc = trm.getLoc();

    MethodNode mtd = ccxt.getMtd();

    IAbstract var = CafeSyntax.typedTerm(trm);
    IType varType = TypeAnalyser.parseType(CafeSyntax.typedType(trm), dict, errors);

    if (var instanceof Name) {
      String varName = Abstract.getId(var);

      InsnList ins = mtd.instructions;

      VarInfo v = dict.find(varName);
      if (v == null) {
        ISpec vrSpec = SrcSpec.generic(loc, varType, dict, ccxt.getRepository(), errors);

        v = dict.declareLocal(varName, vrSpec, true, access);
        LabelNode startLabel = new LabelNode();
        mtd.localVariables.add(new LocalVariableNode(v.getJavaSafeName(), v.getJavaSig(), null, startLabel, endLabel, v
            .getOffset()));
        ins.add(startLabel);
      } else if (!v.isInited()) {
        v.setInited(true);
        switch (v.getWhere()) {
        case localVar: {
          LabelNode startLabel = new LabelNode();

          mtd.localVariables.add(new LocalVariableNode(v.getJavaSafeName(), v.getJavaSig(), null, startLabel, endLabel,
              v.getOffset()));
          ins.add(startLabel);
          break;
        }
        default:
        }

      }

      new StoreCont(v, dict).cont(termSpec, dict, loc, errors, ccxt);
    } else
      errors.reportError("expecting a variable, not: " + var, loc);
    succ.cont(SrcSpec.prcSrc, dict, loc, errors, ccxt);
  }

  public static List<String> declaredVars(IAbstract ptn)
  {
    List<String> vars = new ArrayList<>();

    declareVars(ptn, vars);

    return vars;
  }

  private static void declareVars(IAbstract ptn, List<String> vars)
  {
    if (CafeSyntax.termHasType(ptn)) {
      if (!CafeSyntax.isAnonymous(ptn)) {
        if (Abstract.isName(ptn))
          vars.add(Abstract.getId(ptn));
      }
    } else if (CafeSyntax.isTypedTerm(ptn)) {
      IAbstract term = CafeSyntax.typedTerm(ptn);

      if (!CafeSyntax.isAnonymous(term)) {
        if (Abstract.isName(term))
          vars.add(Abstract.getId(term));
      }
    } else if (CafeSyntax.isConstructor(ptn)) {
      IList conArgs = CafeSyntax.constructorArgs(ptn);
      for (int ix = 0; ix < conArgs.size(); ix++)
        declareVars((IAbstract) conArgs.getCell(ix), vars);
    }
  }
}
