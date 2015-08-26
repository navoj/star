package org.star_lang.star.compiler.canonical;

import java.util.ArrayList;
import java.util.List;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.Location;

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
public class LetTerm extends BaseExpression
{
  final private List<IStatement> environment;
  final private IContentExpression bound;

  public LetTerm(Location loc, IContentExpression bound, IStatement... environment)
  {
    super(loc, bound.getType());
    this.bound = bound;
    this.environment = new ArrayList<>();
    for (IStatement stmt : environment)
      this.environment.add(stmt);
  }

  public LetTerm(Location loc, IContentExpression bound, List<IStatement> environment)
  {
    super(loc, bound.getType());
    this.bound = bound;
    this.environment = environment;
  }

  public List<IStatement> getEnvironment()
  {
    return environment;
  }

  public IContentExpression getBoundExp()
  {
    return bound;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    int outer = disp.markIndent();
    int mark = disp.markIndent(2);
    disp.appendWord(StandardNames.LET);
    disp.append("{");

    for (IStatement entry : environment) {
      disp.append("\n");
      entry.prettyPrint(disp);
    }

    disp.popIndent(mark);
    disp.append("\n");
    disp.appendWord("}");
    disp.appendWord(StandardNames.IN);
    bound.prettyPrint(disp);
    disp.popIndent(outer);
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitLetTerm(this);
  }

  @Override
  public <A, E, P, C, D, T> E transform(TransformExpression<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformLetTerm(this, context);
  }
}
