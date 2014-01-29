package org.star_lang.star.operators.arith;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.star_lang.star.compiler.cafe.compile.HWM;
import org.star_lang.star.compiler.cafe.compile.Inliner;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.transform.PrimitiveOverloader;
import org.star_lang.star.compiler.type.TypeUtils;
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

import com.starview.platform.data.type.IType;
import com.starview.platform.data.type.Location;
import com.starview.platform.data.type.StandardTypes;

/**
 * Binary arithmetic functions
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
