package org.star_lang.star.operators.misc;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.star_lang.star.compiler.cafe.compile.HWM;
import org.star_lang.star.compiler.cafe.compile.InlinePredicate;
import org.star_lang.star.compiler.cafe.compile.Sense;
import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.misc.runtime.HashCode;
import org.star_lang.star.operators.misc.runtime.MiscOps.Id;
import org.star_lang.star.operators.misc.runtime.MiscOps.IsNull;

public class MiscOps
{
  public static void declare(Intrinsics cxt)
  {
    cxt.declareBuiltin(new Builtin(HashCode.name, HashCode.type(), HashCode.class));
    cxt.declareBuiltin(new IsNil());
    cxt.declareBuiltin(new Builtin(Id.name, Id.funType(), Id.class));
  }

  public static class IsNil extends Builtin implements InlinePredicate
  {
    public IsNil()
    {
      super(IsNull.name, IsNull.type(), IsNull.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM)
    {
    }

    @Override
    public void inline(MethodNode mtd, HWM stackHWM, Sense sense, LabelNode lbl)
    {
      InsnList ins = mtd.instructions;
      switch (sense) {
      case jmpOnFail:
        ins.add(new JumpInsnNode(Opcodes.IFNONNULL, lbl));
        break;
      case jmpOnOk:
        ins.add(new JumpInsnNode(Opcodes.IFNULL, lbl));
        break;
      }
    }
  }
}
