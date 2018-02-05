package org.star_lang.star.compiler.canonical;

/*
 * Copyright (c) 2016. Francis G. McCabe
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

import org.star_lang.star.compiler.operator.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.Location;

/**
 * Created by fgm on 4/14/16.
 */
@SuppressWarnings("serial")
public class ValuePtn extends ContentPattern {
  private final IContentExpression value;

  public ValuePtn(Location loc, IContentExpression exp) {
    super(loc, exp.getType());
    this.value = exp;
  }

  public IContentExpression getValue() {
    return value;
  }

  @Override
  public <A, E, P, C, D, T> P transformPattern(TransformPattern<A, E, P, C, D, T> transform, T context) {
    return transform.transformValuePtn(this, context);
  }

  @Override
  public void accept(CanonicalVisitor visitor) {
    visitor.visitValuePtn(this);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp) {
    disp.appendWord(StandardNames.PERIOD);
    value.prettyPrint(disp);
  }
}
