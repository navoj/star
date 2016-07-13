package org.star_lang.star.compiler.ast;

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

public class DefaultAbstractVisitor implements IAbstractVisitor {

  @Override
  public void visitApply(Apply app) {
    app.getOperator().accept(this);
    for (IValue el : app.getArgs())
      ((IAbstract) el).accept(this);
  }

  @Override
  public void visitBooleanLiteral(BooleanLiteral lit) {
  }

  @Override
  public void visitFloatLiteral(FloatLiteral flt) {
  }

  @Override
  public void visitStringLiteral(StringLiteral str) {
  }

  @Override
  public void visitIntegerLiteral(IntegerLiteral lit) {
  }

  @Override
  public void visitLongLiteral(LongLiteral lit) {
  }

  @Override
  public void visitName(Name name) {
  }

}
