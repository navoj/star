package org.star_lang.star.compiler.wff;

import org.star_lang.star.compiler.ast.Apply;
import org.star_lang.star.compiler.ast.BigDecimalLiteral;
import org.star_lang.star.compiler.ast.BooleanLiteral;
import org.star_lang.star.compiler.ast.CharLiteral;
import org.star_lang.star.compiler.ast.FloatLiteral;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.ast.IAbstractVisitor;
import org.star_lang.star.compiler.ast.IntegerLiteral;
import org.star_lang.star.compiler.ast.LongLiteral;
import org.star_lang.star.compiler.ast.Name;
import org.star_lang.star.compiler.ast.StringLiteral;
import org.star_lang.star.data.IValue;

/**
 * 
 * Copyright (C) 2013 Starview Inc
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
public class Complexity implements IAbstractVisitor
{
  private int count = 0;

  private Complexity()
  {
  }

  public static int complexity(IAbstract term)
  {
    Complexity comp = new Complexity();
    term.accept(comp);
    return comp.count;
  }

  @Override
  public void visitApply(Apply app)
  {
    count++;
    for (IValue arg : app.getArgs())
      ((IAbstract) arg).accept(this);
  }

  @Override
  public void visitBooleanLiteral(BooleanLiteral lit)
  {
    count++;
  }

  @Override
  public void visitCharLiteral(CharLiteral lit)
  {
    count++;
  }

  @Override
  public void visitFloatLiteral(FloatLiteral flt)
  {
    count++;
  }

  @Override
  public void visitIntegerLiteral(IntegerLiteral lit)
  {
    count++;
  }

  @Override
  public void visitLongLiteral(LongLiteral lit)
  {
    count++;
  }

  @Override
  public void visitBigDecimal(BigDecimalLiteral lit)
  {
    count++;
  }

  @Override
  public void visitName(Name name)
  {
    count++;
  }

  @Override
  public void visitStringLiteral(StringLiteral str)
  {
    count++;
  }

}
