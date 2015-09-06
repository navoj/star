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

/**
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * <p>
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA
 *
 * @author fgm
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

  public static void compileCond(IAbstract cond, Sense sense, LabelNode elLabel,
                                 CafeDictionary dict, CafeDictionary outer, String inFunction, CodeContext ccxt) {
    if (cond instanceof Apply) {
      ICompileCondition handler = handlers.get(((Apply) cond).getOp());
      if (handler != null) {
        handler.handleCond((Apply) cond, sense, elLabel, dict, outer, inFunction, ccxt);
        return;
      }
    }

    IContinuation cont = new BranchCont(sense, elLabel, dict);

    Expressions.compileExp(cond, dict, outer, inFunction, cont, ccxt);
  }

  private static class CompileTruthValue implements ICompileCondition {

    @Override
    public void handleCond(Apply cond, Sense sense, LabelNode elLabel, CafeDictionary dict,
                           CafeDictionary outer, String inFunction, CodeContext ccxt) {
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
    public void handleCond(Apply cond, Sense sense, LabelNode elLabel, CafeDictionary dict,
                           CafeDictionary outer, String inFunction, CodeContext ccxt) {
      IAbstract lhs = cond.getArg(0);
      IAbstract rhs = cond.getArg(1);
      MethodNode mtd = ccxt.getMtd();

      switch (sense) {
        case jmpOnFail:
          compileCond(lhs, sense, elLabel, dict, outer, inFunction, ccxt);
          compileCond(rhs, sense, elLabel, dict, outer, inFunction, ccxt);
          break;
        case jmpOnOk: {
          LabelNode nxLabel = new LabelNode();
          compileCond(lhs, Sense.jmpOnFail, nxLabel, dict, outer, inFunction, ccxt);
          compileCond(rhs, sense, elLabel, dict, outer, inFunction, ccxt);
          Utils.jumpTarget(mtd.instructions, nxLabel);
        }
      }

    }
  }

  private static class CompileDisjunction implements ICompileCondition {
    CompileDisjunction() {
    }

    @Override
    public void handleCond(Apply cond, Sense sense, LabelNode elLabel, CafeDictionary dict,
                           CafeDictionary outer, String inFunction, CodeContext ccxt) {
      IAbstract lhs = cond.getArg(0);
      IAbstract rhs = cond.getArg(1);
      MethodNode mtd = ccxt.getMtd();

      switch (sense) {
        case jmpOnFail: {
          LabelNode ok = new LabelNode();
          compileCond(lhs, sense.negate(), ok, dict, outer, inFunction, ccxt);
          compileCond(rhs, sense, elLabel, dict, outer, inFunction, ccxt);
          mtd.instructions.add(ok);
          break;
        }
        case jmpOnOk:
          compileCond(lhs, sense, elLabel, dict, outer, inFunction, ccxt);
          compileCond(rhs, sense, elLabel, dict, outer, inFunction, ccxt);
      }
    }
  }

  private static class CompileNegation implements ICompileCondition {
    CompileNegation() {
    }

    @Override
    public void handleCond(Apply cond, Sense sense, LabelNode elLabel, CafeDictionary dict,
                           CafeDictionary outer, String inFunction, CodeContext ccxt) {
      IAbstract rhs = cond.getArg(0);

      compileCond(rhs, sense.negate(), elLabel, dict, outer, inFunction, ccxt);
    }
  }

  private static class CompileConditional implements ICompileCondition {

    @Override
    public void handleCond(Apply cond, Sense sense, LabelNode elLabel, CafeDictionary dict,
                           CafeDictionary outer, String inFunction, CodeContext ccxt) {
      IAbstract test = CafeSyntax.conditionalTest(cond);
      IAbstract lhs = CafeSyntax.conditionalThen(cond);
      IAbstract rhs = CafeSyntax.conditionalElse(cond);

      MethodNode mtd = ccxt.getMtd();
      InsnList ins = mtd.instructions;

      switch (sense) {
        case jmpOnFail: {
          LabelNode ok = new LabelNode();
          LabelNode other = new LabelNode();

          compileCond(test, sense, other, dict, outer, inFunction, ccxt);

          compileCond(lhs, sense.negate(), ok, dict, outer, inFunction, ccxt);

          ins.add(new JumpInsnNode(Opcodes.GOTO, elLabel));
          Utils.jumpTarget(ins, other);

          compileCond(rhs, sense, elLabel, dict, outer, inFunction, ccxt);

          Utils.jumpTarget(ins, ok);
          break;
        }
        case jmpOnOk: {
          LabelNode fail = new LabelNode();
          LabelNode other = new LabelNode();

          compileCond(test, Sense.jmpOnFail, other, dict, outer, inFunction, ccxt);

          compileCond(lhs, sense, elLabel, dict, outer, inFunction, ccxt);

          ins.add(new JumpInsnNode(Opcodes.GOTO, fail));
          Utils.jumpTarget(ins, other);
          compileCond(rhs, sense, elLabel, dict, outer, inFunction, ccxt);

          Utils.jumpTarget(ins, fail);
        }
      }

    }
  }

  private static class CompileMatch implements ICompileCondition {
    CompileMatch() {
    }

    @Override
    public void handleCond(Apply cond, Sense sense, LabelNode elLabel, CafeDictionary dict,
                           CafeDictionary outer, String inFunction, CodeContext ccxt) {
      assert CafeSyntax.isMatch(cond);

      MethodNode mtd = ccxt.getMtd();
      LabelNode exLabel = new LabelNode();
      IContinuation succ = new JumpCont(sense == Sense.jmpOnOk ? elLabel : exLabel);
      IContinuation fail = new JumpCont(sense == Sense.jmpOnOk ? exLabel : elLabel);

      Expressions.compileExp(CafeSyntax.matchExp(cond), dict, outer, inFunction, new PatternCont(CafeSyntax.matchPtn(cond), dict, outer, AccessMode.readOnly, mtd, exLabel,
              succ, fail),
          ccxt);
      Utils.jumpTarget(mtd.instructions, exLabel);
    }
  }
}
