package org.star_lang.star.data.indextree;

import java.util.Map.Entry;

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
public class IndexTreeNode<K, V> extends IndexTree<K, V>
{
  private final IndexTree<K, V> l1, l2, r1, r2;

  protected IndexTreeNode(short maskLen, int mask, IndexTree<K, V> l1, IndexTree<K, V> l2, IndexTree<K, V> r1,
      IndexTree<K, V> r2)
  {
    super(maskLen, mask);
    this.l1 = l1;
    this.l2 = l2;
    this.r1 = r1;
    this.r2 = r2;
  }

  public IndexTree<K, V> getL1()
  {
    return l1;
  }

  public IndexTree<K, V> getL2()
  {
    return l2;
  }

  public IndexTree<K, V> getR1()
  {
    return r1;
  }

  public IndexTree<K, V> getR2()
  {
    return r2;
  }

  @Override
  protected V find(K key, int hash)
  {
    int commonMask = maskPrefix(hash, maskLen);

    if (commonMask == mask) {
      switch (nth4Way(hash, maskLen)) {
      case 0:
        return l1.find(key, hash);
      case 1:
        return l2.find(key, hash);
      case 2:
        return r1.find(key, hash);
      case 3:
        return r2.find(key, hash);
      default:
        throw new IllegalStateException();
      }
    } else
      return null;
  }

  @Override
  protected boolean contains(K key, int hash)
  {
    int commonMask = maskPrefix(hash, maskLen);

    if (commonMask == mask) {
      switch (nth4Way(hash, maskLen)) {
      case 0:
        return l1.contains(key, hash);
      case 1:
        return l2.contains(key, hash);
      case 2:
        return r1.contains(key, hash);
      case 3:
        return r2.contains(key, hash);
      default:
        throw new IllegalStateException();
      }
    } else
      return false;
  }

  @Override
  protected IndexTree<K, V> mergeTree(IndexTree<K, V> other)
  {
    int otherMask = other.mask;
    int cml = Math.min(maskLen, common2WayMaskLen(mask, otherMask));
    int cm = maskPrefix(mask, cml);

    if (cml < maskLen) { // common mask is shorter than this node's mask
      IndexTree<K, V> empty = IndexTree.emptyTree();

      switch (nth4Way(otherMask, cml)) {
      case 0:
        switch (nth4Way(mask, cml)) {
        case 1:
          return new IndexTreeNode<>((short) cml, cm, other, this, empty, empty);
        case 2:
          return new IndexTreeNode<>((short) cml, cm, other, empty, this, empty);
        case 3:
          return new IndexTreeNode<>((short) cml, cm, other, empty, empty, this);
        default:
          throw new IllegalStateException();
        }
      case 1:
        switch (nth4Way(mask, cml)) {
        case 0:
          return new IndexTreeNode<>((short) cml, cm, this, other, empty, empty);
        case 2:
          return new IndexTreeNode<>((short) cml, cm, empty, other, this, empty);
        case 3:
          return new IndexTreeNode<>((short) cml, cm, empty, other, empty, this);
        default:
          throw new IllegalStateException();
        }
      case 2:
        switch (nth4Way(mask, cml)) {
        case 0:
          return new IndexTreeNode<>((short) cml, cm, this, empty, other, empty);
        case 1:
          return new IndexTreeNode<>((short) cml, cm, empty, this, other, empty);
        case 3:
          return new IndexTreeNode<>((short) cml, cm, empty, empty, other, this);
        default:
          throw new IllegalStateException();
        }
      case 3:
        switch (nth4Way(mask, cml)) {
        case 0:
          return new IndexTreeNode<>((short) cml, cm, this, empty, empty, other);
        case 1:
          return new IndexTreeNode<>((short) cml, cm, empty, this, empty, other);
        case 2:
          return new IndexTreeNode<>((short) cml, cm, empty, empty, this, other);
        default:
          throw new IllegalStateException();
        }
      default:
        throw new IllegalStateException();
      }
    } else if (other instanceof IndexTreeLeaf) {
      switch (nth4Way(otherMask, cml)) {
      case 0:
        return new IndexTreeNode<>((short) cml, cm, l1.mergeTree(other), l2, r1, r2);
      case 1:
        return new IndexTreeNode<>((short) cml, cm, l1, l2.mergeTree(other), r1, r2);
      case 2:
        return new IndexTreeNode<>((short) cml, cm, l1, l2, r1.mergeTree(other), r2);
      case 3:
        return new IndexTreeNode<>((short) cml, cm, l1, l2, r1, r2.mergeTree(other));
      default:
        throw new IllegalStateException();
      }
    } else {
      assert other instanceof IndexTreeNode;
      IndexTreeNode<K, V> otherNode = (IndexTreeNode<K, V>) other;

      return new IndexTreeNode<>((short) cml, cm, l1.mergeTree(otherNode.l1), l2.mergeTree(otherNode.l2), r1
              .mergeTree(otherNode.r1), r2.mergeTree(otherNode.r2));
    }
  }

  @Override
  protected IndexTree<K, V> delete(K key, int hash)
  {
    int cm = maskPrefix(hash, maskLen);
    if (cm == mask) {
      IndexTree<K, V> NL1 = l1;
      IndexTree<K, V> NL2 = l2;
      IndexTree<K, V> NR1 = r1;
      IndexTree<K, V> NR2 = r2;

      switch (nth4Way(hash, maskLen)) {
      case 0:
        NL1 = l1.delete(key, hash);
        break;
      case 1:
        NL2 = l2.delete(key, hash);
        break;
      case 2:
        NR1 = r1.delete(key, hash);
        break;
      case 3:
        NR2 = r2.delete(key, hash);
        break;
      }

      IndexTree<K, V> NE = NL1;

      if (NE.isEmpty())
        NE = NL2;
      else if (!NL2.isEmpty())
        return new IndexTreeNode<>(maskLen, mask, NL1, NL2, NR1, NR2);

      if (NE.isEmpty())
        NE = NR1;
      else if (!NR1.isEmpty())
        return new IndexTreeNode<>(maskLen, mask, NL1, NL2, NR1, NR2);
      if (NE.isEmpty())
        return NR2;
      else
        return new IndexTreeNode<>(maskLen, mask, NL1, NL2, NR1, NR2);
    } else
      return this; // not present
  }

  @Override
  public boolean isEmpty()
  {
    return false;
  }

  @Override
  public int size()
  {
    return l1.size() + l2.size() + r1.size() + r2.size();
  }

  @Override
  public <S> S fold(Fold<Entry<K, V>, S> folder, S init)
  {
    return r2.fold(folder, r1.fold(folder, l2.fold(folder, l1.fold(folder, init))));
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    if (SHOW_HEX) {
      disp.append(Integer.toHexString(mask));
      disp.append(":");
      disp.append(maskLen);
      disp.append("=");
    }

    l1.prettyPrint(disp);
    l2.prettyPrint(disp);
    r1.prettyPrint(disp);
    r2.prettyPrint(disp);
  }

  @Override
  public int hashCode()
  {
    return 37 * (37 * (37 * l1.hashCode() + l2.hashCode()) + r1.hashCode()) + r2.hashCode();
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
      return true;
    else if (obj instanceof IndexTreeNode) {
      @SuppressWarnings("unchecked")
      IndexTreeNode<K, V> other = (IndexTreeNode<K, V>) obj;
      return l1.equals(other.l1) && l2.equals(other.l2) && r1.equals(other.r1) && r2.equals(other.r2);
    } else
      return false;
  }

}