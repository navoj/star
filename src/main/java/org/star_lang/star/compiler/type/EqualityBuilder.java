package org.star_lang.star.compiler.type;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.ast.AApply;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.ast.Name;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.ComboIterable;
import org.star_lang.star.compiler.util.FixedList;
import org.star_lang.star.compiler.util.GenSym;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.operators.arith.FloatCompare.FltEQ;
import org.star_lang.star.operators.arith.LongCompare.LngEQ;
import org.star_lang.star.operators.arith.runtime.FloatUnary;
import org.star_lang.star.operators.arith.runtime.IntCompare.IntEQ;
import org.star_lang.star.operators.arith.runtime.IntUnary;
import org.star_lang.star.operators.arith.runtime.LongUnary;
import org.star_lang.star.operators.general.runtime.GeneralEq;
import org.star_lang.star.operators.misc.runtime.HashCode;
import org.star_lang.star.operators.string.runtime.StringCompare;
import org.star_lang.star.operators.string.runtime.StringOps;

/**
 * This class is focused on implementing the equality contract -- if possible -- for a given type
 * description
 * <p>
 * Copyright (c) 2015. Francis G. McCabe
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

public class EqualityBuilder {
  private static boolean checkThetaForEquality(String tpLabel, Iterable<IAbstract> theta) {
    for (IAbstract stmt : theta) {
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

  public static Iterable<IAbstract> checkForEqualities(Iterable<IAbstract> theta, Dictionary dict) {
    List<IAbstract> equalities = new ArrayList<>();
    for (IAbstract stmt : theta) {
      Visibility visibility = CompilerUtils.visibility(stmt);
      stmt = CompilerUtils.stripVisibility(stmt);
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

    if (!equalities.isEmpty())
      return new ComboIterable<>(theta, equalities);
    else
      return theta;
  }

  public static IAbstract equalityImplementation(IAbstract stmt, Dictionary dict) {
    assert CompilerUtils.isTypeDefn(stmt);

    IAbstract specs = CompilerUtils.typeDefnConstructors(stmt);
    IAbstract tp = CompilerUtils.typeDefnType(stmt);
    String typeLabel = CompilerUtils.typeLabel(tp);
    String equalLabel = typeLabel + "$equal";
    String hashLabel = typeLabel + "$hash";

    Location loc = tp.getLoc();
    IAbstract equality = Abstract.name(loc, StandardNames.EQUALITY);

    if (Abstract.isBinary(tp, StandardNames.WHERE))
      tp = Abstract.binaryLhs(tp);

    IAbstract eqnType = CompilerUtils.functionType(loc, FixedList.create(tp, tp), Abstract.name(loc,
        StandardTypes.BOOLEAN));
    IAbstract hashType = CompilerUtils.functionType(loc, FixedList.create(tp), Abstract.name(loc,
        StandardTypes.INTEGER));
    Function<IAbstract, IAbstract> eqCon = (t) -> Abstract.binary(loc, StandardNames.OVER, equality, t);

    List<IAbstract> tVars = CompilerUtils.findTypeVarsInType(tp, dict, FixedList.create(typeLabel));
    List<IAbstract> requirements = setupRequirements(stmt, eqCon, tVars);

    IAbstract implType = eqCon.apply(tp);

    if (!requirements.isEmpty()) {
      implType = Abstract.binary(loc, StandardNames.WHERE, implType, CompilerUtils.tupleUp(loc, StandardNames.AND,
          requirements));
      eqnType = Abstract.binary(loc, StandardNames.WHERE, eqnType, CompilerUtils.tupleUp(loc, StandardNames.AND,
          requirements));
      hashType = Abstract.binary(loc, StandardNames.WHERE, hashType, CompilerUtils.tupleUp(loc, StandardNames.AND,
          requirements));
    }

    eqnType = CompilerUtils.universalType(loc, tVars, eqnType);
    hashType = CompilerUtils.universalType(loc, tVars, hashType);

    List<IAbstract> equalityEquations = equalityEquations(loc, equalLabel, specs, tp, eqnType);
    List<IAbstract> hashEquations = hashEquations(loc, hashLabel, specs, tp, hashType);

    List<IAbstract> defs = new ArrayList<>();
    defs.addAll(equalityEquations);
    defs.addAll(hashEquations);

    if (!defs.isEmpty()) {
      // construct implementation equality of tp is let { <equations> } in
      // {= = <label>}
      IAbstract eqlDefn = Abstract.binary(loc, StandardNames.EQUAL, new Name(loc, StandardNames.EQUAL), new Name(loc, equalLabel));
      IAbstract hshDefn = Abstract.binary(loc, StandardNames.EQUAL, new Name(loc, StandardNames.HASHCODE), new Name(loc, hashLabel));

      IAbstract defn = CompilerUtils.letExp(loc, CompilerUtils.blockTerm(loc, defs), CompilerUtils.blockTerm(loc, eqlDefn, hshDefn));
      return CompilerUtils.implementationStmt(loc, CompilerUtils.universalType(loc, tVars, implType), defn);
    }
    return null;
  }

  // Not permitted to auto-implement equality for type with existentials inside
  private static boolean checkExistentials(IAbstract stmt) {
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

  public static IAbstract mergeRequirements(IAbstract tp, IAbstract req, List<IAbstract> constraints) {
    Location loc = tp.getLoc();

    return Abstract.binary(loc, StandardNames.WHERE, tp, CompilerUtils
        .tupleUp(loc, StandardNames.AND, constraints, req));
  }

  public static IAbstract mergeRequirements(IAbstract tp, List<IAbstract> constraints) {
    Location loc = tp.getLoc();

    if (!constraints.isEmpty())
      return Abstract.binary(loc, StandardNames.WHERE, tp, CompilerUtils.tupleUp(loc, StandardNames.AND, constraints));
    else
      return tp;
  }

  public static IAbstract setupRequirement(Location loc, IAbstract stmt, Function<IAbstract, IAbstract> over,
                                           List<IAbstract> tVars) {
    findRequirement(tVars, CompilerUtils.typeDefnType(stmt));
    for (IAbstract con : CompilerUtils.unWrap(CompilerUtils.typeDefnConstructors(stmt)))
      if (Abstract.isBinary(con, StandardNames.WHERE))
        findConstraintRequirements(tVars, Abstract.binaryRhs(con));

    if (!tVars.isEmpty()) {
      IAbstract req = null;
      for (IAbstract ref : tVars) {
        IAbstract contTerm = over.apply(ref);
        if (req == null)
          req = contTerm;
        else
          req = Abstract.binary(loc, StandardNames.AND, contTerm, req);
      }
      return req;
    } else
      return null;
  }

  public static List<IAbstract> setupRequirements(IAbstract stmt, Function<IAbstract, IAbstract> over,
                                                  List<IAbstract> tVars) {
    findRequirement(tVars, CompilerUtils.typeDefnType(stmt));
    List<IAbstract> reqs = new ArrayList<>();

    for (IAbstract con : CompilerUtils.unWrap(CompilerUtils.typeDefnConstructors(stmt)))
      if (Abstract.isBinary(con, StandardNames.WHERE))
        findConstraintRequirements(tVars, Abstract.binaryRhs(con));

    if (!tVars.isEmpty()) {
      for (IAbstract ref : tVars) {
        reqs.add(over.apply(ref));
      }
    }
    return reqs;
  }

  public static void findRequirement(List<IAbstract> requirements, IAbstract tpExp) {
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

  public static void findConstraintRequirements(List<IAbstract> requirements, IAbstract tpExp) {
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

  public static List<IAbstract> findConstraints(IAbstract stmt) {
    IAbstract tp = CompilerUtils.typeDefnType(stmt);
    List<IAbstract> cons = new ArrayList<>();

    if (Abstract.isBinary(tp, StandardNames.WHERE))
      for (IAbstract con : CompilerUtils.unWrap(Abstract.binaryRhs(tp), StandardNames.AND))
        cons.add(con);

    for (IAbstract con : CompilerUtils.unWrap(CompilerUtils.typeDefnConstructors(stmt)))
      if (Abstract.isBinary(con, StandardNames.WHERE))
        for (IAbstract c : CompilerUtils.unWrap(Abstract.binaryRhs(con), StandardNames.AND))
          cons.add(c);
    return cons;
  }

  private static List<IAbstract> equalityEquations(Location loc, String label, IAbstract term, IAbstract type, IAbstract eqType) {
    List<IAbstract> eqns = new ArrayList<>();
    if (Abstract.isBinary(term, StandardNames.WHERE))
      term = Abstract.binaryLhs(term);

    for (IAbstract el : CompilerUtils.unWrap(term, StandardNames.OR)) {
      IAbstract equalityEqn = equalityEqn(loc, label, el, type);
      if (equalityEqn != null)
        eqns.add(equalityEqn);
    }

    if (eqns.size() > 1)
      eqns.add(specDeflt(loc, label));

    List<IAbstract> defs = new ArrayList<>();
    defs.add(CompilerUtils.typeAnnotationStmt(loc, new Name(loc, label), eqType));
    defs.add(CompilerUtils.function(loc, eqns));

    return defs;
  }

  private static IAbstract equalityEqn(Location loc, String label, IAbstract term, IAbstract type) {
    if (term instanceof Name)
      return enumEquality(loc, label, term);
    else if (CompilerUtils.isBraceTerm(term))
      return recordEquality(loc, type, label, term);
    else
      return conEquality(loc, label, term, type);
  }

  private static List<IAbstract> hashEquations(Location loc, String label, IAbstract term, IAbstract type, IAbstract hashType) {
    List<IAbstract> eqns = new ArrayList<>();
    if (Abstract.isBinary(term, StandardNames.WHERE))
      term = Abstract.binaryLhs(term);

    for (IAbstract el : CompilerUtils.unWrap(term, StandardNames.OR)) {
      IAbstract equalityEqn = hashEqn(loc, label, el, type);
      if (equalityEqn != null)
        eqns.add(equalityEqn);
    }

    List<IAbstract> defs = new ArrayList<>();
    defs.add(CompilerUtils.typeAnnotationStmt(loc, new Name(loc, label), hashType));
    defs.add(CompilerUtils.function(loc, eqns));

    return defs;
  }

  private static IAbstract hashEqn(Location loc, String label, IAbstract term, IAbstract type) {
    if (term instanceof Name)
      return enumHash(loc, label, (Name) term);
    else if (CompilerUtils.isBraceTerm(term))
      return recordHash(loc, type, label, term);
    else
      return conHash(loc, label, term, type);
  }

  private static IAbstract enumHash(Location loc, String label, Name term) {
    List<IAbstract> args = new ArrayList<>();
    args.add(term);
    return CompilerUtils.equation(loc, label, args, Abstract.newInteger(loc, term.getLabel().hashCode()));
  }

  private static IAbstract conHash(Location loc, String label, IAbstract term, IAbstract type) {
    assert term instanceof AApply;
    List<IAbstract> lVars = new ArrayList<>();

    AApply apply = (AApply) term;
    String conLabel = apply.getOp();
    IAbstract conHash = Abstract.newInteger(loc, conLabel.hashCode());

    IAbstract thirtySeven = Abstract.newInteger(loc, 37);

    for (int vNo = 0; vNo < Abstract.arity(term); vNo++) {
      Name lV = new Name(loc, "$L" + vNo);
      IAbstract tp = apply.getArg(vNo);

      lVars.add(lV);

      if (Abstract.isInt(conHash))
        conHash = Abstract.newInteger(loc, Abstract.getInt(conHash) * 37);
      else
        conHash = Abstract.binary(loc, StandardNames.TIMES, conHash, thirtySeven);

      conHash = Abstract.binary(loc, StandardNames.PLUS, conHash, hash(loc, tp, type, lV, label));
    }

    List<IAbstract> args = new ArrayList<>();
    args.add(new AApply(loc, conLabel, lVars));

    return CompilerUtils.equation(loc, label, args, conHash);
  }

  private static IAbstract recordHash(Location loc, IAbstract type, String label, IAbstract term) {
    assert CompilerUtils.isBraceTerm(term) && Abstract.isName(CompilerUtils.braceLabel(term));

    IAbstract conLabel = CompilerUtils.braceLabel(term);
    int vNo = 0;
    IAbstract lArgs = null;
    IAbstract recordHash = Abstract.newInteger(loc, conLabel.hashCode());
    IAbstract thirtySeven = Abstract.newInteger(loc, 37);

    for (IAbstract el : CompilerUtils.unWrap(CompilerUtils.braceArg(term), StandardNames.TERM)) {
      if (CompilerUtils.isTypeAnnotation(el)) {
        IAbstract tp = CompilerUtils.typeAnnotation(el);
        IAbstract field = CompilerUtils.typeAnnotatedTerm(el);
        Name lV = new Name(loc, "$L" + vNo);

        vNo++;

        IAbstract lArg = CompilerUtils.equals(loc, field, lV);
        if (lArgs == null)
          lArgs = lArg;
        else
          lArgs = Abstract.binary(loc, StandardNames.TERM, lArgs, lArg);

        recordHash = Abstract.binary(loc, StandardNames.PLUS, Abstract.binary(loc, StandardNames.TIMES, recordHash, thirtySeven),
            hash(loc, tp, type, lV, label));
      }
    }

    List<IAbstract> args = new ArrayList<>();
    if (lArgs != null) {
      args.add(CompilerUtils.braceTerm(loc, conLabel, lArgs));
    } else {
      args.add(CompilerUtils.emptyBrace(loc, conLabel));
    }

    return CompilerUtils.equation(loc, label, args, recordHash);
  }

  private static IAbstract specDeflt(Location loc, String label) {
    List<IAbstract> args = new ArrayList<>();
    Name lV = new Name(loc, GenSym.genSym("L"));
    Name rV = new Name(loc, GenSym.genSym("R"));
    args.add(lV);
    args.add(rV);
    return CompilerUtils.defaultEquation(loc, label, args, Abstract.name(loc, StandardNames.FALSE));
  }

  /**
   * Construct
   * <p>
   * <pre>
   * <label>(<enum>,<enum>) is true;
   * </pre>
   */
  private static IAbstract enumEquality(Location loc, String label, IAbstract term) {
    assert term instanceof Name;
    List<IAbstract> args = new ArrayList<>();
    args.add(term);
    args.add(term);
    return CompilerUtils.equation(loc, label, args, Abstract.name(loc, StandardNames.TRUE));
  }

  /**
   * Construct
   * <p>
   * <pre>
   * <label>(<con>(L1,..,Ln),<con>(R1,..,Rn)) is L1=R1 and ... Ln=Rn
   * </pre>
   *
   * @param type Type whose equality is being defined. Allows for recursion.
   */
  private static IAbstract conEquality(Location loc, String label, IAbstract term, IAbstract type) {
    assert term instanceof AApply;
    List<IAbstract> lVars = new ArrayList<>();
    List<IAbstract> rVars = new ArrayList<>();

    AApply apply = (AApply) term;
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
    args.add(new AApply(loc, conLabel, lVars));
    args.add(new AApply(loc, conLabel, rVars));

    return CompilerUtils.equation(loc, label, args, cond);
  }

  /**
   * Construct
   * <p>
   * <pre>
   * <label>(<con>{F1=L1;...;Fn=Ln}, <con>{F1=R1;...;Fn=Rn}) is L1=R1 and ... and Ln=Rn
   * </pre>
   *
   * @param type Type whose equality is being defined. Permits handling of recursive types.
   */

  private static IAbstract recordEquality(Location loc, IAbstract type, String label, IAbstract term) {
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

  private static IAbstract equality(Location loc, IAbstract attTp, IAbstract type, Name lV, Name rV, String label) {
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
    else if (Abstract.isName(attTp, StandardTypes.RAW_STRING))
      return Abstract.binary(loc, StringCompare.StringEQ.NAME, lV, rV);
    else if (supportsEquality(attTp))
      return CompilerUtils.equals(loc, lV, rV);
    else
      return Abstract.binary(loc, GeneralEq.name, lV, rV);
  }

  private static boolean supportsEquality(IAbstract type) {
    return !CompilerUtils.isProgramType(type) && !Abstract.isName(type, StandardNames.ANY);
  }

  private static IAbstract hash(Location loc, IAbstract attTp, IAbstract type, Name lV, String label) {
    if (CompilerUtils.isReference(attTp))
      attTp = CompilerUtils.referencedTerm(attTp);

    if (attTp.equals(type))
      return Abstract.unary(loc, label, lV);
    else if (Abstract.isName(attTp, StandardTypes.RAW_INTEGER))
      return Abstract.unary(loc,StandardTypes.INTEGER,Abstract.unary(loc, IntUnary.IntHash.name, lV));
    else if (Abstract.isName(attTp, StandardTypes.RAW_LONG))
      return Abstract.unary(loc,StandardTypes.INTEGER,Abstract.unary(loc, LongUnary.LongHash.name, lV));
    else if (Abstract.isName(attTp, StandardTypes.RAW_FLOAT))
      return Abstract.unary(loc,StandardTypes.INTEGER,Abstract.unary(loc, FloatUnary.FloatHash.name, lV));
    else if (Abstract.isName(attTp, StandardTypes.RAW_STRING))
      return Abstract.unary(loc,StandardTypes.INTEGER,Abstract.unary(loc, StringOps.StringHash.NAME, lV));
    else if (supportsEquality(attTp))
      return Abstract.unary(loc, StandardNames.HASHCODE, lV);
    else
      return Abstract.unary(loc,StandardTypes.INTEGER,Abstract.unary(loc, HashCode.NAME, lV));
  }
}
