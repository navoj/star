package org.star_lang.star.compiler.canonical;

import java.util.ArrayList;
import java.util.List;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;

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
public class ConstructorPtn extends ContentPattern
{
  /**
   * The Tuple content expression matches a labeled or unlabeled positional term
   */
  private final String label;
  private final List<IContentPattern> elements;

  public ConstructorPtn(Location loc, String label, IType type, List<IContentPattern> arguments)
  {
    super(loc, type);
    this.label = label;
    this.elements = arguments;
  }

  public ConstructorPtn(Location loc, List<IContentPattern> args)
  {
    this(loc, TypeUtils.tupleLabel(args.size()), makeType(args), args);
  }

  public ConstructorPtn(Location loc, String label, IType type, IContentPattern... args)
  {
    this(loc, label, type, makeArgs(args));
  }

  public ConstructorPtn(Location loc, IType type, IContentPattern... args)
  {
    this(loc, TypeUtils.tupleLabel(args.length), type, makeArgs(args));
  }

  public ConstructorPtn(Location loc, IContentPattern... args)
  {
    this(loc, TypeUtils.tupleLabel(args.length), makeType(args), args);
  }

  public static IContentPattern tuplePtn(Location loc, IContentPattern... args)
  {
    return new ConstructorPtn(loc, args);
  }

  public String getLabel()
  {
    return label;
  }

  public IContentExpression getFun()
  {
    return new Variable(getLoc(), StandardTypes.voidType, label);
  }

  public List<IContentPattern> getElements()
  {
    return elements;
  }

  public IContentPattern getArg(int ix)
  {
    return elements.get(ix);
  }

  public int arity()
  {
    return elements.size();
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    if (label != null && !label.isEmpty() && !TypeUtils.isTupleLabel(label))
      disp.appendId(label);
    disp.append("(");
    disp.prettyPrint(elements, ", ");
    disp.append(")");
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitTuplePtn(this);
  }

  @Override
  public <A, E, P, C, D, T> P transformPattern(TransformPattern<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformConstructorPtn(this, context);
  }

  private static List<IContentPattern> makeArgs(IContentPattern args[])
  {
    List<IContentPattern> list = new ArrayList<IContentPattern>();
    for (IContentPattern arg : args)
      list.add(arg);
    return list;
  }

  private static IType makeType(IContentPattern args[])
  {
    IType aTypes[] = new IType[args.length];
    for (int ix = 0; ix < args.length; ix++)
      aTypes[ix] = args[ix].getType();
    return TypeUtils.tupleType(aTypes);
  }

  private static IType makeType(List<IContentPattern> args)
  {
    IType aTypes[] = new IType[args.size()];
    for (int ix = 0; ix < args.size(); ix++)
      aTypes[ix] = args.get(ix).getType();
    return TypeUtils.tupleType(aTypes);
  }
}
