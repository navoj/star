package org.star_lang.star.data;

import java.io.Serializable;

import org.star_lang.star.data.type.IType;
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
 * An object that holds a specific piece of data that is being passed through the system. By
 * default, IValues should be immutable once their construction has been completed.
 * 
 * Values are not normally processed explicitly as IValues by the StarRules/Cafe code generator.
 * This is because the Cafe compiler has better knowledge of generated code than this interface
 * offers.
 * 
 */
public interface IValue extends Serializable
{
  /**
   * Return the type of this value. The value that is returned can be used &mdash; together with the
   * type description &mdash; to introspect the value.
   * 
   * @return the IType
   */
  IType getType();

  /**
   * Construct a deep copy of the value
   * 
   * @return a complete copy of the value
   * @throws EvaluationException
   */
  IValue copy() throws EvaluationException;

  /**
   * Construct a shallow copy of the value. A shallow copy guarantees that a top-level change will
   * not affect the original, but a deeper change might.
   * 
   * @return a shallow copy.
   */
  IValue shallowCopy() throws EvaluationException;

  /**
   * Part of the {@link IValueVisitor} visitor pattern. All the {@link IValue} interfaces implement
   * this interface.
   * 
   * The appropriate implementation of {@code accept} is a spring back to the appropriate method in
   * the visitor.
   * 
   * For example, to implement the {@code accept} for a relation value do:
   * 
   * <pre>
   * void accept(IValueVisitor visitor)
   * {
   *   visitor.visitRelation(this);
   * }
   * </pre>
   * 
   * @param visitor
   *          the visitor that is being used.
   * 
   */
  void accept(IValueVisitor visitor);

  /**
   * It is not permitted for ISV implementations to implicitly rely on the Object implementation of
   * equals.
   * 
   * @param rhs
   *          The object to which to compare <code>this</code>.
   */
  @Override
  boolean equals(Object rhs);

  /**
   * Return the hash code of the value. Each implementation of ISV is not permitted to use the
   * default Object implementation of hashCode;
   * 
   * @return the hash code of the value
   */
  @Override
  int hashCode();
}
