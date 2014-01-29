package com.starview.platform.data.type;

import org.star_lang.star.compiler.type.DisplayType;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.ListUtils;

/**
 * An explicit named type expression. If the expression has any type arguments then it denotes an
 * instance of a generic type.
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
public class TypeExp implements IType
{
  private final IType typeArgs[];
  private final IType typeCon;

  public TypeExp(IType typeCon, IType... args)
  {
    this.typeCon = typeCon;
    this.typeArgs = args;

    assert ListUtils.assertNoNulls(args);
  }

  /**
   * The regular way of constructing a new type constructor expression. The TyCon encompasses both
   * of simple non-generic types and generic type expressions.
   * 
   * @param label
   *          the name of the type
   * @param args
   *          a varargs array of types that form the type arguments of the type expression.
   */
  public TypeExp(String label, IType... args)
  {
    this(new Type(label, Kind.kind(args.length)), args);
  }

  @Override
  public Kind kind()
  {
    return Kind.type;
  }

  @Override
  public String typeLabel()
  {
    return typeCon.typeLabel();
  }

  @Override
  public <X> void accept(ITypeVisitor<X> visitor, X cxt)
  {
    visitor.visitTypeExp(this, cxt);
  }

  @Override
  public <T, C, X> T transform(TypeTransformer<T, C, X> trans, X cxt)
  {
    return trans.transformTypeExp(this, cxt);
  }

  /**
   * The type arity corresponds to the genericity of the type expression -- how many type arguments
   * it has.
   * 
   * @return the number of type arguments of this type expression
   */
  public int typeArity()
  {
    return typeArgs.length;
  }

  /**
   * Find a type argument from the type expression.
   * 
   * @param ix
   *          the index of the type argument desired. Must satisfy 0<=ix && ix<typeArity()
   * @return the ixth type argument of this type expression
   */
  public IType getTypeArg(int ix)
  {
    return typeArgs[ix];
  }

  /**
   * Get all of the type arguments as a single array of types.
   * 
   * @return an array of types
   */
  public IType[] getTypeArgs()
  {
    return typeArgs;
  }

  public IType getTypeCon()
  {
    return typeCon;
  }

  @Override
  public String toString()
  {
    return DisplayType.toString(this);
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o) // Shortcut eval
      return true;

    if (o instanceof IType) {
      IType itype = TypeUtils.deRef((IType) o);
      if (itype instanceof TypeExp) {
        TypeExp other = (TypeExp) itype;
        if (getTypeCon().equals(other.getTypeCon()) && typeArgs.length == other.typeArgs.length) {
          for (int ix = 0; ix < typeArgs.length; ix++) {
            if (!typeArgs[ix].equals(other.typeArgs[ix]))
              return false;
          }

          return true;
        }
        return false;
      }
    }
    return false;
  }

  @Override
  public int hashCode()
  {
    int result = getTypeCon().hashCode();
    for (int i = 0; i < typeArgs.length; i++) {
      result *= 31;
      result += typeArgs[i].hashCode();
    }
    return result;
  }
}
