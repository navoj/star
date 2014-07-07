package org.star_lang.star.compiler.ast;

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
public class DefaultAbstractVisitor implements IAbstractVisitor
{

  @Override
  public void visitApply(Apply app)
  {
    app.getOperator().accept(this);
    for (IValue el : app.getArgs())
      ((IAbstract) el).accept(this);
  }

  @Override
  public void visitBooleanLiteral(BooleanLiteral lit)
  {
  }

  @Override
  public void visitCharLiteral(CharLiteral lit)
  {
  }

  @Override
  public void visitFloatLiteral(FloatLiteral flt)
  {
  }

  @Override
  public void visitStringLiteral(StringLiteral str)
  {
  }

  @Override
  public void visitIntegerLiteral(IntegerLiteral lit)
  {
  }

  @Override
  public void visitLongLiteral(LongLiteral lit)
  {
  }

  @Override
  public void visitBigDecimal(BigDecimalLiteral lit)
  {
  }

  @Override
  public void visitName(Name name)
  {
  }

}
