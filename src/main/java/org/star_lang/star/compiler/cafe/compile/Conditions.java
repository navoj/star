package org.star_lang.star.compiler.cafe.compile;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.ast.Apply;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.cafe.CafeSyntax;
import org.star_lang.star.compiler.cafe.Names;
import org.star_lang.star.compiler.cafe.compile.cont.BranchCont;
import org.star_lang.star.compiler.cafe.compile.cont.IContinuation;
import org.star_lang.star.compiler.cafe.compile.cont.JumpCont;
import org.star_lang.star.compiler.cafe.compile.cont.PatternCont;
import org.star_lang.star.compiler.util.AccessMode;

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

public class Conditions {
  private final static Map<String, ICompileCondition> handlers = new HashMap<>();

  static {
    handlers.put(Names.CONSTRUCT, new CompileTruthValue());
    handlers.put(Names.AND, new CompileConjunction());
    handlers.put(Names.OR, new CompileDisjunction());
    handlers.put(Names.NOT, new CompileNegation());
    handlers.put(Names.IF, new CompileConditional());
    handlers.put(Names.MATCH, new CompileMatch());
  }

  public static void compileCond(IAbstract cond, Sense sense, LabelNode elLabel, CodeContext ccxt) {
    if (cond instanceof Apply) {
      ICompileCondition handler = handlers.get(((Apply) cond).getOp());
      if (handler != null) {
        handler.handleCond((Apply) cond, sense, elLabel, ccxt);
        return;
      }
    }

    IContinuation cont = new BranchCont(sense, elLabel, ccxt.getDict());

    Expressions.compileExp(cond, cont, ccxt);
  }

  private static class CompileTruthValue implements ICompileCondition {

    @Override
    public void handleCond(Apply cond, Sense sense, LabelNode elLabel,
                           CodeContext ccxt) {
      String label = CafeSyntax.constructorOp(cond);
      MethodNode mtd = ccxt.getMtd();
      ErrorReport errors = ccxt.getErrors();

      if (label.equals(Names.FALSE))
        switch (sense) {
          case jmpOnFail:
            mtd.instructions.add(new JumpInsnNode(Opcodes.GOTO, elLabel));
            break;
          case jmpOnOk:
        }
      else if (label.equals(Names.TRUE))
        switch (sense) {
          case jmpOnOk:
            mtd.instructions.add(new JumpInsnNode(Opcodes.GOTO, elLabel));
            break;
          case jmpOnFail:
        }
      else
        errors.reportError("invalid conditonal: " + cond, cond.getLoc());
    }
  }

  private static class CompileConjunction implements ICompileCondition {

    @Override
    public void handleCond(Apply cond, Sense sense, LabelNode elLabel,
                           CodeContext ccxt) {
      IAbstract lhs = cond.getArg(0);
      IAbstract rhs = cond.getArg(1);
      MethodNode mtd = ccxt.getMtd();

      switch (sense) {
        case jmpOnFail:
          compileCond(lhs, sense, elLabel, ccxt);
          compileCond(rhs, sense, elLabel, ccxt);
          break;
        case jmpOnOk: {
          LabelNode nxLabel = new LabelNode();
          compileCond(lhs, Sense.jmpOnFail, nxLabel, ccxt);
          compileCond(rhs, sense, elLabel, ccxt);
          Utils.jumpTarget(mtd.instructions, nxLabel);
        }
      }

    }
  }

  private static class CompileDisjunction implements ICompileCondition {
    CompileDisjunction() {
    }

    @Override
    public void handleCond(Apply cond, Sense sense, LabelNode elLabel,
                           CodeContext ccxt) {
      IAbstract lhs = cond.getArg(0);
      IAbstract rhs = cond.getArg(1);
      MethodNode mtd = ccxt.getMtd();

      switch (sense) {
        case jmpOnFail: {
          LabelNode ok = new LabelNode();
          compileCond(lhs, sense.negate(), ok, ccxt);
          compileCond(rhs, sense, elLabel, ccxt);
          mtd.instructions.add(ok);
          break;
        }
        case jmpOnOk:
          compileCond(lhs, sense, elLabel, ccxt);
          compileCond(rhs, sense, elLabel, ccxt);
      }
    }
  }

  private static class CompileNegation implements ICompileCondition {
    CompileNegation() {
    }

    @Override
    public void handleCond(Apply cond, Sense sense, LabelNode elLabel,
                           CodeContext ccxt) {
      IAbstract rhs = cond.getArg(0);

      compileCond(rhs, sense.negate(), elLabel, ccxt);
    }
  }

  private static class CompileConditional implements ICompileCondition {

    @Override
    public void handleCond(Apply cond, Sense sense, LabelNode elLabel,
                           CodeContext ccxt) {
      IAbstract test = CafeSyntax.conditionalTest(cond);
      IAbstract lhs = CafeSyntax.conditionalThen(cond);
      IAbstract rhs = CafeSyntax.conditionalElse(cond);

      MethodNode mtd = ccxt.getMtd();
      InsnList ins = mtd.instructions;

      switch (sense) {
        case jmpOnFail: {
          LabelNode ok = new LabelNode();
          LabelNode other = new LabelNode();

          compileCond(test, sense, other, ccxt);

          compileCond(lhs, sense.negate(), ok, ccxt);

          ins.add(new JumpInsnNode(Opcodes.GOTO, elLabel));
          Utils.jumpTarget(ins, other);

          compileCond(rhs, sense, elLabel, ccxt);

          Utils.jumpTarget(ins, ok);
          break;
        }
        case jmpOnOk: {
          LabelNode fail = new LabelNode();
          LabelNode other = new LabelNode();

          compileCond(test, Sense.jmpOnFail, other, ccxt);

          compileCond(lhs, sense, elLabel, ccxt);

          ins.add(new JumpInsnNode(Opcodes.GOTO, fail));
          Utils.jumpTarget(ins, other);
          compileCond(rhs, sense, elLabel, ccxt);

          Utils.jumpTarget(ins, fail);
        }
      }

    }
  }

  private static class CompileMatch implements ICompileCondition {
    CompileMatch() {
    }

    @Override
    public void handleCond(Apply cond, Sense sense, LabelNode elLabel, CodeContext ccxt) {
      assert CafeSyntax.isMatch(cond);

      MethodNode mtd = ccxt.getMtd();
      LabelNode exLabel = new LabelNode();
      IContinuation succ = new JumpCont(sense == Sense.jmpOnOk ? elLabel : exLabel);
      IContinuation fail = new JumpCont(sense == Sense.jmpOnOk ? exLabel : elLabel);
      CafeDictionary dict = ccxt.getDict();
      CafeDictionary outer = ccxt.getOuter();

      Expressions.compileExp(CafeSyntax.matchExp(cond),  new PatternCont(CafeSyntax.matchPtn(cond), dict, outer, AccessMode.readOnly, mtd, exLabel,
              succ, fail),
          ccxt);
      Utils.jumpTarget(mtd.instructions, exLabel);
    }
  }
}
