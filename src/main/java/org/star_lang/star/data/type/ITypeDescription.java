package org.star_lang.star.data.type;

import org.star_lang.star.compiler.type.Dictionary;
import org.star_lang.star.compiler.util.PrettyPrintable;

/**
 * An ITypeDescription is the programmer's key to accessing the meta-level aspects of an IValue. It
 * contains enough information to permit introspection and to permit the Factory to create
 * appropriate instances of the type.
 */

/*
 * Copyright (c) 2015. Francis G. McCabe
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
public interface ITypeDescription extends PrettyPrintable
{
  /**
   * The name of this type. Note that a type expression may involve type variables, so a type's name
   * is not sufficient information to construct instances of a type expression.
   * 
   * @return the name of the type
   */
  String getName();

  /**
   * Where was this type defined?
   * 
   * @return
   */
  Location getLoc();

  /**
   * A type may be generic, in which case the {@code typeArity} of the type will be greater than
   * zero.
   * 
   * @return the number of type arguments type expressions of this type should have.
   */
  int typeArity();

  /**
   * Compute a generalized version of the type
   * 
   * @return
   */
  IType getType();

  /**
   * Return what kind of type this is
   * 
   * @return
   */
  Kind kind();

  /**
   * Verify that a given type expression is consistent with this description
   * 
   * @param type
   * @param loc
   * @param dict
   */
  IType verifyType(IType type, Location loc, Dictionary dict) throws TypeConstraintException;
}
