package org.star_lang.star.compiler.ast;

import org.star_lang.star.compiler.type.TypeUtils;

import com.starview.platform.data.EvaluationException;
import com.starview.platform.data.IConstructor;
import com.starview.platform.data.IValue;
import com.starview.platform.data.type.ConstructorSpecifier;
import com.starview.platform.data.type.IType;
import com.starview.platform.data.type.Location;
import com.starview.platform.data.type.StandardTypes;
import com.starview.platform.data.value.Factory;

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

@SuppressWarnings("serial")
public class StringLiteral extends Literal
{
  public static final String name = "stringAst";

  private static final int locIndex = 0;
  private static final int strIndex = 1;
  private static final IType conType = TypeUtils.tupleConstructorType(Location.type, TypeUtils
      .typeExp(StandardTypes.STRING), ASyntax.type);

  private final String str;

  public StringLiteral(Location loc, String str)
  {
    super(loc);
    this.str = str;
  }

  public StringLiteral(IValue loc, IValue val) throws EvaluationException
  {
    super((Location) loc);
    this.str = Factory.stringValue(val);
  }

  @Override
  public astType astType()
  {
    return astType.Str;
  }

  public static ConstructorSpecifier spec()
  {
    return new ConstructorSpecifier(Location.nullLoc, null, name, stringIx, conType, StringLiteral.class, ASyntax.class);
  }

  @Override
  public void accept(IAbstractVisitor visitor)
  {
    visitor.visitStringLiteral(this);
  }

  @Override
  public String getLit()
  {
    return str;
  }

  @Override
  public int conIx()
  {
    return stringIx;
  }

  @Override
  public String getLabel()
  {
    return name;
  }

  @Override
  public int size()
  {
    return 2;
  }

  @Override
  public IValue getCell(int index)
  {
    switch (index) {
    case locIndex:
      return getLoc();
    case strIndex:
      return Factory.newString(str);
    default:
      throw new IllegalArgumentException("index out of range");
    }
  }

  public IValue get___1()
  {
    return getCell(strIndex);
  }

  @Override
  public IValue[] getCells()
  {
    return new IValue[] { getLoc(), Factory.newString(str) };
  }

  @Override
  public IConstructor shallowCopy() throws EvaluationException
  {
    return new StringLiteral(getLoc(), str);
  }

  public static IType conType()
  {
    return conType;
  }

  @Override
  public boolean equals(Object obj)
  {
    return obj instanceof StringLiteral && ((StringLiteral) obj).getLit().equals(getLit());
  }

  @Override
  public int hashCode()
  {
    return getLit().hashCode();
  }
}
