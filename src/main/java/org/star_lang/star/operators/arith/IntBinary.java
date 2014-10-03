package org.star_lang.star.operators.arith;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
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
import org.star_lang.star.operators.arith.runtime.IntBinary.IntDivide;
import org.star_lang.star.operators.arith.runtime.IntBinary.IntLeft;
import org.star_lang.star.operators.arith.runtime.IntBinary.IntMax;
import org.star_lang.star.operators.arith.runtime.IntBinary.IntMin;
import org.star_lang.star.operators.arith.runtime.IntBinary.IntMinus;
import org.star_lang.star.operators.arith.runtime.IntBinary.IntPlus;
import org.star_lang.star.operators.arith.runtime.IntBinary.IntPwr;
import org.star_lang.star.operators.arith.runtime.IntBinary.IntRemainder;
import org.star_lang.star.operators.arith.runtime.IntBinary.IntRight;
import org.star_lang.star.operators.arith.runtime.IntBinary.IntTimes;



/**
 * Binary arithmetic functions
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


public abstract class IntBinary extends Builtin
{
  static private final IType type;

  static
  {
    IType intType = StandardTypes.rawIntegerType;
    type = TypeUtils.functionType(intType, intType, intType);
  }

  private IntBinary(String name, Class<?> implClass)
  {
    super(name, type, implClass);
  }

  public static void declare(Intrinsics cxt)
  {
    cxt.declareBuiltin(new IntgrPlus());
    cxt.declareBuiltin(new IntgrMinus());
    cxt.declareBuiltin(new IntgrTimes());
    cxt.declareBuiltin(new IntgrDivide());
    cxt.declareBuiltin(new IntgrRemainder());
    cxt.declareBuiltin(new IntgrPwr());
    cxt.declareBuiltin(new IntgrMin());
    cxt.declareBuiltin(new IntgrMax());
    cxt.declareBuiltin(new IntgrLeft());
    cxt.declareBuiltin(new IntgrRight());
  }

  public static class IntgrPlus extends IntBinary implements Inliner
  {
    public static final String name = "__integer_plus";

    public IntgrPlus()
    {
      super(name, IntPlus.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM)
    {}

    @Override
    public void inline(ClassNode klass, MethodNode mtd, HWM hwm, Location loc)
    {
      InsnList ins = mtd.instructions;
      ins.add(new InsnNode(Opcodes.IADD));
    }
  }

  public static class IntgrMinus extends IntBinary implements Inliner
  {
    public static final String name = "__integer_minus";

    public IntgrMinus()
    {
      super(name, IntMinus.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM)
    {}

    @Override
    public void inline(ClassNode klass, MethodNode mtd, HWM hwm, Location loc)
    {
      InsnList ins = mtd.instructions;
      ins.add(new InsnNode(Opcodes.ISUB));
    }
  }

  public static class IntgrTimes extends IntBinary implements Inliner
  {
    public static final String name = "__integer_times";

    public IntgrTimes()
    {
      super(name, IntTimes.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM)
    {}

    @Override
    public void inline(ClassNode klass, MethodNode mtd, HWM hwm, Location loc)
    {
      InsnList ins = mtd.instructions;
      ins.add(new InsnNode(Opcodes.IMUL));
    }
  }

  public static class IntgrDivide extends IntBinary implements Inliner
  {
    public static final String name = "__integer_divide";

    public IntgrDivide()
    {
      super(name, IntDivide.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM)
    {}

    @Override
    public void inline(ClassNode klass, MethodNode mtd, HWM hwm, Location loc)
    {
      InsnList ins = mtd.instructions;
      ins.add(new InsnNode(Opcodes.IDIV));
    }
  }

  public static class IntgrRemainder extends IntBinary implements Inliner
  {
    public static final String name = "__integer_rem";

    public IntgrRemainder()
    {
      super(name, IntRemainder.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM)
    {}

    @Override
    public void inline(ClassNode klass, MethodNode mtd, HWM hwm, Location loc)
    {
      InsnList ins = mtd.instructions;
      ins.add(new InsnNode(Opcodes.IREM));
    }
  }

  public static class IntgrMin extends IntBinary
  {
    public static final String name = "__integer_min";

    public IntgrMin()
    {
      super(name, IntMin.class);
    }

  }

  public static class IntgrMax extends IntBinary
  {
    public static final String name = "__integer_max";

    public IntgrMax()
    {
      super(name, IntMax.class);
    }
  }

  public static class IntgrPwr extends IntBinary
  {
    public static final String name = "__integer_power";

    public IntgrPwr()
    {
      super(name, IntPwr.class);
    }
  }

  public static class IntgrLeft extends IntBinary
  {
    public static final String name = "__integer_left";

    public IntgrLeft()
    {
      super(name, IntLeft.class);
    }
  }

  public static class IntgrRight extends IntBinary
  {
    public static final String name = "__integer_right";

    public IntgrRight()
    {
      super(name, IntRight.class);
    }
  }
}
