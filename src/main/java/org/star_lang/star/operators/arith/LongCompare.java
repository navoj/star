package org.star_lang.star.operators.arith;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.star_lang.star.compiler.cafe.compile.HWM;
import org.star_lang.star.compiler.cafe.compile.InlinePredicate;
import org.star_lang.star.compiler.cafe.compile.Sense;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.transform.PrimitiveOverloader;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.arith.runtime.LongCompare.LongEQ;
import org.star_lang.star.operators.arith.runtime.LongCompare.LongGE;
import org.star_lang.star.operators.arith.runtime.LongCompare.LongGT;
import org.star_lang.star.operators.arith.runtime.LongCompare.LongLE;
import org.star_lang.star.operators.arith.runtime.LongCompare.LongLT;
import org.star_lang.star.operators.arith.runtime.LongCompare.LongNE;

import com.starview.platform.data.type.IType;
import com.starview.platform.data.type.StandardTypes;

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

public abstract class LongCompare extends Builtin
{
  private static final IType longType = StandardTypes.rawLongType;
  private static final IType type = TypeUtils.functionType(longType, longType, StandardTypes.booleanType);

  public static void declare(Intrinsics cxt)
  {
    String equality = StandardNames.EQUALITY;
    LngEQ eq = new LngEQ();
    cxt.declareBuiltin(eq);
    PrimitiveOverloader.declarePrimitiveImplementation(equality, StandardNames.EQUAL, longType, eq.getName());

    LngNE ne = new LngNE();
    cxt.declareBuiltin(ne);
    PrimitiveOverloader.declarePrimitiveImplementation(equality, StandardNames.NOT_EQUAL, longType, ne.getName());

    cxt.declareBuiltin(new LngLE());
    cxt.declareBuiltin(new LngLT());
    cxt.declareBuiltin(new LngGE());
    cxt.declareBuiltin(new LngGT());
  }

  private LongCompare(String name, Class<?> implClass)
  {
    super(name, type, implClass);
  }

  public static class LngEQ extends LongCompare implements InlinePredicate
  {
    public static final String name = "__long_eq";

    public LngEQ()
    {
      super(name, LongEQ.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM)
    {
    }

    @Override
    public void inline(MethodNode mtd, HWM stackHWM, Sense sense, LabelNode fail)
    {
      InsnList ins = mtd.instructions;

      if (sense == Sense.jmpOnOk) {
        ins.add(new InsnNode(Opcodes.LCMP));
        ins.add(new JumpInsnNode(Opcodes.IFEQ, fail));
      } else {
        ins.add(new InsnNode(Opcodes.LCMP));
        ins.add(new JumpInsnNode(Opcodes.IFNE, fail));
      }
    }
  }

  public static class LngNE extends LongCompare implements InlinePredicate
  {
    public static final String name = "__long_ne";

    public LngNE()
    {
      super(name, LongNE.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM)
    {
    }

    @Override
    public void inline(MethodNode mtd, HWM stackHWM, Sense sense, LabelNode fail)
    {
      InsnList ins = mtd.instructions;

      if (sense == Sense.jmpOnOk) {
        ins.add(new InsnNode(Opcodes.LCMP));
        ins.add(new JumpInsnNode(Opcodes.IFNE, fail));
      } else {
        ins.add(new InsnNode(Opcodes.LCMP));
        ins.add(new JumpInsnNode(Opcodes.IFEQ, fail));
      }
    }
  }

  public static class LngLE extends LongCompare implements InlinePredicate
  {
    public static final String name = "__long_le";

    public LngLE()
    {
      super(name, LongLE.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM)
    {
    }

    @Override
    public void inline(MethodNode mtd, HWM stackHWM, Sense sense, LabelNode fail)
    {
      InsnList ins = mtd.instructions;

      if (sense == Sense.jmpOnOk) {
        ins.add(new InsnNode(Opcodes.LCMP));
        ins.add(new JumpInsnNode(Opcodes.IFLE, fail));
      } else {
        ins.add(new InsnNode(Opcodes.LCMP));
        ins.add(new JumpInsnNode(Opcodes.IFGT, fail));
      }
    }
  }

  public static class LngLT extends LongCompare implements InlinePredicate
  {
    public static final String name = "__long_lt";

    public LngLT()
    {
      super(name, LongLT.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM)
    {
    }

    @Override
    public void inline(MethodNode mtd, HWM stackHWM, Sense sense, LabelNode fail)
    {
      InsnList ins = mtd.instructions;

      if (sense == Sense.jmpOnOk) {
        ins.add(new InsnNode(Opcodes.LCMP));
        ins.add(new JumpInsnNode(Opcodes.IFLT, fail));
      } else {
        ins.add(new InsnNode(Opcodes.LCMP));
        ins.add(new JumpInsnNode(Opcodes.IFGE, fail));
      }
    }
  }

  public static class LngGT extends LongCompare implements InlinePredicate
  {
    public static final String name = "__long_gt";

    public LngGT()
    {
      super(name, LongGT.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM)
    {
    }

    @Override
    public void inline(MethodNode mtd, HWM stackHWM, Sense sense, LabelNode fail)
    {
      InsnList ins = mtd.instructions;

      if (sense == Sense.jmpOnOk) {
        ins.add(new InsnNode(Opcodes.LCMP));
        ins.add(new JumpInsnNode(Opcodes.IFGT, fail));
      } else {
        ins.add(new InsnNode(Opcodes.LCMP));
        ins.add(new JumpInsnNode(Opcodes.IFLE, fail));
      }
    }
  }

  public static class LngGE extends LongCompare implements InlinePredicate
  {
    public static final String name = "__long_ge";

    public LngGE()
    {
      super(name, LongGE.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM)
    {
    }

    @Override
    public void inline(MethodNode mtd, HWM stackHWM, Sense sense, LabelNode fail)
    {
      InsnList ins = mtd.instructions;

      if (sense == Sense.jmpOnOk) {
        ins.add(new InsnNode(Opcodes.LCMP));
        ins.add(new JumpInsnNode(Opcodes.IFGE, fail));
      } else {
        ins.add(new InsnNode(Opcodes.LCMP));
        ins.add(new JumpInsnNode(Opcodes.IFLT, fail));
      }
    }
  }
}
