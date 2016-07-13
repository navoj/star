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
import org.star_lang.star.operators.arith.runtime.Number2Number.*;

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
    cxt.declareBuiltin(new GInteger2Long());
    cxt.declareBuiltin(new GFloat2Long());
    cxt.declareBuiltin(new GInteger2Float());
    cxt.declareBuiltin(new GLong2Float());
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
}
