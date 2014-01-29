package org.star_lang.star.operators.string;

import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.string.runtime.DateOps.Date2String;
import org.star_lang.star.operators.string.runtime.DateOps.FormatDate;
import org.star_lang.star.operators.string.runtime.DateOps.ParseDate;
import org.star_lang.star.operators.string.runtime.DateOps.String2Date;

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
public class DateOps
{
  public static void declare(Intrinsics cxt)
  {
    cxt.declareBuiltin(new Builtin("__date_string", Date2String.funType(), Date2String.class));
    cxt.declareBuiltin(new Builtin("__string_date", String2Date.funType(), String2Date.class));
    cxt.declareBuiltin(new Builtin("__format_date", FormatDate.funType(), FormatDate.class));
    cxt.declareBuiltin(new Builtin("__parse_date", ParseDate.funType(), ParseDate.class));
  }
}
