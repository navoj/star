package org.star_lang.star.compiler.canonical;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

/**
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
