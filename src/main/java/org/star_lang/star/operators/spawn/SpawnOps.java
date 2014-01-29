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

/**
 * 
 * Copyright (C) 2013 Starview Inc
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA
 * 
 * @author fgm
 *
 */
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
