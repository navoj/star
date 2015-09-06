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
public abstract class IntCompare extends Builtin {
  public static final String INTEGER_NE = "__integer_ne";
  public static final String INTEGER_LT = "__integer_lt";
  public static final String INTEGER_GT = "__integer_gt";
  private static final IType type = TypeUtils.functionType(IntLE.rawIntegerType, IntLE.rawIntegerType,
      StandardTypes.booleanType);

  private IntCompare(String name, Class<?> implClass) {
    super(name, type, implClass);
  }

  public static void declare(Intrinsics cxt) {
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

  public static class IntgrEQ extends IntCompare implements InlinePredicate {
    public IntgrEQ() {
      super(IntEQ.name, IntEQ.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM hwm) {
    }

    @Override
    public void inline(MethodNode mtd, HWM hwm, Sense sense, LabelNode fail) {
      InsnList ins = mtd.instructions;

      if (sense == Sense.jmpOnOk) {
        ins.add(new JumpInsnNode(Opcodes.IF_ICMPEQ, fail));
      } else {
        ins.add(new JumpInsnNode(Opcodes.IF_ICMPNE, fail));
      }
    }
  }

  public static class IntgrNE extends IntCompare implements InlinePredicate {
    public IntgrNE() {
      super(IntCompare.INTEGER_NE, IntNE.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM) {
    }

    @Override
    public void inline(MethodNode mtd, HWM stackHWM, Sense sense, LabelNode fail) {
      InsnList ins = mtd.instructions;

      if (sense == Sense.jmpOnOk) {
        ins.add(new JumpInsnNode(Opcodes.IF_ICMPNE, fail));
      } else {
        ins.add(new JumpInsnNode(Opcodes.IF_ICMPEQ, fail));
      }
    }
  }

  public static class IntgrLE extends IntCompare implements InlinePredicate {
    public IntgrLE() {
      super(IntLE.name, IntLE.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM) {
    }

    @Override
    public void inline(MethodNode mtd, HWM stackHWM, Sense sense, LabelNode fail) {
      InsnList ins = mtd.instructions;

      if (sense == Sense.jmpOnOk) {
        ins.add(new JumpInsnNode(Opcodes.IF_ICMPLE, fail));
      } else {
        ins.add(new JumpInsnNode(Opcodes.IF_ICMPGT, fail));
      }
    }
  }

  public static class IntgrLT extends IntCompare implements InlinePredicate {
    public IntgrLT() {
      super(IntCompare.INTEGER_LT, IntLT.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM) {
    }

    @Override
    public void inline(MethodNode mtd, HWM stackHWM, Sense sense, LabelNode fail) {
      InsnList ins = mtd.instructions;

      if (sense == Sense.jmpOnOk) {
        ins.add(new JumpInsnNode(Opcodes.IF_ICMPLT, fail));
      } else {
        ins.add(new JumpInsnNode(Opcodes.IF_ICMPGE, fail));
      }
    }
  }

  public static class IntgrGT extends IntCompare implements InlinePredicate {
    public IntgrGT() {
      super(IntCompare.INTEGER_GT, IntGT.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM) {
    }

    @Override
    public void inline(MethodNode mtd, HWM stackHWM, Sense sense, LabelNode fail) {
      InsnList ins = mtd.instructions;

      if (sense == Sense.jmpOnOk) {
        ins.add(new JumpInsnNode(Opcodes.IF_ICMPGT, fail));
      } else {
        ins.add(new JumpInsnNode(Opcodes.IF_ICMPLE, fail));
      }
    }
  }

  public static class IntgrGE extends IntCompare implements InlinePredicate {
    public IntgrGE() {
      super(IntGE.name, IntGE.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM) {
    }

    @Override
    public void inline(MethodNode mtd, HWM stackHWM, Sense sense, LabelNode fail) {
      InsnList ins = mtd.instructions;

      if (sense == Sense.jmpOnOk) {
        ins.add(new JumpInsnNode(Opcodes.IF_ICMPGE, fail));
      } else {
        ins.add(new JumpInsnNode(Opcodes.IF_ICMPLT, fail));
      }
    }
  }
}
