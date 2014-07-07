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
import org.star_lang.star.operators.arith.runtime.LongBitString.BitAnd;
import org.star_lang.star.operators.arith.runtime.LongBitString.BitCount;
import org.star_lang.star.operators.arith.runtime.LongBitString.BitOr;
import org.star_lang.star.operators.arith.runtime.LongBitString.BitSar;
import org.star_lang.star.operators.arith.runtime.LongBitString.BitShl;
import org.star_lang.star.operators.arith.runtime.LongBitString.BitShr;
import org.star_lang.star.operators.arith.runtime.LongBitString.BitXor;
import org.star_lang.star.operators.arith.runtime.LongBitString.LongBitNeg;

/**
 * Bitstring functions
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


public abstract class LongBitString extends Builtin
{
  private static final IType type;
  static {
    IType longType = StandardTypes.rawLongType;
    type = TypeUtils.functionType(longType, longType, longType);
  }

  private LongBitString(String name, Class<?> implClass)
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

  public static class BAnd extends LongBitString implements Inliner
  {
    public static final String name = "__long_bit_and";

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
      ins.add(new InsnNode(Opcodes.LAND));
    }
  }

  public static class BOr extends LongBitString implements Inliner
  {
    public static final String name = "__long_bit_or";

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
      ins.add(new InsnNode(Opcodes.LOR));
    }
  }

  public static class BXor extends LongBitString implements Inliner
  {
    public static final String name = "__long_bit_xor";

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
      ins.add(new InsnNode(Opcodes.LXOR));
    }
  }

  public static class BNeg extends Builtin implements Inliner
  {
    public static final String name = "__long_bit_neg";

    public BNeg()
    {
      super(name, TypeUtils.functionType(StandardTypes.rawLongType, StandardTypes.rawLongType), LongBitNeg.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM)
    {
    }

    @Override
    public void inline(ClassNode klass, MethodNode mtd, HWM hwm, Location loc)
    {
      InsnList ins = mtd.instructions;
      hwm.bump(2);
      Expressions.genLongConst(ins, hwm, -1);
      ins.add(new InsnNode(Opcodes.LXOR));
    }
  }

  public static class BShl extends LongBitString implements Inliner
  {
    public static final String name = "__long_bit_shl";

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
      ins.add(new InsnNode(Opcodes.L2I)); // convert shift to an integer
      ins.add(new InsnNode(Opcodes.LSHL));
    }
  }

  public static class BShr extends LongBitString implements Inliner
  {
    public static final String name = "__long_bit_shr";

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
      ins.add(new InsnNode(Opcodes.L2I)); // convert shift to an integer
      ins.add(new InsnNode(Opcodes.LUSHR));
    }
  }

  public static class BSar extends LongBitString implements Inliner
  {
    public static final String name = "__long_bit_sar";

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
      ins.add(new InsnNode(Opcodes.L2I)); // convert shift to an integer
      ins.add(new InsnNode(Opcodes.LSHR));
    }
  }

  public static class BCount extends Builtin implements Inliner
  {
    public static final String name = "__long_bit_count";
    static final long SK5 = 0x5555555555555555L;
    static final long SK3 = 0x3333333333333333L;
    static final long SKF0 = 0x0F0F0F0F0F0F0F0FL;
    static final long SKFF = 0x00FF00FF00FF00FFL;
    static final long SKFFFF = 0x0000FFFF0000FFFFL;
    static final long SKFFFFFFFF = 0x00000000FFFFFFFFL;

    public BCount()
    {
      super(name, TypeUtils.functionType(StandardTypes.rawLongType, StandardTypes.rawIntegerType), BitCount.class);
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
      onePass(ins, hwm, SKFFFFFFFF, 32);
      ins.add(new LdcInsnNode(0x5FL));
      ins.add(new InsnNode(Opcodes.LAND));
      ins.add(new InsnNode(Opcodes.L2I));
    }

    private void onePass(InsnList ins, HWM hwm, long mask, int shift)
    {
      int mark = hwm.mark();
      hwm.bump(4);
      ins.add(new InsnNode(Opcodes.DUP2));
      Expressions.genIntConst(ins, hwm, shift);
      ins.add(new InsnNode(Opcodes.LUSHR));
      ins.add(new LdcInsnNode(mask));
      ins.add(new InsnNode(Opcodes.LAND));
      ins.add(new InsnNode(Opcodes.DUP2_X2)); // achieve a double swap
      ins.add(new InsnNode(Opcodes.POP2));
      ins.add(new LdcInsnNode(mask));
      ins.add(new InsnNode(Opcodes.LAND));
      ins.add(new InsnNode(Opcodes.LADD));
      hwm.reset(mark);
      hwm.bump(2);
    }
  }
}
