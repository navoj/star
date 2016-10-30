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
import org.star_lang.star.operators.arith.runtime.FloatUnary.FloatHash;
import org.star_lang.star.operators.arith.runtime.FloatUnary.FloatLog;
import org.star_lang.star.operators.arith.runtime.FloatUnary.FloatLog10;
import org.star_lang.star.operators.arith.runtime.FloatUnary.FloatRandom;
import org.star_lang.star.operators.arith.runtime.FloatUnary.FloatRound;
import org.star_lang.star.operators.arith.runtime.FloatUnary.FloatSqrt;
import org.star_lang.star.operators.arith.runtime.FloatUnary.FloatUMinus;

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
    cxt.declareBuiltin(new Builtin(FloatHash.name, FloatHash.type(), FloatHash.class));
  }
}
