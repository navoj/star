package org.star_lang.star.compiler.transform;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.star_lang.star.compiler.canonical.IContentExpression;
import org.star_lang.star.compiler.canonical.Variable;

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
    return new InlineContext(new TreeMap<String, IContentExpression>(varMap), new HashSet<String>(excludes));
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
