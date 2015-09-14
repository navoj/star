package org.star_lang.star.compiler.cafe.compile.cont;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.star_lang.star.compiler.cafe.compile.CafeDictionary;
import org.star_lang.star.compiler.cafe.compile.CodeContext;
import org.star_lang.star.compiler.cafe.compile.ISpec;
import org.star_lang.star.data.type.Location;

public class SingleCont implements IContinuation
{
  private final AbstractInsnNode ins;
  private final boolean jump;
  private boolean used = false;

  public SingleCont(AbstractInsnNode ins, boolean jump)
  {
    this.ins = ins;
    this.jump = jump;
  }

  @Override
  public ISpec cont(ISpec src, CafeDictionary cxt, Location loc, CodeContext ccxt)
  {
    assert !used : "this continuation has already been used";
    used = true;
    MethodNode mtd = ccxt.getMtd();
    mtd.instructions.add(ins);
    return src;
  }

  @Override
  public boolean isJump()
  {
    return jump;
  }
}
