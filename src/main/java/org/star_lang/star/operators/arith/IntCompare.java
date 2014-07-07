package org.star_lang.star.operators.arith;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
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
import org.star_lang.star.operators.arith.runtime.IntCompare.IntEQ;
import org.star_lang.star.operators.arith.runtime.IntCompare.IntGE;
import org.star_lang.star.operators.arith.runtime.IntCompare.IntGT;
import org.star_lang.star.operators.arith.runtime.IntCompare.IntLE;
import org.star_lang.star.operators.arith.runtime.IntCompare.IntLT;
import org.star_lang.star.operators.arith.runtime.IntCompare.IntNE;
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

public abstract class IntCompare extends Builtin
{
  public static final String INTEGER_NE = "__integer_ne";
  public static final String INTEGER_LT = "__integer_lt";
  public static final String INTEGER_GT = "__integer_gt";
  private static final IType type = TypeUtils.functionType(IntLE.rawIntegerType, IntLE.rawIntegerType,
      StandardTypes.booleanType);

  private IntCompare(String name, Class<?> implClass)
  {
    super(name, type, implClass);
  }

  public static void declare(Intrinsics cxt)
  {
    String equality = StandardNames.EQUALITY;
    IntgrEQ eq = new IntgrEQ();
    cxt.declareBuiltin(eq);
    PrimitiveOverloader.declarePrimitiveImplementation(equality, StandardNames.EQUAL, IntLE.rawIntegerType, eq.getName());

    IntgrNE ne = new IntgrNE();
    cxt.declareBuiltin(ne);
    PrimitiveOverloader.declarePrimitiveImplementation(equality, StandardNames.NOT_EQUAL, IntLE.rawIntegerType, ne.getName());

    cxt.declareBuiltin(new IntgrLE());
    cxt.declareBuiltin(new IntgrLT());
    cxt.declareBuiltin(new IntgrGE());
    cxt.declareBuiltin(new IntgrGT());
  }

  public static class IntgrEQ extends IntCompare implements InlinePredicate
  {
    public IntgrEQ()
    {
      super(IntEQ.name, IntEQ.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM hwm)
    {
    }

    @Override
    public void inline(MethodNode mtd, HWM hwm, Sense sense, LabelNode fail)
    {
      InsnList ins = mtd.instructions;

      if (sense == Sense.jmpOnOk) {
        ins.add(new JumpInsnNode(Opcodes.IF_ICMPEQ, fail));
      } else {
        ins.add(new JumpInsnNode(Opcodes.IF_ICMPNE, fail));
      }
    }
  }

  public static class IntgrNE extends IntCompare implements InlinePredicate
  {
    public IntgrNE()
    {
      super(IntCompare.INTEGER_NE, IntNE.class);
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
        ins.add(new JumpInsnNode(Opcodes.IF_ICMPNE, fail));
      } else {
        ins.add(new JumpInsnNode(Opcodes.IF_ICMPEQ, fail));
      }
    }
  }

  public static class IntgrLE extends IntCompare implements InlinePredicate
  {
    public IntgrLE()
    {
      super(IntLE.name, IntLE.class);
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
        ins.add(new JumpInsnNode(Opcodes.IF_ICMPLE, fail));
      } else {
        ins.add(new JumpInsnNode(Opcodes.IF_ICMPGT, fail));
      }
    }
  }

  public static class IntgrLT extends IntCompare implements InlinePredicate
  {
    public IntgrLT()
    {
      super(IntCompare.INTEGER_LT, IntLT.class);
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
        ins.add(new JumpInsnNode(Opcodes.IF_ICMPLT, fail));
      } else {
        ins.add(new JumpInsnNode(Opcodes.IF_ICMPGE, fail));
      }
    }
  }

  public static class IntgrGT extends IntCompare implements InlinePredicate
  {
    public IntgrGT()
    {
      super(IntCompare.INTEGER_GT, IntGT.class);
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
        ins.add(new JumpInsnNode(Opcodes.IF_ICMPGT, fail));
      } else {
        ins.add(new JumpInsnNode(Opcodes.IF_ICMPLE, fail));
      }
    }
  }

  public static class IntgrGE extends IntCompare implements InlinePredicate
  {
    public IntgrGE()
    {
      super(IntGE.name, IntGE.class);
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
        ins.add(new JumpInsnNode(Opcodes.IF_ICMPGE, fail));
      } else {
        ins.add(new JumpInsnNode(Opcodes.IF_ICMPLT, fail));
      }
    }
  }
}
