package org.star_lang.star.compiler.canonical;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

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
public class CopyTransformer extends DefaultTransformer<Collection<Variable>>
{
  private final Map<Variable, Canonical> map;

  public CopyTransformer(Map<Variable, Canonical> map)
  {
    this.map = map;
  }

  @Override
  public IContentExpression transformFunctionLiteral(FunctionLiteral f, Collection<Variable> context)
  {
    return super.transformFunctionLiteral(f, context);
  }

  @Override
  public IContentExpression transformLetTerm(LetTerm let, Collection<Variable> context)
  {
    Collection<Variable> exclusions = new ArrayList<>(context);
    for (IStatement stmt : let.getEnvironment()) {
      for (Entry<Variable, Canonical> entry : map.entrySet()) {
        if (stmt.defines(entry.getKey().getName()))
          exclusions.add(entry.getKey());
      }
    }
    return super.transformLetTerm(let, exclusions);
  }

  @Override
  public IContentExpression transformVariable(Variable var, Collection<Variable> exclusions)
  {
    if (!exclusions.contains(var) && map.containsKey(var))
      return (IContentExpression) map.get(var);
    else
      return var;
  }

  @Override
  public IContentAction transformLetAction(LetAction let, Collection<Variable> context)
  {
    Collection<Variable> exclusions = new ArrayList<>(context);
    for (IStatement stmt : let.getEnvironment()) {
      for (Entry<Variable, Canonical> entry : map.entrySet()) {
        if (stmt.defines(entry.getKey().getName()))
          exclusions.add(entry.getKey());
      }
    }
    return super.transformLetAction(let, exclusions);
  }

  @Override
  public IContentPattern transformVariablePtn(Variable var, Collection<Variable> exclusions)
  {
    if (!exclusions.contains(var) && map.containsKey(var))
      return (IContentPattern) map.get(var);
    else
      return var;
  }
}
