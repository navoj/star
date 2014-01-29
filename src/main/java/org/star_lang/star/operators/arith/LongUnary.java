package org.star_lang.star.operators.arith;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.arith.runtime.LongUnary.LongAbs;
import org.star_lang.star.operators.arith.runtime.LongUnary.LongCbrt;
import org.star_lang.star.operators.arith.runtime.LongUnary.LongCeil;
import org.star_lang.star.operators.arith.runtime.LongUnary.LongExp;
import org.star_lang.star.operators.arith.runtime.LongUnary.LongFloor;
import org.star_lang.star.operators.arith.runtime.LongUnary.LongLog;
import org.star_lang.star.operators.arith.runtime.LongUnary.LongLog10;
import org.star_lang.star.operators.arith.runtime.LongUnary.LongRandom;
import org.star_lang.star.operators.arith.runtime.LongUnary.LongRound;
import org.star_lang.star.operators.arith.runtime.LongUnary.LongSqrt;
import org.star_lang.star.operators.arith.runtime.LongUnary.LongUMinus;

import com.starview.platform.data.type.IType;
import com.starview.platform.data.type.StandardTypes;

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
public abstract class LongUnary
{
  public static final String UMINUS = "__long_uminus";
  private static final IType type;

  static {
    IType longType = StandardTypes.rawLongType;
    type = TypeUtils.functionType(longType, longType);
  }

  public static void declare(Intrinsics cxt)
  {
    cxt.declareBuiltin(new Builtin("__long_abs", type, LongAbs.class));
    cxt.declareBuiltin(new Builtin(UMINUS, type, LongUMinus.class));
    cxt.declareBuiltin(new Builtin("__long_random", type, LongRandom.class));
    cxt.declareBuiltin(new Builtin("__long_sqrt", type, LongSqrt.class));
    cxt.declareBuiltin(new Builtin("__long_cbrt", type, LongCbrt.class));
    cxt.declareBuiltin(new Builtin("__long_ceil", type, LongCeil.class));
    cxt.declareBuiltin(new Builtin("__long_floor", type, LongFloor.class));
    cxt.declareBuiltin(new Builtin("__long_round", type, LongRound.class));
    cxt.declareBuiltin(new Builtin("__long_log", type, LongLog.class));
    cxt.declareBuiltin(new Builtin("__long_log10", type, LongLog10.class));
    cxt.declareBuiltin(new Builtin("__long_exp", type, LongExp.class));
  }
}
