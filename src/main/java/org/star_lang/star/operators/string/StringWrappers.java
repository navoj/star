package org.star_lang.star.operators.string;

import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.string.runtime.StringWrappers.Raw2String;
import org.star_lang.star.operators.string.runtime.StringWrappers.String2Raw;
import org.star_lang.star.operators.string.runtime.StringWrappers.UnwrapChar;
import org.star_lang.star.operators.string.runtime.StringWrappers.UnwrapCharacter;
import org.star_lang.star.operators.string.runtime.StringWrappers.WrapChar;
import org.star_lang.star.operators.string.runtime.StringWrappers.WrapCharacter;

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
public class StringWrappers
{
  public static void declare(Intrinsics cxt)
  {
    cxt.declareBuiltin(new Builtin(Raw2String.WRAP_STRING, Raw2String.type(), Raw2String.class));
    cxt.declareBuiltin(new Builtin(String2Raw.UNWRAP_STRING, String2Raw.type(), String2Raw.class));
    cxt.declareBuiltin(new Builtin(WrapChar.WRAP_CHAR, WrapChar.type(), WrapChar.class));
    cxt.declareBuiltin(new Builtin(WrapCharacter.WRAP_CHAR, WrapCharacter.type(), WrapCharacter.class));
    cxt.declareBuiltin(new Builtin(UnwrapChar.UNWRAP_CHAR, UnwrapChar.type(), UnwrapChar.class));
    cxt.declareBuiltin(new Builtin(UnwrapCharacter.UNWRAP_CHAR, UnwrapCharacter.type(), UnwrapCharacter.class));
  }
}
