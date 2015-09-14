package org.star_lang.star.operators.string;

import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.string.runtime.DateOps.Date2String;
import org.star_lang.star.operators.string.runtime.DateOps.FormatDate;
import org.star_lang.star.operators.string.runtime.DateOps.ParseDate;
import org.star_lang.star.operators.string.runtime.DateOps.String2Date;

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
