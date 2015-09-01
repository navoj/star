package org.star_lang.star.compiler.ast;

import org.star_lang.star.compiler.type.DisplayType;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.IType;

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
public class TypeAttribute extends BaseAttribute<IType>
{
  private final IType type;

  public TypeAttribute(IType type)
  {
    super(false, 0);
    this.type = type;
  }

  public IType getType()
  {
    return type;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    DisplayType.display(disp, type);
  }

  @Override
  public IType attribute(IType original)
  {
    return type;
  }

}
