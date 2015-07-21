/**
 * 
 */
package org.star_lang.star.compiler.type;

import org.star_lang.star.compiler.canonical.Variable;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.TypeVar;

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
public interface DictInfo extends PrettyPrintable
{
  /**
   * What is the source of this definition
   * 
   * @return
   */
  Location getLoc();

  /**
   * What is the name of this entry?
   * 
   * @return
   */
  String getName();

  /**
   * What is the type of this entry?
   * 
   * @return
   */
  IType getType();

  /**
   * Is the type variable mentioned in the type of this variable.
   * 
   * @param var
   * @return
   */
  boolean isTypeVarInScope(TypeVar var);

  /**
   * Is this entry initialized?
   * 
   * @return true if the variable is initialized
   */
  boolean isInitialized();

  /**
   * What is the access for this entry?
   * 
   * @return readOnly if you cannot assign to this variable
   */
  AccessMode getAccess();

  /**
   * Return the root variable
   * 
   * @return
   */
  Variable getVariable();
}