package org.star_lang.star.operators.assignment;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.star_lang.star.compiler.cafe.compile.HWM;
import org.star_lang.star.compiler.cafe.compile.Inliner;
import org.star_lang.star.compiler.cafe.compile.Types;
import org.star_lang.star.compiler.cafe.compile.Utils;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.assignment.runtime.GetRefValue.*;
import org.star_lang.star.operators.assignment.runtime.RefCell;
import org.star_lang.star.operators.assignment.runtime.RefCell.*;

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
public class ReferenceOps {

  public static class GetReference extends Builtin implements Inliner {
    private static final String javaCellName = Utils.javaInternalClassName(Cell.class);

    public GetReference() {
      super(GetRef.name, GetRef.type(), GetRef.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM) {
    }

    @Override
    public void inline(ClassNode klass, MethodNode mtd, HWM hwm, Location loc) {
      InsnList ins = mtd.instructions;
      assert ins.getLast() instanceof TypeInsnNode && ((TypeInsnNode) ins.getLast()).desc.equals(javaCellName);
      ins.add(new FieldInsnNode(Opcodes.GETFIELD, javaCellName, RefCell.VALUEFIELD, Types.IVALUE_SIG));
    }
  }

  public static class GetRawBoolReference extends Builtin implements Inliner {
    public GetRawBoolReference() {
      super(GetRawBoolRef.name, GetRawBoolRef.type(), GetRawBoolRef.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM) {
    }

    @Override
    public void inline(ClassNode klass, MethodNode mtd, HWM hwm, Location loc) {
      InsnList ins = mtd.instructions;
      String javaCellName = Utils.javaInternalClassName(BoolCell.class);
      assert ins.getLast() instanceof TypeInsnNode && ((TypeInsnNode) ins.getLast()).desc.equals(javaCellName);
      ins.add(new FieldInsnNode(Opcodes.GETFIELD, javaCellName, RefCell.VALUEFIELD, Types.JAVA_BOOL_SIG));
    }
  }

  public static class GetRawIntegerReference extends Builtin implements Inliner {
    public GetRawIntegerReference() {
      super(GetRawIntegerRef.name, GetRawIntegerRef.type(), GetRawIntegerRef.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM) {
    }

    @Override
    public void inline(ClassNode klass, MethodNode mtd, HWM hwm, Location loc) {
      InsnList ins = mtd.instructions;
      String javaCellName = Utils.javaInternalClassName(IntegerCell.class);
      assert ins.getLast() instanceof TypeInsnNode && ((TypeInsnNode) ins.getLast()).desc.equals(javaCellName);
      ins.add(new FieldInsnNode(Opcodes.GETFIELD, javaCellName, RefCell.VALUEFIELD, Types.JAVA_INT_SIG));
    }
  }

  public static class GetRawLongReference extends Builtin implements Inliner {
    public GetRawLongReference() {
      super(GetRawLongRef.name, GetRawLongRef.type(), GetRawLongRef.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM) {
    }

    @Override
    public void inline(ClassNode klass, MethodNode mtd, HWM hwm, Location loc) {
      InsnList ins = mtd.instructions;
      String javaCellName = Utils.javaInternalClassName(LongCell.class);
      assert ins.getLast() instanceof TypeInsnNode && ((TypeInsnNode) ins.getLast()).desc.equals(javaCellName);
      ins.add(new FieldInsnNode(Opcodes.GETFIELD, javaCellName, RefCell.VALUEFIELD, Types.JAVA_LNG_SIG));
    }
  }

  public static class GetRawFloatReference extends Builtin implements Inliner {
    public GetRawFloatReference() {
      super(GetRawFloatRef.name, GetRawFloatRef.type(), GetRawFloatRef.class);
    }

    @Override
    public void preamble(MethodNode mtd, HWM stackHWM) {
    }

    @Override
    public void inline(ClassNode klass, MethodNode mtd, HWM hwm, Location loc) {
      InsnList ins = mtd.instructions;
      String javaCellName = Utils.javaInternalClassName(FloatCell.class);
      assert ins.getLast() instanceof TypeInsnNode && ((TypeInsnNode) ins.getLast()).desc.equals(javaCellName);
      ins.add(new FieldInsnNode(Opcodes.GETFIELD, javaCellName, RefCell.VALUEFIELD, Types.JAVA_DBL_SIG));
    }
  }
}
