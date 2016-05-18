package org.star_lang.star.data.type;

import java.util.Map;

import org.star_lang.star.compiler.util.PrettyPrintable;

/**
 * The ITypeContext interface provides some simple methods for discovering type definitions.
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
public interface ITypeContext extends PrettyPrintable
{
  /**
   * Return a type description associated with a given type NAME.
   * 
   * @param name
   *          the NAME of the type whose description you are looking for
   * @return the {@code ITypeDescription} object. Will be null if the named type is not defined in
   *         the context.
   */
  ITypeDescription getTypeDescription(String name);

  /**
   * Record a new type description. A type context may separate different type descriptions for the
   * same NAME, so defining a type is not guaranteed to overwrite any existing definition for the
   * same type NAME.
   * 
   * @param desc
   *          what kind of type is this
   */
  void defineType(ITypeDescription desc);

  /**
   * Just check to see if a type exists or not. May be a type variable.
   * 
   * @param name
   *          does this type exist
   * @return whether the type exists
   */
  boolean typeExists(String name);

  /**
   * All the types defined in this interface
   * 
   * @return all types
   */
  Map<String, ITypeDescription> getAllTypes();

  /**
   * We have to be able to determine what the constructors are
   * 
   * @param name
   *          NAME of the constructor
   * @return true if NAME is a constructor
   */
  boolean isConstructor(String name);

  /**
   * Get the constructor description associated with NAME
   * 
   * @param name
   *          of the constructor
   * @return null if NAME is not the NAME of a constructor
   */
  IValueSpecifier getConstructor(String name);

  void declareConstructor(ConstructorSpecifier cons);

  /**
   * A Type alias is a way of giving an alternate NAME to a type. It is convenient to use in cases
   * such as {@code xmlType} which is really a {@code string} but you want to honor the distinction
   * 
   * @param loc
   *          where is the source of this definition
   * @param alias
   *          being defined
   */
  void defineTypeAlias(Location loc, ITypeAlias alias);

  /**
   * Create a new layer to the type context. This allows names to be redefined for an inner scope.
   * 
   * @return a new type context with all the contents of the existing context but primed for an
   *         inner scope.
   */
  ITypeContext fork();

  /**
   * Access a type contract description. A type contract is similar to a type but denotes a
   * constraint on types rather than a specific type.
   * 
   * @param name
   *          of the contract
   * @return the type contract associated with NAME.
   */
  TypeContract getContract(String name);

  /**
   * record a type contract in the context.
   * 
   * @param contract
   *          which contract
   */
  void defineTypeContract(TypeContract contract);

  /** Return all the available type contracts */
  Map<String, TypeContract> allContracts();
}
