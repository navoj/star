package org.star_lang.star.compiler.operator;

import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;

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
@SuppressWarnings("serial")
public abstract class Operator implements PrettyPrintable
{
  private final int priority;
  private final int minPriority;
  private final String operator;
  private final OperatorForm form;

  public Operator(String operator, int priority, OperatorForm form)
  {
    this(operator, form, priority, 0);
  }

  public Operator(String operator, OperatorForm form, int priority, int minPriority)
  {
    assert form != null;
    this.priority = priority;
    this.minPriority = minPriority;
    this.form = form;
    this.operator = operator;
  }

  public String getOperator()
  {
    return operator;
  }

  public OperatorForm getForm()
  {
    return form;
  }

  public int getPriority()
  {
    return priority;
  }

  public int getMinPriority()
  {
    return minPriority;
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

  @Override
  public int hashCode()
  {
    return ((operator.hashCode() * 37 + priority) * 37 + minPriority) * 37 + form.hashCode();
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof Operator) {
      Operator other = (Operator) obj;

      return operator.equals(other.operator) && other.priority == priority && other.minPriority == minPriority
          && form == other.form;
    }
    return false;
  }

  public abstract boolean dominatesRight(Operator op);

  public abstract boolean dominatesLeft(Operator op);

  public abstract String toLatex();
}
