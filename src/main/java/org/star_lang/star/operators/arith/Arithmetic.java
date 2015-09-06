package org.star_lang.star.operators.arith;

import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.arith.runtime.BignumBinary.BigNumDivide;
import org.star_lang.star.operators.arith.runtime.BignumBinary.BigNumMax;
import org.star_lang.star.operators.arith.runtime.BignumBinary.BigNumMin;
import org.star_lang.star.operators.arith.runtime.BignumBinary.BigNumMinus;
import org.star_lang.star.operators.arith.runtime.BignumBinary.BigNumPlus;
import org.star_lang.star.operators.arith.runtime.BignumBinary.BigNumPwr;
import org.star_lang.star.operators.arith.runtime.BignumBinary.BigNumRemainder;
import org.star_lang.star.operators.arith.runtime.BignumBinary.BigNumTimes;
import org.star_lang.star.operators.arith.runtime.BignumCompare.BignumEQ;
import org.star_lang.star.operators.arith.runtime.BignumCompare.BignumGE;
import org.star_lang.star.operators.arith.runtime.BignumCompare.BignumGT;
import org.star_lang.star.operators.arith.runtime.BignumCompare.BignumLE;
import org.star_lang.star.operators.arith.runtime.BignumCompare.BignumLT;
import org.star_lang.star.operators.arith.runtime.BignumCompare.BignumNE;

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


public class Arithmetic
{
  public static void declare()
  {
    Intrinsics.declare(new Builtin("__decimal_plus", BigNumPlus.type(), BigNumPlus.class));
    Intrinsics.declare(new Builtin("__decimal_minus", BigNumMinus.type(), BigNumMinus.class));
    Intrinsics.declare(new Builtin("__decimal_times", BigNumTimes.type(), BigNumTimes.class));
    Intrinsics.declare(new Builtin("__decimal_divide", BigNumDivide.type(), BigNumDivide.class));
    Intrinsics.declare(new Builtin("__decimal_rem", BigNumRemainder.type(), BigNumRemainder.class));
    Intrinsics.declare(new Builtin("__decimal_min", BigNumMin.type(), BigNumMin.class));
    Intrinsics.declare(new Builtin("__decimal_max", BigNumMax.type(), BigNumMax.class));
    Intrinsics.declare(new Builtin("__decimal_power", BigNumPwr.type(), BigNumPwr.class));
    
    Intrinsics.declare(new Builtin(BignumEQ.name, BignumEQ.type(), BignumEQ.class));
    Intrinsics.declare(new Builtin(BignumNE.name, BignumNE.type(), BignumNE.class));
    Intrinsics.declare(new Builtin(BignumLE.name, BignumLE.type(), BignumLE.class));
    Intrinsics.declare(new Builtin(BignumLT.name, BignumLT.type(), BignumLT.class));
    Intrinsics.declare(new Builtin(BignumGE.name, BignumGE.type(), BignumGE.class));
    Intrinsics.declare(new Builtin(BignumGT.name, BignumGT.type(), BignumGT.class));
  }
}
