package org.star_lang.star.compiler.transform;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.star_lang.star.compiler.type.TypeUtils;
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

public class PrimitiveOverloader
{
  /**
   * Handle overloading for primitive types.
   * 
   * This special handling is required because primitive types may not be passed in generic type
   * arguments; making them unsuitable for normal handling of overloading
   */
  private static Map<String, Map<String, Map<IType, String>>> primitiveContracts = new HashMap<>();

  public static void declarePrimitiveImplementation(String contract, String method, IType rawType, String escape)
  {
    Map<String, Map<IType, String>> conMap = primitiveContracts.get(contract);
    if (conMap == null) {
      conMap = new HashMap<>();
      primitiveContracts.put(contract, conMap);
    }

    Map<IType, String> typeMap = conMap.get(method);
    if (typeMap == null) {
      typeMap = new HashMap<>();
      conMap.put(method, typeMap);
    }

    typeMap.put(rawType, escape);
  }

  public static String getPrimitive(TypeExp contract, String mtd)
  {
    Map<String, Map<IType, String>> conMap = primitiveContracts.get(contract.typeLabel());

    if (conMap == null)
      return null;
    else {
      assert TypeUtils.typeArity(contract) == 1;
      IType prType = TypeUtils.deRef(TypeUtils.getTypeArg(contract,0));
      if (TypeUtils.typeArity(prType) == 0) {
        Map<IType, String> typeMap = conMap.get(mtd);
        if (typeMap == null)
          return null;
        else
          return typeMap.get(prType);
      } else
        return null;
    }
  }

  public static boolean hasPrimitiveImplementation(IType contract)
  {
    Map<String, Map<IType, String>> conMap = primitiveContracts.get(contract.typeLabel());

    if (conMap == null)
      return false;
    else {
      assert TypeUtils.typeArity(contract) == 1;
      IType prType = TypeUtils.deRef(TypeUtils.getTypeArg(contract, 0));
      if (TypeUtils.typeArity(prType) == 0) {
        for (Entry<String, Map<IType, String>> entry : conMap.entrySet()) {
          if (entry.getValue().containsKey(prType))
            return true;
        }
        return false;
      } else
        return false;
    }
  }
}
