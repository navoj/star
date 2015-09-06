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
import org.star_lang.star.compiler.canonical.Variable;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.type.DisplayType;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;

@SuppressWarnings("serial")
public class ContractImplementation implements PrettyPrintable
{
  private final String implementedContract;
  private final Variable implementation;
  private final boolean isDefault;

  public ContractImplementation(String implementedContract, Variable implementation, boolean isDefault)
  {
    this.implementedContract = implementedContract;
    this.implementation = implementation;
    this.isDefault = isDefault;
  }

  public String getImplementedContract()
  {
    return implementedContract;
  }

  public Variable getImplementation()
  {
    return implementation;
  }

  public boolean isDefault()
  {
    return isDefault;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    implementation.prettyPrint(disp);
    disp.appendWord(StandardNames.IMPLEMENTS);
    IType implType = TypeUtils.unwrap(implementation.getType());
    if (TypeUtils.isOverloadedType(implType)) {
      DisplayType.displayContract(disp, TypeUtils.getOverloadedType(implType));
      IType[] requirements = TypeUtils.getOverloadRequirements(implType);
      String sep = StandardNames.WHERE;
      for (IType req : requirements) {
        disp.appendWord(sep);
        sep = StandardNames.AND;
        DisplayType.displayContract(disp, req);
      }
    } else
      DisplayType.displayContract(disp, implType);
    if (isDefault)
      disp.appendWord(StandardNames.DEFAULT);
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }
}
