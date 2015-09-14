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

/*
  * Copyright (c) 2015. Francis G. McCabe
  *
  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software distributed under the
  * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  * KIND, either express or implied. See the License for the specific language governing
  * permissions and limitations under the License.
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
