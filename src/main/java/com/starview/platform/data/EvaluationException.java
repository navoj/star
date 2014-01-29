package com.starview.platform.data;
/**
 *   Copyright (C) 2013 Starview Inc
 *
 *   This library is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Lesser General Public
 *   License as published by the Free Software Foundation; either
 *   version 2.1 of the License, or (at your option) any later version.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the Free Software
 *   Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *   
 * @author fgm
 *
 */

import java.util.ArrayList;
import java.util.List;

import org.star_lang.star.compiler.cafe.compile.Utils;
import org.star_lang.star.compiler.cafe.type.CafeTypeDescription;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;

import com.starview.platform.data.type.*;
import com.starview.platform.data.value.Factory;
import com.starview.platform.data.value.QuoteDisplay;
import com.starview.platform.data.value.StringWrap;

public class EvaluationException extends Exception implements IConstructor, PrettyPrintable
{
  private static final long serialVersionUID = 1L;

  private final Location loc;
  private final IValue code;
  private final IValue raise;

  public static final String name = "exception";
  public static final IType type = TypeUtils.typeExp(name);

  public static void declare(ITypeContext cxt)
  {
    ConstructorSpecifier exSpec = new ConstructorSpecifier(Location.nullLoc, null, name, 0, conType(),
        EvaluationException.class, EvaluationException.class);

    List<IValueSpecifier> specs = new ArrayList<IValueSpecifier>();
    specs.add(exSpec);
    ITypeDescription desc = new CafeTypeDescription(Location.nullLoc, type, Utils
        .javaInternalClassName(EvaluationException.class), specs);
    cxt.defineType(desc);
  }

  public EvaluationException(IValue code, IValue datum, Location loc)
  {
    super(datum.toString());
    this.loc = loc;
    this.code = code;
    this.raise = datum;
  }

  public EvaluationException(IValue code, IValue datum, IValue loc)
  {
    this(code, datum, (Location) loc);
  }

  public EvaluationException(IValue datum)
  {
    this(StringWrap.nonStringEnum, datum, Location.nullLoc);
  }

  public EvaluationException(String msg, Location loc)
  {
    this(StringWrap.nonStringEnum, Factory.newString(msg), loc);
  }

  public EvaluationException(String msg)
  {
    this(msg, Location.nullLoc);
  }

  public EvaluationException(String msg, Throwable throwable)
  {
    super(msg, throwable);
    this.loc = null;
    this.code = StringWrap.nonStringEnum;
    this.raise = Factory.newString(msg);
  }

  public Location getLoc()
  {
    return loc;
  }

  public IValue getCode()
  {
    return code;
  }

  public IValue getDatum()
  {
    return raise;
  }

  public IValue get___0()
  {
    return code;
  }

  public IValue get___1()
  {
    return raise;
  }

  public IValue get___2()
  {
    return loc;
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

  private static final int CODE = 0;
  private static final int RAISE = 1;
  private static final int LOC = 2;

  @Override
  public void accept(IValueVisitor visitor)
  {
    visitor.visitConstructor(this);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    QuoteDisplay.display(disp, code);
    disp.append(":");
    QuoteDisplay.display(disp, raise);
    if (loc != null && loc != Location.noWhereEnum) {
      disp.append("@");
      loc.prettyPrint(disp);
    }
  }

  @Override
  public int conIx()
  {
    return 0;
  }

  @Override
  public String getLabel()
  {
    return name;
  }

  @Override
  public int size()
  {
    return 3;
  }

  @Override
  public IValue getCell(int index)
  {
    switch (index) {
    case LOC:
      return loc;
    case CODE:
      return code;
    case RAISE:
      return raise;
    default:
      throw new IndexOutOfBoundsException();
    }
  }

  @Override
  public IValue[] getCells()
  {
    IValue[] data = new IValue[size()];
    data[LOC] = loc;
    data[CODE] = code;
    data[RAISE] = raise;
    return data;
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
  public IType getType()
  {
    return type;
  }

  public static IType conType()
  {
    return TypeUtils.tupleConstructorType(StandardTypes.stringType, StandardTypes.anyType, StandardTypes.locationType,
        type);
  }

  @Override
  public IConstructor shallowCopy() throws EvaluationException
  {
    return this;
  }

  @Override
  public int hashCode()
  {
    return (name.hashCode() * 37 + code.hashCode()) * 37 + raise.hashCode();
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
      return true;
    else if (obj instanceof EvaluationException) {
      EvaluationException ex = (EvaluationException) obj;
      return ex.code.equals(code) && ex.raise.equals(raise);
    } else
      return false;
  }

}
