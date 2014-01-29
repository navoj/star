package org.star_lang.star.compiler.operator;

import org.star_lang.star.compiler.ast.IAttribute;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;

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
