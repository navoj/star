package org.star_lang.star.operators.assignment;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.star_lang.star.compiler.cafe.compile.HWM;
import org.star_lang.star.compiler.cafe.compile.Inliner;
import org.star_lang.star.compiler.cafe.compile.Types;
import org.star_lang.star.compiler.cafe.compile.Utils;
import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.assignment.runtime.RefCell;
import org.star_lang.star.operators.assignment.runtime.GetRefValue.GetRawBoolRef;
import org.star_lang.star.operators.assignment.runtime.GetRefValue.GetRawCharRef;
import org.star_lang.star.operators.assignment.runtime.GetRefValue.GetRawFloatRef;
import org.star_lang.star.operators.assignment.runtime.GetRefValue.GetRawIntegerRef;
import org.star_lang.star.operators.assignment.runtime.GetRefValue.GetRawLongRef;
import org.star_lang.star.operators.assignment.runtime.GetRefValue.GetRef;
import org.star_lang.star.operators.assignment.runtime.RefCell.BoolCell;
import org.star_lang.star.operators.assignment.runtime.RefCell.Cell;
import org.star_lang.star.operators.assignment.runtime.RefCell.CharCell;
import org.star_lang.star.operators.assignment.runtime.RefCell.FloatCell;
import org.star_lang.star.operators.assignment.runtime.RefCell.IntegerCell;
import org.star_lang.star.operators.assignment.runtime.RefCell.LongCell;

import com.starview.platform.data.type.Location;

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
public class ReferenceOps
{

  public static class GetReference extends Builtin implements Inliner
  {
    private static final String javaCellName = Utils.javaInternalClassName(Cell.class);

    public GetReference()
    {
      super(GetRef.name, GetRef.type(), GetRef.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM)
    {
    }

    @Override
    public void inline(ClassNode klass, MethodNode mtd, HWM hwm, Location loc)
    {
      InsnList ins = mtd.instructions;
      assert ins.getLast() instanceof TypeInsnNode && ((TypeInsnNode) ins.getLast()).desc.equals(javaCellName);
      ins.add(new FieldInsnNode(Opcodes.GETFIELD, javaCellName, RefCell.VALUEFIELD, Types.IVALUE_SIG));
    }
  }

  public static class GetRawBoolReference extends Builtin implements Inliner
  {
    public GetRawBoolReference()
    {
      super(GetRawBoolRef.name, GetRawBoolRef.type(), GetRawBoolRef.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM)
    {
    }

    @Override
    public void inline(ClassNode klass, MethodNode mtd, HWM hwm, Location loc)
    {
      InsnList ins = mtd.instructions;
      String javaCellName = Utils.javaInternalClassName(BoolCell.class);
      assert ins.getLast() instanceof TypeInsnNode && ((TypeInsnNode) ins.getLast()).desc.equals(javaCellName);
      ins.add(new FieldInsnNode(Opcodes.GETFIELD, javaCellName, RefCell.VALUEFIELD, Types.JAVA_BOOL_SIG));
    }
  }

  public static class GetRawCharReference extends Builtin implements Inliner
  {
    public GetRawCharReference()
    {
      super(GetRawCharRef.name, GetRawCharRef.type(), GetRawCharRef.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM)
    {
    }

    @Override
    public void inline(ClassNode klass, MethodNode mtd, HWM hwm, Location loc)
    {
      InsnList ins = mtd.instructions;
      String javaCellName = Utils.javaInternalClassName(CharCell.class);
      assert ins.getLast() instanceof TypeInsnNode && ((TypeInsnNode) ins.getLast()).desc.equals(javaCellName);
      ins.add(new FieldInsnNode(Opcodes.GETFIELD, javaCellName, RefCell.VALUEFIELD, Types.JAVA_INT_SIG));
    }
  }

  public static class GetRawIntegerReference extends Builtin implements Inliner
  {
    public GetRawIntegerReference()
    {
      super(GetRawIntegerRef.name, GetRawIntegerRef.type(), GetRawIntegerRef.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM)
    {
    }

    @Override
    public void inline(ClassNode klass, MethodNode mtd, HWM hwm, Location loc)
    {
      InsnList ins = mtd.instructions;
      String javaCellName = Utils.javaInternalClassName(IntegerCell.class);
      assert ins.getLast() instanceof TypeInsnNode && ((TypeInsnNode) ins.getLast()).desc.equals(javaCellName);
      ins.add(new FieldInsnNode(Opcodes.GETFIELD, javaCellName, IntegerCell.VALUEFIELD, Types.JAVA_INT_SIG));
    }
  }

  public static class GetRawLongReference extends Builtin implements Inliner
  {
    public GetRawLongReference()
    {
      super(GetRawLongRef.name, GetRawLongRef.type(), GetRawLongRef.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM)
    {
    }

    @Override
    public void inline(ClassNode klass, MethodNode mtd, HWM hwm, Location loc)
    {
      InsnList ins = mtd.instructions;
      String javaCellName = Utils.javaInternalClassName(LongCell.class);
      assert ins.getLast() instanceof TypeInsnNode && ((TypeInsnNode) ins.getLast()).desc.equals(javaCellName);
      ins.add(new FieldInsnNode(Opcodes.GETFIELD, javaCellName, RefCell.VALUEFIELD, Types.JAVA_LNG_SIG));
    }
  }

  public static class GetRawFloatReference extends Builtin implements Inliner
  {
    public GetRawFloatReference()
    {
      super(GetRawFloatRef.name, GetRawFloatRef.type(), GetRawFloatRef.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM)
    {
    }

    @Override
    public void inline(ClassNode klass, MethodNode mtd, HWM hwm, Location loc)
    {
      InsnList ins = mtd.instructions;
      String javaCellName = Utils.javaInternalClassName(FloatCell.class);
      assert ins.getLast() instanceof TypeInsnNode && ((TypeInsnNode) ins.getLast()).desc.equals(javaCellName);
      ins.add(new FieldInsnNode(Opcodes.GETFIELD, javaCellName, IntegerCell.VALUEFIELD, Types.JAVA_DBL_SIG));
    }
  }
}
