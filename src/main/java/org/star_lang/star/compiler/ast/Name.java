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
public class Name extends ASyntax
{
  public static final String name = "nameAst";

  private static final int locIndex = 0;
  private static final int nameIndex = 1;
  private static final IType conType = TypeUtils.tupleConstructorType(Location.type, TypeUtils
      .typeExp(StandardTypes.STRING), ASyntax.type);

  private final String id;

  public Name(Location loc, String id)
  {
    super(loc);
    assert id != null;
    this.id = id;
  }

  public Name(IValue loc, IValue id) throws EvaluationException
  {
    super((Location) loc);
    this.id = Factory.stringValue(id);
  }

  @Override
  public astType astType()
  {
    return astType.Name;
  }

  public static ConstructorSpecifier spec()
  {
    return new ConstructorSpecifier(Location.nullLoc, null, name, nameIx, conType, Name.class, ASyntax.class);
  }

  public String getId()
  {
    return id;
  }

  @Override
  public int conIx()
  {
    return nameIx;
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
    case nameIndex:
      return Factory.newString(getId());
    default:
      throw new IllegalArgumentException("index out of range");
    }
  }

  public IValue get___1()
  {
    return getCell(nameIndex);
  }

  @Override
  public IValue[] getCells()
  {
    return new IValue[] { getLoc(), Factory.newString(getId()) };
  }

  @Override
  public IConstructor shallowCopy() throws EvaluationException
  {
    return new Name(getLoc(), id);
  }

  public static IType conType()
  {
    return conType;
  }

  @Override
  public boolean isIdentifier(String name)
  {
    return this.id.equals(name);
  }

  @Override
  public boolean equals(Object obj)
  {
    return (obj == this) || (obj instanceof Name && ((Name) obj).id.equals(id));
  }

  @Override
  public int hashCode()
  {
    return id.hashCode();
  }

  @Override
  public void accept(IAbstractVisitor visitor)
  {
    visitor.visitName(this);
  }

  @Override
  public boolean isApply(String op)
  {
    return false;
  }

  @Override
  public boolean isBinaryOperator(String op)
  {
    return false;
  }

  @Override
  public boolean isTernaryOperator(String op)
  {
    return false;
  }

  @Override
  public boolean isUnaryOperator(String op)
  {
    return false;
  }
}
