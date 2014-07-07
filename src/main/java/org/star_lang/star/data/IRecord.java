package org.star_lang.star.data;

/**
 * The IRecord interface is implemented by values that have the concept of members.
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
   * implementation of each getter for the same name. However, that getter will typically not return
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
