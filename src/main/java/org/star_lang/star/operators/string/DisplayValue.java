package org.star_lang.star.operators.string;

import org.star_lang.star.compiler.ast.DisplayAst;
import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.string.runtime.DisplayTerm;

/**
 * Implement display of stuff
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
public class DisplayValue
{
  public static final String displayQuoted = "display_quoted";

  public static void declare(Intrinsics cxt)
  {
    cxt.declareBuiltin(new Builtin(DisplayTerm.name, DisplayTerm.funType(), DisplayTerm.class));

    cxt.declareBuiltin(new Builtin(DisplayAst.name, DisplayAst.type(), DisplayAst.class));

  }
}
