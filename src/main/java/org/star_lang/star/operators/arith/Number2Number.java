package org.star_lang.star.operators.arith;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.star_lang.star.compiler.cafe.compile.HWM;
import org.star_lang.star.compiler.cafe.compile.Inliner;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.arith.runtime.Number2Number.Decimal2Float;
import org.star_lang.star.operators.arith.runtime.Number2Number.Decimal2Integer;
import org.star_lang.star.operators.arith.runtime.Number2Number.Decimal2Long;
import org.star_lang.star.operators.arith.runtime.Number2Number.Float2Decimal;
import org.star_lang.star.operators.arith.runtime.Number2Number.Float2Integer;
import org.star_lang.star.operators.arith.runtime.Number2Number.Float2Long;
import org.star_lang.star.operators.arith.runtime.Number2Number.Integer2Decimal;
import org.star_lang.star.operators.arith.runtime.Number2Number.Integer2Float;
import org.star_lang.star.operators.arith.runtime.Number2Number.Integer2Integer;
import org.star_lang.star.operators.arith.runtime.Number2Number.Integer2Long;
import org.star_lang.star.operators.arith.runtime.Number2Number.Long2Decimal;
import org.star_lang.star.operators.arith.runtime.Number2Number.Long2Float;
import org.star_lang.star.operators.arith.runtime.Number2Number.Long2Integer;

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
public abstract class Number2Number extends Builtin
{

  public static void declare(Intrinsics cxt)
  {
    cxt.declareBuiltin(new GInteger2Integer());
    cxt.declareBuiltin(new GLong2Integer());
    cxt.declareBuiltin(new GFloat2Integer());
    cxt.declareBuiltin(new GDecimal2Integer());
    cxt.declareBuiltin(new GInteger2Long());
    cxt.declareBuiltin(new GFloat2Long());
    cxt.declareBuiltin(new GDecimal2Long());
    cxt.declareBuiltin(new GInteger2Float());
    cxt.declareBuiltin(new GLong2Float());
    cxt.declareBuiltin(new GDecimal2Float());
    cxt.declareBuiltin(new GInteger2Decimal());
    cxt.declareBuiltin(new GLong2Decimal());
    cxt.declareBuiltin(new GFloat2Decimal());
  }

  private Number2Number(String name, IType type, Class<?> implClass)
  {
    super(name, type, implClass);
  }

  public static class GInteger2Integer extends Number2Number
  {
    public static final String name = "__integer_integer";

    public GInteger2Integer()
    {
      super(name, TypeUtils.functionType(StandardTypes.rawIntegerType, StandardTypes.rawIntegerType),
          Integer2Integer.class);
    }
  }

  public static class GLong2Integer extends Number2Number
  {
    public static final String name = "__long_integer";

    public GLong2Integer()
    {
      super(name, TypeUtils.functionType(StandardTypes.rawLongType, StandardTypes.rawIntegerType), Long2Integer.class);
    }
  }

  public static class GFloat2Integer extends Number2Number
  {
    public static final String name = "__float_integer";

    public GFloat2Integer()
    {
      super(name, TypeUtils.functionType(StandardTypes.rawFloatType, StandardTypes.rawIntegerType), Float2Integer.class);
    }
  }

  public static class GDecimal2Integer extends Number2Number
  {
    public static final String name = "__decimal_integer";

    public GDecimal2Integer()
    {
      super(name, TypeUtils.functionType(StandardTypes.rawDecimalType, StandardTypes.rawIntegerType),
          Decimal2Integer.class);
    }
  }

  public static class GInteger2Long extends Number2Number
  {
    public static final String name = "__integer_long";

    public GInteger2Long()
    {
      super(name, TypeUtils.functionType(StandardTypes.rawIntegerType, StandardTypes.rawLongType), Integer2Long.class);
    }
  }

  public static class GFloat2Long extends Number2Number
  {
    public static final String name = "__float_long";

    public GFloat2Long()
    {
      super(name, TypeUtils.functionType(StandardTypes.rawFloatType, StandardTypes.rawLongType), Float2Long.class);
    }
  }

  public static class GDecimal2Long extends Number2Number
  {
    public static final String name = "__decimal_long";

    public GDecimal2Long()
    {
      super(name, TypeUtils.functionType(StandardTypes.rawDecimalType, StandardTypes.rawLongType), Decimal2Long.class);
    }
  }

  public static class GInteger2Float extends Number2Number implements Inliner
  {
    public static final String name = "__integer_float";

    public GInteger2Float()
    {
      super(name, TypeUtils.functionType(StandardTypes.rawIntegerType, StandardTypes.rawFloatType), Integer2Float.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM)
    {
    }

    @Override
    public void inline(ClassNode klass, MethodNode mtd, HWM hwm, Location loc)
    {
      mtd.instructions.add(new InsnNode(Opcodes.I2D));
      hwm.bump(1);
    }
  }

  public static class GLong2Float extends Number2Number
  {
    public static final String name = "__long_float";

    public GLong2Float()
    {
      super(name, TypeUtils.functionType(StandardTypes.rawLongType, StandardTypes.rawFloatType), Long2Float.class);
    }
  }

  public static class GDecimal2Float extends Number2Number
  {
    public static final String name = "__decimal_float";

    public GDecimal2Float()
    {
      super(name, TypeUtils.functionType(StandardTypes.rawDecimalType, StandardTypes.rawFloatType), Decimal2Float.class);
    }
  }

  public static class GInteger2Decimal extends Number2Number
  {
    public static final String name = "__integer_decimal";

    public GInteger2Decimal()
    {
      super(name, TypeUtils.functionType(StandardTypes.rawIntegerType, StandardTypes.rawDecimalType),
          Integer2Decimal.class);
    }
  }

  public static class GLong2Decimal extends Number2Number
  {
    public static final String name = "__long_decimal";

    public GLong2Decimal()
    {
      super(name, TypeUtils.functionType(StandardTypes.rawLongType, StandardTypes.rawDecimalType), Long2Decimal.class);
    }
  }

  public static class GFloat2Decimal extends Number2Number
  {
    public static final String name = "__float_decimal";

    public GFloat2Decimal()
    {
      super(name, TypeUtils.functionType(StandardTypes.rawFloatType, StandardTypes.rawDecimalType), Float2Decimal.class);
    }
  }
}
