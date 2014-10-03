package org.star_lang.star.operators.string;

import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.string.runtime.String2Number.Hex2Integer;
import org.star_lang.star.operators.string.runtime.String2Number.Hex2Long;
import org.star_lang.star.operators.string.runtime.String2Number.String2Boolean;
import org.star_lang.star.operators.string.runtime.String2Number.String2Char;
import org.star_lang.star.operators.string.runtime.String2Number.String2Decimal;
import org.star_lang.star.operators.string.runtime.String2Number.String2Float;
import org.star_lang.star.operators.string.runtime.String2Number.String2Integer;
import org.star_lang.star.operators.string.runtime.String2Number.String2Long;

/**
 * The String2Number functions parse strings into different kinds of numbers.
 * 
 * This is the compile-time declarations of the functions
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

public abstract class String2Number
{

  public static void declare(Intrinsics cxt)
  {
    cxt.declareBuiltin(new Builtin(String2Boolean.name, String2Boolean.type(), String2Boolean.class));
    cxt.declareBuiltin(new Builtin(String2Char.name, String2Char.type(), String2Char.class));
    cxt.declareBuiltin(new Builtin(String2Integer.name, String2Integer.type(), String2Integer.class));
    cxt.declareBuiltin(new Builtin(String2Long.name, String2Long.type(), String2Long.class));
    cxt.declareBuiltin(new Builtin(Hex2Integer.name, Hex2Integer.type(), Hex2Integer.class));
    cxt.declareBuiltin(new Builtin(Hex2Long.name, Hex2Long.type(), Hex2Long.class));
    cxt.declareBuiltin(new Builtin(String2Float.name, String2Float.type(), String2Float.class));
    cxt.declareBuiltin(new Builtin(String2Decimal.name, String2Decimal.type(), String2Decimal.class));
  }
}
