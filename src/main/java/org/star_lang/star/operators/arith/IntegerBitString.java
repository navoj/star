package org.star_lang.star.operators.arith;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.star_lang.star.compiler.cafe.compile.Expressions;
import org.star_lang.star.compiler.cafe.compile.HWM;
import org.star_lang.star.compiler.cafe.compile.Inliner;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.arith.runtime.IntBitString.BitAnd;
import org.star_lang.star.operators.arith.runtime.IntBitString.BitCount;
import org.star_lang.star.operators.arith.runtime.IntBitString.BitNeg;
import org.star_lang.star.operators.arith.runtime.IntBitString.BitOr;
import org.star_lang.star.operators.arith.runtime.IntBitString.BitSar;
import org.star_lang.star.operators.arith.runtime.IntBitString.BitShl;
import org.star_lang.star.operators.arith.runtime.IntBitString.BitShr;
import org.star_lang.star.operators.arith.runtime.IntBitString.BitXor;

/**
 * Bitstring functions for integers
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


public abstract class IntegerBitString extends Builtin
{
  private static final IType type;
  static {
    IType integerType = StandardTypes.rawIntegerType;
    type = TypeUtils.functionType(integerType, integerType, integerType);
  }

  private IntegerBitString(String name, Class<?> implClass)
  {
    super(name, type, implClass);
  }

  public static void declare(Intrinsics cxt)
  {
    cxt.declareBuiltin(new BAnd());
    cxt.declareBuiltin(new BOr());
    cxt.declareBuiltin(new BXor());
    cxt.declareBuiltin(new BNeg());
    cxt.declareBuiltin(new BShl());
    cxt.declareBuiltin(new BShr());
    cxt.declareBuiltin(new BSar());
    cxt.declareBuiltin(new BCount());
  }

  public static class BAnd extends IntegerBitString implements Inliner
  {
    public static final String name = "__integer_bit_and";

    public BAnd()
    {
      super(name, BitAnd.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM)
    {
    }

    @Override
    public void inline(ClassNode klass, MethodNode mtd, HWM hwm, Location loc)
    {
      InsnList ins = mtd.instructions;
      ins.add(new InsnNode(Opcodes.IAND));
    }
  }

  public static class BOr extends IntegerBitString implements Inliner
  {
    public static final String name = "__integer_bit_or";

    public BOr()
    {
      super(name, BitOr.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM)
    {
    }

    @Override
    public void inline(ClassNode klass, MethodNode mtd, HWM hwm, Location loc)
    {
      InsnList ins = mtd.instructions;
      ins.add(new InsnNode(Opcodes.IOR));
    }
  }

  public static class BXor extends IntegerBitString implements Inliner
  {
    public static final String name = "__integer_bit_xor";

    public BXor()
    {
      super(name, BitXor.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM)
    {
    }

    @Override
    public void inline(ClassNode klass, MethodNode mtd, HWM hwm, Location loc)
    {
      InsnList ins = mtd.instructions;
      ins.add(new InsnNode(Opcodes.IXOR));
    }
  }

  public static class BNeg extends Builtin implements Inliner
  {
    public static final String name = "__integer_bit_neg";

    public BNeg()
    {
      super(name, TypeUtils
          .functionType(StandardTypes.rawIntegerType, StandardTypes.rawIntegerType), BitNeg.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM)
    {
    }

    @Override
    public void inline(ClassNode klass, MethodNode mtd, HWM hwm, Location loc)
    {
      InsnList ins = mtd.instructions;
      hwm.probe(1);
      ins.add(new InsnNode(Opcodes.ICONST_M1)); // ~X = -1^X
      ins.add(new InsnNode(Opcodes.IXOR));
    }
  }

  public static class BShl extends IntegerBitString implements Inliner
  {
    public static final String name = "__integer_bit_shl";

    public BShl()
    {
      super(name, BitShl.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM)
    {
    }

    @Override
    public void inline(ClassNode klass, MethodNode mtd, HWM hwm, Location loc)
    {
      InsnList ins = mtd.instructions;
      ins.add(new InsnNode(Opcodes.ISHL));
    }
  }

  public static class BShr extends IntegerBitString implements Inliner
  {
    public static final String name = "__integer_bit_shr";

    public BShr()
    {
      super(name, BitShr.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM)
    {
    }

    @Override
    public void inline(ClassNode klass, MethodNode mtd, HWM hwm, Location loc)
    {
      InsnList ins = mtd.instructions;
      ins.add(new InsnNode(Opcodes.IUSHR));
    }
  }

  public static class BSar extends IntegerBitString implements Inliner
  {
    public static final String name = "__integer_bit_sar";

    public BSar()
    {
      super(name, BitSar.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM)
    {
    }

    @Override
    public void inline(ClassNode klass, MethodNode mtd, HWM hwm, Location loc)
    {
      InsnList ins = mtd.instructions;
      ins.add(new InsnNode(Opcodes.ISHR));
    }
  }

  public static class BCount extends Builtin implements Inliner
  {
    public static final String name = "__integer_bit_count";
    static final int SK5 = 0x55555555;
    static final int SK3 = 0x33333333;
    static final int SKF0 = 0x0F0F0F0F;
    static final int SKFF = 0x00FF00FF;
    static final int SKFFFF = 0x0000FFFF;

    public BCount()
    {
      super(name, TypeUtils
          .functionType(StandardTypes.rawIntegerType, StandardTypes.rawIntegerType), BitCount.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM)
    {
    }

    @Override
    public void inline(ClassNode klass, MethodNode mtd, HWM hwm, Location loc)
    {
      InsnList ins = mtd.instructions;
      onePass(ins, hwm, SK5, 1);
      onePass(ins, hwm, SK3, 2);
      onePass(ins, hwm, SKF0, 4);
      onePass(ins, hwm, SKFF, 8);
      onePass(ins, hwm, SKFFFF, 16);
      ins.add(new LdcInsnNode(0x3F));
      ins.add(new InsnNode(Opcodes.IAND));
    }

    private void onePass(InsnList ins, HWM hwm, int mask, int shift)
    {
      int mark = hwm.mark();
      hwm.bump(4);
      ins.add(new InsnNode(Opcodes.DUP));
      Expressions.genIntConst(ins, hwm, shift);
      ins.add(new InsnNode(Opcodes.IUSHR));
      ins.add(new LdcInsnNode(mask));
      ins.add(new InsnNode(Opcodes.IAND));
      ins.add(new InsnNode(Opcodes.SWAP));
      ins.add(new LdcInsnNode(mask));
      ins.add(new InsnNode(Opcodes.IAND));
      ins.add(new InsnNode(Opcodes.IADD));
      hwm.reset(mark);
      hwm.bump(1);
    }
  }
}
