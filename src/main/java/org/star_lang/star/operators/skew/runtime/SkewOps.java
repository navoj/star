package org.star_lang.star.operators.skew.runtime;

import java.util.Iterator;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IConstructor;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IList;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.type.UniversalType;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.data.value.SkewList;
import org.star_lang.star.operators.CafeEnter;

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
public class SkewOps
{

  public static class SkewEmpty implements IFunction
  {
    public static final String name = "__skew_empty";

    @CafeEnter
    public static boolean enter(IList lst)
    {
      return lst.isEmpty();
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newBool(enter((IList) args[0]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar tv = new TypeVar();
      return new UniversalType(tv, TypeUtils.functionType(TypeUtils.skewType(tv), StandardTypes.booleanType));
    }
  }

  public static class SkewSize implements IFunction
  {
    public static final String name = "__skew_size";

    @CafeEnter
    public static int enter(IList skew)
    {
      return skew.size();
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter((IList) args[0]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar tv = new TypeVar();
      return new UniversalType(tv, TypeUtils.functionType(TypeUtils.skewType(tv), StandardTypes.rawIntegerType));
    }
  }

  public static class SkewHasSize implements IFunction
  {
    public static final String name = "__skew_has_size";

    @CafeEnter
    public static boolean enter(IList skew, int size)
    {
      return skew.size() == size;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newBool(enter((IList) args[0], Factory.intValue(args[1])));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar tv = new TypeVar();
      return new UniversalType(tv, TypeUtils.functionType(TypeUtils.skewType(tv), StandardTypes.rawIntegerType,
          StandardTypes.booleanType));
    }
  }

  public static class SkewEqual implements IFunction
  {
    public static final String name = "__skew_equal";

    @CafeEnter
    public static boolean enter(IList ar1, IList ar2, IFunction equal) throws EvaluationException
    {
      return ar1.equalTo(ar2, equal);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newBool(enter((IList) args[0], (IList) args[1], (IFunction) args[2]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      // %e~(skew of %e,skew of %e,(%e,%e)=>boolean) => boolean
      TypeVar tv = new TypeVar();

      IType skewType = TypeUtils.skewType(tv);
      IType boolType = StandardTypes.booleanType;

      IType funType = TypeUtils.functionType(skewType, skewType, TypeUtils.functionType(tv, tv, boolType), boolType);
      return new UniversalType(tv, funType);
    }
  }

  public static class SkewConcatenate implements IFunction
  {
    public static final String name = "__skew_concatenate";

    @CafeEnter
    public static SkewList enter(SkewList ar1, SkewList ar2) throws EvaluationException
    {
      return ar1.concatenate(ar2);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((SkewList) args[0], (SkewList) args[1]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      // %e~(skew of %e,skew of %e,(%e,%e)=>boolean) => boolean
      TypeVar tv = new TypeVar();

      IType skewType = TypeUtils.skewType(tv);

      IType funType = TypeUtils.functionType(skewType, skewType, skewType);
      return new UniversalType(tv, funType);
    }
  }

  public static class SkewIterate implements IFunction
  {
    public static final String name = "__skew_iterate";

    @CafeEnter
    public static IValue enter(IList skew, IFunction iter, IValue state) throws EvaluationException
    {
      for (Iterator<IValue> it = skew.iterator(); it.hasNext() && !isAllDone(state);)
        state = iter.enter(it.next(), state);

      return state;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((IList) args[0], (IFunction) args[1], args[2]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      // %e~%s~(list of %e,(%e,%s)=>%s,%s) => %s

      TypeVar tv = new TypeVar();
      TypeVar s = new TypeVar();

      IType skewType = TypeUtils.skewType(tv);

      IType funType = TypeUtils.functionType(skewType, TypeUtils.functionType(tv, s, s), s, s);
      return new UniversalType(tv, new UniversalType(s, funType));
    }
  }

  public static class SkewIxIterate implements IFunction
  {
    public static final String name = "__skew_ix_iterate";

    @CafeEnter
    public static IValue enter(IList skew, IFunction iter, IValue state) throws EvaluationException
    {
      int ix = 0;

      for (Iterator<IValue> it = skew.iterator(); it.hasNext() && !isAllDone(state);)
        state = iter.enter(Factory.newInt(ix++), it.next(), state);

      return state;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((IList) args[0], (IFunction) args[1], args[2]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      // %e~%s~(skew of %e,(integer,%e,%s)=>%s,%s) => %s

      TypeVar tv = new TypeVar();
      TypeVar s = new TypeVar();

      IType arType = TypeUtils.skewType(tv);
      IType integerType = StandardTypes.integerType;

      IType funType = TypeUtils.functionType(arType, TypeUtils.functionType(integerType, tv, s, s), s, s);
      return new UniversalType(tv, new UniversalType(s, funType));
    }
  }

  public static boolean isAllDone(IValue state)
  {
    if (state instanceof IConstructor) {
      String label = ((IConstructor) state).getLabel();
      return label.equals(StandardNames.NOMORE) || label.equals(StandardNames.ABORT_ITER);
    } else
      return false;
  }

  public static class SkewMap implements IFunction
  {
    public static final String name = "__skew_map";

    @CafeEnter
    public static IValue enter(IList skew, IFunction iter) throws EvaluationException
    {
      return skew.mapOver(iter);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((IList) args[0], (IFunction) args[1]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      // %e~%s~(list of %e,(%e)=>%f,%s) => list of %f

      TypeVar tv = new TypeVar();
      TypeVar f = new TypeVar();

      IType funType = TypeUtils.functionType(TypeUtils.skewType(tv), TypeUtils.functionType(tv, f), TypeUtils
          .skewType(f));
      return new UniversalType(tv, new UniversalType(f, funType));
    }
  }

  public static class SkewLeftFold implements IFunction
  {
    public static final String name = "__skew_left_fold";

    @CafeEnter
    public static IValue enter(IList src, IFunction transform, IValue init) throws EvaluationException
    {
      return src.leftFold(transform, init);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((IList) args[0], (IFunction) args[1], args[2]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar el = new TypeVar();
      TypeVar tv = new TypeVar();

      IType funType = TypeUtils.functionType(TypeUtils.skewType(el), TypeUtils.functionType(tv, el, tv), tv, tv);
      return new UniversalType(tv, new UniversalType(el, funType));
    }
  }

  public static class SkewRightFold implements IFunction
  {
    public static final String name = "__skew_right_fold";

    @CafeEnter
    public static IValue enter(IList src, IFunction transform, IValue init) throws EvaluationException
    {
      return src.rightFold(transform, init);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((IList) args[0], (IFunction) args[1], args[2]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar el = new TypeVar();
      TypeVar tv = new TypeVar();

      IType funType = TypeUtils.functionType(TypeUtils.skewType(el), TypeUtils.functionType(el, tv, tv), tv, tv);
      return new UniversalType(tv, new UniversalType(el, funType));
    }
  }

  public static class SkewReverse implements IFunction
  {
    public static final String name = "__skew_reverse";

    @CafeEnter
    public static SkewList enter(SkewList ar1) throws EvaluationException
    {
      return ar1.reverse();
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((SkewList) args[0]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      // %e~(skew of %e)=>skew of %e
      TypeVar tv = new TypeVar();

      IType skewType = TypeUtils.skewType(tv);

      return new UniversalType(tv, TypeUtils.functionType(skewType, skewType));
    }
  }
}
