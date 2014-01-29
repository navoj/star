package org.star_lang.star.compiler.transform;

import java.util.HashMap;
import java.util.Map;

import org.star_lang.star.compiler.canonical.Canonical;
import org.star_lang.star.compiler.canonical.CopyTransformer;
import org.star_lang.star.compiler.canonical.Variable;

public class Replacer extends CopyTransformer
{

  public Replacer(Variable old, Canonical rep)
  {
    super(createMap(old, rep));
  }

  private static Map<Variable, Canonical> createMap(Variable old, Canonical rep)
  {
    Map<Variable, Canonical> map = new HashMap<>();

    map.put(old, rep);
    return map;
  }
}
