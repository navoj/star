package org.star_lang.star.compiler.sources;

import java.util.List;
import java.util.Map;

import org.star_lang.star.data.type.ITypeDescription;
import org.star_lang.star.operators.ICafeBuiltin;

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

public class JavaInfo
{
  private final String className;
  private final Map<String, ICafeBuiltin> methods;
  private final List<ITypeDescription> types;

  JavaInfo(String className, Map<String, ICafeBuiltin> methods, List<ITypeDescription> types)
  {
    this.className = className;
    this.methods = methods;
    this.types = types;
  }

  /**
   * Return the NAME of the imported Java class
   * 
   * @return
   */
  public String getClassName()
  {
    return className;
  }

  /**
   * Return the methods that are defined as a result of importing this Java class. This is
   * essentially all the public static functions defined in the class -- provided that their types
   * are 'simple': involving only numbers and strings.
   * 
   * If the class implements the IFunction interface, or if any of its inner classes implement the
   * IFunction interface, then they are imported as functions also. In that case, the function
   * signature may be any valid StarRules function type.
   * 
   * @return
   */
  public Map<String, ICafeBuiltin> getMethods()
  {
    return methods;
  }

  /**
   * Return a list of type specifications corresponding to types declared in the Java import
   * 
   * @return
   */
  public List<ITypeDescription> getTypes()
  {
    return types;
  }

  /**
   * Return an array of the defined symbols. This is primarily used in dependency analysis.
   * 
   * @return
   */
  public String[] defines()
  {
    return methods.keySet().toArray(new String[methods.size()]);
  }
}