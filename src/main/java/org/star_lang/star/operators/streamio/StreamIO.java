package org.star_lang.star.operators.streamio;

import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.streamio.runtime.InChnl;

/**
 * Created by fgm on 7/13/15.
 */
public class StreamIO {
  public static void declare(Intrinsics cxt)
  {
    InChnl.declare(cxt);
  }
}
