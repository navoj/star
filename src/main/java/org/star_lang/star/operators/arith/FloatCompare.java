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
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.arith.runtime.FloatCompare.FloatEQ;
import org.star_lang.star.operators.arith.runtime.FloatCompare.FloatGE;
import org.star_lang.star.operators.arith.runtime.FloatCompare.FloatGT;
import org.star_lang.star.operators.arith.runtime.FloatCompare.FloatLE;
import org.star_lang.star.operators.arith.runtime.FloatCompare.FloatLT;
import org.star_lang.star.operators.arith.runtime.FloatCompare.FloatNE;

/**
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

public abstract class FloatCompare extends Builtin
{
  private static final IType floatType = StandardTypes.rawFloatType;
  private static final IType type = TypeUtils.functionType(floatType, floatType, StandardTypes.booleanType);

  private FloatCompare(String name, Class<?> implClass)
  {
    super(name, type, implClass);
  }

  public static void declare(Intrinsics cxt)
  {
    String equality = StandardNames.EQUALITY;
    FltEQ eq = new FltEQ();
    cxt.declareBuiltin(eq);
    PrimitiveOverloader.declarePrimitiveImplementation(equality, StandardNames.EQUAL, floatType, eq.getName());

    FltNE ne = new FltNE();
    cxt.declareBuiltin(ne);
    PrimitiveOverloader.declarePrimitiveImplementation(equality, StandardNames.NOT_EQUAL, floatType, ne.getName());

    cxt.declareBuiltin(new FltLE());
    cxt.declareBuiltin(new FltLT());
    cxt.declareBuiltin(new FltGE());
    cxt.declareBuiltin(new FltGT());
  }

  public static class FltEQ extends FloatCompare implements InlinePredicate
  {
    public static final String name = "__float_eq";

    public FltEQ()
    {
      super(name, FloatEQ.class);
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
        ins.add(new InsnNode(Opcodes.DCMPG));
        ins.add(new JumpInsnNode(Opcodes.IFEQ, fail));
      } else {
        ins.add(new InsnNode(Opcodes.DCMPG));
        ins.add(new JumpInsnNode(Opcodes.IFNE, fail));
      }
    }
  }

  public static class FltNE extends FloatCompare implements InlinePredicate
  {
    public static final String name = "__float_ne";

    public FltNE()
    {
      super(name, FloatNE.class);
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
        ins.add(new InsnNode(Opcodes.DCMPG));
        ins.add(new JumpInsnNode(Opcodes.IFEQ, fail));
      } else {
        ins.add(new InsnNode(Opcodes.DCMPL));
        ins.add(new JumpInsnNode(Opcodes.IFNE, fail));
      }
    }
  }

  public static class FltLE extends FloatCompare implements InlinePredicate
  {
    public static final String name = "__float_le";

    public FltLE()
    {
      super(name, FloatLE.class);
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
        ins.add(new InsnNode(Opcodes.DCMPG));
        ins.add(new JumpInsnNode(Opcodes.IFLE, fail));
      } else {
        ins.add(new InsnNode(Opcodes.DCMPL));
        ins.add(new JumpInsnNode(Opcodes.IFGT, fail));
      }
    }
  }

  public static class FltLT extends FloatCompare implements InlinePredicate
  {
    public static final String name = "__float_lt";

    public FltLT()
    {
      super(name, FloatLT.class);
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
        ins.add(new InsnNode(Opcodes.DCMPG));
        ins.add(new JumpInsnNode(Opcodes.IFLT, fail));
      } else {
        ins.add(new InsnNode(Opcodes.DCMPL));
        ins.add(new JumpInsnNode(Opcodes.IFGE, fail));
      }
    }
  }

  public static class FltGT extends FloatCompare implements InlinePredicate
  {
    public static final String name = "__float_gt";

    public FltGT()
    {
      super(name, FloatGT.class);
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
        ins.add(new InsnNode(Opcodes.DCMPG));
        ins.add(new JumpInsnNode(Opcodes.IFGT, fail));
      } else {
        ins.add(new InsnNode(Opcodes.DCMPL));
        ins.add(new JumpInsnNode(Opcodes.IFLE, fail));
      }
    }
  }

  public static class FltGE extends FloatCompare implements InlinePredicate
  {
    public static final String name = "__float_ge";

    public FltGE()
    {
      super(name, FloatGE.class);
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
        ins.add(new InsnNode(Opcodes.DCMPG));
        ins.add(new JumpInsnNode(Opcodes.IFGE, fail));
      } else {
        ins.add(new InsnNode(Opcodes.DCMPL));
        ins.add(new JumpInsnNode(Opcodes.IFLE, fail));
      }
    }
  }

}
