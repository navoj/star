package org.star_lang.star.operators.string;

import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.string.runtime.Number2String.*;

public abstract class Number2String
{
  public static void declare(Intrinsics cxt)
  {
    cxt.declareBuiltin(new Builtin(Boolean2String.name, Boolean2String.type(), Boolean2String.class));
    cxt.declareBuiltin(new Builtin(CodePoint2String.name, CodePoint2String.type(), CodePoint2String.class));
    cxt.declareBuiltin(new Builtin(Integer2String.name, Integer2String.type(), Integer2String.class));
    cxt.declareBuiltin(new Builtin(Integer2Hex.name, Integer2Hex.type(), Integer2Hex.class));
    cxt.declareBuiltin(new Builtin(Long2Hex.name, Long2Hex.type(), Long2Hex.class));
    cxt.declareBuiltin(new Builtin(Long2String.name, Long2String.type(), Long2String.class));
    cxt.declareBuiltin(new Builtin(Float2String.name, Float2String.type(), Float2String.class));

    cxt.declareBuiltin(new Builtin(FormatInteger.name, FormatInteger.type(), FormatInteger.class));
    cxt.declareBuiltin(new Builtin(FormatLong.name, FormatLong.type(), FormatLong.class));
    cxt.declareBuiltin(new Builtin(FormatFloat.name, FormatFloat.type(), FormatFloat.class));
  }
}
