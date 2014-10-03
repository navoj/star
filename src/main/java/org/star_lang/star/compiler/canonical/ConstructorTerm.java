package org.star_lang.star.compiler.canonical;

import java.util.List;

import org.star_lang.star.compiler.operator.Operators;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.FixedList;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;

/**
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA
 * 
 * @author fgm
 *
 */
@SuppressWarnings("serial")
public class ConstructorTerm extends BaseExpression
{
  /**
   * The Tuple content expression constructs a labeled or unlabeled positional term
   */

  private final List<IContentExpression> elements;
  private final String label;

  public ConstructorTerm(Location loc, String label, IType type, List<IContentExpression> arguments)
  {
    super(loc, type);
    this.elements = arguments;
    this.label = label;
  }

  public ConstructorTerm(Location loc, String label, IType type, IContentExpression... args)
  {
    this(loc, label, type, FixedList.create(args));
  }

  public ConstructorTerm(Location loc, IType type, IContentExpression... args)
  {
    this(loc, TypeUtils.tupleLabel(args.length), type, FixedList.create(args));
  }

  public ConstructorTerm(Location loc, IContentExpression... args)
  {
    this(loc, TypeUtils.tupleLabel(args.length), makeType(args), FixedList.create(args));
  }

  public ConstructorTerm(Location loc, List<IContentExpression> args)
  {
    this(loc, TypeUtils.tupleLabel(args.size()), makeType(args), args);
  }

  public static IContentExpression tuple(Location loc, IContentExpression... args)
  {
    return new ConstructorTerm(loc, args);
  }

  public String getLabel()
  {
    return label;
  }

  public List<IContentExpression> getElements()
  {
    return elements;
  }

  public int arity()
  {
    return elements.size();
  }

  public IContentExpression getArg(int ix)
  {
    return elements.get(ix);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    if (label == null || label.isEmpty() || TypeUtils.isTupleLabel(label)) {
      disp.append("(");
      disp.prettyPrint(elements, ", ");
      disp.append(")");
    } else if (arity() == 2 && Operators.isRootInfixOperator(label) != null) {
      elements.get(0).prettyPrint(disp);
      disp.appendId(label);
      elements.get(1).prettyPrint(disp);
    } else if (arity() == 1 && Operators.isRootPrefixOperator(label) != null) {
      disp.appendId(label);
      elements.get(0).prettyPrint(disp);
    } else if (arity() == 1 && Operators.isRootPostfixOperator(label) != null) {
      elements.get(0).prettyPrint(disp);
      disp.appendId(label);
    } else {
      disp.appendId(label);
      if (arity() > 0) {
        disp.append("(");
        disp.prettyPrint(elements, ", ");
        disp.append(")");
      }
    }
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitTuple(this);
  }

  @Override
  public <A, E, P, C, D, T> E transform(TransformExpression<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformConstructor(this, context);
  }

  private static IType makeType(IContentExpression args[])
  {
    IType aTypes[] = new IType[args.length];
    for (int ix = 0; ix < args.length; ix++)
      aTypes[ix] = args[ix].getType();
    return TypeUtils.tupleType(aTypes);
  }

  private static IType makeType(List<IContentExpression> args)
  {
    IType aTypes[] = new IType[args.size()];
    for (int ix = 0; ix < args.size(); ix++)
      aTypes[ix] = args.get(ix).getType();
    return TypeUtils.tupleType(aTypes);
  }
}
