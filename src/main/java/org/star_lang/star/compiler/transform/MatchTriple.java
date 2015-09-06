package org.star_lang.star.compiler.transform;

import org.star_lang.star.compiler.canonical.ICondition;
import org.star_lang.star.compiler.canonical.IContentPattern;
import org.star_lang.star.compiler.util.ConsList;
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
      this.args = new ConsList<>(args[ix - 1], this.args);
    this.cond = cond;
    this.body = body;
  }

  MatchTriple(IContentPattern ptn, ICondition cond, T body)
  {
    this.args = ConsList.nil();
    this.args = new ConsList<>(ptn, this.args);
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