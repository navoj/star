package org.star_lang.star.operators.string;

import org.star_lang.star.compiler.operator.StandardNames;
import org.star_lang.star.compiler.transform.PrimitiveOverloader;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.string.runtime.StringCompare.StringEQ;
import org.star_lang.star.operators.string.runtime.StringCompare.StringGE;
import org.star_lang.star.operators.string.runtime.StringCompare.StringGT;
import org.star_lang.star.operators.string.runtime.StringCompare.StringLE;
import org.star_lang.star.operators.string.runtime.StringCompare.StringLT;
import org.star_lang.star.operators.string.runtime.StringCompare.StringNE;

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

public class StringCompare extends Builtin
{

  private static final IType rawStringStype = StandardTypes.rawStringType;

  private StringCompare(String name, Class<?> implClass)
  {
    super(name, TypeUtils.functionType(rawStringStype, rawStringStype, StandardTypes.booleanType), implClass);
  }

  public static void declare(Intrinsics cxt)
  {
    String equality = StandardNames.EQUALITY;
    PrimitiveOverloader.declarePrimitiveImplementation(equality, StandardNames.EQUAL, rawStringStype, StringEQ.NAME);
    cxt.declareBuiltin(new StringCompare(StringEQ.NAME, StringEQ.class));

    cxt.declareBuiltin(new StringCompare(StringNE.NAME, StringNE.class));
    PrimitiveOverloader.declarePrimitiveImplementation(equality, StandardNames.NOT_EQUAL, rawStringStype, StringNE.NAME);

    cxt.declareBuiltin(new StringCompare("__string_lt", StringLT.class));
    cxt.declareBuiltin(new StringCompare("__string_le", StringLE.class));
    cxt.declareBuiltin(new StringCompare("__string_ge", StringGE.class));
    cxt.declareBuiltin(new StringCompare("__string_gt", StringGT.class));
  }
}