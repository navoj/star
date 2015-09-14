package org.star_lang.star.compiler.sources;

import org.star_lang.star.compiler.cafe.compile.Utils;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.operators.ICafeBuiltin;
/*  * Copyright (c) 2015. Francis G. McCabe  *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file  * except in compliance with the License. You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software distributed under the  * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied. See the License for the specific language governing  * permissions and limitations under the License.  */
public final class NestedBuiltin implements ICafeBuiltin
{
  private final String name;
  private final IType type;
  private final Class<? extends IFunction> klass;

  public NestedBuiltin(String name, IType type, Class<? extends IFunction> klass)
  {
    this.name = name;
    this.type = type;
    this.klass = klass;
  }

  @Override
  public IType getType()
  {
    return type;
  }

  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public String getJavaName()
  {
    return Utils.javaIdentifierOf(name);
  }

  @Override
  public String getJavaType()
  {
    return Utils.javaInternalClassName(klass);
  }

  @Override
  public String getJavaSig()
  {
    return Utils.javaTypeSig(klass);
  }

  @Override
  public String getJavaInvokeName()
  {
    return "enter";
  }

  @Override
  public String getJavaInvokeSignature()
  {
    return Utils.javaInvokeSig(klass, "enter");
  }

  @Override
  public boolean isStatic()
  {
    return false;
  }

  @Override
  public Class<?> getImplClass()
  {
    return klass;
  }
}