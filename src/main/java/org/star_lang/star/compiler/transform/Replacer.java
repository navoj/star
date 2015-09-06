package org.star_lang.star.compiler.transform;

import java.util.HashMap;
import java.util.Map;

import org.star_lang.star.compiler.canonical.Canonical;
import org.star_lang.star.compiler.canonical.CopyTransformer;
import org.star_lang.star.compiler.canonical.Variable;
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

public class Replacer extends CopyTransformer
{

  public Replacer(Variable old, Canonical rep)
  {
    super(createMap(old, rep));
  }

  private static Map<Variable, Canonical> createMap(Variable old, Canonical rep)
  {
    Map<Variable, Canonical> map = new HashMap<>();

    map.put(old, rep);
    return map;
  }
}
