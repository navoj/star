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
