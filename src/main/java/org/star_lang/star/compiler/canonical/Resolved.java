package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.util.ListUtils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;

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
public class Resolved extends BaseExpression
{
  private final IContentExpression over;
  private final IContentExpression dicts[];
  private final IType dictType;

  public Resolved(Location loc, IType type, IType dictType, IContentExpression over, IContentExpression... dicts)
  {
    super(loc, type);
    this.over = over;
    this.dicts = dicts;
    this.dictType = dictType;
    assert over != null && dicts != null && ListUtils.assertNoNulls(dicts);
  }

  public IContentExpression getOver()
  {
    return over;
  }

  public IType getDictType()
  {
    return dictType;
  }

  public IContentExpression[] getDicts()
  {
    return dicts;
  }

  public int getArity()
  {
    return dicts.length;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    over.prettyPrint(disp);
    disp.append("[");
    disp.prettyPrint(dicts, ", ");
    disp.append("]");
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitResolved(this);
  }

  @Override
  public <A, E, P, C, D, T> E transform(TransformExpression<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformResolved(this, context);
  }

  @Override
  public int hashCode()
  {
    int hash = over.hashCode();
    for (int ix = 0; ix < dicts.length; ix++)
      hash = hash * 37 + dicts[ix].hashCode();
    return hash;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof Resolved) {
      Resolved other = (Resolved) obj;
      if (other.over.equals(over) && other.dicts.length == dicts.length) {
        for (int ix = 0; ix < dicts.length; ix++)
          if (!other.dicts[ix].equals(dicts[ix]))
            return false;
        return true;
      }
    }
    return false;
  }

}
