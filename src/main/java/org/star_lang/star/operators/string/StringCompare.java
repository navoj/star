package org.star_lang.star.operators.string;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.transform.PrimitiveOverloader;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.string.runtime.StringCompare.StringEQ;
import org.star_lang.star.operators.string.runtime.StringCompare.StringGE;
import org.star_lang.star.operators.string.runtime.StringCompare.StringGT;
import org.star_lang.star.operators.string.runtime.StringCompare.StringLE;
import org.star_lang.star.operators.string.runtime.StringCompare.StringLT;
import org.star_lang.star.operators.string.runtime.StringCompare.StringNE;

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

public class StringCompare extends Builtin
{
  public static final String STRING_EQ = "__string_eq";

  private static final IType rawStringStype = StandardTypes.rawStringType;

  private StringCompare(String name, Class<?> implClass)
  {
    super(name, TypeUtils.functionType(rawStringStype, rawStringStype, StandardTypes.booleanType), implClass);
  }

  public static void declare(Intrinsics cxt)
  {
    String equality = StandardNames.EQUALITY;
    PrimitiveOverloader.declarePrimitiveImplementation(equality, StandardNames.EQUAL, rawStringStype, STRING_EQ);
    cxt.declareBuiltin(new StringCompare(STRING_EQ, StringEQ.class));

    cxt.declareBuiltin(new StringCompare("__string_ne", StringNE.class));
    PrimitiveOverloader.declarePrimitiveImplementation(equality, StandardNames.NOT_EQUAL, rawStringStype, "__string_ne");

    cxt.declareBuiltin(new StringCompare("__string_lt", StringLT.class));
    cxt.declareBuiltin(new StringCompare("__string_le", StringLE.class));
    cxt.declareBuiltin(new StringCompare("__string_ge", StringGE.class));
    cxt.declareBuiltin(new StringCompare("__string_gt", StringGT.class));
  }
}