package org.star_lang.star.compiler.generate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.cafe.CafeSyntax;
import org.star_lang.star.data.*;
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
public class AbstractValue implements IValueVisitor
{
  private final Stack<IAbstract> stack;
  private final Location loc;

  public static IAbstract abstractValue(Location loc, IValue term, ErrorReport errors)
  {
    Stack<IAbstract> stack = new Stack<>();
    AbstractValue visitor = new AbstractValue(loc, stack);
    term.accept(visitor);
    assert stack.size() == 1;
    return stack.pop();
  }

  private static IAbstract abstractValue(Location loc, Object val, IType type)
  {
    if (type.equals(StandardTypes.charType))
      return Abstract.newChar(loc, (Integer) val);
    else if (val instanceof Integer)
      return Abstract.newInteger(loc, ((Integer) val));
    else if (val instanceof Double)
      return Abstract.newFloat(loc, ((Double) val));
    else if (val instanceof Character)
      return Abstract.newChar(loc, ((Character) val));
    else if (val instanceof String)
      return Abstract.newString(loc, ((String) val));
    else if (val instanceof Long)
      return Abstract.newLong(loc, (Long) val);
    else if (val instanceof Boolean)
      return Abstract.newBoolean(loc, (Boolean) val);
    else if (val instanceof BigDecimal)
      return Abstract.newBigdecimal(loc, (BigDecimal) val);
    else if (val instanceof IScalar<?>)
      return abstractValue(loc, ((IScalar<?>) val).getValue(), type);
    else
      throw new UnsupportedOperationException("not implemented");
  }

  private AbstractValue(Location loc, Stack<IAbstract> stack)
  {
    this.stack = stack;
    this.loc = loc;
  }

  @Override
  public void visitScalar(IScalar<?> scalar)
  {
    Object val = scalar.getValue();
    stack.push(abstractValue(loc, val, scalar.getType()));
  }

  @Override
  public void visitRecord(IRecord agg)
  {
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public void visitList(IList list)
  {
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public void visitFunction(IFunction fn)
  {
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public void visitPattern(IPattern ptn)
  {
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public void visitConstructor(IConstructor con)
  {
    List<IAbstract> args = new ArrayList<>();
    for (int ix = 0; ix < con.size(); ix++) {
      con.getCell(ix).accept(this);
      args.add(stack.pop());
    }

    stack.push(CafeSyntax.constructor(loc, con.getLabel(), args));
  }

  @Override
  public void visitMap(IMap map)
  {
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public void visitSet(ISet set) {
    throw new UnsupportedOperationException("not implemented");
  }
}
