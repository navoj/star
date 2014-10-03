package org.star_lang.star.compiler.format.rules;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;

/**
 * A collection of validation rules that may be used to validate an expression
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
@SuppressWarnings("serial")
public class FmtProgram implements PrettyPrintable
{
  private final FmtProgram parent;
  private Map<String, FmtRuleSet> rules = new HashMap<String, FmtRuleSet>();

  public FmtProgram()
  {
    this.parent = null;
  }

  private FmtProgram(FmtProgram parent)
  {
    this.parent = parent;
  }

  public void defineFormatRule(IAbstract aRule, ErrorReport errors)
  {
    FmtRule rule = FmtCompile.compileRule(aRule, this, errors);
    if (rule != null)
      defineFormattingRule(rule);
  }

  public void defineFormattingRule(FmtRule rule)
  {
    String category = rule.getCategory();
    FmtRuleSet rles = rules.get(category);
    if (rles == null) {
      rles = new FmtRuleSet(category);
      rules.put(category, rles);
    }
    rles.defineRule(rule);
  }

  private void defineRules(FmtRuleSet ruleset)
  {
    String category = ruleset.category;
    FmtRuleSet local = rules.get(category);
    if (local == null) {
      local = new FmtRuleSet(category);
      rules.put(category, local);
    }
    local.importRules(ruleset.rules);
  }

  public FmtProgram fork()
  {
    return new FmtProgram(this);
  }

  /**
   * Merge in the validation ruleset of another ruleset with this one
   * 
   * @param other
   */
  public void importRules(FmtProgram other)
  {
    while (other != null && other != this) {
      for (Entry<String, FmtRuleSet> entry : other.rules.entrySet()) {
        FmtRuleSet ruleSet = rules.get(entry.getKey());

        FmtRuleSet importedSet = entry.getValue();

        if (ruleSet != null)
          ruleSet.importRules(importedSet.rules);
        else
          defineRules(importedSet);
      }
      other = other.parent;
    }
  }

  public Collection<FmtRule> rulesFor(String name)
  {
    FmtProgram program = this;
    List<FmtRule> rules = null;
    boolean overridden = false;

    while (program != null) {
      FmtRuleSet lst = program.rules.get(name);

      if (lst != null) {
        if (rules == null)
          rules = lst.rules;
        else {
          if (!overridden) {
            rules = new ArrayList<FmtRule>(rules);
            overridden = true;
          }
          rules.addAll(lst.rules);
        }
      }
      program = program.parent;
    }

    return rules;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    int mark = disp.markIndent(2);
    disp.append("fmt{\n");
    for (Entry<String, FmtRuleSet> entry : rules.entrySet()) {
      FmtRuleSet ruleSet = entry.getValue();
      for (FmtRule rule : ruleSet.rules) {
        rule.prettyPrint(disp);
        disp.append(";\n");
      }
    }
    disp.popIndent(mark);
    disp.append("}\n");
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

  private class FmtRuleSet implements Serializable
  {
    private final List<FmtRule> rules = new ArrayList<FmtRule>();
    private final String category;

    FmtRuleSet(String category)
    {
      this.category = category;
    }

    void defineRule(FmtRule rule)
    {
      rules.add(rule);
    }

    void importRules(Collection<FmtRule> imported)
    {
      for (FmtRule rule : imported)
        if (!rules.contains(rule))
          rules.add(rule);
    }
  }
}
