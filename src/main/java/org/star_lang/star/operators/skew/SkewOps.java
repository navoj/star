package org.star_lang.star.operators.skew;

import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.skew.runtime.Skew2Other.Relation2Skew;
import org.star_lang.star.operators.skew.runtime.Skew2Other.Skew2Relation;
import org.star_lang.star.operators.skew.runtime.SkewIota.SkewFloatIota;
import org.star_lang.star.operators.skew.runtime.SkewIota.SkewIntegerIota;
import org.star_lang.star.operators.skew.runtime.SkewIota.SkewLongIota;
import org.star_lang.star.operators.skew.runtime.SkewOps.SkewConcatenate;
import org.star_lang.star.operators.skew.runtime.SkewOps.SkewEmpty;
import org.star_lang.star.operators.skew.runtime.SkewOps.SkewEqual;
import org.star_lang.star.operators.skew.runtime.SkewOps.SkewHasSize;
import org.star_lang.star.operators.skew.runtime.SkewOps.SkewIterate;
import org.star_lang.star.operators.skew.runtime.SkewOps.SkewIxIterate;
import org.star_lang.star.operators.skew.runtime.SkewOps.SkewLeftFold;
import org.star_lang.star.operators.skew.runtime.SkewOps.SkewMap;
import org.star_lang.star.operators.skew.runtime.SkewOps.SkewReverse;
import org.star_lang.star.operators.skew.runtime.SkewOps.SkewRightFold;
import org.star_lang.star.operators.skew.runtime.SkewOps.SkewSize;
import org.star_lang.star.operators.skew.runtime.SkewQuerySupport.SkewProject0;
import org.star_lang.star.operators.skew.runtime.SkewQuerySupport.SkewUnique;
import org.star_lang.star.operators.skew.runtime.SkewSequenceOps.SkewAppend;
import org.star_lang.star.operators.skew.runtime.SkewSequenceOps.SkewCons;
import org.star_lang.star.operators.skew.runtime.SkewSequenceOps.SkewEmptyMatch;
import org.star_lang.star.operators.skew.runtime.SkewSequenceOps.SkewHeadMatch;
import org.star_lang.star.operators.skew.runtime.SkewSequenceOps.SkewNil;
import org.star_lang.star.operators.skew.runtime.SkewSequenceOps.SkewTailMatch;
import org.star_lang.star.operators.skew.runtime.SkewSliceOps.SkewDeleteElement;
import org.star_lang.star.operators.skew.runtime.SkewSliceOps.SkewElement;
import org.star_lang.star.operators.skew.runtime.SkewSliceOps.SkewIndexElement;
import org.star_lang.star.operators.skew.runtime.SkewSliceOps.SkewSlice;
import org.star_lang.star.operators.skew.runtime.SkewSliceOps.SkewSplice;
import org.star_lang.star.operators.skew.runtime.SkewUpdate.SkewDelete;
import org.star_lang.star.operators.skew.runtime.SkewUpdate.SkewSetElement;
import org.star_lang.star.operators.skew.runtime.SkewUpdate.SkewSort;
import org.star_lang.star.operators.skew.runtime.SkewUpdate.UpdateIntoSkew;

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
  public static void declare(Intrinsics cxt)
  {
    cxt.declareBuiltin(new Builtin(SkewEmpty.name, SkewEmpty.type(), SkewEmpty.class));

    cxt.declareBuiltin(new Builtin(SkewSize.name, SkewSize.type(), SkewSize.class));
    cxt.declareBuiltin(new Builtin(SkewEqual.name, SkewEqual.type(), SkewEqual.class));
    cxt.declareBuiltin(new Builtin(SkewHasSize.name, SkewHasSize.type(), SkewHasSize.class));

    cxt.declareBuiltin(new Builtin(SkewNil.name, SkewNil.type(), SkewNil.class));
    cxt.declareBuiltin(new Builtin(SkewCons.name, SkewCons.type(), SkewCons.class));
    cxt.declareBuiltin(new Builtin(SkewAppend.name, SkewAppend.type(), SkewAppend.class));
    cxt.declareBuiltin(new Builtin(SkewEmptyMatch.name, SkewEmptyMatch.type(), SkewEmptyMatch.class));
    cxt.declareBuiltin(new Builtin(SkewHeadMatch.name, SkewHeadMatch.type(), SkewHeadMatch.class));
    cxt.declareBuiltin(new Builtin(SkewTailMatch.name, SkewTailMatch.type(), SkewTailMatch.class));

    cxt.declareBuiltin(new Builtin(SkewElement.name, SkewElement.type(), SkewElement.class));
    cxt.declareBuiltin(new Builtin(SkewIndexElement.name, SkewIndexElement.type(), SkewIndexElement.class));
    cxt.declareBuiltin(new Builtin(SkewConcatenate.name, SkewConcatenate.type(), SkewConcatenate.class));
    cxt.declareBuiltin(new Builtin(SkewSlice.name, SkewSlice.type(), SkewSlice.class));
    cxt.declareBuiltin(new Builtin(SkewSplice.name, SkewSplice.type(), SkewSplice.class));
    cxt.declareBuiltin(new Builtin(SkewSetElement.name, SkewSetElement.type(), SkewSetElement.class));
    cxt.declareBuiltin(new Builtin(SkewDeleteElement.name, SkewDeleteElement.type(), SkewDeleteElement.class));

    cxt.declareBuiltin(new Builtin(SkewDelete.name, SkewDelete.type(), SkewDelete.class));
    cxt.declareBuiltin(new Builtin(UpdateIntoSkew.name, UpdateIntoSkew.type(), UpdateIntoSkew.class));

    cxt.declareBuiltin(new Builtin(SkewIterate.name, SkewIterate.type(), SkewIterate.class));
    cxt.declareBuiltin(new Builtin(SkewIxIterate.name, SkewIxIterate.type(), SkewIxIterate.class));
    cxt.declareBuiltin(new Builtin(SkewMap.name, SkewMap.type(), SkewMap.class));
    cxt.declareBuiltin(new Builtin(SkewLeftFold.name, SkewLeftFold.type(), SkewLeftFold.class));
    cxt.declareBuiltin(new Builtin(SkewRightFold.name, SkewRightFold.type(), SkewRightFold.class));

    cxt.declareBuiltin(new Builtin(SkewReverse.name, SkewReverse.type(), SkewReverse.class));
    cxt.declareBuiltin(new Builtin(SkewSort.name, SkewSort.type(), SkewSort.class));

    cxt.declareBuiltin(new Builtin(SkewIntegerIota.name, SkewIntegerIota.type(), SkewIntegerIota.class));
    cxt.declareBuiltin(new Builtin(SkewLongIota.name, SkewLongIota.type(), SkewLongIota.class));
    cxt.declareBuiltin(new Builtin(SkewFloatIota.name, SkewFloatIota.type(), SkewFloatIota.class));

    cxt.declareBuiltin(new Builtin(SkewProject0.name, SkewProject0.type(), SkewProject0.class));
    cxt.declareBuiltin(new Builtin(SkewUnique.name, SkewUnique.type(), SkewUnique.class));
    cxt.declareBuiltin(new Builtin(Skew2Relation.name, Skew2Relation.type(), Skew2Relation.class));
    cxt.declareBuiltin(new Builtin(Relation2Skew.name, Relation2Skew.type(), Relation2Skew.class));
  }
}
