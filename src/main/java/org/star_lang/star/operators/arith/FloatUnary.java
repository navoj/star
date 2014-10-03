package org.star_lang.star.operators.arith;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.arith.runtime.FloatUnary.FloatAbs;
import org.star_lang.star.operators.arith.runtime.FloatUnary.FloatCbrt;
import org.star_lang.star.operators.arith.runtime.FloatUnary.FloatCeil;
import org.star_lang.star.operators.arith.runtime.FloatUnary.FloatExp;
import org.star_lang.star.operators.arith.runtime.FloatUnary.FloatFloor;
import org.star_lang.star.operators.arith.runtime.FloatUnary.FloatLog;
import org.star_lang.star.operators.arith.runtime.FloatUnary.FloatLog10;
import org.star_lang.star.operators.arith.runtime.FloatUnary.FloatRandom;
import org.star_lang.star.operators.arith.runtime.FloatUnary.FloatRound;
import org.star_lang.star.operators.arith.runtime.FloatUnary.FloatSqrt;
import org.star_lang.star.operators.arith.runtime.FloatUnary.FloatUMinus;
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

public abstract class FloatUnary
{

  public static final String UMINUS = "__float_uminus";

  public static void declare(Intrinsics cxt)
  {
    IType floatType = StandardTypes.rawFloatType;
    IType type = TypeUtils.functionType(floatType, floatType);

    cxt.declareBuiltin(new Builtin("__float_abs", type, FloatAbs.class));
    cxt.declareBuiltin(new Builtin(UMINUS, type, FloatUMinus.class));
    cxt.declareBuiltin(new Builtin("__float_random", type, FloatRandom.class));
    cxt.declareBuiltin(new Builtin("__float_sqrt", type, FloatSqrt.class));
    cxt.declareBuiltin(new Builtin("__float_cbrt", type, FloatCbrt.class));
    cxt.declareBuiltin(new Builtin("__float_ceil", type, FloatCeil.class));
    cxt.declareBuiltin(new Builtin("__float_floor", type, FloatFloor.class));
    cxt.declareBuiltin(new Builtin("__float_round", type, FloatRound.class));
    cxt.declareBuiltin(new Builtin("__float_log", type, FloatLog.class));
    cxt.declareBuiltin(new Builtin("__float_log10", type, FloatLog10.class));
    cxt.declareBuiltin(new Builtin("__float_exp", type, FloatExp.class));
  }
}
