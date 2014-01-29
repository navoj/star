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

/**
 * 
 * Copyright (C) 2013 Starview Inc
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
