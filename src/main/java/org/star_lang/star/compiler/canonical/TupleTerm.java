package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.FixedList;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;

import java.util.List;

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

@SuppressWarnings("serial")
public class TupleTerm extends BaseExpression {
  /**
   * The Tuple content expression constructs a labeled or unlabeled positional term
   */

  private final List<IContentExpression> elements;

  public TupleTerm(Location loc, IType type, List<IContentExpression> arguments) {
    super(loc, type);
    this.elements = arguments;
  }

  public TupleTerm(Location loc, IType type, IContentExpression... args) {
    this(loc, type, FixedList.create(args));
  }

  public TupleTerm(Location loc, IContentExpression... args) {
    this(loc, makeType(args), FixedList.create(args));
  }

  public TupleTerm(Location loc, List<IContentExpression> args) {
    this(loc, makeType(args), args);
  }

  public static IContentExpression tuple(Location loc, IContentExpression... args) {
    return new TupleTerm(loc, args);
  }

  public List<IContentExpression> getElements() {
    return elements;
  }

  public int arity() {
    return elements.size();
  }

  public IContentExpression getArg(int ix) {
    return elements.get(ix);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp) {
    disp.append("(");
    disp.prettyPrint(elements, ", ");
    disp.append(")");
  }

  @Override
  public void accept(CanonicalVisitor visitor) {
    visitor.visitTuple(this);
  }

  @Override
  public <A, E, P, C, D, T> E transform(TransformExpression<A, E, P, C, D, T> transform, T context) {
    return transform.transformTuple(this, context);
  }

  private static IType makeType(IContentExpression args[]) {
    IType aTypes[] = new IType[args.length];
    for (int ix = 0; ix < args.length; ix++)
      aTypes[ix] = args[ix].getType();
    return TypeUtils.tupleType(aTypes);
  }

  private static IType makeType(List<IContentExpression> args) {
    IType aTypes[] = new IType[args.size()];
    for (int ix = 0; ix < args.size(); ix++)
      aTypes[ix] = args.get(ix).getType();
    return TypeUtils.tupleType(aTypes);
  }
}
