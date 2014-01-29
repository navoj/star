package org.star_lang.star.compiler.transform;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.star_lang.star.compiler.type.TypeUtils;

import com.starview.platform.data.type.IType;
import com.starview.platform.data.type.TypeExp;

/**
 * 
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
 *
 */
public class PrimitiveOverloader
{
  /**
   * Handle overloading for primitive types.
   * 
   * This special handling is required because primitive types may not be passed in generic type
   * arguments; making them unsuitable for normal handling of overloading
   */
  private static Map<String, Map<String, Map<IType, String>>> primitiveContracts = new HashMap<String, Map<String, Map<IType, String>>>();

  public static void declarePrimitiveImplementation(String contract, String method, IType rawType, String escape)
  {
    Map<String, Map<IType, String>> conMap = primitiveContracts.get(contract);
    if (conMap == null) {
      conMap = new HashMap<String, Map<IType, String>>();
      primitiveContracts.put(contract, conMap);
    }

    Map<IType, String> typeMap = conMap.get(method);
    if (typeMap == null) {
      typeMap = new HashMap<IType, String>();
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
      assert contract.typeArity() == 1;
      IType prType = TypeUtils.deRef(contract.getTypeArg(0));
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
