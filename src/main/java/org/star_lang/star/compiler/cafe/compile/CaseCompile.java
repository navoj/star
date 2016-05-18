package org.star_lang.star.compiler.cafe.compile;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.ast.*;
import org.star_lang.star.compiler.cafe.CafeSyntax;
import org.star_lang.star.compiler.cafe.Names;
import org.star_lang.star.compiler.cafe.compile.Patterns.NamePtn;
import org.star_lang.star.compiler.cafe.compile.cont.ComboCont;
import org.star_lang.star.compiler.cafe.compile.cont.IContinuation;
import org.star_lang.star.compiler.cafe.compile.cont.JumpCont;
import org.star_lang.star.compiler.cafe.compile.cont.ReconcileCont;
import org.star_lang.star.compiler.cafe.type.ICafeConstructorSpecifier;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.compiler.util.Pair;
import org.star_lang.star.data.IList;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.TypeDescription;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;


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

public class CaseCompile {
  public interface ICaseCompile {
    ISpec compile(IAbstract term, IContinuation cont, CodeContext ccxt);
  }

  public static ISpec compileSwitch(Location loc, VarInfo var, IList rules, IAbstract deflt,
                                    ICaseCompile handler, IContinuation cont, CodeContext ccxt) {
    switch (var.getKind()) {
      case rawBool:
        return booleanCases(loc, var, rules, deflt, handler, cont, ccxt);
      case rawInt:
      case rawLong:
      case rawFloat:
      case rawBinary:
      case rawString:
      case rawDecimal:
        return scalarCases(loc, var, rules, deflt, handler, cont, ccxt);
      case general:
        if (TypeUtils.isRawStringType(var.getType()))
          return scalarCases(loc, var, rules, deflt, handler, cont, ccxt);
        else
          return constructorCases(loc, var, rules, deflt, cont, handler, ccxt);
      default:
        ccxt.getErrors().reportError("invalid case for doing cases analysis", loc);
        return SrcSpec.prcSrc;
    }
  }

  private static ISpec constructorCases(Location loc, VarInfo var, IList rules, IAbstract deflt,
                                        IContinuation cont, ICaseCompile handler, CodeContext ccxt) {
    LabelNode exitLbl = new LabelNode();
    if (!cont.isJump())
      cont = new ComboCont(cont, new JumpCont(exitLbl));

    MethodNode mtd = ccxt.getMtd();
    HWM hwm = ccxt.getMtdHwm();
    InsnList ins = mtd.instructions;
    CafeDictionary dict = ccxt.getDict();
    CafeDictionary outer = ccxt.getOuter();
    ErrorReport errors = ccxt.getErrors();

    if (TypeUtils.isTupleType(var.getType())) {
      assert rules.size() == 1;
      IAbstract rl = (IAbstract) rules.getCell(0);
      LabelNode endLabel = new LabelNode();
      LabelNode nxLabel = new LabelNode();
      LabelNode defltLbl = new LabelNode();

      CafeDictionary caseDict = dict.fork();
      CodeContext pCxt = ccxt.fork(caseDict, outer);

      int mark = hwm.bump(1);
      var.loadValue(mtd, hwm, dict);

      Patterns.tuplePtn(CafeSyntax.caseRulePtn(rl), AccessMode.readOnly, caseDict, outer, endLabel,
          new NamePtn(), new JumpCont(nxLabel), new JumpCont(defltLbl), pCxt);

      hwm.reset(mark);
      Utils.jumpTarget(ins, nxLabel);
      ISpec caseSpec = handler.compile(CafeSyntax.caseRuleBody(rl), cont, pCxt);

      dict.migrateFreeVars(caseDict);
      ins.add(endLabel);

      ins.add(defltLbl);

      if (deflt == null) {
        hwm.probe(3);
        ins.add(new TypeInsnNode(Opcodes.NEW, "java/lang/IllegalArgumentException"));
        ins.add(new InsnNode(Opcodes.DUP));
        ins.add(new LdcInsnNode(loc.toString()));
        ins.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/IllegalArgumentException", Types.INIT,
            "(Ljava/lang/String;)V"));
        ins.add(new InsnNode(Opcodes.ATHROW));
      } else
        caseSpec = handler.compile(deflt, cont, ccxt);

      Utils.jumpTarget(ins, exitLbl);

      return caseSpec;
    } else {
      TypeDescription desc = (TypeDescription) dict.findType(var.getType().typeLabel());
      if (desc == null)
        errors.reportError("type " + var.getType() + " not declared", loc);
      else {
        int maxIx = desc.maxConIx();
        ISpec caseSpec = null;

        LabelNode labels[] = new LabelNode[maxIx + 1];
        for (int ix = 0; ix < labels.length; ix++)
          labels[ix] = new LabelNode();
        boolean caseGen[] = new boolean[maxIx + 1];

        LabelNode defltLbl = new LabelNode();

        var.loadValue(mtd, hwm, dict);
        ins.add(new TypeInsnNode(Opcodes.CHECKCAST, Types.ICONSTRUCTOR));
        ins.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Types.ICONSTRUCTOR, Constructors.CONIX, "()I"));
        ins.add(new TableSwitchInsnNode(0, maxIx, defltLbl, labels));

        IContinuation defCon = new JumpCont(defltLbl);

        for (IValue r : rules) {
          IAbstract rl = (IAbstract) r;
          assert CafeSyntax.isCaseRule(rl) && CafeSyntax.isConstructor(CafeSyntax.caseRulePtn(rl));

          Apply ptn = (Apply) CafeSyntax.caseRulePtn(rl);
          String lbl = CafeSyntax.constructorOp(ptn);
          ICafeConstructorSpecifier con = (ICafeConstructorSpecifier) desc.getValueSpecifier(lbl);

          if (con != null) {
            int conIx = con.getConIx();
            LabelNode caseLbl = labels[conIx];
            caseGen[conIx] = true;
            ins.add(caseLbl);
            CafeDictionary caseDict = dict.fork();
            CodeContext pCxt = ccxt.fork(caseDict, outer);

            int mark = hwm.bump(1);
            LabelNode endLabel = new LabelNode();
            LabelNode nxLabel = new LabelNode();
            Patterns.constructorPtnArgs(var, lbl, CafeSyntax.constructorArgs(ptn), desc, caseDict, outer,
                AccessMode.readOnly, endLabel, new NamePtn(), defCon, new JumpCont(nxLabel), pCxt);
            hwm.reset(mark);
            Utils.jumpTarget(ins, nxLabel);
            caseSpec = handler.compile(Abstract.getArg(rl, 1), cont, pCxt);

            dict.migrateFreeVars(caseDict);
            ins.add(endLabel);
          } else
            errors.reportError(lbl + " not part of " + var.getType(), loc);
        }
        for (int ix = 0; ix < labels.length; ix++) {
          if (!caseGen[ix])
            ins.add(labels[ix]);
        }
        ins.add(defltLbl);

        if (deflt == null) {
          hwm.probe(3);
          ins.add(new TypeInsnNode(Opcodes.NEW, "java/lang/IllegalArgumentException"));
          ins.add(new InsnNode(Opcodes.DUP));
          ins.add(new LdcInsnNode(loc.toString()));
          ins.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/IllegalArgumentException", Types.INIT,
              "(Ljava/lang/String;)V"));
          ins.add(new InsnNode(Opcodes.ATHROW));
        } else
          caseSpec = handler.compile(deflt, cont, ccxt);

        Utils.jumpTarget(ins, exitLbl);

        assert caseSpec != null;
        return caseSpec;
      }
    }
    return SrcSpec.prcSrc;
  }

  // Deal with boolean analysis
  // Deal with a case analysis of scalars

  private static ISpec booleanCases(Location loc, VarInfo var, IList rules, IAbstract deflt,
                                    ICaseCompile handler, IContinuation cont, CodeContext ccxt) {
    IAbstract trueCase = null;
    IAbstract falseCase = deflt;
    ErrorReport errors = ccxt.getErrors();
    CafeDictionary dict = ccxt.getDict();

    MethodNode mtd = ccxt.getMtd();
    HWM hwm = ccxt.getMtdHwm();

    for (IValue r : rules) {
      assert Abstract.isBinary((IAbstract) r, Names.CASE);
      IAbstract ptn = Abstract.binaryLhs((IAbstract) r);
      if (ptn instanceof BooleanLiteral && ((BooleanLiteral) ptn).getLit())
        trueCase = Abstract.binaryRhs((IAbstract) r);
      else
        falseCase = Abstract.binaryRhs((IAbstract) r);
    }

    if (trueCase == null)
      errors.reportError("no case for true in case", loc);
    else if (falseCase == null)
      errors.reportError("no case for false in case", loc);

    LabelNode elLabel = new LabelNode();
    InsnList ins = mtd.instructions;

    var.loadValue(mtd, hwm, dict);

    ins.add(new JumpInsnNode(Opcodes.IFEQ, elLabel));

    ISpec trType = handler.compile(trueCase, cont, ccxt);
    ins.add(elLabel);
    ISpec elType = handler.compile(falseCase, cont, ccxt);

    Expressions.checkType(trType, elType, mtd, dict, hwm);
    return trType;
  }

  // Deal with a case analysis of scalars
  private static ISpec scalarCases(Location loc, VarInfo var, IList rules, IAbstract deflt,
                                   ICaseCompile handler, IContinuation cont, CodeContext ccxt) {
    Map<Integer, Pair<LabelNode, List<Pair<Literal, IAbstract>>>> cases = new TreeMap<>();
    ErrorReport errors = ccxt.getErrors();
    CafeDictionary dict = ccxt.getDict();

    MethodNode mtd = ccxt.getMtd();
    HWM hwm = ccxt.getMtdHwm();
    LabelNode exitLbl = new LabelNode();
    if (!cont.isJump())
      cont = new ComboCont(cont, new JumpCont(exitLbl));

    ReconcileCont reconcile = new ReconcileCont(cont);

    for (IValue r : rules) {
      IAbstract rl = (IAbstract) r;
      assert Abstract.isBinary(rl, Names.CASE) && Abstract.binaryLhs(rl) instanceof Literal;
      Literal lit = (Literal) Abstract.binaryLhs(rl);

      int hash = lit.getLit().hashCode();

      if (!cases.containsKey(hash)) {
        List<Pair<Literal, IAbstract>> entries = new ArrayList<>();
        cases.put(hash, Pair.pair(new LabelNode(), entries));
        entries.add(Pair.pair(lit, Abstract.binaryRhs(rl)));
      } else {
        Pair<LabelNode, List<Pair<Literal, IAbstract>>> entry = cases.get(hash);

        for (Pair<Literal, IAbstract> e : entry.right())
          if (e.left().getLit().equals(lit)) {
            Location leftLoc = e.left().getLoc();
            errors.reportError("duplicate case: `" + lit + "', other case at " + leftLoc, loc, leftLoc);
          }
        entry.right().add(Pair.pair(lit, Abstract.binaryRhs(rl)));
      }
    }

    int keys[] = new int[cases.size()];
    LabelNode labels[] = new LabelNode[cases.size()];
    int ix = 0;
    for (Entry<Integer, Pair<LabelNode, List<Pair<Literal, IAbstract>>>> entry : cases.entrySet()) {
      keys[ix] = entry.getKey();
      labels[ix] = entry.getValue().left();
      ix++;
    }

    LabelNode defltLbl = new LabelNode();
    InsnList ins = mtd.instructions;

    switch (var.getKind()) {
      case rawBool:
      case rawInt:
        var.loadValue(mtd, hwm, dict);
        break;
      case rawLong: // (int)(value ^ (value >>> 32))
        hwm.bump(5);
        var.loadValue(mtd, hwm, dict);
        ins.add(new InsnNode(Opcodes.DUP2));
        ins.add(new IntInsnNode(Opcodes.BIPUSH, 32));
        ins.add(new InsnNode(Opcodes.LUSHR));
        ins.add(new InsnNode(Opcodes.LXOR));
        ins.add(new InsnNode(Opcodes.L2I));
        hwm.bump(-4);
        break;
      case rawFloat:
        hwm.bump(4);
        ins.add(new TypeInsnNode(Opcodes.NEW, Types.JAVA_DOUBLE_TYPE));
        ins.add(new InsnNode(Opcodes.DUP));
        var.loadValue(mtd, hwm, dict);
        ins.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, Types.JAVA_DOUBLE_TYPE, Types.INIT, "(D)V"));
        ins.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, Types.JAVA_OBJECT_TYPE, "hashCode", "()I"));
        hwm.bump(-3);
        break;
      case rawString:
      case general:
        var.loadValue(mtd, hwm, dict);
        ins.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, Types.JAVA_OBJECT_TYPE, "hashCode", "()I"));
        hwm.bump(1);
        break;
      default:
        errors.reportError("(internal) do not know how to generate hash code of " + var, var.getLoc());
    }
    ins.add(new LookupSwitchInsnNode(defltLbl, keys, labels));

    for (Entry<Integer, Pair<LabelNode, List<Pair<Literal, IAbstract>>>> entry : cases.entrySet()) {
      List<Pair<Literal, IAbstract>> cseCases = entry.getValue().right();
      LabelNode test = entry.getValue().left();

      for (int lx = 0; lx < cseCases.size(); lx++) {
        ins.add(test);
        Pair<Literal, IAbstract> cse = cseCases.get(lx);
        test = lx < cseCases.size() - 1 ? new LabelNode() : defltLbl;

        int mark = hwm.getDepth();
        switch (var.getKind()) {
          case rawInt: {
            var.loadValue(mtd, hwm, dict);
            hwm.bump(1);
            ins.add(new LdcInsnNode(((IntegerLiteral) cse.left()).getLit()));
            ins.add(new JumpInsnNode(Opcodes.IF_ICMPNE, test));
            handler.compile(cse.right(), reconcile, ccxt);
            break;
          }
          case rawLong: {
            var.loadValue(mtd, hwm, dict);
            hwm.bump(2);
            ins.add(new LdcInsnNode(((LongLiteral) cse.left()).getLit()));
            ins.add(new InsnNode(Opcodes.LCMP));
            ins.add(new JumpInsnNode(Opcodes.IFNE, test));
            handler.compile(cse.right(), reconcile, ccxt);
            break;
          }
          case rawFloat: {
            hwm.bump(2);
            var.loadValue(mtd, hwm, dict);
            ins.add(new LdcInsnNode(((FloatLiteral) cse.left()).getLit()));
            ins.add(new InsnNode(Opcodes.DCMPG));
            ins.add(new JumpInsnNode(Opcodes.IFNE, test));
            handler.compile(cse.right(), reconcile, ccxt);
            break;
          }
          default:
          case rawString: {
            hwm.bump(1);
            var.loadValue(mtd, hwm, dict);
            ins.add(new LdcInsnNode(((StringLiteral) cse.left()).getLit()));
            ins.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, Types.JAVA_OBJECT_TYPE, "equals", Types.EQUALS_SIG));
            ins.add(new JumpInsnNode(Opcodes.IFEQ, test));
            handler.compile(cse.right(), reconcile, ccxt);
            break;
          }
        }
        hwm.reset(mark);
      }
    }

    ins.add(defltLbl);

    if (deflt == null) {
      hwm.probe(3);
      ins.add(new TypeInsnNode(Opcodes.NEW, "java/lang/IllegalArgumentException"));
      ins.add(new InsnNode(Opcodes.DUP));
      ins.add(new LdcInsnNode(loc.toString()));
      ins.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/IllegalArgumentException", Types.INIT,
          "(Ljava/lang/String;)V"));
      ins.add(new InsnNode(Opcodes.ATHROW));
    } else
      handler.compile(deflt, reconcile, ccxt);

    Utils.jumpTarget(ins, exitLbl);
    return reconcile.getSpec();
  }
}
