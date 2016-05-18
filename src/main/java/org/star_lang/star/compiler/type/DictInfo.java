package org.star_lang.star.compiler.type;

import org.star_lang.star.compiler.canonical.Variable;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.TypeVar;

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

public interface DictInfo extends PrettyPrintable
{
  /**
   * What is the source of this definition
   * 
   * @return
   */
  Location getLoc();

  /**
   * What is the NAME of this entry?
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