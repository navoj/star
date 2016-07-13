package org.star_lang.star.operators.string;

import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.string.runtime.String2Number.*;

/*
 * The String2Number functions parse strings into different kinds of numbers.
 * 
 * This is the compile-time declarations of the functions
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

public abstract class String2Number
{

  public static void declare(Intrinsics cxt)
  {
    cxt.declareBuiltin(new Builtin(String2Boolean.name, String2Boolean.type(), String2Boolean.class));
    cxt.declareBuiltin(new Builtin(String2Integer.name, String2Integer.type(), String2Integer.class));
    cxt.declareBuiltin(new Builtin(String2Long.name, String2Long.type(), String2Long.class));
    cxt.declareBuiltin(new Builtin(Hex2Integer.name, Hex2Integer.type(), Hex2Integer.class));
    cxt.declareBuiltin(new Builtin(Hex2Long.name, Hex2Long.type(), Hex2Long.class));
    cxt.declareBuiltin(new Builtin(String2Float.name, String2Float.type(), String2Float.class));
  }
}
