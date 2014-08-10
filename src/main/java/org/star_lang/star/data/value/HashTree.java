package org.star_lang.star.data.value;

import java.util.Iterator;
import java.util.Map.Entry;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IConstructor;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IMap;
import org.star_lang.star.data.IPattern;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.IValueVisitor;
import org.star_lang.star.data.indextree.IndexTree;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.TypeVar;

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
public class HashTree implements IMap, PrettyPrintable
{
  private final IndexTree<IValue, IValue> indexTree;

  public HashTree(IndexTree<IValue, IValue> indexTree)
  {
    this.indexTree = indexTree;
  }

  public HashTree()
  {
    this.indexTree = IndexTree.emptyTree();
  }

  @Override
  public IType getType()
  {
    for (Entry<IValue, IValue> entry : this) {
      return TypeUtils.dictionaryType(entry.getKey().getType(), entry.getValue().getType());
    }
    return TypeUtils.dictionaryType(new TypeVar(), new TypeVar());
  }

  @Override
  public IMap copy() throws EvaluationException
  {
    return this;
  }

  @Override
  public IValue shallowCopy() throws EvaluationException
  {
    return this;
  }

  @Override
  public void accept(IValueVisitor visitor)
  {
    visitor.visitMap(this);
  }

  @Override
  public Iterator<Entry<IValue, IValue>> iterator()
  {
    return indexTree.iterator();
  }

  @Override
  public Iterator<Entry<IValue, IValue>> reverseIterator()
  {
    return indexTree.reverseIterator();
  }

  @Override
  public boolean contains(IValue key)
  {
    return indexTree.contains(key);
  }

  @Override
  public IValue getMember(IValue key)
  {
    return indexTree.find(key);
  }

  @Override
  public IMap setMember(IValue key, IValue value) throws EvaluationException
  {
    return new HashTree(indexTree.insrt(key, value));
  }

  @Override
  public HashTree removeMember(IValue key) throws EvaluationException
  {
    return new HashTree(indexTree.delete(key));
  }

  @Override
  public boolean isEmpty()
  {
    return indexTree.isEmpty();
  }

  @Override
  public IMap filterOut(IPattern filter) throws EvaluationException
  {
    IndexTree<IValue, IValue> tree = IndexTree.emptyTree();
    for (Iterator<Entry<IValue, IValue>> it = indexTree.iterator(); it.hasNext();) {
      Entry<IValue, IValue> next = it.next();

      IValue tpl = NTuple.tuple(next.getKey(), next.getValue());
      if (filter.match(tpl) == null)
        tree = tree.insrt(next.getKey(), next.getValue());
    }
    return new HashTree(tree);
  }

  @Override
  public IMap update(IPattern filter, IFunction transform) throws EvaluationException
  {
    IndexTree<IValue, IValue> tree = IndexTree.emptyTree();
    for (Iterator<Entry<IValue, IValue>> it = indexTree.iterator(); it.hasNext();) {
      Entry<IValue, IValue> next = it.next();

      IValue tpl = NTuple.tuple(next.getKey(), next.getValue());
      if (filter.match(tpl) == null)
        tree = tree.insrt(next.getKey(), next.getValue());
      else {
        IConstructor nv = (IConstructor) transform.enter(tpl);
        tree = tree.insrt(Factory.getNth(nv, 0), Factory.getNth(nv, 1));
      }
    }

    return new HashTree(tree);
  }

  @Override
  public int size()
  {
    return indexTree.size();
  }

  @Override
  public boolean equals(IMap other, IFunction valQ) throws EvaluationException
  {
    if (other.size() == size()) {
      for (Entry<IValue, IValue> entry : this) {
        IValue oVal = other.getMember(entry.getKey());
        if (oVal == null || !Factory.boolValue(valQ.enter(entry.getValue(), oVal)))
          return false;
      }
      return true;
    }
    return false;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.appendWord(StandardNames.DICTIONARY);
    disp.appendWord(StandardNames.OF);
    disp.append("{");
    int m1 = disp.markIndent(2);
    indexTree.prettyPrint(disp);
    disp.popIndent(m1);
    disp.append("}");
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }
}
