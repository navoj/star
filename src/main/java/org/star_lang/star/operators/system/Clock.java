package org.star_lang.star.operators.system;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.system.runtime.Clock.Nanos;
import org.star_lang.star.operators.system.runtime.Clock.Now;
import org.star_lang.star.operators.system.runtime.Clock.Today;

public class Clock
{
  public static void declare(Intrinsics cxt)
  {
    cxt.declareBuiltin(new Builtin("nanos", TypeUtils.functionType(StandardTypes.longType), Nanos.class));
    cxt.declareBuiltin(new Builtin("_now", Now.funType(), Now.class));
    cxt.declareBuiltin(new Builtin("_today", Today.funType(), Today.class));
  }
}
