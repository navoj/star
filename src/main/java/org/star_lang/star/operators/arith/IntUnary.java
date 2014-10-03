package org.star_lang.star.operators.arith;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.arith.runtime.IntUnary.IntAbs;
import org.star_lang.star.operators.arith.runtime.IntUnary.IntCbrt;
import org.star_lang.star.operators.arith.runtime.IntUnary.IntCeil;
import org.star_lang.star.operators.arith.runtime.IntUnary.IntExp;
import org.star_lang.star.operators.arith.runtime.IntUnary.IntFloor;
import org.star_lang.star.operators.arith.runtime.IntUnary.IntLog;
import org.star_lang.star.operators.arith.runtime.IntUnary.IntLog10;
import org.star_lang.star.operators.arith.runtime.IntUnary.IntRandom;
import org.star_lang.star.operators.arith.runtime.IntUnary.IntRound;
import org.star_lang.star.operators.arith.runtime.IntUnary.IntSqrt;
import org.star_lang.star.operators.arith.runtime.IntUnary.IntUMinus;

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

public abstract class IntUnary
{
  public static final String UMINUS = "__integer_uminus";

  public static void declare(Intrinsics cxt)
  {
    IType intType = StandardTypes.rawIntegerType;
    IType type = TypeUtils.functionType(intType, intType);
    cxt.declareBuiltin(new Builtin("__integer_abs", type, IntAbs.class));
    cxt.declareBuiltin(new Builtin(UMINUS, type, IntUMinus.class));
    cxt.declareBuiltin(new Builtin("__integer_random", type, IntRandom.class));
    cxt.declareBuiltin(new Builtin("__integer_sqrt", type, IntSqrt.class));
    cxt.declareBuiltin(new Builtin("__integer_cbrt", type, IntCbrt.class));
    cxt.declareBuiltin(new Builtin("__integer_ceil", type, IntCeil.class));
    cxt.declareBuiltin(new Builtin("__integer_floor", type, IntFloor.class));
    cxt.declareBuiltin(new Builtin("__integer_round", type, IntRound.class));
    cxt.declareBuiltin(new Builtin("__integer_log", type, IntLog.class));
    cxt.declareBuiltin(new Builtin("__integer_log10", type, IntLog10.class));
    cxt.declareBuiltin(new Builtin("__integer_exp", type, IntExp.class));
  }
}
