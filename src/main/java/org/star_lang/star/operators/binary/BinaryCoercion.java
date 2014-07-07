package org.star_lang.star.operators.binary;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.binary.runtime.BinaryCoercion.Binary2String;
import org.star_lang.star.operators.binary.runtime.BinaryCoercion.String2Binary;

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
public abstract class BinaryCoercion
{
  public static final String binary2String = "__binary_string";
  public static final String string2binary = "__string_binary";

  public static void declare(Intrinsics cxt)
  {
    cxt.declareBuiltin(new Builtin(binary2String, TypeUtils.functionType(StandardTypes.binaryType,StandardTypes.stringType), Binary2String.class));
    cxt.declareBuiltin(new Builtin(string2binary, TypeUtils.functionType(StandardTypes.stringType ,StandardTypes.binaryType), String2Binary.class));
  }
}
