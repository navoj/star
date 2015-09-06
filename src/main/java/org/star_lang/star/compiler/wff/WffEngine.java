package org.star_lang.star.compiler.wff;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.util.Bag;
import org.star_lang.star.compiler.wff.WffOp.applyMode;
import org.star_lang.star.compiler.wff.WffProgram.WffRuleSet;
import org.star_lang.star.data.type.Location;

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


public class WffEngine
{
  public static final boolean traceValidation = System.getProperty("TRACE_VALIDATION", "false")
      .equalsIgnoreCase("true");

  Map<String, Set<IAbstract>> assertions = new HashMap<>();
  private final ErrorReport errors;
  private final Stack<WffProgram> wffProgramStack = new Stack<>();

  public WffEngine(ErrorReport errors, WffProgram wffProgram)
  {
    this.errors = errors;
    wffProgramStack.push(wffProgram);
  }

  public WffOp.applyMode validate(IAbstract term, String category)
  {
    Collection<WffRule> rules = rulesFor(category);

    if (rules == null)
      reportError("cannot find validation rules for category " + category, term.getLoc());
    else
    {
      for (WffRule rule : rules)
      {
        WffOp.applyMode mode = rule.validate(term, this);
        switch (mode)
        {
          case validates:
            term.setCategory(category);
            return mode;
          case notApply:
            continue;
          case notValidates:
            // reportError("`" + term.toString() + "' not a valid " + category,
            // term.getLoc());
            return mode;
        }
      }

      reportError("`" + term.toString() + "' not a valid " + category, term.getLoc());
    }
    return WffOp.applyMode.notValidates;
  }

  public Bag<WffRule> rulesFor(String name)
  {
    Bag<WffRule> rules = null;
    boolean overridden = false;

    for (int ix = wffProgramStack.size(); ix > 0; ix--)
    {
      WffRuleSet lst = wffProgramStack.get(ix - 1).rulesFor(name);

      if (lst != null)
      {
        if (rules == null)
          rules = lst.getRules();
        else
        {
          if (!overridden)
          {
            rules = new Bag<>(rules);
            overridden = true;
          }
          rules.addAll(lst.getRules());
        }
      }
    }

    return rules;
  }

  public int pushRules(WffProgram rules)
  {
    int mark = wffProgramStack.size();
    wffProgramStack.push(rules);
    return mark;
  }

  public void reset(int mark)
  {
    wffProgramStack.setSize(mark);
  }

  public void assrt(IAbstract term, String label)
  {
    Set<IAbstract> line = assertions.get(label);
    if (line == null)
    {
      line = new HashSet<>();
      assertions.put(label, line);
    }
    line.add(term);
  }

  public void retract(IAbstract term, String label)
  {
    Set<IAbstract> line = assertions.get(label);
    if (line != null)
      line.remove(term);
  }

  public applyMode exists(IAbstract term, String label)
  {
    Set<IAbstract> line = assertions.get(label);
    if (line != null)
      return line.contains(term) ? applyMode.validates : applyMode.notValidates;
    else
      return applyMode.notValidates;
  }

  protected void reportError(String msg, Location loc)
  {
    errors.reportError(msg, loc);
  }

  protected void reportWarning(String msg, Location loc)
  {
    errors.reportWarning(msg, loc);
  }

  protected void reportInfo(String msg, Location loc)
  {
    errors.reportInfo(msg, loc);
  }
}
