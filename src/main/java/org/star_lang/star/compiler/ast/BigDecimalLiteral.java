package org.star_lang.star.compiler.ast;

import java.math.BigDecimal;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IConstructor;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.ConstructorSpecifier;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.value.Factory;

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
@SuppressWarnings("serial")
public class BigDecimalLiteral extends Literal
{
  public static final String name = "decimalAst";
  private static final int locIndex = 0;
  private static final int decimalIndex = 1;
  private static final IType conType = TypeUtils.tupleConstructorType(Location.type, TypeUtils
      .typeExp(StandardTypes.DECIMAL), ASyntax.type);

  private final BigDecimal big;

  public BigDecimalLiteral(Location loc, BigDecimal big)
  {
    super(loc);
    this.big = big;
  }

  public BigDecimalLiteral(Location loc, IValue big) throws EvaluationException
  {
    super(loc);
    this.big = Factory.decimalValue(big);
  }

  @Override
  public astType astType()
  {
    return astType.Dec;
  }

  public static ConstructorSpecifier spec()
  {
    return new ConstructorSpecifier(Location.nullLoc, null, name, decimalIx, conType, BigDecimalLiteral.class,
        ASyntax.class);
  }

  @Override
  public void accept(IAbstractVisitor visitor)
  {
    visitor.visitBigDecimal(this);
  }

  @Override
  public BigDecimal getLit()
  {
    return big;
  }

  @Override
  public int conIx()
  {
    return decimalIx;
  }

  @Override
  public String getLabel()
  {
    return name;
  }

  @Override
  public int size()
  {
    return 2;
  }

  @Override
  public IValue getCell(int index)
  {
    switch (index) {
    case locIndex:
      return getLoc();
    case decimalIndex:
      return Factory.newDecimal(big);
    default:
      throw new IllegalArgumentException("index out of range");
    }
  }

  public IValue get___1()
  {
    return getCell(decimalIndex);
  }

  @Override
  public IValue[] getCells()
  {
    return new IValue[] { getLoc(), Factory.newDecimal(big) };
  }

  @Override
  public IConstructor shallowCopy() throws EvaluationException
  {
    return new BigDecimalLiteral(getLoc(), big);
  }

  public static IType conType()
  {
    return conType;
  }

  @Override
  public boolean equals(Object obj)
  {
    return obj instanceof BigDecimalLiteral && ((BigDecimalLiteral) obj).getLit().equals(getLit());
  }

  @Override
  public int hashCode()
  {
    return getLit().hashCode();
  }
}
