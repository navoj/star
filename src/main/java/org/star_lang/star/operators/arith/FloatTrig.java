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

public abstract class FloatTrig
{

  public static void declare(Intrinsics cxt)
  {
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
