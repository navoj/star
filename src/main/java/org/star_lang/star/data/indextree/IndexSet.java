package org.star_lang.star.data.indextree;

import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.compiler.util.WrapIterator;

import java.util.Iterator;
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
public class IndexSet<T> implements Sets<T>, PrettyPrintable {
  private final IndexTree<T, Object> tree;

  public IndexSet() {
    this.tree = IndexTree.emptyTree();
  }

  public static <T> IndexSet<T> emptySet() {
    return new IndexSet<>();
  }

  private IndexSet(IndexTree<T, Object> tree) {
    this.tree = tree;
  }

  public IndexSet<T> add(T el) {
    if (!tree.contains(el))
      return new IndexSet<>(tree.insrt(el, el));
    else
      return this;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp) {
    String sep = "";
    disp.append("[");
    for (Entry<T, Object> e : tree) {
      disp.append(sep);
      sep = ", ";
      T el = e.getKey();
      if (el instanceof PrettyPrintable)
        ((PrettyPrintable) el).prettyPrint(disp);
      else
        disp.append(el.toString());
    }
    disp.append("]");
  }

  @Override
  public String toString() {
    return PrettyPrintDisplay.toString(this);
  }

  @Override
  public boolean isEmpty() {
    return tree.isEmpty();
  }

  @Override
  public int size() {
    return tree.size();
  }

  @Override
  public boolean contains(T el) {
    return tree.contains(el);
  }

  @Override
  public IndexSet<T> insert(T key) {
    return add(key);
  }

  @Override
  public Sets<T> delete(T key) {
    return new IndexSet<>(tree.delete(key));
  }

  @Override
  public <S> S fold(Fold<T, S> folder, S init) {
    S result = init;
    for (Entry<T, Object> entry : tree) {
      result = folder.apply(entry.getKey(), result);
    }
    return result;
  }

  @Override
  public Iterator<T> iterator() {
    return new WrapIterator<>(Entry::getKey, tree.iterator());
  }

  @Override
  public Iterator<T> reverseIterator() {
    return new WrapIterator<>(Entry::getKey, tree.reverseIterator());
  }

  @Override
  public T pick() {
    Entry<T, ?> entry = tree.pick();
    return entry.getKey();
  }

  @Override
  public Sets<T> remaining() {
    return new IndexSet<>(tree.remaining());
  }
}
