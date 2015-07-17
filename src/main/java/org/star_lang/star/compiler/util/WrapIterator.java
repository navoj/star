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

import java.util.Iterator;
import java.util.function.Function;

/**
 * Created by fgm on 7/14/15.
 */
public class WrapIterator<A, T> implements Iterator<T> {
  private final Function<A, T> wrap;
  private final Iterator<A> orig;

  public WrapIterator(Function<A, T> wrap, Iterator<A> orig) {
    this.wrap = wrap;
    this.orig = orig;
  }

  @Override
  public boolean hasNext() {
    return orig.hasNext();
  }

  @Override
  public T next() {
    return wrap.apply(orig.next());
  }

  @Override
  public void remove() {
    orig.remove();
  }
}
