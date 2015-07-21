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
