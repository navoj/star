package org.star_lang.star.operators.spawn;

import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.spawn.runtime.ForkJoin;
import org.star_lang.star.operators.spawn.runtime.SpawnIdent;
import org.star_lang.star.operators.spawn.runtime.NotifyWait.Notify;
import org.star_lang.star.operators.spawn.runtime.NotifyWait.Wait;
import org.star_lang.star.operators.spawn.runtime.SpawnOps.SpawnAction;
import org.star_lang.star.operators.spawn.runtime.SpawnOps.SpawnDelayedAction;
import org.star_lang.star.operators.spawn.runtime.SpawnOps.SpawnExp;
import org.star_lang.star.operators.spawn.runtime.SpawnOps.SpawnQueuedAction;
import org.star_lang.star.operators.spawn.runtime.SpawnOps.Waitfor;

public class SpawnOps
{
  public static void declare(Intrinsics cxt)
  {
    SpawnIdent.declare(cxt);
    cxt.declareBuiltin(new Builtin(SpawnExp.name, SpawnExp.type(), SpawnExp.class));
    cxt.declareBuiltin(new Builtin(SpawnAction.name, SpawnAction.type(), SpawnAction.class));
    cxt.declareBuiltin(new Builtin(SpawnQueuedAction.name, SpawnQueuedAction.type(), SpawnQueuedAction.class));
    cxt.declareBuiltin(new Builtin(SpawnDelayedAction.name, SpawnDelayedAction.type(), SpawnDelayedAction.class));
    cxt.declareBuiltin(new Builtin(Waitfor.name, Waitfor.type(), Waitfor.class));
    cxt.declareBuiltin(new Builtin(Wait.name, Wait.type(), Wait.class));
    cxt.declareBuiltin(new Builtin(Notify.name, Notify.type(), Notify.class));

    ForkJoin.declare(cxt);
  }
}
