package org.star_lang.star.operators.string;

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
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.string.runtime.Char2Number.Char2Int;
import org.star_lang.star.operators.string.runtime.Char2Number.Int2Char;
import org.star_lang.star.operators.string.runtime.CharCompare;
import org.star_lang.star.operators.string.runtime.CharCompare.CharMin;
import org.star_lang.star.operators.string.runtime.CharUtils.IsIdentifierPart;
import org.star_lang.star.operators.string.runtime.CharUtils.IsIdentifierStart;
import org.star_lang.star.operators.string.runtime.CharUtils.IsLowerCase;
import org.star_lang.star.operators.string.runtime.CharUtils.IsUnicodeIdentifier;
import org.star_lang.star.operators.string.runtime.CharUtils.IsUpperCase;

/**
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
public abstract class CharOps
{
  private static final IType rawCharType = StandardTypes.rawCharType;

  public static void declare(Intrinsics cxt)
  {
    String equality = StandardNames.EQUALITY;
    PrimitiveOverloader.declarePrimitiveImplementation(equality, StandardNames.EQUAL, rawCharType, CharCompare.CharEQ.name);
    cxt.declareBuiltin(new CharEQ());

    cxt.declareBuiltin(new CharLT());
    cxt.declareBuiltin(new CharLE());
    cxt.declareBuiltin(new CharGE());
    cxt.declareBuiltin(new CharGT());

    cxt.declareBuiltin(new Builtin(CharMin.name, CharMin.type(), CharMin.class));

    cxt.declareBuiltin(new Builtin(Char2Int.name, Char2Int.type(), Char2Int.class));
    cxt.declareBuiltin(new Builtin(Int2Char.name, Int2Char.type(), Int2Char.class));

    cxt.declareBuiltin(new Builtin(IsIdentifierStart.name, IsIdentifierStart.type(), IsIdentifierStart.class));
    cxt.declareBuiltin(new Builtin(IsIdentifierPart.name, IsIdentifierPart.type(), IsIdentifierPart.class));
    cxt.declareBuiltin(new Builtin(IsUnicodeIdentifier.name, IsUnicodeIdentifier.type(), IsUnicodeIdentifier.class));
    cxt.declareBuiltin(new Builtin(IsLowerCase.name, IsLowerCase.type(), IsLowerCase.class));
    cxt.declareBuiltin(new Builtin(IsUpperCase.name, IsUpperCase.type(), IsUpperCase.class));
  }

  public static class CharEQ extends Builtin implements InlinePredicate
  {
    public CharEQ()
    {
      super(CharCompare.CharEQ.name, CharCompare.CharEQ.type(), CharCompare.CharEQ.class);
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

  public static class CharLT extends Builtin implements InlinePredicate
  {
    public CharLT()
    {
      super(CharCompare.CharLT.name, CharCompare.CharLT.type(), CharCompare.CharLT.class);
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

  public static class CharLE extends Builtin implements InlinePredicate
  {
    public CharLE()
    {
      super(CharCompare.CharLE.name, CharCompare.CharLE.type(), CharCompare.CharLE.class);
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

  public static class CharGE extends Builtin implements InlinePredicate
  {
    public CharGE()
    {
      super(CharCompare.CharGE.name, CharCompare.CharGE.type(), CharCompare.CharGE.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM)
    {
    }

    @Override
    public void inline(MethodNode mtd, HWM stackHWM, Sense sense, LabelNode lbl)
    {
      InsnList ins = mtd.instructions;

      if (sense == Sense.jmpOnOk) {
        ins.add(new JumpInsnNode(Opcodes.IF_ICMPGE, lbl));
      } else {
        ins.add(new JumpInsnNode(Opcodes.IF_ICMPLT, lbl));
      }
    }
  }

  public static class CharGT extends Builtin implements InlinePredicate
  {
    public CharGT()
    {
      super(CharCompare.CharGT.name, CharCompare.CharGT.type(), CharCompare.CharGT.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM)
    {
    }

    @Override
    public void inline(MethodNode mtd, HWM stackHWM, Sense sense, LabelNode lbl)
    {
      InsnList ins = mtd.instructions;

      if (sense == Sense.jmpOnOk) {
        ins.add(new JumpInsnNode(Opcodes.IF_ICMPGT, lbl));
      } else {
        ins.add(new JumpInsnNode(Opcodes.IF_ICMPLE, lbl));
      }
    }
  }

}