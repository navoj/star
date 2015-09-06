package org.star_lang.star.operators.arith;

import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.arith.runtime.NumericWrapper.UnwrapBool;
import org.star_lang.star.operators.arith.runtime.NumericWrapper.UnwrapBoolean;
import org.star_lang.star.operators.arith.runtime.NumericWrapper.UnwrapDbl;
import org.star_lang.star.operators.arith.runtime.NumericWrapper.UnwrapDouble;
import org.star_lang.star.operators.arith.runtime.NumericWrapper.UnwrapFloat;
import org.star_lang.star.operators.arith.runtime.NumericWrapper.UnwrapFlt;
import org.star_lang.star.operators.arith.runtime.NumericWrapper.UnwrapInt;
import org.star_lang.star.operators.arith.runtime.NumericWrapper.UnwrapInteger;
import org.star_lang.star.operators.arith.runtime.NumericWrapper.UnwrapLng;
import org.star_lang.star.operators.arith.runtime.NumericWrapper.UnwrapLong;
import org.star_lang.star.operators.arith.runtime.NumericWrapper.WrapBool;
import org.star_lang.star.operators.arith.runtime.NumericWrapper.WrapBoolean;
import org.star_lang.star.operators.arith.runtime.NumericWrapper.WrapDbl;
import org.star_lang.star.operators.arith.runtime.NumericWrapper.WrapDouble;
import org.star_lang.star.operators.arith.runtime.NumericWrapper.WrapFloat;
import org.star_lang.star.operators.arith.runtime.NumericWrapper.WrapFlt;
import org.star_lang.star.operators.arith.runtime.NumericWrapper.WrapInt;
import org.star_lang.star.operators.arith.runtime.NumericWrapper.WrapInteger;
import org.star_lang.star.operators.arith.runtime.NumericWrapper.WrapLng;
import org.star_lang.star.operators.arith.runtime.NumericWrapper.WrapLong;

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
public class NumericWrapper {
  public static final String UNWRAP_CHAR = "__unwrap_char";
  public static final String WRAP_CHAR = "__wrap_char";
  public static final String UNWRAP_CHARACTER = "__unwrap_character";
  public static final String WRAP_CHARACTER = "__wrap_character";

  public static void declare(Intrinsics cxt) {
    cxt.declareBuiltin(new Builtin(WrapBool.WRAP_BOOL, WrapBool.type(), WrapBool.class));
    cxt.declareBuiltin(new Builtin(UnwrapBool.UNWRAP_BOOL, UnwrapBool.type(), UnwrapBool.class));
    cxt.declareBuiltin(new Builtin(WrapBoolean.WRAP_BOOLEAN, WrapBoolean.type(), WrapBoolean.class));
    cxt.declareBuiltin(new Builtin(UnwrapBoolean.UNWRAP_BOOLEAN, UnwrapBoolean.type(), UnwrapBoolean.class));
    cxt.declareBuiltin(new Builtin(WrapInt.WRAP_INT, WrapInt.type(), WrapInt.class));
    cxt.declareBuiltin(new Builtin(UnwrapInt.UNWRAP_INT, UnwrapInt.type(), UnwrapInt.class));
    cxt.declareBuiltin(new Builtin(WrapInteger.WRAP_INTEGER, WrapInteger.type(), WrapInteger.class));
    cxt.declareBuiltin(new Builtin(UnwrapInteger.UNWRAP_INTEGER, UnwrapInteger.type(), UnwrapInteger.class));
    cxt.declareBuiltin(new Builtin(WrapLng.WRAP_LNG, WrapLng.type(), WrapLng.class));
    cxt.declareBuiltin(new Builtin(UnwrapLng.UNWRAP_LNG, UnwrapLng.type(), UnwrapLng.class));
    cxt.declareBuiltin(new Builtin(WrapLong.WRAP_LONG, WrapLong.type(), WrapLong.class));
    cxt.declareBuiltin(new Builtin(UnwrapLong.UNWRAP_LONG, UnwrapLong.type(), UnwrapLong.class));
    cxt.declareBuiltin(new Builtin(WrapFlt.WRAP_FLT, WrapFlt.type(), WrapFlt.class));
    cxt.declareBuiltin(new Builtin(UnwrapFlt.UNWRAP_FLT, UnwrapFlt.type(), UnwrapFlt.class));
    cxt.declareBuiltin(new Builtin(WrapFloat.WRAP_FLOAT, WrapFloat.type(), WrapFloat.class));
    cxt.declareBuiltin(new Builtin(UnwrapFloat.UNWRAP_FLOAT, UnwrapFloat.type(), UnwrapFloat.class));
    cxt.declareBuiltin(new Builtin(WrapDbl.WRAP_DBL, WrapDbl.type(), WrapDbl.class));
    cxt.declareBuiltin(new Builtin(UnwrapDbl.UNWRAP_DBL, UnwrapDbl.type(), UnwrapDbl.class));
    cxt.declareBuiltin(new Builtin(WrapDouble.WRAP_DOUBLE, WrapDouble.type(), WrapDouble.class));
    cxt.declareBuiltin(new Builtin(UnwrapDouble.UNWRAP_DOUBLE, UnwrapDouble.type(), UnwrapDouble.class));
  }
}
