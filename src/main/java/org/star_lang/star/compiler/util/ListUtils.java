package org.star_lang.star.compiler.util;

import java.util.ArrayList;
import java.util.List;

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
public class ListUtils
{

  public static <T> List<T> mergeLists(List<T> l1, List<T> l2)
  {
    List<T> result = new ArrayList<T>(l1);
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
