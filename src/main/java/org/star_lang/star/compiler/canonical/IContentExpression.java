package org.star_lang.star.compiler.canonical;

import org.star_lang.star.data.type.IType;

public interface IContentExpression extends Canonical
{

  /**
   * Expressions have a type associated with them. This returns the type of the expression.
   * 
   * @return an expression denoting the type of the expression.
   */

  IType getType();

  /**
   * Allow a transformer to transform this expression
   * 
   * @param transform
   * @return the transformed entity. May not be an expression, depends on the transform
   */
  <A, E, P, C, D, T> E transform(TransformExpression<A, E, P, C, D, T> transform, T context);
}
