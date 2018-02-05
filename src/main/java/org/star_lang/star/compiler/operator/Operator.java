package org.star_lang.star.compiler.operator;

import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;

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
