package org.star_lang.star.operators.arith;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.star_lang.star.compiler.cafe.compile.HWM;
import org.star_lang.star.compiler.cafe.compile.Inliner;
import org.star_lang.star.compiler.operator.StandardNames;
import org.star_lang.star.compiler.transform.PrimitiveOverloader;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.arith.runtime.FloatBinary.Bits2Float;
import org.star_lang.star.operators.arith.runtime.FloatBinary.Float2Bits;
import org.star_lang.star.operators.arith.runtime.FloatBinary.FloatDivide;
import org.star_lang.star.operators.arith.runtime.FloatBinary.FloatMax;
import org.star_lang.star.operators.arith.runtime.FloatBinary.FloatMin;
import org.star_lang.star.operators.arith.runtime.FloatBinary.FloatMinus;
import org.star_lang.star.operators.arith.runtime.FloatBinary.FloatPlus;
import org.star_lang.star.operators.arith.runtime.FloatBinary.FloatPwr;
import org.star_lang.star.operators.arith.runtime.FloatBinary.FloatRemainder;
import org.star_lang.star.operators.arith.runtime.FloatBinary.FloatTimes;

/**
 * Binary arithmetic functions
 *
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

public abstract class FloatBinary extends Builtin
{
  private final static IType floatType = StandardTypes.rawFloatType;
  private final static IType type = TypeUtils.functionType(floatType, floatType, floatType);

  private FloatBinary(String name, Class<?> implClass)
  {
    super(name, type, implClass);
  }

  public static void declare(Intrinsics cxt)
  {
    cxt.declareBuiltin(new FltPlus());
    PrimitiveOverloader.declarePrimitiveImplementation(StandardNames.ARITHMETIC, StandardNames.PLUS, floatType,
        FltPlus.name);
    PrimitiveOverloader.declarePrimitiveImplementation(StandardNames.ARITHMETIC, StandardNames.TIMES, floatType,
        FltTimes.name);
    PrimitiveOverloader.declarePrimitiveImplementation(StandardNames.ARITHMETIC, StandardNames.MINUS, floatType,
        FltMinus.name);
    PrimitiveOverloader.declarePrimitiveImplementation(StandardNames.ARITHMETIC, StandardNames.DIVIDE, floatType,
        FltDivide.name);
    PrimitiveOverloader.declarePrimitiveImplementation(StandardNames.ARITHMETIC, StandardNames.PCENT, floatType,
        FltRemainder.name);

    cxt.declareBuiltin(new FltMinus());
    cxt.declareBuiltin(new FltTimes());
    cxt.declareBuiltin(new FltDivide());
    cxt.declareBuiltin(new FltRemainder());
    cxt.declareBuiltin(new FltPwr());
    cxt.declareBuiltin(new Builtin(FloatMin.name, FloatMin.type(), FloatMin.class));
    cxt.declareBuiltin(new Builtin(FloatMax.name, FloatMax.type(), FloatMax.class));
    cxt.declareBuiltin(new Builtin(Float2Bits.name, Float2Bits.type(), Float2Bits.class));
    cxt.declareBuiltin(new Builtin(Bits2Float.name, Bits2Float.type(), Bits2Float.class));
  }

  public static class FltPlus extends FloatBinary implements Inliner
  {
    public static final String name = "__float_plus";

    public FltPlus()
    {
      super(name, FloatPlus.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM)
    {
    }

    @Override
    public void inline(ClassNode klass, MethodNode mtd, HWM hwm, Location loc)
    {
      InsnList ins = mtd.instructions;
      ins.add(new InsnNode(Opcodes.DADD));
      hwm.bump(-2);
    }
  }

  public static class FltMinus extends FloatBinary implements Inliner
  {
    public static final String name = "__float_minus";

    public FltMinus()
    {
      super(name, FloatMinus.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM)
    {
    }

    @Override
    public void inline(ClassNode klass, MethodNode mtd, HWM hwm, Location loc)
    {
      InsnList ins = mtd.instructions;
      ins.add(new InsnNode(Opcodes.DSUB));
    }
  }

  public static class FltTimes extends FloatBinary implements Inliner
  {
    public static final String name = "__float_times";

    public FltTimes()
    {
      super(name, FloatTimes.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM)
    {
    }

    @Override
    public void inline(ClassNode klass, MethodNode mtd, HWM hwm, Location loc)
    {
      InsnList ins = mtd.instructions;
      ins.add(new InsnNode(Opcodes.DMUL));
    }
  }

  public static class FltDivide extends FloatBinary implements Inliner
  {
    public static final String name = "__float_divide";

    public FltDivide()
    {
      super(name, FloatDivide.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM)
    {
    }

    @Override
    public void inline(ClassNode klass, MethodNode mtd, HWM hwm, Location loc)
    {
      InsnList ins = mtd.instructions;
      ins.add(new InsnNode(Opcodes.DDIV));
    }
  }

  public static class FltRemainder extends FloatBinary implements Inliner
  {
    public static final String name = "__float_rem";

    public FltRemainder()
    {
      super(name, FloatRemainder.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM)
    {
    }

    @Override
    public void inline(ClassNode klass, MethodNode mtd, HWM hwm, Location loc)
    {
      InsnList ins = mtd.instructions;
      ins.add(new InsnNode(Opcodes.DREM));
    }
  }

  public static class FltPwr extends FloatBinary
  {
    public static final String name = "__float_power";

    public FltPwr()
    {
      super(name, FloatPwr.class);
    }
  }
}
