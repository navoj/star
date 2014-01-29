package org.star_lang.star.data.indextree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Stack;

import org.star_lang.star.compiler.util.NullIterator;
import org.star_lang.star.compiler.util.Pair;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;

/*
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
public abstract class IndexTree<K, V> implements Mapping<K, V>
{
  protected final int mask;
  protected final short maskLen;
  protected static final int MSB = ~(-1 >>> 1);

  protected static final boolean SHOW_HEX = false;

  public IndexTree(short maskLen, int mask)
  {
    this.mask = mask;
    this.maskLen = maskLen;
  }

  public int getMask()
  {
    return mask;
  }

  public short getMaskLen()
  {
    return maskLen;
  }

  protected static boolean nthBit(int mask, int pos)
  {
    int nth = MSB >>> pos;
    return (mask & nth) == nth;
  }

  protected static int commonMaskLen(int H1, int H2)
  {
    int C = 32;
    while (H1 != H2 && C > 0) {
      H1 = H1 >>> 1;
      H2 = H2 >>> 1;
      C--;
    }
    return C;
  }

  protected static int common2WayMaskLen(int H1, int H2)
  {
    int C = 32;
    while (H1 != H2 && C > 0) {
      H1 = H1 >>> 2;
      H2 = H2 >>> 2;
      C -= 2;
    }
    return C;
  }

  // construct a 2 bit number from a specified position in the mask
  protected static int nth4Way(int mask, int pos)
  {
    mask = mask >>> (30 - pos);
    return mask & 3;
  }

  protected static int maskPrefix(int mask, int len)
  {
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
  public V find(K key)
  {
    return find(key, key.hashCode());
  }

  abstract protected V find(K key, int hash);

  @Override
  public boolean contains(K key)
  {
    return contains(key, key.hashCode());
  }

  abstract protected boolean contains(K key, int hash);

  @Override
  public IndexTree<K, V> insrt(K key, V value)
  {
    return mergeTree(new IndexTreeLeaf<>(key.hashCode(), key, value));
  }

  abstract protected IndexTree<K, V> mergeTree(IndexTree<K, V> other);

  @Override
  public IndexTree<K, V> delete(K key)
  {
    return delete(key, key.hashCode());
  }

  abstract protected IndexTree<K, V> delete(K key, int hash);

  public static <K, V> IndexTree<K, V> emptyTree()
  {
    return new IndexTreeLeaf<K, V>(-1, new ArrayList<Pair<K, V>>());
  }

  @Override
  abstract public boolean isEmpty();

  @Override
  abstract public int size();

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

  @Override
  public Iterator<Entry<K, V>> iterator()
  {
    return new Iterator<Entry<K, V>>() {
      Iterator<Entry<K, V>> leafIterator = new NullIterator<>();

      final Stack<IndexTree<K, V>> stack = new Stack<>();

      {
        stack.push(IndexTree.this);
        probeNext();
      }

      @Override
      public boolean hasNext()
      {
        return leafIterator.hasNext() || !stack.isEmpty();
      }

      private void probeNext()
      {
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
      public Entry<K, V> next()
      {
        Entry<K, V> next = leafIterator.next();
        probeNext();
        return next;
      }

      @Override
      public void remove()
      {
        throw new UnsupportedOperationException("not permitted");
      }
    };
  }
  
  public Iterator<Entry<K, V>> reverseIterator()
  {
    return new Iterator<Entry<K, V>>() {
      Iterator<Entry<K, V>> leafIterator = new NullIterator<>();

      final Stack<IndexTree<K, V>> stack = new Stack<>();

      {
        stack.push(IndexTree.this);
        probeNext();
      }

      @Override
      public boolean hasNext()
      {
        return leafIterator.hasNext() || !stack.isEmpty();
      }

      private void probeNext()
      {
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
      public Entry<K, V> next()
      {
        Entry<K, V> next = leafIterator.next();
        probeNext();
        return next;
      }

      @Override
      public void remove()
      {
        throw new UnsupportedOperationException("not permitted");
      }
    };
  }
  
}