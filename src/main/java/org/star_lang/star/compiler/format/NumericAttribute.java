package org.star_lang.star.compiler.format;

import org.star_lang.star.compiler.ast.BaseAttribute;
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
public class NumericAttribute extends BaseAttribute<Integer>
{
  private final int count;
  private final Absolute absolute;

  public NumericAttribute(Absolute absolute, int count, int specificity)
  {
    super(true, specificity);
    this.count = count;
    this.absolute = absolute;
  }

  public Absolute isAbsolute()
  {
    return absolute;
  }

  public int count()
  {
    return count;
  }

  @Override
  public Integer attribute(Integer original)
  {
    switch (absolute) {
    case absolute:
    default:
      return count;
    case increasing:
      return count + original;
    case decreasing:
      return original - count;
    }
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    switch (absolute) {
    case decreasing:
      disp.append("-");
      break;
    case increasing:
      disp.append("+");
      break;
    case absolute:
      break;
    case mark:
      disp.append("=");
      break;
    }
    disp.append(count);
  }
}
