package org.star_lang.star.compiler.util;

/**
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
 * @author fgm
 *
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
    return new Triple<S, M, T>(lft, mdl, rgt);
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
