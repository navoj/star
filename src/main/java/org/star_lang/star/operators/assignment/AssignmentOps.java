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
