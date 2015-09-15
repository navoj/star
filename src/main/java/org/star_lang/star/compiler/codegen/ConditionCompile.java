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

import org.objectweb.asm.tree.LabelNode;
import org.star_lang.star.compiler.cafe.compile.CodeContext;
import org.star_lang.star.compiler.cafe.compile.ISpec;
import org.star_lang.star.compiler.cafe.compile.Sense;
import org.star_lang.star.compiler.cafe.compile.cont.IContinuation;
import org.star_lang.star.compiler.canonical.*;

/**
 * Created by fgm on 9/14/15.
 */
public class ConditionCompile implements TransformCondition<ISpec, ISpec, ISpec, ISpec, ISpec, IContinuation> {
  private final CodeContext cxt;
  private final Sense sense;
  private final LabelNode elLabel;

  public ConditionCompile(Sense sense, LabelNode elLabel, CodeContext cxt) {
    this.cxt = cxt;
    this.sense = sense;
    this.elLabel = elLabel;
  }

  public static ISpec compile(ICondition cond, Sense sense, LabelNode elLabel, CodeContext cxt, IContinuation cont) {
    ConditionCompile comp = new ConditionCompile(sense, elLabel, cxt);
    return cond.transform(comp, cont);
  }

  @Override
  public ISpec transformConditionCondition(ConditionCondition conditionCondition, IContinuation context) {
    return null;
  }

  @Override
  public ISpec transformConjunction(Conjunction conjunction, IContinuation context) {
    return null;
  }

  @Override
  public ISpec transformDisjunction(Disjunction disjunction, IContinuation context) {
    return null;
  }

  @Override
  public ISpec transformFalseCondition(FalseCondition falseCondition, IContinuation context) {
    return null;
  }

  @Override
  public ISpec transformImplies(Implies implies, IContinuation context) {
    return null;
  }

  @Override
  public ISpec transformIsTrue(IsTrue i, IContinuation context) {
    return null;
  }

  @Override
  public ISpec transformListSearch(ListSearch ptn, IContinuation context) {
    return null;
  }

  @Override
  public ISpec transformMatches(Matches matches, IContinuation context) {
    return null;
  }

  @Override
  public ISpec transformNegation(Negation negation, IContinuation context) {
    return null;
  }

  @Override
  public ISpec transformOtherwise(Otherwise otherwise, IContinuation context) {
    return null;
  }

  @Override
  public ISpec transformSearch(Search predication, IContinuation context) {
    return null;
  }

  @Override
  public ISpec transformTrueCondition(TrueCondition trueCondition, IContinuation context) {
    return null;
  }
}
