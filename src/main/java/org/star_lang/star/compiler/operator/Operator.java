package org.star_lang.star.compiler.operator;

import org.star_lang.star.compiler.standard.StandardNames;
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
public final class Operator implements PrettyPrintable
{
  private final int left, priority, right;
  private final int minPriority;
  private final String operator;
  private final OperatorForm form;

  public Operator(String operator, int left, int priority, int right, OperatorForm form)
  {
    this(operator, form, left, priority, right, 0);
  }

  public Operator(String operator, OperatorForm form, int left, int priority, int right, int minPriority)
  {
    assert form != null;
    this.left = left;
    this.right = right;
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

  public int leftPriority()
  {
    return left;
  }

  public int rightPriority()
  {
    return right;
  }

  public int getMinPriority()
  {
    return minPriority;
  }

  public boolean isLeftAssoc()
  {
    return left == priority;
  }

  public boolean isRightAssoc()
  {
    return right == priority;
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

  public String toLatex()
  {
    StringBuilder b = new StringBuilder();
    b.append("\\tt ").append(StandardNames.latexString(getOperator())).append("&").append(getPriority());
    switch (form) {
    case prefix:
      if (right < priority)
        b.append("&prefix");
      else
        b.append("&assoc prefix");
      break;
    case infix:
      if (left < priority) {
        if (right < priority)
          b.append("&infix");
        else
          b.append("&right");
      } else
        b.append("&left");
      break;
    case postfix:
      if (left < priority)
        b.append("&postfix");
      else
        b.append("&assoc postfix");
      break;
    default:
      return "*not an operator*";
    }
    return b.toString();
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append(StandardNames.META_HASH);
    switch (form) {
    case prefix:
      if (right < priority)
        disp.append(StandardNames.PREFIX);
      else
        disp.append(StandardNames.PREFIXA);
      break;
    case infix:
      if (left < priority) {
        if (right < priority)
          disp.append(StandardNames.INFIX);
        else
          disp.append(StandardNames.RIGHT);
      } else
        disp.append(StandardNames.LEFT);
      break;
    case postfix:
      if (right < priority)
        disp.append(StandardNames.POSTFIX);
      else
        disp.append(StandardNames.POSTFIXA);
      break;
      default:
        disp.append("not operator");
    }

    disp.append("((");
    disp.append(operator);
    disp.append("),");
    disp.append(priority);
    if (minPriority > 0) {
      disp.append(",");
      disp.append(minPriority);
    }
    disp.append(")");
  }
}
