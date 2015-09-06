package org.star_lang.star.compiler.operator;

import org.star_lang.star.compiler.ast.IAttribute;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;

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
public class OpFormAttribute implements IAttribute
{
  public static final String name = "OperatorFormAttribute";

  private final int priority;
  private final OperatorForm form;

  public OpFormAttribute(int priority, OperatorForm form)
  {
    this.priority = priority;
    this.form = form;
  }

  @Override
  public boolean isIheritable()
  {
    return false;
  }

  public int getPriority()
  {
    return priority;
  }

  public OperatorForm getForm()
  {
    return form;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append(priority);
    disp.append(":");
    disp.append(form.toString());

  }
}
