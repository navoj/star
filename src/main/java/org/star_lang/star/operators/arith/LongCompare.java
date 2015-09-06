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
import org.star_lang.star.operators.arith.runtime.LongCompare.LongEQ;
import org.star_lang.star.operators.arith.runtime.LongCompare.LongGE;
import org.star_lang.star.operators.arith.runtime.LongCompare.LongGT;
import org.star_lang.star.operators.arith.runtime.LongCompare.LongLE;
import org.star_lang.star.operators.arith.runtime.LongCompare.LongLT;
import org.star_lang.star.operators.arith.runtime.LongCompare.LongNE;

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
