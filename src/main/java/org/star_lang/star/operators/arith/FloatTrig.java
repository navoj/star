package org.star_lang.star.operators.arith;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.arith.runtime.FloatTrig.FloatACos;
import org.star_lang.star.operators.arith.runtime.FloatTrig.FloatASin;
import org.star_lang.star.operators.arith.runtime.FloatTrig.FloatATan;
import org.star_lang.star.operators.arith.runtime.FloatTrig.FloatCos;
import org.star_lang.star.operators.arith.runtime.FloatTrig.FloatCosh;
import org.star_lang.star.operators.arith.runtime.FloatTrig.FloatSin;
import org.star_lang.star.operators.arith.runtime.FloatTrig.FloatSinh;
import org.star_lang.star.operators.arith.runtime.FloatTrig.FloatTan;
import org.star_lang.star.operators.arith.runtime.FloatTrig.FloatTanh;

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
public abstract class FloatTrig {

  public static void declare(Intrinsics cxt) {
    IType floatType = StandardTypes.rawFloatType;
    IType type = TypeUtils.functionType(floatType, floatType);

    cxt.declareBuiltin(new Builtin("__float_sin", type, FloatSin.class));
    cxt.declareBuiltin(new Builtin("__float_asin", type, FloatASin.class));
    cxt.declareBuiltin(new Builtin("__float_sinh", type, FloatSinh.class));
    cxt.declareBuiltin(new Builtin("__float_cos", type, FloatCos.class));
    cxt.declareBuiltin(new Builtin("__float_acos", type, FloatACos.class));
    cxt.declareBuiltin(new Builtin("__float_cosh", type, FloatCosh.class));
    cxt.declareBuiltin(new Builtin("__float_tan", type, FloatTan.class));
    cxt.declareBuiltin(new Builtin("__float_atan", type, FloatATan.class));
    cxt.declareBuiltin(new Builtin("__float_tanh", type, FloatTanh.class));
  }
}
