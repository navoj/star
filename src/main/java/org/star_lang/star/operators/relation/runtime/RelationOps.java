package org.star_lang.star.operators.relation.runtime;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.operators.CafeEnter;

import com.starview.platform.data.EvaluationException;
import com.starview.platform.data.IArray;
import com.starview.platform.data.IFunction;
import com.starview.platform.data.IPattern;
import com.starview.platform.data.IRelation;
import com.starview.platform.data.IValue;
import com.starview.platform.data.type.IType;
import com.starview.platform.data.type.StandardTypes;
import com.starview.platform.data.type.TypeVar;
import com.starview.platform.data.type.UniversalType;
import com.starview.platform.data.value.Array;
import com.starview.platform.data.value.BoolWrap;
import com.starview.platform.data.value.Factory;
import com.starview.platform.data.value.NTuple;
import com.starview.platform.data.value.Relation;

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
public class RelationOps
{

  public static class NewRelation implements IFunction
  {
    public static final String name = "__newRelation";

    @CafeEnter
    public static IRelation enter()
    {
      return Relation.create();
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
      TypeVar tv = new TypeVar();
      return new UniversalType(tv, TypeUtils.functionType(TypeUtils.relationType(tv)));
    }
  }

  public static class EmptyRelation implements IFunction
  {
    public static final String name = "__relation_empty";

    @CafeEnter
    public static BoolWrap enter(IRelation rel)
    {
      return Factory.newBool(rel.isEmpty());
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((IRelation) args[0]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar tv = new TypeVar();
      return new UniversalType(tv, TypeUtils.functionType(TypeUtils.relationType(tv), StandardTypes.booleanType));
    }
  }

  public static class RelationSize implements IFunction
  {
    public static final String name = "__relation_size";

    @CafeEnter
    public static int enter(IRelation rel)
    {
      return rel.size();
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newInt(enter((IRelation) args[0]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar tv = new TypeVar();
      return new UniversalType(tv, TypeUtils.functionType(TypeUtils.relationType(tv), StandardTypes.rawIntegerType));
    }
  }

  public static class ExtendRelation implements IFunction
  {
    public static final String name = "__extend_relation";

    @CafeEnter
    public static IRelation enter(IRelation rel, IValue tuple) throws EvaluationException
    {
      return rel.addCell(tuple);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((IRelation) args[0], args[1]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar v = new TypeVar();
      IType relType = TypeUtils.relationType(v);
      return new UniversalType(v, TypeUtils.functionType(relType, v, relType));
    }
  }

  public static class MergeRelation implements IFunction
  {
    public static final String name = "__merge_relation";

    @CafeEnter
    public static IRelation enter(IRelation rel, IRelation src) throws EvaluationException
    {
      return rel.concat(src);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((IRelation) args[0], (IRelation) args[1]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar v = new TypeVar();
      IType relType = TypeUtils.relationType(v);
      return new UniversalType(v, TypeUtils.functionType(relType, relType, relType));
    }
  }

  public static class DeleteFromRelation implements IFunction
  {
    public static final String name = "__delete_from_relation";

    @CafeEnter
    public static IRelation enter(IRelation rel, IPattern filter) throws EvaluationException
    {
      return rel.deleteUsingPattern(filter);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((IRelation) args[0], (IPattern) args[1]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar tv = new TypeVar();
      IType filter = TypeUtils.patternType(TypeUtils.tupleType(), tv);
      IType relType = TypeUtils.relationType(tv);
      return new UniversalType(tv, TypeUtils.functionType(relType, filter, relType));
    }
  }

  public static class RelationSlice implements IFunction
  {
    public static final String name = "__relation_slice";

    @CafeEnter
    public static Relation enter(Relation rel, int from, int to) throws EvaluationException
    {
      return rel.slice(from, to);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((Relation) args[0], Factory.intValue(args[1]), Factory.intValue(args[2]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar v = new TypeVar();
      IType relType = TypeUtils.relationType(v);
      return new UniversalType(v, TypeUtils.functionType(relType, StandardTypes.rawIntegerType,
          StandardTypes.rawIntegerType, relType));
    }
  }

  public static class RelationSplice implements IFunction
  {
    public static final String name = "__relation_splice";

    @CafeEnter
    public static Relation enter(Relation rel, int from, int to, Relation sub) throws EvaluationException
    {
      return rel.splice(sub, from, to);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((Relation) args[0], Factory.intValue(args[1]), Factory.intValue(args[2]), (Relation) args[3]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar v = new TypeVar();
      IType relType = TypeUtils.relationType(v);
      return new UniversalType(v, TypeUtils.functionType(relType, StandardTypes.rawIntegerType,
          StandardTypes.rawIntegerType, relType, relType));
    }
  }

  public static class UpdateIntoRelation implements IFunction
  {
    public static final String name = "__update_into_relation";

    @CafeEnter
    public static IRelation enter(IRelation rel, IPattern filter, IFunction repl) throws EvaluationException
    {
      return rel.updateUsingPattern(filter, repl);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((IRelation) args[0], (IPattern) args[1], (IFunction) args[2]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar tv = new TypeVar();
      IType filter = TypeUtils.patternType(TypeUtils.tupleType(), tv);
      IType replFun = TypeUtils.functionType(tv, tv);
      IType relType = TypeUtils.relationType(tv);
      return new UniversalType(tv, TypeUtils.functionType(relType, filter, replFun, relType));
    }
  }

  public static class RelationCons implements IFunction
  {
    public static final String name = "__relationCons";

    @CafeEnter
    public static IValue enter(IValue el, IRelation src) throws EvaluationException
    {
      return src.addCell(el);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter(args[0], (IRelation) args[1]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar tplType = new TypeVar();
      IType relationType = TypeUtils.relationType(tplType);
      return new UniversalType(tplType, TypeUtils.functionType(tplType, relationType, relationType));
    }
  }

  public static class RelationHeadMatch implements IPattern
  {
    public static final String name = "__relation_head_match";

    @CafeEnter
    public static IValue matches(IValue list) throws EvaluationException
    {
      Relation rel = (Relation) list;

      if (rel.size() >= 1) {
        return NTuple.tuple(rel.getCell(0), rel.slice(1, rel.size()));
      } else
        return null;
    }

    @Override
    public IValue match(IValue list) throws EvaluationException
    {
      return matches(list);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar e = new TypeVar();
      IType l = TypeUtils.relationType(e);

      return new UniversalType(e, TypeUtils.patternType(TypeUtils.tupleType(e, l), l));
    }
  }

  public static class RelationTailMatch implements IPattern
  {
    public static final String name = "__relation_tail_match";

    @Override
    public IValue match(IValue list) throws EvaluationException
    {
      return matches(list);
    }

    @CafeEnter
    public static IValue matches(IValue data) throws EvaluationException
    {
      Relation rel = (Relation) data;

      int size = rel.size();

      if (size >= 1)
        return NTuple.tuple(rel.slice(0, size - 1), rel.getCell(size - 1));
      else
        return null;
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar tv = new TypeVar();
      IType l = TypeUtils.relationType(tv);

      return new UniversalType(tv, TypeUtils.patternType(TypeUtils.tupleType(l, tv), l));
    }
  }

  public static class Relation2Array implements IFunction
  {
    public static final String name = "__relation_array";

    @CafeEnter
    public static IArray enter(Relation rel) throws EvaluationException
    {
      return Array.newArray(rel.iterator(), rel.size());
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((Relation) args[0]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar v = new TypeVar();
      return new UniversalType(v, TypeUtils.functionType(TypeUtils.relationType(v), TypeUtils.arrayType(v)));
    }
  }

  public static class RelationEqual implements IFunction
  {
    public static final String name = "__relation_eq";

    @CafeEnter
    public static BoolWrap enter(IRelation rel1, IRelation rel2, IFunction eq) throws EvaluationException
    {
      return Factory.newBool(rel1.equals(rel2, eq));
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((IRelation) args[0], (IRelation) args[1], (IFunction) args[2]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar v = new TypeVar();
      IType relType = TypeUtils.relationType(v);
      IType eqType = TypeUtils.functionType(v, v, StandardTypes.booleanType);
      return new UniversalType(v, TypeUtils.functionType(relType, relType, eqType, StandardTypes.booleanType));
    }
  }

  public static class RelationMap implements IFunction
  {
    public static final String name = "__relation_map";

    @CafeEnter
    public static IRelation enter(Relation src, IFunction iter) throws EvaluationException
    {
      return src.mapOver(iter);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((Relation) args[0], (IFunction) args[1]);
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

      IType funType = TypeUtils.functionType(TypeUtils.relationType(e), TypeUtils.functionType(e, f), TypeUtils
          .relationType(f));
      return new UniversalType(e, new UniversalType(f, funType));
    }
  }

  public static class RelationFilter implements IFunction
  {
    public static final String name = "__relation_filter";

    @CafeEnter
    public static IValue enter(IRelation array, IFunction iter) throws EvaluationException
    {
      return array.filter(iter);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((IRelation) args[0], (IFunction) args[1]);
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

      IType funType = TypeUtils.functionType(TypeUtils.relationType(e), TypeUtils.functionType(e,
          StandardTypes.booleanType), TypeUtils.relationType(e));
      return new UniversalType(e, funType);
    }
  }

  public static class RelationLeftFold implements IFunction
  {
    public static final String name = "__relation_left_fold";

    @CafeEnter
    public static IValue enter(IRelation src, IFunction transform, IValue init) throws EvaluationException
    {
      return src.leftFold(transform, init);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((IRelation) args[0], (IFunction) args[1], args[2]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      // %e~(indexed of %e,(%e,%f)=>%f,%f) => %f

      TypeVar e = new TypeVar();
      TypeVar f = new TypeVar();

      IType funType = TypeUtils.functionType(TypeUtils.relationType(e), TypeUtils.functionType(f, e, f), f, f);
      return new UniversalType(e, new UniversalType(f, funType));
    }
  }

  public static class RelationRightFold implements IFunction
  {
    public static final String name = "__relation_right_fold";

    @CafeEnter
    public static IValue enter(IRelation src, IFunction transform, IValue init) throws EvaluationException
    {
      return src.rightFold(transform, init);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((IRelation) args[0], (IFunction) args[1], args[2]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar tv = new TypeVar();
      TypeVar f = new TypeVar();

      IType funType = TypeUtils.functionType(TypeUtils.relationType(tv), TypeUtils.functionType(tv, f, f), f, f);
      return new UniversalType(tv, new UniversalType(f, funType));
    }
  }
}
