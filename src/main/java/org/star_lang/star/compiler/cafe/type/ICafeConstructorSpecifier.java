package org.star_lang.star.compiler.cafe.type;

import org.star_lang.star.data.type.IType;

/**
 * Encapsulate cafe specific elements of the constructor value specifiers
 * 
 *
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
public interface ICafeConstructorSpecifier
{
  String getLabel();
  
  String getTypeLabel();

  int getConIx();

  Class<?> getCafeClass();

  void setCafeClass(Class<?> klass);

  String getJavaType();

  boolean hasMember(String id);

  IType getConType();

  String memberName(int ix);

  ICafeConstructorSpecifier cleanCopy();
}
