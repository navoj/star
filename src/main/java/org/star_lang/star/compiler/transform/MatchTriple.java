package org.star_lang.star.compiler.transform;

import org.star_lang.star.compiler.canonical.ICondition;
import org.star_lang.star.compiler.canonical.IContentPattern;
import org.star_lang.star.compiler.util.ConsList;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;

/*
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
 */
@SuppressWarnings("serial")
public class MatchTriple<T extends PrettyPrintable> implements PrettyPrintable
{
  ConsList<IContentPattern> args;
  ICondition cond;
  T body;

  MatchTriple(ConsList<IContentPattern> args, ICondition cond, T body)
  {
    this.args = args;
    this.cond = cond;
    this.body = body;
  }

  MatchTriple(IContentPattern args[], ICondition cond, T body)
  {
    this.args = ConsList.nil();
    for (int ix = args.length; ix > 0; ix--)
      this.args = new ConsList<IContentPattern>(args[ix - 1], this.args);
    this.cond = cond;
    this.body = body;
  }

  MatchTriple(IContentPattern ptn, ICondition cond, T body)
  {
    this.args = ConsList.nil();
    this.args = new ConsList<IContentPattern>(ptn, this.args);
    this.cond = cond;
    this.body = body;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append("(");
    args.prettyPrint(disp);
    disp.append(", ");
    if (cond != null)
      cond.prettyPrint(disp);
    else
      disp.append("(no cond)");
    disp.append(", ");
    body.prettyPrint(disp);
    disp.append(")");
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }
}