package org.star_lang.star.compiler.codegen;

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

import org.star_lang.star.compiler.cafe.compile.CodeContext;
import org.star_lang.star.compiler.cafe.compile.ISpec;
import org.star_lang.star.compiler.cafe.compile.VarPattern;
import org.star_lang.star.compiler.cafe.compile.cont.IContinuation;
import org.star_lang.star.compiler.canonical.*;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.data.value.Cons;

/**
 * Created by fgm on 9/8/15.
 */
public class PatternCompile implements TransformPattern<ISpec, ISpec, ISpec, ISpec, ISpec, IContinuation> {
  private final CodeContext cxt;
  private final IContinuation fail;

  private PatternCompile(IContinuation fail, CodeContext cxt) {
    this.fail = fail;
    this.cxt = cxt;
  }

  public static ISpec compile(IContentPattern ptn, ISpec src, VarPattern handler, IContinuation succ, IContinuation fail, CodeContext cxt) {
    PatternCompile comp = new PatternCompile(fail, cxt);
    return ptn.transformPattern(comp, succ);
  }

  @Override
  public ISpec transformRecordPtn(RecordPtn aggregatePtn, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformCastPtn(CastPtn ptn, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformMatchingPtn(MatchingPattern matches, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformPatternApplication(PatternApplication apply, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformRegexpPtn(RegExpPattern ptn, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformScalarPtn(ScalarPtn scalar, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformConstructorPtn(ConstructorPtn tuplePtn, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformVariablePtn(Variable variable, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformWherePattern(WherePattern wherePattern, IContinuation cont) {
    return null;
  }
}
