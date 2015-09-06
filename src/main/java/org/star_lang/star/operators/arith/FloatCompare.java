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

/*
 * Copyright (c) 2015. Francis G. McCabe
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
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
