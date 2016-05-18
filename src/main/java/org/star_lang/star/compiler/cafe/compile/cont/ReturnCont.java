package org.star_lang.star.compiler.cafe.compile.cont;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.star_lang.star.compiler.cafe.compile.*;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;

public class ReturnCont implements IContinuation
{
  private final IType tipe;
  private final ISpec spec;
  private final CafeDictionary dict;

  public ReturnCont(IType tipe, ISpec spec, CafeDictionary dict)
  {
    this.tipe = tipe;
    this.spec = spec;
    this.dict = dict;
  }

  @Override
  public ISpec cont(ISpec src, CafeDictionary cxt, Location loc, CodeContext ccxt)
  {
    MethodNode mtd = ccxt.getMtd();
    HWM hwm = ccxt.getMtdHwm();

    InsnList ins = mtd.instructions;

    Expressions.checkType(src, spec, mtd, dict, hwm);

    switch (Types.varType(tipe)) {
    case rawBool:
      ins.add(new InsnNode(Opcodes.IRETURN));
      break;
    case rawInt:
      ins.add(new InsnNode(Opcodes.IRETURN));
      break;
    case rawLong:
      ins.add(new InsnNode(Opcodes.LRETURN));
      break;
    case rawFloat:
      ins.add(new InsnNode(Opcodes.DRETURN));
      break;
    case rawBinary:
    case rawString:
    case rawDecimal:
    case general:
      ins.add(new InsnNode(Opcodes.ARETURN));
      break;
    default:
      assert false : "invalid kind of variable";
    }
    return SrcSpec.prcSrc;
  }

  @Override
  public boolean isJump()
  {
    return true;
  }
}
