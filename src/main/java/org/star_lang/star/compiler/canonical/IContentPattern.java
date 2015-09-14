package org.star_lang.star.compiler.canonical;

import org.star_lang.star.data.type.IType;

public interface IContentPattern extends Canonical
{
  /**
   * The type of the pattern. Technically, the type of values that this pattern matches.
   * 
   * @return
   */
  IType getType();

  /**
   * Allow a transformer to transform this pattern
   * 
   * @param transform
   * @return the transformed entity. May not be a pattern, depends on the transform
   */
  <A, E, P, C, D, T> P transformPattern(TransformPattern<A, E, P, C, D, T> transform, T context);
}
