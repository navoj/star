package org.star_lang.star.data.type;

/** 
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
 */
import org.star_lang.star.compiler.type.DisplayType;

@SuppressWarnings("serial")
public abstract class AbstractType implements IType
{
  public static final String ALIAS = "alias";
  public static final String CONTRACT = "contract";
  public static final String IMPLEMENTATION = "implementation";
  public static final String FALLBACK = "fallback";
  public static final String IMPLEMENTS = "implements";
  public static final String INSTANCE_OF = "instance of";
  public static final String ACTION = "action";
  public static final String OVER = "over";
  public static final String DETERMINES = "determines";
  public static final String FUN_TYPE = "=>";
  public static final String OVERLOADED_TYPE = "$=>";
  public static final String PTN_TYPE = "<=";
  public static final String CONSTRUCTOR_TYPE = "<=>";
  public static final String TYPE = "type";
  public static final String KIND = "kind";
  public static final String HAS_KIND = "has kind";
  public static final String TUPLE = "tuple";
  public static final String IS_TUPLE = "is tuple";
  public static final String FORALL = "forall";
  public static final String FOR_ALL = "for all";
  public static final String ST = "suchthat";
  public static final String S_T = "such that";
  public static final String EXISTS = "exists";

  private final String label;
  protected final Kind kind;

  protected AbstractType(String label, Kind kind)
  {
    this.label = label;
    this.kind = kind;
  }

  @Override
  public String typeLabel()
  {
    return label;
  }

  @Override
  public Kind kind()
  {
    return kind;
  }

  @Override
  public String toString()
  {
    return DisplayType.toString(this);
  }
}
