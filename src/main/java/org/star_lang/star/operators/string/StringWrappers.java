package org.star_lang.star.operators.string;

import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.string.runtime.StringWrappers.Raw2String;
import org.star_lang.star.operators.string.runtime.StringWrappers.String2Raw;
import org.star_lang.star.operators.string.runtime.StringWrappers.UnwrapChar;
import org.star_lang.star.operators.string.runtime.StringWrappers.UnwrapCharacter;
import org.star_lang.star.operators.string.runtime.StringWrappers.WrapChar;
import org.star_lang.star.operators.string.runtime.StringWrappers.WrapCharacter;

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
