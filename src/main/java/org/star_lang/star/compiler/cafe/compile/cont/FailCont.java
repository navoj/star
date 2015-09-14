package org.star_lang.star.compiler.cafe.compile.cont;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.star_lang.star.compiler.cafe.compile.CafeDictionary;
import org.star_lang.star.compiler.cafe.compile.CodeContext;
import org.star_lang.star.compiler.cafe.compile.HWM;
import org.star_lang.star.compiler.cafe.compile.ISpec;
import org.star_lang.star.data.type.Location;

public final class FailCont implements IContinuation
{
  @Override
  public boolean isJump()
  {
    return true;
  }

  @Override
  public ISpec cont(ISpec src, CafeDictionary cxt, Location loc, CodeContext ccxt)
  {
    MethodNode mtd = ccxt.getMtd();
    HWM hwm = ccxt.getMtdHwm();
    InsnList ins = mtd.instructions;
    ins.add(new InsnNode(Opcodes.ACONST_NULL));
    hwm.probe(1);
    ins.add(new InsnNode(Opcodes.ARETURN));
    return null;
  }
}