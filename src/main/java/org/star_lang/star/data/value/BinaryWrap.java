package org.star_lang.star.data.value;

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
import org.star_lang.star.data.type.TypeDescription;

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
@SuppressWarnings("serial")
public abstract class BinaryWrap implements IValue, PrettyPrintable
{
  public static final NonBinaryWrapper nonBinaryEnum = new NonBinaryWrapper();

  @Override
  public IType getType()
  {
    return StandardTypes.binaryType;
  }

  public static void declare(ITypeContext cxt)
  {
    IType binaryType = TypeUtils.typeExp(StandardTypes.BINARY);
    IType rawBinaryType = TypeUtils.typeExp(StandardTypes.RAW_BINARY);
    IType conType = TypeUtils.tupleConstructorType(rawBinaryType, binaryType);
    ConstructorSpecifier strSpec = new ConstructorSpecifier(Location.nullLoc, null, StandardTypes.BINARY, 0, conType,
        BinaryWrapper.class, BinaryWrap.class);
    ConstructorSpecifier nonSpec = new ConstructorSpecifier(Location.nullLoc, null, StandardTypes.NON_BINARY, 1,
        TypeUtils.constructorType(binaryType), NonBinaryWrapper.class, BinaryWrap.class);

    List<IValueSpecifier> specs = new ArrayList<>();
    specs.add(strSpec);
    specs.add(nonSpec);
    ITypeDescription type = new CafeTypeDescription(Location.nullLoc, binaryType, Utils
        .javaInternalClassName(BinaryWrap.class), specs);
    cxt.defineType(type);
    cxt.defineType(new TypeDescription(rawBinaryType));
  }

  public static class BinaryWrapper extends BinaryWrap implements IScalar<Object>, IConstructor
  {
    private final Object obj;

    public BinaryWrapper(Object str)
    {
      this.obj = str;
      assert str != null;
    }

    // This is here to allow Cafe to access the value
    public Object get___0()
    {
      return obj;
    }

    @Override
    public void accept(IValueVisitor visitor)
    {
      visitor.visitScalar(this);
    }

    @Override
    public Object getValue()
    {
      return obj;
    }

    @Override
    public void prettyPrint(PrettyPrintDisplay disp)
    {
      disp.append(obj.toString());
    }

    @Override
    public String toString()
    {
      return obj.toString();
    }

    @Override
    public boolean equals(Object obj)
    {
      if (obj instanceof BinaryWrapper) {
        BinaryWrapper other = (BinaryWrapper) obj;
        return obj.equals(other.obj);
      }
      return false;
    }

    @Override
    public int hashCode()
    {
      return obj.hashCode();
    }

    @Override
    public IConstructor copy()
    {
      return this;
    }

    @Override
    public IConstructor shallowCopy() throws EvaluationException
    {
      return this;
    }

    @Override
    public int conIx()
    {
      return 0;
    }

    @Override
    public String getLabel()
    {
      return StandardTypes.BINARY;
    }

    @Override
    public int size()
    {
      return 1; // No user-adjustable parts inside
    }

    @Override
    public IValue getCell(int index)
    {
      throw new UnsupportedOperationException("not permitted");
    }

    @Override
    public IValue[] getCells()
    {
      throw new UnsupportedOperationException("not permitted");
    }

    @Override
    public void setCell(int index, IValue value) throws EvaluationException
    {
      throw new UnsupportedOperationException("not permitted");
    }
  }

  public final static class NonBinaryWrapper extends BinaryWrap implements IConstructor
  {

    @Override
    public IType getType()
    {
      return StandardTypes.binaryType;
    }

    @Override
    public void accept(IValueVisitor visitor)
    {
      visitor.visitConstructor(this);
    }

    @Override
    public int conIx()
    {
      return 1;
    }

    @Override
    public String getLabel()
    {
      return StandardTypes.NON_BINARY;
    }

    @Override
    public void prettyPrint(PrettyPrintDisplay disp)
    {
      disp.appendWord(getLabel());
    }

    @Override
    public int size()
    {
      return 0;
    }

    @Override
    public IValue getCell(int index)
    {
      throw new UnsupportedOperationException("not permitted");
    }

    @Override
    public IValue[] getCells()
    {
      throw new UnsupportedOperationException("not permitted");
    }

    @Override
    public void setCell(int index, IValue value) throws EvaluationException
    {
      throw new UnsupportedOperationException("not permitted");
    }

    @Override
    public IConstructor copy() throws EvaluationException
    {
      return this;
    }

    @Override
    public IConstructor shallowCopy() throws EvaluationException
    {
      return this;
    }
  }
}
