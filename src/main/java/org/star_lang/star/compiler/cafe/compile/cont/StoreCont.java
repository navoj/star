package org.star_lang.star.compiler.cafe.compile.cont;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.star_lang.star.compiler.cafe.Names;
import org.star_lang.star.compiler.cafe.compile.*;
import org.star_lang.star.data.type.Location;

public class StoreCont implements IContinuation
{
  private final VarInfo var;
  private final CafeDictionary dict;

  public StoreCont(VarInfo var, CafeDictionary dict)
  {
    this.var = var;
    this.dict = dict;
  }

  @Override
  public ISpec cont(ISpec src, CafeDictionary cxt, Location loc, CodeContext ccxt)
  {
    MethodNode mtd = ccxt.getMtd();
    HWM hwm = ccxt.getMtdHwm();

    InsnList ins = mtd.instructions;
    Expressions.checkType(src, var, mtd, dict, hwm);

    switch (var.getWhere()) {
    case freeVar:
      ins.add(new VarInsnNode(Opcodes.ALOAD, dict.find(Names.PRIVATE_THIS).getOffset()));
      ins.add(new InsnNode(Opcodes.SWAP));
      ins.add(new FieldInsnNode(Opcodes.PUTFIELD, dict.getOwnerName(), var.getJavaSafeName(), var.getJavaSig()));
      break;
    case field: {
      VarInfo recordVar = var.getRecord();
      recordVar.loadValue(mtd, hwm, cxt);
      ins.add(new InsnNode(Opcodes.SWAP));
      ins.add(new FieldInsnNode(Opcodes.PUTFIELD, recordVar.getJavaType(), var.getJavaSafeName(), var.getJavaSig()));
      break;
    }
    case arrayArg: {
      var.getRecord().loadValue(mtd, hwm, cxt);
      ins.add(new InsnNode(Opcodes.SWAP));
      ins.add(new InsnNode(Opcodes.AASTORE));
      break;
    }
    case localVar: {
      int offset = var.getOffset();

      switch (var.getKind()) {
      case rawBool:
        ins.add(new VarInsnNode(Opcodes.ISTORE, offset));
        break;
      case rawInt:
        ins.add(new VarInsnNode(Opcodes.ISTORE, offset));
        break;
      case rawLong:
        ins.add(new VarInsnNode(Opcodes.LSTORE, offset));
        break;
      case rawFloat:
        ins.add(new VarInsnNode(Opcodes.DSTORE, offset));
        break;
      case rawBinary:
      case rawString:
      case general:
        ins.add(new VarInsnNode(Opcodes.ASTORE, offset));
        break;
      default:
        assert false : "invalid kind of variable";
      }
      break;
    }
    case staticField:
      ins.add(new FieldInsnNode(Opcodes.PUTSTATIC, dict.getOwnerName(), var.getJavaSafeName(), var.getJavaSig()));
      break;
    default:
      ccxt.getErrors().reportError("(internal) invalid target for store of " + src, loc);
    }

    return SrcSpec.prcSrc;
  }

  @Override
  public boolean isJump()
  {
    return false;
  }
}
