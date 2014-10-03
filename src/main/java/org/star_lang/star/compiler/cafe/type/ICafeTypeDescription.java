package org.star_lang.star.compiler.cafe.type;

import java.util.List;
import java.util.SortedMap;

import org.star_lang.star.compiler.cafe.compile.ISpec;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.IValueSpecifier;
import org.star_lang.star.data.type.TypeConstraintException;

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

public interface ICafeTypeDescription
{
  String getJavaName();

  String getJavaSig();

  IValueSpecifier declareConstructor(String name, IType conType, int conIx, String javaTypeName, String javaOwner,
      String javaInvokeSig, String javaSafeName) throws TypeConstraintException;

  IValueSpecifier declareConstructor(String name, IType conType, int conIx, ISpec spec, String javaTypeName,
      String javaOwner, String javaConSig, String javaSafeName, List<ISpec> fields, SortedMap<String, Integer> index)
      throws TypeConstraintException;
}
