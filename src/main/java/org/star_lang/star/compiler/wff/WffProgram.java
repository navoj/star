package org.star_lang.star.compiler.wff;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.util.Bag;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;

/**
 * A collection of validation rules that may be used to validate an expression
 *
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
@SuppressWarnings("serial")
public class WffProgram implements PrettyPrintable
{
  private Map<String, WffRuleSet> rules = new HashMap<>();

  public void defineValidationRule(IAbstract aRule, ErrorReport errors)
  {
    WffCompile compile = new WffCompile();
    WffRule rule = compile.compileRule(aRule, this, errors);
    if (rule != null)
      defineValidationRule(rule);
  }

  public void defineValidationRule(WffRule rule)
  {
    String category = rule.getCategory();
    WffRuleSet rles = rules.get(category);
    if (rles == null) {
      rles = new WffRuleSet(category);
      rules.put(category, rles);
    }
    rles.defineRule(rule);
  }

  private void defineRules(WffRuleSet ruleset)
  {
    String category = ruleset.category;
    WffRuleSet local = rules.get(category);
    if (local == null) {
      local = new WffRuleSet(category);
      rules.put(category, local);
    }
    local.importRules(ruleset.rules);
  }

  /**
   * Merge in the validation ruleset of another ruleset with this one
   * 
   * @param other
   */
  public void importRules(WffProgram other)
  {
    assert other != null && other != this;
    for (Entry<String, WffRuleSet> entry : other.rules.entrySet()) {
      WffRuleSet ruleSet = rules.get(entry.getKey());

      WffRuleSet importedSet = entry.getValue();

      if (ruleSet != null)
        ruleSet.importRules(importedSet.rules);
      else
        defineRules(importedSet);
    }
  }

  public WffRuleSet rulesFor(String name)
  {
    return rules.get(name);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    int mark = disp.markIndent(2);
    disp.append("wff{\n");
    for (Entry<String, WffRuleSet> entry : rules.entrySet()) {
      WffRuleSet ruleSet = entry.getValue();
      for (WffRule rule : ruleSet.rules) {
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

  public class WffRuleSet implements Serializable
  {
    private final Bag<WffRule> rules = new Bag<>(new RuleCompare());

    private final String category;

    WffRuleSet(String category)
    {
      this.category = category;
    }

    void defineRule(WffRule rule)
    {
      rules.add(rule);
    }

    public Bag<WffRule> getRules()
    {
      return rules;
    }

    void importRules(Collection<WffRule> imported)
    {
      for (WffRule rule : imported) {
        if (!rules.contains(rule))
          rules.add(rule);
      }
    }
  }

  private class RuleCompare implements Comparator<WffRule>, Serializable
  {
    @Override
    public int compare(WffRule o1, WffRule o2)
    {
      return (int) (o2.specificity() - o1.specificity());
    }
  }

}
