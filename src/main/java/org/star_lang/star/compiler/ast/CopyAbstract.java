package org.star_lang.star.compiler.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.Location;

/**
 * Copy an astTerm
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
public class CopyAbstract implements IAbstractVisitor
{
  private final Stack<IAbstract> stack = new Stack<>();

  public IAbstract copy(IAbstract term)
  {
    term.accept(this);
    return stack.pop();
  }

  protected Location getLocation(IAbstract term)
  {
    return term.getLoc();
  }

  @Override
  public void visitApply(Apply app)
  {
    IAbstract nOp = copy(app.getOperator());
    List<IAbstract> nArgs = new ArrayList<>();
    for (IValue arg : app.getArgs()) {
      nArgs.add(copy((IAbstract) arg));
    }
    stack.push(new Apply(getLocation(app), nOp, nArgs, app.getCategories(), app.getAttributes()));
  }

  @Override
  public void visitBooleanLiteral(BooleanLiteral lit)
  {
    stack.push(new BooleanLiteral(getLocation(lit), lit.getLit()));
  }

  @Override
  public void visitCharLiteral(CharLiteral lit)
  {
    stack.push(new CharLiteral(getLocation(lit), lit.getLit()));
  }

  @Override
  public void visitFloatLiteral(FloatLiteral flt)
  {
    stack.push(new FloatLiteral(getLocation(flt), flt.getLit()));
  }

  @Override
  public void visitStringLiteral(StringLiteral str)
  {
    stack.push(new StringLiteral(getLocation(str), str.getLit()));
  }

  @Override
  public void visitIntegerLiteral(IntegerLiteral lit)
  {
    stack.push(new IntegerLiteral(getLocation(lit), lit.getLit()));
  }

  @Override
  public void visitLongLiteral(LongLiteral lit)
  {
    stack.push(new LongLiteral(getLocation(lit), lit.getLit()));
  }

  @Override
  public void visitBigDecimal(BigDecimalLiteral lit)
  {
    stack.push(new BigDecimalLiteral(getLocation(lit), lit.getLit()));
  }

  @Override
  public void visitName(Name name)
  {
    stack.push(new Name(getLocation(name), name.getId()));
  }
}
