package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.util.ListUtils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;

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
