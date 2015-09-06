package org.star_lang.star.data.type;

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
public interface ITypeVisitor<C>
{
  /**
   * Apply visitor to a 'simple type'.
   * 
   * @param t
   * @param cxt
   *          visitor specific context
   */
  void visitSimpleType(Type t, C cxt);

  /**
   * Apply the visitor to a type expression. This includes most concrete types, including function
   * types, record types, tuple types, etc.
   * 
   * @param t
   * @param cxt
   *          visitor specific context
   */
  void visitTypeExp(TypeExp t, C cxt);

  /**
   * Apply visitor to a tuple type
   * 
   * @param t
   * @param cxt
   */
  void visitTupleType(TupleType t, C cxt);

  /**
   * Apply the visitor to an interface type;
   * 
   * @param t
   * @param cxt
   *          visitor specific context
   */
  void visitTypeInterface(TypeInterfaceType t, C cxt);

  /**
   * Apply the visitor to a type variable. Note that it is guaranteed that the type variable is not
   * bound to a concrete type; but it may have type constraints applied to it.
   * 
   * @param v
   * @param cxt
   *          visitor specific context
   */
  void visitTypeVar(TypeVar v, C cxt);

  /**
   * Apply the visitor to an existentially quantified type.
   * 
   * @param t
   * @param cxt
   *          visitor specific context
   */
  void visitExistentialType(ExistentialType t, C cxt);

  /**
   * Apply the visitor to a universally quantified type.
   * 
   * @param t
   * @param cxt
   *          visitor specific context
   */
  void visitUniversalType(UniversalType t, C cxt);
}
