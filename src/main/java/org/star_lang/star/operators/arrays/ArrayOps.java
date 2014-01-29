package org.star_lang.star.operators.arrays;

import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.arrays.runtime.Array2Other.Array2Relation;
import org.star_lang.star.operators.arrays.runtime.ArrayIndexSlice.ArrayDeleteElement;
import org.star_lang.star.operators.arrays.runtime.ArrayIndexSlice.ArrayEl;
import org.star_lang.star.operators.arrays.runtime.ArrayIndexSlice.ArrayElement;
import org.star_lang.star.operators.arrays.runtime.ArrayIndexSlice.ArrayIndexElement;
import org.star_lang.star.operators.arrays.runtime.ArrayIndexSlice.ArraySlice;
import org.star_lang.star.operators.arrays.runtime.ArrayIndexSlice.ArraySplice;
import org.star_lang.star.operators.arrays.runtime.ArrayIota.ArrayFloatIota;
import org.star_lang.star.operators.arrays.runtime.ArrayIota.ArrayIntegerIota;
import org.star_lang.star.operators.arrays.runtime.ArrayIota.ArrayLongIota;
import org.star_lang.star.operators.arrays.runtime.ArrayOps.ArrayConcatenate;
import org.star_lang.star.operators.arrays.runtime.ArrayOps.ArrayEmpty;
import org.star_lang.star.operators.arrays.runtime.ArrayOps.ArrayEqual;
import org.star_lang.star.operators.arrays.runtime.ArrayOps.ArrayFilter;
import org.star_lang.star.operators.arrays.runtime.ArrayOps.ArrayHasSize;
import org.star_lang.star.operators.arrays.runtime.ArrayOps.ArrayIterate;
import org.star_lang.star.operators.arrays.runtime.ArrayOps.ArrayIxIterate;
import org.star_lang.star.operators.arrays.runtime.ArrayOps.ArrayLeftFold;
import org.star_lang.star.operators.arrays.runtime.ArrayOps.ArrayLeftFold1;
import org.star_lang.star.operators.arrays.runtime.ArrayOps.ArrayMap;
import org.star_lang.star.operators.arrays.runtime.ArrayOps.ArrayReverse;
import org.star_lang.star.operators.arrays.runtime.ArrayOps.ArrayRightFold;
import org.star_lang.star.operators.arrays.runtime.ArrayOps.ArrayRightFold1;
import org.star_lang.star.operators.arrays.runtime.ArrayOps.ArraySize;
import org.star_lang.star.operators.arrays.runtime.ArrayQuerySupport.ArrayProject0;
import org.star_lang.star.operators.arrays.runtime.ArrayQuerySupport.ArrayUnique;
import org.star_lang.star.operators.arrays.runtime.ArraySequenceOps.ArrayAppend;
import org.star_lang.star.operators.arrays.runtime.ArraySequenceOps.ArrayArray;
import org.star_lang.star.operators.arrays.runtime.ArraySequenceOps.ArrayCons;
import org.star_lang.star.operators.arrays.runtime.ArraySequenceOps.ArrayEmptyMatch;
import org.star_lang.star.operators.arrays.runtime.ArraySequenceOps.ArrayHeadMatch;
import org.star_lang.star.operators.arrays.runtime.ArraySequenceOps.ArrayNil;
import org.star_lang.star.operators.arrays.runtime.ArraySequenceOps.ArrayTailMatch;
import org.star_lang.star.operators.arrays.runtime.ArraySequenceOps.BinaryArray;
import org.star_lang.star.operators.arrays.runtime.ArraySequenceOps.TernaryArray;
import org.star_lang.star.operators.arrays.runtime.ArraySequenceOps.UnaryArray;
import org.star_lang.star.operators.arrays.runtime.ArraySetOps.ArrayComplement;
import org.star_lang.star.operators.arrays.runtime.ArraySetOps.ArrayIntersect;
import org.star_lang.star.operators.arrays.runtime.ArraySetOps.ArrayUnion;
import org.star_lang.star.operators.arrays.runtime.ArrayUpdate.ArrayDelete;
import org.star_lang.star.operators.arrays.runtime.ArrayUpdate.ArraySetElement;
import org.star_lang.star.operators.arrays.runtime.ArrayUpdate.ArraySort;
import org.star_lang.star.operators.arrays.runtime.ArrayUpdate.UpdateIntoArray;

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
  public static void declare(Intrinsics cxt)
  {
    cxt.declareBuiltin(new Builtin(ArrayEmpty.name, ArrayEmpty.type(), ArrayEmpty.class));
    cxt.declareBuiltin(new Builtin(ArrayArray.name, ArrayArray.type(), ArrayArray.class));

    cxt.declareBuiltin(new Builtin(ArraySize.name, ArraySize.type(), ArraySize.class));
    cxt.declareBuiltin(new Builtin(ArrayEqual.name, ArrayEqual.type(), ArrayEqual.class));
    cxt.declareBuiltin(new Builtin(ArrayHasSize.name, ArrayHasSize.type(), ArrayHasSize.class));

    cxt.declareBuiltin(new Builtin(ArrayNil.name, ArrayNil.type(), ArrayNil.class));
    cxt.declareBuiltin(new Builtin(ArrayCons.name, ArrayCons.type(), ArrayCons.class));
    cxt.declareBuiltin(new Builtin(ArrayAppend.name, ArrayAppend.type(), ArrayAppend.class));
    cxt.declareBuiltin(new Builtin(ArrayEmptyMatch.name, ArrayEmptyMatch.type(), ArrayEmptyMatch.class));
    cxt.declareBuiltin(new Builtin(ArrayHeadMatch.name, ArrayHeadMatch.type(), ArrayHeadMatch.class));
    cxt.declareBuiltin(new Builtin(ArrayTailMatch.name, ArrayTailMatch.type(), ArrayTailMatch.class));

    cxt.declareBuiltin(new Builtin(ArrayEl.name, ArrayEl.type(), ArrayEl.class));
    cxt.declareBuiltin(new Builtin(ArrayElement.name, ArrayElement.type(), ArrayElement.class));
    cxt.declareBuiltin(new Builtin(ArrayIndexElement.name, ArrayIndexElement.type(), ArrayIndexElement.class));
    cxt.declareBuiltin(new Builtin(ArrayConcatenate.name, ArrayConcatenate.type(), ArrayConcatenate.class));
    cxt.declareBuiltin(new Builtin(ArraySlice.name, ArraySlice.type(), ArraySlice.class));
    cxt.declareBuiltin(new Builtin(ArraySplice.name, ArraySplice.type(), ArraySplice.class));
    cxt.declareBuiltin(new Builtin(ArraySetElement.name, ArraySetElement.type(), ArraySetElement.class));
    cxt.declareBuiltin(new Builtin(ArrayDeleteElement.name, ArrayDeleteElement.type(), ArrayDeleteElement.class));

    cxt.declareBuiltin(new Builtin(UnaryArray.name, UnaryArray.type(), UnaryArray.class));
    cxt.declareBuiltin(new Builtin(BinaryArray.name, BinaryArray.type(), BinaryArray.class));
    cxt.declareBuiltin(new Builtin(TernaryArray.name, TernaryArray.type(), TernaryArray.class));

    cxt.declareBuiltin(new Builtin(ArrayDelete.name, ArrayDelete.type(), ArrayDelete.class));
    cxt.declareBuiltin(new Builtin(UpdateIntoArray.name, UpdateIntoArray.type(), UpdateIntoArray.class));

    cxt.declareBuiltin(new Builtin(ArrayIterate.name, ArrayIterate.type(), ArrayIterate.class));
    cxt.declareBuiltin(new Builtin(ArrayIxIterate.name, ArrayIxIterate.type(), ArrayIxIterate.class));
    cxt.declareBuiltin(new Builtin(ArraySort.name, ArraySort.type(), ArraySort.class));
    cxt.declareBuiltin(new Builtin(ArrayMap.name, ArrayMap.type(), ArrayMap.class));
    cxt.declareBuiltin(new Builtin(ArrayFilter.name, ArrayFilter.type(), ArrayFilter.class));
    cxt.declareBuiltin(new Builtin(ArrayLeftFold.name, ArrayLeftFold.type(), ArrayLeftFold.class));
    cxt.declareBuiltin(new Builtin(ArrayRightFold.name, ArrayRightFold.type(), ArrayRightFold.class));
    cxt.declareBuiltin(new Builtin(ArrayLeftFold1.name, ArrayLeftFold1.type(), ArrayLeftFold1.class));
    cxt.declareBuiltin(new Builtin(ArrayRightFold1.name, ArrayRightFold1.type(), ArrayRightFold1.class));

    cxt.declareBuiltin(new Builtin(ArrayReverse.name, ArrayReverse.type(), ArrayReverse.class));

    cxt.declareBuiltin(new Builtin(ArrayIntegerIota.name, ArrayIntegerIota.type(), ArrayIntegerIota.class));
    cxt.declareBuiltin(new Builtin(ArrayLongIota.name, ArrayLongIota.type(), ArrayLongIota.class));
    cxt.declareBuiltin(new Builtin(ArrayFloatIota.name, ArrayFloatIota.type(), ArrayFloatIota.class));

    cxt.declareBuiltin(new Builtin(ArrayProject0.name, ArrayProject0.type(), ArrayProject0.class));
    cxt.declareBuiltin(new Builtin(ArrayUnique.name, ArrayUnique.type(), ArrayUnique.class));
    cxt.declareBuiltin(new Builtin(Array2Relation.name, Array2Relation.type(), Array2Relation.class));

    cxt.declareBuiltin(new Builtin(ArrayUnion.name, ArrayUnion.type(), ArrayUnion.class));
    cxt.declareBuiltin(new Builtin(ArrayIntersect.name, ArrayIntersect.type(), ArrayIntersect.class));
    cxt.declareBuiltin(new Builtin(ArrayComplement.name, ArrayComplement.type(), ArrayComplement.class));
  }
}
