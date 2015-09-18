package org.star_lang.star.compiler.cafe.compile.cont;

import org.star_lang.star.compiler.cafe.compile.CafeDictionary;
import org.star_lang.star.compiler.cafe.compile.CodeContext;
import org.star_lang.star.compiler.cafe.compile.ISpec;
import org.star_lang.star.compiler.cafe.compile.VarPattern;
import org.star_lang.star.compiler.canonical.IContentPattern;
import org.star_lang.star.compiler.codegen.PatternCompile;
import org.star_lang.star.data.type.Location;

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

public class PttrnCont implements IContinuation {
  private final IContentPattern ptn;
  private final CodeContext cxt;
  private final IContinuation succ, fail;
  private final VarPattern handler;

  public PttrnCont(IContentPattern ptn, VarPattern handler, CodeContext cxt, IContinuation succ, IContinuation fail) {
    this.ptn = ptn;
    this.handler = handler;
    this.cxt = cxt;
    this.succ = succ;
    this.fail = fail;
  }

  @Override
  public ISpec cont(ISpec src, CafeDictionary cxt, Location loc, CodeContext ccxt) {
    return PatternCompile.compile(ptn, src, handler, succ, fail, ccxt);
  }

  @Override
  public boolean isJump() {
    return succ.isJump() && fail.isJump();
  }

}
