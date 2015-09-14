package org.star_lang.star.operators.string;

import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.string.runtime.Number2String.Boolean2String;
import org.star_lang.star.operators.string.runtime.Number2String.Char2String;
import org.star_lang.star.operators.string.runtime.Number2String.Decimal2String;
import org.star_lang.star.operators.string.runtime.Number2String.Float2String;
import org.star_lang.star.operators.string.runtime.Number2String.FormatFloat;
import org.star_lang.star.operators.string.runtime.Number2String.FormatInteger;
import org.star_lang.star.operators.string.runtime.Number2String.FormatLong;
import org.star_lang.star.operators.string.runtime.Number2String.Integer2Hex;
import org.star_lang.star.operators.string.runtime.Number2String.Integer2String;
import org.star_lang.star.operators.string.runtime.Number2String.Long2Hex;
import org.star_lang.star.operators.string.runtime.Number2String.Long2String;

public abstract class Number2String
{
  public static void declare(Intrinsics cxt)
  {
    cxt.declareBuiltin(new Builtin(Boolean2String.name, Boolean2String.type(), Boolean2String.class));
    cxt.declareBuiltin(new Builtin(Char2String.name, Char2String.type(), Char2String.class));
    cxt.declareBuiltin(new Builtin(Integer2String.name, Integer2String.type(), Integer2String.class));
    cxt.declareBuiltin(new Builtin(Integer2Hex.name, Integer2Hex.type(), Integer2Hex.class));
    cxt.declareBuiltin(new Builtin(Long2Hex.name, Long2Hex.type(), Long2Hex.class));
    cxt.declareBuiltin(new Builtin(Long2String.name, Long2String.type(), Long2String.class));
    cxt.declareBuiltin(new Builtin(Float2String.name, Float2String.type(), Float2String.class));
    cxt.declareBuiltin(new Builtin(Decimal2String.name, Decimal2String.type(), Decimal2String.class));

    cxt.declareBuiltin(new Builtin(FormatInteger.name, FormatInteger.type(), FormatInteger.class));
    cxt.declareBuiltin(new Builtin(FormatLong.name, FormatLong.type(), FormatLong.class));
    cxt.declareBuiltin(new Builtin(FormatFloat.name, FormatFloat.type(), FormatFloat.class));
  }
}
