package org.star_lang.star.data;

import org.star_lang.star.data.type.IType;

/**
 * The {@link IPattern} interface is implemented by pattern code: pattern abstractions
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

public interface IPattern
{
  /**
   * Enter a pattern and return the result as a tuple of extracted values.
   * 
   * @param arg
   *          the value that the pattern will match against
   * 
   * @return null if the pattern fails, otherwise a tuple of IValues representing the extracted
   *         values
   */
  IValue match(IValue arg) throws EvaluationException;

  /**
   * All programs expose a means of getting their type
   * 
   * @return
   */
  IType getType();
}
