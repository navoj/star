package org.star_lang.star.operators.system;

import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.system.runtime.BinaryWrappers.UnwrapBinary;
import org.star_lang.star.operators.system.runtime.BinaryWrappers.WrapBinary;
import org.star_lang.star.operators.system.runtime.RawWrappers.UnwrapRaw;
import org.star_lang.star.operators.system.runtime.RawWrappers.WrapRaw;

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
public class BinaryWrappers
{
  public static void declare(Intrinsics cxt)
  {
    cxt.declareBuiltin(new Builtin(WrapBinary.WRAP_BINARY, WrapBinary.type(), WrapBinary.class));
    cxt.declareBuiltin(new Builtin(UnwrapBinary.UNWRAP_BINARY, UnwrapBinary.type(), UnwrapBinary.class));

    cxt.declareBuiltin(new Builtin(WrapRaw.WRAP_RAW, WrapRaw.type(), WrapRaw.class));
    cxt.declareBuiltin(new Builtin(UnwrapRaw.UNWRAP_RAW, UnwrapRaw.type(), UnwrapRaw.class));
  }
}
