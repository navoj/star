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
 * The IRecord interface is implemented by values that have the concept of members.
 * 
 *
 * In addition to the generic interface for getting and setting members of records, it is expected
 * that specific getters and setters will also be present. For example, if a record has the field
 * <code>age</code> which is an <code>int</code>, then, in addition to
 * 
 * <pre>
 * getMember(&quot;age&quot;)
 * </pre>
 * 
 * being supported, an implementation would <emph>also</emph> support the method
 * 
 * <pre>
 * int getAge()
 * </pre>
 * 
 * together with the corresponding setter
 * 
 * <pre>
 * void setAge(int age)
 * </pre>
 * 
 * Unfortunately, it is not possible to document this in the Java interface in an enforceable way.
 * 
 * @author fgm
 * 
 */
public interface IRecord extends IConstructor
{
  /**
   * Generic way of getting a member of the record. In many instances there is a type-specific
   * implementation of each getter for the same NAME. However, that getter will typically not return
   * an {@link IValue} &mdash; especially for so-called raw types.
   * 
   * For example, if a record has an integer member for {@code age}, then
   * 
   * <pre>
   * getMember(&quot;age&quot;)
   * </pre>
   * 
   * will return an {@link IValue} object, but the method:
   * 
   * <pre>
   * int getAge()
   * </pre>
   * 
   * will return an {@code int} value.
   * 
   * @param memberName
   * @return the member, if it exists.
   * @throws {@link IllegalArgumentException} if the requested member does not exist for this
   *         specific record.
   */
  IValue getMember(String memberName);

  /**
   * Used to set a field of the record .
   * 
   * As with {@code getMember}, there are likely to be type-specific individualized setters for the
   * members of a record.
   * 
   * @param memberName
   * @param value
   * @throws EvaluationException
   *           when trying to set a member that is not permitted
   */
  void setMember(String memberName, IValue value) throws EvaluationException;

  /**
   * Access all the fields in the record.
   * 
   * @return an array of all the field names.
   */
  String[] getMembers();

  @Override
  IRecord copy() throws EvaluationException;

  @Override
  IRecord shallowCopy() throws EvaluationException;
}
