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
import org.star_lang.star.operators.arith.runtime.LongBinary.LongDivide;
import org.star_lang.star.operators.arith.runtime.LongBinary.LongLeft;
import org.star_lang.star.operators.arith.runtime.LongBinary.LongMax;
import org.star_lang.star.operators.arith.runtime.LongBinary.LongMin;
import org.star_lang.star.operators.arith.runtime.LongBinary.LongMinus;
import org.star_lang.star.operators.arith.runtime.LongBinary.LongPlus;
import org.star_lang.star.operators.arith.runtime.LongBinary.LongPwr;
import org.star_lang.star.operators.arith.runtime.LongBinary.LongRemainder;
import org.star_lang.star.operators.arith.runtime.LongBinary.LongRight;
import org.star_lang.star.operators.arith.runtime.LongBinary.LongTimes;



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


public abstract class LongBinary extends Builtin
{
  private static final IType type;

  static
  {
    IType longType = StandardTypes.rawLongType;
    type = TypeUtils.functionType(longType, longType, longType);
  }

  private LongBinary(String name, Class<?> implClass)
  {
    super(name, type, implClass);
  }

  public static void declare(Intrinsics cxt)
  {
    cxt.declareBuiltin(new LngPlus());
    cxt.declareBuiltin(new LngMinus());
    cxt.declareBuiltin(new LngTimes());
    cxt.declareBuiltin(new LngDivide());
    cxt.declareBuiltin(new LngRemainder());
    cxt.declareBuiltin(new LngPwr());
    cxt.declareBuiltin(new LngMin());
    cxt.declareBuiltin(new LngMax());
    cxt.declareBuiltin(new LngLeft());
    cxt.declareBuiltin(new LngRight());
  }

  public static class LngPlus extends LongBinary implements Inliner
  {
    public static final String name = "__long_plus";

    public LngPlus()
    {
      super(name, LongPlus.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM)
    {}

    @Override
    public void inline(ClassNode klass, MethodNode mtd, HWM hwm, Location loc)
    {
      InsnList ins = mtd.instructions;
      ins.add(new InsnNode(Opcodes.LADD));
    }
  }

  public static class LngMinus extends LongBinary implements Inliner
  {
    public static final String name = "__long_minus";

    public LngMinus()
    {
      super(name, LongMinus.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM)
    {}

    @Override
    public void inline(ClassNode klass, MethodNode mtd, HWM hwm, Location loc)
    {
      InsnList ins = mtd.instructions;
      ins.add(new InsnNode(Opcodes.LSUB));
    }
  }

  public static class LngTimes extends LongBinary implements Inliner
  {
    public static final String name = "__long_times";

    public LngTimes()
    {
      super(name, LongTimes.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM)
    {}

    @Override
    public void inline(ClassNode klass, MethodNode mtd, HWM hwm, Location loc)
    {
      InsnList ins = mtd.instructions;
      ins.add(new InsnNode(Opcodes.LMUL));
    }
  }

  public static class LngDivide extends LongBinary implements Inliner
  {
    public static final String name = "__long_divide";

    public LngDivide()
    {
      super(name, LongDivide.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM)
    {}

    @Override
    public void inline(ClassNode klass, MethodNode mtd, HWM hwm, Location loc)
    {
      InsnList ins = mtd.instructions;
      ins.add(new InsnNode(Opcodes.LDIV));
    }
  }

  public static class LngRemainder extends LongBinary implements Inliner
  {
    public static final String name = "__long_rem";

    public LngRemainder()
    {
      super(name, LongRemainder.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM)
    {}

    @Override
    public void inline(ClassNode klass, MethodNode mtd, HWM hwm, Location loc)
    {
      InsnList ins = mtd.instructions;
      ins.add(new InsnNode(Opcodes.LREM));
    }
  }

  public static class LngMin extends LongBinary
  {
    public static final String name = "__long_min";

    public LngMin()
    {
      super(name, LongMin.class);
    }
  }

  public static class LngMax extends LongBinary
  {
    public static final String name = "__long_max";

    public LngMax()
    {
      super(name, LongMax.class);
    }
  }

  public static class LngPwr extends LongBinary
  {
    public static final String name = "__long_power";

    public LngPwr()
    {
      super(name, LongPwr.class);
    }
  }

  public static class LngLeft extends LongBinary
  {
    public static final String name = "__long_left";

    public LngLeft()
    {
      super(name, LongLeft.class);
    }
  }

  public static class LngRight extends LongBinary
  {
    public static final String name = "__long_right";

    public LngRight()
    {
      super(name, LongRight.class);
    }
  }
}
