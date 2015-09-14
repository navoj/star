package org.star_lang.star.operators.system;

import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.system.runtime.BinaryWrappers.UnwrapBinary;
import org.star_lang.star.operators.system.runtime.BinaryWrappers.WrapBinary;
import org.star_lang.star.operators.system.runtime.RawWrappers.UnwrapRaw;
import org.star_lang.star.operators.system.runtime.RawWrappers.WrapRaw;

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
