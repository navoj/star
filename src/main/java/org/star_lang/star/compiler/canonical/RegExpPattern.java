package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;

@SuppressWarnings("serial")
public class RegExpPattern extends ContentPattern
{
  private final String pattern;
  private final IContentPattern groups[];
  private final NFA nfa;

  public RegExpPattern(Location loc, String pattern, NFA nfa, IContentPattern groups[])
  {
    super(loc, StandardTypes.stringType);
    this.pattern = pattern;
    this.nfa = nfa;
    this.groups = groups;
  }

  public int groupCount()
  {
    return groups.length;
  }

  public IContentPattern group(int ix)
  {
    return groups[ix];
  }

  public IContentPattern[] getGroups()
  {
    return groups;
  }

  public NFA getNfa()
  {
    return nfa;
  }

  public String getRegexpPtn()
  {
    return pattern;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append("`");
    disp.append(pattern);
    if (groups.length > 0) {
      disp.append(":");
      String sep = "";
      for (IContentPattern el : groups) {
        disp.append(sep);
        sep = ", ";
        el.prettyPrint(disp);
      }
    }
    disp.append("`");
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitRegexpPtn(this);
  }

  @Override
  public <A, E, P, C, D, T> P transformPattern(TransformPattern<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformRegexpPtn(this, context);
  }
}
