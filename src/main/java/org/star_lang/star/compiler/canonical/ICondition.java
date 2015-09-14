package org.star_lang.star.compiler.canonical;

/**
 * An ICondition represents one of the conditions in the content language; for example &lsquo;X in
 * R&rsquo; is a condition.
 * 
 * The evaluation of conditions is based on <i>satisfaction</i> semantics: i.e., looking for a
 * <i>binding</i> of any variables embedded within patterns within the condition that satisfies the
 * condition &mdash; i.e., makes the condition true. In general there may be many ways of binding
 * variables to satisfy the condition; resulting in many potential solutions.
 * 
 * A condition is always governed by another element of the content language that acts as a kind of
 * receptacle for the solutions found. For example, in:
 * 
 * <pre>
 * all X where X in R
 * </pre>
 * 
 * the fragment
 * 
 * <pre>
 * X in R
 * </pre>
 * 
 * is a condition &mdash; actually a Predication. The different ways that the predication may be
 * solved are captured by the form:
 * 
 * <pre>
 * all X where ...
 * </pre>
 * 
 * which collects the solutions into a Relation value &mdash; the value of the query expression
 * itself.
 * 
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

public interface ICondition extends Canonical
{
  /**
   * Allow a transformer to transform this condition
   * 
   * @param transform
   * @return the transformed entity. Might not result in a condition, depends on the transform
   */
  <A, E, P, C, D, T> C transform(TransformCondition<A, E, P, C, D, T> transform, T context);
}
