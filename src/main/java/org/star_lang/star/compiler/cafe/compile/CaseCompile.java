package org.star_lang.star.compiler.cafe.compile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.star_lang.star.code.repository.CodeCatalog;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.ast.Apply;
import org.star_lang.star.compiler.ast.BooleanLiteral;
import org.star_lang.star.compiler.ast.CharLiteral;
import org.star_lang.star.compiler.ast.FloatLiteral;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.ast.IntegerLiteral;
import org.star_lang.star.compiler.ast.Literal;
import org.star_lang.star.compiler.ast.LongLiteral;
import org.star_lang.star.compiler.ast.StringLiteral;
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

import com.starview.platform.data.IList;
import com.starview.platform.data.IValue;
import com.starview.platform.data.type.Location;
import com.starview.platform.data.type.TypeDescription;

/**
 * 
 * Copyright (C) 2013 Starview Inc
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
public class CaseCompile
{
  public interface ICaseCompile
  {
    ISpec compile(IAbstract term, CafeDictionary dict, IContinuation cont);
  }

  public static ISpec compileSwitch(Location loc, VarInfo var, IList rules, IAbstract deflt, CafeDictionary dict,
      CafeDictionary outer, ErrorReport errors, ICaseCompile handler, IContinuation cont, CodeContext ccxt)
  {
    switch (var.getKind()) {
    case rawBool:
      return booleanCases(loc, var, rules, deflt, dict, errors, handler, cont, ccxt);
    case rawChar:
    case rawInt:
    case rawLong:
    case rawFloat:
    case rawBinary:
    case rawString:
    case rawDecimal:
      return scalarCases(loc, var, rules, deflt, dict, errors, handler, cont, ccxt);
    case general:
      if (TypeUtils.isRawStringType(var.getType()))
        return scalarCases(loc, var, rules, deflt, dict, errors, handler, cont, ccxt);
      else
        return constructorCases(loc, var, rules, deflt, dict, outer, errors, cont, handler, ccxt);
    default:
      errors.reportError("invalid case for doing cases analysis", loc);
      return SrcSpec.prcSrc;
    }
  }

  private static ISpec constructorCases(Location loc, VarInfo var, IList rules, IAbstract deflt, CafeDictionary dict,
      CafeDictionary outer, ErrorReport errors, IContinuation cont, ICaseCompile handler, CodeContext ccxt)
  {
    LabelNode exitLbl = new LabelNode();
    if (!cont.isJump())
      cont = new ComboCont(cont, new JumpCont(exitLbl));

    MethodNode mtd = ccxt.getMtd();
    HWM hwm = ccxt.getMtdHwm();
    InsnList ins = mtd.instructions;

    if (TypeUtils.isTupleType(var.getType())) {
      assert rules.size() == 1;
      IAbstract rl = (IAbstract) rules.getCell(0);
      LabelNode endLabel = new LabelNode();
      LabelNode nxLabel = new LabelNode();
      LabelNode defltLbl = new LabelNode();

      CafeDictionary caseDict = dict.fork();

      int mark = hwm.bump(1);
      var.loadValue(mtd, hwm, dict);

      Patterns.tuplePtn(CafeSyntax.caseRulePtn(rl), AccessMode.readOnly, caseDict, outer, endLabel, errors,
          new NamePtn(), new JumpCont(nxLabel), new JumpCont(defltLbl), ccxt);

      hwm.reset(mark);
      Utils.jumpTarget(ins, nxLabel);
      ISpec caseSpec = handler.compile(CafeSyntax.caseRuleBody(rl), caseDict, cont);

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
        caseSpec = handler.compile(deflt, dict, cont);

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

            int mark = hwm.bump(1);
            LabelNode endLabel = new LabelNode();
            LabelNode nxLabel = new LabelNode();
            Patterns.constructorPtnArgs(var, lbl, CafeSyntax.constructorArgs(ptn), desc, caseDict, outer,
                AccessMode.readOnly, endLabel, errors, new NamePtn(), defCon, new JumpCont(nxLabel), ccxt);
            hwm.reset(mark);
            Utils.jumpTarget(ins, nxLabel);
            caseSpec = handler.compile(Abstract.getArg(rl, 1), caseDict, cont);

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
          caseSpec = handler.compile(deflt, dict, cont);

        Utils.jumpTarget(ins, exitLbl);

        assert caseSpec != null;
        return caseSpec;
      }
    }
    return SrcSpec.prcSrc;
  }

  // Deal with boolean analysis
  // Deal with a case analysis of scalars

  private static ISpec booleanCases(Location loc, VarInfo var, IList rules, IAbstract deflt, CafeDictionary dict,
      ErrorReport errors, ICaseCompile handler, IContinuation cont, CodeContext ccxt)
  {
    IAbstract trueCase = null;
    IAbstract falseCase = deflt;

    MethodNode mtd = ccxt.getMtd();
    CodeCatalog bldCat = ccxt.getBldCat();
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

    ISpec trType = handler.compile(trueCase, dict, cont);
    ins.add(elLabel);
    ISpec elType = handler.compile(falseCase, dict, cont);

    Expressions.checkType(trType, elType, mtd, dict, hwm, loc, errors, bldCat);
    return trType;
  }

  // Deal with a case analysis of scalars
  private static ISpec scalarCases(Location loc, VarInfo var, IList rules, IAbstract deflt, CafeDictionary dict,
      ErrorReport errors, ICaseCompile handler, IContinuation cont, CodeContext ccxt)
  {
    Map<Integer, Pair<LabelNode, List<Pair<Literal, IAbstract>>>> cases = new TreeMap<Integer, Pair<LabelNode, List<Pair<Literal, IAbstract>>>>();

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
        List<Pair<Literal, IAbstract>> entries = new ArrayList<Pair<Literal, IAbstract>>();
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
    case rawChar:
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
        case rawChar: {
          var.loadValue(mtd, hwm, dict);
          hwm.bump(1);
          ins.add(new LdcInsnNode(((CharLiteral) cse.left()).getLit()));
          ins.add(new JumpInsnNode(Opcodes.IF_ICMPNE, test));
          handler.compile(cse.right(), dict, reconcile);
          break;
        }
        case rawInt: {
          var.loadValue(mtd, hwm, dict);
          hwm.bump(1);
          ins.add(new LdcInsnNode(((IntegerLiteral) cse.left()).getLit()));
          ins.add(new JumpInsnNode(Opcodes.IF_ICMPNE, test));
          handler.compile(cse.right(), dict, reconcile);
          break;
        }
        case rawLong: {
          var.loadValue(mtd, hwm, dict);
          hwm.bump(2);
          ins.add(new LdcInsnNode(((LongLiteral) cse.left()).getLit()));
          ins.add(new InsnNode(Opcodes.LCMP));
          ins.add(new JumpInsnNode(Opcodes.IFNE, test));
          ;
          handler.compile(cse.right(), dict, reconcile);
          break;
        }
        case rawFloat: {
          hwm.bump(2);
          var.loadValue(mtd, hwm, dict);
          ins.add(new LdcInsnNode(((FloatLiteral) cse.left()).getLit()));
          ins.add(new InsnNode(Opcodes.DCMPG));
          ins.add(new JumpInsnNode(Opcodes.IFNE, test));
          handler.compile(cse.right(), dict, reconcile);
          break;
        }
        default:
        case rawString: {
          hwm.bump(1);
          var.loadValue(mtd, hwm, dict);
          ins.add(new LdcInsnNode(((StringLiteral) cse.left()).getLit()));
          ins.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, Types.JAVA_OBJECT_TYPE, "equals", Types.EQUALS_SIG));
          ins.add(new JumpInsnNode(Opcodes.IFEQ, test));
          handler.compile(cse.right(), dict, reconcile);
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
      handler.compile(deflt, dict, reconcile);

    Utils.jumpTarget(ins, exitLbl);
    return reconcile.getSpec();
  }
}
