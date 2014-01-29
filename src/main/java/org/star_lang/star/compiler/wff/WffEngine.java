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

import com.starview.platform.data.type.Location;


/**
 * 
 * Copyright (C) 2013 Starview Inc
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

public class WffEngine
{
  public static final boolean traceValidation = System.getProperty("TRACE_VALIDATION", "false")
      .equalsIgnoreCase("true");

  Map<String, Set<IAbstract>> assertions = new HashMap<String, Set<IAbstract>>();
  private final ErrorReport errors;
  private final Stack<WffProgram> wffProgramStack = new Stack<WffProgram>();

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
            rules = new Bag<WffRule>(rules);
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
      line = new HashSet<IAbstract>();
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
