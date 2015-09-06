package org.star_lang.star.data;
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

/**
 * The {@code IConstructor} interface is implemented by so-called constructor functions defined in
 * algebraic data-types.
 * 
 * @author fgm
 * 
 */

public interface IConstructor extends IValue
{
  /**
   * Every constructor has an index that denotes a code within an algebraic data type
   * 
   * @return
   */
  int conIx();

  /**
   * Get the label that identifies which constructor was used to build this value
   * 
   * @return a string label
   */
  String getLabel();

  /**
   * See how many elements are in the constructor
   * 
   * @return the number of elements in the constructor. A return value of 0 indicates an enumerated
   *         symbol.
   */
  int size();

  /**
   * Return the value corresponding to a given index
   * 
   * @param index
   * @return the nth argument of the constructor.
   */
  IValue getCell(int index);

  /**
   * Return an array of all values
   * 
   * @return the array of arguments to the constructor
   */
  IValue[] getCells();

  /**
   * Set the cell at a specific position to a new value. Should NEVER be null.
   * 
   * @param index
   *          the offset of the value to update
   * @param value
   *          the new value of the constructor argument.
   * @throws EvaluationException
   *           TODO
   */
  void setCell(int index, IValue value) throws EvaluationException;

  /**
   * When constructors are copied, the copy is also an IConstructor
   */
  @Override
  IConstructor copy() throws EvaluationException;

  @Override
  IConstructor shallowCopy() throws EvaluationException;
}
