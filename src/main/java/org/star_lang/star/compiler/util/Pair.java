package org.star_lang.star.compiler.util;

import java.util.Map.Entry;

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
