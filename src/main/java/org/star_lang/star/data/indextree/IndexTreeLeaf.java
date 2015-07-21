package org.star_lang.star.data.indextree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.star_lang.star.compiler.util.Pair;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.compiler.util.Singleton;

/* 
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
 */
@SuppressWarnings("serial")
public class IndexTreeLeaf<K, V> extends IndexTree<K, V>
{
  private final List<Pair<K, V>> leafs;

  public IndexTreeLeaf(int mask, List<Pair<K, V>> leafs)
  {
    super((short) 32, mask);
    this.leafs = leafs;
  }

  public IndexTreeLeaf(int mask, K key, V value)
  {
    super((short) 32, mask);
    this.leafs = new Singleton<>(Pair.pair(key, value));
  }

  @Override
  protected V find(K key, int hash)
  {
    if (hash == mask) {
      for (Pair<K, V> p : leafs) {
        if (p.left.equals(key))
          return p.right;
      }
    }
    return null;
  }

  @Override
  protected boolean contains(K key, int hash)
  {
    if (hash == mask) {
      for (Pair<K, V> p : leafs)
        if (p.left.equals(key))
          return true;
    }
    return false;
  }

  @Override
  protected IndexTree<K, V> mergeTree(IndexTree<K, V> other)
  {
    if (other instanceof IndexTreeLeaf)
      return mergeLeafs((IndexTreeLeaf<K, V>) other);
    else
      return other.mergeTree(this);
  }

  protected IndexTree<K, V> mergeLeafs(IndexTreeLeaf<K, V> otherLeaf)
  {
    if (otherLeaf.mask == mask) {
      List<Pair<K, V>> newList = new ArrayList<>();
      outer: for (Pair<K, V> thisP : leafs) {
        for (Pair<K, V> otherP : otherLeaf.leafs) {
          if (thisP.left.equals(otherP.left)) {
            continue outer;
          }
        }
        newList.add(thisP);
      }
      newList.addAll(otherLeaf.leafs);
      return new IndexTreeLeaf<>(mask, newList);
    } else if (otherLeaf.isEmpty())
      return this;
    else if (isEmpty())
      return otherLeaf;
    else {
      int cml = common2WayMaskLen(mask, otherLeaf.mask);
      int cm = maskPrefix(mask, cml);
      IndexTree<K, V> empty = IndexTree.emptyTree();

      assert cml < 32;

      IndexTree<K, V> L1 = empty;
      IndexTree<K, V> L2 = empty;
      IndexTree<K, V> R1 = empty;
      IndexTree<K, V> R2 = empty;

      switch (nth4Way(mask, cml)) {
      case 0:
        L1 = this;
        break;
      case 1:
        L2 = this;
        break;
      case 2:
        R1 = this;
        break;
      case 3:
        R2 = this;
        break;
      default:
        throw new IllegalStateException();
      }

      switch (nth4Way(otherLeaf.mask, cml)) {
      case 0:
        assert L1 == empty;
        L1 = otherLeaf;
        break;
      case 1:
        assert L2 == empty;
        L2 = otherLeaf;
        break;
      case 2:
        assert R1 == empty;
        R1 = otherLeaf;
        break;
      case 3:
        assert R2 == empty;
        R2 = otherLeaf;
        break;
      default:
        throw new IllegalStateException();
      }

      return new IndexTreeNode<>((short) cml, cm, L1, L2, R1, R2);
    }
  }

  @Override
  protected IndexTree<K, V> delete(K key, int hash)
  {
    if (this.mask == hash) {
      for (int cx = 0; cx < leafs.size(); cx++) {
        Pair<K, V> entry = leafs.get(cx);
        if (entry.left().equals(key))
          return new IndexTreeLeaf<>(this.mask, removeIx(leafs, cx));
      }
      return this;
    } else
      return this;
  }

  private static <K, V> List<Pair<K, V>> removeIx(List<Pair<K, V>> list, int offset)
  {
    List<Pair<K, V>> newList = new ArrayList<>();
    for (int ix = 0; ix < list.size(); ix++)
      if (ix != offset)
        newList.add(list.get(ix));
    return newList;
  }

  @Override
  public boolean isEmpty()
  {
    return leafs.isEmpty();
  }

  @Override
  public int size()
  {
    return leafs.size();
  }

  @Override
  public Iterator<Entry<K, V>> iterator()
  {
    return new Iterator<Entry<K, V>>() {
      Iterator<Pair<K, V>> it = leafs.iterator();

      @Override
      public boolean hasNext()
      {
        return it.hasNext();
      }

      @Override
      public Entry<K, V> next()
      {
        return it.next();
      }

      @Override
      public void remove()
      {
        throw new UnsupportedOperationException();
      }
    };
  }

  @Override
  public Iterator<Entry<K, V>> reverseIterator()
  {
    return new Iterator<Entry<K, V>>() {
      int pos = leafs.size();

      @Override
      public boolean hasNext()
      {
        return pos > 0;
      }

      @Override
      public Entry<K, V> next()
      {
        return leafs.get(--pos);
      }

      @Override
      public void remove()
      {
        throw new UnsupportedOperationException();
      }
    };
  }

  @Override
  public <S> S fold(Fold<Entry<K, V>, S> folder, S state)
  {
    for (Entry<K, V> entry : leafs)
      state = folder.apply(entry, state);
    return state;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    for (Pair<K, V> p : leafs) {
      K key = p.left();
      if (key instanceof PrettyPrintable)
        ((PrettyPrintable) key).prettyPrint(disp);
      else
        disp.append(key.toString());

      disp.append("->");

      V val = p.right();
      if (val instanceof PrettyPrintable)
        ((PrettyPrintable) val).prettyPrint(disp);
      else
        disp.append(val.toString());

      disp.append(";\n");
    }
  }

  @Override
  public int hashCode()
  {
    int hash = 0;
    for (Pair<K, V> p : leafs)
      hash = hash * 37 + p.hashCode();

    return hash;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
      return true;
    if (obj instanceof IndexTreeLeaf) {
      @SuppressWarnings("unchecked")
      IndexTreeLeaf<K, V> other = (IndexTreeLeaf<K, V>) obj;
      if (other.leafs.size() == leafs.size()) {
        leafLoop: for (Pair<K, V> p : leafs) {
          for (Pair<K, V> o : other.leafs) {
            if (p.equals(o))
              continue leafLoop;
          }
          return false;
        }
        return true;
      }
    }
    return false;
  }
}
