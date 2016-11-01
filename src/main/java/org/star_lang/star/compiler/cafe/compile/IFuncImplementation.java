package org.star_lang.star.compiler.cafe.compile;

import java.lang.reflect.Method;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.star_lang.star.code.repository.CodeCatalog;
import org.star_lang.star.code.repository.CodeRepository;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.ast.AApply;
import org.star_lang.star.compiler.cafe.Names;
import org.star_lang.star.compiler.type.Freshen;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IList;
import org.star_lang.star.data.IPattern;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.operators.Builtins;
import org.star_lang.star.operators.ICafeBuiltin;
import org.star_lang.star.operators.Intrinsics;

public class IFuncImplementation
{
  public static final String IFUNCTION_INVOKE_SIG;

  static {
    String enterSig = null;
    try {
      Method enter = IFunction.class.getDeclaredMethod(Names.ENTERFUNCTION, (new IValue[] {}).getClass());
      enterSig = Type.getMethodDescriptor(enter);
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }
    IFUNCTION_INVOKE_SIG = enterSig;
  }

  public static MethodNode ifunc(Location loc, String javaOwner, String javaMtdSig, CafeDictionary dict,
      IType[] argTypes, IType resType, CodeCatalog bldCat, ErrorReport errors)
  {
    final int IFUNCARGOFFSET = 1;

    try {
      Method enter = IFunction.class.getDeclaredMethod(Names.ENTERFUNCTION, (new IValue[] {}).getClass());
      String enterSig = Type.getMethodDescriptor(enter);
      String isvArraySig = Type.getDescriptor(enter.getParameterTypes()[0]);

      MethodNode funMtd = new MethodNode(Opcodes.ACC_PUBLIC, Names.ENTERFUNCTION, enterSig, null, new String[] {});
      LabelNode first = new LabelNode();
      LabelNode last = new LabelNode();

      InsnList funIns = funMtd.instructions;
      funIns.add(first);
      funMtd.localVariables.add(new LocalVariableNode(Names.PRIVATE_THIS, "L" + javaOwner + ";", null, first, last,
          Theta.THIS_OFFSET));

      funMtd.localVariables.add(new LocalVariableNode("args", isvArraySig, null, first, last, IFUNCARGOFFSET));

      HWM stkDepth = new HWM();

      funIns.add(new VarInsnNode(Opcodes.ALOAD, Theta.THIS_OFFSET));
      stkDepth.bump(1);

      for (int ix = 0; ix < argTypes.length; ix++) {
        funIns.add(new VarInsnNode(Opcodes.ALOAD, IFUNCARGOFFSET));
        stkDepth.bump(2);

        Expressions.genIntConst(funIns, stkDepth, ix);
        funIns.add(new InsnNode(Opcodes.AALOAD));

        IType argType = argTypes[ix];
        AutoBoxing.unboxValue(funMtd, stkDepth, argType);
      }
      funIns.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, javaOwner, Names.ENTER, javaMtdSig));
      stkDepth.bump(1);
      if (resType == null || TypeUtils.isProcedureReturnType(resType)) {
        funIns.add(new InsnNode((Opcodes.ACONST_NULL)));
      } else
        AutoBoxing.boxValue(resType, funIns, dict);
      funIns.add(new InsnNode(Opcodes.ARETURN));
      funIns.add(last);
      funMtd.maxLocals = IFUNCARGOFFSET + 1;
      funMtd.maxStack = stkDepth.getHwm();
      return funMtd;
    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  public static MethodNode ipattern(Location loc, String javaOwner, String javaMtdSig, CafeDictionary dict,
      IType[] argTypes, IType ptnType, CodeCatalog bldCat, ErrorReport errors)
  {
    final int IPTNARGOFFSET = 1;

    try {
      Method enter = IPattern.class.getDeclaredMethod(Names.MATCH, (new IValue[] {}).getClass());
      String enterSig = Type.getMethodDescriptor(enter);
      String isvArgSig = Type.getDescriptor(enter.getParameterTypes()[1]);

      MethodNode ptnMtd = new MethodNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_SYNTHETIC, Names.MATCH, enterSig, null,
          new String[] {});
      LabelNode first = new LabelNode();
      LabelNode last = new LabelNode();

      InsnList funIns = ptnMtd.instructions;
      funIns.add(first);
      ptnMtd.localVariables.add(new LocalVariableNode(Names.PRIVATE_THIS, "L" + javaOwner + ";", null, first, last,
          Theta.THIS_OFFSET));
      ptnMtd.localVariables.add(new LocalVariableNode("ptn", isvArgSig, null, first, last, IPTNARGOFFSET));

      HWM hwm = new HWM();

      funIns.add(new VarInsnNode(Opcodes.ALOAD, Theta.THIS_OFFSET));
      hwm.bump(1);

      funIns.add(new VarInsnNode(Opcodes.ALOAD, IPTNARGOFFSET));
      hwm.bump(1);
      AutoBoxing.unboxValue(ptnMtd, hwm, ptnType);

      funIns.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, javaOwner, Names.MATCH, javaMtdSig));
      funIns.add(new InsnNode(Opcodes.ARETURN));
      funIns.add(last);
      ptnMtd.maxLocals = IPTNARGOFFSET + 1;
      ptnMtd.maxStack = hwm.getHwm();
      return ptnMtd;
    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  /**
   * Call a function using the IFunction interface
   * 
   * @param app
   * @param dict
   * @param outer
   * @param errors
   * @param repository
   *          code repository
   * @param bldCat
   * @param inFunction
   * @param ccxt
   *          TODO
   * @return
   */
  public static IType invokeIfunc(AApply app, CafeDictionary dict, CafeDictionary outer, ErrorReport errors,
                                  CodeRepository repository, CodeCatalog bldCat, String inFunction, CodeContext ccxt)
  {
    String funName = app.getOp();
    VarInfo var = dict.find(funName);
    Location loc = app.getLoc();
    MethodNode mtd = ccxt.getMtd();
    HWM hwm = ccxt.getMtdHwm();
    InsnList ins = mtd.instructions;
    IList args = app.getArgs();
    String isvSig = Type.getDescriptor(IValue.class);

    if (var != null) {
      IType type = var.getType();
      if (TypeUtils.isFunType(type)) {
        ISpec[] argSpecs = SrcSpec.generics(type, dict, bldCat, repository, errors, loc);
        hwm.bump(1); // always leave one entry on the stack
        int mark = hwm.getDepth();

        if (TypeUtils.arityOfFunctionType(type) != app.arity())
          errors.reportError("expecting " + TypeUtils.arityOfFunctionType(type) + " arguments", loc);
        else {
          // preamble to access the appropriate value
          switch (var.getKind()) {
          case builtin: {
            ICafeBuiltin builtin = Intrinsics.getBuiltin(funName);

            ins.add(new FieldInsnNode(Opcodes.GETSTATIC, dict.getOwnerName(), builtin.getJavaName(), builtin
                .getJavaSig()));
            break;
          }
          case general: {
            switch (var.getWhere()) {
            case freeVar:
              ins.add(new VarInsnNode(Opcodes.ALOAD, dict.find(Names.PRIVATE_THIS).getOffset()));
              ins.add(new FieldInsnNode(Opcodes.GETFIELD, dict.getOwnerName(), var.getJavaSafeName(), isvSig));
              break;
            case localVar:
              ins.add(new VarInsnNode(Opcodes.ALOAD, var.getOffset()));
              break;
            case staticField:
              ins.add(new FieldInsnNode(Opcodes.GETSTATIC, var.getJavaOwner(), var.getJavaSafeName(), isvSig));
              break;
            default:
              assert false : "invalid location for variable";
            }
            break;
          }
          case constructor: {
            ins.add(new TypeInsnNode(Opcodes.NEW, var.getJavaType()));
            ins.add(new InsnNode(Opcodes.DUP));
            hwm.bump(1); // space for the duplicate
            break;
          }
          default:
            errors.reportError("invalid kind for : " + funName + ":" + var.getType(), loc);
          }

          // Push in the args
          Expressions.argArray(args, argSpecs, ccxt);

          // actually invoke the entity
          switch (var.getKind()) {
          case builtin: {
            ICafeBuiltin builtin = Intrinsics.getBuiltin(funName);

            Method enterMethod = Builtins.findCafeEnter(funName);
            String funSig = builtin.getJavaInvokeSignature();
            String classSig = builtin.getJavaType();

            ins.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, classSig, enterMethod.getName(), funSig));
            break;
          }
          case general: {
            ins.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Type.getInternalName(IFunction.class), Names.ENTER, var
                .getJavaInvokeSig()));
            break;
          }
          case constructor: {
            String javaSig = Types.javaConstructorSig(var.getType());
            ins.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, var.getJavaType(), Types.INIT, javaSig));
            break;
          }
          default:
          }
        }

        hwm.reset(mark);
        return argSpecs[argSpecs.length - 1].getType();
      } else
        errors.reportError("tried to invoke non-function: " + funName + ":" + type, loc);
    } else
      errors.reportError(funName + " not declared", app.getLoc());

    return StandardTypes.unitType;
  }

  // Map a generic call to a specific call by adding in type cast instructions
  public static MethodNode generic(CafeDictionary outer, VarInfo var, ErrorReport errors, CodeCatalog bldCat)
  {
    IType funType = Freshen.freshenForUse(var.getType());
    IType genFunType = Types.genericFunType(funType);
    String genericSig = Types.javaMethodSig(genFunType);
    MethodNode mtd = new MethodNode(Opcodes.ACC_PUBLIC, Names.ENTERFUNCTION, genericSig, null, new String[] {});
    LabelNode first = new LabelNode();
    LabelNode last = new LabelNode();
    CafeDictionary dict = outer.funDict(outer.getOwner());

    InsnList ins = mtd.instructions;
    ins.add(first);
    List<LocalVariableNode> locals = mtd.localVariables;
    locals.add(new LocalVariableNode(Names.PRIVATE_THIS, var.getJavaSig(), null, first, last, Theta.THIS_OFFSET));

    HWM hwm = new HWM();

    ins.add(new VarInsnNode(Opcodes.ALOAD, Theta.THIS_OFFSET));
    hwm.bump(1);

    IType argTypes[] = TypeUtils.getFunArgTypes(funType);
    for (int ix = 1; ix <= argTypes.length; ix++) {
      IType argType = argTypes[ix - 1];
      String argSig = Types.javaTypeSig(argType, dict);
      String id = "__" + ix;

      switch (Types.varType(argType)) {
      case rawBool:
        locals.add(new LocalVariableNode(id, argSig, null, first, last, ix));
        ins.add(new VarInsnNode(Opcodes.ILOAD, ix));
        hwm.bump(1);
        break;
      case rawInt:
        locals.add(new LocalVariableNode(id, argSig, null, first, last, ix));
        ins.add(new VarInsnNode(Opcodes.ILOAD, ix));
        hwm.bump(1);
        break;
      case rawLong:
        locals.add(new LocalVariableNode(id, argSig, null, first, last, ix));
        ins.add(new VarInsnNode(Opcodes.LLOAD, ix));
        hwm.bump(2);
        break;
      case rawFloat:
        locals.add(new LocalVariableNode(id, argSig, null, first, last, ix));
        ins.add(new VarInsnNode(Opcodes.DLOAD, ix));
        hwm.bump(2);
        break;
      case rawString:
        locals.add(new LocalVariableNode(id, argSig, null, first, last, ix));
        ins.add(new VarInsnNode(Opcodes.ALOAD, ix));
        hwm.bump(1);
        break;
      case rawBinary:
        locals.add(new LocalVariableNode(id, Types.OBJECT_SIG, null, first, last, ix));
        ins.add(new VarInsnNode(Opcodes.ALOAD, ix));
        hwm.bump(1);
        break;
      case general:
        locals.add(new LocalVariableNode(id, Types.IVALUE_SIG, null, first, last, ix));
        ins.add(new VarInsnNode(Opcodes.ALOAD, ix));
        AutoBoxing.unboxValue(mtd, hwm, argType);
        hwm.bump(1);
        break;
      default:
        errors.reportError("cannot handle " + argType, var.getLoc());
      }
      // if (!(argType instanceof TypeVar) && !Types.isRawType(argType))
      // ins.add(new TypeInsnNode(Opcodes.CHECKCAST, argJavaType));
    }
    ins.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, var.getJavaType(), Names.ENTER, var.getJavaInvokeSig()));

    IType resType = TypeUtils.getFunResultType(funType);
    if (!TypeUtils.isProcedureReturnType(resType)) {
      AutoBoxing.boxValue(resType, ins, dict);
      ins.add(new InsnNode(Opcodes.ARETURN));
    } else
      ins.add(new InsnNode(Opcodes.RETURN));

    ins.add(last);
    mtd.maxLocals = argTypes.length + 1;
    mtd.maxStack = hwm.getHwm();
    return mtd;
  }
}
