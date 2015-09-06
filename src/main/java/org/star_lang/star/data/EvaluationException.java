package org.star_lang.star.data;
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


import java.util.ArrayList;
import java.util.List;

import org.star_lang.star.compiler.cafe.compile.Utils;
import org.star_lang.star.compiler.cafe.type.CafeTypeDescription;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.data.type.*;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.data.value.QuoteDisplay;
import org.star_lang.star.data.value.StringWrap;

public class EvaluationException extends Exception implements IConstructor, PrettyPrintable
{
  private static final long serialVersionUID = 1L;

  private final Location loc;
  private final IValue code;
  private final IValue raise;

  public static final String name = "exception";
  public static final IType type = TypeUtils.typeExp(name);

  public static void declare(ITypeContext cxt)
  {
    ConstructorSpecifier exSpec = new ConstructorSpecifier(Location.nullLoc, null, name, 0, conType(),
        EvaluationException.class, EvaluationException.class);

    List<IValueSpecifier> specs = new ArrayList<>();
    specs.add(exSpec);
    ITypeDescription desc = new CafeTypeDescription(Location.nullLoc, type, Utils
        .javaInternalClassName(EvaluationException.class), specs);
    cxt.defineType(desc);
  }

  public EvaluationException(IValue code, IValue datum, Location loc)
  {
    super(datum.toString());
    this.loc = loc;
    this.code = code;
    this.raise = datum;
  }

  public EvaluationException(IValue code, IValue datum, IValue loc)
  {
    this(code, datum, (Location) loc);
  }

  public EvaluationException(IValue datum)
  {
    this(StringWrap.nonStringEnum, datum, Location.nullLoc);
  }

  public EvaluationException(String msg, Location loc)
  {
    this(StringWrap.nonStringEnum, Factory.newString(msg), loc);
  }

  public EvaluationException(String msg)
  {
    this(msg, Location.nullLoc);
  }

  public EvaluationException(String msg, Throwable throwable)
  {
    super(msg, throwable);
    this.loc = null;
    this.code = StringWrap.nonStringEnum;
    this.raise = Factory.newString(msg);
  }

  public Location getLoc()
  {
    return loc;
  }

  public IValue getCode()
  {
    return code;
  }

  public IValue getDatum()
  {
    return raise;
  }

  public IValue get___0()
  {
    return code;
  }

  public IValue get___1()
  {
    return raise;
  }

  public IValue get___2()
  {
    return loc;
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

  private static final int CODE = 0;
  private static final int RAISE = 1;
  private static final int LOC = 2;

  @Override
  public void accept(IValueVisitor visitor)
  {
    visitor.visitConstructor(this);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    QuoteDisplay.display(disp, code);
    disp.append(":");
    QuoteDisplay.display(disp, raise);
    if (loc != null && loc != Location.noWhereEnum) {
      disp.append("@");
      loc.prettyPrint(disp);
    }
  }

  @Override
  public int conIx()
  {
    return 0;
  }

  @Override
  public String getLabel()
  {
    return name;
  }

  @Override
  public int size()
  {
    return 3;
  }

  @Override
  public IValue getCell(int index)
  {
    switch (index) {
    case LOC:
      return loc;
    case CODE:
      return code;
    case RAISE:
      return raise;
    default:
      throw new IndexOutOfBoundsException();
    }
  }

  @Override
  public IValue[] getCells()
  {
    IValue[] data = new IValue[size()];
    data[LOC] = loc;
    data[CODE] = code;
    data[RAISE] = raise;
    return data;
  }

  @Override
  public void setCell(int index, IValue value) throws EvaluationException
  {
    throw new UnsupportedOperationException("not permitted");
  }

  @Override
  public IConstructor copy() throws EvaluationException
  {
    return this;
  }

  @Override
  public IType getType()
  {
    return type;
  }

  public static IType conType()
  {
    return TypeUtils.tupleConstructorType(StandardTypes.stringType, StandardTypes.anyType, StandardTypes.locationType,
        type);
  }

  @Override
  public IConstructor shallowCopy() throws EvaluationException
  {
    return this;
  }

  @Override
  public int hashCode()
  {
    return (name.hashCode() * 37 + code.hashCode()) * 37 + raise.hashCode();
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
      return true;
    else if (obj instanceof EvaluationException) {
      EvaluationException ex = (EvaluationException) obj;
      return ex.code.equals(code) && ex.raise.equals(raise);
    } else
      return false;
  }

}
