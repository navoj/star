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
 * @author fgm
 * 
 */
public class Simplify extends DefaultTransformer<SimplifyCxt>
{

  @Override
  public IContentExpression transformLetTerm(LetTerm let, SimplifyCxt context)
  {
    // TODO Auto-generated method stub
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
    // TODO Auto-generated method stub
    return super.transformLetAction(let, context);
  }

}
