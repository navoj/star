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
