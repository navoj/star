package org.star_lang.star.compiler.cafe.compile.cont;

import org.star_lang.star.compiler.cafe.compile.CafeDictionary;
import org.star_lang.star.compiler.cafe.compile.CodeContext;
import org.star_lang.star.compiler.cafe.compile.ISpec;
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

public class ComboCont implements IContinuation
{
  private final IContinuation cont1, cont2;

  public ComboCont(IContinuation cont1, IContinuation cont2)
  {
    this.cont1 = cont1;
    this.cont2 = cont2;
  }

  @Override
  public ISpec cont(ISpec src, CafeDictionary cxt, Location loc, CodeContext ccxt)
  {
    ISpec left = cont1.cont(src, cxt, loc, ccxt);
    return cont2.cont(left, cxt, loc, ccxt);
  }

  @Override
  public boolean isJump()
  {
    return cont2.isJump();
  }
}