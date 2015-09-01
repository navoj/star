package org.star_lang.star.compiler.ast;

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
public class LongLiteral extends Literal
{
  public static final String name = "longAst";
  private static final int locIndex = 0;
  private static final int longIndex = 1;
  private static final IType conType = TypeUtils.tupleConstructorType(Location.type, TypeUtils
      .typeExp(StandardTypes.LONG), ASyntax.type);

  private final long lx;

  public LongLiteral(Location loc, long l)
  {
    super(loc);
    this.lx = l;
  }

  public LongLiteral(IValue loc, IValue val) throws EvaluationException
  {
    super((Location) loc);
    this.lx = Factory.lngValue(val);
  }

  @Override
  public astType astType()
  {
    return astType.Long;
  }

  public static ConstructorSpecifier spec()
  {
    return new ConstructorSpecifier(Location.nullLoc, null, name, longIx, conType, LongLiteral.class, ASyntax.class);
  }

  @Override
  public void accept(IAbstractVisitor visitor)
  {
    visitor.visitLongLiteral(this);
  }

  @Override
  public Long getLit()
  {
    return lx;
  }

  @Override
  public int conIx()
  {
    return longIx;
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
    case longIndex:
      return Factory.newLng(lx);
    default:
      throw new IllegalArgumentException("index out of range");
    }
  }

  public IValue get___1()
  {
    return getCell(longIndex);
  }

  @Override
  public IValue[] getCells()
  {
    return new IValue[] { getLoc(), Factory.newLng(lx) };
  }

  @Override
  public IConstructor shallowCopy() throws EvaluationException
  {
    return new LongLiteral(getLoc(), lx);
  }

  public static IType conType()
  {
    return conType;
  }

  @Override
  public boolean equals(Object obj)
  {
    return obj instanceof LongLiteral && ((LongLiteral) obj).lx == lx;
  }

  @Override
  public int hashCode()
  {
    return (int) (lx >> 32) + ((int) lx);
  }
}
