package org.star_lang.star.compiler.cafe;

import java.util.ArrayList;
import java.util.List;

import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.ast.Apply;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.ast.IntegerLiteral;
import org.star_lang.star.compiler.ast.Name;
import org.star_lang.star.compiler.ast.StringLiteral;
import org.star_lang.star.compiler.ast.TypeAttribute;
import org.star_lang.star.compiler.cafe.compile.Utils;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.GenSym;
import org.star_lang.star.data.IList;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.value.ResourceURI;

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
public class CafeSyntax
{

  public static IAbstract importSpec(Location loc, String pkg)
  {
    return new Apply(loc, Names.IMPORT, new StringLiteral(loc, pkg));
  }

  public static IAbstract importSpec(Location loc, ResourceURI uri)
  {
    return Abstract.unary(loc, Names.IMPORT, new StringLiteral(loc, uri.toString()));
  }

  public static boolean isImport(IAbstract trm)
  {
    return Abstract.isUnary(trm, Names.IMPORT) && Abstract.argPath(trm, 0) instanceof StringLiteral;
  }

  public static String importImport(IAbstract trm)
  {
    assert isImport(trm);

    return ((StringLiteral) Abstract.unaryArg(trm)).getLit();
  }

  public static IAbstract javaImport(Location loc, String className)
  {
    return Abstract.unary(loc, Names.JAVA, new Name(loc, className));
  }

  public static boolean isJavaImport(IAbstract stmt)
  {
    return Abstract.isUnary(stmt, Names.JAVA);
  }

  public static String javaImportClass(IAbstract stmt)
  {
    assert isJavaImport(stmt);
    return Abstract.getId(Abstract.unaryArg(stmt));
  }

  public static boolean isLambda(IAbstract fun)
  {
    return Abstract.isBinary(fun, Names.ARROW) && Abstract.isApply(Abstract.binaryLhs(fun), Names.FUNCTION);
  }

  public static IList lambdaArgs(IAbstract fun)
  {
    assert isLambda(fun);
    return ((Apply) Abstract.binaryLhs(fun)).getArgs();
  }

  public static IAbstract lambdaValue(IAbstract fun)
  {
    assert isLambda(fun);
    return Abstract.binaryRhs(fun);
  }

  public static IAbstract lambdaFun(Location loc, List<IAbstract> args, IAbstract exp, IAbstract type)
  {
    return Abstract.binary(loc, Names.ARROW, new Apply(loc, Names.FUNCTION, args), exp);
  }

  public static IAbstract functionDefn(Location loc, String name, List<IAbstract> args, IAbstract exp, IAbstract type)
  {
    return isDeclaration(loc, typeCast(loc, Abstract.name(loc, name), type), lambdaFun(loc, args, exp, type));
  }

  public static boolean isFunctionDefn(IAbstract def)
  {
    if (isIsDeclaration(def))
      return (CafeSyntax.isTypedTerm(isDeclLval(def)) || CafeSyntax.termHasType(isDeclLval(def)))
          && isLambda(isDeclValue(def));
    else if (isVarDeclaration(def))
      return CafeSyntax.isTypedTerm(varDeclLval(def)) && isLambda(varDeclValue(def));
    else
      return false;
  }

  public static IAbstract functionLval(IAbstract def)
  {
    assert isFunctionDefn(def);
    return isDeclLval(def);
  }

  public static IAbstract functionType(IAbstract def)
  {
    assert isFunctionDefn(def);

    return Abstract.argPath(def, 0, 1);
  }

  public static IList functionArgs(IAbstract fun)
  {
    assert isFunctionDefn(fun);

    return lambdaArgs(Abstract.binaryRhs(fun));
  }

  public static IAbstract functionExp(IAbstract fun)
  {
    assert isFunctionDefn(fun);

    return lambdaValue(Abstract.binaryRhs(fun));
  }

  public static String definedFunctionName(IAbstract trm)
  {
    assert isFunctionDefn(trm);

    return Abstract.getId(CafeSyntax.typedTerm(isDeclLval(trm)));
  }

  public static IAbstract memoDefn(Location loc, String name, IAbstract exp, IType type)
  {
    Name nm = Abstract.name(loc, name);
    markTermWithType(nm, type);
    IAbstract memo = Abstract.unary(loc, Names.MEMO, exp);
    assert termHasType(exp);

    markTermWithType(memo, type);
    return isDeclaration(loc, nm, memo);
  }

  public static boolean isMemoDefn(IAbstract def)
  {
    return isIsDeclaration(def) && termHasType(isDeclLval(def)) && Abstract.isUnary(isDeclValue(def), Names.MEMO);
  }

  public static IAbstract memoLval(IAbstract def)
  {
    assert isMemoDefn(def);
    return isDeclLval(def);
  }

  public static IAbstract memoExp(IAbstract fun)
  {
    assert isMemoDefn(fun);

    return Abstract.argPath(fun, 1, 0);
  }

  public static String definedMemoName(IAbstract trm)
  {
    assert isMemoDefn(trm);

    IAbstract declaredName = isDeclLval(trm);
    if (Abstract.isIdentifier(declaredName))
      return Abstract.getId(declaredName);
    else {
      assert isTypedTerm(declaredName);
      return Abstract.getId(CafeSyntax.typedTerm(declaredName));
    }
  }

  public static boolean isMemo(IAbstract exp)
  {
    return Abstract.isUnary(exp, Names.MEMO);
  }

  public static IAbstract memoExpression(IAbstract memo)
  {
    assert isMemo(memo);
    return Abstract.unaryArg(memo);
  }

  // A pattern definition looks like:
  // <id>:<type> is pattern <exp> <= <ptn>
  //
  public static IAbstract patternDefn(Location loc, String name, IAbstract result, IAbstract ptn, IAbstract type)
  {
    return isDeclaration(loc, typeCast(loc, Abstract.name(loc, name), type), pattern(loc, result, ptn));
  }

  public static boolean isPatternDefn(IAbstract exp)
  {
    if (isIsDeclaration(exp) && Abstract.isBinary(isDeclValue(exp), Names.LARROW)) {
      IAbstract defn = Abstract.binaryLhs(isDeclValue(exp));
      return Abstract.isApply(defn, StandardNames.PATTERN);
    } else
      return false;
  }

  public static IAbstract pattern(Location loc, IAbstract result, IAbstract ptn)
  {
    return Abstract.binary(loc, Names.LARROW, Abstract.unary(loc, Names.PATTERN, result), ptn);
  }

  public static IAbstract patternLval(IAbstract def)
  {
    assert isPatternDefn(def);
    return isDeclLval(def);
  }

  public static IAbstract patternType(IAbstract def)
  {
    assert isPatternDefn(def);

    return typedType(isDeclLval(def));
  }

  public static IAbstract patternResult(IAbstract def)
  {
    assert isPatternDefn(def);

    return Abstract.unaryArg(Abstract.binaryLhs(isDeclValue(def)));
  }

  public static IAbstract patternPtn(IAbstract def)
  {
    assert isPatternDefn(def);

    return Abstract.binaryRhs(Abstract.binaryRhs(def));
  }

  public static String definedPatternName(IAbstract def)
  {
    assert isPatternDefn(def);

    return Abstract.getId(CafeSyntax.typedTerm(isDeclLval(def)));
  }

  public static boolean isPattern(IAbstract trm)
  {
    return Abstract.isBinary(trm, Names.LARROW) && Abstract.isUnary(Abstract.binaryLhs(trm), Names.PATTERN);
  }

  public static IAbstract ptnResult(IAbstract fun)
  {
    assert isPattern(fun);

    return Abstract.unaryArg(Abstract.binaryLhs(fun));
  }

  public static IAbstract apply(Location loc, String op, IAbstract... args)
  {
    return new Apply(loc, op, args);
  }

  public static IAbstract apply(Location loc, String op, List<IAbstract> args)
  {
    return new Apply(loc, op, args);
  }

  public static IAbstract apply(Location loc, IAbstract op, List<IAbstract> args)
  {
    return new Apply(loc, op, args);
  }

  public static IAbstract funcall(Location loc, String op, IAbstract args)
  {
    return new Apply(loc, Names.FCALL, new Name(loc, op), args);
  }

  public static IAbstract funcall(Location loc, IAbstract op, IAbstract args)
  {
    return new Apply(loc, Names.FCALL, op, args);
  }

  public static boolean isFunCall(IAbstract trm)
  {
    return Abstract.isBinary(trm, Names.FCALL);
  }

  public static IAbstract funCallOperator(IAbstract trm)
  {
    assert isFunCall(trm);
    return Abstract.binaryLhs(trm);
  }

  public static String funCallName(IAbstract term)
  {
    IAbstract op = funCallOperator(term);
    return Abstract.getId(op);
  }

  public static IAbstract funCallArgs(IAbstract trm)
  {
    assert isFunCall(trm);

    return Abstract.binaryRhs(trm);
  }

  public static IAbstract escape(Location loc, String op, IAbstract... args)
  {
    return new Apply(loc, Names.ESCAPE, Abstract.name(loc, op), block(loc, args));
  }

  public static boolean isEscape(IAbstract trm)
  {
    return Abstract.isBinary(trm, Names.ESCAPE) && Abstract.argPath(trm, 0) instanceof Name
        && isBlock(Abstract.argPath(trm, 1));
  }

  public static String escapeOp(IAbstract trm)
  {
    assert isEscape(trm);
    return Abstract.getId(Abstract.binaryLhs(trm));
  }

  public static IList escapeArgs(IAbstract trm)
  {
    assert isEscape(trm);

    return blockContents(Abstract.argPath(trm, 1));
  }

  public static IAbstract dot(Location loc, IAbstract rc, String field)
  {
    assert rc instanceof Name;
    return Abstract.binary(loc, Names.PERIOD, rc, Abstract.name(loc, field));
  }

  public static IAbstract dot(Location loc, IAbstract rc, int offset)
  {
    assert rc instanceof Name;
    return Abstract.binary(loc, Names.PERIOD, rc, new IntegerLiteral(loc, offset));
  }

  public static boolean isDot(IAbstract trm)
  {
    return Abstract.isBinary(trm, Names.PERIOD)
        && (Abstract.binaryRhs(trm) instanceof Name || Abstract.binaryRhs(trm) instanceof IntegerLiteral);
  }

  public static IAbstract dotRecord(IAbstract trm)
  {
    assert isDot(trm);

    return Abstract.binaryLhs(trm);
  }

  public static IAbstract dotField(IAbstract trm)
  {
    assert isDot(trm);

    return Abstract.binaryRhs(trm);
  }

  public static IAbstract regexp(Location loc, String regexp, List<IAbstract> args)
  {
    return Abstract.binary(loc, Names.REGEXP, new StringLiteral(loc, regexp), block(loc, args));
  }

  public static boolean isRegexp(IAbstract trm)
  {
    return Abstract.isBinary(trm, Names.REGEXP) && Abstract.binaryLhs(trm) instanceof StringLiteral
        && isBlock(Abstract.binaryRhs(trm));
  }

  public static String regexpExp(IAbstract trm)
  {
    assert isRegexp(trm);

    return Abstract.getString(Abstract.binaryLhs(trm));
  }

  public static IList regexpArgs(IAbstract trm)
  {
    assert isRegexp(trm);
    return blockContents(Abstract.binaryRhs(trm));
  }

  public static IAbstract builtinName(Location loc, String name, String javaName)
  {
    return new Apply(loc, Names.BUILTIN, Abstract.name(loc, name), Abstract.name(loc, javaName));
  }

  public static IAbstract copy(Location loc, IAbstract exp)
  {
    return new Apply(loc, Names.COPY, exp);
  }

  public static boolean isCopy(IAbstract exp)
  {
    return Abstract.isUnary(exp, Names.COPY);
  }

  public static IAbstract copied(IAbstract exp)
  {
    assert isCopy(exp);

    return Abstract.unaryArg(exp);
  }

  public static IAbstract arith(Location loc, IAbstract lhs, String op, IAbstract rhs)
  {
    return new Apply(loc, op, lhs, rhs);
  }

  public static IAbstract typeCast(Location loc, IAbstract term, IAbstract type)
  {
    return new Apply(loc, Names.COLON, term, type);
  }

  public static boolean isTypedTerm(IAbstract trm)
  {
    return Abstract.isBinary(trm, Names.COLON);
  }

  public static IAbstract typedTerm(IAbstract trm)
  {
    assert isTypedTerm(trm);
    return Abstract.argPath(trm, 0);
  }

  public static IAbstract typedType(IAbstract trm)
  {
    assert isTypedTerm(trm);
    return Abstract.argPath(trm, 1);
  }

  public static boolean termHasType(IAbstract term)
  {
    return term.hasAttribute(Names.TYPE);
  }

  public static IType termType(IAbstract term)
  {
    assert termHasType(term);
    return ((TypeAttribute) term.getAttribute(Names.TYPE)).getType();
  }

  public static IAbstract markTermWithType(IAbstract term, IType type)
  {
    assert !termHasType(term);
    term.setAttribute(Names.TYPE, new TypeAttribute(type));
    return term;
  }

  public static IAbstract callPtn(Location loc, IAbstract pttrn, IAbstract reslt)
  {
    return new Apply(loc, Names.QQUERY, pttrn, reslt);
  }

  public static boolean isPtnCall(IAbstract trm)
  {
    return Abstract.isBinary(trm, Names.QQUERY);
  }

  public static IAbstract callPtnPtn(IAbstract trm)
  {
    assert isPtnCall(trm);
    return Abstract.binaryLhs(trm);
  }

  public static IAbstract ptnCallResult(IAbstract trm)
  {
    assert isPtnCall(trm);
    return Abstract.binaryRhs(trm);
  }

  public static IAbstract predicate(Location loc, IAbstract lhs, String op, IAbstract rhs)
  {
    return new Apply(loc, op, lhs, rhs);
  }

  public static IAbstract letExp(Location loc, List<IAbstract> defs, IAbstract bound)
  {
    return new Apply(loc, Names.LET, block(loc, defs), bound);
  }

  public static IAbstract letExp(Location loc, IAbstract def, IAbstract bound)
  {
    List<IAbstract> defs = new ArrayList<IAbstract>();
    defs.add(def);
    return letExp(loc, defs, bound);
  }

  public static boolean isLetExp(IAbstract trm)
  {
    return Abstract.isBinary(trm, Names.LET) && Abstract.isApply(Abstract.argPath(trm, 0), Names.BLOCK);
  }

  public static IAbstract letBound(IAbstract trm)
  {
    assert isLetExp(trm);
    return Abstract.argPath(trm, 1);
  }

  public static IList letDefs(IAbstract trm)
  {
    assert isLetExp(trm);
    return blockContents(Abstract.argPath(trm, 0));
  }

  public static IAbstract nothing(Location loc)
  {
    return new Name(loc, Names.NOTHING);
  }

  public static boolean isNothing(IAbstract trm)
  {
    return Abstract.isName(trm, Names.NOTHING);
  }

  public static IAbstract block(Location loc, IList acts)
  {
    return new Apply(loc, Names.BLOCK, acts);
  }

  public static IAbstract block(Location loc, List<IAbstract> acts)
  {
    return new Apply(loc, Names.BLOCK, acts);
  }

  public static IAbstract block(Location loc, IAbstract... acts)
  {
    return new Apply(loc, Names.BLOCK, acts);
  }

  public static boolean isBlock(IAbstract term)
  {
    return Abstract.isApply(term, Names.BLOCK);
  }

  public static IList blockContents(IAbstract term)
  {
    assert isBlock(term);

    return Abstract.getArgs(term);
  }

  public static IAbstract conditional(Location loc, IAbstract test, IAbstract th, IAbstract el)
  {
    return new Apply(loc, Names.IF, test, th, el);
  }

  public static boolean isConditional(IAbstract trm)
  {
    return Abstract.isTernary(trm, Names.IF);
  }

  public static IAbstract conditionalTest(IAbstract trm)
  {
    assert isConditional(trm);
    return Abstract.argPath(trm, 0);
  }

  public static IAbstract conditionalThen(IAbstract trm)
  {
    assert isConditional(trm);
    return Abstract.argPath(trm, 1);
  }

  public static IAbstract conditionalElse(IAbstract trm)
  {
    assert isConditional(trm);
    return Abstract.argPath(trm, 2);
  }

  public static boolean isCatchAction(IAbstract act)
  {
    return Abstract.isBinary(act, Names.CATCH);
  }

  public static IAbstract catchBody(IAbstract act)
  {
    assert isCatchAction(act);
    return Abstract.binaryLhs(act);
  }

  public static IAbstract catchHandler(IAbstract act)
  {
    assert isCatchAction(act);
    return Abstract.binaryRhs(act);
  }

  public static IAbstract varDeclaration(Location loc, IAbstract lv, IAbstract exp)
  {
    return new Apply(loc, Names.VAR, lv, exp);
  }

  public static boolean isVarDeclaration(IAbstract trm)
  {
    return Abstract.isBinary(trm, Names.VAR);
  }

  public static IAbstract varDeclLval(IAbstract trm)
  {
    assert isVarDeclaration(trm);
    return Abstract.argPath(trm, 0);
  }

  public static IAbstract varDeclValue(IAbstract trm)
  {
    assert isVarDeclaration(trm);
    return Abstract.argPath(trm, 1);
  }

  public static IAbstract isDeclaration(Location loc, IAbstract lv, IAbstract exp)
  {
    return new Apply(loc, Names.IS, lv, exp);
  }

  public static boolean isIsDeclaration(IAbstract trm)
  {
    return Abstract.isBinary(trm, Names.IS);
  }

  public static IAbstract isDeclLval(IAbstract trm)
  {
    assert isIsDeclaration(trm);
    return Abstract.argPath(trm, 0);
  }

  public static IAbstract isDeclValue(IAbstract trm)
  {
    assert isIsDeclaration(trm);
    return Abstract.argPath(trm, 1);
  }

  public static IAbstract assignment(Location loc, IAbstract lv, IAbstract exp)
  {
    return new Apply(loc, Names.ASSIGN, lv, exp);
  }

  public static boolean isAssignment(IAbstract term)
  {
    if (Abstract.isBinary(term, Names.ASSIGN)) {
      IAbstract lVal = Abstract.binaryLhs(term);
      return Abstract.isName(lVal) || isDot(lVal) || isTuple(lVal);
    }
    return false;
  }

  public static IAbstract assignmentLval(IAbstract term)
  {
    assert isAssignment(term);

    return Abstract.argPath(term, 0);
  }

  public static IAbstract assignmentRval(IAbstract term)
  {
    assert isAssignment(term);

    return Abstract.argPath(term, 1);
  }

  public static IAbstract reference(Location loc, IAbstract cond)
  {
    return Abstract.unary(loc, Names.REF, cond);
  }

  public static boolean isReference(IAbstract cond)
  {
    return Abstract.isUnary(cond, Names.REF);
  }

  public static IAbstract refVal(IAbstract trm)
  {
    assert isReference(trm);

    return Abstract.unaryArg(trm);
  }

  public static IAbstract assertion(Location loc, IAbstract cond)
  {
    return new Apply(loc, Names.ASSERT, cond);
  }

  public static boolean isAssert(IAbstract cond)
  {
    return Abstract.isUnary(cond, Names.ASSERT);
  }

  public static IAbstract assertedCond(IAbstract cond)
  {
    assert isAssert(cond);

    return Abstract.argPath(cond, 0);
  }

  public static IAbstract ignore(Location loc, IAbstract exp)
  {
    return new Apply(loc, Names.IGNORE, exp);
  }

  public static boolean isIgnore(IAbstract cond)
  {
    return Abstract.isUnary(cond, Names.IGNORE);
  }

  public static IAbstract ignoredExp(IAbstract cond)
  {
    assert isIgnore(cond);

    return Abstract.argPath(cond, 0);
  }

  public static IAbstract labeled(Location loc, String lbl, IAbstract act)
  {
    return new Apply(loc, Names.LABELED, new Name(loc, lbl), act);
  }

  public static boolean isLabeled(IAbstract act)
  {
    return Abstract.isBinary(act, Names.LABELED) && Abstract.argPath(act, 0) instanceof Name;
  }

  public static String labeledLabel(IAbstract act)
  {
    assert isLabeled(act);

    return Abstract.getId(Abstract.argPath(act, 0));
  }

  public static IAbstract labeledAction(IAbstract act)
  {
    assert isLabeled(act);

    return Abstract.argPath(act, 1);
  }

  public static IAbstract loop(Location loc, IAbstract act)
  {
    return new Apply(loc, Names.LOOP, act);
  }

  public static boolean isLoop(IAbstract act)
  {
    return Abstract.isUnary(act, Names.LOOP);
  }

  public static IAbstract loopAction(IAbstract act)
  {
    assert isLoop(act);

    return Abstract.argPath(act, 0);
  }

  public static IAbstract sync(Location loc, IAbstract obj, IAbstract act)
  {
    return new Apply(loc, Names.SYNC, obj, act);
  }

  public static boolean isSync(IAbstract act)
  {
    return Abstract.isBinary(act, Names.SYNC);
  }

  public static IAbstract syncAction(IAbstract act)
  {
    assert isSync(act);

    return Abstract.binaryRhs(act);
  }

  public static IAbstract syncObject(IAbstract act)
  {
    assert isSync(act);

    return Abstract.binaryLhs(act);
  }

  public static boolean isYield(IAbstract act)
  {
    return Abstract.isUnary(act, Names.YIELD);
  }

  public static IAbstract yieldedAction(IAbstract act)
  {
    assert isYield(act);

    return Abstract.unaryArg(act);
  }

  public static IAbstract yieldAction(Location loc, IAbstract act)
  {
    return Abstract.unary(loc, Names.YIELD, act);
  }

  public static IAbstract continew(Location loc, List<IAbstract> args)
  {
    return new Apply(loc, Names.CONTINUE, args);
  }

  public static IAbstract whileLoop(Location loc, IAbstract cond, IAbstract act)
  {
    return new Apply(loc, Names.WHILE, cond, act);
  }

  public static boolean isWhile(IAbstract act)
  {
    return Abstract.isBinary(act, Names.WHILE);
  }

  public static IAbstract whileTest(IAbstract act)
  {
    assert isWhile(act);

    return Abstract.argPath(act, 0);
  }

  public static IAbstract whileBody(IAbstract act)
  {
    assert isWhile(act);

    return Abstract.argPath(act, 1);
  }

  public static IAbstract leave(Location loc, String lbl)
  {
    return new Apply(loc, Names.LEAVE, new Name(loc, lbl));
  }

  public static boolean isLeave(IAbstract act)
  {
    return Abstract.isUnary(act, Names.LEAVE) && Abstract.argPath(act, 0) instanceof Name;
  }

  public static String leaveLabel(IAbstract act)
  {
    assert isLeave(act);

    return Abstract.getId(Abstract.argPath(act, 0));
  }

  public static IAbstract switchAction(Location loc, IAbstract sel, List<IAbstract> cases, IAbstract deflt)
  {
    return new Apply(loc, Names.SWITCH, sel, block(loc, cases), deflt);
  }

  public static boolean isSwitch(IAbstract trm)
  {
    return Abstract.isTernary(trm, Names.SWITCH) && isBlock(Abstract.argPath(trm, 1));
  }

  public static IAbstract switchSel(IAbstract trm)
  {
    assert isSwitch(trm);

    return Abstract.argPath(trm, 0);
  }

  public static IList switchCases(IAbstract trm)
  {
    assert isSwitch(trm);

    return blockContents(Abstract.argPath(trm, 1));
  }

  public static IAbstract switchDeflt(IAbstract trm)
  {
    assert isSwitch(trm);

    return Abstract.argPath(trm, 2);
  }

  public static IAbstract catchAction(Location loc, IAbstract body, IAbstract handler)
  {
    return new Apply(loc, Names.CATCH, body, handler);
  }

  public static IAbstract throwAction(Location loc, IAbstract exp)
  {
    return new Apply(loc, Names.THROW, exp);
  }

  public static IAbstract throwExp(Location loc, IAbstract exp)
  {
    return Abstract.unary(loc, Names.THROW, exp);
  }

  public static boolean isThrow(IAbstract trm)
  {
    return Abstract.isUnary(trm, Names.THROW);
  }

  public static IAbstract thrownExp(IAbstract trm)
  {
    assert isThrow(trm);

    return Abstract.argPath(trm, 0);
  }

  public static IAbstract caseExp(Location loc, IAbstract sel, List<IAbstract> cases, IAbstract deflt)
  {
    assert deflt != null;
    return new Apply(loc, Names.SWITCH, sel, block(loc, cases), deflt);
  }

  public static IAbstract caseRule(Location loc, IAbstract ptn, IAbstract body)
  {
    return new Apply(loc, Names.CASE, ptn, body);
  }

  public static boolean isCaseRule(IAbstract trm)
  {
    return Abstract.isBinary(trm, Names.CASE);
  }

  public static IAbstract caseRulePtn(IAbstract trm)
  {
    assert isCaseRule(trm);

    return Abstract.argPath(trm, 0);
  }

  public static IAbstract caseRuleBody(IAbstract trm)
  {
    assert isCaseRule(trm);

    return Abstract.argPath(trm, 1);
  }

  public static IAbstract match(Location loc, IAbstract exp, IAbstract ptn)
  {
    return new Apply(loc, Names.MATCH, exp, ptn);
  }

  public static boolean isMatch(IAbstract exp)
  {
    return Abstract.isBinary(exp, Names.MATCH);
  }

  public static IAbstract matchExp(IAbstract exp)
  {
    assert isMatch(exp);

    return Abstract.argPath(exp, 0);
  }

  public static IAbstract matchPtn(IAbstract exp)
  {
    assert isMatch(exp);

    return Abstract.argPath(exp, 1);
  }

  public static IAbstract conjunction(Location loc, IAbstract lhs, IAbstract rhs)
  {
    if (Abstract.isName(lhs, Names.TRUE))
      return rhs;
    else if (Abstract.isName(rhs, Names.TRUE))
      return lhs;
    else
      return new Apply(loc, Names.AND, lhs, rhs);
  }

  public static IAbstract disjunction(Location loc, IAbstract lhs, IAbstract rhs)
  {
    if (Abstract.isName(lhs, Names.FALSE))
      return rhs;
    else if (Abstract.isName(rhs, Names.FALSE))
      return lhs;
    else
      return new Apply(loc, Names.OR, lhs, rhs);
  }

  public static IAbstract negation(Location loc, IAbstract rhs)
  {
    return new Apply(loc, Names.NOT, rhs);
  }

  public static IAbstract valof(Location loc, IAbstract act)
  {
    return new Apply(loc, Names.VALOF, act);
  }

  public static boolean isValof(IAbstract trm)
  {
    return Abstract.isUnary(trm, Names.VALOF);
  }

  public static IAbstract valofAction(IAbstract trm)
  {
    assert isValof(trm);
    return Abstract.argPath(trm, 0);
  }

  public static IAbstract valis(Location loc, IAbstract exp)
  {
    return new Apply(loc, Names.VALIS, exp);
  }

  public static boolean isValis(IAbstract trm)
  {
    return Abstract.isUnary(trm, Names.VALIS);
  }

  public static IAbstract valisExp(IAbstract trm)
  {
    assert isValis(trm);
    return Abstract.argPath(trm, 0);
  }

  public static IAbstract typeDef(Location loc, IAbstract type, List<IAbstract> specs)
  {
    return new Apply(loc, Names.TYPE, type, CafeSyntax.block(loc, specs));
  }

  public static boolean isTypeDef(IAbstract trm)
  {
    return Abstract.isBinary(trm, Names.TYPE) && isBlock(Abstract.argPath(trm, 1));
  }

  public static IAbstract typeDefType(IAbstract trm)
  {
    assert isTypeDef(trm);
    return Abstract.argPath(trm, 0);
  }

  public static String typeDefName(IAbstract def)
  {
    assert isTypeDef(def);

    return typeExpLabel(Abstract.argPath(def, 0));
  }

  public static IList typeDefSpecs(IAbstract trm)
  {
    assert isTypeDef(trm);
    return blockContents(Abstract.argPath(trm, 1));
  }

  public static IAbstract typeExp(Location loc, String name, List<IAbstract> typeArgs)
  {
    return new Apply(loc, name, typeArgs);
  }

  public static IAbstract typeExp(Location loc, String name, IAbstract... typeArgs)
  {
    return new Apply(loc, name, typeArgs);
  }

  public static boolean isTypeExp(IAbstract tp)
  {
    return tp instanceof Name || (tp instanceof Apply && !(isTypeVar(tp) || isForallType(tp) || isExistsType(tp)));
  }

  public static String typeExpLabel(IAbstract tp)
  {
    assert isTypeExp(tp);

    if (Abstract.isName(tp))
      return Abstract.getId(tp);
    else
      return Abstract.getOp(tp);
  }

  public static IList typeExpArgs(IAbstract tp)
  {
    assert isTypeExp(tp);

    return Abstract.getArgs(tp);
  }

  public static IAbstract typeVar(Location loc, String name)
  {
    return new Apply(loc, Names.TYPEVAR, new Name(loc, name));
  }

  public static boolean isTypeFunVar(IAbstract trm)
  {
    return Abstract.isBinary(trm, StandardNames.DBLCENT) && Abstract.argPath(trm, 0) instanceof Name
        && Abstract.binaryRhs(trm) instanceof IntegerLiteral;
  }

  public static IAbstract typeFunVar(Location loc, String name, int arity)
  {
    return Abstract.binary(loc, StandardNames.DBLCENT, new Name(loc, name), new IntegerLiteral(loc, arity));
  }

  public static int typeFunArity(IAbstract tp)
  {
    assert isTypeFunVar(tp);
    return Abstract.getInt(Abstract.binaryRhs(tp));
  }

  public static String typeFunVarName(IAbstract tp)
  {
    assert isTypeFunVar(tp);
    return Abstract.getId(Abstract.binaryLhs(tp));
  }

  public static IAbstract typeVar(Location loc, String name, List<IAbstract> cons)
  {
    IAbstract tVar = typeVar(loc, name);

    if (!cons.isEmpty())
      tVar = new Apply(loc, Names.REQUIRING, tVar, block(loc, cons));

    return tVar;
  }

  public static boolean isTypeVar(IAbstract trm)
  {
    return (Abstract.isBinary(trm, Names.REQUIRING) && isBlock(Abstract.argPath(trm, 1)) && isTypeVar(Abstract.argPath(
        trm, 0)))
        || (Abstract.isUnary(trm, Names.TYPEVAR) && Abstract.argPath(trm, 0) instanceof Name);
  }

  public static String typeVarName(IAbstract trm)
  {
    while (Abstract.isBinary(trm, Names.REQUIRING))
      trm = Abstract.argPath(trm, 0);
    assert isTypeVar(trm);
    return ((Name) Abstract.argPath(trm, 0)).getId();
  }

  public static IList typeVarContracts(IAbstract trm)
  {
    assert isTypeVar(trm);

    if (Abstract.isBinary(trm, Names.REQUIRING))
      return blockContents(Abstract.argPath(trm, 1));
    else
      return null;
  }

  public static IAbstract arrowType(Location loc, List<IAbstract> typeArgs, IAbstract res)
  {
    List<IAbstract> args = new ArrayList<IAbstract>(typeArgs);
    args.add(res);
    return new Apply(loc, Names.ARROW, args);
  }

  public static boolean isArrowType(IAbstract tp)
  {
    tp = unwrapType(tp);
    return Abstract.isApply(tp, Names.ARROW) && ((Apply) tp).arity() > 0;
  }

  public static IAbstract arrowTypeArgs(IAbstract tp)
  {
    assert isArrowType(tp);
    tp = unwrapType(tp);
    return Abstract.binaryLhs(tp);
  }

  public static IAbstract arrowTypeRes(IAbstract tp)
  {
    assert isArrowType(tp);
    tp = unwrapType(tp);

    IList tArgs = ((Apply) tp).getArgs();

    return (IAbstract) tArgs.getCell(tArgs.size() - 1);
  }

  public static IAbstract procedureType(Location loc, List<IAbstract> argTypes)
  {
    List<IAbstract> args = new ArrayList<IAbstract>(argTypes);
    args.add(new Name(loc, Names.VOID));
    return new Apply(loc, Names.ARROW, args);
  }

  public static boolean isProcedureType(IAbstract tp)
  {
    return isArrowType(tp) && Abstract.isName(arrowTypeRes(tp), Names.VOID);
  }

  public static IAbstract patternType(Location loc, List<IAbstract> typeArgs, IAbstract res)
  {
    typeArgs.add(res);
    return new Apply(loc, Names.LARROW, typeArgs);
  }

  public static boolean isPatternType(IAbstract trm)
  {
    trm = unwrapType(trm);
    return Abstract.isApply(trm, Names.LARROW) && ((Apply) trm).arity() > 0;
  }

  public static List<IAbstract> patternTypeArgs(IAbstract trm)
  {
    assert isPatternType(trm);

    IList tArgs = ((Apply) trm).getArgs();
    List<IAbstract> args = new ArrayList<IAbstract>();
    for (int ix = 0; ix < tArgs.size() - 1; ix++)
      args.add((IAbstract) tArgs.getCell(ix));
    return args;
  }

  public static IAbstract patternTypePtn(IAbstract trm)
  {
    assert isPatternType(trm);
    trm = unwrapType(trm);

    IList args = ((Apply) trm).getArgs();

    return (IAbstract) args.getCell(args.size() - 1);
  }

  public static boolean isForallType(IAbstract tp)
  {
    return Abstract.isApply(tp, Names.FORALL, 2);
  }

  public static boolean isExistsType(IAbstract tp)
  {
    return Abstract.isApply(tp, Names.EXISTS, 2);
  }

  public static boolean isRawCharType(IAbstract tp)
  {
    return Abstract.isApply(tp, Names.RAW_CHAR_TYPE, 0);
  }

  public static boolean isRawStringType(IAbstract tp)
  {
    return Abstract.isApply(tp, Names.RAW_STRING_TYPE, 0);
  }

  public static boolean isRawIntegerType(IAbstract tp)
  {
    return Abstract.isApply(tp, Names.RAW_INT_TYPE, 0);
  }

  public static boolean isRawLongType(IAbstract tp)
  {
    return Abstract.isApply(tp, Names.RAW_LONG_TYPE, 0);
  }

  public static boolean isRawFloatType(IAbstract tp)
  {
    return Abstract.isApply(tp, Names.RAW_FLOAT_TYPE, 0);
  }

  public static boolean isRawDecimalType(IAbstract tp)
  {
    return Abstract.isApply(tp, Names.RAW_DECIMAL_TYPE, 0);
  }

  public static IAbstract tupleType(Location loc, List<IAbstract> argTypes)
  {
    return new Apply(loc, TypeUtils.tupleLabel(argTypes.size()), argTypes);
  }

  public static IAbstract tupleType(Location loc, IAbstract... argTypes)
  {
    return new Apply(loc, TypeUtils.tupleLabel(argTypes.length), argTypes);
  }

  public static Name variable(Location loc, String name)
  {
    return new Name(loc, name);
  }

  public static Name anonymous(Location loc)
  {
    return new Name(loc, GenSym.genSym(StandardNames.ANONYMOUS_PREFIX));
  }

  public static boolean isAnonymous(IAbstract trm)
  {
    if (isTypedTerm(trm))
      return isAnonymous(typedTerm(trm));
    else
      return trm instanceof Name && Utils.isAnonymous(((Name) trm).getId());
  }

  public static IAbstract constructor(Location loc, String label, List<IAbstract> args)
  {
    return Abstract.binary(loc, Names.CONSTRUCT, Abstract.name(loc, label), block(loc, args));
  }

  public static IAbstract constructor(Location loc, String label, IAbstract... args)
  {
    return Abstract.binary(loc, Names.CONSTRUCT, Abstract.name(loc, label), block(loc, args));
  }

  public static IAbstract constructor(Location loc, IAbstract label, IAbstract... args)
  {
    return Abstract.binary(loc, Names.CONSTRUCT, label, block(loc, args));
  }

  public static IAbstract constructor(Location loc, IAbstract label, IList args)
  {
    return Abstract.binary(loc, Names.CONSTRUCT, label, block(loc, args));
  }

  public static IAbstract constructor(Location loc, IAbstract label, List<IAbstract> args)
  {
    return Abstract.binary(loc, Names.CONSTRUCT, label, block(loc, args));
  }

  public static boolean isConstructor(IAbstract trm)
  {
    return Abstract.isBinary(trm, Names.CONSTRUCT) && isBlock(Abstract.binaryRhs(trm));
  }

  public static boolean isConstructor(IAbstract trm, String label)
  {
    return isConstructor(trm) && Abstract.isIdentifier(constructorName(trm), label);
  }

  public static String constructorOp(IAbstract trm)
  {
    assert isConstructor(trm);

    return Abstract.getId(Abstract.binaryLhs(trm));
  }

  public static IAbstract constructorName(IAbstract trm)
  {
    assert isConstructor(trm);

    return Abstract.binaryLhs(trm);
  }

  public static IList constructorArgs(IAbstract trm)
  {
    assert isConstructor(trm);

    return blockContents(Abstract.binaryRhs(trm));
  }

  public static IAbstract constructorArgTpl(IAbstract trm)
  {
    assert isConstructor(trm);

    return Abstract.binaryRhs(trm);
  }

  public static IAbstract tuple(Location loc, IAbstract type, IAbstract... els)
  {
    return constructor(loc, TypeUtils.tupleLabel(els.length), els);
  }

  public static boolean isTuple(IAbstract term)
  {
    return isConstructor(term) && TypeUtils.isTupleLabel(constructorOp(term));
  }

  public static boolean isTruth(IAbstract trm)
  {
    return isConstructor(trm, Names.TRUE);
  }

  public static IAbstract truth(Location loc)
  {
    return constructor(loc, Names.TRUE);
  }

  public static boolean isFalse(IAbstract trm)
  {
    return isConstructor(trm, Names.FALSE);
  }

  public static IAbstract falseness(Location loc)
  {
    return constructor(loc, Names.FALSE);
  }

  public static IAbstract existentialType(Location loc, IAbstract tVar, IAbstract bound)
  {
    return new Apply(loc, Names.EXISTS, tVar, bound);
  }

  public static boolean isExistentialType(IAbstract tp)
  {
    return Abstract.isBinary(tp, Names.EXISTS);
  }

  public static IAbstract existentialTypeVar(IAbstract tp)
  {
    assert isExistentialType(tp);
    return Abstract.binaryLhs(tp);
  }

  public static IAbstract existentialBoundType(IAbstract tp)
  {
    assert isExistentialType(tp);
    return Abstract.binaryRhs(tp);
  }

  public static IAbstract universalType(Location loc, IAbstract tVar, IAbstract bound)
  {
    return new Apply(loc, Names.FORALL, tVar, bound);
  }

  public static boolean isUniversalType(IAbstract tp)
  {
    return Abstract.isBinary(tp, Names.FORALL);
  }

  public static IAbstract universalBoundVar(IAbstract tp)
  {
    assert isUniversalType(tp);
    return Abstract.binaryLhs(tp);
  }

  public static IAbstract universalBoundType(IAbstract tp)
  {
    assert isUniversalType(tp);
    return Abstract.binaryRhs(tp);
  }

  public static IAbstract unwrapType(IAbstract tp)
  {
    while (isUniversalType(tp) || isExistentialType(tp)) {
      if (isUniversalType(tp))
        tp = universalBoundType(tp);
      else
        tp = existentialBoundType(tp);
    }
    return tp;
  }

  public static IAbstract constructorSpec(Location loc, String name, List<IAbstract> args)
  {
    return Abstract.binary(loc, Names.CONSPEC, Abstract.name(loc, name), block(loc, args));
  }

  public static boolean isConstructorSpec(IAbstract trm)
  {
    return Abstract.isBinary(trm, Names.CONSPEC) && Abstract.argPath(trm, 0) instanceof Name;
  }

  public static String constructorSpecLabel(IAbstract trm)
  {
    assert isConstructorSpec(trm);
    return Abstract.getId(Abstract.argPath(trm, 0));
  }

  public static IList constructorSpecArgs(IAbstract trm)
  {
    assert isConstructorSpec(trm);
    return blockContents(Abstract.argPath(trm, 1));
  }

  public static IAbstract record(Location loc, IAbstract name, List<IAbstract> args)
  {
    return Abstract.binary(loc, Names.RECORD, name, block(loc, args));
  }

  public static IAbstract record(Location loc, IAbstract name, IAbstract... args)
  {
    return Abstract.binary(loc, Names.RECORD, name, block(loc, args));
  }

  public static boolean isRecord(IAbstract trm)
  {
    return Abstract.isBinary(trm, Names.RECORD) && Abstract.binaryLhs(trm) instanceof Name
        && isBlock(Abstract.binaryRhs(trm));
  }

  public static String recordLabel(IAbstract trm)
  {
    return Abstract.getId(recordLbl(trm));
  }

  public static IAbstract recordLbl(IAbstract trm)
  {
    assert isRecord(trm);
    return Abstract.argPath(trm, 0);
  }

  public static IList recordArgs(IAbstract trm)
  {
    assert isRecord(trm);
    return blockContents(Abstract.binaryRhs(trm));
  }

  public static IAbstract face(Location loc, String label, List<IAbstract> args)
  {
    return Abstract.binary(loc, Names.FACE, new Name(loc, label), block(loc, args));
  }

  public static IAbstract face(Location loc, String label, IAbstract... args)
  {
    return Abstract.binary(loc, Names.FACE, new Name(loc, label), block(loc, args));
  }

  public static boolean isFace(IAbstract trm)
  {
    return Abstract.isBinary(trm, Names.FACE) && Abstract.binaryLhs(trm) instanceof Name
        && isBlock(Abstract.binaryRhs(trm));
  }

  public static String faceLabel(IAbstract trm)
  {
    return Abstract.getId(faceLbl(trm));
  }

  public static IAbstract faceLbl(IAbstract trm)
  {
    assert isFace(trm);
    return Abstract.binaryLhs(trm);
  }

  public static IList faceContents(IAbstract term)
  {
    assert isFace(term);
    return blockContents(Abstract.binaryRhs(term));
  }

  public static boolean isField(IAbstract trm)
  {
    return Abstract.isBinary(trm, StandardNames.EQUAL) && Abstract.binaryLhs(trm) instanceof Name;
  }

  public static IAbstract field(Location loc, String field, IAbstract value)
  {
    return Abstract.binary(loc, StandardNames.EQUAL, new Name(loc, field), value);
  }

  public static String fieldName(IAbstract trm)
  {
    assert isField(trm);

    return Abstract.getId(Abstract.binaryLhs(trm));
  }

  public static IAbstract fieldValue(IAbstract trm)
  {
    assert isField(trm);

    return Abstract.binaryRhs(trm);
  }

  public static IAbstract voidExp(Location loc)
  {
    return Abstract.name(loc, Names.VOID);
  }

  public static boolean isVoid(IAbstract exp)
  {
    return Abstract.isName(exp, Names.VOID);
  }

  public static IAbstract nullPtn(Location loc)
  {
    return Abstract.name(loc, Names.NULL_PTN);
  }

  public static boolean isNullPtn(IAbstract exp)
  {
    return Abstract.isName(exp, Names.NULL_PTN);
  }
}
