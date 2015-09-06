package org.star_lang.star.data.value;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.star_lang.star.compiler.cafe.type.CafeTypeDescription;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IConstructor;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.IValueVisitor;
import org.star_lang.star.data.type.ConstructorSpecifier;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.ITypeContext;
import org.star_lang.star.data.type.ITypeDescription;
import org.star_lang.star.data.type.IValueSpecifier;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.TypeExp;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.type.UniversalType;
import org.star_lang.star.operators.string.runtime.ValueDisplay;
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
public abstract class Cons implements IConstructor, PrettyPrintable, Iterable<IValue>
{
  public static final String typeLabel = "cons";
  public static Nil nilEnum = new Nil();

  public static class ConsCons extends Cons
  {
    public static final int conIx = 0;
    public static final String label = "cons";

    private static final int headOffset = 0;
    private static final int tailOffset = 1;

    private final IValue head;
    private final IValue tail;

    public ConsCons(IValue head, IValue tail)
    {
      this.head = head;
      this.tail = tail;
    }

    @Override
    public int conIx()
    {
      return conIx;
    }

    @Override
    public String getLabel()
    {
      return label;
    }

    @Override
    public int size()
    {
      return 2;
    }

    @Override
    public IValue getCell(int index)
    {
      switch (index) {
      case headOffset:
        return head;
      case tailOffset:
        return tail;
      default:
        throw new IllegalAccessError("index out of range");
      }
    }

    public IValue get___0()
    {
      return head;
    }

    public IValue get___1()
    {
      return tail;
    }

    @Override
    public IValue[] getCells()
    {
      return new IValue[] { head, tail };
    }

    @Override
    public void setCell(int index, IValue value) throws EvaluationException
    {
      throw new IllegalAccessError("not permitted");
    }

    @Override
    public IConstructor copy() throws EvaluationException
    {
      return new ConsCons(head.copy(), tail.copy());
    }

    @Override
    public IConstructor shallowCopy() throws EvaluationException
    {
      return new ConsCons(head, tail);
    }

    @Override
    public IType getType()
    {
      return new TypeExp(typeLabel, head.getType());
    }

    public static IType conType()
    {
      TypeVar tv = new TypeVar();
      return new UniversalType(tv, TypeUtils.tupleConstructorType(tv, new TypeExp(typeLabel, tv)));
    }

    @Override
    public boolean equals(Object obj)
    {
      if (obj instanceof ConsCons) {
        ConsCons cons = (ConsCons) obj;
        return cons.head.equals(head) && cons.tail.equals(tail);
      } else
        return false;
    }

    @Override
    public int hashCode()
    {
      return ((label.hashCode() * 37) + head.hashCode() * 37) + tail.hashCode();
    }
  }

  public static class Nil extends Cons
  {
    public static final int conIx = 1;
    public static final String label = "nil";

    @Override
    public int conIx()
    {
      return conIx;
    }

    @Override
    public String getLabel()
    {
      return label;
    }

    @Override
    public int size()
    {
      return 0;
    }

    @Override
    public IValue getCell(int index)
    {
      throw new IllegalAccessError("index out of range");
    }

    @Override
    public void setCell(int index, IValue value) throws EvaluationException
    {
      throw new IllegalAccessError("index out of range");
    }

    @Override
    public IValue[] getCells()
    {
      return new IValue[] {};
    }

    @Override
    public IConstructor copy() throws EvaluationException
    {
      return this;
    }

    @Override
    public IConstructor shallowCopy() throws EvaluationException
    {
      return this;
    }

    @Override
    public IType getType()
    {
      return new TypeExp(typeLabel, new TypeVar());
    }

    public static IType conType()
    {
      TypeVar tv = new TypeVar();
      return new UniversalType(tv, TypeUtils.constructorType(new TypeExp(typeLabel, tv)));
    }

    @Override
    public boolean equals(Object obj)
    {
      return obj instanceof Nil;
    }

    @Override
    public int hashCode()
    {
      return label.hashCode();
    }
  }

  @Override
  public Iterator<IValue> iterator()
  {
    return new Iterator<IValue>() {
      private IValue cns = Cons.this;

      @Override
      public boolean hasNext()
      {
        return cns instanceof ConsCons;
      }

      @Override
      public IValue next()
      {
        IValue next = ((ConsCons) cns).head;
        cns = ((ConsCons) cns).tail;
        return next;
      }

      @Override
      public void remove()
      {
        throw new UnsupportedOperationException("not permitted");
      }
    };
  }

  public IValue nth(int ix)
  {
    IValue cns = this;
    while (ix > 0 && cns instanceof ConsCons) {
      ConsCons cons = (ConsCons) cns;
      ix--;
      cns = cons.tail;
    }
    if (ix == 0 && cns instanceof ConsCons)
      return ((ConsCons) cns).head;
    else
      return null;
  }

  public int length()
  {
    IValue cns = this;
    int length = 0;
    while (cns instanceof ConsCons) {
      ConsCons cons = (ConsCons) cns;
      length++;
      cns = cons.tail;
    }
    return length;
  }

  @Override
  public void accept(IValueVisitor visitor)
  {
    visitor.visitConstructor(this);
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    ValueDisplay.display(disp, this);
  }

  public static Cons list(IValue... els)
  {
    Cons list = nilEnum;
    for (int ix = els.length - 1; ix >= 0; ix--)
      list = new ConsCons(els[ix], list);
    return list;
  }

  public static Cons cons(IValue head, IValue tail)
  {
    return new ConsCons(head, tail);
  }

  public static Cons list(List<IValue> els)
  {
    Cons list = nilEnum;
    for (int ix = els.size() - 1; ix >= 0; ix--)
      list = new ConsCons(els.get(ix), list);
    return list;
  }

  public static void declare(ITypeContext cxt)
  {
    TypeVar tv = new TypeVar();

    IType consType = new TypeExp(typeLabel, tv); // avoid issues with initialization of
                                                    // intrinsics
    IType conConType = new UniversalType(tv, TypeUtils.tupleConstructorType(tv, consType, consType));
    Location nullLoc = Location.nullLoc;

    ConstructorSpecifier consSpec = new ConstructorSpecifier(nullLoc, null, ConsCons.label, ConsCons.conIx, conConType,
        ConsCons.class, Cons.class);

    IType nilConType = new UniversalType(tv, TypeUtils.constructorType(consType));
    ConstructorSpecifier nilSpec = new ConstructorSpecifier(nullLoc, null, Nil.label, Nil.conIx, nilConType, Nil.class,
        Cons.class);

    List<IValueSpecifier> specs = new ArrayList<>();
    specs.add(consSpec);
    specs.add(nilSpec);

    ITypeDescription desc = new CafeTypeDescription(nullLoc,new UniversalType(tv, consType), Cons.class.getName(),
        specs);

    cxt.defineType(desc);
  }
}
