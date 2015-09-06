package org.star_lang.star.operators.arith;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.arith.runtime.BigNumUnary.DecimalAbs;
import org.star_lang.star.operators.arith.runtime.BigNumUnary.DecimalRandom;
import org.star_lang.star.operators.arith.runtime.BigNumUnary.DecimalSqrt;
import org.star_lang.star.operators.arith.runtime.BigNumUnary.DecimalUMinus;

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

public abstract class BigNumUnary
{

  public static void declare(Intrinsics cxt)
  {
    IType decimalType = StandardTypes.rawDecimalType;
    IType type = TypeUtils.functionType(decimalType, decimalType);

    cxt.declareBuiltin(new Builtin("__decimal_abs", type, DecimalAbs.class));
    cxt.declareBuiltin(new Builtin("__decimal_uminus", type, DecimalUMinus.class));
    cxt.declareBuiltin(new Builtin("__decimal_random", type, DecimalRandom.class));
    cxt.declareBuiltin(new Builtin("__decimal_sqrt", type, DecimalSqrt.class));
  }
}
