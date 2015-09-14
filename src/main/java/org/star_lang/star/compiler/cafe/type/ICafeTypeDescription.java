package org.star_lang.star.compiler.cafe.type;

import java.util.List;
import java.util.SortedMap;

import org.star_lang.star.compiler.cafe.compile.ISpec;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.IValueSpecifier;
import org.star_lang.star.data.type.TypeConstraintException;

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

public interface ICafeTypeDescription
{
  String getJavaName();

  String getJavaSig();

  IValueSpecifier declareConstructor(String name, IType conType, int conIx, String javaTypeName, String javaOwner,
      String javaInvokeSig, String javaSafeName) throws TypeConstraintException;

  IValueSpecifier declareConstructor(String name, IType conType, int conIx, ISpec spec, String javaTypeName,
      String javaOwner, String javaConSig, String javaSafeName, List<ISpec> fields, SortedMap<String, Integer> index)
      throws TypeConstraintException;
}
