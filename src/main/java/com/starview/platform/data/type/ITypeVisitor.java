package com.starview.platform.data.type;

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
 * @param <C>
 */
public interface ITypeVisitor<C>
{
  /**
   * Apply visitor to a 'simple type'.
   * 
   * @param t
   * @param cxt
   *          visitor specific context
   */
  void visitSimpleType(Type t, C cxt);

  /**
   * Apply the visitor to a type expression. This includes most concrete types, including function
   * types, record types, tuple types, etc.
   * 
   * @param t
   * @param cxt
   *          visitor specific context
   */
  void visitTypeExp(TypeExp t, C cxt);

  /**
   * Apply the visitor to an interface type;
   * 
   * @param t
   * @param cxt
   *          visitor specific context
   */
  void visitTypeInterface(TypeInterfaceType t, C cxt);

  /**
   * Apply the visitor to a type variable. Note that it is guaranteed that the type variable is not
   * bound to a concrete type; but it may have type constraints applied to it.
   * 
   * @param v
   * @param cxt
   *          visitor specific context
   */
  void visitTypeVar(TypeVar v, C cxt);

  /**
   * Apply the visitor to an existentially quantified type.
   * 
   * @param t
   * @param cxt
   *          visitor specific context
   */
  void visitExistentialType(ExistentialType t, C cxt);

  /**
   * Apply the visitor to a universally quantified type.
   * 
   * @param t
   * @param cxt
   *          visitor specific context
   */
  void visitUniversalType(UniversalType t, C cxt);
}
