package org.star_lang.star.operators.string;

import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.string.runtime.StringWrappers.Raw2String;
import org.star_lang.star.operators.string.runtime.StringWrappers.String2Raw;

public class StringWrappers
{
  public static void declare(Intrinsics cxt)
  {
    cxt.declareBuiltin(new Builtin(Raw2String.WRAP_STRING, Raw2String.type(), Raw2String.class));
    cxt.declareBuiltin(new Builtin(String2Raw.UNWRAP_STRING, String2Raw.type(), String2Raw.class));
  }
}
