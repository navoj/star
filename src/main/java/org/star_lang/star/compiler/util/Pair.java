package org.star_lang.star.compiler.util;

import java.util.Map.Entry;

/**
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * <p>
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA
 *
 * @author fgm
 */
@SuppressWarnings("serial")
public class Pair<S, T> implements PrettyPrintable, Entry<S, T> {
  public final S left;
  public final T right;

  public Pair(S lft, T rgt) {
    this.left = lft;
    this.right = rgt;
  }

  public S left() {
    return left;
  }

  public T right() {
    return right;
  }

  public static <S, T> Pair<S, T> pair(S lft, T rgt) {
    return new Pair<>(lft, rgt);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp) {
    disp.append("<");
    showTerm(disp, left);
    disp.append(", ");
    showTerm(disp, right);
    disp.append(">");
  }

  private <X> void showTerm(PrettyPrintDisplay disp, X el) {
    if (el instanceof PrettyPrintable)
      ((PrettyPrintable) el).prettyPrint(disp);
    else if (el != null)
      disp.append(el.toString());
    else
      disp.append("(null)");
  }

  @Override
  public String toString() {
    return PrettyPrintDisplay.toString(this);
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Pair<?, ?>) {
      Pair<S, T> oPair = (Pair<S, T>) obj;

      return left.equals(oPair.left) && right.equals(oPair.right);
    } else
      return false;
  }

  @Override
  public int hashCode() {
    return left.hashCode() * 43 + right.hashCode();
  }

  @Override
  public S getKey() {
    return left;
  }

  @Override
  public T getValue() {
    return right;
  }

  @Override
  public T setValue(T value) {
    throw new UnsupportedOperationException("read only");
  }
}
