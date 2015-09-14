package org.star_lang.star.operators;

import java.lang.reflect.Method;

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
public class Builtins
{

  public static Method findCafeEnter(String name)
  {
    ICafeBuiltin builtin = Intrinsics.getBuiltin(name);
    if (builtin != null)
      return findCafeEnter(builtin);
    return null;
  }

  public static Method findCafeEnter(ICafeBuiltin builtin)
  {
    for (Method m : builtin.getImplClass().getMethods()) {
      if (m.isAnnotationPresent(CafeEnter.class))
        return m;
    }

    return null;
  }
}
