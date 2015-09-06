package org.star_lang.star.compiler.util;

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
@SuppressWarnings("serial")
public class Triple<S, M, T> implements PrettyPrintable
{
  public final S left;
  public final M middle;
  public final T right;

  public Triple(S lft, M mdl, T rgt)
  {
    this.left = lft;
    this.middle = mdl;
    this.right = rgt;
  }

  public S left()
  {
    return left;
  }

  public M middle()
  {
    return middle;
  }

  public T right()
  {
    return right;
  }

  public static <S, M, T> Triple<S, M, T> create(S lft, M mdl, T rgt)
  {
    return new Triple<>(lft, mdl, rgt);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append("<");
    if (left instanceof PrettyPrintable)
      ((PrettyPrintable) left).prettyPrint(disp);
    else if (left != null)
      disp.append(left.toString());
    else
      disp.append("(null)");
    disp.append(", ");
    if (middle instanceof PrettyPrintable)
      ((PrettyPrintable) middle).prettyPrint(disp);
    else if (middle != null)
      disp.append(middle.toString());
    else
      disp.append("(null)");
    disp.append(", ");
    if (right instanceof PrettyPrintable)
      ((PrettyPrintable) right).prettyPrint(disp);
    else if (right != null)
      disp.append(right.toString());
    else
      disp.append("(null)");
    disp.append(">");
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof Triple<?, ?, ?>) {
      Triple<S, M, T> oPair = (Triple<S, M, T>) obj;

      return left.equals(oPair.left) && middle.equals(oPair.middle) && right.equals(oPair.right);
    } else
      return false;
  }

  @Override
  public int hashCode()
  {
    return (left.hashCode() * 43 + middle.hashCode()) * 43 + right.hashCode();
  }
}
