package org.star_lang.star.operators.system;

import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.system.runtime.LogMsg;

public class LogMsgs
{
  public static void declare(Intrinsics cxt)
  {
    cxt.declareBuiltin(new Builtin(LogMsg.name, LogMsg.__logMsgType(), LogMsg.class));
  }
}
