package com.starview.platform.data.type;

/**
 * Copyright (C) 2013 Starview Inc
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
        sep = StandardNames.ALSO;
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
