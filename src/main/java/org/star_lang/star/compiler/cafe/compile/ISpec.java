package org.star_lang.star.compiler.cafe.compile;

import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;

/**
 * This interface describes the Cafe type of an entity and the Java-related representation of the
 * entity.
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
public interface ISpec extends PrettyPrintable
{
  /**
   * Return the location associated with this specification
   * 
   * @return a {@link Location}
   */
  Location getLoc();

  /**
   * Get the Star type of the source.
   * 
   * @return
   */
  IType getType();

  /**
   * Get the Java type of the source
   * 
   * @return
   */
  String getJavaType();

  /**
   * Get the Java signature of the source. This is usually equal to L+javaType+;
   * 
   * @return
   */
  String getJavaSig();

  /**
   * Get Java's invocation signature for the source - i.e., what to do to invoke the function
   * 
   * @return
   */
  String getJavaInvokeSig();

  /**
   * Get the java name of the method to invoke
   * 
   * @return
   */
  String getJavaInvokeName();

  /**
   * Return the element that represents this value in the frame state
   * 
   * @return
   */
  Object getFrameCode();

  /**
   * How many slots does this value take?
   * 
   * @return
   */
  int slotSize();
}
