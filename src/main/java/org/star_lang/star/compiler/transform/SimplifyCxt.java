package org.star_lang.star.compiler.transform;

import java.util.HashMap;
import java.util.Map;

import org.star_lang.star.compiler.canonical.Resolved;
import org.star_lang.star.compiler.canonical.Variable;
import org.star_lang.star.compiler.util.GenSym;
import org.star_lang.star.compiler.util.Pair;

public class SimplifyCxt
{
  private final Map<Resolved, Pair<Variable, Integer>> resolved;

  public SimplifyCxt()
  {
    this(new HashMap<>());
  }

  public SimplifyCxt(Map<Resolved, Pair<Variable, Integer>> resolved)
  {
    this.resolved = resolved;
  }

  public Map<Resolved, Pair<Variable, Integer>> getResolved()
  {
    return resolved;
  }

  public Variable recordResolved(Resolved res)
  {
    Pair<Variable, Integer> var = resolved.get(res);
    if (var == null) {
      Variable v = Variable.create(res.getLoc(), res.getType(), GenSym.genSym("_"));
      resolved.put(res, Pair.pair(v, 1));
      return v;
    } else
      resolved.put(res, Pair.pair(var.left, var.right + 1));
    return var.left;
  }
}
