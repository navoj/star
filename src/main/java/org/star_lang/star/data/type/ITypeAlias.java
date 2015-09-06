package org.star_lang.star.data.type;

import org.star_lang.star.compiler.type.Dictionary;

/**
 * A TypeAlias is a mapping between type expressions. It permits a programmer to specify type
 * aliases of the form:
 * 
 * type llist of %t is alias of list of list of %t
 * 
 * A TypeAlias can be viewed as the definition of an existentially quantified type.
 *
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
public interface ITypeAlias extends ITypeDescription
{
  /**
   * Apply the alias to a potential type expression to acquire the aliased type expression.
   * 
   * @param pattern
   *          the type to test against the alias
   * @param loc
   *          where the alias is being applied
   * @param cxt
   *          the context of the aliasing
   * @return the aliased type
   * @throws TypeConstraintException
   *           if the pattern does not apply.
   */
  IType apply(IType pattern, Location loc, Dictionary cxt) throws TypeConstraintException;
}
