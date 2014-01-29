package com.starview.platform.data.type;

import java.util.Map;

import org.star_lang.star.compiler.util.PrettyPrintable;

/**
 * The ITypeContext interface provides some simple methods for discovering type definitions.
 * 
 * Copyright (C) 2013 Starview Inc
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
 */
public interface ITypeContext extends PrettyPrintable
{
  /**
   * Return a type description associated with a given type name.
   * 
   * @param name
   *          the name of the type whose description you are looking for
   * @return the {@code ITypeDescription} object. Will be null if the named type is not defined in
   *         the context.
   */
  ITypeDescription getTypeDescription(String name);

  /**
   * Record a new type description. A type context may separate different type descriptions for the
   * same name, so defining a type is not guaranteed to overwrite any existing definition for the
   * same type name.
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
   *          name of the constructor
   * @return true if name is a constructor
   */
  boolean isConstructor(String name);

  /**
   * Get the constructor description associated with name
   * 
   * @param name
   *          of the constructor
   * @return null if name is not the name of a constructor
   */
  IValueSpecifier getConstructor(String name);

  void declareConstructor(ConstructorSpecifier cons);

  /**
   * A Type alias is a way of giving an alternate name to a type. It is convenient to use in cases
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
   * @return the type contract associated with name.
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
