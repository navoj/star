package org.star_lang.star.data.type;

import org.star_lang.star.compiler.type.Dictionary;
import org.star_lang.star.compiler.type.DisplayType;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.StringUtils;

/**
 * The TypeExists class is used to represent cases where there is a type but it is opaquely defined.
 *
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
public class TypeExists implements ITypeDescription
{
  private final Location loc;
  private final String name;
  private final IType type;

  public TypeExists(Location loc, String name, Kind kind)
  {
    this.loc = loc;
    this.name = name;
    this.type = new TypeVar();
    ((TypeVar) type).setConstraint(new HasKind((TypeVar) type, kind));
  }

  public TypeExists(Location loc, String name, IType type)
  {
    this.loc = loc;
    this.name = name;
    this.type = type;
    assert type != null && name != null;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append(name);
    disp.appendWord("=");
    DisplayType.display(disp, type);
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public Location getLoc()
  {
    return loc;
  }

  @Override
  public int typeArity()
  {
    return type.kind().arity();
  }

  @Override
  public Kind kind()
  {
    return type.kind();
  }

  @Override
  public IType getType()
  {
    return type;
  }

  @Override
  public IType verifyType(IType type, Location loc, Dictionary dict) throws TypeConstraintException
  {
    Kind kind = this.type.kind();

    if (kind != Kind.unknown) {
      if (kind == Kind.type) {
        if (!(type instanceof TypeVar || type instanceof Type))
          throw new TypeConstraintException(StringUtils.msg(type, "not consistent with ", this.type), loc);
      } else if (type instanceof TypeExp) {
        if (((TypeExp) type).typeArity() != kind.arity())
          throw new TypeConstraintException(StringUtils.msg(type, "not consistent with ", this.type), loc);
      } else
        throw new TypeConstraintException(StringUtils.msg(type, "not consistent with ", this.type), loc);
    }
    return type;
  }
}
