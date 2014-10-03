package org.star_lang.star.compiler.format;

import org.star_lang.star.compiler.ast.BaseAttribute;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;

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
public class BooleanAttribute extends BaseAttribute<Boolean>
{
  private final boolean att;

  public BooleanAttribute(boolean flag, int specificity)
  {
    this(flag, specificity, true);
  }

  public BooleanAttribute(boolean att, int specificity, boolean inheritable)
  {
    super(inheritable, specificity);
    this.att = att;
  }

  @Override
  public Boolean attribute(Boolean original)
  {
    return att;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append(att ? "true" : "false");
  }

}
