package org.star_lang.star.compiler.operator;

import org.star_lang.star.compiler.standard.StandardNames;
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
public class BracketPair implements PrettyPrintable
{
  public final String leftBracket, rightBracket, operator;
  public final int innerPriority;

  public BracketPair(int innerPriority, String leftBracket, String rightBracket, String operator)
  {
    this.innerPriority = innerPriority;
    this.leftBracket = leftBracket;
    this.rightBracket = rightBracket;
    this.operator = operator;
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append(StandardNames.META_HASH);
    disp.appendWord(StandardNames.BRACKETS);
    disp.append("(");
    disp.appendQuoted(leftBracket);
    disp.append(",");
    disp.appendQuoted(rightBracket);
    disp.append(",");
    disp.append(innerPriority);
    disp.append(")");
  }

}
