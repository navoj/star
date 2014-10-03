package org.star_lang.star.compiler.cafe.type;

import org.star_lang.star.data.type.IType;

/**
 * Encapsulate cafe specific elements of the constructor value specifiers
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
public interface ICafeConstructorSpecifier
{
  String getLabel();
  
  String getTypeLabel();

  int getConIx();

  Class<?> getCafeClass();

  void setCafeClass(Class<?> klass);

  String getJavaType();

  boolean hasMember(String id);

  IType getConType();

  String memberName(int ix);

  ICafeConstructorSpecifier cleanCopy();
}
