package org.star_lang.star.data.value;

import java.util.Iterator;
import java.util.Stack;

import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.compiler.util.SingleIterator;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.operators.string.runtime.ValueDisplay;

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
public abstract class SkewTree implements Iterable<IValue>, PrettyPrintable
{
  protected final IValue entry;

  protected SkewTree(IValue entry)
  {
    this.entry = entry;
  }

  public IValue getEntry()
  {
    return entry;
  }

  public abstract int size();

  public abstract IValue lookup(int weight, int index) throws ArrayIndexOutOfBoundsException;

  public abstract SkewTree update(int weight, int index, IValue el) throws ArrayIndexOutOfBoundsException;

  public abstract SkewTree mapOver(IFunction transform) throws EvaluationException;

  public abstract IValue leftFold(IFunction fun, IValue init) throws EvaluationException;

  public abstract IValue rightFold(IFunction fun, IValue init) throws EvaluationException;

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

  @Override
  public Iterator<IValue> iterator()
  {
    return new Iterator<IValue>() {
      Stack<SkewTree> itStack = new Stack<>();
      {
        probe(SkewTree.this);
      }

      private void probe(SkewTree skew)
      {
        if (skew instanceof SkewNode)
          probe(((SkewNode) skew).getRight());

        itStack.push(skew);
      }

      @Override
      public boolean hasNext()
      {
        return !itStack.isEmpty();
      }

      @Override
      public IValue next()
      {
        SkewTree node = itStack.pop();
        IValue next = node.entry;

        if (node instanceof SkewNode) {
          SkewNode skNode = (SkewNode) node;
          probe(skNode.left);
        }
        return next;
      }

      @Override
      public void remove()
      {
        throw new UnsupportedOperationException();
      }
    };
  }

  public static class SkewLeaf extends SkewTree
  {
    public SkewLeaf(IValue entry)
    {
      super(entry);
    }

    @Override
    public int size()
    {
      return 1;
    }

    @Override
    public IValue lookup(int weight, int index) throws ArrayIndexOutOfBoundsException
    {
      if (index == 0)
        return entry;
      else
        throw new ArrayIndexOutOfBoundsException();
    }

    @Override
    public SkewTree update(int weight, int index, IValue el) throws ArrayIndexOutOfBoundsException
    {
      if (weight == 1 && index == 0)
        return new SkewLeaf(el);
      else
        throw new ArrayIndexOutOfBoundsException();
    }

    @Override
    public SkewTree mapOver(IFunction transform) throws EvaluationException
    {
      return new SkewLeaf(transform.enter(entry));
    }

    @Override
    public IValue leftFold(IFunction fun, IValue init) throws EvaluationException
    {
      return fun.enter(init, entry);
    }

    @Override
    public IValue rightFold(IFunction fun, IValue init) throws EvaluationException
    {
      return fun.enter(entry, init);
    }

    @Override
    public Iterator<IValue> iterator()
    {
      return new SingleIterator<IValue>(entry);
    }

    @Override
    public void prettyPrint(PrettyPrintDisplay disp)
    {
      ValueDisplay.display(disp, entry);
    }

  }

  public static class SkewNode extends SkewTree
  {
    private final SkewTree left, right;

    public SkewNode(IValue entry, SkewTree left, SkewTree right)
    {
      super(entry);
      this.left = left;
      this.right = right;
    }

    public SkewTree getLeft()
    {
      return left;
    }

    public SkewTree getRight()
    {
      return right;
    }

    @Override
    public int size()
    {
      return left.size() + right.size() + 1;
    }

    @Override
    public IValue lookup(int weight, int index) throws ArrayIndexOutOfBoundsException
    {
      SkewTree tree = this;

      while (index != 0) {
        if (tree instanceof SkewNode) {
          weight = weight >> 1;
          if (index <= weight) {
            tree = ((SkewNode) tree).left;
            index--;
          } else {
            tree = ((SkewNode) tree).right;
            index -= weight + 1;
          }
        } else
          throw new ArrayIndexOutOfBoundsException();
      }
      return tree.entry;
    }

    @Override
    public SkewTree update(int weight, int index, IValue el) throws ArrayIndexOutOfBoundsException
    {
      if (index == 0)
        return new SkewNode(el, left, right);
      else {
        int w2 = weight >> 1;
        if (index <= w2)
          return new SkewNode(this.entry, left.update(w2, index - 1, el), right);
        else
          return new SkewNode(this.entry, left, right.update(w2, index - 1 - w2, el));
      }
    }

    @Override
    public SkewTree mapOver(IFunction transform) throws EvaluationException
    {
      return new SkewNode(transform.enter(entry), left.mapOver(transform), right.mapOver(transform));
    }

    @Override
    public IValue leftFold(IFunction fun, IValue init) throws EvaluationException
    {
      return right.leftFold(fun, left.leftFold(fun, fun.enter(init, entry)));
    }

    @Override
    public IValue rightFold(IFunction fun, IValue init) throws EvaluationException
    {
      return fun.enter(entry, left.rightFold(fun, right.rightFold(fun, init)));
    }

    @Override
    public void prettyPrint(PrettyPrintDisplay disp)
    {
      disp.append("<");
      ValueDisplay.display(disp, entry);
      disp.append(":");
      left.prettyPrint(disp);
      disp.append(";");
      right.prettyPrint(disp);
      disp.append(">");

    }
  }

}
