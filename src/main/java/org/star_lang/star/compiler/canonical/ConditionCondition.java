package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.Location;

/*
 * 
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
public class ConditionCondition extends Condition
{
  private final ICondition test, lhs, rhs;

  public ConditionCondition(Location loc, ICondition test, ICondition lhs, ICondition rhs)
  {
    super(loc);
    this.test = test;
    this.lhs = lhs;
    this.rhs = rhs;
  }

  public ICondition getTest()
  {
    return test;
  }

  public ICondition getLhs()
  {
    return lhs;
  }

  public ICondition getRhs()
  {
    return rhs;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append("(");
    test.prettyPrint(disp);
    disp.append("?");
    lhs.prettyPrint(disp);
    disp.appendWord("|");
    rhs.prettyPrint(disp);
    disp.append(")");
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitConditionCondition(this);
  }

  @Override
  public <A, E, P, C, D, T> C transform(TransformCondition<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformConditionCondition(this, context);
  }
}
