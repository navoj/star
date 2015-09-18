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
import org.star_lang.star.compiler.cafe.compile.*;
import org.star_lang.star.compiler.cafe.compile.cont.IContinuation;
import org.star_lang.star.compiler.cafe.compile.cont.JumpCont;
import org.star_lang.star.compiler.cafe.compile.cont.PttrnCont;
import org.star_lang.star.compiler.canonical.*;

/**
 * Created by fgm on 9/14/15.
 */
public class ConditionCompile implements TransformCondition<ISpec, ISpec, ISpec, ISpec, ISpec, IContinuation> {
  private final CodeContext cxt;
  private final IContinuation fail;

  public ConditionCompile(IContinuation fail, CodeContext cxt) {
    this.cxt = cxt;
    this.fail = fail;
  }

  public static ISpec compile(ICondition cond, CodeContext cxt, IContinuation fail, IContinuation cont) {
    ConditionCompile comp = new ConditionCompile(fail, cxt);
    return cond.transform(comp, cont);
  }

  @Override
  public ISpec transformConditionCondition(ConditionCondition cond, IContinuation cont) {

    LabelNode nxLbl = new LabelNode();
    JumpCont intCont = new JumpCont(nxLbl);

    LabelNode elLbl = new LabelNode();
    JumpCont elseCont = new JumpCont(elLbl);

    compile(cond.getTest(), cxt, elseCont, intCont);
    Utils.jumpTarget(cxt.getIns(), nxLbl);
    compile(cond.getLhs(), cxt, fail, intCont);
    Utils.jumpTarget(cxt.getIns(), elLbl);
    compile(cond.getRhs(), cxt, fail, cont);

    return cont.cont(SrcSpec.prcSrc, cxt.getDict(), cond.getLoc(), cxt);
  }

  @Override
  public ISpec transformConjunction(Conjunction conjunction, IContinuation cont) {
    LabelNode nxLbl = new LabelNode();
    JumpCont intCont = new JumpCont(nxLbl);

    compile(conjunction.getLhs(), cxt, fail, intCont);
    Utils.jumpTarget(cxt.getIns(), nxLbl);
    return compile(conjunction.getRhs(), cxt, fail, cont);
  }

  @Override
  public ISpec transformDisjunction(Disjunction disjunction, IContinuation cont) {
    compile(disjunction.getLhs(), cxt, fail, cont);
    return compile(disjunction.getRhs(), cxt, fail, cont);
  }

  @Override
  public ISpec transformFalseCondition(FalseCondition falseCondition, IContinuation cont) {
    return fail.cont(SrcSpec.prcSrc, cxt.getDict(), falseCondition.getLoc(), cxt);
  }

  @Override
  public ISpec transformImplies(Implies implies, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformIsTrue(IsTrue i, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformListSearch(ListSearch ptn, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformMatches(Matches matches, IContinuation cont) {
    PttrnCont ptnCont = new PttrnCont(matches.getPtn(),new Patterns.NamePtn(),cxt,cont,fail);
    return ExpressionCompile.compile(matches.getExp(),ptnCont,cxt);
  }

  @Override
  public ISpec transformNegation(Negation negation, IContinuation cont) {
    return compile(negation.getNegated(), cxt, cont, fail);
  }

  @Override
  public ISpec transformOtherwise(Otherwise otherwise, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformSearch(Search predication, IContinuation cont) {
    return null;
  }

  @Override
  public ISpec transformTrueCondition(TrueCondition trueCondition, IContinuation cont) {
    return cont.cont(SrcSpec.prcSrc, cxt.getDict(), trueCondition.getLoc(), cxt);
  }
}
