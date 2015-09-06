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

public abstract class IntUnary {
  public static final String UMINUS = "__integer_uminus";

  public static void declare(Intrinsics cxt) {
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
