package org.star_lang.star.operators.system;

import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.system.runtime.StopHere;

public class GStopHere
{
  public static void declare(Intrinsics cxt)
  {
    cxt.declareBuiltin(new Builtin(StopHere.name, StopHere.type(), StopHere.class));
  }
}
