package org.star_lang.star.compiler.type;

import java.util.ArrayList;
import java.util.List;

import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.ast.Apply;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.ast.IntegerLiteral;
import org.star_lang.star.compiler.ast.Name;
import org.star_lang.star.compiler.ast.StringLiteral;
import org.star_lang.star.compiler.macrocompile.MacroCompiler;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.FixedList;
import org.star_lang.star.compiler.util.GenSym;
import org.star_lang.star.compiler.util.Wrapper;
import org.star_lang.star.data.IList;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.type.Location.NoWhere;
import org.star_lang.star.operators.arrays.runtime.ArrayIndexSlice.ArrayEl;
import org.star_lang.star.operators.arrays.runtime.ArrayOps;

/**
 * This class implements coercion from quoted form to the defined type.
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
public class QuoteBuilder
{
  private static IAbstract nowhere = Abstract.name(Location.nullLoc, NoWhere.nowhere);
  private static IAbstract quoted = Abstract.name(Location.nullLoc, StandardTypes.QUOTED);

  public static IAbstract checkForQuoting(IAbstract theta, Dictionary dict)
  {
    List<IAbstract> equations = new ArrayList<>();
    for (IAbstract stmt : CompilerUtils.unWrap(theta)) {
      Visibility visibility = CompilerUtils.privacy(stmt);
      stmt = CompilerUtils.dePrivatize(stmt);
      if (CompilerUtils.isTypeDefn(stmt) && !CompilerUtils.isTypeAlias(stmt)) {
        String tpLabel = CompilerUtils.typeDefnName(stmt);
        if (tpLabel != null && supportsQuote(stmt)) {
          if (!checkForDeQuote(tpLabel, theta)) {
            IAbstract implementation = dequoteImplementation(stmt, dict);
            if (implementation != null) {
              equations.add(CompilerUtils.privateStmt(stmt.getLoc(), visibility, implementation));
            }
          }
          if (!checkForQuote(tpLabel, theta)) {
            IAbstract implementation = quoteImplementation(stmt, dict);
            if (implementation != null) {
              equations.add(CompilerUtils.privateStmt(stmt.getLoc(), visibility, implementation));
            }
          }
        }
      }
    }
    for (IAbstract stmt : equations)
      theta = Abstract.binary(stmt.getLoc(), StandardNames.TERM, stmt, theta);
    return theta;
  }

  private static boolean checkForDeQuote(String tpLabel, IAbstract theta)
  {
    for (IAbstract stmt : CompilerUtils.unWrap(theta)) {
      if (CompilerUtils.isImplementationStmt(stmt)) {
        IAbstract con = CompilerUtils.implementedContract(stmt);
        if (Abstract.isName(con, StandardNames.COERCION)) {
          IAbstract overTypes = CompilerUtils.implementationContractType(stmt);
          if (Abstract.isTupleTerm(overTypes, 2)
              && Abstract.isIdentifier(Abstract.tupleArg(overTypes, 0), StandardTypes.QUOTED)
              && CompilerUtils.typeLabel(Abstract.tupleArg(overTypes, 1)).equals(tpLabel))
            return true;
        }
      }
    }
    return false;
  }

  public static IAbstract dequoteImplementation(IAbstract tDef, Dictionary dict)
  {
    assert CompilerUtils.isTypeDefn(tDef);

    IAbstract specs = CompilerUtils.typeDefnConstructors(tDef);
    IAbstract tp = CompilerUtils.typeDefnType(tDef);
    String typeLabel = CompilerUtils.typeLabel(tp);
    String label = typeLabel + "$dequote";

    IAbstract implTpSpec;
    IAbstract tpConstraint = null;

    Location loc = tp.getLoc();

    if (Abstract.isBinary(tp, StandardNames.WHERE)) {
      tpConstraint = Abstract.binaryRhs(tp);
      tp = Abstract.binaryLhs(tp);
    }

    IAbstract quoted = Abstract.name(loc, StandardTypes.QUOTED);
    IAbstract eqnType = CompilerUtils.functionType(loc, FixedList.create(quoted), tp);

    List<IAbstract> tVars = CompilerUtils.findTypeVarsInType(tp, dict, FixedList.create(typeLabel));
    IAbstract requirement = setupDeQuoteRequirement(loc, tp, tVars);
    if (requirement != null) {
      implTpSpec = mergeRequirements(Abstract.tupleTerm(loc, quoted, tp), requirement, tpConstraint);

      eqnType = mergeRequirements(eqnType, requirement, tpConstraint);
    } else
      implTpSpec = mergeRequirements(Abstract.tupleTerm(loc, quoted, tp), tpConstraint);

    eqnType = CompilerUtils.universalType(loc, tVars, eqnType);

    IAbstract equations = dequoteEquations(loc, label, specs, tp, eqnType);

    if (equations != null) {
      // construct implementation coercion over (quoted,tp) is let { <equations> } in
      // {= = <label>}
      IAbstract defn = CompilerUtils.letExp(loc, equations, CompilerUtils.blockTerm(loc, CompilerUtils.equals(loc,
          new Name(loc, StandardNames.COERCE), new Name(loc, label))));
      return CompilerUtils.implementationStmt(loc, CompilerUtils.universalType(loc, tVars, Abstract.binary(loc,
          StandardNames.OVER, new Name(loc, StandardNames.COERCION), implTpSpec)), defn);
    }
    return null;
  }

  private static boolean checkForQuote(String tpLabel, IAbstract theta)
  {
    for (IAbstract stmt : CompilerUtils.unWrap(theta)) {
      if (CompilerUtils.isImplementationStmt(stmt)) {
        IAbstract con = CompilerUtils.implementedContract(stmt);
        if (Abstract.isName(con, StandardNames.COERCION)) {
          IAbstract overTypes = CompilerUtils.implementationContractType(stmt);
          if (Abstract.isTupleTerm(overTypes, 2)
              && Abstract.isIdentifier(Abstract.tupleArg(overTypes, 1), StandardTypes.QUOTED)
              && CompilerUtils.typeLabel(Abstract.tupleArg(overTypes, 0)).equals(tpLabel))
            return true;
        }
      }
    }
    return false;
  }

  /**
   * Set up implementation of the coercion from teh type to quoted.
   * 
   * @param tpDef
   * @param dict
   * @return an implementation of (tp)=>quoted
   */

  public static IAbstract quoteImplementation(IAbstract tpDef, Dictionary dict)
  {
    assert CompilerUtils.isTypeDefn(tpDef);

    IAbstract specs = CompilerUtils.typeDefnConstructors(tpDef);
    IAbstract tp = CompilerUtils.typeDefnType(tpDef);
    String typeLabel = CompilerUtils.typeLabel(tp);
    String label = typeLabel + "$quote";

    IAbstract implTpSpec;
    IAbstract tpConstraint = null;

    Location loc = tp.getLoc();

    if (Abstract.isBinary(tp, StandardNames.WHERE)) {
      tpConstraint = Abstract.binaryRhs(tp);
      tp = Abstract.binaryLhs(tp);
    }

    IAbstract eqnType = CompilerUtils.functionType(loc, FixedList.create(tp), quoted);

    List<IAbstract> tVars = CompilerUtils.findTypeVarsInType(tp, dict, FixedList.create(typeLabel));
    IAbstract requirement = setupQuoteRequirement(loc, tp, tVars);
    if (requirement != null) {
      implTpSpec = mergeRequirements(Abstract.tupleTerm(loc, tp, quoted), requirement, tpConstraint);

      eqnType = mergeRequirements(eqnType, requirement, tpConstraint);
    } else
      implTpSpec = mergeRequirements(Abstract.tupleTerm(loc, tp, quoted), tpConstraint);

    eqnType = CompilerUtils.universalType(loc, tVars, eqnType);

    IAbstract equations = quoteEquations(loc, label, specs, tp, eqnType);

    if (equations != null) {
      // construct implementation coercion over (tp,quoted) is let { <equations> } in
      // {coerce = <label>}
      IAbstract defn = CompilerUtils.letExp(loc, equations, CompilerUtils.blockTerm(loc, CompilerUtils.equals(loc,
          new Name(loc, StandardNames.COERCE), new Name(loc, label))));
      return CompilerUtils.implementationStmt(loc, CompilerUtils.universalType(loc, tVars, Abstract.binary(loc,
          StandardNames.OVER, new Name(loc, StandardNames.COERCION), implTpSpec)), defn);
    }
    return null;
  }

  public static IAbstract mergeRequirements(IAbstract tp, IAbstract req, IAbstract constraint)
  {
    Location loc = tp.getLoc();

    if (constraint != null)
      return Abstract.binary(loc, StandardNames.WHERE, tp, Abstract.binary(loc, StandardNames.AND, constraint, req));
    else
      return Abstract.binary(loc, StandardNames.WHERE, tp, req);
  }

  public static IAbstract mergeRequirements(IAbstract tp, IAbstract constraint)
  {
    Location loc = tp.getLoc();

    if (constraint != null)
      return Abstract.binary(loc, StandardNames.WHERE, tp, constraint);
    else
      return tp;
  }

  private static IAbstract setupDeQuoteRequirement(Location loc, IAbstract tp, List<IAbstract> tVars)
  {
    findRequirement(tVars, tp);
    if (!tVars.isEmpty()) {
      IAbstract req = null;
      IAbstract quotedType = new Name(loc, StandardTypes.QUOTED);
      IAbstract contract = new Name(loc, StandardNames.COERCION);

      for (IAbstract ref : tVars) {
        IAbstract contTerm = Abstract.binary(loc, StandardNames.OVER, contract, Abstract
            .tupleTerm(loc, quotedType, ref));
        if (req == null)
          req = contTerm;
        else
          req = Abstract.binary(loc, StandardNames.AND, contTerm, req);
      }
      return req;
    } else
      return null;
  }

  private static IAbstract setupQuoteRequirement(Location loc, IAbstract tp, List<IAbstract> tVars)
  {
    findRequirement(tVars, tp);
    if (!tVars.isEmpty()) {
      IAbstract req = null;
      IAbstract quotedType = new Name(loc, StandardTypes.QUOTED);
      IAbstract contract = new Name(loc, StandardNames.COERCION);

      for (IAbstract ref : tVars) {
        IAbstract contTerm = Abstract.binary(loc, StandardNames.OVER, contract, Abstract
            .tupleTerm(loc, ref, quotedType));
        if (req == null)
          req = contTerm;
        else
          req = Abstract.binary(loc, StandardNames.AND, contTerm, req);
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

  /**
   * Individual constructor terms are dequoted with these specEquations.
   * 
   * @param loc
   *          where is the type definition
   * @param label
   *          what is the name of the dequoting function
   * @param term
   *          type specification term
   * @param type
   *          result type for dequoter
   * @param eqType
   *          type of function
   * @return the vector of equations that implement the dequoter
   */
  private static IAbstract dequoteEquations(Location loc, String label, IAbstract term, IAbstract type, IAbstract eqType)
  {
    List<IAbstract> eqns = new ArrayList<>();
    eqns.add(CompilerUtils.typeAnnotationStmt(loc, new Name(loc, label), eqType));
    if (Abstract.isBinary(term, StandardNames.WHERE))
      term = Abstract.binaryLhs(term);
    for (IAbstract el : CompilerUtils.unWrap(term, StandardNames.OR)) {
      IAbstract equalityEqn = specDeQuote(loc, label, el, type);
      if (equalityEqn != null)
        eqns.add(equalityEqn);
    }

    return CompilerUtils.tupleUp(loc, StandardNames.TERM, eqns);
  }

  private static IAbstract specDeQuote(Location loc, String label, IAbstract term, IAbstract type)
  {
    if (term instanceof Name)
      return dequoteEnum(loc, label, term);
    else if (CompilerUtils.isBraceTerm(term))
      return dequoteRecord(loc, type, label, term);
    else
      return dequoteConstructor(loc, label, term, type);
  }

  /**
   * Construct
   * <p/>
   * 
   * <pre>
   * <label>(astName(_,<enum>)) is <enum>;
   * </pre>
   */
  private static IAbstract dequoteEnum(Location loc, String label, IAbstract term)
  {
    assert term instanceof Name;
    List<IAbstract> args = FixedList.create(MacroCompiler.astName(loc, Abstract.anon(loc), ((Name) term).getId()));
    return CompilerUtils.equation(loc, label, args, term);
  }

  /**
   * Construct
   * <p/>
   * 
   * <pre>
   * <label>(astApply(_,astName(_,<con>),list of {L1;...;Ln})) is <con>(<label>(L1),..,<label>(Ln)))
   * </pre>
   * 
   * @param type
   *          Type whose dequoter is being defined. Allows for recursion.
   */
  private static IAbstract dequoteConstructor(Location loc, String label, IAbstract term, IAbstract type)
  {
    assert term instanceof Apply;
    List<IAbstract> vars = new ArrayList<>();
    IAbstract anon = Abstract.anon(loc);

    Apply apply = (Apply) term;
    String conLabel = apply.getOp();
    IAbstract opPtn = MacroCompiler.astName(loc, anon, conLabel);
    IAbstract argsVar = new Name(loc, GenSym.genSym("_args"));

    IAbstract lhsPtn = Abstract.ternary(loc, Apply.name, anon, opPtn, argsVar);

    Wrapper<IAbstract> cond = Wrapper.create(null);

    IList argArray = apply.getArgs();
    int arity = argArray.size();

    CompilerUtils.extendCondition(cond, Abstract.binary(loc, ArrayOps.ArrayHasSize.name, argsVar, CompilerUtils
        .rawLiteral(loc, new IntegerLiteral(loc, arity))));
    for (int ix = 0; ix < arity; ix++) {
      Wrapper<IAbstract> subCond = Wrapper.create(null);
      IAbstract argVar = Abstract.name(loc, GenSym.genSym("_"));
      IAbstract arg = Abstract.binary(loc, ArrayEl.name, argsVar, CompilerUtils.rawLiteral(loc, new IntegerLiteral(loc,
          ix)));
      CompilerUtils.extendCondition(cond, CompilerUtils.boundTo(loc, argVar, arg));
      CompilerUtils.appendCondition(cond, subCond);
      IAbstract argType = Abstract.getArg(term, ix);
      if (argType.equals(type))
        vars.add(Abstract.unary(loc, label, argVar));
      else
        vars.add(Abstract.binary(loc, StandardNames.AS, argVar, argType));
    }

    IAbstract rhs = Abstract.apply(loc, apply.getOperator(), vars);

    if (CompilerUtils.isTrivial(cond.get()))
      return CompilerUtils.equation(loc, Abstract.unary(loc, label, lhsPtn), rhs);
    else
      return CompilerUtils.equation(loc, Abstract.unary(loc, label, Abstract.binary(loc, StandardNames.WHERE, lhsPtn,
          cond.get())), rhs);
  }

  /**
   * Construct
   * <p/>
   * 
   * <pre>
   * <label>(astApply(_,astName(_,"{}"),list of{astName(_,<con>); args})) is
   *    <con>{F1=find(args,F1) as T1;...;Fn=find(args,Fn) as Tn}
   * </pre>
   * 
   * @param type
   *          Type whose dequoter is being defined. Permits handling of recursive types.
   */

  private static IAbstract dequoteRecord(Location loc, IAbstract type, String label, IAbstract term)
  {
    assert CompilerUtils.isBraceTerm(term) && Abstract.isName(CompilerUtils.braceLabel(term));

    Wrapper<IAbstract> cond = Wrapper.create(null);

    IAbstract argsVar = new Name(loc, GenSym.genSym("_args"));
    List<IAbstract> vars = new ArrayList<>();
    IAbstract anon = Abstract.anon(loc);
    IAbstract conLabel = CompilerUtils.braceLabel(term);
    IAbstract opPtn = MacroCompiler.astName(loc, anon, Abstract.getId(conLabel));

    IAbstract lhsPtn = MacroCompiler.astApply(loc, anon, MacroCompiler.astName(loc, anon, StandardNames.BRACES),
        FixedList.create(opPtn, argsVar));

    for (IAbstract el : CompilerUtils.unWrap(CompilerUtils.braceArg(term), StandardNames.TERM)) {
      IAbstract argType = CompilerUtils.typeAnnotation(el);
      IAbstract field = CompilerUtils.typeAnnotatedTerm(el);

      Wrapper<IAbstract> subCond = Wrapper.create(null);
      IAbstract argVar = Abstract.name(loc, GenSym.genSym("_"));

      IAbstract arg = Abstract.binary(loc, "__find_in_ast", argsVar, CompilerUtils.rawLiteral(loc, new StringLiteral(
          loc, Abstract.getId(field))));

      CompilerUtils.extendCondition(cond, CompilerUtils.boundTo(loc, argVar, arg));
      CompilerUtils.appendCondition(cond, subCond);
      if (argType.equals(type))
        vars.add(Abstract.binary(loc, StandardNames.EQUAL, field, Abstract.unary(loc, label, argVar)));
      else
        vars.add(Abstract.binary(loc, StandardNames.EQUAL, field, Abstract.binary(loc, StandardNames.AS, argVar,
            argType)));
    }

    final IAbstract rhs = CompilerUtils.braceTerm(loc, conLabel, vars);

    if (CompilerUtils.isTrivial(cond.get()))
      return CompilerUtils.equation(loc, Abstract.unary(loc, label, lhsPtn), rhs);
    else
      return CompilerUtils.equation(loc, Abstract.unary(loc, label, Abstract.binary(loc, StandardNames.WHERE, lhsPtn,
          cond.get())), rhs);
  }

  private static boolean supportsQuote(IAbstract stmt)
  {
    IAbstract term = CompilerUtils.typeDefnConstructors(stmt);
    if (Abstract.isBinary(term, StandardNames.WHERE))
      term = Abstract.binaryLhs(term);
    for (IAbstract el : CompilerUtils.unWrap(term, StandardNames.OR)) {
      if (CompilerUtils.isBraceTerm(el)) {
        for (IAbstract arg : CompilerUtils.unWrap(CompilerUtils.braceArg(el), StandardNames.TERM)) {
          if (CompilerUtils.isTypeAnnotation(arg)) {
            IAbstract type = CompilerUtils.typeAnnotation(arg);
            if (CompilerUtils.isProgramType(type) || Abstract.isName(type, StandardNames.ANY))
              return false;
          }
        }
      } else if (Abstract.isApply(el)) {
        for (IValue arg : Abstract.getArgs(el)) {
          IAbstract type = (IAbstract) arg;
          if (CompilerUtils.isProgramType(type) || Abstract.isName(type, StandardNames.ANY))
            return false;
        }
      }
    }

    return true;
  }

  /**
   * Individual constructor terms are quoted with these equations.
   * 
   * @param loc
   *          where is the type definition
   * @param label
   *          what is the name of the quoting function
   * @param term
   *          type specification term
   * @param type
   *          result type for quoter
   * @param eqType
   *          type of function
   * @return the vector of equations that implement the dequoter
   */
  private static IAbstract quoteEquations(Location loc, String label, IAbstract term, IAbstract type, IAbstract eqType)
  {
    List<IAbstract> eqns = new ArrayList<>();
    eqns.add(CompilerUtils.typeAnnotationStmt(loc, new Name(loc, label), eqType));
    if (Abstract.isBinary(term, StandardNames.WHERE))
      term = Abstract.binaryLhs(term);
    for (IAbstract el : CompilerUtils.unWrap(term, StandardNames.OR)) {
      IAbstract equalityEqn = specQuote(loc, label, el, type);
      if (equalityEqn != null)
        eqns.add(equalityEqn);
    }

    return CompilerUtils.tupleUp(loc, StandardNames.TERM, eqns);
  }

  private static IAbstract specQuote(Location loc, String label, IAbstract term, IAbstract type)
  {
    if (term instanceof Name)
      return quoteEnum(loc, label, term);
    else if (CompilerUtils.isBraceTerm(term))
      return quoteRecord(loc, type, label, term);
    else
      return quoteConstructor(loc, label, term, type);
  }

  /**
   * Construct
   * <p/>
   * 
   * <pre>
   * <label>(<enum>) is astName(noWhere,<enum>);
   * </pre>
   */
  private static IAbstract quoteEnum(Location loc, String label, IAbstract term)
  {
    assert term instanceof Name;
    return CompilerUtils.equation(loc, label, FixedList.create(term), MacroCompiler.astName(loc, nowhere, ((Name) term)
        .getId()));
  }

  /**
   * Construct
   * <p/>
   * 
   * <pre>
   * <label>(<label>(L1),..,<label>(Ln)) is astApply(_,astName(_,<label>),list of {L1 as quoted;...;Ln as quoted}))
   * </pre>
   * 
   * @param type
   *          Type whose quoter is being defined. Allows for recursion.
   */
  private static IAbstract quoteConstructor(Location loc, String label, IAbstract term, IAbstract type)
  {
    assert term instanceof Apply;
    List<IAbstract> vars = new ArrayList<>();
    List<IAbstract> args = new ArrayList<>();

    Apply apply = (Apply) term;
    String conLabel = apply.getOp();
    IAbstract opPtn = new Name(loc, conLabel);
    IList argArray = apply.getArgs();
    int arity = argArray.size();

    for (int ix = 0; ix < arity; ix++) {
      IAbstract argVar = Abstract.name(loc, GenSym.genSym("_"));
      args.add(argVar);
      vars.add(Abstract.binary(loc, StandardNames.AS, argVar, quoted));
    }

    IAbstract lhsPtn = Abstract.apply(loc, opPtn, args);

    IAbstract rhs = MacroCompiler.astApply(loc, nowhere, MacroCompiler.astName(loc, nowhere, conLabel), vars);

    return CompilerUtils.equation(loc, Abstract.unary(loc, label, lhsPtn), rhs);
  }

  /**
   * Construct
   * <p/>
   * 
   * <pre>
   * <label>(astApply(_,astName(_,"{}"),list of{astName(_,<con>); args})) is
   *    <con>{F1=find(args,F1) as T1;...;Fn=find(args,Fn) as Tn}
   * </pre>
   * 
   * @param type
   *          Type whose dequoter is being defined. Permits handling of recursive types.
   */

  private static IAbstract quoteRecord(Location loc, IAbstract type, String label, IAbstract term)
  {
    assert CompilerUtils.isBraceTerm(term) && Abstract.isName(CompilerUtils.braceLabel(term));

    Wrapper<IAbstract> cond = Wrapper.create(null);

    IAbstract argsVar = new Name(loc, GenSym.genSym("_args"));
    List<IAbstract> vars = new ArrayList<>();
    IAbstract anon = Abstract.anon(loc);
    IAbstract conLabel = CompilerUtils.braceLabel(term);
    IAbstract opPtn = MacroCompiler.astName(loc, anon, Abstract.getId(conLabel));

    IAbstract lhsPtn = MacroCompiler.astApply(loc, anon, MacroCompiler.astName(loc, anon, StandardNames.BRACES),
        FixedList.create(opPtn, argsVar));

    for (IAbstract el : CompilerUtils.unWrap(CompilerUtils.braceArg(term), StandardNames.TERM)) {
      IAbstract argType = CompilerUtils.typeAnnotation(el);
      IAbstract field = CompilerUtils.typeAnnotatedTerm(el);

      Wrapper<IAbstract> subCond = Wrapper.create(null);
      IAbstract argVar = Abstract.name(loc, GenSym.genSym("_"));

      IAbstract arg = Abstract.binary(loc, "__find_in_ast", argsVar, CompilerUtils.rawLiteral(loc, new StringLiteral(
          loc, Abstract.getId(field))));

      CompilerUtils.extendCondition(cond, CompilerUtils.boundTo(loc, argVar, arg));
      CompilerUtils.appendCondition(cond, subCond);
      if (argType.equals(type))
        vars.add(Abstract.binary(loc, StandardNames.EQUAL, field, Abstract.unary(loc, label, argVar)));
      else
        vars.add(Abstract.binary(loc, StandardNames.EQUAL, field, Abstract.binary(loc, StandardNames.AS, argVar,
            argType)));
    }

    final IAbstract rhs = CompilerUtils.braceTerm(loc, conLabel, vars);

    if (CompilerUtils.isTrivial(cond.get()))
      return CompilerUtils.equation(loc, Abstract.unary(loc, label, lhsPtn), rhs);
    else
      return CompilerUtils.equation(loc, Abstract.unary(loc, label, Abstract.binary(loc, StandardNames.WHERE, lhsPtn,
          cond.get())), rhs);
  }
}
