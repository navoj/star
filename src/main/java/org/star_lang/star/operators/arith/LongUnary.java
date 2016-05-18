package org.star_lang.star.operators.arith;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.arith.runtime.LongUnary.*;

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
    cxt.declareBuiltin(new Builtin(LongHash.name, LongHash.type(), LongHash.class));
  }
}
