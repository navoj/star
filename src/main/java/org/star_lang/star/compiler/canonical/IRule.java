package org.star_lang.star.compiler.canonical;

/**
 * The IRule interface is a marker for rule-like objects
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
 * 
 * @param <T>
 *          encodes the kind of rule it is
 */

public interface IRule<T> extends Canonical
{

  /**
   * The argument patterns associated with the rule.
   * 
   * @return the argument patterns
   */
  IContentPattern[] getArgs();

  /**
   * Version specific entity that returns the right-hand side expression or action or whatever.
   * 
   * @return the replacement body of the rule
   */
  T getBody();
}
