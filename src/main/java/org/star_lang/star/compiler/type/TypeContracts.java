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
package org.star_lang.star.compiler.type;

import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.TypeExp;

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

public class TypeContracts
{
  public static String contractImplTypeName(String name)
  {
    assert !name.startsWith("@");
    return /* "@" + */name;
  }

  public static TypeExp contractImplType(TypeExp contract)
  {
    assert !contract.typeLabel().startsWith("@");
    return (TypeExp) TypeUtils.typeExp(contractImplTypeName(contract.typeLabel()), contract.getTypeArgs());
  }

  public static String contractFallbackName(IType contract)
  {
    contract = TypeUtils.unwrap(contract);
    assert !contract.typeLabel().startsWith("@");
    return "@" + contractImplTypeName(contract.typeLabel()) + "#@@";
  }

  public static boolean isContractFallbackName(String name)
  {
    return name.startsWith("@") && name.endsWith("#@@");
  }

}
