package org.star_lang.star.operators.relation;

import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.relation.runtime.RelationOps.DeleteFromRelation;
import org.star_lang.star.operators.relation.runtime.RelationOps.EmptyRelation;
import org.star_lang.star.operators.relation.runtime.RelationOps.ExtendRelation;
import org.star_lang.star.operators.relation.runtime.RelationOps.MergeRelation;
import org.star_lang.star.operators.relation.runtime.RelationOps.NewRelation;
import org.star_lang.star.operators.relation.runtime.RelationOps.Relation2Array;
import org.star_lang.star.operators.relation.runtime.RelationOps.RelationCons;
import org.star_lang.star.operators.relation.runtime.RelationOps.RelationEqual;
import org.star_lang.star.operators.relation.runtime.RelationOps.RelationFilter;
import org.star_lang.star.operators.relation.runtime.RelationOps.RelationHeadMatch;
import org.star_lang.star.operators.relation.runtime.RelationOps.RelationLeftFold;
import org.star_lang.star.operators.relation.runtime.RelationOps.RelationMap;
import org.star_lang.star.operators.relation.runtime.RelationOps.RelationRightFold;
import org.star_lang.star.operators.relation.runtime.RelationOps.RelationSize;
import org.star_lang.star.operators.relation.runtime.RelationOps.RelationSlice;
import org.star_lang.star.operators.relation.runtime.RelationOps.RelationSplice;
import org.star_lang.star.operators.relation.runtime.RelationOps.RelationTailMatch;
import org.star_lang.star.operators.relation.runtime.RelationOps.UpdateIntoRelation;
import org.star_lang.star.operators.relation.runtime.RelationQuerySupport.RelationProject0;
import org.star_lang.star.operators.relation.runtime.RelationQuerySupport.RelationUnique;
import org.star_lang.star.operators.relation.runtime.RelationSetOps.RelationComplement;
import org.star_lang.star.operators.relation.runtime.RelationSetOps.RelationIntersect;
import org.star_lang.star.operators.relation.runtime.RelationSetOps.RelationUnion;
import org.star_lang.star.operators.relation.runtime.SortRelation;

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
  public static void declare(Intrinsics cxt)
  {
    cxt.declareBuiltin(new Builtin(NewRelation.name, NewRelation.type(), NewRelation.class));
    cxt.declareBuiltin(new Builtin(EmptyRelation.name, EmptyRelation.type(), EmptyRelation.class));
    cxt.declareBuiltin(new Builtin(RelationSize.name, RelationSize.type(), RelationSize.class));
    cxt.declareBuiltin(new Builtin(ExtendRelation.name, ExtendRelation.type(), ExtendRelation.class));
    cxt.declareBuiltin(new Builtin(MergeRelation.name, MergeRelation.type(), MergeRelation.class));
    cxt.declareBuiltin(new Builtin(DeleteFromRelation.name, DeleteFromRelation.type(), DeleteFromRelation.class));
    cxt.declareBuiltin(new Builtin(UpdateIntoRelation.name, UpdateIntoRelation.type(), UpdateIntoRelation.class));

    cxt.declareBuiltin(new Builtin(RelationEqual.name, RelationEqual.type(), RelationEqual.class));
    cxt.declareBuiltin(new Builtin(RelationCons.name, RelationCons.type(), RelationCons.class));
    cxt.declareBuiltin(new Builtin(RelationHeadMatch.name, RelationHeadMatch.type(), RelationHeadMatch.class));
    cxt.declareBuiltin(new Builtin(RelationTailMatch.name, RelationTailMatch.type(), RelationTailMatch.class));

    cxt.declareBuiltin(new Builtin(RelationUnique.name, RelationUnique.type(), RelationUnique.class));
    cxt.declareBuiltin(new Builtin(RelationProject0.name, RelationProject0.type(), RelationProject0.class));

    cxt.declareBuiltin(new Builtin(Relation2Array.name, Relation2Array.type(), Relation2Array.class));
    cxt.declareBuiltin(new Builtin(SortRelation.name, SortRelation.type(), SortRelation.class));
    cxt.declareBuiltin(new Builtin(RelationMap.name, RelationMap.type(), RelationMap.class));
    cxt.declareBuiltin(new Builtin(RelationFilter.name, RelationFilter.type(), RelationFilter.class));
    cxt.declareBuiltin(new Builtin(RelationLeftFold.name, RelationLeftFold.type(), RelationLeftFold.class));
    cxt.declareBuiltin(new Builtin(RelationRightFold.name, RelationRightFold.type(), RelationRightFold.class));

    cxt.declareBuiltin(new Builtin(RelationUnion.name, RelationUnion.type(), RelationUnion.class));
    cxt.declareBuiltin(new Builtin(RelationIntersect.name, RelationIntersect.type(), RelationIntersect.class));
    cxt.declareBuiltin(new Builtin(RelationComplement.name, RelationComplement.type(), RelationComplement.class));

    cxt.declareBuiltin(new Builtin(RelationSlice.name, RelationSlice.type(), RelationSlice.class));
    cxt.declareBuiltin(new Builtin(RelationSplice.name, RelationSplice.type(), RelationSplice.class));
  }
}
