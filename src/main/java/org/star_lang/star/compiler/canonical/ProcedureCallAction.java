package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;


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
public class ProcedureCallAction extends Action {
  private final IContentExpression proc;
  private final IContentExpression arg;

  public ProcedureCallAction(Location loc, IContentExpression proc, IContentExpression arg) {
    super(loc, StandardTypes.unitType);
    this.proc = proc;
    this.arg = arg;
  }

  public ProcedureCallAction(Location loc, IContentExpression proc, IContentExpression... args) {
    this(loc, proc, new TupleTerm(loc, args));
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp) {
    proc.prettyPrint(disp);
    arg.prettyPrint(disp);
  }

  public IContentExpression getProc() {
    return proc;
  }

  public IContentExpression getArgs() {
    return arg;
  }

  @Override
  public void accept(CanonicalVisitor visitor) {
    visitor.visitProcedureCallAction(this);
  }

  @Override
  public void accept(ActionVisitor visitor) {
    visitor.visitProcedureCallAction(this);
  }

  @Override
  public <A, E, P, C, D, T> A transform(TransformAction<A, E, P, C, D, T> transform, T context) {
    return transform.transformProcedureCallAction(this, context);
  }
}
