package org.star_lang.star.compiler.type;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.star_lang.star.compiler.canonical.Variable;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.data.type.*;

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

public interface Dictionary extends ITypeContext {
  /**
   * Is name a declared variable in this dictionary
   *
   * @param name
   * @return true if it is.
   */
  boolean isDeclaredVar(String name);

  /**
   * Is the name defined at all in the dictionary
   *
   * @param name
   * @return
   */
  boolean isDefinedVar(String name);

  /**
   * Return recorded information about a named variable.
   *
   * @param name
   * @return a VarInfo object, if it exists, from the dictionary
   */
  DictInfo getVar(String name);

  /**
   * Convenience function to return the type of a variable
   *
   * @param name
   * @return
   */
  IType getVarType(String name);

  /**
   * Access a variable and put it into scope appropriately. This can involve 'lifting' the variable
   * from an outer scope into this one
   *
   * @param name
   * @return
   */
  DictInfo varReference(String name);

  void declareVar(String name, DictInfo var);

  /**
   * Declare a variable with a defined access pattern
   *
   * @param name        the name of the variable being declared
   * @param var         the variable being declared
   * @param access      whether the variable can be updated or not
   * @param visibility  is this variable to be exported from current environment
   * @param initialized does this variable already have a value?
   */
  void declareVar(String name, Variable var, AccessMode access, Visibility visibility, boolean initialized);

  Iterator<DictInfo> iterator();

  boolean isFreeVar(Variable var);

  Variable[] getFreeVars();

  /**
   * Find the kind of a type in the dictionary
   *
   * @param the name of the type in question
   * @return its kind if known
   */
  Kind kindOfType(String name);

  /**
   * Declare the implementation of a type contract
   *
   * @param var          that denotes the implementation
   * @param contractName that is being implemented by this variable
   * @param isDefault    whether this implementation is a default implementation or not
   */
  void declareImplementation(Variable var, String contractName, boolean isDefault);

  Map<String, Set<ContractImplementation>> allImplementations();

  /**
   * Create a new layer to the dictionary. This allows names to be redefined for an inner scope.
   *
   * @return a new dictionary with all the contents of the existing dictionary but primed for an
   * inner scope.
   */
  @Override
  Dictionary fork();

  /**
   * Is a name declared in this dictionary but not in an outer dictionary?
   * <p>
   * This is a test for a non-free variable.
   *
   * @param vName
   * @param outer
   * @return
   */
  boolean isLocallyDeclared(String vName, Dictionary outer);

  /**
   * Is the type variable mentioned in the type of any variable that is in scope.
   *
   * @param var
   * @return
   */
  boolean isTypeVarInScope(TypeVar var);

  /**
   * Dictionaries may be nested to reflect their nested scoping. An outer dictionary represents the
   * outer scope.
   *
   * @return
   */

  Dictionary outerDict();
}
