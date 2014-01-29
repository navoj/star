package org.star_lang.star.operators.arrays.runtime;

import java.util.Iterator;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.operators.CafeEnter;

import com.starview.platform.data.EvaluationException;
import com.starview.platform.data.IArray;
import com.starview.platform.data.IConstructor;
import com.starview.platform.data.IFunction;
import com.starview.platform.data.IValue;
import com.starview.platform.data.type.IType;
import com.starview.platform.data.type.StandardTypes;
import com.starview.platform.data.type.TypeVar;
import com.starview.platform.data.type.UniversalType;
import com.starview.platform.data.value.Factory;

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

public class ArrayOps
{

  public static class ArrayEmpty implements IFunction
  {
    public static final String name = "__array_empty";

    @CafeEnter
    public static boolean enter(IArray lst)
    {
      return lst.isEmpty();
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newBool(enter((IArray) args[0]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar tv = new TypeVar();
      return new UniversalType(tv, TypeUtils.functionType(TypeUtils.arrayType(tv), StandardTypes.booleanType));
    }
  }

  public static class ArraySize implements IFunction
  {
    public static final String name = "__array_size";

    @CafeEnter
    public static int enter(IArray array)
    {
      return array.size();
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter((IArray) args[0]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar tv = new TypeVar();
      return new UniversalType(tv, TypeUtils.functionType(TypeUtils.arrayType(tv), StandardTypes.rawIntegerType));
    }
  }

  public static class ArrayHasSize implements IFunction
  {
    public static final String name = "__array_has_size";

    @CafeEnter
    public static boolean enter(IArray array, int size)
    {
      return array.size() == size;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newBool(enter((IArray) args[0], Factory.intValue(args[1])));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar tv = new TypeVar();
      return new UniversalType(tv, TypeUtils.functionType(TypeUtils.arrayType(tv), StandardTypes.rawIntegerType,
          StandardTypes.booleanType));
    }
  }

  public static class ArrayEqual implements IFunction
  {
    public static final String name = "__array_equal";

    @CafeEnter
    public static boolean enter(IArray ar1, IArray ar2, IFunction equal) throws EvaluationException
    {
      return ar1.equals(ar2, equal);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newBool(enter((IArray) args[0], (IArray) args[1], (IFunction) args[2]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      // %e~(array of %e,array of %e,(%e,%e)=>boolean) => boolean
      TypeVar e = new TypeVar();

      IType arrayType = TypeUtils.arrayType(e);
      IType boolType = StandardTypes.booleanType;

      IType funType = TypeUtils.functionType(arrayType, arrayType, TypeUtils.functionType(e, e, boolType), boolType);
      return new UniversalType(e, funType);
    }
  }

  public static class ArrayConcatenate implements IFunction
  {
    public static final String name = "__array_concatenate";

    @CafeEnter
    public static IArray enter(IArray ar1, IArray ar2) throws EvaluationException
    {
      return ar1.concatList(ar2);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((IArray) args[0], (IArray) args[1]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      // %e~(array of %e,array of %e,(%e,%e)=>boolean) => boolean
      TypeVar e = new TypeVar();

      IType arrayType = TypeUtils.arrayType(e);

      IType funType = TypeUtils.functionType(arrayType, arrayType, arrayType);
      return new UniversalType(e, funType);
    }
  }

  public static class ArrayIterate implements IFunction
  {
    public static final String name = "__array_iterate";

    @CafeEnter
    public static IValue enter(IArray array, IFunction iter, IValue state) throws EvaluationException
    {
      for (Iterator<IValue> it = array.iterator(); it.hasNext() && !isAllDone(state);)
        state = iter.enter(it.next(), state);

      return state;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((IArray) args[0], (IFunction) args[1], args[2]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      // %e~%s~(list of %e,(%e,%s)=>%s,%s) => %s

      TypeVar e = new TypeVar();
      TypeVar s = new TypeVar();

      IType arrayType = TypeUtils.arrayType(e);

      IType funType = TypeUtils.functionType(arrayType, TypeUtils.functionType(e, s, s), s, s);
      return new UniversalType(e, new UniversalType(s, funType));
    }
  }

  public static class ArrayIxIterate implements IFunction
  {
    public static final String name = "__array_ix_iterate";

    @CafeEnter
    public static IValue enter(IArray array, IFunction iter, IValue state) throws EvaluationException
    {
      int ix = 0;

      for (Iterator<IValue> it = array.iterator(); it.hasNext() && !isAllDone(state);)
        state = iter.enter(Factory.newInt(ix++), it.next(), state);

      return state;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((IArray) args[0], (IFunction) args[1], args[2]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      // %e~%s~(array of %e,(integer,%e,%s)=>%s,%s) => %s

      TypeVar e = new TypeVar();
      TypeVar s = new TypeVar();

      IType arType = TypeUtils.arrayType(e);
      IType integerType = StandardTypes.integerType;

      IType funType = TypeUtils.functionType(arType, TypeUtils.functionType(integerType, e, s, s), s, s);
      return new UniversalType(e, new UniversalType(s, funType));
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

  public static class ArrayMap implements IFunction
  {
    public static final String name = "__array_map";

    @CafeEnter
    public static IValue enter(IArray array, IFunction iter) throws EvaluationException
    {
      return array.mapOver(iter);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((IArray) args[0], (IFunction) args[1]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      // %e~%s~(list of %e,(%e)=>%f,%s) => list of %f

      TypeVar e = new TypeVar();
      TypeVar f = new TypeVar();

      IType funType = TypeUtils.functionType(TypeUtils.arrayType(e), TypeUtils.functionType(e, f), TypeUtils
          .arrayType(f));
      return new UniversalType(e, new UniversalType(f, funType));
    }
  }

  public static class ArrayFilter implements IFunction
  {
    public static final String name = "__array_filter";

    @CafeEnter
    public static IValue enter(IArray array, IFunction iter) throws EvaluationException
    {
      return array.filter(iter);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((IArray) args[0], (IFunction) args[1]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      // %e~(list of %e,(%e)=>boolean) => list of %e

      TypeVar e = new TypeVar();

      IType funType = TypeUtils.functionType(TypeUtils.arrayType(e), TypeUtils.functionType(e,
          StandardTypes.booleanType), TypeUtils.arrayType(e));
      return new UniversalType(e, funType);
    }
  }

  public static class ArrayLeftFold implements IFunction
  {
    public static final String name = "__array_left_fold";

    @CafeEnter
    public static IValue enter(IArray src, IFunction transform, IValue init) throws EvaluationException
    {
      return src.leftFold(transform, init);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((IArray) args[0], (IFunction) args[1], args[2]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar el = new TypeVar();
      TypeVar st = new TypeVar();

      IType funType = TypeUtils.functionType(TypeUtils.arrayType(el), TypeUtils.functionType(st, el, st), st, st);
      return new UniversalType(el, new UniversalType(st, funType));
    }
  }

  public static class ArrayLeftFold1 implements IFunction
  {
    public static final String name = "__array_left_fold1";

    @CafeEnter
    public static IValue enter(IArray src, IFunction transform) throws EvaluationException
    {
      return src.leftFold1(transform);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((IArray) args[0], (IFunction) args[1]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar el = new TypeVar();
      TypeVar st = new TypeVar();

      IType funType = TypeUtils.functionType(TypeUtils.arrayType(el), TypeUtils.functionType(st, el, st), st);
      return new UniversalType(el, new UniversalType(st, funType));
    }
  }

  public static class ArrayRightFold implements IFunction
  {
    public static final String name = "__array_right_fold";

    @CafeEnter
    public static IValue enter(IArray src, IFunction transform, IValue init) throws EvaluationException
    {
      return src.rightFold(transform, init);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((IArray) args[0], (IFunction) args[1], args[2]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar el = new TypeVar();
      TypeVar st = new TypeVar();

      IType funType = TypeUtils.functionType(TypeUtils.arrayType(el), TypeUtils.functionType(el, st, st), st, st);
      return new UniversalType(el, new UniversalType(st, funType));
    }
  }

  public static class ArrayRightFold1 implements IFunction
  {
    public static final String name = "__array_right_fold1";

    @CafeEnter
    public static IValue enter(IArray src, IFunction transform) throws EvaluationException
    {
      return src.rightFold1(transform);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((IArray) args[0], (IFunction) args[1]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar el = new TypeVar();
      TypeVar st = new TypeVar();

      IType funType = TypeUtils.functionType(TypeUtils.arrayType(el), TypeUtils.functionType(el, st, st), st);
      return new UniversalType(el, new UniversalType(st, funType));
    }
  }

  public static class ArrayReverse implements IFunction
  {
    public static final String name = "__array_reverse";

    @CafeEnter
    public static IArray enter(IArray ar1) throws EvaluationException
    {
      return ar1.reverse();
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((IArray) args[0]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      // %e~(array of %e)=>array of %e
      TypeVar e = new TypeVar();

      IType arrayType = TypeUtils.arrayType(e);

      IType funType = TypeUtils.functionType(arrayType, arrayType);
      return new UniversalType(e, funType);
    }
  }
}
