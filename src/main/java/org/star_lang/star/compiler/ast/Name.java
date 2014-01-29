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
public class Name extends ASyntax
{
  public static final String name = "nameAst";

  private static final int locIndex = 0;
  private static final int nameIndex = 1;
  private static final IType conType = TypeUtils.tupleConstructorType(Location.type, TypeUtils
      .typeExp(StandardTypes.STRING), ASyntax.type);

  private final String id;

  public Name(Location loc, String id)
  {
    super(loc);
    assert id != null;
    this.id = id;
  }

  public Name(IValue loc, IValue id) throws EvaluationException
  {
    super((Location) loc);
    this.id = Factory.stringValue(id);
  }

  @Override
  public astType astType()
  {
    return astType.Name;
  }

  public static ConstructorSpecifier spec()
  {
    return new ConstructorSpecifier(Location.nullLoc, null, name, nameIx, conType, Name.class, ASyntax.class);
  }

  public String getId()
  {
    return id;
  }

  @Override
  public int conIx()
  {
    return nameIx;
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
    case nameIndex:
      return Factory.newString(getId());
    default:
      throw new IllegalArgumentException("index out of range");
    }
  }

  public IValue get___1()
  {
    return getCell(nameIndex);
  }

  @Override
  public IValue[] getCells()
  {
    return new IValue[] { getLoc(), Factory.newString(getId()) };
  }

  @Override
  public IConstructor shallowCopy() throws EvaluationException
  {
    return new Name(getLoc(), id);
  }

  public static IType conType()
  {
    return conType;
  }

  @Override
  public boolean isIdentifier(String name)
  {
    return this.id.equals(name);
  }

  @Override
  public boolean equals(Object obj)
  {
    return (obj == this) || (obj instanceof Name && ((Name) obj).id.equals(id));
  }

  @Override
  public int hashCode()
  {
    return id.hashCode();
  }

  @Override
  public void accept(IAbstractVisitor visitor)
  {
    visitor.visitName(this);
  }

  @Override
  public boolean isApply(String op)
  {
    return false;
  }

  @Override
  public boolean isBinaryOperator(String op)
  {
    return false;
  }

  @Override
  public boolean isTernaryOperator(String op)
  {
    return false;
  }

  @Override
  public boolean isUnaryOperator(String op)
  {
    return false;
  }
}
