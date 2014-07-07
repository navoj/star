package org.star_lang.star.operators.hash.runtime;

import java.util.Map.Entry;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IMap;
import org.star_lang.star.data.IPattern;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.type.UniversalType;
import org.star_lang.star.data.value.BoolWrap;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.data.value.HashTree;
import org.star_lang.star.data.value.Option;
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
public class HashTreeOps
{
  public static class HashCreate implements IFunction
  {
    public static final String name = "__hashCreate";

    @CafeEnter
    public static IMap enter() throws EvaluationException
    {
      return new HashTree();
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter();
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar k = new TypeVar();
      TypeVar v = new TypeVar();
      return new UniversalType(k, new UniversalType(v, TypeUtils.functionType(TypeUtils.mapType(k, v))));
    }
  }

  public static class HashCopy implements IFunction
  {

    @CafeEnter
    public static IValue enter(IValue hash) throws EvaluationException
    {
      return hash.shallowCopy();
    }

    public static final String name = "__hashCopy";

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter(args[0]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar k = new TypeVar();
      TypeVar v = new TypeVar();
      return new UniversalType(k, new UniversalType(v, TypeUtils.functionType(TypeUtils.mapType(k, v), TypeUtils
          .mapType(k, v))));
    }
  }

  public static class HashContains implements IFunction
  {
    public static final String name = "__hashContains";

    @CafeEnter
    public static boolean enter(IMap hash, IValue key)
    {
      return hash.contains(key);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newBool(enter((IMap) args[0], args[1]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar k = new TypeVar();
      TypeVar v = new TypeVar();
      return new UniversalType(k, new UniversalType(v, TypeUtils.functionType(TypeUtils.mapType(k, v), k,
          StandardTypes.booleanType)));
    }
  }

  public static class HashGet implements IFunction
  {
    public static final String name = "__hashGet";

    @CafeEnter
    public static IValue enter(IMap hash, IValue key) throws EvaluationException
    {
      IValue reslt = hash.getMember(key);
      if (reslt == null)
        return Option.noneEnum;
      else
        return Option.some(reslt);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((IMap) args[0], args[1]);
    }

    @Override
    public IType getType()
    {
      return funType();
    }

    public static IType funType()
    {
      TypeVar k = new TypeVar();
      TypeVar v = new TypeVar();
      IType resType = TypeUtils.optionType(v);
      return new UniversalType(k, new UniversalType(v, TypeUtils.functionType(TypeUtils.mapType(k, v), k, resType)));
    }
  }

  public static class HashUpdate implements IFunction
  {
    @CafeEnter
    public static IValue enter(IMap hash, IValue key, IValue val) throws EvaluationException
    {
      return hash.setMember(key, val);
    }

    public static final String name = "__hashUpdate";

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((IMap) args[0], args[1], args[2]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar k = new TypeVar();
      TypeVar v = new TypeVar();
      IType hashType = TypeUtils.mapType(k, v);
      return new UniversalType(v, new UniversalType(k, TypeUtils.functionType(hashType, k, v, hashType)));
    }
  }

  public static class HashEqual implements IFunction
  {
    public static final String name = "__hash_equal";

    @CafeEnter
    public static boolean enter(IMap ar1, IMap ar2, IFunction valQ) throws EvaluationException
    {
      return ar1.equals(ar2, valQ);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newBool(enter((IMap) args[0], (IMap) args[1], (IFunction) args[2]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      // for all %k, %v st (hash of (%k,%v),hash of (%k,%v),(%v,%v)=>boolean) => boolean
      TypeVar k = new TypeVar("%k");
      TypeVar v = new TypeVar("%v");

      IType mapType = TypeUtils.mapType(k, v);
      IType boolType = StandardTypes.booleanType;

      IType funType = TypeUtils.functionType(mapType, mapType, TypeUtils.functionType(v, v, boolType), boolType);
      return new UniversalType(k, new UniversalType(v, funType));
    }
  }

  public static class HashMerge implements IFunction
  {
    public static final String name = "__hash_merge";

    @CafeEnter
    public static IMap enter(IMap hash, IMap rhs) throws EvaluationException
    {
      for (Entry<IValue, IValue> entry : rhs)
        hash = hash.setMember(entry.getKey(), entry.getValue());
      return hash;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((IMap) args[0], (IMap) args[1]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar k = new TypeVar();
      TypeVar v = new TypeVar();
      IType mapType = TypeUtils.mapType(k, v);
      return new UniversalType(k, new UniversalType(v, TypeUtils.functionType(mapType, mapType, mapType)));
    }
  }

  public static class DeleteFromHash implements IFunction
  {
    public static final String name = "__delete_from_hash";

    @CafeEnter
    public static IMap enter(IMap hash, IPattern filter) throws EvaluationException
    {
      return hash.filterOut(filter);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((IMap) args[0], (IPattern) args[1]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar k = new TypeVar();
      TypeVar v = new TypeVar();
      IType mapType = TypeUtils.mapType(k, v);
      IType ptnType = TypeUtils.patternType(TypeUtils.tupleType(), TypeUtils.tupleType(k, v));
      return new UniversalType(k, new UniversalType(v, TypeUtils.functionType(mapType, ptnType, mapType)));
    }
  }

  public static class UpdateIntoHash implements IFunction
  {
    public static final String name = "__update_into_hash";

    @CafeEnter
    public static IMap enter(IMap hash, IPattern filter, IFunction tx) throws EvaluationException
    {
      return hash.update(filter, tx);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((IMap) args[0], (IPattern) args[1], (IFunction) args[2]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar k = new TypeVar();
      TypeVar v = new TypeVar();
      IType mapType = TypeUtils.mapType(k, v);
      IType twoType = TypeUtils.tupleType(k, v);
      IType ptnType = TypeUtils.patternType(TypeUtils.tupleType(), twoType);
      IType funType = TypeUtils.functionType(twoType, twoType);
      return new UniversalType(k, new UniversalType(v, TypeUtils.functionType(mapType, ptnType, funType, mapType)));
    }
  }

  public static class HashDelete implements IFunction
  {
    public static final String mapDelete = "__hashDelete";

    @CafeEnter
    public static IValue enter(IMap hash, IValue key) throws EvaluationException
    {
      return hash.removeMember(key);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((IMap) args[0], args[1]);
    }

    @Override
    public IType getType()
    {
      return funType();
    }

    public static IType funType()
    {
      TypeVar k = new TypeVar();
      TypeVar v = new TypeVar();
      IType hashType = TypeUtils.mapType(k, v);
      return new UniversalType(k, new UniversalType(v, TypeUtils.functionType(hashType, k, hashType)));
    }
  }

  public static class HashEmpty implements IFunction
  {
    public static final String mapEmpty = "__hash_empty";

    @CafeEnter
    public static BoolWrap enter(IMap hash)
    {
      return Factory.newBool(hash.isEmpty());
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((IMap) args[0]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar k = new TypeVar();
      TypeVar v = new TypeVar();
      return new UniversalType(k, new UniversalType(v, TypeUtils.functionType(TypeUtils.mapType(k, v),
          StandardTypes.booleanType)));
    }
  }

  public static class HashSize implements IFunction
  {
    public static final String name = "__hash_size";

    @CafeEnter
    public static int enter(IMap hash)
    {
      return hash.size();
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter((IMap) args[0]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar ky = new TypeVar();
      TypeVar vl = new TypeVar();
      return new UniversalType(ky, new UniversalType(vl, TypeUtils.functionType(TypeUtils.mapType(ky, vl),
          StandardTypes.rawIntegerType)));
    }
  }
}
