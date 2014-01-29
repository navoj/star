package org.star_lang.star.compiler.util;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Stack;

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
public class Bag<E> extends AbstractCollection<E> implements PrettyPrintable
{
  private final Comparator<E> compare;
  private Node<E> tree = null;

  public Bag(Comparator<E> compare)
  {
    this.compare = compare;
  }

  public Bag(Bag<E> proto)
  {
    this.compare = proto.compare;
    this.tree = replicate(null, proto.tree);
  }

  private enum RedBlack {
    red, black
  }

  private static class Node<E> implements Serializable
  {
    RedBlack color;
    Node<E> left;
    E item;
    Node<E> right;
    Node<E> parent;

    Node(Node<E> parent, Node<E> left, E item, Node<E> right, RedBlack color)
    {
      this.left = left;
      this.item = item;
      this.right = right;
      this.parent = parent;
    }
  }

  private static <E> Node<E> grandParent(Node<E> tree)
  {
    if (tree != null && tree.parent != null)
      return tree.parent.parent;
    else
      return null;
  }

  private static <E> Node<E> uncle(Node<E> tree)
  {
    Node<E> gp = grandParent(tree);

    if (gp == null)
      return null;
    else if (tree.parent == gp.left)
      return gp.right;
    else
      return gp.left;
  }

  @SuppressWarnings("unused")
  private static <E> void insertAdjust(Node<E> tree)
  {
    assert tree.color == RedBlack.red;

    if (tree.parent == null)
      tree.color = RedBlack.black;
    else if (tree.parent.color == RedBlack.black)
      return;
    else {
      Node<E> u = uncle(tree);
      Node<E> g = grandParent(tree);

      if (u != null && u.color == RedBlack.red) {
        tree.parent.color = RedBlack.black;
        u.color = RedBlack.black;
        g.color = RedBlack.red;
        insertAdjust(g);
      } else {
        if (tree == tree.parent.right && tree.parent == g.left) {
          rotateLeft(tree.parent);
          tree = tree.left;
        } else if (tree == tree.parent.left && tree.parent == g.right) {
          rotateRight(tree.parent);
          tree = tree.right;
        }
        g = grandParent(tree);
        tree.parent.color = RedBlack.black;
        g.color = RedBlack.red;
        if (tree == tree.parent.left && tree.parent == g.left)
          rotateRight(g);
        else
          rotateLeft(g);
      }
    }
  }

  private static <E> void rotateRight(Node<E> n)
  {
    Node<E> left = n.left;
    Node<E> parent = n.parent;

    n.left = left.right;
    if (n.left != null)
      n.left.parent = n;

    left.right = n;
    left.right.parent = n;

    n.parent = left;
    left.parent = parent;
    if (parent != null) {
      if (parent.left == n)
        parent.left = left;
      else
        parent.right = left;
    }
  }

  private static <E> void rotateLeft(Node<E> n)
  {
    Node<E> right = n.right;
    Node<E> parent = n.parent;

    n.right = right.left;
    if (n.right != null)
      n.right.parent = n;

    right.left = n;
    n.parent = right;

    right.parent = parent;
    if (parent != null) {
      if (parent.left == n)
        parent.left = right;
      else
        parent.right = right;
    }
  }

  private static <E> Node<E> sibling(Node<E> n)
  {
    if (n == n.parent.left)
      return n.parent.right;
    else
      return n.parent.left;
  }

  @SuppressWarnings("unused")
  private static <E> void deleteCase1(Node<E> n)
  {
    if (n.parent != null) {
      Node<E> s = sibling(n);
      if (s.color == RedBlack.red) {
        n.parent.color = RedBlack.red;
        s.color = RedBlack.black;
        if (n == n.parent.left)
          rotateLeft(n.parent);
        else
          rotateRight(n.parent);
      }
      s = sibling(n);
      if (n.parent.color == RedBlack.black && s.color == RedBlack.black
          && s.left.color == RedBlack.black && s.right.color == RedBlack.black) {
        s.color = RedBlack.red;
        deleteCase1(n.parent);
      } else {
        s = sibling(n);
        if (n.parent.color == RedBlack.red && s.color == RedBlack.black
            && s.left.color == RedBlack.black && s.right.color == RedBlack.black) {
          s.color = RedBlack.red;
          n.parent.color = RedBlack.black;
        } else {
          if (s.color == RedBlack.black) {
            if (n == n.parent.left && s.right.color == RedBlack.black
                && s.left.color == RedBlack.red) {
              s.color = RedBlack.red;
              s.left.color = RedBlack.black;
              rotateRight(s);
            } else if (n == n.parent.right && s.left.color == RedBlack.black
                && s.right.color == RedBlack.red) {
              s.color = RedBlack.red;
              s.right.color = RedBlack.black;
              rotateLeft(s);
            }
          }
          s = sibling(n);
          s.color = n.parent.color;
          n.parent.color = RedBlack.black;

          if (n == n.parent.left) {
            s.right.color = RedBlack.black;
            rotateLeft(n.parent);
          } else {
            s.left.color = RedBlack.black;
            rotateRight(n.parent);
          }
        }
      }
    }
  }

  private interface NodeVisitor<E>
  {
    boolean accept(E item);
  }

  @Override
  public boolean add(E e)
  {
    tree = add(null, tree, e);
    return true;
  }

  private Node<E> replicate(Node<E> parent, Node<E> tree)
  {
    if (tree == null)
      return tree;
    else
      return new Node<E>(parent, replicate(tree, tree.left), tree.item,
          replicate(tree, tree.right), tree.color);
  }

  private Node<E> add(Node<E> parent, Node<E> tree, E e)
  {
    if (tree == null)
      return new Node<E>(parent, null, e, null, RedBlack.red);
    else {
      int comp = compare.compare(tree.item, e);
      if (comp > 0)
        tree.left = add(tree, tree.left, e);
      else if (comp < 0)
        tree.right = add(tree, tree.right, e);
      else if (!tree.item.equals(e))
        tree.right = add(tree, tree.right, e);

      return tree;
    }
  }

  @Override
  public void clear()
  {
    tree = null;
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean contains(Object o)
  {
    E item = (E) o;
    return contains(tree, item);
  }

  private boolean contains(Node<E> tree, E item)
  {
    if (tree == null)
      return false;
    else {
      int comp = compare.compare(tree.item, item);
      if (comp > 0)
        return contains(tree.left, item);
      else if (comp == 0) {
        if (tree.item.equals(item))
          return true;
        else
          // have to allow for duplicates under ordering
          return contains(tree.left, item) || contains(tree.right, item);
      } else
        return contains(tree.right, item);
    }
  }

  @Override
  public boolean isEmpty()
  {
    return tree == null;
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean remove(Object o)
  {
    Wrapper<Boolean> changed = Wrapper.create(false);
    tree = remove(tree, (E) o, changed);
    return changed.get();
  }

  private Node<E> remove(Node<E> tree, E item, Wrapper<Boolean> changed)
  {
    if (tree == null)
      return null;
    else {
      int comp = compare.compare(tree.item, item);
      if (comp > 0)
        tree.left = remove(tree.left, item, changed);
      else {
        if (comp == 0) {
          if (tree.item.equals(item)) {
            changed.set(true);
            return mergeTrees(tree.left, tree.right);
          } else
            tree.right = remove(tree.right, item, changed);
        } else
          tree.right = remove(tree.right, item, changed);
      }
      return tree;
    }
  }

  private Node<E> mergeTrees(Node<E> left, Node<E> right)
  {
    if (left == null)
      return right;
    else if (right == null)
      return left;
    else {
      if (left.right == null) {
        left.right = right;
        return left;
      } else if (right.left == null) {
        right.left = left;
        return right;
      } else {
        left.right = mergeTrees(left.right, right);
        return left;
      }
    }
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

  @Override
  public void prettyPrint(final PrettyPrintDisplay disp)
  {
    disp.append("{");
    walkTree(tree, new NodeVisitor<E>() {
      String sep = "";

      @Override
      public boolean accept(E item)
      {
        disp.append(sep);
        sep = "; ";
        if (item instanceof PrettyPrintable)
          ((PrettyPrintable) item).prettyPrint(disp);
        else
          disp.append(item.toString());
        return true;
      }
    });
    disp.append("}");
  }

  private void walkTree(Node<E> tree, NodeVisitor<E> visitor)
  {
    if (tree != null) {
      walkTree(tree.left, visitor);
      visitor.accept(tree.item);
      walkTree(tree.right, visitor);
    }
  }

  @Override
  public Iterator<E> iterator()
  {
    return new BagIterator(tree);
  }

  private class BagIterator implements Iterator<E>
  {
    private final Stack<Node<E>> stack = new Stack<Node<E>>();
    private Node<E> next = null;

    BagIterator(Node<E> tree)
    {
      stack.push(tree);
      while (tree.left != null) {
        tree = tree.left;
        stack.push(tree);
      }
    }

    @Override
    public boolean hasNext()
    {
      return !stack.isEmpty();
    }

    @Override
    public E next()
    {
      next = stack.pop();
      if (next.right != null) {
        Node<E> tree = next.right;
        stack.push(tree);
        while (tree.left != null) {
          tree = tree.left;
          stack.push(tree);
        }
      }
      return next.item;
    }

    @Override
    public void remove()
    {
      Bag.this.remove(next.item);
    }
  }

  @Override
  public int size()
  {
    return size(tree);
  }

  private int size(Node<E> tree)
  {
    if (tree == null)
      return 0;
    else
      return size(tree.left) + size(tree.right) + 1;
  }
}
