package org.star_lang.star.compiler.canonical;

import java.util.ArrayList;
import java.util.List;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;

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
public class TuplePtn extends ContentPattern {
  /**
   * The Tuple pattern matches a tuple of patterns
   */
  private final List<IContentPattern> elements;

  public TuplePtn(Location loc, IType type, List<IContentPattern> arguments) {
    super(loc, type);
    this.elements = arguments;
  }

  public TuplePtn(Location loc, List<IContentPattern> args) {
    this(loc, makeType(args), args);
  }

  public TuplePtn(Location loc, IType type, IContentPattern... args) {
    this(loc, type, makeArgs(args));
  }

  public TuplePtn(Location loc, IContentPattern... args) {
    this(loc, makeType(args), args);
  }

  public static IContentPattern tuplePtn(Location loc, IContentPattern... args) {
    return new TuplePtn(loc, args);
  }

  public List<IContentPattern> getElements() {
    return elements;
  }

  public IContentPattern getArg(int ix) {
    return elements.get(ix);
  }

  public int arity() {
    return elements.size();
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp) {
    disp.append("(");
    disp.prettyPrint(elements, ", ");
    disp.append(")");
  }

  @Override
  public void accept(CanonicalVisitor visitor) {
    visitor.visitTuplePtn(this);
  }

  @Override
  public <A, E, P, C, D, T> P transformPattern(TransformPattern<A, E, P, C, D, T> transform, T context) {
    return transform.transformTuplePtn(this, context);
  }

  private static List<IContentPattern> makeArgs(IContentPattern args[]) {
    List<IContentPattern> list = new ArrayList<>();
    for (IContentPattern arg : args)
      list.add(arg);
    return list;
  }

  private static IType makeType(IContentPattern args[]) {
    IType aTypes[] = new IType[args.length];
    for (int ix = 0; ix < args.length; ix++)
      aTypes[ix] = args[ix].getType();
    return TypeUtils.tupleType(aTypes);
  }

  private static IType makeType(List<IContentPattern> args) {
    IType aTypes[] = new IType[args.size()];
    for (int ix = 0; ix < args.size(); ix++)
      aTypes[ix] = args.get(ix).getType();
    return TypeUtils.tupleType(aTypes);
  }
}
