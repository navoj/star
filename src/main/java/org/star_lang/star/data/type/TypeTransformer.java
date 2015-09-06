package org.star_lang.star.data.type;

/*
 * A transformer interface for type expressions and constraints
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
public interface TypeTransformer<T, C, X>
{
  T transformSimpleType(Type t, X cxt);

  /**
   * Apply the transformer to a type expression. This includes most concrete types, including
   * function types, record types, tuple types, etc.
   * 
   * @param t
   * @param cxt
   *          context
   */
  T transformTypeExp(TypeExp t, X cxt);

  /**
   * Transform a tuple type
   * 
   * @param t
   *          the tuple type to transform
   * @param cxt
   * @return
   */
  T transformTupleType(TupleType t, X cxt);

  /**
   * Apply the transformer to an interface type;
   * 
   * @param t
   * @param cxt
   *          context
   */
  T transformTypeInterface(TypeInterfaceType t, X cxt);

  /**
   * Apply the transformer to a type variable. Note that it is guaranteed that the type variable is
   * not bound to a concrete type; but it may have type constraints applied to it.
   * 
   * @param v
   * @param cxt
   *          context
   */
  T transformTypeVar(TypeVar v, X cxt);

  /**
   * Apply the transformer to an existentially quantified type.
   * 
   * @param t
   * @param cxt
   *          context
   */
  T transformExistentialType(ExistentialType t, X cxt);

  /**
   * Apply the transformer to a universally quantified type.
   * 
   * @param t
   * @param cxt
   *          context
   */
  T transformUniversalType(UniversalType t, X cxt);

  /**
   * Constraints are also part of types
   * 
   * @param con
   * @return
   */

  /**
   * Transform a contract constraint
   * 
   * @param con
   * @param cxt
   *          context
   * @return
   */
  C transformContractConstraint(ContractConstraint con, X cxt);

  /**
   * transform a has kind constraint
   * 
   * @param has
   * @param cxt
   *          context
   * @return
   */
  C transformHasKindConstraint(HasKind has, X cxt);

  /**
   * transform an instance of constraint
   * 
   * @param inst
   * @param cxt
   *          context
   * @return
   */
  C transformInstanceOf(InstanceOf inst, X cxt);

  /**
   * Transform a field constraint
   * 
   * @param fc
   * @param cxt
   *          context
   * @return
   */
  C transformFieldConstraint(FieldConstraint fc, X cxt);

  /**
   * transform a type field constraint
   * 
   * @param tc
   * @param cxt
   *          context
   * @return
   */
  C transformFieldTypeConstraint(FieldTypeConstraint tc, X cxt);

  /**
   * transform a tuple constraint
   * 
   * @param t
   * @param cxt
   *          context
   * @return
   */
  C transformTupleContraint(TupleConstraint t, X cxt);
}
