package org.star_lang.star.data.type;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;

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
public abstract class Quantifier implements PrettyPrintable
{
  private final TypeVar var;

  Quantifier(TypeVar var)
  {
    this.var = var;
  }

  public TypeVar getVar()
  {
    return var;
  }

  abstract public IType wrap(IType type);

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

  public static class Universal extends Quantifier
  {
    public Universal(TypeVar v)
    {
      super(v);
    }

    @Override
    public void prettyPrint(PrettyPrintDisplay disp)
    {
      disp.append(StandardNames.FOR_ALL);
      disp.appendId(getVar().getVarName());
    }

    @Override
    public IType wrap(IType type)
    {
      if (TypeUtils.isTypeVar(getVar()))
        return new UniversalType(getVar(), type);
      else
        return type;
    }
  }

  public static class Existential extends Quantifier
  {
    public Existential(TypeVar v)
    {
      super(v);
    }

    @Override
    public void prettyPrint(PrettyPrintDisplay disp)
    {
      disp.append(StandardNames.EXISTS);
      disp.appendId(getVar().getVarName());
    }

    @Override
    public IType wrap(IType type)
    {
      if (TypeUtils.isTypeVar(getVar()))
        return new ExistentialType(getVar(), type);
      else
        return type;
    }
  }
}
