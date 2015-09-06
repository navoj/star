package org.star_lang.star.data.type;

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
  private final Kind kind;

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
