package org.star_lang.star.compiler.type;

import java.util.ArrayList;
import java.util.List;

import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.ast.Apply;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.ast.Name;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.FixedList;
import org.star_lang.star.operators.arith.FloatCompare.FltEQ;
import org.star_lang.star.operators.arith.LongCompare.LngEQ;
import org.star_lang.star.operators.arith.runtime.BignumCompare.BignumEQ;
import org.star_lang.star.operators.arith.runtime.IntCompare.IntEQ;
import org.star_lang.star.operators.general.runtime.GeneralEq;
import org.star_lang.star.operators.string.StringCompare;

import com.starview.platform.data.IValue;
import com.starview.platform.data.type.Location;
import com.starview.platform.data.type.StandardTypes;

/**
 * This class is focused on implementing the equality contract -- if possible -- for a given type
 * description
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
public class EqualityBuilder
{
  private static boolean checkThetaForEquality(String tpLabel, IAbstract theta)
  {
    for (IAbstract stmt : CompilerUtils.unWrap(theta)) {
      if (CompilerUtils.isImplementationStmt(stmt)) {
        IAbstract con = CompilerUtils.implementedContract(stmt);
        if (Abstract.isName(con, StandardNames.EQUALITY)) {
          if (tpLabel.equals(CompilerUtils.implementationContractTypeName(stmt)))
            return true;
        }
      }
    }
    return false;
  }

  public static IAbstract checkForEqualities(IAbstract theta, Dictionary dict)
  {
    List<IAbstract> equalities = new ArrayList<>();
    for (IAbstract stmt : CompilerUtils.unWrap(theta)) {
      Visibility visibility = CompilerUtils.privacy(stmt);
      stmt = CompilerUtils.dePrivatize(stmt);
      if (CompilerUtils.isTypeDefn(stmt) && !CompilerUtils.isTypeAlias(stmt)) {
        String tpLabel = CompilerUtils.typeDefnName(stmt);
        if (tpLabel != null && !checkThetaForEquality(tpLabel, theta) && !checkExistentials(stmt)) {
          IAbstract equality = equalityImplementation(stmt, dict);
          if (equality != null) {
            equalities.add(CompilerUtils.privateStmt(stmt.getLoc(), visibility, equality));
          }
        }
      }
    }
    for (IAbstract stmt : equalities)
      theta = Abstract.binary(stmt.getLoc(), StandardNames.TERM, stmt, theta);
    return theta;
  }

  public static IAbstract equalityImplementation(IAbstract stmt, Dictionary dict)
  {
    assert CompilerUtils.isTypeDefn(stmt);

    IAbstract specs = CompilerUtils.typeDefnConstructors(stmt);
    IAbstract tp = CompilerUtils.typeDefnType(stmt);
    String typeLabel = CompilerUtils.typeLabel(tp);
    String label = typeLabel + "$equal";

    IAbstract implTpSpec;
    List<IAbstract> constraints = findConstraints(stmt);

    Location loc = tp.getLoc();

    if (Abstract.isBinary(tp, StandardNames.WHERE)) {
      tp = Abstract.binaryLhs(tp);
    }

    IAbstract eqnType = CompilerUtils.functionType(loc, FixedList.create(tp, tp), Abstract.name(loc,
        StandardTypes.BOOLEAN));

    List<IAbstract> tVars = CompilerUtils.findTypeVarsInType(tp, dict, FixedList.create(typeLabel));
    IAbstract requirement = setupRequirement(loc, stmt, new Name(loc, StandardNames.EQUALITY), tVars);
    if (requirement != null) {
      implTpSpec = mergeRequirements(tp, requirement, constraints);

      eqnType = mergeRequirements(eqnType, requirement, constraints);
    } else
      implTpSpec = mergeRequirements(tp, constraints);

    eqnType = CompilerUtils.universalType(loc, tVars, eqnType);

    IAbstract equations = specEquations(loc, label, specs, tp, eqnType);

    if (equations != null) {
      // construct implementation equality of tp is let { <equations> } in
      // {= = <label>}
      IAbstract defn = CompilerUtils.letExp(loc, equations, CompilerUtils.blockTerm(loc, Abstract.binary(loc,
          StandardNames.EQUAL, new Name(loc, StandardNames.EQUAL), new Name(loc, label))));
      return CompilerUtils.implementationStmt(loc, CompilerUtils.universalType(loc, tVars, Abstract.binary(loc,
          StandardNames.OVER, new Name(loc, StandardNames.EQUALITY), implTpSpec)), defn);
    }
    return null;
  }

  // Not permitted to auto-implement equality for type with existentials inside
  private static boolean checkExistentials(IAbstract stmt)
  {
    IAbstract specs = CompilerUtils.typeDefnConstructors(stmt);

    if (Abstract.isBinary(specs, StandardNames.WHERE))
      specs = Abstract.binaryLhs(specs);
    for (IAbstract spec : CompilerUtils.unWrap(specs, StandardNames.OR)) {
      if (CompilerUtils.isBraceTerm(spec)) {
        for (IAbstract el : CompilerUtils.unWrap(CompilerUtils.braceArg(spec), StandardNames.TERM)) {
          if (CompilerUtils.isKindAnnotation(el))
            return true;
        }
      }
    }
    return false;
  }

  public static IAbstract mergeRequirements(IAbstract tp, IAbstract req, List<IAbstract> constraints)
  {
    Location loc = tp.getLoc();

    return Abstract.binary(loc, StandardNames.WHERE, tp, CompilerUtils.tupleUp(loc, StandardNames.ALSO, constraints,
        req));
  }

  public static IAbstract mergeRequirements(IAbstract tp, List<IAbstract> constraints)
  {
    Location loc = tp.getLoc();

    if (!constraints.isEmpty())
      return Abstract.binary(loc, StandardNames.WHERE, tp, CompilerUtils.tupleUp(loc, StandardNames.ALSO, constraints));
    else
      return tp;
  }

  public static IAbstract setupRequirement(Location loc, IAbstract stmt, IAbstract contract, List<IAbstract> tVars)
  {
    findRequirement(tVars, CompilerUtils.typeDefnType(stmt));
    for (IAbstract con : CompilerUtils.unWrap(CompilerUtils.typeDefnConstructors(stmt)))
      if (Abstract.isBinary(con, StandardNames.WHERE))
        findConstraintRequirements(tVars, Abstract.binaryRhs(con));

    if (!tVars.isEmpty()) {
      IAbstract req = null;
      for (IAbstract ref : tVars) {
        IAbstract contTerm = Abstract.binary(loc, StandardNames.OVER, contract, ref);
        if (req == null)
          req = contTerm;
        else
          req = Abstract.binary(loc, StandardNames.ALSO, contTerm, req);
      }
      return req;
    } else
      return null;
  }

  public static void findRequirement(List<IAbstract> requirements, IAbstract tpExp)
  {
    if (CompilerUtils.isTypeVar(tpExp)) {
      tpExp = CompilerUtils.typeVName(tpExp);
      if (!requirements.contains(tpExp))
        requirements.add(tpExp);
    } else if (Abstract.isBinary(tpExp, StandardNames.OF))
      findRequirement(requirements, Abstract.binaryRhs(tpExp));
    else if (Abstract.isParenTerm(tpExp))
      findRequirement(requirements, Abstract.deParen(tpExp));
    else if (Abstract.isTupleTerm(tpExp)) {
      for (IValue el : Abstract.tupleArgs(tpExp))
        findRequirement(requirements, (IAbstract) el);
    } else if (Abstract.isBinary(tpExp, StandardNames.WHERE)) {
      findRequirement(requirements, Abstract.binaryLhs(tpExp));
      findConstraintRequirements(requirements, Abstract.binaryRhs(tpExp));
    }
  }

  public static void findConstraintRequirements(List<IAbstract> requirements, IAbstract tpExp)
  {
    if (Abstract.isBinary(tpExp, StandardNames.OVER))
      findRequirement(requirements, Abstract.binaryRhs(tpExp));
    else if (CompilerUtils.isDerived(tpExp)) {
      findRequirement(requirements, CompilerUtils.derivedType(tpExp));
      findRequirement(requirements, CompilerUtils.dependsType(tpExp));
    } else if (CompilerUtils.isImplementsConstraint(tpExp)) {
      findRequirement(requirements, CompilerUtils.implementConType(tpExp));
      for (IAbstract el : CompilerUtils.unWrap(CompilerUtils.implementsConstraints(tpExp), StandardNames.TERM)) {
        if (CompilerUtils.isTypeAnnotation(el))
          findRequirement(requirements, CompilerUtils.typeAnnotation(el));
      }
    }
  }

  public static List<IAbstract> findConstraints(IAbstract stmt)
  {
    IAbstract tp = CompilerUtils.typeDefnType(stmt);
    List<IAbstract> cons = new ArrayList<>();

    if (Abstract.isBinary(tp, StandardNames.WHERE))
      for (IAbstract con : CompilerUtils.unWrap(Abstract.binaryRhs(tp), StandardNames.ALSO))
        cons.add(con);

    for (IAbstract con : CompilerUtils.unWrap(CompilerUtils.typeDefnConstructors(stmt)))
      if (Abstract.isBinary(con, StandardNames.WHERE))
        for (IAbstract c : CompilerUtils.unWrap(Abstract.binaryRhs(con), StandardNames.ALSO))
          cons.add(c);
    return cons;
  }

  private static IAbstract specEquations(Location loc, String label, IAbstract term, IAbstract type, IAbstract eqType)
  {
    List<IAbstract> eqns = new ArrayList<>();
    eqns.add(CompilerUtils.typeAnnotationStmt(loc, new Name(loc, label), eqType));
    if (Abstract.isBinary(term, StandardNames.WHERE))
      term = Abstract.binaryLhs(term);
    for (IAbstract el : CompilerUtils.unWrap(term, StandardNames.OR)) {
      IAbstract equalityEqn = specEquality(loc, label, el, type);
      if (equalityEqn != null)
        eqns.add(equalityEqn);
    }

    if (eqns.size() > 1)
      eqns.add(specDeflt(loc, label));
    return CompilerUtils.tupleUp(loc, StandardNames.TERM, eqns);
  }

  private static IAbstract specEquality(Location loc, String label, IAbstract term, IAbstract type)
  {
    if (term instanceof Name)
      return enumEquality(loc, label, term);
    else if (CompilerUtils.isBraceTerm(term))
      return recordEquality(loc, type, label, term);
    else
      return conEquality(loc, label, term, type);
  }

  private static IAbstract specDeflt(Location loc, String label)
  {
    List<IAbstract> args = new ArrayList<>();
    Name lV = new Name(loc, "L");
    Name rV = new Name(loc, "R");
    args.add(lV);
    args.add(rV);
    return CompilerUtils.defaultEquation(loc, label, args, Abstract.name(loc, StandardNames.FALSE));
  }

  /**
   * Construct
   * 
   * <pre>
   * <label>(<enum>,<enum>) is true;
   * </pre>
   */
  private static IAbstract enumEquality(Location loc, String label, IAbstract term)
  {
    assert term instanceof Name;
    List<IAbstract> args = new ArrayList<>();
    args.add(term);
    args.add(term);
    return CompilerUtils.equation(loc, label, args, Abstract.name(loc, StandardNames.TRUE));
  }

  /**
   * Construct
   * 
   * <pre>
   * <label>(<con>(L1,..,Ln),<con>(R1,..,Rn)) is L1=R1 and ... Ln=Rn
   * </pre>
   * 
   * @param type
   *          Type whose equality is being defined. Allows for recursion.
   */
  private static IAbstract conEquality(Location loc, String label, IAbstract term, IAbstract type)
  {
    assert term instanceof Apply;
    List<IAbstract> lVars = new ArrayList<>();
    List<IAbstract> rVars = new ArrayList<>();

    Apply apply = (Apply) term;
    String conLabel = apply.getOp();
    IAbstract cond = Abstract.name(loc, StandardNames.TRUE);

    for (int vNo = 0; vNo < Abstract.arity(term); vNo++) {
      IAbstract tp = apply.getArg(vNo);

      Name lV = new Name(loc, "$L" + vNo);
      Name rV = new Name(loc, "$R" + vNo);
      lVars.add(lV);
      rVars.add(rV);

      if (Abstract.isName(cond, StandardNames.TRUE))
        cond = equality(loc, tp, type, lV, rV, label);
      else
        cond = Abstract.binary(loc, StandardNames.AND, cond, equality(loc, tp, type, lV, rV, label));
    }

    List<IAbstract> args = new ArrayList<>();
    args.add(new Apply(loc, conLabel, lVars));
    args.add(new Apply(loc, conLabel, rVars));

    return CompilerUtils.equation(loc, label, args, cond);
  }

  /**
   * Construct
   * 
   * <pre>
   * <label>(<con>{F1=L1;...;Fn=Ln}, <con>{F1=R1;...;Fn=Rn}) is L1=R1 and ... and Ln=Rn
   * </pre>
   * 
   * @param type
   *          Type whose equality is being defined. Permits handling of recursive types.
   */

  private static IAbstract recordEquality(Location loc, IAbstract type, String label, IAbstract term)
  {
    assert CompilerUtils.isBraceTerm(term) && Abstract.isName(CompilerUtils.braceLabel(term));

    IAbstract conLabel = CompilerUtils.braceLabel(term);
    IAbstract cond = Abstract.name(loc, StandardNames.TRUE);
    int vNo = 0;
    IAbstract lArgs = null;
    IAbstract rArgs = null;

    for (IAbstract el : CompilerUtils.unWrap(CompilerUtils.braceArg(term), StandardNames.TERM)) {
      if (CompilerUtils.isTypeAnnotation(el)) {
        IAbstract tp = CompilerUtils.typeAnnotation(el);
        IAbstract field = CompilerUtils.typeAnnotatedTerm(el);
        Name lV = new Name(loc, "$L" + vNo);
        Name rV = new Name(loc, "$R" + vNo);

        vNo++;

        IAbstract lArg = CompilerUtils.equals(loc, field, lV);
        if (lArgs == null)
          lArgs = lArg;
        else
          lArgs = Abstract.binary(loc, StandardNames.TERM, lArgs, lArg);

        IAbstract rArg = CompilerUtils.equals(loc, field, rV);
        if (rArgs == null)
          rArgs = rArg;
        else
          rArgs = Abstract.binary(loc, StandardNames.TERM, rArgs, rArg);

        IAbstract eqTest = equality(loc, tp, type, lV, rV, label);

        if (Abstract.isName(cond, StandardNames.TRUE))
          cond = eqTest;
        else
          cond = Abstract.binary(loc, StandardNames.AND, cond, eqTest);
      }
    }

    List<IAbstract> args = new ArrayList<>();
    if (lArgs != null && rArgs != null) {
      args.add(CompilerUtils.braceTerm(loc, conLabel, lArgs));
      args.add(CompilerUtils.braceTerm(loc, conLabel, rArgs));
    } else {
      args.add(CompilerUtils.emptyBrace(loc, conLabel));
      args.add(CompilerUtils.emptyBrace(loc, conLabel));
    }

    return CompilerUtils.equation(loc, label, args, cond);
  }

  private static IAbstract equality(Location loc, IAbstract attTp, IAbstract type, Name lV, Name rV, String label)
  {
    if (CompilerUtils.isReference(attTp))
      attTp = CompilerUtils.referencedTerm(attTp);

    if (attTp.equals(type))
      return Abstract.binary(loc, label, lV, rV);
    else if (Abstract.isName(attTp, StandardTypes.RAW_INTEGER))
      return Abstract.binary(loc, IntEQ.name, lV, rV);
    else if (Abstract.isName(attTp, StandardTypes.RAW_LONG))
      return Abstract.binary(loc, LngEQ.name, lV, rV);
    else if (Abstract.isName(attTp, StandardTypes.RAW_FLOAT))
      return Abstract.binary(loc, FltEQ.name, lV, rV);
    else if (Abstract.isName(attTp, StandardTypes.RAW_DECIMAL))
      return Abstract.binary(loc, BignumEQ.name, lV, rV);
    else if (Abstract.isName(attTp, StandardTypes.RAW_STRING))
      return Abstract.binary(loc, StringCompare.STRING_EQ, lV, rV);
    else if (supportsEquality(attTp))
      return CompilerUtils.equals(loc, lV, rV);
    else
      return Abstract.binary(loc, GeneralEq.name, lV, rV);
  }

  private static boolean supportsEquality(IAbstract type)
  {
    return !CompilerUtils.isProgramType(type) && !Abstract.isName(type, StandardNames.ANY);
  }
}
