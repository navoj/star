package org.star_lang.star.data.indextree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Stack;

import org.star_lang.star.compiler.util.NullIterator;
import org.star_lang.star.compiler.util.Pair;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;

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
public abstract class IndexTree<K, V> implements Mapping<K, V>, Pick<Entry<K, V>> {
  protected final int mask;
  protected final short maskLen;
  protected static final int MSB = ~(-1 >>> 1);

  protected static final boolean SHOW_HEX = false;

  public IndexTree(short maskLen, int mask) {
    this.mask = mask;
    this.maskLen = maskLen;
  }

  protected static int commonMaskLen(int H1, int H2) {
    int C = 32;
    while (H1 != H2 && C > 0) {
      H1 = H1 >>> 1;
      H2 = H2 >>> 1;
      C--;
    }
    return C;
  }

  protected static int common2WayMaskLen(int H1, int H2) {
    int C = 32;
    while (H1 != H2 && C > 0) {
      H1 = H1 >>> 2;
      H2 = H2 >>> 2;
      C -= 2;
    }
    return C;
  }

  // construct a 2 bit number from a specified position in the mask
  protected static int nth4Way(int mask, int pos) {
    mask = mask >>> (30 - pos);
    return mask & 3;
  }

  protected static int maskPrefix(int mask, int len) {
    if (len == 0)
      return 0;
    else {
      int cml = 32 - len;
      int msb = -1 >>> cml;
      int lhsMask = msb << cml;
      return lhsMask & mask;
    }
  }

  @Override
  public V find(K key) {
    return find(key, key.hashCode());
  }

  abstract protected V find(K key, int hash);

  @Override
  public boolean contains(K key) {
    return contains(key, key.hashCode());
  }

  abstract protected boolean contains(K key, int hash);

  @Override
  public IndexTree<K, V> insrt(K key, V value) {
    return mergeTree(new IndexTreeLeaf<>(key.hashCode(), key, value));
  }

  abstract protected IndexTree<K, V> mergeTree(IndexTree<K, V> other);

  @Override
  public IndexTree<K, V> delete(K key) {
    return delete(key, key.hashCode());
  }

  abstract protected IndexTree<K, V> delete(K key, int hash);

  public static <K, V> IndexTree<K, V> emptyTree() {
    return new IndexTreeLeaf<>(-1, new ArrayList<>());
  }

  @Override
  abstract public boolean isEmpty();

  @Override
  abstract public int size();

  @Override
  public String toString() {
    return PrettyPrintDisplay.toString(this);
  }

  @Override
  public Iterator<Entry<K, V>> iterator() {
    return new Iterator<Entry<K, V>>() {
      Iterator<Entry<K, V>> leafIterator = new NullIterator<>();

      final Stack<IndexTree<K, V>> stack = new Stack<>();

      {
        stack.push(IndexTree.this);
        probeNext();
      }

      @Override
      public boolean hasNext() {
        return leafIterator.hasNext() || !stack.isEmpty();
      }

      private void probeNext() {
        while (!leafIterator.hasNext() && !stack.isEmpty()) {
          IndexTree<K, V> top = stack.pop();
          if (top instanceof IndexTreeNode<?, ?>) {
            IndexTreeNode<K, V> topNode = (IndexTreeNode<K, V>) top;
            stack.push(topNode.getR2());
            stack.push(topNode.getR1());
            stack.push(topNode.getL2());
            stack.push(topNode.getL1());
          } else {
            assert top instanceof IndexTreeLeaf<?, ?>;
            leafIterator = top.iterator();
          }
        }
      }

      @Override
      public Entry<K, V> next() {
        Entry<K, V> next = leafIterator.next();
        probeNext();
        return next;
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException("not permitted");
      }
    };
  }

  public Iterator<Entry<K, V>> reverseIterator() {
    return new Iterator<Entry<K, V>>() {
      Iterator<Entry<K, V>> leafIterator = new NullIterator<>();

      final Stack<IndexTree<K, V>> stack = new Stack<>();

      {
        stack.push(IndexTree.this);
        probeNext();
      }

      @Override
      public boolean hasNext() {
        return leafIterator.hasNext() || !stack.isEmpty();
      }

      private void probeNext() {
        while (!leafIterator.hasNext() && !stack.isEmpty()) {
          IndexTree<K, V> top = stack.pop();
          if (top instanceof IndexTreeNode<?, ?>) {
            IndexTreeNode<K, V> topNode = (IndexTreeNode<K, V>) top;
            stack.push(topNode.getL1());
            stack.push(topNode.getL2());
            stack.push(topNode.getR1());
            stack.push(topNode.getR2());
          } else {
            assert top instanceof IndexTreeLeaf<?, ?>;
            leafIterator = top.reverseIterator();
          }
        }
      }

      @Override
      public Entry<K, V> next() {
        Entry<K, V> next = leafIterator.next();
        probeNext();
        return next;
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException("not permitted");
      }
    };
  }

  @Override
  public IndexTree<K, V> remaining() {
    if (!isEmpty()) {
      Entry<K, V> el = pick();
      return delete(el.getKey());
    } else
      return null;
  }
}