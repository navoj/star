package com.starview.platform.data.type;

import org.star_lang.star.compiler.type.TypeUtils;

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
public class Type extends AbstractType
{
  public Type(String name, Kind kind)
  {
    super(name, kind);
  }

  public Type(String name, int arity)
  {
    this(name, Kind.kind(arity));
  }

  public Type(String name)
  {
    this(name, Kind.type);
  }

  public int getArity()
  {
    return kind().arity();
  }

  @Override
  public <C> void accept(ITypeVisitor<C> visitor, C cxt)
  {
    visitor.visitSimpleType(this, cxt);
  }

  @Override
  public <T, C, X> T transform(TypeTransformer<T, C, X> trans, X cxt)
  {
    return trans.transformSimpleType(this, cxt);
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof IType) {
      IType itype = TypeUtils.deRef((IType) obj);
      if (itype instanceof Type) {
        Type type = (Type) itype;

        return type.typeLabel().equals(typeLabel()) && type.kind().equals(kind());
      }
    }
    return false;
  }

  @Override
  public int hashCode()
  {
    return typeLabel().hashCode();
  }
}
