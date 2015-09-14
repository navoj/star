package org.star_lang.star.compiler.cafe.compile.cont;

import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.cafe.compile.CafeDictionary;
import org.star_lang.star.compiler.cafe.compile.CodeContext;
import org.star_lang.star.compiler.cafe.compile.ISpec;
import org.star_lang.star.compiler.cafe.compile.Patterns;
import org.star_lang.star.compiler.util.AccessMode;
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

public class PatternCont implements IContinuation
{
  private final IAbstract ptn;
  private final CafeDictionary dict, outer;
  private final AccessMode access;
  private final LabelNode endLabel;
  private final IContinuation succ, fail;

  public PatternCont(IAbstract ptn, CafeDictionary dict, CafeDictionary outer, AccessMode access, MethodNode mtd,
                     LabelNode endLabel, IContinuation succ, IContinuation fail)
  {
    this.ptn = ptn;
    this.dict = dict;
    this.outer = outer;
    this.access = access;
    this.endLabel = endLabel;
    this.succ = succ;
    this.fail = fail;
  }

  @Override
  public ISpec cont(ISpec src, CafeDictionary cxt, Location loc, CodeContext ccxt)
  {
    Patterns.compilePttrn(ptn, access, src, dict, outer, endLabel, succ, fail, ccxt);
    return src;
  }

  @Override
  public boolean isJump()
  {
    return succ.isJump() && fail.isJump();
  }

}
