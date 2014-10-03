package org.star_lang.star.data.type;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;

/**
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
