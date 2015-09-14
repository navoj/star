package org.star_lang.star.operators.string;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.string.runtime.KeyGenerate;

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

public class KeyGen extends Builtin
{
  public final static String name = "$keyGen$";

  public KeyGen()
  {
    super(name, TypeUtils.functionType(StandardTypes.rawIntegerType), KeyGenerate.class);
  }

  public static void declare(Intrinsics cxt)
  {
    cxt.declareBuiltin(new KeyGen());
  }
}
