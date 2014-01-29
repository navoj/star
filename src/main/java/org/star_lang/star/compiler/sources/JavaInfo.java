package org.star_lang.star.compiler.sources;

import java.util.List;
import java.util.Map;

import org.star_lang.star.operators.ICafeBuiltin;

import com.starview.platform.data.type.ITypeDescription;

/**
 * A Java import is managed by a JavaInfo class.
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
   * Return the name of the imported Java class
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