package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;

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
