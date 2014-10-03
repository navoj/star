package org.star_lang.star.data.type;

import java.util.Collection;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.type.Dictionary;
import org.star_lang.star.compiler.type.DisplayType;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.FixedList;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.compiler.util.StringUtils;

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
public class HasKind implements ITypeConstraint, PrettyPrintable
{
  private final TypeVar var;
  private final Kind kind;

  public HasKind(TypeVar var, Kind kind)
  {
    this.var = var;
    this.kind = kind;
    assert kind != Kind.unknown;
  }

  public TypeVar getVar()
  {
    return var;
  }

  public Kind getKind()
  {
    return kind;
  }

  @Override
  public void checkBinding(IType candidate, Location loc, Dictionary cxt) throws TypeConstraintException
  {
    candidate = TypeUtils.deRef(candidate);

    if (!kind.checkKind(candidate.kind()))
      throw new TypeConstraintException(StringUtils.msg(candidate, " not same kind as ", var), loc);
  }

  @Override
  public boolean sameConstraint(ITypeConstraint other, Location loc, Dictionary cxt) throws TypeConstraintException
  {
    if (other instanceof HasKind) {
      HasKind o = (HasKind) other;
      if (o == this)
        return true;
      else if (o.var.equals(var))
        return o.kind.equals(kind);
    }
    return false;
  }

  @Override
  public Collection<TypeVar> affectedVars()
  {
    return FixedList.create(var);
  }

  @Override
  public <X> void accept(ITypeVisitor<X> visitor, X cxt)
  {
  }

  @Override
  public <T, C, X> C transform(TypeTransformer<T, C, X> trans, X cxt)
  {
    return trans.transformHasKindConstraint(this, cxt);
  }

  @Override
  public int hashCode()
  {
    return (var.hashCode() * 37) + kind.hashCode();
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
      return true;
    else if (obj instanceof HasKind) {
      HasKind o = (HasKind) obj;
      return o.var.equals(var) && o.kind.equals(kind);
    }
    return false;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    DisplayType d = new DisplayType(disp);
    var.accept(d, null);
    disp.appendWord(StandardNames.HAS_KIND);
    kind.prettyPrint(disp);
  }

  @Override
  public void showConstraint(DisplayType disp)
  {
    var.accept(disp, null);
    PrettyPrintDisplay pp = disp.getDisp();
    pp.appendWord(StandardNames.HAS_KIND);
    kind.prettyPrint(pp);
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }
}
