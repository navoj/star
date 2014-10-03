package org.star_lang.star.compiler.canonical;

import java.util.Collection;

import org.star_lang.star.compiler.type.Visibility;

/**
 * A statement in a theta environment
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
public interface IStatement extends Canonical
{
  /**
   * Test to see if this statement is 'about' a given name
   * 
   * @param name
   * @return true if the statement is a definition for the name
   */
  boolean defines(String name);

  /**
   * Return a list of all the defined names. You cannot infer any type information from this list.
   * 
   * @return a Collection<String> of the defined names.
   */
  Collection<String> definedFields();

  /**
   * Return a list of defined types.
   * 
   * @return a Collection<String> of type names;
   */
  Collection<String> definedTypes();

  /**
   * Does this statement define something that should not be exported from the theta environment
   * that the statement is in?
   */
  Visibility getVisibility();

  /**
   * Allow a transformer to transform this definition
   * 
   * @param transform
   * @param context
   * @return the transformed entity. Might not result in a statement, depends on the transform
   */
  <A, E, P, C, D, T> D transform(TransformStatement<A, E, P, C, D, T> transform, T context);
}
