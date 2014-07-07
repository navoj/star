package org.star_lang.star.data.value;

import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IList;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.IValueVisitor;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.value.SkewTree.SkewLeaf;
import org.star_lang.star.data.value.SkewTree.SkewNode;
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
public class SkewList implements PrettyPrintable, IList, Iterable<IValue>
{
  public static final String label = "skew";
  private static final SkewList empty = new SkewList(0, null, null);

  private final int weight;
  private final SkewTree tree;
  private final SkewList next;

  private SkewList(int weight, SkewTree tree, SkewList next)
  {
    this.weight = weight;
    this.tree = tree;
    this.next = next;
  }

  public static SkewList empty()
  {
    return empty;
  }

  public static SkewList newSkewList(List<IValue> list)
  {
    SkewList skew = empty;
    for (int ix = list.size(); ix > 0; ix--)
      skew = skew.consCell(list.get(ix - 1));
    return skew;
  }

  public static SkewList newSkewList(Iterator<IValue> it)
  {
    return newSkewList(it, empty);
  }

  private static SkewList newSkewList(Iterator<IValue> it, SkewList soFar)
  {
    if (it.hasNext()) {
      IValue el = it.next();
      soFar = newSkewList(it, soFar);
      return soFar.consCell(el);
    } else
      return soFar;
  }

  @Override
  public boolean isEmpty()
  {
    return this == empty;
  }

  @Override
  public int size()
  {
    int size = 0;
    SkewList list = this;
    while (list != empty) {
      size += list.weight;
      list = list.next;
    }
    return size;
  }

  @Override
  public SkewList consCell(IValue el)
  {
    if (!isEmpty()) {
      if (!next.isEmpty()) {
        int w2 = next.weight;
        if (weight == w2)
          return new SkewList(weight + weight + 1, new SkewNode(el, tree, next.tree), next.next);
      }
    }

    return new SkewList(1, new SkewLeaf(el), this);
  }

  public SkewList addCell(IValue el)
  {
    return empty.consCell(el).concatenate(this);
  }

  @Override
  public SkewList tail()
  {
    if (isEmpty())
      throw new UnsupportedOperationException();
    else {
      if (tree instanceof SkewLeaf)
        return next;
      else {
        assert tree instanceof SkewNode;
        int w2 = weight / 2;
        return new SkewList(w2, ((SkewNode) tree).getLeft(), new SkewList(w2, ((SkewNode) tree).getRight(), next));
      }
    }
  }

  @Override
  public IValue getCell(int index)
  {
    SkewList list = this;
    while (!list.isEmpty()) {
      int weight = list.weight;
      if (weight > index)
        return list.tree.lookup(weight, index);
      else {
        index -= list.weight;
        list = list.next;
      }
    }
    throw new ArrayIndexOutOfBoundsException();
  }

  private static SkewList mapOver(SkewList list, IFunction transform) throws EvaluationException
  {
    if (list.isEmpty())
      return list;
    else {
      SkewList tail = mapOver(list.next, transform);
      return new SkewList(list.weight, list.tree.mapOver(transform), tail);
    }
  }

  @Override
  public SkewList mapOver(IFunction transform) throws EvaluationException
  {
    return mapOver(this, transform);
  }

  @Override
  public IValue leftFold(IFunction transform, IValue init) throws EvaluationException
  {
    SkewList list = this;
    IValue state = init;

    while (!list.isEmpty()) {
      state = list.tree.leftFold(transform, state);
      list = list.next;
    }

    return state;
  }

  @Override
  public IValue leftFold1(IFunction fun) throws EvaluationException
  {
    SkewList list = this;

    if (list.isEmpty())
      throw new EvaluationException("list is empty");
    else {
      Iterator<IValue> it = iterator();
      IValue st = it.next();
      while (it.hasNext())
        st = fun.enter(st, it.next());
      return st;
    }
  }

  private static IValue rightFold(SkewList list, IFunction transform, IValue init) throws EvaluationException
  {
    if (list.isEmpty())
      return init;
    else {
      IValue state = rightFold(list.next, transform, init);
      return list.tree.rightFold(transform, state);
    }
  }

  @Override
  public IValue rightFold(IFunction transform, IValue init) throws EvaluationException
  {
    return rightFold(this, transform, init);
  }

  @Override
  public IValue rightFold1(IFunction transform) throws EvaluationException
  {
    if (isEmpty())
      throw new EvaluationException("list is empty");
    else {
      Stack<IValue> stk = new Stack<>();
      for (IValue el : this)
        stk.push(el);
      IValue st = stk.pop();
      while (!stk.isEmpty())
        st = transform.enter(stk.pop(), st);
      return st;
    }
  }

  @Override
  public boolean equalTo(IList other, IFunction test) throws EvaluationException
  {
    SkewList list = this;

    while (!list.isEmpty() && !other.isEmpty()) {
      IValue h1 = list.getCell(0);
      IValue h2 = other.getCell(0);
      if (!Factory.boolValue(test.enter(h1, h2)))
        return false;
      list = list.tail();
      other = other.tail();
    }
    return list.isEmpty() && other.isEmpty();
  }

  @Override
  public Iterator<IValue> iterator()
  {
    return new Iterator<IValue>() {
      Stack<SkewTree> itStack = new Stack<>();
      {
        probe(SkewList.this);
      }

      private void probe(SkewList list)
      {
        if (!list.isEmpty()) {
          probe(list.next);
          probe(list.tree);
        }
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
          probe(skNode.getLeft());
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

  @Override
  public IType getType()
  {
    if (isEmpty())
      return TypeUtils.typeExp(label, new TypeVar());
    else
      return TypeUtils.typeExp(label, getCell(0).getType());
  }

  @Override
  public SkewList shallowCopy()
  {
    return this;
  }

  @Override
  public IValue copy() throws EvaluationException
  {
    return this;
  }

  @Override
  public void accept(IValueVisitor visitor)
  {
    visitor.visitList(this);
  }

  private static SkewList update(int index, IValue el, SkewList entries)
  {
    if (entries.isEmpty())
      throw new ArrayIndexOutOfBoundsException();
    else {
      int weight = entries.weight;
      if (weight > index)
        return new SkewList(weight, entries.tree.update(weight, index, el), entries.next);
      else
        return new SkewList(weight, entries.tree, update(index - weight, el, entries.next));
    }
  }

  @Override
  public IList substituteCell(int index, IValue value)
  {
    return update(index, value, this);
  }

  /**
   * Construct the concatenation of this list with another: this++other
   * 
   * This is an expensive operation.
   * 
   * @param other
   * @return the new list.
   * @throws EvaluationException
   *           if something goes wrong; it shouldn't of course
   */
  public SkewList concatenate(SkewList other)
  {
    IFunction conc = new IFunction() {

      @Override
      public IType getType()
      {
        return null;
      }

      @Override
      public IValue enter(IValue... args) throws EvaluationException
      {
        SkewList list = (SkewList) args[1];
        return list.consCell(args[0]);
      }
    };
    try {
      return (SkewList) rightFold(conc, other);
    } catch (EvaluationException e) {
      throw new UnsupportedOperationException(e);
    }
  }

  public SkewList drop(int from)
  {
    if (from == 0)
      return this;
    else if (weight < from)
      return next.drop(from - weight);
    else if (weight == from)
      return next;
    else
      return tail().drop(from - 1); // this will split the head tree and try again
  }

  private static SkewList frontEls(int count, SkewList soFar, Iterator<IValue> it)
  {
    if (count > 0 && it.hasNext()) {
      IValue el = it.next();
      SkewList list = frontEls(count - 1, soFar, it);
      return list.consCell(el);
    } else
      return soFar;
  }

  public SkewList front(int count)
  {
    return frontEls(count, empty, iterator());
  }

  public SkewList slice(int from, int to)
  {
    SkewList tail = drop(from);
    return tail.front(to - from);
  }

  public SkewList splice(SkewList sub, int from, int to) throws EvaluationException
  {
    return front(from).concatenate(sub.concatenate(drop(to)));
  }

  public SkewList remove(int from, int to) throws EvaluationException
  {
    return front(from).concatenate(drop(to));
  }

  /**
   * Construct a reversed skew list
   * 
   * This is an expensive operation.
   * 
   * @return the new list.
   * @throws EvaluationException
   *           if something goes wrong; it shouldn't of course
   */
  public SkewList reverse() throws EvaluationException
  {
    SkewList list = empty;
    for (IValue el : this)
      list = list.consCell(el);
    return list;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.appendWord(label);
    disp.append(" of {");
    String sep = "";
    int mark = disp.markIndent(2);
    for (IValue el : this) {
      disp.append(sep);
      sep = "; ";
      ValueDisplay.display(disp, el);
    }
    disp.popIndent(mark);
    disp.append("}");
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }
}
