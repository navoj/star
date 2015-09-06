package org.star_lang.star.data;

import org.star_lang.star.data.type.IType;

/**
 * The {@link IFunction} interface is implemented by code: functions and procedures.
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

public interface IFunction
{
  /**
   * Enter a function and return the result thereof
   * 
   * @param args
   *          the arguments to the function
   * 
   * @return the value returned by the function
   */
  IValue enter(IValue... args) throws EvaluationException;

  /**
   * All functions expose a means of getting their type
   * 
   * @return the type of the function
   */
  IType getType();
}
