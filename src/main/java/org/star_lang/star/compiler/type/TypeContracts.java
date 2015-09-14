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
