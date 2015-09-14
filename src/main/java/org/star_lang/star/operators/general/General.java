package org.star_lang.star.operators.general;

import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.general.runtime.Assert;
import org.star_lang.star.operators.general.runtime.GeneralEq;
import org.star_lang.star.operators.general.runtime.Raise;

/**
 * Some general primitive functions
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

public abstract class General
{
  public static void declare(Intrinsics cxt)
  {
    cxt.declareBuiltin(new Builtin(GeneralEq.name, GeneralEq.type(), GeneralEq.class));
    cxt.declareBuiltin(new Builtin(Assert.name, Assert.type(), Assert.class));
    cxt.declareBuiltin(new Builtin(Raise.name, Raise.type(), Raise.class));
  }
}
