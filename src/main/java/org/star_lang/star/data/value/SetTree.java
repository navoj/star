package org.star_lang.star.data.value;

import org.star_lang.star.compiler.operator.StandardNames;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IPattern;
import org.star_lang.star.data.ISet;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.IValueVisitor;
import org.star_lang.star.data.indextree.Fold;
import org.star_lang.star.data.indextree.IndexSet;
import org.star_lang.star.data.indextree.Sets;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.TypeVar;

import java.util.Iterator;

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
public class SetTree implements ISet, PrettyPrintable {
  private final Sets<IValue> set;

  public SetTree(Sets<IValue> set) {
    this.set = set;
  }

  public SetTree() {
    this.set = IndexSet.emptySet();
  }

  @Override
  public IType getType() {
    for (IValue entry : this) {
      return TypeUtils.setType(entry.getType());
    }
    return TypeUtils.setType(new TypeVar());
  }

  @Override
  public ISet copy() throws EvaluationException {
    return this;
  }

  @Override
  public IValue shallowCopy() throws EvaluationException {
    return this;
  }

  @Override
  public void accept(IValueVisitor visitor) {
    visitor.visitSet(this);
  }

  @Override
  public Iterator<IValue> iterator() {
    return set.iterator();
  }

  @Override
  public Iterator<IValue> reverseIterator() {
    return set.reverseIterator();
  }

  @Override
  public boolean contains(IValue key) {
    return set.contains(key);
  }

  @Override
  public ISet insert(IValue value) {
    return new SetTree(set.insert(value));
  }

  @Override
  public SetTree delete(IValue key) {
    return new SetTree(set.delete(key));
  }

  @Override
  public boolean isEmpty() {
    return set.isEmpty();
  }

  @Override
  public ISet filterOut(IPattern filter) throws EvaluationException {
    IndexSet<IValue> result = IndexSet.emptySet();
    for (IValue el : set) {
      IValue tpl = NTuple.tuple(el);
      if (filter.match(tpl) == null)
        result = result.insert(el);
    }

    return new SetTree(result);
  }

  @Override
  public ISet updateUsingPattern(IPattern filter, IFunction transform) throws EvaluationException {
    IndexSet<IValue> result = IndexSet.emptySet();
    for (IValue el : set) {
      if (filter.match(el) != null)
        result = result.insert(transform.enter(el));
      else
        result = result.insert(el);
    }

    return new SetTree(result);
  }

  @Override
  public ISet map(IFunction trans) throws EvaluationException {
    IndexSet<IValue> result = IndexSet.emptySet();

    for (IValue el : set)
      result = result.insert(trans.enter(el));
    return new SetTree(result);
  }

  @Override
  public <S> S fold(Fold<IValue, S> folder, S init) {
    S result = init;
    for (IValue el : set) {
      result = folder.apply(el, result);
    }
    return result;
  }

  @Override
  public int size() {
    return set.size();
  }

  @Override
  public boolean equals(ISet other) {
    if (other.size() == size()) {
      for (IValue entry : this) {
        if (!other.contains(entry))
          return false;
      }
      return true;
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = StandardNames.SET.hashCode();
    for (IValue el : set) {
      hash = hash * 37 + el.hashCode();
    }
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof ISet && equals((ISet) obj);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp) {
    disp.appendWord(StandardNames.SET);
    disp.appendWord(StandardNames.OF);
    disp.space();
    disp.append("[");
    int mark = disp.markIndent(2);
    String sep = "";
    for (IValue entry : set) {
      disp.append(sep);
      sep = ", ";
      if (entry instanceof PrettyPrintable)
        ((PrettyPrintable) entry).prettyPrint(disp);
      else
        disp.append(entry.toString());
    }
    disp.popIndent(mark);
    disp.append("]");
  }

  @Override
  public IValue pick() {
    return set.pick();
  }

  @Override
  public SetTree remaining() {
    return new SetTree((Sets<IValue>) set.remaining());
  }

  @Override
  public String toString() {
    return PrettyPrintDisplay.toString(this);
  }
}
