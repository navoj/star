package org.star_lang.star.compiler.wff;

import org.star_lang.star.compiler.ast.*;
import org.star_lang.star.data.IValue;

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

public class Complexity implements IAbstractVisitor {
  private int count = 0;

  private Complexity() {
  }

  public static int complexity(IAbstract term) {
    Complexity comp = new Complexity();
    term.accept(comp);
    return comp.count;
  }

  @Override
  public void visitApply(Apply app) {
    count++;
    for (IValue arg : app.getArgs())
      ((IAbstract) arg).accept(this);
  }

  @Override
  public void visitBooleanLiteral(BooleanLiteral lit) {
    count++;
  }

  @Override
  public void visitFloatLiteral(FloatLiteral flt) {
    count++;
  }

  @Override
  public void visitIntegerLiteral(IntegerLiteral lit) {
    count++;
  }

  @Override
  public void visitLongLiteral(LongLiteral lit) {
    count++;
  }

  @Override
  public void visitBigDecimal(BigDecimalLiteral lit) {
    count++;
  }

  @Override
  public void visitName(Name name) {
    count++;
  }

  @Override
  public void visitStringLiteral(StringLiteral str) {
    count++;
  }

}
