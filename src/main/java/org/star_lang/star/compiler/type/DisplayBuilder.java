package org.star_lang.star.compiler.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.ast.Apply;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.ast.IntegerLiteral;
import org.star_lang.star.compiler.ast.Name;
import org.star_lang.star.compiler.ast.StringLiteral;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.ComboIterable;
import org.star_lang.star.compiler.util.FixedList;
import org.star_lang.star.compiler.util.GenSym;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.operators.string.runtime.DisplayTerm;
import org.star_lang.star.operators.string.runtime.Number2String.Decimal2String;
import org.star_lang.star.operators.string.runtime.Number2String.Float2String;
import org.star_lang.star.operators.string.runtime.Number2String.Integer2String;
import org.star_lang.star.operators.string.runtime.Number2String.Long2String;

/**
 * This class is focused on implementing the pp contract -- if possible -- for a given type
 * description
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
public class DisplayBuilder
{
  private static boolean checkThetaForImplementation(String tpLabel, String contract, Iterable<IAbstract> theta,
      Dictionary dict)
  {
    if (TypeUtils.implementsContract(tpLabel, contract, dict))
      return true;
    else
      for (IAbstract stmt : theta) {
        if (CompilerUtils.isImplementationStmt(stmt)) {
          IAbstract con = CompilerUtils.implementedContract(stmt);
          if (Abstract.isName(con, contract)) {
            if (tpLabel != null && tpLabel.equals(CompilerUtils.implementationContractTypeName(stmt)))
              return true;
          }
        }
      }
    return false;
  }

  public static Iterable<IAbstract> checkForDisplay(Iterable<IAbstract> theta, Dictionary dict)
  {
    Map<String, IAbstract> printers = new HashMap<>();
    for (IAbstract stmt : theta) {
      Visibility visibility = CompilerUtils.privacy(stmt);
      stmt = CompilerUtils.dePrivatize(stmt);

      if (CompilerUtils.isTypeAlias(stmt))
        lookForAnonTypes(CompilerUtils.typeAliasAlias(stmt), dict, theta, printers);
      else if (CompilerUtils.isTypeDefn(stmt) && !CompilerUtils.isTypeAlias(stmt) && !checkExistentials(stmt)) {
        String tpLabel = CompilerUtils.typeDefnName(stmt);
        if (tpLabel != null && !checkThetaForImplementation(tpLabel, StandardNames.PPRINT, theta, dict)
            && !printers.containsKey(tpLabel)) {
          if (checkThetaForImplementation(tpLabel, StandardNames.SEQUENCE, theta, dict)) {
            IAbstract pp = seqPPimplementation(stmt, dict);
            if (pp != null)
              printers.put(tpLabel, CompilerUtils.privateStmt(stmt.getLoc(), visibility, pp));
          } else {
            IAbstract pp = ppImplementation(stmt, dict);
            if (pp != null)
              printers.put(tpLabel, CompilerUtils.privateStmt(stmt.getLoc(), visibility, pp));
          }
        }

        IAbstract specs = CompilerUtils.typeDefnConstructors(stmt);
        if (Abstract.isBinary(specs, StandardNames.WHERE))
          specs = Abstract.binaryLhs(specs);

        for (IAbstract term : CompilerUtils.unWrap(specs, StandardNames.OR)) {
          if (Abstract.isBinary(term, StandardNames.WHERE))
            term = Abstract.binaryLhs(term);
          if (CompilerUtils.isBraceTerm(term))
            checkTypeAnnotations(CompilerUtils.contentsOfRecord(term), dict, printers, theta);
          else if (Abstract.isRoundTerm(term))
            for (IValue tp : Abstract.roundTermArgs(term))
              if (!CompilerUtils.isTypeVar((IAbstract) tp))
                lookForAnonTypes((IAbstract) tp, dict, theta, printers);
        }
      }
    }
    checkTypeAnnotations(theta, dict, printers, theta);

    if (!printers.isEmpty())
      return new ComboIterable<IAbstract>(theta, printers.values());
    else
      return theta;
  }

  private static void lookForAnonTypes(IAbstract tp, Dictionary dict, Iterable<IAbstract> theta,
      Map<String, IAbstract> printers)
  {
    if (Abstract.isBinary(tp, StandardNames.OF)) {
      IAbstract tpArgs = Abstract.binaryRhs(tp);

      if (Abstract.isTupleTerm(tpArgs)) {
        for (IValue el : ((Apply) tpArgs).getArgs())
          lookForAnonTypes((IAbstract) el, dict, theta, printers);
      } else
        lookForAnonTypes(tpArgs, dict, theta, printers);
    } else if (Abstract.isBinary(tp, StandardNames.UNI_TILDA)
        && Abstract.isUnary(Abstract.binaryLhs(tp), StandardNames.TYPEVAR))
      lookForAnonTypes(Abstract.binaryRhs(tp), dict, theta, printers);
    else if (Abstract.isBinary(tp, StandardNames.FUN_ARROW)) {
      IAbstract arg = Abstract.binaryLhs(tp);

      if (Abstract.isTupleTerm(arg)) {
        for (IValue el : Abstract.tupleArgs(arg))
          lookForAnonTypes((IAbstract) el, dict, theta, printers);
      }

      lookForAnonTypes(Abstract.binaryRhs(tp), dict, theta, printers);
    } else if (Abstract.isRoundTerm(tp, StandardNames.ACTION_TYPE)) {
      for (IValue el : Abstract.getArgs(tp))
        lookForAnonTypes((IAbstract) el, dict, theta, printers);
    } else if (Abstract.isBinary(tp, StandardNames.PTN_TYPE)) {
      IAbstract resArg = Abstract.binaryLhs(tp);

      if (Abstract.isTupleTerm(resArg)) {
        for (IValue el : Abstract.tupleArgs(resArg))
          lookForAnonTypes((IAbstract) el, dict, theta, printers);
      }
      lookForAnonTypes(Abstract.binaryRhs(tp), dict, theta, printers);
    } else if (Abstract.isParenTerm(tp))
      lookForAnonTypes(Abstract.deParen(tp), dict, theta, printers);
    else if (Abstract.isTupleTerm(tp)) {
      for (IValue el : Abstract.tupleArgs(tp))
        lookForAnonTypes((IAbstract) el, dict, theta, printers);

      if (!checkThetaForImplementation(CompilerUtils.typeLabel(tp), StandardNames.PPRINT, theta, dict))
        buildTupleDisplay(tp, printers);
    } else if (CompilerUtils.isInterfaceType(tp)) {
      for (IAbstract spec : CompilerUtils.unWrap(CompilerUtils.blockContent(tp), StandardNames.TERM)) {
        if (CompilerUtils.isTypeAnnotation(spec))
          lookForAnonTypes(CompilerUtils.typeAnnotation(spec), dict, theta, printers);
      }
      if (!checkThetaForImplementation(CompilerUtils.anonRecordTypeLabel(tp), StandardNames.PPRINT, theta, dict))
        buildRecordDisplay(tp, printers);
    }
  }

  private static void buildRecordDisplay(IAbstract tp, Map<String, IAbstract> printers)
  {
    assert CompilerUtils.isAnonAggConLiteral(tp);

    Location loc = tp.getLoc();
    String label = CompilerUtils.anonRecordTypeLabel(tp) + "$display";

    IAbstract eqn = CompilerUtils.function(loc, displayRecordContents(loc, label, tp, "", CompilerUtils
        .blockContent(tp)));

    IAbstract constraint = null;
    List<IAbstract> fieldTypes = new ArrayList<>();
    List<IAbstract> tVars = new ArrayList<>();
    for (IAbstract spec : CompilerUtils.unWrap(CompilerUtils.blockContent(tp), StandardNames.TERM)) {
      if (CompilerUtils.isTypeAnnotation(spec)) {
        IAbstract field = CompilerUtils.typeAnnotatedTerm(spec);
        IAbstract tV = CompilerUtils.typeVar(loc, new Name(loc, GenSym.genSym(Abstract.getId(field))));
        tVars.add(tV);
        IAbstract contract = Abstract.binary(loc, StandardNames.OVER, new Name(loc, StandardNames.PPRINT), tV);
        if (constraint == null)
          constraint = contract;
        else
          constraint = Abstract.binary(loc, StandardNames.AND, constraint, contract);
        IAbstract fieldTp = CompilerUtils.typeAnnotationStmt(loc, field, tV);
        fieldTypes.add(fieldTp);
      }
    }
    IAbstract implType = CompilerUtils.blockTerm(loc, fieldTypes);
    if (constraint != null)
      implType = Abstract.binary(loc, StandardNames.WHERE, implType, constraint);

    if (eqn != null) {
      IAbstract defn = CompilerUtils.letExp(loc, CompilerUtils.blockTerm(loc, eqn), CompilerUtils.blockTerm(loc,
          CompilerUtils.equals(loc, new Name(loc, StandardNames.PPDISP), new Name(loc, label))));
      printers.put(CompilerUtils.typeLabel(tp), CompilerUtils.privateStmt(loc, CompilerUtils.implementationStmt(loc,
          CompilerUtils.universalType(loc, tVars, Abstract.binary(loc, StandardNames.OVER, new Name(loc,
              StandardNames.PPRINT), implType)), defn)));
    }
  }

  private static void buildTupleDisplay(IAbstract tp, Map<String, IAbstract> printers)
  {
    assert Abstract.isTupleTerm(tp);

    Location loc = tp.getLoc();
    String label = CompilerUtils.typeLabel(tp) + "$display";

    IAbstract eqn = CompilerUtils.function(loc, displayConstructor(loc, label, tp, tp));

    IAbstract constraint = null;
    List<IAbstract> fieldTypes = new ArrayList<>();
    List<IAbstract> tVars = new ArrayList<>();
    for (int ix = 0; ix < Abstract.tupleArity(tp); ix++) {
      IAbstract tV = CompilerUtils.typeVar(loc, new Name(loc, GenSym.genSym("_" + ix)));
      tVars.add(tV);
      IAbstract contract = Abstract.binary(loc, StandardNames.OVER, new Name(loc, StandardNames.PPRINT), tV);
      if (constraint == null)
        constraint = contract;
      else
        constraint = Abstract.binary(loc, StandardNames.AND, constraint, contract);

      fieldTypes.add(tV);
    }
    IAbstract implType = Abstract.parenTerm(loc, Abstract.tupleTerm(loc, fieldTypes));
    if (constraint != null)
      implType = Abstract.binary(loc, StandardNames.WHERE, implType, constraint);

    if (eqn != null) {
      IAbstract defn = CompilerUtils.letExp(loc, CompilerUtils.blockTerm(loc, eqn), CompilerUtils.blockTerm(loc,
          Abstract.binary(loc, StandardNames.EQUAL, new Name(loc, StandardNames.PPDISP), new Name(loc, label))));
      printers.put(CompilerUtils.typeLabel(tp), CompilerUtils.privateStmt(loc, CompilerUtils.implementationStmt(loc,
          CompilerUtils.universalType(loc, tVars, Abstract.binary(loc, StandardNames.OVER, new Name(loc,
              StandardNames.PPRINT), implType)), defn)));
    }
  }

  private static void checkTypeAnnotations(Iterable<IAbstract> env, Dictionary dict, Map<String, IAbstract> printers,
      Iterable<IAbstract> theta)
  {
    for (IAbstract stmt : env) {
      if (CompilerUtils.isTypeAnnotation(stmt)) {
        IAbstract type = CompilerUtils.typeAnnotation(stmt);

        lookForAnonTypes(type, dict, theta, printers);
      }
    }
  }

  private static boolean checkExistentials(IAbstract stmt)
  {
    IAbstract specs = CompilerUtils.typeDefnConstructors(stmt);

    if (Abstract.isBinary(specs, StandardNames.WHERE))
      specs = Abstract.binaryLhs(specs);

    for (IAbstract spec : CompilerUtils.unWrap(specs, StandardNames.OR)) {
      if (CompilerUtils.isBraceTerm(spec)) {
        for (IAbstract el : CompilerUtils.unWrap(CompilerUtils.braceArg(spec), StandardNames.TERM)) {
          if (CompilerUtils.isKindAnnotation(el)) {
            IAbstract con = CompilerUtils.kindAnnotatedConstraint(el);
            if (con == null || !checkConstraint(con, Abstract.getId(CompilerUtils.kindAnnotatedTerm(el))))
              return true;
          }
        }
      }
    }
    return false;
  }

  private static boolean checkConstraint(IAbstract cons, String lbl)
  {
    for (IAbstract con : CompilerUtils.unWrap(cons, StandardNames.AND)) {
      if (CompilerUtils.isContractSpec(con)) {
        IAbstract c = CompilerUtils.contractSpecName(con);
        if (Abstract.isIdentifier(c, StandardNames.PPRINT)
            && Abstract.isIdentifier(CompilerUtils.contractSpecType(con), lbl))
          return true;
      }
    }
    return false;
  }

  private static IAbstract ppImplementation(IAbstract stmt, Dictionary dict)
  {
    assert CompilerUtils.isTypeDefn(stmt);

    IAbstract specs = CompilerUtils.typeDefnConstructors(stmt);
    IAbstract tp = CompilerUtils.typeDefnType(stmt);
    String tplabel = CompilerUtils.typeLabel(tp);
    String label = tplabel + "$display";

    if (Abstract.isBinary(tp, StandardNames.WHERE))
      tp = Abstract.binaryLhs(tp);

    Location loc = stmt.getLoc();
    IAbstract pprint = new Name(loc, StandardNames.PPRINT);

    List<IAbstract> tVars = CompilerUtils.findTypeVarsInType(tp, dict, FixedList.create(tplabel));

    IAbstract prtType = CompilerUtils.functionType(loc, FixedList.create(tp), Abstract.name(loc, StandardNames.PP));
    Function<IAbstract, IAbstract> ppCon = (t) -> Abstract.binary(loc, StandardNames.OVER, pprint, t);

    List<IAbstract> requirements = EqualityBuilder.setupRequirements(stmt, ppCon, tVars);
    IAbstract implType = ppCon.apply(tp);

    if (!requirements.isEmpty()) {
      implType = Abstract.binary(loc, StandardNames.WHERE, implType, CompilerUtils.tupleUp(loc, StandardNames.AND,
          requirements));
      prtType = Abstract.binary(loc, StandardNames.WHERE, prtType, CompilerUtils.tupleUp(loc, StandardNames.AND,
          requirements));
    }

    prtType = CompilerUtils.universalType(loc, tVars, prtType);

    IAbstract functions = specFunctions(loc, label, specs, tp, prtType);

    if (functions != null) {
      IAbstract defn = CompilerUtils.letExp(loc, functions, CompilerUtils.blockTerm(loc, Abstract.binary(loc,
          StandardNames.EQUAL, new Name(loc, StandardNames.PPDISP), new Name(loc, label))));
      return CompilerUtils.implementationStmt(loc, CompilerUtils.universalType(loc, tVars, implType), defn);
    }
    return null;
  }

  private static IAbstract seqPPimplementation(IAbstract stmt, Dictionary dict)
  {
    assert CompilerUtils.isTypeDefn(stmt);

    Location loc = stmt.getLoc();
    IAbstract tp = CompilerUtils.typeDefnType(stmt);
    String tpLabel = CompilerUtils.typeLabel(tp);

    List<IAbstract> tVars = CompilerUtils.findTypeVarsInType(tp, dict, FixedList.create(tpLabel));
    IAbstract pprint = Abstract.name(loc, StandardNames.PPRINT);

    Function<IAbstract, IAbstract> ppCon = (t) -> Abstract.binary(loc, StandardNames.OVER, pprint, t);

    List<IAbstract> requirements = EqualityBuilder.setupRequirements(stmt, ppCon, tVars);
    IAbstract implType = ppCon.apply(tp);

    if (!requirements.isEmpty()) {
      implType = Abstract.binary(loc, StandardNames.WHERE, implType, CompilerUtils.tupleUp(loc, StandardNames.AND,
          requirements));
    }

    IAbstract arg = new Name(loc, GenSym.genSym("V$"));
    List<IAbstract> args = FixedList.create(arg);

    IAbstract eqn = CompilerUtils.function(loc, CompilerUtils.equation(loc, StandardNames.PPDISP, args, Abstract
        .binary(loc, "sequenceDisplay", new StringLiteral(loc, tpLabel), arg)));

    IAbstract defn = CompilerUtils.blockTerm(loc, eqn);
    return CompilerUtils.implementationStmt(loc, CompilerUtils.universalType(loc, tVars, implType), defn);
  }

  private static IAbstract specFunctions(Location loc, String label, IAbstract term, IAbstract type, IAbstract prtType)
  {
    List<IAbstract> eqns = new ArrayList<>();

    for (IAbstract el : CompilerUtils.unWrap(term, StandardNames.OR)) {
      IAbstract equalityEqn = specDisplay(loc, label, el, type);
      if (equalityEqn != null)
        eqns.add(equalityEqn);
    }

    return CompilerUtils.blockTerm(loc, CompilerUtils.typeAnnotationStmt(loc, new Name(loc, label), prtType),
        CompilerUtils.function(loc, eqns));
  }

  private static IAbstract specDisplay(Location loc, String label, IAbstract term, IAbstract type)
  {
    if (Abstract.isBinary(term, StandardNames.WHERE))
      return specDisplay(loc, label, Abstract.binaryLhs(term), type);
    else if (term instanceof Name)
      return enumDisplay(loc, label, term);
    else if (CompilerUtils.isBraceTerm(term))
      return displayRecord(loc, label, term, type);
    else
      return displayConstructor(loc, label, term, type);
  }

  /**
   * Construct
   * 
   * <pre>
   * <label>(<enum>) is ppStr("<enum>");
   * </pre>
   */
  private static IAbstract enumDisplay(Location loc, String label, IAbstract term)
  {
    assert term instanceof Name;
    List<IAbstract> args = new ArrayList<>();
    args.add(term);
    return CompilerUtils.equation(loc, label, args, str(loc, Abstract.newString(loc, ((Name) term).getId())));
  }

  /**
   * Construct display for a constructor
   * 
   * <pre>
   * <label>(<con>(L1,..,Ln)) is ppSequence(2,Cons of { 
   *   ppStr("<con>(");ppDisp(L1); ppStr(",");...;ppStr(",");ppDisp(Ln);ppStr(")")})
   * </pre>
   * 
   */
  private static IAbstract displayConstructor(Location loc, String label, IAbstract term, IAbstract type)
  {
    assert term instanceof Apply;
    List<IAbstract> lVars = new ArrayList<>();

    Apply apply = (Apply) term;
    String conLabel = apply.getOp();
    IAbstract disp = argSeq(apply, 0, Abstract.arity(term), lVars, type, label, cons(loc, str(loc, ")"), nil(loc)));
    disp = cons(loc, str(loc, Abstract.isTupleTerm(term) ? "(" : conLabel + "("), disp);

    List<IAbstract> args = new ArrayList<>();
    args.add(new Apply(loc, conLabel, lVars));

    return CompilerUtils.equation(loc, label, args, seq(loc, 2, disp));
  }

  private static IAbstract argSeq(Apply term, int ix, int arity, List<IAbstract> lVars, IAbstract type, String label,
      IAbstract tail)
  {
    Location loc = term.getLoc();
    if (ix == arity)
      return tail;
    else {
      IAbstract tp = term.getArg(ix);
      Name lV = new Name(loc, "$L" + ix);
      lVars.add(lV);
      IAbstract disp = display(loc, tp, type, lV, label);

      IAbstract rhs = argSeq(term, ix + 1, arity, lVars, type, label, tail);
      if (ix < arity - 1)
        rhs = cons(loc, str(loc, ", "), rhs);
      return cons(loc, disp, rhs);
    }
  }

  private static IAbstract cons(Location loc, IAbstract lhs, IAbstract rhs)
  {
    return Abstract.binary(loc, "cons", lhs, rhs);
  }

  private static IAbstract nil(Location loc)
  {
    return Abstract.zeroary(loc, "nil");
  }

  private static IAbstract str(Location loc, String str)
  {
    return Abstract.unary(loc, "ppStr", Abstract.newString(loc, str));
  }

  private static IAbstract str(Location loc, IAbstract str)
  {
    return Abstract.unary(loc, "ppStr", str);
  }

  private static IAbstract seq(Location loc, int indent, IAbstract str)
  {
    return Abstract.binary(loc, "ppSequence", new IntegerLiteral(loc, indent), str);
  }

  private static IAbstract nl(Location loc)
  {
    return Abstract.name(loc, "ppNl");
  }

  /**
   * Construct
   * 
   * <pre>
   * <label>(<con>{F1=L1;...;Fn=Ln}) is "<con>{"++"F1"=L1++";"++...++";"++"Fn="++Ln++"}"
   * </pre>
   */

  private static IAbstract displayRecord(Location loc, String label, IAbstract term, IAbstract type)
  {
    assert CompilerUtils.isBraceTerm(term) && Abstract.isName(CompilerUtils.braceLabel(term));

    String conLbl = Abstract.getId(CompilerUtils.braceLabel(term));
    if (TypeUtils.isAnonRecordLabel(conLbl))
      conLbl = "";
    return displayRecordContents(loc, label, type, conLbl, CompilerUtils.braceArg(term));
  }

  public static IAbstract displayRecordContents(Location loc, String label, IAbstract type, String conLabel,
      IAbstract content)
  {
    IAbstract dispLbl = Abstract.newString(loc, conLabel + "{");
    IAbstract suffix = Abstract.newString(loc, "}");
    IAbstract sep = str(loc, Abstract.newString(loc, "; "));

    int vNo = 0;
    ArrayList<IAbstract> lArgs = new ArrayList<>();
    IAbstract dispSequence = cons(loc, str(loc, suffix), nil(loc));

    for (IAbstract el : CompilerUtils.unWrap(content)) {
      if (CompilerUtils.isTypeAnnotation(el)) {
        if (vNo > 0)
          dispSequence = cons(loc, sep, cons(loc, nl(loc), dispSequence));

        IAbstract tp = CompilerUtils.typeAnnotation(el);
        IAbstract field = Abstract.deParen(CompilerUtils.typeAnnotatedTerm(el));
        String equals = "=";

        if (CompilerUtils.isRef(tp)) {
          equals = ":=";
          tp = CompilerUtils.referencedTerm(tp);
        }

        IAbstract fldPrefix = str(loc, Abstract.newString(loc, field instanceof Name ? ((Name) field).getId() + equals
            : field.toString() + equals));
        Name lV = new Name(loc, "$F" + vNo++);

        IAbstract disp = conLabel.equals("") ? Abstract.unary(loc, StandardNames.PPDISP, lV) : display(loc, tp, type,
            lV, label);

        IAbstract fldDisp = seq(loc, 2, cons(loc, fldPrefix, cons(loc, disp, nil(loc))));

        dispSequence = cons(loc, fldDisp, dispSequence);

        lArgs.add(CompilerUtils.equals(loc, field, lV));
      }
    }

    dispSequence = seq(loc, 2, cons(loc, str(loc, dispLbl), dispSequence));

    List<IAbstract> args = new ArrayList<>();
    if (!conLabel.equals(""))
      args.add(CompilerUtils.braceTerm(loc, conLabel, lArgs));
    else
      args.add(CompilerUtils.blockTerm(loc, lArgs));

    return CompilerUtils.equation(loc, label, args, dispSequence);
  }

  private static IAbstract display(Location loc, IAbstract attTp, IAbstract labelType, Name lV, String label)
  {
    if (attTp.equals(labelType))
      return Abstract.unary(loc, label, lV);
    else if (Abstract.isName(attTp, StandardTypes.RAW_INTEGER))
      return Abstract.unary(loc, StandardNames.PPSTRING, Abstract.unary(loc, StandardTypes.STRING, Abstract.unary(loc,
          Integer2String.name, lV)));
    else if (Abstract.isName(attTp, StandardTypes.RAW_LONG))
      return Abstract.unary(loc, StandardNames.PPSTRING, Abstract.unary(loc, StandardTypes.STRING, Abstract.unary(loc,
          Long2String.name, lV)));
    else if (Abstract.isName(attTp, StandardTypes.RAW_FLOAT))
      return Abstract.unary(loc, StandardNames.PPSTRING, Abstract.unary(loc, StandardTypes.STRING, Abstract.unary(loc,
          Float2String.name, lV)));
    else if (Abstract.isName(attTp, StandardTypes.RAW_DECIMAL))
      return Abstract.unary(loc, StandardNames.PPSTRING, Abstract.unary(loc, StandardTypes.STRING, Abstract.unary(loc,
          Decimal2String.name, lV)));
    else if (Abstract.isName(attTp, StandardTypes.RAW_STRING))
      return Abstract.unary(loc, StandardNames.PPSTRING, Abstract.unary(loc, StandardTypes.STRING, lV));
    else if (supportsDisplay(attTp))
      return Abstract.unary(loc, StandardNames.PPDISP, lV);
    else
      return str(loc, Abstract.unary(loc, DisplayTerm.name, lV));
  }

  private static boolean supportsDisplay(IAbstract type)
  {
    return !CompilerUtils.isProgramType(type) && !Abstract.isName(type, StandardNames.ANY);
  }

}
