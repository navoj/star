package org.star_lang.star.compiler.canonical.compile;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.star_lang.star.code.repository.CodeCatalog;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.cafe.Names;
import org.star_lang.star.compiler.cafe.compile.CafeDictionary;
import org.star_lang.star.compiler.cafe.compile.Constructors;
import org.star_lang.star.compiler.cafe.compile.IFuncImplementation;
import org.star_lang.star.compiler.cafe.compile.ISpec;
import org.star_lang.star.compiler.cafe.compile.JavaKind;
import org.star_lang.star.compiler.cafe.compile.SrcSpec;
import org.star_lang.star.compiler.cafe.compile.Theta;
import org.star_lang.star.compiler.cafe.compile.Types;
import org.star_lang.star.compiler.cafe.compile.Utils;
import org.star_lang.star.compiler.cafe.compile.VarInfo;
import org.star_lang.star.compiler.cafe.compile.VarSource;
import org.star_lang.star.compiler.canonical.Application;
import org.star_lang.star.compiler.canonical.CanonUtils;
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
import org.star_lang.star.compiler.canonical.RaiseExpression;
import org.star_lang.star.compiler.canonical.RecordSubstitute;
import org.star_lang.star.compiler.canonical.RecordTerm;
import org.star_lang.star.compiler.canonical.Resolved;
import org.star_lang.star.compiler.canonical.Scalar;
import org.star_lang.star.compiler.canonical.Shriek;
import org.star_lang.star.compiler.canonical.TransformExpression;
import org.star_lang.star.compiler.canonical.ValofExp;
import org.star_lang.star.compiler.canonical.Variable;
import org.star_lang.star.compiler.canonical.VoidExp;
import org.star_lang.star.compiler.canonical.compile.cont.Cast;
import org.star_lang.star.compiler.canonical.compile.cont.Combo;
import org.star_lang.star.compiler.canonical.compile.cont.JumpContinue;
import org.star_lang.star.compiler.canonical.compile.cont.NullCont;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.Pair;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.type.TypeExp;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.data.value.NTuple;
import org.star_lang.star.data.value.NTuple.NTpl;
import org.star_lang.star.operators.assignment.runtime.GetRefValue.GetRawBoolRef;
import org.star_lang.star.operators.assignment.runtime.GetRefValue.GetRawCharRef;
import org.star_lang.star.operators.assignment.runtime.GetRefValue.GetRawFloatRef;
import org.star_lang.star.operators.assignment.runtime.GetRefValue.GetRawIntegerRef;
import org.star_lang.star.operators.assignment.runtime.GetRefValue.GetRawLongRef;
import org.star_lang.star.operators.assignment.runtime.GetRefValue.GetRef;

/**
 * Generate JVM code for expressions
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
public class ExpressionCompile implements
    TransformExpression<FrameState, FrameState, FrameState, FrameState, FrameState, CompileContext>
{
  public static String EVAL_EXCEPT_SIG;
  public static String EQUAL_SIG;
  public static final boolean CHECK_NONNULL = false;

  static {
    try {
      Constructor<EvaluationException> evalExceptionCon = EvaluationException.class.getConstructor(IValue.class);
      EVAL_EXCEPT_SIG = org.objectweb.asm.Type.getConstructorDescriptor(evalExceptionCon);

      Method equalsMethod = Object.class.getDeclaredMethod("equals", Object.class);
      EQUAL_SIG = org.objectweb.asm.Type.getMethodDescriptor(equalsMethod);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public FrameState transformApplication(Application app, CompileContext context)
  {
    Location loc = app.getLoc();
    InsnList ins = context.getIns();

    // Set up the function on the stack
    FrameState funState = app.getFunction().transform(this, context);
    FrameState frameState = funState;

    IType funType = app.getFunction().getType();

    IContentExpression argTpl = app.getArgs();

    Pair<FrameState, Boolean> argState = compileArguments(argTpl, frameState, context);

    // actually invoke the function
    doLineNumber(loc, context);

    if (argState.right) {
      ISpec funSpec = funState.tos();
      String funJavaType = funSpec.getJavaType();
      // This is awful, but the JVM made me do it.
      if (funJavaType.startsWith(Types.FUN_PREFIX) || funJavaType.startsWith(Types.PRC_PREFIX))
        ins.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, funJavaType, Names.ENTER, funSpec.getJavaInvokeSig()));
      else
        ins.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, funJavaType, Names.ENTER, funSpec.getJavaInvokeSig()));
    } else
      ins.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Types.IFUNC, Names.ENTERFUNCTION,
          IFuncImplementation.IFUNCTION_INVOKE_SIG));

    FrameState reslt = context.getFrame().pushStack(
        SrcSpec.typeSpec(loc, TypeUtils.getFunResultType(funType), context.getDict(), context.getBldCat(), context
            .getErrors()));
    return context.cont(reslt, loc);
  }

  Pair<FrameState, Boolean> compileArguments(IContentExpression arg, FrameState frame, CompileContext cxt)
  {
    InsnList ins = cxt.getIns();

    if (CanonUtils.isTuple(arg)) {
      List<IContentExpression> args = CanonUtils.constructorArgs(arg);
      CompileContext argsCxt = cxt.fork(frame);

      ISpec argSpecs[] = SrcSpec.typeSpecs(TypeUtils.tupleTypes(arg.getType()), cxt.getDict(), cxt.getBldCat(), cxt
          .getRepository(), cxt.getErrors(), arg.getLoc());

      if (args.size() < Theta.MAX_ARGS)
        return Pair.pair(compileArgs(args, argSpecs, argsCxt), true);
      else
        return Pair.pair(argArray(args, argSpecs, argsCxt), false);
    } else if (CanonUtils.isAnonRecord(arg)) {
      Map<String, IContentExpression> elements = CanonUtils.recordElements((RecordTerm) arg);
      int arity = elements.size();

      if (arity < Theta.MAX_ARGS) {
        ISpec argSpecs[] = new ISpec[arity];
        Iterator<IContentExpression> it = elements.values().iterator();
        for (int ix = 0; ix < arity; ix++) {
          IContentExpression a = it.next();
          argSpecs[ix] = SrcSpec.typeSpec(a.getLoc(), a.getType(), cxt.getDict(), cxt.getBldCat(), cxt.getErrors());
        }
        int ix = 0;
        for (IContentExpression a : elements.values()) {
          JumpContinue jump = new JumpContinue(new LabelNode(), frame, cxt);
          cxt = cxt.fork(jump, frame);

          frame = a.transform(this, cxt);

          jump.jumpTarget(cxt.getIns(), frame, cxt);

          if (argSpecs != null)
            checkCast(cxt, frame.tos(), argSpecs[ix].getJavaType());
          ix++;
        }
        return Pair.pair(frame, true);
      } else {
        genIntConst(cxt, frame, arity);
        ins.add(new TypeInsnNode(Opcodes.ANEWARRAY, Types.IVALUE));
        FrameState frameState = frame.pushStack(SrcSpec.arraySrc);

        Iterator<IContentExpression> it = elements.values().iterator();
        for (int ix = 0; ix < arity; ix++) {
          IContentExpression a = it.next();

          FrameState subState = frameState.pushStack(SrcSpec.arraySrc);
          ins.add(new InsnNode(Opcodes.DUP));
          subState = genIntConst(cxt, subState, ix);

          JumpContinue jump = new JumpContinue(new LabelNode(), frameState, cxt);
          CompileContext argCxt = cxt.fork(jump, frameState);

          frameState = a.transform(this, argCxt);

          jump.jumpTarget(cxt.getIns(), subState, cxt);

          ins.add(new InsnNode(Opcodes.AASTORE));
        }
        return Pair.pair(frameState, false);
      }
    } else {
      LabelNode nxLbl = new LabelNode();
      JumpContinue cont = new JumpContinue(nxLbl, frame, cxt);
      FrameState argFrame = arg.transform(this, cxt.fork(cont, frame));

      cont.jumpTarget(ins, argFrame, cxt);

      ins.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Types.ICONSTRUCTOR, Constructors.GET_CELLS,
          Constructors.GETCELLS_INVOKESIG));
      return Pair.pair(frame.pushStack(SrcSpec.arraySrc), false);
    }
  }

  void doLineNumber(Location loc, CompileContext context)
  {
    Location lastLoc = context.getLastLoc();
    if (!loc.equals(Location.nullLoc) && lastLoc.getLineCnt() != loc.getLineCnt()) {
      MethodNode mtd = context.getMtd();

      LabelNode lnLbl = new LabelNode();
      mtd.instructions.add(lnLbl);
      mtd.instructions.add(new LineNumberNode(loc.getLineCnt(), lnLbl));
      context.setLastLoc(loc);
    }
  }

  @Override
  public FrameState transformRecord(RecordTerm record, CompileContext cxt)
  {
    SortedMap<String, IContentExpression> elements = record.getArguments();
    Location loc = record.getLoc();
    CafeDictionary dict = cxt.getDict();
    CodeCatalog bldCat = cxt.getBldCat();
    ErrorReport errors = cxt.getErrors();

    int arity = elements.size();
    ISpec argSpecs[] = new ISpec[arity];

    {
      Iterator<IContentExpression> it = elements.values().iterator();

      for (int ix = 0; ix < arity; ix++) {
        IContentExpression arg = it.next();
        argSpecs[ix] = SrcSpec.typeSpec(arg.getLoc(), arg.getType(), dict, bldCat, errors);
      }
    }

    if (arity < Theta.MAX_ARGS) {
      FrameState frame = cxt.getFrame();

      int ix = 0;
      for (IContentExpression arg : elements.values()) {
        JumpContinue jump = new JumpContinue(new LabelNode(), frame, cxt);
        cxt = cxt.fork(jump, frame);

        FrameState actual = arg.transform(this, cxt);

        jump.jumpTarget(cxt.getIns(), actual, cxt);

        if (argSpecs != null)
          checkCast(cxt, actual.tos(), argSpecs[ix].getJavaType());
        ix++;
      }
    } else {
      InsnList ins = cxt.getIns();
      FrameState frame = cxt.getFrame();

      genIntConst(cxt, frame, arity);
      ins.add(new TypeInsnNode(Opcodes.ANEWARRAY, Types.IVALUE));
      FrameState frameState = cxt.getFrame().pushStack(SrcSpec.arraySrc);

      Iterator<IContentExpression> it = elements.values().iterator();
      for (int ix = 0; ix < arity; ix++) {
        IContentExpression arg = it.next();

        FrameState subState = frameState.pushStack(SrcSpec.arraySrc);
        ins.add(new InsnNode(Opcodes.DUP));
        subState = genIntConst(cxt, subState, ix);

        JumpContinue jump = new JumpContinue(new LabelNode(), frameState, cxt);
        cxt = cxt.fork(jump, frameState);

        frameState = arg.transform(this, cxt);

        jump.jumpTarget(cxt.getIns(), subState, cxt);

        ins.add(new InsnNode(Opcodes.AASTORE));
      }
    }

    return cxt.getFrame().pushStack(SrcSpec.typeSpec(loc, record.getType(), dict, bldCat, errors));
  }

  @Override
  public FrameState transformRecordSubstitute(RecordSubstitute update, CompileContext context)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public FrameState transformCaseExpression(CaseExpression exp, CompileContext context)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public FrameState transformCastExpression(CastExpression exp, CompileContext cxt)
  {
    CodeCatalog bldCat = cxt.getBldCat();
    CafeDictionary dict = cxt.getDict();

    final IType castType = exp.getType();
    // we pretty much ignore a function type when casting to it...
    final ISpec castSpec = TypeUtils.isProgramType(castType) ? SrcSpec.generic(exp.getLoc(), castType, dict, cxt
        .getRepository(), cxt.getErrors()) : SrcSpec.typeSpec(exp.getLoc(), castType, dict, bldCat, cxt.getErrors());

    Continue converter = new Cast(castType, cxt, castSpec);

    Continue combo = new Combo(converter, cxt.getCont());
    return exp.getInner().transform(this, cxt.fork(combo));
  }

  @Override
  public FrameState transformConditionalExp(ConditionalExp cond, CompileContext context)
  {
    // LabelNode thLabel = new LabelNode();
    // LabelNode elLabel = new LabelNode();
    // InsnList ins = context.getIns();
    //
    // ReconcileCont reconcile = new ReconcileCont(context.getCont());
    //
    // Location loc = cond.getLoc();
    // doLineNumber(loc, context);
    //
    // CafeDictionary dict = context.getDict();
    // CafeDictionary thDict = dict.fork();
    //
    // ConditionContext ccxt = ConditionContext.fork(context, Sense.jmpOnFail, elLabel, thLabel);
    // cond.getCnd().transform(new ConditionCompile(), ccxt);
    //
    // Utils.jumpTarget(ins, thLabel);
    //
    // CompileContext thcxt = ccxt.fork(reconcile);
    // cond.getThExp().transform(this, thcxt);
    // dict.migrateFreeVars(thDict);
    // ins.add(elLabel);
    // CafeDictionary elDict = dict.fork();
    // CompileContext elCxt = thcxt.fork(elDict);
    // cond.getElExp().transform(this, elCxt);
    //
    // dict.migrateFreeVars(elDict);
    // thDict.dictUndo();
    // elDict.dictUndo();
    // return reconcile.getSpec();
    return null;
  }

  @Override
  public FrameState transformContentCondition(ContentCondition cond, CompileContext context)
  {
    // LabelNode lf = new LabelNode();
    // LabelNode lx = new LabelNode();
    //
    // ConditionContext ccxt = ConditionContext.fork(context, Sense.jmpOnFail, lf, lx);
    // cond.getCondition().transform(new ConditionCompile(), ccxt);
    //
    // InsnList ins = context.getIns();
    // ins.add(new InsnNode(Opcodes.ICONST_1));
    // ins.add(new JumpInsnNode(Opcodes.GOTO, lx));
    // ins.add(lf);
    // ins.add(new InsnNode(Opcodes.ICONST_0));
    // ins.add(lx);
    //
    // return context.cont(SrcSpec.rawBoolSrc, cond.getLoc());
    return null;
  }

  @Override
  public FrameState transformMemo(MemoExp memo, CompileContext context)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public FrameState transformMethodVariable(MethodVariable var, CompileContext context)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public FrameState transformNullExp(NullExp nil, CompileContext context)
  {
    InsnList ins = context.getIns();
    ins.add(new InsnNode(Opcodes.ACONST_NULL));

    return context.cont(context.getFrame().pushStack(SrcSpec.voidSrc), nil.getLoc());
  }

  @Override
  public FrameState transformFunctionLiteral(FunctionLiteral f, CompileContext context)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public FrameState transformLetTerm(LetTerm let, CompileContext context)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public FrameState transformOverloaded(Overloaded over, CompileContext context)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public FrameState transformOverloadedFieldAccess(OverloadedFieldAccess over, CompileContext context)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public FrameState transformOverloadVariable(OverloadedVariable var, CompileContext context)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public FrameState transformPatternAbstraction(PatternAbstraction pattern, CompileContext context)
  {
    // TODO Auto-generated method stub
    return null;
  }

  public FrameState compileFieldAccess(Location loc, IContentExpression route, String field, IType fieldType,
      CompileContext context)
  {
    LabelNode nxLbl = new LabelNode();

    JumpContinue jump = new JumpContinue(nxLbl, context.getFrame(), context);
    CompileContext recCxt = context.fork(jump);
    InsnList ins = context.getIns();

    FrameState recState = route.transform(this, recCxt);
    jump.jumpTarget(ins, recState, recCxt);
    ISpec rec = recState.tos();
    Utils.jumpTarget(ins, nxLbl);

    IType recordType = TypeUtils.deRef(route.getType());
    final ISpec fieldSpec;

    if (recordType instanceof TypeExp || recordType instanceof Type) {
      CafeDictionary dict = context.getDict();
      fieldSpec = dict.getFieldSpec(recordType, field);
      String getter = Types.getterName(dict.fieldJavaName(recordType, field));
      String javaRecordType = dict.javaName(recordType);

      checkCast(context, rec, javaRecordType);

      ins.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, javaRecordType, getter, "()"
          + dict.javaFieldSig(recordType, field)));
    } else
    // We use the generic getMember method
    {

      ins.add(new TypeInsnNode(Opcodes.CHECKCAST, Types.IRECORD));
      ins.add(new LdcInsnNode(field));

      ins.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Types.IRECORD, "getMember", "(" + Types.JAVA_STRING_SIG + ")"
          + Types.IVALUE_SIG));
      fieldSpec = SrcSpec.generic(loc);
    }

    FrameState fState = context.getFrame().pushStack(fieldSpec);

    return context.cont(fState, loc);
  }

  @Override
  public FrameState transformFieldAccess(FieldAccess dot, CompileContext context)
  {
    return compileFieldAccess(dot.getLoc(), dot.getRecord(), dot.getField(), dot.getType(), context);
  }

  @Override
  public FrameState transformRaiseExpression(RaiseExpression exp, CompileContext cxt)
  {
    MethodNode mtd = cxt.getMtd();
    InsnList ins = mtd.instructions;

    String exceptionType = Type.getInternalName(EvaluationException.class);

    FrameState subFrame = cxt.getFrame().pushStack(SrcSpec.constructorSrc).pushStack(SrcSpec.constructorSrc);

    ins.add(new TypeInsnNode(Opcodes.NEW, exceptionType));
    ins.add(new InsnNode(Opcodes.DUP));

    exp.getRaise().transform(this, cxt.fork(subFrame).fork(new NullCont()));

    ins.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, exceptionType, Types.INIT, EVAL_EXCEPT_SIG));
    ins.add(new InsnNode(Opcodes.ATHROW));

    return cxt.cont(cxt.getFrame(), exp.getLoc());
  }

  public static void checkCast(CompileContext cxt, ISpec spec, String javaType)
  {
    if (!spec.getJavaType().equals(javaType)) {
      cxt.getIns().add(new TypeInsnNode(Opcodes.CHECKCAST, javaType));
    }
  }

  @Override
  public FrameState transformReference(Shriek ref, CompileContext context)
  {
    Location loc = ref.getLoc();

    IContentExpression cell = ref.getReference();
    IType argType = cell.getType();

    assert TypeUtils.isReferenceType(argType);

    IType referType = TypeUtils.functionType(argType, TypeUtils.referencedType(argType));
    final IContentExpression refFun;

    if (TypeUtils.isRawBoolType(TypeUtils.referencedType(argType)))
      refFun = new Variable(loc, referType, GetRawBoolRef.name);
    else if (TypeUtils.isRawCharType(TypeUtils.referencedType(argType)))
      refFun = new Variable(loc, referType, GetRawCharRef.name);
    else if (TypeUtils.isRawIntType(TypeUtils.referencedType(argType)))
      refFun = new Variable(loc, referType, GetRawIntegerRef.name);
    else if (TypeUtils.isRawLongType(TypeUtils.referencedType(argType)))
      refFun = new Variable(loc, referType, GetRawLongRef.name);
    else if (TypeUtils.isRawFloatType(TypeUtils.referencedType(argType)))
      refFun = new Variable(loc, referType, GetRawFloatRef.name);
    else
      refFun = new Variable(loc, referType, GetRef.name);

    IContentExpression refApp = Application.apply(loc, TypeUtils.referencedType(argType), refFun, cell);

    return refApp.transform(this, context);
  }

  @Override
  public FrameState transformResolved(Resolved res, CompileContext context)
  {
    Location loc = res.getLoc();
    IContentExpression resCall = Application.apply(loc, res.getType(), res.getOver(), res.getDicts());
    return resCall.transform(this, context);
  }

  @Override
  public FrameState transformScalar(Scalar scalar, CompileContext context)
  {
    IValue val = scalar.getValue();
    IType type = val.getType();
    FrameState frame = context.getFrame();
    try {
      if (type.equals(StandardTypes.rawIntegerType)) {
        int ix = Factory.intValue(val);
        genIntConst(context, frame, ix);
        return context.cont(frame.pushStack(SrcSpec.rawIntSrc), scalar.getLoc());
      } else if (type.equals(StandardTypes.rawLongType)) {
        long lx = Factory.longValue(val);
        genLongConst(context, frame, lx);
        return context.cont(frame.pushStack(SrcSpec.rawLongSrc), scalar.getLoc());
      } else if (type.equals(StandardTypes.rawFloatType)) {
        double dx = Factory.floatValue(val);
        genFloatConst(context, frame, dx);
        return context.cont(frame.pushStack(SrcSpec.rawDblSrc), scalar.getLoc());
      } else if (type.equals(StandardTypes.rawDecimalType)) {
        BigDecimal dx = Factory.decimalValue(val);
        genDecimalConst(context, dx);
        return context.cont(frame.pushStack(SrcSpec.rawDecimalSrc), scalar.getLoc());
      } else if (type.equals(StandardTypes.rawCharType)) {
        int ix = Factory.charValue(val);
        genIntConst(context, frame, ix);
        return context.cont(frame.pushStack(SrcSpec.rawCharSrc), scalar.getLoc());
      } else if (type.equals(StandardTypes.rawStringType)) {
        String str = Factory.stringValue(val);
        context.getIns().add(new LdcInsnNode(str));
        return context.cont(frame.pushStack(SrcSpec.rawStringSrc), scalar.getLoc());
      } else {
        context.reportError("invalid form of scalar", scalar.getLoc());
        InsnList ins = context.getIns();
        ins.add(new InsnNode(Opcodes.ACONST_NULL));

        return context.cont(frame.pushStack(SrcSpec.voidSrc), scalar.getLoc());
      }
    } catch (EvaluationException e) {
      context.reportError(e.getMessage(), scalar.getLoc());
      return context.cont(frame.pushStack(SrcSpec.voidSrc), scalar.getLoc());
    }
  }

  public static FrameState genIntConst(CompileContext cxt, FrameState frame, int ix)
  {
    InsnList ins = cxt.getIns();
    switch (ix) {
    case -1:
      ins.add(new InsnNode(Opcodes.ICONST_M1));
      return frame.pushStack(SrcSpec.rawIntSrc);
    case 0:
      ins.add(new InsnNode(Opcodes.ICONST_0));
      return frame.pushStack(SrcSpec.rawIntSrc);
    case 1:
      ins.add(new InsnNode(Opcodes.ICONST_1));
      return frame.pushStack(SrcSpec.rawIntSrc);
    case 2:
      ins.add(new InsnNode(Opcodes.ICONST_2));
      return frame.pushStack(SrcSpec.rawIntSrc);
    case 3:
      ins.add(new InsnNode(Opcodes.ICONST_3));
      return frame.pushStack(SrcSpec.rawIntSrc);
    case 4:
      ins.add(new InsnNode(Opcodes.ICONST_4));
      return frame.pushStack(SrcSpec.rawIntSrc);
    case 5:
      ins.add(new InsnNode(Opcodes.ICONST_5));
      return frame.pushStack(SrcSpec.rawIntSrc);
    default:
      ins.add(new LdcInsnNode(ix));
      return frame.pushStack(SrcSpec.rawIntSrc);
    }
  }

  public static FrameState genLongConst(CompileContext cxt, FrameState frame, long ix)
  {
    InsnList ins = cxt.getIns();
    if (ix == 0)
      ins.add(new InsnNode(Opcodes.LCONST_0));
    else if (ix == 1)
      ins.add(new InsnNode(Opcodes.LCONST_1));
    else
      ins.add(new LdcInsnNode(ix));

    return frame.pushStack(SrcSpec.rawLngSrc);
  }

  public static FrameState genFloatConst(CompileContext cxt, FrameState frame, double dx)
  {
    InsnList ins = cxt.getIns();
    if (dx == 0.0)
      ins.add(new InsnNode(Opcodes.DCONST_0));
    else if (dx == 1.0)
      ins.add(new InsnNode(Opcodes.DCONST_1));
    else
      ins.add(new LdcInsnNode(dx));

    return frame.pushStack(SrcSpec.rawDblSrc);
  }

  private static final ISpec byteArraySpec = new SrcSpec(TypeUtils.arrayType(StandardTypes.integerType),
      Location.nullLoc, Utils.javaInternalClassName(byte[].class), "L" + Utils.javaInternalClassName(byte[].class)
          + ";", null, null);

  // We have to build a BigDecimal in small pieces
  public static FrameState genDecimalConst(CompileContext cxt, BigDecimal bx)
  {
    InsnList ins = cxt.getIns();
    FrameState frame = cxt.getFrame();
    BigInteger bix = bx.unscaledValue();
    int scale = bx.scale();
    byte[] ixData = bix.toByteArray();

    ins.add(new TypeInsnNode(Opcodes.NEW, Type.getInternalName(BigDecimal.class)));
    ins.add(new InsnNode(Opcodes.DUP));
    ins.add(new TypeInsnNode(Opcodes.NEW, Type.getInternalName(BigInteger.class)));
    ins.add(new InsnNode(Opcodes.DUP));
    FrameState iFrame = genIntConst(cxt, cxt.getFrame(), ixData.length);

    ins.add(new IntInsnNode(Opcodes.NEWARRAY, Opcodes.T_BYTE));
    iFrame = iFrame.pushStack(byteArraySpec).pushStack(byteArraySpec);

    for (int ix = 0; ix < ixData.length; ix++) {
      ins.add(new InsnNode(Opcodes.DUP));
      genIntConst(cxt, frame, ix);
      genIntConst(cxt, frame, ixData[ix]);
      ins.add(new InsnNode(Opcodes.BASTORE));
    }
    ins.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, Type.getInternalName(BigInteger.class), Types.INIT, "([B)V"));
    genIntConst(cxt, frame, scale);
    ins.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, Type.getInternalName(BigDecimal.class), Types.INIT, "("
        + Type.getDescriptor(BigInteger.class) + "I)V"));
    return frame.pushStack(SrcSpec.rawDecimalSrc);
  }

  @Override
  public FrameState transformConstructor(ConstructorTerm tuple, CompileContext context)
  {
    List<IContentExpression> args = tuple.getElements();
    Location loc = tuple.getLoc();
    CafeDictionary dict = context.getDict();
    CodeCatalog bldCat = context.getBldCat();
    ErrorReport errors = context.getErrors();

    ISpec argSpcs[] = new ISpec[args.size()];
    for (int ix = 0; ix < args.size(); ix++) {
      IContentExpression arg = args.get(ix);
      argSpcs[ix] = SrcSpec.typeSpec(arg.getLoc(), arg.getType(), dict, bldCat, errors);
    }

    if (args.size() < Theta.MAX_ARGS)
      compileArgs(args, argSpcs, context);
    else
      argArray(args, argSpcs, context);

    return context.getFrame().pushStack(SrcSpec.typeSpec(loc, tuple.getType(), dict, bldCat, errors));
  }

  public FrameState compileArgs(List<IContentExpression> args, ISpec[] argSpecs, CompileContext cxt)
  {
    FrameState frame = cxt.getFrame();

    assert args.size() < Theta.MAX_ARGS;
    for (int ix = 0; ix < args.size(); ix++) {
      IContentExpression arg = args.get(ix);

      JumpContinue jump = new JumpContinue(new LabelNode(), frame, cxt);
      cxt = cxt.fork(jump, frame);

      FrameState actual = arg.transform(this, cxt);

      jump.jumpTarget(cxt.getIns(), actual, cxt);

      if (argSpecs != null)
        checkCast(cxt, actual.tos(), argSpecs[ix].getJavaType());
    }
    return cxt.getFrame();
  }

  public FrameState argArray(List<IContentExpression> args, ISpec[] argSpecs, CompileContext cxt)
  {
    int arity = args.size();

    InsnList ins = cxt.getIns();
    FrameState frame = cxt.getFrame();

    genIntConst(cxt, frame, arity);
    ins.add(new TypeInsnNode(Opcodes.ANEWARRAY, Types.IVALUE));
    frame = frame.pushStack(SrcSpec.arraySrc);

    for (int ix = 0; ix < arity; ix++) {
      IContentExpression arg = args.get(ix);

      ins.add(new InsnNode(Opcodes.DUP));
      FrameState dup = frame.pushStack(SrcSpec.arraySrc);
      genIntConst(cxt, frame, ix);

      JumpContinue jump = new JumpContinue(new LabelNode(), dup, cxt);
      cxt = cxt.fork(jump, dup);

      dup = arg.transform(this, cxt);

      jump.jumpTarget(cxt.getIns(), dup, cxt);

      ins.add(new InsnNode(Opcodes.AASTORE));
    }
    return frame;
  }

  public FrameState buildTuple(ConstructorTerm con, CompileContext cxt)
  {
    Location loc = con.getLoc();
    List<IContentExpression> args = con.getElements();
    int arity = args.size();
    InsnList ins = cxt.getIns();

    if (arity == 0) {
      ins.add(new FieldInsnNode(Opcodes.GETSTATIC, Utils.javaInternalClassName(NTuple.class), "$0Enum", Utils
          .javaTypeSig(NTpl.class)));
    } else {
      String tupleJavaType = Utils.javaInternalClassName(NTpl.class);

      ISpec argSpecs[] = SrcSpec.typeSpecs(TypeUtils.tupleTypes(con.getType()), cxt.getDict(), cxt.getBldCat(), cxt
          .getRepository(), cxt.getErrors(), loc);

      argArray(args, argSpecs, cxt);

      ins.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, tupleJavaType, Types.INIT, "(" + Types.IVALUE_ARRAY + ")V"));
    }
    FrameState reslt = cxt.getFrame().pushStack(SrcSpec.generalSrc);

    return cxt.cont(reslt, loc);
  }

  public static void checkType(ISpec actual, ISpec expected, CompileContext cxt)
  {
    if (!TypeUtils.isTypeVar(expected.getType()))
      checkCast(cxt, actual, expected.getJavaType());
  }

  @Override
  public FrameState transformValofExp(ValofExp valof, final CompileContext cxt)
  {
    final MethodNode mtd = cxt.getMtd();
    final InsnList ins = mtd.instructions;
    final LabelNode endLbl = new LabelNode();

    Continue valisCont = new Continue() {

      @Override
      public boolean isJump()
      {
        return true;
      }

      @Override
      public FrameState cont(FrameState src, Location loc)
      {
        src = cxt.cont(src, loc);
        if (!cxt.getCont().isJump())
          mtd.instructions.add(new JumpInsnNode(Opcodes.GOTO, endLbl));
        return src;
      }
    };
    CompileContext ccxt = cxt.fork(valisCont);
    FrameState res = valof.getAction().transform(new ActionCompile(), ccxt);

    Utils.jumpTarget(ins, endLbl);
    return res;
  }

  @Override
  public FrameState transformVariable(Variable v, CompileContext cxt)
  {
    String id = v.getName();
    Location loc = v.getLoc();
    VarInfo var = varReference(id, loc, cxt);

    if (var != null) {
      if (!var.isInited())
        cxt.reportError("accessing uninitiliazed variable: " + id + "\ndeclared at " + var.getLoc(), loc);
      else if (var.getWhere() == VarSource.staticMethod)
        cxt.reportError("cannot treat static method " + id + " as a regular value", loc);
      else
        var.loadValue(cxt.getFrame(), cxt);

      return cxt.cont(cxt.getFrame().pushStack(var), loc);
    } else {
      cxt.reportError(id + " not defined", loc);
      return cxt.cont(cxt.getFrame(), loc);
    }
  }

  // Convert a reference to a free reference
  public static VarInfo varReference(String name, Location loc, CompileContext cxt)
  {
    CafeDictionary dict = cxt.getDict();
    VarInfo var = dict.find(name);

    if (var == null && dict.getParent() != null) {
      VarInfo ref = varReference(name, loc, cxt);

      if (ref != null) {
        if (ref.getKind() == JavaKind.builtin || ref.getKind() == JavaKind.constructor)
          return ref;

        if (!ref.isInited())
          cxt.reportError("accessing uninitialized free variable: " + ref + "@" + ref.getLoc(), loc, ref.getLoc());
        return dict.declareFree(ref.getAccess().downGrade(), ref);
      }
    }

    return var;
  }

  @Override
  public FrameState transformVoidExp(VoidExp exp, CompileContext context)
  {
    InsnList ins = context.getIns();
    ins.add(new InsnNode(Opcodes.ACONST_NULL));

    return context.cont(context.getFrame().pushStack(SrcSpec.voidSrc), exp.getLoc());
  }
}
