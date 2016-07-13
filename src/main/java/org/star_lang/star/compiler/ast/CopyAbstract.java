package org.star_lang.star.compiler.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.star_lang.star.data.IValue;
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

public class CopyAbstract implements IAbstractVisitor {
  private final Stack<IAbstract> stack = new Stack<>();

  public IAbstract copy(IAbstract term) {
    term.accept(this);
    return stack.pop();
  }

  protected Location getLocation(IAbstract term) {
    return term.getLoc();
  }

  @Override
  public void visitApply(Apply app) {
    IAbstract nOp = copy(app.getOperator());
    List<IAbstract> nArgs = new ArrayList<>();
    for (IValue arg : app.getArgs()) {
      nArgs.add(copy((IAbstract) arg));
    }
    stack.push(new Apply(getLocation(app), nOp, nArgs, app.getCategories(), app.getAttributes()));
  }

  @Override
  public void visitBooleanLiteral(BooleanLiteral lit) {
    stack.push(new BooleanLiteral(getLocation(lit), lit.getLit()));
  }

  @Override
  public void visitFloatLiteral(FloatLiteral flt) {
    stack.push(new FloatLiteral(getLocation(flt), flt.getLit()));
  }

  @Override
  public void visitStringLiteral(StringLiteral str) {
    stack.push(new StringLiteral(getLocation(str), str.getLit()));
  }

  @Override
  public void visitIntegerLiteral(IntegerLiteral lit) {
    stack.push(new IntegerLiteral(getLocation(lit), lit.getLit()));
  }

  @Override
  public void visitLongLiteral(LongLiteral lit) {
    stack.push(new LongLiteral(getLocation(lit), lit.getLit()));
  }

  @Override
  public void visitName(Name name) {
    stack.push(new Name(getLocation(name), name.getId()));
  }
}
