package org.star_lang.star.operators.system;

import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.system.runtime.SimpleLog;
import org.star_lang.star.operators.system.runtime.Sleep;
import org.star_lang.star.operators.system.runtime.SystemUtils.AvailableProcessors;
import org.star_lang.star.operators.system.runtime.SystemUtils.Exit;
import org.star_lang.star.operators.system.runtime.SystemUtils.FreeMemory;
import org.star_lang.star.operators.system.runtime.SystemUtils.Gc;
import org.star_lang.star.operators.system.runtime.SystemUtils.GetEnv;
import org.star_lang.star.operators.system.runtime.SystemUtils.MaxMemory;
import org.star_lang.star.operators.system.runtime.SystemUtils.TotalMemory;
import org.star_lang.star.operators.system.runtime.Yield;

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

public class SystemUtils
{
  public static void declare(Intrinsics cxt)
  {
    cxt.declareBuiltin(new Builtin(Exit.name, Exit.type(), Exit.class));
    cxt.declareBuiltin(new Builtin(GetEnv.name, GetEnv.type(), GetEnv.class));

    cxt.declareBuiltin(new Builtin(AvailableProcessors.name, AvailableProcessors.type(), AvailableProcessors.class));

    cxt.declareBuiltin(new Builtin(Gc.name, Gc.type(), Gc.class));
    cxt.declareBuiltin(new Builtin(FreeMemory.name, FreeMemory.type(), FreeMemory.class));
    cxt.declareBuiltin(new Builtin(TotalMemory.name, TotalMemory.type(), TotalMemory.class));
    cxt.declareBuiltin(new Builtin(MaxMemory.name, MaxMemory.type(), MaxMemory.class));

    cxt.declareBuiltin(new Builtin(Sleep.name, Sleep.type(), Sleep.class));
    cxt.declareBuiltin(new Builtin(Yield.name, Yield.type(), Yield.class));

    cxt.declareBuiltin(new Builtin(SimpleLog.name, SimpleLog.type(), SimpleLog.class));
  }
}
