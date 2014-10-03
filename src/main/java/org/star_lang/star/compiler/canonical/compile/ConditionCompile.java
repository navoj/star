package org.star_lang.star.compiler.canonical.compile;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.star_lang.star.compiler.cafe.compile.ISpec;
import org.star_lang.star.compiler.cafe.compile.Sense;
import org.star_lang.star.compiler.cafe.compile.Utils;
import org.star_lang.star.compiler.canonical.ConditionCondition;
import org.star_lang.star.compiler.canonical.Conjunction;
import org.star_lang.star.compiler.canonical.Disjunction;
import org.star_lang.star.compiler.canonical.FalseCondition;
import org.star_lang.star.compiler.canonical.Implies;
import org.star_lang.star.compiler.canonical.IsTrue;
import org.star_lang.star.compiler.canonical.ListSearch;
import org.star_lang.star.compiler.canonical.Matches;
import org.star_lang.star.compiler.canonical.Negation;
import org.star_lang.star.compiler.canonical.Otherwise;
import org.star_lang.star.compiler.canonical.Search;
import org.star_lang.star.compiler.canonical.TransformCondition;
import org.star_lang.star.compiler.canonical.TrueCondition;
import org.star_lang.star.compiler.canonical.compile.cont.PttrnCont;

/**
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA
 * 
 * @author fgm
 *
 */
public class ConditionCompile implements TransformCondition<ISpec, ISpec, ISpec, ISpec, ISpec, ConditionContext>
{

  @Override
  public ISpec transformConditionCondition(ConditionCondition cond, ConditionContext cxt)
  {
    Sense sense = cxt.getSense();
    InsnList ins = cxt.getIns();

    switch (sense) {
    case jmpOnFail: {
      LabelNode ok = new LabelNode();
      LabelNode other = new LabelNode();
      ConditionContext tsub = cxt.fork(Sense.jmpOnOk, cxt.getLf(), other);
      cond.getTest().transform(this, tsub);
      ConditionContext thSub = cxt.fork(sense.negate(), cxt.getLf(), ok);
      cond.getLhs().transform(this, thSub);
      ins.add(new JumpInsnNode(Opcodes.GOTO, cxt.getLf()));
      Utils.jumpTarget(ins, other);
      cond.getRhs().transform(this, cxt);
      Utils.jumpTarget(ins, ok);
    }
    case jmpOnOk: {
      LabelNode fail = new LabelNode();
      LabelNode other = new LabelNode();
      ConditionContext tsub = cxt.fork(Sense.jmpOnFail, cxt.getLf(), other);
      cond.getTest().transform(this, tsub);
      cond.getLhs().transform(this, cxt);
      ins.add(new JumpInsnNode(Opcodes.GOTO, fail));
      Utils.jumpTarget(ins, other);
      cond.getRhs().transform(this, cxt);
      Utils.jumpTarget(ins, fail);
    }
    }

    return null;
  }

  @Override
  public ISpec transformConjunction(Conjunction conj, ConditionContext cxt)
  {
    switch (cxt.getSense()) {
    case jmpOnFail:
      conj.getLhs().transform(this, cxt);
      conj.getRhs().transform(this, cxt);
      break;
    case jmpOnOk: {
      LabelNode nxLbl = new LabelNode();
      ConditionContext sub = cxt.fork(Sense.jmpOnFail, nxLbl, cxt.getLx());
      conj.getLhs().transform(this, sub);
      conj.getRhs().transform(this, cxt);
      InsnList ins = cxt.getIns();
      Utils.jumpTarget(ins, nxLbl);
    }
    }

    return null;
  }

  @Override
  public ISpec transformDisjunction(Disjunction disj, ConditionContext cxt)
  {
    switch (cxt.getSense()) {
    case jmpOnOk:
      disj.getLhs().transform(this, cxt);
      disj.getRhs().transform(this, cxt);
      break;
    case jmpOnFail: {
      LabelNode nxLbl = new LabelNode();
      ConditionContext sub = cxt.fork(Sense.jmpOnOk, cxt.getLf(), nxLbl);
      disj.getLhs().transform(this, sub);
      disj.getRhs().transform(this, cxt);
      InsnList ins = cxt.getIns();
      Utils.jumpTarget(ins, nxLbl);
    }
    }
    return null;
  }

  @Override
  public ISpec transformFalseCondition(FalseCondition falseCondition, ConditionContext cxt)
  {
    if (cxt.getSense() == Sense.jmpOnFail)
      cxt.getIns().add(new JumpInsnNode(Opcodes.GOTO, cxt.getLf()));
    return null;
  }

  @Override
  public ISpec transformImplies(Implies implies, ConditionContext cxt)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ISpec transformIsTrue(IsTrue i, ConditionContext cxt)
  {
    return null;
  }

  @Override
  public ISpec transformListSearch(ListSearch ptn, ConditionContext cxt)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ISpec transformMatches(Matches matches, ConditionContext cxt)
  {
    LabelNode exLbl = new LabelNode();
    ConditionContext sub = cxt.fork(cxt.getSense(),cxt.getLf(),exLbl);
    CompileContext expCxt = cxt.fork(new PttrnCont(matches.getPtn(), sub));
    matches.getExp().transform(new ExpressionCompile(), expCxt);
    Utils.jumpTarget(cxt.getIns(), exLbl);
    
    return null;
  }

  @Override
  public ISpec transformNegation(Negation neg, ConditionContext cxt)
  {
    neg.getNegated().transform(this, cxt.fork(cxt.getSense().negate(), cxt.getLf(), cxt.getLx()));
    return null;
  }

  @Override
  public ISpec transformOtherwise(Otherwise otherwise, ConditionContext cxt)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ISpec transformSearch(Search predication, ConditionContext cxt)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ISpec transformTrueCondition(TrueCondition trueCondition, ConditionContext cxt)
  {
    if (cxt.getSense() == Sense.jmpOnOk)
      cxt.getIns().add(new JumpInsnNode(Opcodes.GOTO, cxt.getLx()));
    return null;
  }

}
