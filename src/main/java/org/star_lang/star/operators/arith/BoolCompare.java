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
import org.star_lang.star.operators.arith.runtime.BoolCompare.BoolEQ;
import org.star_lang.star.operators.arith.runtime.BoolCompare.BoolGE;
import org.star_lang.star.operators.arith.runtime.BoolCompare.BoolGT;
import org.star_lang.star.operators.arith.runtime.BoolCompare.BoolLE;
import org.star_lang.star.operators.arith.runtime.BoolCompare.BoolLT;
import org.star_lang.star.operators.arith.runtime.BoolCompare.BoolNE;

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
public abstract class BoolCompare extends Builtin
{
  private static final IType rawBoolType = StandardTypes.rawBoolType;
  private static final IType type = TypeUtils.functionType(rawBoolType, rawBoolType, StandardTypes.booleanType);

  public static void declare(Intrinsics cxt)
  {
    String equality = StandardNames.EQUALITY;
    BooleanEQ eq = new BooleanEQ();
    cxt.declareBuiltin(eq);
    PrimitiveOverloader.declarePrimitiveImplementation(equality, StandardNames.EQUAL, rawBoolType, eq.getName());

    BooleanNE ne = new BooleanNE();
    cxt.declareBuiltin(ne);
    PrimitiveOverloader.declarePrimitiveImplementation(equality, StandardNames.NOT_EQUAL, rawBoolType, ne.getName());

    cxt.declareBuiltin(new BooleanLE());
    cxt.declareBuiltin(new BooleanLT());
    cxt.declareBuiltin(new BooleanGE());
    cxt.declareBuiltin(new BooleanGT());
  }

  private BoolCompare(String name, Class<?> implClass)
  {
    super(name, type, implClass);
  }

  public static class BooleanEQ extends BoolCompare implements InlinePredicate
  {
    public static final String name = "__bool_eq";

    public BooleanEQ()
    {
      super(name, BoolEQ.class);
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

  public static class BooleanNE extends BoolCompare implements InlinePredicate
  {
    public static final String name = "__bool_ne";

    public BooleanNE()
    {
      super(name, BoolNE.class);
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

  public static class BooleanLE extends BoolCompare implements InlinePredicate
  {
    public static final String name = "__bool_le";

    public BooleanLE()
    {
      super(name, BoolLE.class);
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

  public static class BooleanLT extends BoolCompare implements InlinePredicate
  {
    public static final String name = "__bool_lt";

    public BooleanLT()
    {
      super(name, BoolLT.class);
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

  public static class BooleanGT extends BoolCompare implements InlinePredicate
  {
    public static final String name = "__bool_gt";

    public BooleanGT()
    {
      super(name, BoolGT.class);
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

  public static class BooleanGE extends BoolCompare implements InlinePredicate
  {
    public static final String name = "__bool_ge";

    public BooleanGE()
    {
      super(name, BoolGE.class);
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
