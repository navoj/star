package org.star_lang.star.operators.assignment;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.star_lang.star.compiler.cafe.compile.HWM;
import org.star_lang.star.compiler.cafe.compile.Inliner;
import org.star_lang.star.compiler.cafe.compile.Types;
import org.star_lang.star.compiler.cafe.compile.Utils;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.assignment.runtime.RefCell;
import org.star_lang.star.operators.assignment.runtime.Assignments.Assign;
import org.star_lang.star.operators.assignment.runtime.Assignments.AssignRawBool;
import org.star_lang.star.operators.assignment.runtime.Assignments.AssignRawChar;
import org.star_lang.star.operators.assignment.runtime.Assignments.AssignRawFloat;
import org.star_lang.star.operators.assignment.runtime.Assignments.AssignRawInteger;
import org.star_lang.star.operators.assignment.runtime.Assignments.AssignRawLong;
import org.star_lang.star.operators.assignment.runtime.RefCell.BoolCell;
import org.star_lang.star.operators.assignment.runtime.RefCell.Cell;
import org.star_lang.star.operators.assignment.runtime.RefCell.CharCell;
import org.star_lang.star.operators.assignment.runtime.RefCell.FloatCell;
import org.star_lang.star.operators.assignment.runtime.RefCell.IntegerCell;
import org.star_lang.star.operators.assignment.runtime.RefCell.LongCell;
/**
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

public class AssignmentOps
{

  public static class Assignment extends Builtin implements Inliner
  {
    public Assignment()
    {
      super(Assign.name, Assign.type(), Assign.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM)
    {
    }

    @Override
    public void inline(ClassNode klass, MethodNode mtd, HWM hwm, Location loc)
    {
      InsnList ins = mtd.instructions;
      String javaCellName = Utils.javaInternalClassName(Cell.class);
      ins.add(new FieldInsnNode(Opcodes.PUTFIELD, javaCellName, RefCell.VALUEFIELD, Types.IVALUE_SIG));
    }
  }

  public static class RawBoolAssignment extends Builtin implements Inliner
  {
    public RawBoolAssignment()
    {
      super(AssignRawBool.name, AssignRawBool.type(), AssignRawBool.class);
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
      ins.add(new FieldInsnNode(Opcodes.PUTFIELD, javaCellName, RefCell.VALUEFIELD, Types.JAVA_BOOL_SIG));
    }
  }

  public static class RawCharAssignment extends Builtin implements Inliner
  {
    public RawCharAssignment()
    {
      super(AssignRawChar.name, AssignRawChar.type(), AssignRawChar.class);
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
      ins.add(new FieldInsnNode(Opcodes.PUTFIELD, javaCellName, RefCell.VALUEFIELD, Types.JAVA_INT_SIG));
    }
  }

  public static class RawIntegerAssignment extends Builtin implements Inliner
  {
    public RawIntegerAssignment()
    {
      super(AssignRawInteger.name, AssignRawInteger.type(), AssignRawInteger.class);
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
      ins.add(new FieldInsnNode(Opcodes.PUTFIELD, javaCellName, RefCell.VALUEFIELD, Types.JAVA_INT_SIG));
    }
  }

  public static class RawLongAssignment extends Builtin implements Inliner
  {
    public RawLongAssignment()
    {
      super(AssignRawLong.name, AssignRawLong.type(), AssignRawLong.class);
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
      ins.add(new FieldInsnNode(Opcodes.PUTFIELD, javaCellName, RefCell.VALUEFIELD, Types.JAVA_LNG_SIG));
    }
  }

  public static class RawFloatAssignment extends Builtin implements Inliner
  {
    public RawFloatAssignment()
    {
      super(AssignRawFloat.name, AssignRawFloat.type(), AssignRawFloat.class);
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
      ins.add(new FieldInsnNode(Opcodes.PUTFIELD, javaCellName, RefCell.VALUEFIELD, Types.JAVA_DBL_SIG));
    }
  }
}
