package org.star_lang.star.operators.hash.runtime;

import java.util.Iterator;
import java.util.Map.Entry;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IMap;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.type.UniversalType;
import org.star_lang.star.data.value.NTuple;
import org.star_lang.star.operators.CafeEnter;
import org.star_lang.star.operators.arrays.runtime.ArrayOps;

/**
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
public class HashTreeIterable
{

  public static class HashIterate implements IFunction
  {
    public static final String name = "__hash_iterate";

    @CafeEnter
    public static IValue enter(IMap hash, IFunction iter, IValue state) throws EvaluationException
    {
      for (Iterator<Entry<IValue, IValue>> it = hash.iterator(); it.hasNext() && !ArrayOps.isAllDone(state);) {
        Entry<IValue, IValue> entry = it.next();
        state = iter.enter(entry.getValue(), state);
      }

      return state;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((IMap) args[0], (IFunction) args[1], args[2]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      // %k~%v~%s~(map of (%k,%v),(%v,%s)=>%s,%s) => %s

      TypeVar k = new TypeVar();
      TypeVar v = new TypeVar();
      TypeVar s = new TypeVar();

      IType mapType = TypeUtils.dictionaryType(k, v);

      IType funType = TypeUtils.functionType(mapType, TypeUtils.functionType(v, s, s), s, s);
      return new UniversalType(k, new UniversalType(v, new UniversalType(s, funType)));
    }
  }

  public static class HashIxIterate implements IFunction
  {
    public static final String name = "__hash_ix_iterate";

    @CafeEnter
    public static IValue enter(IMap hash, IFunction iter, IValue state) throws EvaluationException
    {
      for (Iterator<Entry<IValue, IValue>> it = hash.iterator(); it.hasNext() && !ArrayOps.isAllDone(state);) {
        Entry<IValue, IValue> entry = it.next();
        state = iter.enter(entry.getKey(), entry.getValue(), state);
      }

      return state;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((IMap) args[0], (IFunction) args[1], args[2]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      // %k~%v~%s~(map of (%k,%v),(%k,%v,%s)=>%s,%s) => %s

      TypeVar k = new TypeVar();
      TypeVar v = new TypeVar();
      TypeVar s = new TypeVar();

      IType mapType = TypeUtils.dictionaryType(k, v);

      IType funType = TypeUtils.functionType(mapType, TypeUtils.functionType(k, v, s, s), s, s);
      return new UniversalType(k, new UniversalType(v, new UniversalType(s, funType)));
    }
  }

  public static class HashLeftFold implements IFunction
  {
    public static final String name = "__hash_left_fold";

    @CafeEnter
    public static IValue enter(IMap src, IFunction transform, IValue state) throws EvaluationException
    {
      for (Iterator<Entry<IValue, IValue>> it = src.iterator(); it.hasNext();) {
        Entry<IValue, IValue> entry = it.next();
        state = transform.enter(state, NTuple.tuple(entry.getKey(), entry.getValue()));
      }
      return state;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((IMap) args[0], (IFunction) args[1], args[2]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar ky = new TypeVar();
      TypeVar el = new TypeVar();
      TypeVar st = new TypeVar();

      IType funType = TypeUtils.functionType(TypeUtils.dictionaryType(ky, el), TypeUtils.functionType(st, TypeUtils
          .tupleType(ky, el), st), st, st);
      return new UniversalType(ky, new UniversalType(el, new UniversalType(st, funType)));
    }
  }

  public static class HashLeftFold1 implements IFunction
  {
    public static final String name = "__hash_left_fold1";

    @CafeEnter
    public static IValue enter(IMap src, IFunction transform) throws EvaluationException
    {
      Iterator<Entry<IValue, IValue>> it = src.iterator();
      Entry<IValue, IValue> first = it.next();
      IValue state = NTuple.tuple(first.getKey(), first.getValue());
      if (it.hasNext()) {
        while (it.hasNext()) {
          Entry<IValue, IValue> entry = it.next();
          state = transform.enter(state, NTuple.tuple(entry.getKey(), entry.getValue()));
        }
        return state;
      } else
        throw new EvaluationException("map is empty");
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((IMap) args[0], (IFunction) args[1]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar ky = new TypeVar();
      TypeVar el = new TypeVar();
      TypeVar st = new TypeVar();

      IType funType = TypeUtils.functionType(TypeUtils.dictionaryType(ky, el), TypeUtils.functionType(st, TypeUtils
          .tupleType(ky, el), st), st);
      return new UniversalType(ky, new UniversalType(el, new UniversalType(st, funType)));
    }
  }

  public static class HashRightFold implements IFunction
  {
    public static final String name = "__hash_right_fold";

    @CafeEnter
    public static IValue enter(IMap src, IFunction transform, IValue state) throws EvaluationException
    {
      for (Iterator<Entry<IValue, IValue>> it = src.reverseIterator(); it.hasNext();) {
        Entry<IValue, IValue> entry = it.next();
        state = transform.enter(NTuple.tuple(entry.getKey(), entry.getValue()), state);
      }
      return state;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((IMap) args[0], (IFunction) args[1], args[2]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar ky = new TypeVar();
      TypeVar el = new TypeVar();
      TypeVar st = new TypeVar();

      IType funType = TypeUtils.functionType(TypeUtils.dictionaryType(ky, el), TypeUtils.functionType(TypeUtils
          .tupleType(ky, el), st, st), st, st);
      return new UniversalType(ky, new UniversalType(el, new UniversalType(st, funType)));
    }
  }

  public static class HashRightFold1 implements IFunction
  {
    public static final String name = "__hash_right_fold1";

    @CafeEnter
    public static IValue enter(IMap src, IFunction transform) throws EvaluationException
    {
      Iterator<Entry<IValue, IValue>> it = src.reverseIterator();
      Entry<IValue, IValue> first = it.next();
      IValue state = NTuple.tuple(first.getKey(), first.getValue());
      if (it.hasNext()) {
        while (it.hasNext()) {
          Entry<IValue, IValue> entry = it.next();
          state = transform.enter(NTuple.tuple(entry.getKey(), entry.getValue()), state);
        }
        return state;
      } else
        throw new EvaluationException("map is empty");
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((IMap) args[0], (IFunction) args[1]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar ky = new TypeVar();
      TypeVar el = new TypeVar();
      TypeVar st = new TypeVar();

      IType funType = TypeUtils.functionType(TypeUtils.dictionaryType(ky, el), TypeUtils.functionType(TypeUtils
          .tupleType(ky, el), st, st), st);
      return new UniversalType(ky, new UniversalType(el, new UniversalType(st, funType)));
    }
  }
}
