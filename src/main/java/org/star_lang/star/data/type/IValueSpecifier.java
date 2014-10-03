package org.star_lang.star.data.type;

import org.star_lang.star.compiler.canonical.IContentExpression;
import org.star_lang.star.compiler.util.PrettyPrintable;

/**
 * An IValueSpecifier is a specification of constructed values. A given type may have more than one
 * IValueSpecifier; however, certain types must have exactly one value specifier: specifically
 * {@link ScalarSpecifier}s may not be mixed with {@link ConstructorSpecifier}s.
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

public interface IValueSpecifier extends PrettyPrintable
{
  /**
   * The label of the value specifier
   * 
   * @return
   */
  String getLabel();

  /**
   * Every constructor has a type. That type is the same type that would be needed were the
   * constructor to be a 'real' function; which of course it is.
   * 
   * @return the type of the function
   */
  IType getConType();

  /**
   * Where the constructor is defined, in some source file
   * 
   * @return
   */
  Location getLoc();

  /**
   * Access the constructor code itself
   * 
   * @return
   */
  IContentExpression source();
}
