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
 * @author fgm
 *
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
