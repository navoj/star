package org.star_lang.star.operators.arith;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.arith.runtime.BigNumUnary.DecimalAbs;
import org.star_lang.star.operators.arith.runtime.BigNumUnary.DecimalRandom;
import org.star_lang.star.operators.arith.runtime.BigNumUnary.DecimalSqrt;
import org.star_lang.star.operators.arith.runtime.BigNumUnary.DecimalUMinus;

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
