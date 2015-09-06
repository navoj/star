package org.star_lang.star.compiler.util;

import java.util.ArrayList;
import java.util.List;

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

public class ListUtils
{

  public static <T> List<T> mergeLists(List<T> l1, List<T> l2)
  {
    List<T> result = new ArrayList<>(l1);
    for (T el : l2)
      if (!result.contains(el))
        result.add(el);
    return result;
  }

  public static <T> boolean isSubset(List<T> set, List<T> sub)
  {
    for (T el : sub)
      if (!set.contains(el))
        return false;
    return true;
  }

  public static <T> boolean assertNoNulls(T args[])
  {
    for (T el : args)
      if (el == null)
        return false;
    return true;
  }

  public static <T> boolean assertNoNulls(List<T> args)
  {
    for (T el : args)
      if (el == null)
        return false;
    return true;
  }

}
