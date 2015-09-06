package org.star_lang.star.compiler.transform;

import org.star_lang.star.compiler.canonical.DefaultTransformer;
import org.star_lang.star.compiler.canonical.IContentAction;
import org.star_lang.star.compiler.canonical.IContentExpression;
import org.star_lang.star.compiler.canonical.LetAction;
import org.star_lang.star.compiler.canonical.LetTerm;
import org.star_lang.star.compiler.canonical.Resolved;

/**
 * Simplify a canonical expression by pulling out invokations of resolved
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

public class Simplify extends DefaultTransformer<SimplifyCxt>
{

  @Override
  public IContentExpression transformLetTerm(LetTerm let, SimplifyCxt context)
  {
    return super.transformLetTerm(let, context);
  }

  @Override
  public IContentExpression transformResolved(Resolved res, SimplifyCxt context)
  {
    IContentExpression[] dicts = res.getDicts();
    IContentExpression[] ndicts = new IContentExpression[dicts.length];
    for (int ix = 0; ix < dicts.length; ix++) {
      if (dicts[ix] instanceof Resolved)
        ndicts[ix] = dicts[ix].transform(this, context);
      else
        return res;
    }
    Resolved nres = new Resolved(res.getLoc(), res.getType(), res.getDictType(), res.getOver(), ndicts);
    return context.recordResolved(nres);
  }

  @Override
  public IContentAction transformLetAction(LetAction let, SimplifyCxt context)
  {
    return super.transformLetAction(let, context);
  }

}
