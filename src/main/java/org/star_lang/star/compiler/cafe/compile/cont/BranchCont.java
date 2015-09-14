package org.star_lang.star.compiler.cafe.compile.cont;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.star_lang.star.compiler.cafe.compile.CafeDictionary;
import org.star_lang.star.compiler.cafe.compile.CodeContext;
import org.star_lang.star.compiler.cafe.compile.Expressions;
import org.star_lang.star.compiler.cafe.compile.HWM;
import org.star_lang.star.compiler.cafe.compile.ISpec;
import org.star_lang.star.compiler.cafe.compile.Sense;
import org.star_lang.star.compiler.cafe.compile.SrcSpec;
import org.star_lang.star.data.type.Location;

public class BranchCont implements IContinuation
{
  private final Sense sense;
  private final LabelNode elLabel;
  private final CafeDictionary dict;

  public BranchCont(Sense sense, LabelNode label, CafeDictionary dict)
  {
    this.elLabel = label;
    this.sense = sense;
    this.dict = dict;
  }

  @Override
  public ISpec cont(ISpec src, CafeDictionary cxt, Location loc, CodeContext ccxt)
  {
    MethodNode mtd = ccxt.getMtd();
    HWM hwm = ccxt.getMtdHwm();

    Expressions.checkType(src, SrcSpec.rawBoolSrc, mtd, dict, hwm);
    mtd.instructions.add(new JumpInsnNode(sense == Sense.jmpOnOk ? Opcodes.IFNE : Opcodes.IFEQ, elLabel));

    return SrcSpec.prcSrc;
  }

  @Override
  public boolean isJump()
  {
    return false;
  }

}