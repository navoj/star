package org.star_lang.star.operators.assignment.runtime;

import java.util.ArrayList;
import java.util.List;

import org.star_lang.star.compiler.cafe.type.CafeTypeDescription;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IConstructor;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.IValueVisitor;
import org.star_lang.star.data.type.ConstructorSpecifier;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.ITypeDescription;
import org.star_lang.star.data.type.IValueSpecifier;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.type.TypeExp;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.type.UniversalType;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.assignment.AssignmentOps.Assignment;
import org.star_lang.star.operators.assignment.AssignmentOps.RawBoolAssignment;
import org.star_lang.star.operators.assignment.AssignmentOps.RawCharAssignment;
import org.star_lang.star.operators.assignment.AssignmentOps.RawFloatAssignment;
import org.star_lang.star.operators.assignment.AssignmentOps.RawIntegerAssignment;
import org.star_lang.star.operators.assignment.AssignmentOps.RawLongAssignment;
import org.star_lang.star.operators.assignment.ReferenceOps.GetRawBoolReference;
import org.star_lang.star.operators.assignment.ReferenceOps.GetRawCharReference;
import org.star_lang.star.operators.assignment.ReferenceOps.GetRawFloatReference;
import org.star_lang.star.operators.assignment.ReferenceOps.GetRawIntegerReference;
import org.star_lang.star.operators.assignment.ReferenceOps.GetRawLongReference;
import org.star_lang.star.operators.assignment.ReferenceOps.GetReference;
import org.star_lang.star.operators.string.runtime.ValueDisplay;

/*
 * A RefCell holds the value of a re-assignable variable
 *
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

@SuppressWarnings("serial")
public abstract class RefCell implements IConstructor, PrettyPrintable {
  public static final String typeLabel = StandardNames.REF;
  public static final String VALUEFIELD = "value";

  private static final int itemOffset = 0;

  public static class Cell extends RefCell {
    public static final int conIx = 0;
    public static final String label = "_cell";

    public IValue value;

    public Cell(IValue value) {
      this.value = value;
    }

    @Override
    public int conIx() {
      return conIx;
    }

    @Override
    public String getLabel() {
      return label;
    }

    @Override
    public int size() {
      return 1;
    }

    @Override
    public IValue getCell(int index) {
      switch (index) {
        case itemOffset:
          return value;
        default:
          throw new IllegalAccessError("index out of range");
      }
    }

    public IValue get___0() {
      return value;
    }

    @Override
    public IValue[] getCells() {
      return new IValue[]{value};
    }

    public void setItem(IValue value) {
      this.value = value;
    }

    @Override
    public void setCell(int index, IValue value) throws EvaluationException {
      switch (index) {
        case itemOffset:
          this.value = value;
          return;
        default:
          throw new IllegalAccessError("index out of range");
      }
    }

    @Override
    public IConstructor copy() throws EvaluationException {
      return new Cell(value.copy());
    }

    @Override
    public IConstructor shallowCopy() throws EvaluationException {
      return new Cell(value);
    }

    @Override
    public IType getType() {
      return new TypeExp(typeLabel, value.getType());
    }

    public static IType conType() {
      TypeVar tv = new TypeVar();
      return new UniversalType(tv, TypeUtils.tupleConstructorType(tv, new TypeExp(typeLabel, tv)));
    }
  }

  /**
   * Cells for raw boolean values
   */
  public static class BoolCell extends RefCell {
    public static final int conIx = 1;
    public static final String label = "_bool_cell";

    public boolean value;

    public BoolCell(boolean value) {
      this.value = value;
    }

    @Override
    public int conIx() {
      return conIx;
    }

    @Override
    public String getLabel() {
      return label;
    }

    @Override
    public int size() {
      return 1;
    }

    @Override
    public IValue getCell(int index) {
      switch (index) {
        case itemOffset:
          return Factory.newBool(value);
        default:
          throw new IllegalAccessError("index out of range");
      }
    }

    public boolean get___0() {
      return value;
    }

    @Override
    public IValue[] getCells() {
      return new IValue[]{Factory.newBool(value)};
    }

    public void setItem(boolean value) {
      this.value = value;
    }

    @Override
    public void setCell(int index, IValue value) throws EvaluationException {
      switch (index) {
        case itemOffset:
          this.value = Factory.boolValue(value);
        default:
          throw new IllegalAccessError("index out of range");
      }
    }

    @Override
    public IConstructor copy() throws EvaluationException {
      return new BoolCell(value);
    }

    @Override
    public IConstructor shallowCopy() throws EvaluationException {
      return new BoolCell(value);
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      return new TypeExp(typeLabel, StandardTypes.rawBoolType);
    }

    public static IType conType() {
      return TypeUtils.tupleConstructorType(StandardTypes.rawBoolType,
              new TypeExp(typeLabel, StandardTypes.rawBoolType));
    }
  }

  /**
   * Cells for raw char values
   */
  public static class CharCell extends RefCell {
    public static final int conIx = 2;
    public static final String label = "_char_cell";

    public int value;

    public CharCell(int value) {
      this.value = value;
    }

    @Override
    public int conIx() {
      return conIx;
    }

    @Override
    public String getLabel() {
      return label;
    }

    @Override
    public int size() {
      return 1;
    }

    @Override
    public IValue getCell(int index) {
      switch (index) {
        case itemOffset:
          return Factory.newChar(value);
        default:
          throw new IllegalAccessError("index out of range");
      }
    }

    public int get___0() {
      return value;
    }

    @Override
    public IValue[] getCells() {
      return new IValue[]{Factory.newChar(value)};
    }

    public void setItem(int value) {
      this.value = value;
    }

    @Override
    public void setCell(int index, IValue value) throws EvaluationException {
      switch (index) {
        case itemOffset:
          this.value = Factory.charValue(value);
        default:
          throw new IllegalAccessError("index out of range");
      }
    }

    @Override
    public IConstructor copy() throws EvaluationException {
      return new CharCell(value);
    }

    @Override
    public IConstructor shallowCopy() throws EvaluationException {
      return new CharCell(value);
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      return new TypeExp(typeLabel, StandardTypes.rawCharType);
    }

    public static IType conType() {
      return TypeUtils.tupleConstructorType(StandardTypes.rawCharType,
              new TypeExp(typeLabel, StandardTypes.rawCharType));
    }
  }

  /**
   * Cells for raw integer values
   */
  public static class IntegerCell extends RefCell {
    public static final int conIx = 3;
    public static final String label = "_integer_cell";

    public int value;

    public IntegerCell(int value) {
      this.value = value;
    }

    @Override
    public int conIx() {
      return conIx;
    }

    @Override
    public String getLabel() {
      return label;
    }

    @Override
    public int size() {
      return 1;
    }

    @Override
    public IValue getCell(int index) {
      switch (index) {
        case itemOffset:
          return Factory.newInt(value);
        default:
          throw new IllegalAccessError("index out of range");
      }
    }

    @Override
    public IValue[] getCells() {
      return new IValue[]{Factory.newInt(value)};
    }

    public int get___0() {
      return value;
    }

    public void setItem(int value) {
      this.value = value;
    }

    @Override
    public void setCell(int index, IValue value) throws EvaluationException {
      switch (index) {
        case itemOffset:
          this.value = Factory.intValue(value);
        default:
          throw new IllegalAccessError("index out of range");
      }
    }

    @Override
    public IConstructor copy() throws EvaluationException {
      return new IntegerCell(value);
    }

    @Override
    public IConstructor shallowCopy() throws EvaluationException {
      return new IntegerCell(value);
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      return new TypeExp(typeLabel, StandardTypes.rawIntegerType);
    }

    public static IType conType() {
      return TypeUtils.tupleConstructorType(StandardTypes.rawIntegerType, new TypeExp(typeLabel,
              StandardTypes.rawIntegerType));
    }
  }

  /**
   * Cells for raw long values
   */
  public static class LongCell extends RefCell {
    public static final int conIx = 4;
    public static final String label = "_long_cell";

    public long value;

    public LongCell(long value) {
      this.value = value;
    }

    @Override
    public int conIx() {
      return conIx;
    }

    @Override
    public String getLabel() {
      return label;
    }

    @Override
    public int size() {
      return 1;
    }

    @Override
    public IValue getCell(int index) {
      switch (index) {
        case itemOffset:
          return Factory.newLng(value);
        default:
          throw new IllegalAccessError("index out of range");
      }
    }

    public long get___0() {
      return value;
    }

    @Override
    public IValue[] getCells() {
      return new IValue[]{Factory.newLng(value)};
    }

    public void setItem(long value) {
      this.value = value;
    }

    @Override
    public void setCell(int index, IValue value) throws EvaluationException {
      switch (index) {
        case itemOffset:
          this.value = Factory.intValue(value);
        default:
          throw new IllegalAccessError("index out of range");
      }
    }

    @Override
    public IConstructor copy() throws EvaluationException {
      return new LongCell(value);
    }

    @Override
    public IConstructor shallowCopy() throws EvaluationException {
      return new LongCell(value);
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      return new TypeExp(typeLabel, StandardTypes.rawLongType);
    }

    public static IType conType() {
      return TypeUtils.tupleConstructorType(StandardTypes.rawLongType,
              new TypeExp(typeLabel, StandardTypes.rawLongType));
    }
  }

  /**
   * Cells for raw float values
   */
  public static class FloatCell extends RefCell {
    public static final int conIx = 5;
    public static final String label = "_float_cell";

    public double value;

    public FloatCell(double value) {
      this.value = value;
    }

    @Override
    public int conIx() {
      return conIx;
    }

    @Override
    public String getLabel() {
      return label;
    }

    @Override
    public int size() {
      return 1;
    }

    @Override
    public IValue getCell(int index) {
      switch (index) {
        case itemOffset:
          return Factory.newFlt(value);
        default:
          throw new IllegalAccessError("index out of range");
      }
    }

    public double get___0() {
      return value;
    }

    @Override
    public IValue[] getCells() {
      return new IValue[]{Factory.newFlt(value)};
    }

    public void setItem(double value) {
      this.value = value;
    }

    @Override
    public void setCell(int index, IValue value) throws EvaluationException {
      switch (index) {
        case itemOffset:
          this.value = Factory.fltValue(value);
        default:
          throw new IllegalAccessError("index out of range");
      }
    }

    @Override
    public IConstructor copy() throws EvaluationException {
      return new FloatCell(value);
    }

    @Override
    public IConstructor shallowCopy() throws EvaluationException {
      return new FloatCell(value);
    }

    @Override
    public IType getType() {
      return type();
    }

    public static IType type() {
      return new TypeExp(typeLabel, StandardTypes.rawFloatType);
    }

    public static IType conType() {
      return TypeUtils.tupleConstructorType(StandardTypes.rawFloatType, new TypeExp(typeLabel,
              StandardTypes.rawFloatType));
    }
  }

  @Override
  public void accept(IValueVisitor visitor) {
    visitor.visitConstructor(this);
  }

  @Override
  public String toString() {
    return PrettyPrintDisplay.toString(this);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp) {
    ValueDisplay.display(disp, this);
  }

  /**
   * NB: equality of a RefCell, and hashCode, are effectively pointer equality and hashCode. Should
   * NOT dereference the cell.
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return super.equals(obj);
  }

  public static String cellLabel(IType type) {
    assert TypeUtils.isReferenceType(type);
    IType refType = TypeUtils.referencedType(type);
    if (TypeUtils.isRawBoolType(refType))
      return BoolCell.label;
    else if (TypeUtils.isRawIntType(refType))
      return IntegerCell.label;
    else if (TypeUtils.isRawLongType(refType))
      return LongCell.label;
    else if (TypeUtils.isRawFloatType(refType))
      return FloatCell.label;
    else
      return Cell.label;
  }

  public static IType conType(IType type) {
    assert TypeUtils.isReferenceType(type);
    IType refType = TypeUtils.referencedType(type);
    if (TypeUtils.isRawBoolType(refType))
      return BoolCell.conType();
    else if (TypeUtils.isRawIntType(refType))
      return IntegerCell.conType();
    else if (TypeUtils.isRawLongType(refType))
      return LongCell.conType();
    else if (TypeUtils.isRawFloatType(refType))
      return FloatCell.conType();
    else
      return Cell.conType();
  }

  public static RefCell cell(IValue el) {
    return new Cell(el);
  }

  public static void declare() {
    Location nullLoc = Location.nullLoc;

    List<IValueSpecifier> specs = new ArrayList<>();

    specs
            .add(new ConstructorSpecifier(nullLoc, null, Cell.label, Cell.conIx, Cell.conType(), Cell.class, RefCell.class));
    specs.add(new ConstructorSpecifier(nullLoc, null, BoolCell.label, BoolCell.conIx, BoolCell.conType(),
            BoolCell.class, RefCell.class));
    specs.add(new ConstructorSpecifier(nullLoc, null, CharCell.label, CharCell.conIx, CharCell.conType(),
            CharCell.class, RefCell.class));
    specs.add(new ConstructorSpecifier(nullLoc, null, IntegerCell.label, IntegerCell.conIx, IntegerCell.conType(),
            IntegerCell.class, RefCell.class));
    specs.add(new ConstructorSpecifier(nullLoc, null, LongCell.label, LongCell.conIx, LongCell.conType(),
            LongCell.class, RefCell.class));
    specs.add(new ConstructorSpecifier(nullLoc, null, FloatCell.label, FloatCell.conIx, FloatCell.conType(),
            FloatCell.class, RefCell.class));

    TypeVar tv = new TypeVar();
    ITypeDescription desc = new CafeTypeDescription(nullLoc, new UniversalType(tv, new TypeExp(typeLabel, tv)),
            RefCell.class.getName(), specs);

    Intrinsics.declare(desc);

    Intrinsics.declare(new GetRawBoolReference());
    Intrinsics.declare(new GetRawCharReference());
    Intrinsics.declare(new GetRawIntegerReference());
    Intrinsics.declare(new GetRawLongReference());
    Intrinsics.declare(new GetRawFloatReference());
    Intrinsics.declare(new GetReference());
    Intrinsics.declare(new RawBoolAssignment());
    Intrinsics.declare(new RawCharAssignment());
    Intrinsics.declare(new RawIntegerAssignment());
    Intrinsics.declare(new RawLongAssignment());
    Intrinsics.declare(new RawFloatAssignment());
    Intrinsics.declare(new Assignment());
  }
}
