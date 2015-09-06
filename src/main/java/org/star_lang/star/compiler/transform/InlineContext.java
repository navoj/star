package org.star_lang.star.compiler.transform;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.star_lang.star.compiler.canonical.IContentExpression;
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

public class InlineContext
{
  private final Map<String, IContentExpression> varMap;
  private final Set<String> excludes;

  public InlineContext(Map<String, IContentExpression> varMap, Set<String> excludes)
  {
    this.varMap = varMap;
    this.excludes = excludes;
  }

  public InlineContext fork()
  {
    return new InlineContext(new TreeMap<>(varMap), new HashSet<>(excludes));
  }

  void exclude(Collection<String> excl)
  {
    excludes.addAll(excl);
  }

  boolean isExcluded(String vName)
  {
    return excludes.contains(vName);
  }

  IContentExpression replaceVar(Variable var)
  {
    String vName = var.getName();
    if (!isExcluded(vName)) {
      IContentExpression repl = varMap.get(vName);
      if (repl != null)
        return repl;
    }
    return var;
  }
}
