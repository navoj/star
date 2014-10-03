package org.star_lang.star.compiler.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

/*
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
public class Template
{
  private final List<TemplateFragment> fragments;
  private final Set<String> vars;

  public Template(List<TemplateFragment> fragments, Set<String> vars)
  {
    this.fragments = fragments;
    this.vars = vars;
  }

  public Set<String> getVars()
  {
    return vars;
  }

  public String applyTemplate(Map<String, String> vars)
  {
    StringBuilder blder = new StringBuilder();
    for (TemplateFragment frag : fragments)
      frag.apply(vars, blder);
    return blder.toString();
  }

  abstract static class TemplateFragment
  {
    abstract void apply(Map<String, String> vars, StringBuilder blder);
  }

  public static class LitFragment extends TemplateFragment
  {
    private final String lit;

    LitFragment(String lit)
    {
      this.lit = lit;
    }

    @Override
    void apply(Map<String, String> vars, StringBuilder blder)
    {
      blder.append(lit);
    }
  }

  public static class VarFragment extends TemplateFragment
  {
    private final String var;

    VarFragment(String var)
    {
      this.var = var;
    }

    @Override
    void apply(Map<String, String> vars, StringBuilder blder)
    {
      blder.append(vars.get(var));
    }
  }
}