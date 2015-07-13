package org.star_lang.star.data.value;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.star_lang.star.compiler.cafe.compile.Utils;
import org.star_lang.star.compiler.cafe.type.CafeTypeDescription;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IConstructor;
import org.star_lang.star.data.IScalar;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.IValueVisitor;
import org.star_lang.star.data.type.ConstructorSpecifier;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.ITypeContext;
import org.star_lang.star.data.type.ITypeDescription;
import org.star_lang.star.data.type.IValueSpecifier;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;

/**
 * Big decimal values
 * <p/>
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA
 *
 * @author fgm
 */

@SuppressWarnings("serial")
public abstract class BigNumWrap implements IValue, PrettyPrintable {
  public final static class BigNumWrapper extends BigNumWrap implements IScalar<BigDecimal>, IConstructor {
    private final BigDecimal ix;

    public BigNumWrapper(BigDecimal ix) {
      this.ix = ix;
    }

    @Override
    public BigDecimal getValue() {
      return ix;
    }

    @Override
    public int conIx() {
      return 0;
    }

    @Override
    public String getLabel() {
      return StandardTypes.DECIMAL;
    }

    public BigDecimal get___0() {
      return ix;
    }

    @Override
    public int size() {
      return 0; // No user-adjustable parts inside
    }

    @Override
    public void accept(IValueVisitor visitor) {
      visitor.visitScalar(this);
    }

    @Override
    public IValue getCell(int index) {
      throw new UnsupportedOperationException("not permitted");
    }

    @Override
    public IValue[] getCells() {
      throw new UnsupportedOperationException("not permitted");
    }

    @Override
    public void setCell(int index, IValue value) throws EvaluationException {
      throw new UnsupportedOperationException("not permitted");
    }

    @Override
    public BigNumWrapper copy() {
      return this;
    }

    @Override
    public BigNumWrapper shallowCopy() throws EvaluationException {
      return this;
    }

    @Override
    public String toString() {
      return ix.toString();
    }

    @Override
    public void prettyPrint(PrettyPrintDisplay disp) {
      disp.append(ix.toString());
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof BigNumWrapper) {
        BigNumWrapper other = (BigNumWrapper) obj;
        return this.getValue().equals(other.getValue());
      }
      return false;
    }

    @Override
    public int hashCode() {
      return this.getValue().hashCode();
    }

    public static IType conType() {
      return TypeUtils.tupleConstructorType(StandardTypes.rawDecimalType, StandardTypes.decimalType);
    }
  }

  public final static class NonDecimalWrapper extends BigNumWrap implements IConstructor {
    @Override
    public void accept(IValueVisitor visitor) {
      visitor.visitConstructor(this);
    }

    @Override
    public int conIx() {
      return 1;
    }

    @Override
    public String getLabel() {
      return StandardTypes.NON_DECIMAL;
    }

    @Override
    public int size() {
      return 0;
    }

    @Override
    public IValue getCell(int index) {
      throw new UnsupportedOperationException("not permitted");
    }

    @Override
    public IValue[] getCells() {
      throw new UnsupportedOperationException("not permitted");
    }

    @Override
    public void setCell(int index, IValue value) throws EvaluationException {
      throw new UnsupportedOperationException("not permitted");
    }

    @Override
    public void prettyPrint(PrettyPrintDisplay disp) {
      disp.appendWord(StandardTypes.NON_DECIMAL);
    }

    @Override
    public IConstructor copy() throws EvaluationException {
      return this;
    }

    @Override
    public IConstructor shallowCopy() throws EvaluationException {
      return this;
    }

    public static IType conType() {
      return TypeUtils.constructorType(StandardTypes.decimalType);
    }
  }

  @Override
  public IType getType() {
    return StandardTypes.decimalType;
  }

  public static void declare(ITypeContext cxt) {
    IType numberType = TypeUtils.typeExp(StandardTypes.DECIMAL);
    IType conType = TypeUtils.tupleConstructorType(TypeUtils.typeExp(StandardTypes.RAW_DECIMAL), numberType);
    ConstructorSpecifier decSpec = new ConstructorSpecifier(Location.nullLoc, null, StandardTypes.DECIMAL, 0, conType,
            BigNumWrapper.class, BigNumWrap.class);
    List<IValueSpecifier> specs = new ArrayList<>();
    specs.add(decSpec);
    ITypeDescription type = new CafeTypeDescription(Location.nullLoc, numberType, Utils
            .javaInternalClassName(BigNumWrap.class), specs);
    cxt.defineType(type);
  }

}
