package org.star_lang.star.compiler;

import static org.star_lang.star.data.type.StandardTypes.booleanType;
import static org.star_lang.star.data.type.StandardTypes.integerType;
import static org.star_lang.star.data.type.StandardTypes.longType;
import static org.star_lang.star.data.type.StandardTypes.rawIntegerType;
import static org.star_lang.star.data.type.StandardTypes.rawLongType;
import static org.star_lang.star.data.type.StandardTypes.rawStringType;
import static org.star_lang.star.data.type.StandardTypes.stringType;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Predicate;

import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.ast.Apply;
import org.star_lang.star.compiler.ast.DefaultAbstractVisitor;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.ast.IAbstractVisitor;
import org.star_lang.star.compiler.ast.IntegerLiteral;
import org.star_lang.star.compiler.ast.Name;
import org.star_lang.star.compiler.ast.StringLiteral;
import org.star_lang.star.compiler.cafe.CafeSyntax;
import org.star_lang.star.compiler.canonical.Application;
import org.star_lang.star.compiler.canonical.Conjunction;
import org.star_lang.star.compiler.canonical.ConstructorPtn;
import org.star_lang.star.compiler.canonical.ConstructorTerm;
import org.star_lang.star.compiler.canonical.Disjunction;
import org.star_lang.star.compiler.canonical.FalseCondition;
import org.star_lang.star.compiler.canonical.ICondition;
import org.star_lang.star.compiler.canonical.IContentAction;
import org.star_lang.star.compiler.canonical.IContentExpression;
import org.star_lang.star.compiler.canonical.IContentPattern;
import org.star_lang.star.compiler.canonical.IsTrue;
import org.star_lang.star.compiler.canonical.MethodVariable;
import org.star_lang.star.compiler.canonical.NullAction;
import org.star_lang.star.compiler.canonical.Scalar;
import org.star_lang.star.compiler.canonical.ScalarPtn;
import org.star_lang.star.compiler.canonical.TrueCondition;
import org.star_lang.star.compiler.canonical.Variable;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.type.Dictionary;
import org.star_lang.star.compiler.type.TypeContracts;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.type.Visibility;
import org.star_lang.star.compiler.util.GenSym;
import org.star_lang.star.compiler.util.Wrapper;
import org.star_lang.star.data.IList;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.AbstractType;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.value.Option;

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

public class CompilerUtils {
  public static ICondition truth = new TrueCondition(Location.nullLoc);
  public static ICondition falseity = new FalseCondition(Location.nullLoc);

  public static Iterable<IAbstract> unWrap(final IAbstract term, final String operator) {
    return new Iterable<IAbstract>() {

      @Override
      public Iterator<IAbstract> iterator() {
        return new Iterator<IAbstract>() {

          IAbstract current = term;

          Stack<IAbstract> stack = new Stack<>();

          {
            while (current != null && (Abstract.isBinary(current, operator) || Abstract.isUnary(current, operator))) {
              if (Abstract.isBinary(current, operator))
                stack.push(Abstract.getArg(current, 1));
              current = Abstract.getArg(current, 0);
            }
          }

          @Override
          public boolean hasNext() {
            return current != null;
          }

          @Override
          public IAbstract next() {
            IAbstract next = current;

            if (!stack.isEmpty()) {
              current = stack.pop();
              while (Abstract.isBinary(current, operator) || Abstract.isUnary(current, operator)) {
                if (Abstract.isBinary(current, operator))
                  stack.push(Abstract.getArg(current, 1));
                current = Abstract.getArg(current, 0);
              }
            } else
              current = null;
            return next;
          }

          @Override
          public void remove() {
            throw new UnsupportedOperationException("not permitted");
          }
        };
      }
    };
  }

  public static Iterable<IAbstract> unWrap(IAbstract term) {
    return unWrap(term, StandardNames.TERM);
  }

  public static Iterable<IAbstract> reverseUnwrap(final IAbstract term, final String operator) {
    return new Iterable<IAbstract>() {

      @Override
      public Iterator<IAbstract> iterator() {
        return new Iterator<IAbstract>() {

          IAbstract current = term;

          Stack<IAbstract> stack = new Stack<>();

          {
            while (current != null && (Abstract.isBinary(current, operator) || Abstract.isUnary(current, operator))) {
              if (Abstract.isBinary(current, operator))
                stack.push(Abstract.binaryLhs(current));
              current = Abstract.binaryRhs(current);
            }
          }

          @Override
          public boolean hasNext() {
            return current != null;
          }

          @Override
          public IAbstract next() {
            IAbstract next = current;

            if (!stack.isEmpty()) {
              current = stack.pop();
              while (Abstract.isBinary(current, operator) || Abstract.isUnary(current, operator)) {
                if (Abstract.isBinary(current, operator))
                  stack.push(Abstract.binaryLhs(current));
                current = Abstract.binaryRhs(current);
              }
            } else
              current = null;
            return next;
          }

          @Override
          public void remove() {
            throw new UnsupportedOperationException("not permitted");
          }
        };
      }
    };
  }

  public static int count(final IAbstract term, final String operator) {
    int count = 0;

    Iterator<IAbstract> it = new Iterator<IAbstract>() {
      IAbstract current = term;

      Stack<IAbstract> stack = new Stack<>();

      {
        while (current != null && (Abstract.isBinary(current, operator) || Abstract.isUnary(current, operator))) {
          if (Abstract.isBinary(current, operator))
            stack.push(Abstract.getArg(current, 1));
          current = Abstract.getArg(current, 0);
        }
      }

      @Override
      public boolean hasNext() {
        return current != null;
      }

      @Override
      public IAbstract next() {
        IAbstract next = current;

        if (!stack.isEmpty()) {
          current = stack.pop();
          while (Abstract.isBinary(current, operator) || Abstract.isUnary(current, operator)) {
            if (Abstract.isBinary(current, operator))
              stack.push(Abstract.getArg(current, 1));
            current = Abstract.getArg(current, 0);
          }
        } else
          current = null;
        return next;
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException("not permitted");
      }
    };

    while (it.hasNext()) {
      count++;
      it.next();
    }
    return count;
  }

  public static boolean filterUnwrap(final IAbstract term, final String operator, Predicate<IAbstract> test) {
    for (IAbstract t : unWrap(term, operator)) {
      if (!test.test(t))
        return false;
    }
    return true;
  }

  public static IAbstract tupleUp(Location loc, String operator, List<IAbstract> els) {
    assert els.size() >= 1;

    IAbstract tuple = els.get(els.size() - 1);

    for (int ix = els.size() - 1; ix > 0; ix--)
      tuple = Abstract.binary(loc, operator, els.get(ix - 1), tuple);

    return tuple;
  }

  public static IAbstract tupleUp(Location loc, String operator, List<IAbstract> els, IAbstract last) {

    IAbstract tuple = last;

    for (int ix = els.size() - 1; ix >= 0; ix--)
      tuple = Abstract.binary(loc, operator, els.get(ix), tuple);

    return tuple;
  }

  public static IAbstract tupleUp(Location loc, String operator, IAbstract... els) {
    assert els.length >= 1;

    IAbstract tuple = els[els.length - 1];

    for (int ix = els.length - 1; ix > 0; ix--)
      tuple = Abstract.binary(loc, operator, els[ix - 1], tuple);

    return tuple;
  }

  public static void extendCondition(Wrapper<ICondition> condition, ICondition cond) {
    final ICondition current = condition.get();
    if (isTrivial(current))
      condition.set(cond);
    else if (!isTrivial(cond))
      condition.set(new Conjunction(current.getLoc(), current, cond));
  }

  public static ICondition conjunction(ICondition left, ICondition right) {
    if (isTrivial(left))
      return right;
    else if (isTrivial(right))
      return left;
    else
      return new Conjunction(left.getLoc(), left, right);
  }

  public static ICondition disjunction(ICondition left, ICondition right) {
    if (isTrivial(left))
      return truth;
    else if (isTrivial(right))
      return truth;
    else
      return new Disjunction(left.getLoc(), left, right);
  }

  public static void extendCondition(Wrapper<IAbstract> condition, IAbstract cond) {
    final IAbstract current = condition.get();
    if (isTrivial(current))
      condition.set(cond);
    else if (!isTrivial(cond))
      condition.set(Abstract.binary(cond.getLoc(), StandardNames.AND, current, cond));
  }

  public static void appendCondition(Wrapper<IAbstract> tgt, Wrapper<IAbstract> other) {
    if (!isTrivial(other.get()))
      extendCondition(tgt, other.get());
  }

  public static ICondition equals(Location loc, IContentExpression lhs, IContentExpression rhs) {
    IType type = lhs.getType();
    IType eqlType = TypeUtils.functionType(type, type, StandardTypes.booleanType);
    return new IsTrue(loc, new Application(loc, StandardTypes.booleanType, new MethodVariable(loc, StandardNames.EQUAL,
        eqlType, StandardNames.EQUALITY, TypeUtils.overloadedType(TypeUtils.tupleType(TypeUtils.typeExp(TypeContracts
        .contractImplTypeName(StandardNames.EQUALITY), type)), eqlType)), lhs, rhs));
  }

  public static boolean isEquality(ICondition cond) {
    if (cond instanceof IsTrue) {
      IContentExpression embedded = ((IsTrue) cond).getExp();
      if (embedded instanceof Application) {
        IContentExpression fun = ((Application) embedded).getFunction();
        if (fun instanceof MethodVariable) {
          MethodVariable method = (MethodVariable) fun;
          return method.getName().equals(StandardNames.EQUAL);
        }
      }
    }
    return false;
  }

  public static IContentExpression equalityLhs(ICondition cond) {
    assert isEquality(cond);

    return ((Application) ((IsTrue) cond).getExp()).getArg(0);
  }

  public static IContentExpression equalityRhs(ICondition cond) {
    assert isEquality(cond);

    return ((Application) ((IsTrue) cond).getExp()).getArg(1);
  }

  public static ICondition greaterEquals(Location loc, IContentExpression lhs, IContentExpression rhs) {
    IType type = lhs.getType();
    IType compType = TypeUtils.functionType(type, type, StandardTypes.booleanType);
    return new IsTrue(loc, new Application(loc, StandardTypes.booleanType, new MethodVariable(loc,
        StandardNames.GREATER_EQUAL, compType, StandardNames.COMPARABLE, TypeUtils.overloadedType(TypeUtils.tupleType(
        TypeUtils.typeExp(StandardNames.COMPARABLE, type)), compType)), lhs, rhs));
  }

  public static boolean isPackageIdentifier(IAbstract term) {
    term = Abstract.deParen(term);
    return Abstract.isName(term);
  }

  public static String getPackageIdentifier(IAbstract term) {
    if (term instanceof Name)
      return ((Name) term).getId();
    else if (Abstract.isParenTerm(term))
      return getPackageIdentifier(Abstract.deParen(term));
    else if (Abstract.isBinary(term, StandardNames.EXPORTS))
      return getPackageIdentifier(Abstract.binaryLhs(term));
    else if (term instanceof Apply)
      return ((Apply) term).getOp();
    else
      throw new IllegalArgumentException("expecting an identifier");
  }

  public static boolean isJavaStmt(IAbstract stmt) {
    return Abstract.isUnary(dePrivatize(stmt), StandardNames.JAVA);
  }

  public static String findJavaClassName(IAbstract el) {
    StringBuilder blder = new StringBuilder();
    findJavaClassName(el, blder);
    return blder.toString();
  }

  private static void findJavaClassName(IAbstract cls, StringBuilder blder) {
    if (Abstract.isBinary(cls, StandardNames.PERIOD)) {
      findJavaClassName(Abstract.binaryLhs(cls), blder);
      blder.append(".");
      findJavaClassName(Abstract.binaryRhs(cls), blder);
    } else if (Abstract.isParenTerm(cls))
      findJavaClassName(Abstract.deParen(cls), blder);
    else if (Abstract.isIdentifier(cls))
      blder.append(Abstract.getId(cls));
    else
      blder.append(cls.toString());
  }

  public static boolean isIdentifier(IAbstract term) {
    return term instanceof Name || (Abstract.isParenTerm(term) && Abstract.getArg(term, 0) instanceof Name);
  }

  public static boolean isIdentifier(IAbstract term, String name) {
    if (Abstract.isParenTerm(term))
      term = Abstract.getArg(term, 0);
    return term instanceof Name && ((Name) term).isIdentifier(name);
  }

  public static boolean isInteger(IAbstract term) {
    return term instanceof IntegerLiteral || (Abstract.isParenTerm(term) && Abstract.getArg(term,
        0) instanceof IntegerLiteral);
  }

  public static Integer getInteger(IAbstract term) {
    if (term instanceof IntegerLiteral)
      return ((IntegerLiteral) term).getLit();
    else if (Abstract.isParenTerm(term))
      return getInteger(Abstract.getArg(term, 0));
    else
      return null;
  }

  public static boolean isString(IAbstract term) {
    return term instanceof StringLiteral;
  }

  public static boolean isEmptyString(IAbstract term) {
    return term instanceof StringLiteral && ((StringLiteral) term).getLit().isEmpty();
  }

  public static boolean isApply(IAbstract term) {
    return term instanceof Apply && isIdentifier(((Apply) term).getOperator());
  }

  public static boolean isConditional(IAbstract term) {
    return Abstract.isBinary(term, StandardNames.COLON) && Abstract.isBinary(Abstract.binaryLhs(term),
        StandardNames.QUESTION);
  }

  public static IAbstract conditionalTest(IAbstract term) {
    assert isConditional(term);
    return Abstract.binaryLhs(Abstract.binaryLhs(term));
  }

  public static IAbstract conditionalThen(IAbstract term) {
    assert isConditional(term);
    return Abstract.binaryRhs(Abstract.binaryLhs(term));
  }

  public static IAbstract conditionalElse(IAbstract term) {
    assert isConditional(term);
    return Abstract.binaryRhs(term);
  }

  public static boolean isComputational(IContentExpression exp) {
    return !(exp instanceof Variable || exp instanceof Scalar);
  }

  public static boolean isTrivial(IContentExpression cond) {
    return cond == null || isTrueLiteral(cond);
  }

  public static boolean isTrivial(IAbstract cond) {
    return cond == null ||
        Abstract.isName(cond, StandardNames.TRUE) ||
        CafeSyntax.isConstructor(cond) && CafeSyntax.constructorOp(cond).equals(StandardNames.TRUE);
  }

  public static boolean isTrivialFunction(IAbstract term) {
    if (isEquation(term)) {
      IList args = getEquationArgs(term);
      IAbstract rhs = equationRhs(term);

      if (args.size() == 1 && args.getCell(0).equals(rhs))
        return true;
    }
    return false;
  }

  public static boolean isImport(IAbstract stmt) {
    if (isPrivate(stmt))
      stmt = privateTerm(stmt);
    if (Abstract.isUnary(stmt, StandardNames.IMPORT)) {
      IAbstract pkg = Abstract.deParen(Abstract.unaryArg(stmt));
      return pkg instanceof Name || pkg instanceof StringLiteral;
    }
    return false;
  }

  public static boolean isNamedImport(IAbstract stmt) {
    if (isPrivate(stmt))
      stmt = privateTerm(stmt);
    return Abstract.isBinary(stmt, StandardNames.IS) && isIdentifier(Abstract.binaryLhs(stmt)) && isImport(Abstract
        .binaryRhs(stmt));
  }

  public static IAbstract namedImportName(IAbstract stmt) {
    assert isNamedImport(stmt);
    if (isPrivate(stmt))
      stmt = privateTerm(stmt);
    return Abstract.binaryLhs(stmt);
  }

  public static IAbstract namedImportPkg(IAbstract stmt) {
    assert isNamedImport(stmt);
    if (isPrivate(stmt))
      stmt = privateTerm(stmt);
    return Abstract.deParen(Abstract.unaryArg(Abstract.binaryRhs(stmt)));
  }

  public static IAbstract importStmt(Location loc, IAbstract pkg) {
    return Abstract.unary(loc, StandardNames.IMPORT, pkg);
  }

  public static IAbstract namedImportStmt(Location loc, IAbstract var, IAbstract pkg) {
    return equation(loc, var, Abstract.unary(loc, StandardNames.IMPORT, pkg));
  }

  public static IAbstract importPkg(IAbstract stmt) {
    if (isPrivate(stmt))
      stmt = privateTerm(stmt);
    assert isImport(stmt);

    return Abstract.deParen(Abstract.unaryArg(stmt));
  }

  public static boolean isOpen(IAbstract stmt) {
    if (isPrivate(stmt))
      stmt = privateTerm(stmt);
    return Abstract.isUnary(stmt, StandardNames.OPEN);
  }

  public static IAbstract openedRecord(IAbstract stmt) {
    assert isOpen(stmt);
    if (isPrivate(stmt))
      stmt = privateTerm(stmt);
    return Abstract.unaryArg(stmt);
  }

  public static IAbstract openStmt(Location loc, IAbstract record) {
    return Abstract.unary(loc, StandardNames.OPEN, record);
  }

  public static boolean isPrivate(IAbstract def) {
    return Abstract.isUnary(def, StandardNames.PRIVATE);
  }

  public static Visibility privacy(IAbstract def) {
    if (Abstract.isUnary(def, StandardNames.PRIVATE))
      return Visibility.priVate;
    else
      return Visibility.pUblic;
  }

  public static IAbstract privateTerm(IAbstract def) {
    assert isPrivate(def);
    return Abstract.unaryArg(def);
  }

  public static IAbstract privateStmt(Location loc, Visibility visibility, IAbstract term) {
    switch (visibility) {
      case priVate:
        return Abstract.unary(loc, StandardNames.PRIVATE, term);
      default:
        return term;
    }
  }

  public static IAbstract privateStmt(Location loc, IAbstract term) {
    return Abstract.unary(loc, StandardNames.PRIVATE, term);
  }

  public static IAbstract dePrivatize(IAbstract def) {
    if (Abstract.isUnary(def, StandardNames.PRIVATE))
      return dePrivatize(Abstract.unaryArg(def));
    else
      return def;
  }

  public static boolean isRegexp(IAbstract term) {
    return Abstract.isUnary(term, StandardNames.REGEXP) && Abstract.getArg(term, 0) instanceof StringLiteral;
  }

  public static String regexpExp(IAbstract term) {
    assert isRegexp(term);
    return ((StringLiteral) Abstract.unaryArg(term)).getLit();
  }

  public static Location regexpLoc(IAbstract term) {
    assert isRegexp(term);
    return Abstract.unaryArg(term).getLoc();
  }

  public static boolean isEquals(IAbstract term) {
    return Abstract.isBinary(term, StandardNames.EQUAL);
  }

  public static IAbstract equalityLhs(IAbstract term) {
    assert isEquals(term);
    return Abstract.binaryLhs(term);
  }

  public static IAbstract equalityRhs(IAbstract term) {
    assert isEquals(term);
    return Abstract.binaryRhs(term);
  }

  public static IAbstract equals(Location loc, IAbstract lhs, IAbstract rhs) {
    return Abstract.binary(loc, StandardNames.EQUAL, lhs, rhs);
  }

  public static IAbstract regexp(Location loc, String exp) {
    return new Apply(loc, StandardNames.REGEXP, new StringLiteral(loc, exp));
  }

  public static boolean isTrivial(ICondition cond) {
    if (cond == null)
      return true;
    else if (cond instanceof IsTrue)
      return isTrueLiteral(((IsTrue) cond).getExp());
    else
      return cond instanceof TrueCondition;
  }

  public static boolean isTrivial(IContentAction action) {
    return action == null || action instanceof NullAction;
  }

  public static boolean isBraceTerm(IAbstract term, String name) {
    return (Abstract.isBinary(term, StandardNames.BRACES) && Abstract.isName(Abstract.binaryLhs(term), name))
        || (Abstract.isUnary(term, name) && Abstract.isName(Abstract.unaryArg(term), StandardNames.BRACES));
  }

  // check for foo{E1;..;En} or foo{}
  public static boolean isBraceTerm(IAbstract term) {
    return Abstract.isBinary(term, StandardNames.BRACES) || (Abstract.isUnary(term) && Abstract.isName(Abstract
        .unaryArg(term), StandardNames.BRACES));
  }

  // check for {} or {A1;..;An} = {}(';'(A1,..,An)..)
  public static boolean isBlockTerm(IAbstract term) {
    return isEmptyBlock(term) || Abstract.isUnary(term, StandardNames.BRACES);
  }

  public static boolean isEmptyBlock(IAbstract term) {
    return Abstract.isName(term, StandardNames.BRACES);
  }

  public static IAbstract blockTerm(Location loc, List<IAbstract> content) {
    if (content.isEmpty())
      return Abstract.name(loc, StandardNames.BRACES);
    else
      return Abstract.unary(loc, StandardNames.BRACES, tupleUp(loc, StandardNames.TERM, content));
  }

  public static IAbstract blockTerm(Location loc, IAbstract... content) {
    if (content.length == 0)
      return Abstract.name(loc, StandardNames.BRACES);
    else
      return Abstract.unary(loc, StandardNames.BRACES, tupleUp(loc, StandardNames.TERM, content));
  }

  public static IAbstract blockContent(IAbstract term) {
    assert isBlockTerm(term);
    if (isEmptyBlock(term))
      return null;
    else
      return Abstract.argPath(term, 0);
  }

  public static List<IAbstract> contentsOfBlock(IAbstract term) {
    assert isBlockTerm(term);

    if (isEmptyBlock(term))
      return new ArrayList<>();
    else
      return contentsOfTheta(Abstract.unaryArg(term));
  }

  public static List<IAbstract> contentsOfTheta(IAbstract term) {
    List<IAbstract> stmts = new ArrayList<>();

    if (term != null)
      checkBlockTerm(term, stmts);

    return stmts;
  }

  private static void checkBlockTerm(IAbstract term, List<IAbstract> stmts) {
    if (Abstract.isBinary(term, StandardNames.TERM)) {
      checkBlockTerm(Abstract.binaryLhs(term), stmts);
      checkBlockTerm(Abstract.binaryRhs(term), stmts);
    } else if (Abstract.isUnary(term, StandardNames.TERM))
      checkBlockTerm(Abstract.unaryArg(term), stmts);
    else
      stmts.add(term);
  }

  public static IAbstract emptyBrace(Location loc, String name) {
    return Abstract.unary(loc, name, new Name(loc, StandardNames.BRACES));
  }

  public static IAbstract emptyBrace(Location loc, IAbstract name) {
    return Abstract.unary(loc, name, new Name(loc, StandardNames.BRACES));
  }

  public static boolean isEmptyBrace(IAbstract term) {
    return Abstract.isUnary(term) && Abstract.isName(Abstract.unaryArg(term), StandardNames.BRACES);
  }

  public static IAbstract braceLabel(IAbstract term) {
    assert isBraceTerm(term);
    if (Abstract.isBinary(term, StandardNames.BRACES))
      return Abstract.binaryLhs(term);
    else
      return ((Apply) term).getOperator();
  }

  public static IAbstract braceArg(IAbstract term) {
    assert isBraceTerm(term);

    if (isEmptyBrace(term))
      return null;
    else
      return Abstract.binaryRhs(term);
  }

  public static IAbstract braceTerm(Location loc, IAbstract label, IAbstract... args) {
    if (args == null)
      return emptyBrace(loc, label);
    else
      return Abstract.binary(loc, StandardNames.BRACES, label, tupleUp(loc, StandardNames.TERM, args));
  }

  public static IAbstract braceTerm(Location loc, String label, List<IAbstract> args) {
    return braceTerm(loc, new Name(loc, label), args);
  }

  public static IAbstract braceTerm(Location loc, IAbstract label, List<IAbstract> args) {
    if (!args.isEmpty())
      return Abstract.binary(loc, StandardNames.BRACES, label, tupleUp(loc, StandardNames.TERM, args));
    else
      return emptyBrace(loc, label);
  }

  public static boolean isQueryTerm(IAbstract term) {
    if (Abstract.isBinary(term, StandardNames.OF) && (Abstract.isName(Abstract.binaryLhs(term)) || Abstract.isUnary(
        Abstract.binaryLhs(term), StandardNames.REDUCTION)) && CompilerUtils.isBlockTerm(Abstract.binaryRhs(term)))
      term = blockContent(Abstract.binaryRhs(term));

    return (Abstract.isBinary(term, StandardNames.ORDERBY) ||
        Abstract.isBinary(term, StandardNames.ORDERDESCENDINBY) ||
        Abstract.isBinary(term, StandardNames.DESCENDINGBY)) &&
        Abstract.isBinary(Abstract.binaryLhs(term), StandardNames.WHERE) ||
        Abstract.isBinary(term, StandardNames.WHERE);
  }

  public static IAbstract queryQuantifier(IAbstract term) {
    assert isQueryTerm(term);
    if (Abstract.isBinary(term, StandardNames.OF) && (Abstract.isName(Abstract.binaryLhs(term)) || Abstract.isUnary(
        Abstract.binaryLhs(term), StandardNames.REDUCTION)) && CompilerUtils.isBlockTerm(Abstract.binaryRhs(term)))
      term = blockContent(Abstract.binaryRhs(term));

    if ((Abstract.isBinary(term, StandardNames.ORDERBY) || Abstract.isBinary(term, StandardNames.ORDERDESCENDINBY)
        || Abstract.isBinary(term, StandardNames.DESCENDINGBY)) && Abstract.isBinary(Abstract.binaryLhs(term),
        StandardNames.WHERE))
      return Abstract.argPath(term, 0, 0, 0);
    else {
      assert Abstract.isBinary(term, StandardNames.WHERE);
      return Abstract.binaryLhs(term);
    }
  }

  public static IAbstract queryCondition(IAbstract term) {
    assert isQueryTerm(term);
    if (Abstract.isBinary(term, StandardNames.OF) && (Abstract.isName(Abstract.binaryLhs(term)) || Abstract.isUnary(
        Abstract.binaryLhs(term), StandardNames.REDUCTION)) && CompilerUtils.isBlockTerm(Abstract.binaryRhs(term)))
      term = blockContent(Abstract.binaryRhs(term));

    if ((Abstract.isBinary(term, StandardNames.ORDERBY) || Abstract.isBinary(term, StandardNames.ORDERDESCENDINBY)
        || Abstract.isBinary(term, StandardNames.DESCENDINGBY)) && Abstract.isBinary(Abstract.binaryLhs(term),
        StandardNames.WHERE))
      return Abstract.argPath(term, 0, 1);
    else if (Abstract.isBinary(term, StandardNames.WHERE))
      return Abstract.binaryRhs(term);
    else
      return null;
  }

  public static IAbstract queryOrderBy(IAbstract term) {
    assert isQueryTerm(term);
    if (Abstract.isBinary(term, StandardNames.OF) && (Abstract.isName(Abstract.binaryLhs(term)) || Abstract.isUnary(
        Abstract.binaryLhs(term), StandardNames.REDUCTION)) && CompilerUtils.isBlockTerm(Abstract.binaryRhs(term)))
      term = blockContent(Abstract.binaryRhs(term));

    if ((Abstract.isBinary(term, StandardNames.ORDERBY) || Abstract.isBinary(term, StandardNames.ORDERDESCENDINBY)
        || Abstract.isBinary(term, StandardNames.DESCENDINGBY))) {
      IAbstract order = Abstract.binaryRhs(term);
      if (Abstract.isBinary(order, StandardNames.USING))
        return Abstract.binaryLhs(order);
      else
        return order;
    } else
      return null;
  }

  public static IAbstract queryOrderUsing(IAbstract term) {
    assert isQueryTerm(term);
    if (Abstract.isBinary(term, StandardNames.OF) && (Abstract.isName(Abstract.binaryLhs(term)) || Abstract.isUnary(
        Abstract.binaryLhs(term), StandardNames.REDUCTION)) && isBlockTerm(Abstract.binaryRhs(term)))
      term = blockContent(Abstract.binaryRhs(term));

    if ((Abstract.isBinary(term, StandardNames.ORDERBY) || Abstract.isBinary(term, StandardNames.ORDERDESCENDINBY)
        || Abstract.isBinary(term, StandardNames.DESCENDINGBY))) {
      IAbstract order = Abstract.binaryRhs(term);
      if (Abstract.isBinary(order, StandardNames.USING))
        return Abstract.binaryRhs(order);
      else
        return order;
    } else
      return null;
  }

  public static boolean isSequenceTerm(IAbstract term) {
    return Abstract.isBinary(term, StandardNames.OF) && Abstract.isName(Abstract.binaryLhs(term)) &&
        CompilerUtils.isBlockTerm(Abstract.binaryRhs(term)) && !isQueryTerm(term);
  }

  public static String sequenceLabel(IAbstract term) {
    assert isSequenceTerm(term) || isLabeledSequenceTerm(term);

    return Abstract.getId(Abstract.binaryLhs(term));
  }

  public static boolean isMapTerm(IAbstract term) {
    return isBraceTerm(term, StandardNames.HASH) ||
        Abstract.isBinary(term, StandardNames.OF) && isBlockTerm(Abstract.binaryRhs(term));
  }

  public static IAbstract mapContents(IAbstract term) {
    assert isMapTerm(term);

    if (Abstract.isBinary(term, StandardNames.OF))
      return blockContent(Abstract.binaryRhs(term));
    else
      return braceArg(term);
  }

  // A field access expression
  public static boolean isFieldAccess(IAbstract term) {
    return Abstract.isBinary(term, StandardNames.PERIOD);
  }

  public static IAbstract fieldRecord(IAbstract term) {
    assert isFieldAccess(term);

    return Abstract.binaryLhs(term);
  }

  public static IAbstract fieldField(IAbstract term) {
    assert isFieldAccess(term);

    return Abstract.binaryRhs(term);
  }

  public static IAbstract fieldExp(Location loc, IAbstract record, IAbstract field) {
    return Abstract.binary(loc, StandardNames.PERIOD, record, field);
  }

  // A square term uses the [] as the binary operator... A[x] = A([](x))
  public static boolean isSquareTerm(IAbstract term) {
    return Abstract.isBinary(term, StandardNames.SQUARE);
  }

  public static IAbstract squareLabel(IAbstract term) {
    assert isSquareTerm(term);

    return Abstract.binaryLhs(term);
  }

  public static IAbstract squareArg(IAbstract term) {
    assert isSquareTerm(term);

    return Abstract.binaryRhs(term);
  }

  public static boolean isSquareSequenceTerm(IAbstract term) {
    return Abstract.isUnary(term, StandardNames.SQUARE) || Abstract.isIdentifier(term, StandardNames.SQUARE);
  }

  public static IAbstract squareContent(IAbstract term) {
    assert isSquareSequenceTerm(term);
    if (Abstract.isUnary(term, StandardNames.SQUARE))
      return Abstract.unaryArg(term);
    else
      return null;
  }

  public static boolean isLabeledSequenceTerm(IAbstract term) {
    return Abstract.isBinary(term, StandardNames.OF) && Abstract.isName(Abstract.binaryLhs(term))
        && isSquareSequenceTerm(Abstract.binaryRhs(term));
  }

  public static IAbstract labeledContent(IAbstract term) {
    assert isLabeledSequenceTerm(term);
    return squareContent(Abstract.binaryRhs(term));
  }

  // This is specially crafted to work with the replacement of P[Ix] in L to _index(P,Ix) in L

  public static boolean isIndexPattern(IAbstract term) {
    return Abstract.isUnary(term, StandardNames.SQUARE) && Abstract.isBinary(Abstract.unaryArg(term),
        StandardNames.COMMA);
  }

  public static IAbstract indexPttrnPtn(IAbstract term) {
    assert isIndexPattern(term);
    return Abstract.binaryLhs(Abstract.unaryArg(term));
  }

  public static IAbstract indexPttrnIx(IAbstract term) {
    assert isIndexPattern(term);
    return Abstract.binaryRhs(Abstract.unaryArg(term));
  }

  public static boolean isKeyValCond(IAbstract term) {
    return Abstract.isBinary(term, StandardNames.IN) && Abstract.isBinary(Abstract.deParen(Abstract.binaryLhs(term)),
        StandardNames.MAP_ARROW);
  }

  public static IAbstract keyValCondKey(IAbstract term) {
    assert isKeyValCond(term);

    return Abstract.binaryLhs(Abstract.deParen(Abstract.binaryLhs(term)));
  }

  public static IAbstract keyValCondVal(IAbstract term) {
    assert isKeyValCond(term);

    return Abstract.binaryRhs(Abstract.deParen(Abstract.binaryLhs(term)));
  }

  public static IAbstract keyValCondColl(IAbstract term) {
    assert isKeyValCond(term);

    return Abstract.binaryRhs(term);
  }

  public static boolean isRecordLiteral(IAbstract term) {
    if (isBraceTerm(term)) {
      for (IAbstract el : unWrap(braceArg(term), StandardNames.TERM)) {
        if (isPrivate(el))
          el = privateTerm(el);
        if (!(isProgramStmt(el) || isTypeStmt(el) || isTypeAnnotation(el) || isBlockTerm(el) || isImplementationStmt(
            el)))
          return false;
      }
      return true;
    } else
      return false;
  }

  public static Iterable<IAbstract> contentsOfRecord(IAbstract term) {
    return contentsOfTheta(braceArg(term));
  }

  public static boolean isThetaLiteral(IAbstract term) {
    if (isBlockTerm(term)) {
      for (IAbstract el : unWrap(blockContent(term), StandardNames.TERM)) {
        if (!(isProgramStmt(el) || isTypeStmt(el) || isTypeAnnotation(el) || isBlockTerm(el) || isImplementationStmt(
            el)))
          return false;
      }
      return true;
    }
    return false;
  }

  public static boolean isAttributeSpec(IAbstract term) {
    if (Abstract.isUnary(term, StandardNames.BRACES)) {
      for (IAbstract el : unWrap(Abstract.getArg(term, 0), StandardNames.TERM)) {
        if (!((Abstract.isBinary(el, StandardNames.COLON) || Abstract.isBinary(el, ":+")) && Abstract.getArg(el,
            0) instanceof Name))
          return false;
      }
      return true;
    } else
      return isIdentifier(term, StandardNames.BRACES);
  }

  public static IAbstract attributes(IAbstract term) {
    assert isAttributeSpec(term);
    if (Abstract.isUnary(term, StandardNames.BRACES))
      return Abstract.getArg(term, 0);
    else
      return null;
  }

  public static boolean isAnonAggConLiteral(IAbstract term) {
    return Abstract.isUnary(term, StandardNames.BRACES);
  }

  public static IAbstract anonAggEls(IAbstract term) {
    assert isAnonAggConLiteral(term);

    return Abstract.getArg(term, 0);
  }

  public static String anonRecordTypeLabel(IAbstract term) {
    assert isAnonAggConLiteral(term);
    Set<String> typeNames = new TreeSet<>();
    Set<String> labels = new TreeSet<>();

    for (IAbstract el : unWrap(blockContent(term), StandardNames.TERM))
      if (isTypeAnnotation(el) && isIdentifier(typeAnnotatedTerm(el))) {
        String name = Abstract.getId(typeAnnotatedTerm(el));
        labels.add(name);
      } else if (isKindAnnotation(el))
        typeNames.add(Abstract.getId(kindAnnotatedTerm(el)));

    StringBuilder bldr = new StringBuilder();
    bldr.append(StandardNames.RECORD_LABEL);
    int hash = 0;
    int arity = 0;

    for (String typeName : typeNames)
      hash = hash * 37 + typeName.hashCode();

    for (String entry : labels) {
      hash = hash * 37 + entry.hashCode();
      arity++;
    }
    bldr.append(Math.abs(hash));
    bldr.append("_");
    bldr.append(arity);

    return bldr.toString();
  }

  public static boolean isInterfaceType(IAbstract type) {
    if (isBlockTerm(type)) {
      for (IAbstract el : unWrap(blockContent(type))) {
        if (!isTypeAnnotation(el) && !isKindAnnotation(el) && !isTypeEquality(el))
          return false;
      }
      return true;
    } else
      return false;
  }

  public static IAbstract interfaceTypeElements(IAbstract type) {
    assert isInterfaceType(type);

    return blockContent(type);
  }

  public static boolean isLetTerm(IAbstract term) {
    return (Abstract.isUnary(term, StandardNames.LET) && Abstract.isBinary(Abstract.unaryArg(term), StandardNames.IN))
        || (Abstract.isBinary(term, StandardNames.USING) && isBlockTerm(Abstract.binaryRhs(term)));
  }

  public static List<IAbstract> letDefs(IAbstract term) {
    assert isLetTerm(term);

    if (Abstract.isUnary(term, StandardNames.LET) && Abstract.isBinary(Abstract.unaryArg(term), StandardNames.IN))
      return contentsOfBlock(Abstract.binaryLhs(Abstract.unaryArg(term)));
    else
      return contentsOfBlock(Abstract.binaryRhs(term));
  }

  public static IAbstract letBound(IAbstract term) {
    assert isLetTerm(term);
    if (Abstract.isUnary(term, StandardNames.LET) && Abstract.isBinary(Abstract.unaryArg(term), StandardNames.IN))
      return Abstract.binaryRhs(Abstract.unaryArg(term));
    else
      return Abstract.binaryLhs(term);
  }

  public static IAbstract letExp(Location loc, IAbstract defs, IAbstract bound) {
    return Abstract.unary(loc, StandardNames.LET, Abstract.binary(loc, StandardNames.IN, defs, bound));
  }

  public static boolean isValofExp(IAbstract term) {
    return Abstract.isUnary(term, StandardNames.VALOF) && CompilerUtils.isBlockTerm(Abstract.unaryArg(term));
  }

  public static IAbstract valofBody(IAbstract term) {
    assert isValofExp(term);
    return Abstract.unaryArg(term);
  }

  public static IAbstract valofExp(Location loc, IAbstract... stmts) {
    return Abstract.unary(loc, StandardNames.VALOF, blockTerm(loc, stmts));
  }

  public static IAbstract valofValis(Location loc, IAbstract exp, IAbstract... stmts) {
    IAbstract defs[] = new IAbstract[stmts.length + 1];
    System.arraycopy(stmts, 0, defs, 0, stmts.length);
    defs[stmts.length] = Abstract.unary(loc, StandardNames.VALIS, exp);
    return valofExp(loc, defs);
  }

  public static boolean isComputationExpression(IAbstract term) {
    return Abstract.isBinary(term, StandardNames.COMPUTATION) && isBlockTerm(Abstract.binaryRhs(term)) && Abstract
        .isIdentifier(Abstract.binaryLhs(term));
  }

  public static IAbstract computationType(IAbstract term) {
    assert isComputationExpression(term);

    return Abstract.deParen(Abstract.binaryLhs(term));
  }

  public static IAbstract computationBody(IAbstract term) {
    assert isComputationExpression(term);
    return CompilerUtils.blockContent(Abstract.binaryRhs(term));
  }

  public static boolean isRunComputation(IAbstract term) {
    return Abstract.isUnary(term, StandardNames.VALOF) && !isBlockTerm(Abstract.unaryArg(term));
  }

  public static IAbstract runComputation(IAbstract term) {
    assert isRunComputation(term);

    IAbstract comp = Abstract.unaryArg(term);

    if (Abstract.isBinary(comp, StandardNames.ON_ABORT))
      return Abstract.binaryLhs(comp);
    else
      return comp;
  }

  public static IAbstract runCompAbort(IAbstract term) {
    assert isRunComputation(term);

    IAbstract comp = Abstract.unaryArg(term);

    if (Abstract.isBinary(comp, StandardNames.ON_ABORT))
      return Abstract.binaryRhs(comp);
    else
      return Abstract.name(term.getLoc(), StandardNames.RAISE_FUN);
  }

  public static boolean isPerformAction(IAbstract term) {
    if (Abstract.isUnary(term, StandardNames.PERFORM)) {
      IAbstract performed = Abstract.unaryArg(term);
      return !Abstract.isBinary(performed, StandardNames.ON_ABORT) || isBlockTerm(Abstract.binaryRhs(performed));
    }
    return false;
  }

  public static boolean isBasicPerform(IAbstract term) {
    return Abstract.isUnary(term, StandardNames.PERFORM) && !Abstract.isBinary(Abstract.unaryArg(term),
        StandardNames.ON_ABORT);
  }

  public static IAbstract performedAction(IAbstract term) {
    assert isPerformAction(term);
    term = Abstract.unaryArg(term);
    if (Abstract.isBinary(term, StandardNames.ON_ABORT))
      return Abstract.binaryLhs(term);
    else
      return term;
  }

  public static IAbstract performedAbort(IAbstract term) {
    assert isPerformAction(term);
    term = Abstract.unaryArg(term);
    if (Abstract.isBinary(term, StandardNames.ON_ABORT))
      return blockContent(Abstract.binaryRhs(term));
    else {
      Location loc = term.getLoc();
      Name XX = Abstract.name(loc, GenSym.genSym("$X"));
      return defaultCaseRule(loc, XX, raise(loc, XX));
    }
  }

  public static boolean isLambdaExp(IAbstract term) {
    return Abstract.isBinary(term, StandardNames.FUN_ARROW) && (Abstract.isUnary(Abstract.binaryLhs(term),
        StandardNames.LAMBDA) || Abstract.isTupleTerm(Abstract.binaryLhs(term)));
  }

  public static IAbstract lambdaExp(IAbstract term) {
    assert isLambdaExp(term);

    return Abstract.binaryRhs(term);
  }

  public static IAbstract lambdaPtn(IAbstract term) {
    assert isLambdaExp(term);

    IAbstract lhs = Abstract.binaryLhs(term);

    if (Abstract.isTupleTerm(lhs))
      return lhs;
    else
      return Abstract.unaryArg(lhs);
  }

  public static int lambdaArity(IAbstract term) {
    assert isLambdaExp(term);
    IAbstract lhs = lambdaPtn(term);
    if (Abstract.isBinary(lhs, StandardNames.WHERE))
      lhs = Abstract.binaryLhs(lhs);
    if (Abstract.isTupleTerm(lhs))
      return Abstract.tupleArity(lhs);
    else
      return 1;
  }

  public static IAbstract lambda(Location loc, IAbstract lhs, IAbstract rhs) {
    return Abstract.binary(loc, StandardNames.FUN_ARROW, lhs, rhs);
  }

  public static boolean isMemoExp(IAbstract term) {
    return Abstract.isUnary(term, StandardNames.MEMO);
  }

  public static IAbstract convertToLambda(IAbstract rules) {
    if (Abstract.isBinary(rules, StandardNames.PIPE))
      return Abstract.binary(rules.getLoc(), StandardNames.PIPE, convertToLambda(Abstract.binaryLhs(rules)),
          convertToLambda(Abstract.binaryRhs(rules)));
    else if (Abstract.isBinary(rules, StandardNames.IS))
      return Abstract.binary(rules.getLoc(), StandardNames.FUN_ARROW, convertLambdaHead(Abstract.binaryLhs(rules)),
          Abstract.binaryRhs(rules));
    else
      return rules;
  }

  private static IAbstract convertLambdaHead(IAbstract term) {
    if (Abstract.isBinary(term, StandardNames.WHERE))
      return Abstract.binary(term.getLoc(), StandardNames.WHERE, convertLambdaHead(Abstract.binaryLhs(term)), Abstract
          .binaryRhs(term));
    else if (Abstract.isUnary(term, StandardNames.DEFAULT))
      return convertLambdaHead(Abstract.unaryArg(term));
    else if (Abstract.isApply(term))
      return Abstract.tupleTerm(term.getLoc(), ((Apply) term).getArgs());
    else
      return term;
  }

  public static IAbstract memoTerm(Location loc, IAbstract trm) {
    return Abstract.unary(loc, StandardNames.MEMO, trm);
  }

  public static IAbstract memoedTerm(IAbstract trm) {
    assert isMemoExp(trm);
    return Abstract.unaryArg(trm);
  }

  public static boolean isCaseTerm(IAbstract term) {
    return Abstract.isUnary(term, StandardNames.SWITCH) && Abstract.isBinary(Abstract.unaryArg(term), StandardNames.IN)
        && isBlockTerm(Abstract.binaryRhs(Abstract.unaryArg(term)));
  }

  public static boolean isCaseExp(IAbstract term) {
    return isCaseTerm(term);
  }

  public static boolean isCaseAction(IAbstract term) {
    return isCaseTerm(term);
  }

  public static IAbstract caseTerm(Location loc, IAbstract sel, List<IAbstract> cases) {
    IAbstract cses = blockTerm(loc, cases);
    return Abstract.unary(loc, StandardNames.SWITCH, Abstract.binary(loc, StandardNames.IN, sel, cses));
  }

  public static IAbstract caseSel(IAbstract term) {
    assert isCaseTerm(term);
    term = Abstract.unaryArg(term);
    return Abstract.binaryLhs(term);
  }

  public static IAbstract caseRules(IAbstract term) {
    assert isCaseTerm(term);
    term = Abstract.unaryArg(term);
    return blockContent(Abstract.binaryRhs(term));
  }

  public static boolean isCaseRule(IAbstract term) {
    return Abstract.isUnary(term, StandardNames.CASE) && (Abstract.isBinary(Abstract.unaryArg(term), StandardNames.IS)
        || Abstract.isBinary(Abstract.unaryArg(term), StandardNames.DO));
  }

  public static boolean isDefaultCaseRule(IAbstract term) {
    return isCaseRule(term) && Abstract.isUnary(Abstract.binaryLhs(Abstract.unaryArg(term)), StandardNames.DEFAULT);
  }

  public static IAbstract caseRulePtn(IAbstract term) {
    assert isCaseRule(term);
    return Abstract.binaryLhs(Abstract.unaryArg(term));
  }

  public static IAbstract caseDefaultRulePtn(IAbstract term) {
    assert isDefaultCaseRule(term);
    return Abstract.unaryArg(Abstract.binaryLhs(Abstract.unaryArg(term)));
  }

  public static IAbstract caseRuleValue(IAbstract term) {
    assert isCaseRule(term);
    return Abstract.binaryRhs(Abstract.unaryArg(term));
  }

  public static IAbstract caseRule(Location loc, IAbstract ptn, IAbstract value) {
    return Abstract.unary(loc, StandardNames.CASE, Abstract.binary(loc, StandardNames.IS, ptn, value));
  }

  public static IAbstract defaultCaseRule(Location loc, IAbstract ptn, IAbstract value) {
    return caseRule(loc, Abstract.unary(loc, StandardNames.DEFAULT, ptn), value);
  }

  public static boolean isDefaultRule(IAbstract term) {
    return (Abstract.isBinary(term, StandardNames.IS) ||
        Abstract.isBinary(term, StandardNames.DO) ||
        Abstract.isBinary(term, StandardNames.ASSIGN)) &&
        Abstract.isUnary(Abstract.binaryLhs(term), StandardNames.DEFAULT);
  }

  public static IAbstract defaultRulePtn(IAbstract term) {
    assert isDefaultRule(term);
    return Abstract.unaryArg(Abstract.binaryLhs(term));
  }

  public static IAbstract defaultRuleValue(IAbstract term) {
    assert isDefaultRule(term);
    return Abstract.binaryRhs(term);
  }

  public static boolean isAssert(IAbstract term) {
    return Abstract.isUnary(term, StandardNames.ASSERT);
  }

  public static IAbstract asserted(IAbstract term) {
    assert isAssert(term);
    return Abstract.unaryArg(term);
  }

  public static IAbstract assertion(Location loc, IAbstract term) {
    return Abstract.unary(loc, StandardNames.ASSERT, term);
  }

  public static boolean isIgnore(IAbstract term) {
    return Abstract.isUnary(term, StandardNames.IGNORE);
  }

  public static IAbstract ignored(IAbstract term) {
    assert isIgnore(term);
    return Abstract.unaryArg(term);
  }

  public static boolean isAssignment(IAbstract term) {
    return Abstract.isBinary(term, StandardNames.ASSIGN);
  }

  public static IAbstract assignedVar(IAbstract term) {
    assert isAssignment(term);
    return Abstract.binaryLhs(term);
  }

  public static IAbstract assignedValue(IAbstract term) {
    assert isAssignment(term);
    return Abstract.binaryRhs(term);
  }

  public static boolean isCast(IAbstract term) {
    return Abstract.isBinary(term, StandardNames.CAST);
  }

  public static IAbstract castExp(IAbstract term) {
    assert isCast(term);
    return Abstract.binaryLhs(term);
  }

  public static IAbstract castType(IAbstract term) {
    assert isCast(term);
    return Abstract.binaryRhs(term);
  }

  public static boolean isCoerce(IAbstract term) {
    return Abstract.isBinary(term, StandardNames.AS);
  }

  public static IAbstract coercedExp(IAbstract term) {
    assert isCoerce(term);
    return Abstract.binaryLhs(term);
  }

  public static IAbstract coercedType(IAbstract term) {
    assert isCoerce(term);
    return Abstract.binaryRhs(term);
  }

  public static boolean isDefaultExp(IAbstract term) {
    return Abstract.isBinary(term, StandardNames.OR_ELSE);
  }

  public static IAbstract defaultExpDefault(IAbstract term) {
    assert isDefaultExp(term);
    return Abstract.binaryRhs(term);
  }

  public static IAbstract defaultExpNormal(IAbstract term) {
    assert isDefaultExp(term);
    return Abstract.binaryLhs(term);
  }

  public static boolean isQuoted(IAbstract term) {
    return Abstract.isUnary(term, StandardNames.QUOTE);
  }

  public static IAbstract quotedExp(IAbstract term) {
    assert isQuoted(term);
    return Abstract.unaryArg(term);
  }

  public static boolean isUnQuoted(IAbstract term) {
    return Abstract.isUnary(term, StandardNames.UNQUOTE) || Abstract.isUnary(term, StandardNames.QUESTION);
  }

  public static boolean isQuestion(IAbstract term) {
    return Abstract.isUnary(term, StandardNames.QUESTION) && Abstract.isIdentifier(Abstract.unaryArg(term));
  }

  public static IAbstract unquotedExp(IAbstract term) {
    assert isUnQuoted(term);
    return Abstract.unaryArg(term);
  }

  public static boolean isRef(IAbstract term) {
    return Abstract.isUnary(term, StandardNames.REF);
  }

  public static boolean isReference(IAbstract term) {
    return Abstract.isUnary(term, StandardNames.REF);
  }

  public static IAbstract referencedTerm(IAbstract term) {
    assert isReference(term);
    return Abstract.unaryArg(term);
  }

  public static boolean isShriek(IAbstract term) {
    return Abstract.isUnary(term, StandardNames.SHRIEK);
  }

  public static IAbstract shriekTerm(IAbstract term) {
    assert isShriek(term);
    return Abstract.unaryArg(term);
  }

  public static IAbstract shriekTerm(Location loc, IAbstract exp) {
    return Abstract.unary(loc, StandardNames.SHRIEK, exp);
  }

  public static boolean isVarPtn(IAbstract term) {
    return Abstract.isUnary(term, StandardNames.VAR) && Abstract.deParen(Abstract.unaryArg(term)) instanceof Name;
  }

  public static IAbstract varPtnVar(IAbstract term) {
    assert isVarPtn(term);
    return Abstract.deParen(Abstract.unaryArg(term));
  }

  public static IAbstract varPtn(IAbstract exp) {
    assert Abstract.deParen(exp) instanceof Name;
    return Abstract.unary(exp.getLoc(), StandardNames.VAR, exp);
  }

  public static boolean isForLoop(IAbstract act) {
    return Abstract.isBinary(act, StandardNames.DO) && Abstract.isUnary(Abstract.binaryLhs(act), StandardNames.FOR);
  }

  public static IAbstract forLoopCond(IAbstract act) {
    assert isForLoop(act);
    return Abstract.unaryArg(Abstract.binaryLhs(act));
  }

  public static IAbstract forLoopBody(IAbstract act) {
    assert isForLoop(act);
    return Abstract.binaryRhs(act);
  }

  public static boolean isRaise(IAbstract term) {
    return Abstract.isUnary(term, StandardNames.RAISE);
  }

  public static IAbstract raisedException(IAbstract term) {
    assert isRaise(term);

    IAbstract ex = Abstract.unaryArg(term);
    if (Abstract.isBinary(ex, StandardNames.COLON))
      return Abstract.binaryRhs(ex);
    else
      return ex;
  }

  public static IAbstract raisedCode(IAbstract term) {
    assert isRaise(term);

    term = Abstract.unaryArg(term);
    if (Abstract.isBinary(term, StandardNames.COLON))
      return Abstract.binaryLhs(term);
    else
      return new Name(term.getLoc(), StandardTypes.NON_STRING);
  }

  public static IAbstract raise(Location loc, IAbstract term) {
    return Abstract.unary(loc, StandardNames.RAISE, term);
  }

  public static IAbstract raise(Location loc, IAbstract code, IAbstract term) {
    return Abstract.unary(loc, StandardNames.RAISE, Abstract.binary(loc, StandardNames.COLON, code, term));
  }

  public static boolean isAbortHandler(IAbstract act) {
    return Abstract.isUnary(act, StandardNames.TRY) && Abstract.isBinary(Abstract.unaryArg(act), StandardNames.ON_ABORT)
        && isBlockTerm(Abstract.binaryRhs(Abstract.unaryArg(act)));
  }

  public static IAbstract abortHandlerBody(IAbstract act) {
    assert isAbortHandler(act);
    return Abstract.binaryLhs(Abstract.unaryArg(act));
  }

  public static IAbstract abortHandlerHandler(IAbstract act) {
    assert isAbortHandler(act);
    return blockContent(Abstract.binaryRhs(Abstract.unaryArg(act)));
  }

  public static boolean isPackageStmt(IAbstract stmt) {
    return Abstract.isBinary(stmt, StandardNames.IS) && CompilerUtils.isBraceTerm(Abstract.binaryRhs(stmt),
        StandardNames.PACKAGE);
  }

  public static IAbstract packageName(IAbstract stmt) {
    assert isPackageStmt(stmt);
    return Abstract.binaryLhs(stmt);
  }

  public static IAbstract packageContents(IAbstract stmt) {
    assert isPackageStmt(stmt);
    return braceArg(Abstract.binaryRhs(stmt));
  }

  public static IAbstract packageStmt(Location loc, IAbstract name, List<IAbstract> stmts) {
    return equation(loc, name, braceTerm(loc, StandardNames.PACKAGE, stmts));
  }

  public static Name functionName(IAbstract term) {
    assert isFunctionStatement(term);

    IAbstract name = nameOfFunction(term);

    if (name instanceof Name)
      return (Name) name;
    else
      throw new IllegalArgumentException("expecting a function name");
  }

  public static Name procedureName(IAbstract term) {
    assert isProcedureStatement(term);

    IAbstract name = nameOfProcedure(term);

    if (name instanceof Name)
      return (Name) name;
    else
      throw new IllegalArgumentException("expecting a procedure term");
  }

  public static Name patternName(IAbstract stmt) {
    IAbstract name = nameOfPattern(stmt);

    if (name instanceof Name)
      return (Name) name;
    else
      throw new IllegalArgumentException("expecting a pattern abstraction");
  }

  public static boolean isProgramStmt(IAbstract stmt) {
    if (isPrivate(stmt))
      return isProgramStmt(privateTerm(stmt));
    else
      return isFunctionStatement(stmt) || isProcedureStatement(stmt) || isPatternStatement(stmt) || isVarDeclaration(
          stmt) || isIsStatement(stmt) || isOpen(stmt);
  }

  public static boolean isVarDeclaration(IAbstract term) {
    if (isPrivate(term))
      return isVarDeclaration(privateTerm(term));
    else
      return Abstract.isUnary(term, StandardNames.VAR) && Abstract.isBinary(Abstract.unaryArg(term),
          StandardNames.ASSIGN);
  }

  public static IAbstract varDeclarationPattern(IAbstract term) {
    assert isVarDeclaration(term);

    if (isPrivate(term))
      return varDeclarationPattern(privateTerm(term));
    else if ((Abstract.isUnary(term, StandardNames.VAR)) && Abstract.isBinary(Abstract.unaryArg(term),
        StandardNames.ASSIGN))
      return Abstract.unary(term.getLoc(), StandardNames.VAR, Abstract.binaryLhs(Abstract.unaryArg(term)));
    else
      return null;
  }

  public static IAbstract varDeclarationExpression(IAbstract term) {
    assert isVarDeclaration(term);

    if (isPrivate(term))
      return varDeclarationExpression(privateTerm(term));
    else if ((Abstract.isUnary(term, StandardNames.VAR)) && Abstract.isBinary(Abstract.unaryArg(term),
        StandardNames.ASSIGN))
      return Abstract.binaryRhs(Abstract.unaryArg(term));
    else
      return null;
  }

  public static boolean isContractSpec(IAbstract term) {
    term = unwrapQuants(term);
    if (Abstract.isBinary(term, StandardNames.WHERE))
      return isContractSpec(Abstract.binaryLhs(term));
    else if (Abstract.isParenTerm(term))
      return isContractSpec(Abstract.deParen(term));
    else
      return Abstract.isBinary(term, StandardNames.OVER) && Abstract.isIdentifier(Abstract.binaryLhs(term));
  }

  public static IAbstract contractSpecName(IAbstract term) {
    assert isContractSpec(term);

    term = unwrapQuants(term);

    if (Abstract.isBinary(term, StandardNames.WHERE))
      return contractSpecName(Abstract.binaryLhs(term));
    else if (Abstract.isParenTerm(term))
      return contractSpecName(Abstract.deParen(term));
    else if (Abstract.isBinary(term, StandardNames.OVER) && Abstract.isIdentifier(Abstract.binaryLhs(term)))
      return Abstract.binaryLhs(term);
    else
      return null;
  }

  public static IAbstract contractSpecType(IAbstract term) {
    assert isContractSpec(term);

    term = unwrapQuants(term);

    if (Abstract.isBinary(term, StandardNames.WHERE))
      return contractSpecName(Abstract.binaryLhs(term));
    else if (Abstract.isParenTerm(term))
      return contractSpecName(Abstract.deParen(term));
    else if (Abstract.isBinary(term, StandardNames.OVER))
      return Abstract.binaryRhs(term);
    else
      return null;
  }

  public static boolean isContractStmt(IAbstract exp) {
    if (Abstract.isUnary(exp, StandardNames.CONTRACT)) {
      IAbstract conForm = Abstract.unaryArg(exp);
      if (Abstract.isBinary(conForm, StandardNames.IS))
        return isContractSpec(Abstract.binaryLhs(conForm));
    }
    return false;
  }

  public static IAbstract contractForm(IAbstract trm) {
    assert isContractStmt(trm);

    return Abstract.deParen(Abstract.binaryLhs(Abstract.unaryArg(trm)));
  }

  public static IAbstract contractName(IAbstract stmt) {
    assert isContractStmt(stmt);

    return contractSpecName(contractForm(stmt));
  }

  public static IAbstract contractSpec(IAbstract trm) {
    assert isContractStmt(trm);

    return blockContent(Abstract.binaryRhs(Abstract.unaryArg(trm)));
  }

  public static boolean isFallbackImplementationStmt(IAbstract term) {
    if (Abstract.isUnary(term, StandardNames.IMPLEMENTATION)) {
      IAbstract t = Abstract.unaryArg(term);
      return Abstract.isBinary(t, StandardNames.IS) && Abstract.isUnary(Abstract.binaryLhs(t), StandardNames.DEFAULT);
    }
    return false;
  }

  public static boolean isImplementationStmt(IAbstract term) {
    if (Abstract.isUnary(term, StandardNames.IMPLEMENTATION) && Abstract.isBinary(Abstract.unaryArg(term),
        StandardNames.IS)) {
      IAbstract lhs = Abstract.binaryLhs(Abstract.unaryArg(term));
      if (Abstract.isUnary(lhs, StandardNames.DEFAULT))
        lhs = Abstract.unaryArg(lhs);
      return isContractSpec(lhs);
    }

    return false;
  }

  public static IAbstract implementationStmt(Location loc, IAbstract con, IAbstract def) {
    assert isContractSpec(con);

    return Abstract.unary(loc, StandardNames.IMPLEMENTATION, Abstract.binary(loc, StandardNames.IS, con, def));
  }

  public static IAbstract implementedContractSpec(IAbstract term) {
    assert isImplementationStmt(term);
    assert Abstract.isUnary(term, StandardNames.IMPLEMENTATION);
    term = Abstract.unaryArg(term);
    assert Abstract.isBinary(term, StandardNames.IS);
    term = Abstract.binaryLhs(term);
    if (Abstract.isUnary(term, StandardNames.DEFAULT))
      return Abstract.unaryArg(term);
    else
      return term;
  }

  public static IAbstract implementedContract(IAbstract term) {
    assert isImplementationStmt(term);
    IAbstract spec = unwrapQuants(implementedContractSpec(term));

    while (Abstract.isBinary(spec, StandardNames.WHERE))
      spec = Abstract.binaryLhs(spec);
    if (Abstract.isBinary(spec, StandardNames.OVER))
      return Abstract.binaryLhs(spec);
    else
      return null;
  }

  public static String implementedContractName(IAbstract term) {
    IAbstract spec = unwrapQuants(implementedContractSpec(term));

    boolean flag = true;
    while (flag) {
      flag = false;
      if (Abstract.isBinary(spec, StandardNames.WHERE)) {
        flag = true;
        spec = Abstract.binaryLhs(spec);
      } else if (Abstract.isBinary(spec, StandardNames.OVER)) {
        flag = true;
        spec = Abstract.deParen(Abstract.binaryLhs(spec));
      } else if (Abstract.isBinary(spec, StandardNames.DETERMINES)) {
        flag = true;
        spec = Abstract.binaryLhs(spec);
      }
    }
    assert Abstract.isIdentifier(spec);
    return Abstract.getId(spec);
  }

  public static IAbstract implementationContractType(IAbstract term) {
    assert isImplementationStmt(term);
    IAbstract spec = unwrapQuants(implementedContractSpec(term));

    boolean flag = true;
    while (flag) {
      flag = false;
      if (Abstract.isBinary(spec, StandardNames.WHERE)) {
        flag = true;
        spec = Abstract.binaryLhs(spec);
      } else if (Abstract.isBinary(spec, StandardNames.OVER)) {
        flag = true;
        spec = Abstract.deParen(Abstract.binaryRhs(spec));
      } else if (Abstract.isBinary(spec, StandardNames.DETERMINES)) {
        flag = true;
        spec = Abstract.binaryLhs(spec);
      }
    }

    return spec;
  }

  public static String implementationContractTypeName(IAbstract stmt) {
    assert isImplementationStmt(stmt);

    return typeLabel(implementationContractType(stmt));
  }

  public static IAbstract implementationBody(IAbstract term) {
    assert isImplementationStmt(term);

    return Abstract.binaryRhs(Abstract.unaryArg(term));
  }

  public static boolean isImplementationDeclaration(IAbstract term) {
    if (Abstract.isUnary(term, StandardNames.DEFAULT))
      term = Abstract.unaryArg(term);
    return Abstract.isBinary(term, StandardNames.IMPLEMENTS) || Abstract.isBinary(term, StandardNames.WHERE) && Abstract
        .isBinary(Abstract.binaryLhs(term), StandardNames.IMPLEMENTS);
  }

  public static IAbstract implementationDeclarationVar(IAbstract term) {
    assert isImplementationDeclaration(term);
    if (Abstract.isUnary(term, StandardNames.DEFAULT))
      term = Abstract.unaryArg(term);
    if (Abstract.isBinary(term, StandardNames.WHERE))
      term = Abstract.binaryLhs(term);
    assert Abstract.isBinary(term, StandardNames.IMPLEMENTS);
    return Abstract.binaryLhs(term);
  }

  public static IAbstract defStatement(Location loc, IAbstract lhs, IAbstract rhs) {
    return Abstract.unary(loc, StandardNames.DEF, Abstract.binary(loc, StandardNames.IS, lhs, rhs));
  }

  public static boolean isIsStatement(IAbstract term) {
    if (isPrivate(term))
      return isIsStatement(privateTerm(term));
    else
      return Abstract.isUnary(term, StandardNames.DEF) && Abstract.isBinary(Abstract.unaryArg(term), StandardNames.IS);
  }

  public static IAbstract isStmtPattern(IAbstract term) {
    assert isIsStatement(term);

    if (isPrivate(term))
      return isStmtPattern(privateTerm(term));
    else if (Abstract.isUnary(term, StandardNames.DEF) && Abstract.isBinary(Abstract.unaryArg(term), StandardNames.IS))
      return Abstract.binaryLhs(Abstract.unaryArg(term));
    else
      return null;
  }

  public static IAbstract isStmtValue(IAbstract term) {
    assert isIsStatement(term);
    if (isPrivate(term))
      return isStmtValue(privateTerm(term));
    else if (Abstract.isUnary(term, StandardNames.DEF) && Abstract.isBinary(Abstract.unaryArg(term), StandardNames.IS))
      return Abstract.binaryRhs(Abstract.unaryArg(term));
    else
      return null;
  }

  public static IAbstract varIsDeclaration(Location loc, IAbstract ptn, IAbstract exp) {
    return Abstract.unary(loc, StandardNames.DEF, Abstract.binary(loc, StandardNames.IS, ptn, exp));
  }

  public static boolean isIsForm(IAbstract term) {
    return Abstract.isBinary(term, StandardNames.IS);
  }

  public static IAbstract isFormPattern(IAbstract term) {
    assert isIsForm(term);

    return Abstract.binaryLhs(term);
  }

  public static IAbstract isFormValue(IAbstract term) {
    assert isIsForm(term);
    return Abstract.binaryRhs(term);
  }

  public static boolean isMacroVar(IAbstract term) {
    return isMacroDef(term) && macroRulePtn(term) instanceof Name;
  }

  public static boolean isMacroDef(IAbstract term) {
    return Abstract.isUnary(term, StandardNames.META_HASH) && Abstract.isBinary(Abstract.unaryArg(term), StandardNames.MACRORULE);
  }

  public static IAbstract macroRulePtn(IAbstract term) {
    assert isMacroDef(term);
    return Abstract.binaryLhs(Abstract.unaryArg(term));
  }

  public static IAbstract macroRuleRepl(IAbstract term) {
    assert isMacroDef(term);
    return Abstract.binaryRhs(Abstract.unaryArg(term));
  }

  public static boolean isCodeMacro(IAbstract term) {
    if (Abstract.isUnary(term, StandardNames.META_HASH)) {
      IAbstract stmt = Abstract.unaryArg(term);
      return isIsStatement(stmt) || isFunctionStatement(stmt);
    }
    return false;
  }

  public static IAbstract codeMacroEqn(IAbstract term) {
    assert isCodeMacro(term);
    return Abstract.unaryArg(term);
  }

  public static boolean testPipe(IAbstract trm, Predicate<IAbstract> test) {
    if (Abstract.isBinary(trm, StandardNames.PIPE))
      return testPipe(Abstract.binaryLhs(trm), test) && testPipe(Abstract.binaryRhs(trm), test);
    else
      return test.test(trm);
  }

  public static boolean isFunctionStatement(IAbstract trm) {
    if (isPrivate(trm))
      trm = privateTerm(trm);

    return Abstract.isUnary(trm, StandardNames.FUN) && testPipe(Abstract.unaryArg(trm), CompilerUtils::isEquation);
  }

  public static IAbstract functionRules(IAbstract trm) {
    assert isFunctionStatement(trm);

    if (isPrivate(trm))
      trm = privateTerm(trm);

    return Abstract.unaryArg(trm);
  }

  public static IAbstract firstRule(IAbstract trm) {
    if (Abstract.isBinary(trm, StandardNames.PIPE))
      return firstRule(Abstract.binaryLhs(trm));
    else
      return trm;
  }

  public static boolean isEquation(IAbstract trm) {
    return Abstract.isBinary(trm, StandardNames.IS) && isProgramHeadPtn(Abstract.binaryLhs(trm));
  }

  public static IList getEquationArgs(IAbstract eqn) {
    assert isEquation(eqn);

    return ((Apply) equationLhs(eqn)).getArgs();
  }

  public static IAbstract equationLhs(IAbstract term) {
    assert isEquation(term);
    IAbstract lhs = Abstract.binaryLhs(term);

    if (Abstract.isUnary(lhs, StandardNames.DEFAULT))
      lhs = Abstract.unaryArg(lhs);

    if (Abstract.isBinary(lhs, StandardNames.WHERE))
      lhs = Abstract.binaryLhs(lhs);

    return lhs;
  }

  public static int arityOfEquation(IAbstract term) {
    assert isEquation(term);

    return getEquationArgs(term).size();
  }

  public static IAbstract equationRhs(IAbstract term) {
    assert isEquation(term);
    return Abstract.binaryRhs(term);
  }

  public static IAbstract equation(Location loc, String label, List<IAbstract> args, IAbstract value) {
    return Abstract.binary(loc, StandardNames.IS, new Apply(loc, label, args), value);
  }

  public static IAbstract equation(Location loc, IAbstract lhs, IAbstract value) {
    return Abstract.binary(loc, StandardNames.IS, lhs, value);
  }

  public static IAbstract equation(Location loc, String label, List<IAbstract> args, IAbstract cond, IAbstract value) {
    IAbstract head = new Apply(loc, label, args);
    if (!isTrivial(cond))
      head = Abstract.binary(loc, StandardNames.WHERE, head, cond);
    return Abstract.binary(loc, StandardNames.IS, head, value);
  }

  public static IAbstract defaultEquation(Location loc, String label, List<IAbstract> args, IAbstract value) {
    return Abstract.binary(loc, StandardNames.IS, Abstract.unary(loc, StandardNames.DEFAULT, new Apply(loc, label,
        args)), value);
  }

  public static IAbstract function(Location loc, IAbstract... equations) {
    assert equations.length > 0;
    IAbstract rules = equations[equations.length - 1];

    for (int ix = equations.length - 1; ix > 0; ix--)
      rules = Abstract.binary(loc, StandardNames.PIPE, equations[ix - 1], rules);

    return Abstract.unary(loc, StandardNames.FUN, rules);
  }

  public static IAbstract function(Location loc, List<IAbstract> equations) {
    assert equations.size() > 0;
    IAbstract rules = equations.get(equations.size() - 1);

    for (int ix = equations.size() - 1; ix > 0; ix--)
      rules = Abstract.binary(loc, StandardNames.PIPE, equations.get(ix - 1), rules);

    return Abstract.unary(loc, StandardNames.FUN, rules);
  }

  public static boolean isProcedureStatement(IAbstract stmt) {
    if (isPrivate(stmt))
      return isProcedureStatement(privateTerm(stmt));
    else
      return Abstract.isUnary(stmt, StandardNames.PRC) && testPipe(Abstract.unaryArg(stmt),
          CompilerUtils::isActionRule);
  }

  public static IAbstract procedureRules(IAbstract stmt) {
    assert isProcedureStatement(stmt);

    if (isPrivate(stmt))
      return procedureRules(privateTerm(stmt));
    else
      return Abstract.unaryArg(stmt);
  }

  public static IAbstract procedure(Location loc, IAbstract... equations) {
    assert equations.length > 0;
    IAbstract rules = equations[equations.length - 1];

    for (int ix = equations.length - 1; ix > 0; ix--)
      rules = Abstract.binary(loc, StandardNames.PIPE, equations[ix - 1], rules);

    return Abstract.unary(loc, StandardNames.PRC, rules);
  }

  public static IAbstract procedure(Location loc, List<IAbstract> equations) {
    assert equations.size() > 0;
    IAbstract rules = equations.get(equations.size() - 1);

    for (int ix = equations.size() - 1; ix > 0; ix--)
      rules = Abstract.binary(loc, StandardNames.PIPE, equations.get(ix - 1), rules);

    return Abstract.unary(loc, StandardNames.PRC, rules);
  }

  public static boolean isActionRule(IAbstract term) {
    return Abstract.isBinary(term, StandardNames.DO) && isProgramHeadPtn(Abstract.binaryLhs(term));
  }

  public static boolean isProcLambda(IAbstract term) {
    return Abstract.isBinary(term, StandardNames.DO) && Abstract.isTupleTerm(Abstract.binaryLhs(term));
  }

  public static int procLambdaArity(IAbstract term) {
    IAbstract lhs = Abstract.binaryLhs(term);
    if (Abstract.isBinary(lhs, StandardNames.WHERE))
      lhs = Abstract.binaryLhs(lhs);
    if (Abstract.isTupleTerm(lhs))
      return Abstract.tupleArity(lhs);
    else
      return 1;
  }

  public static IAbstract actionRuleLhs(IAbstract term) {
    IAbstract lhs = Abstract.binaryLhs(term);

    if (Abstract.isUnary(lhs, StandardNames.DEFAULT))
      return Abstract.unaryArg(lhs);
    else
      return lhs;
  }

  public static int arityOfActionRule(IAbstract term) {
    IAbstract lhs = actionRuleLhs(term);

    if (Abstract.isUnary(lhs, StandardNames.DEFAULT))
      lhs = Abstract.unaryArg(lhs);

    if (Abstract.isBinary(lhs, StandardNames.WHERE))
      lhs = Abstract.binaryLhs(lhs);

    return ((Apply) lhs).getArgs().size();
  }

  public static IAbstract actionRuleBody(IAbstract term) {
    return Abstract.binaryRhs(term);
  }

  public static IAbstract actionRule(Location loc, IAbstract lhs, IAbstract body) {
    return Abstract.binary(loc, StandardNames.DO, lhs, body);
  }

  public static boolean isPatternStatement(IAbstract stmt) {
    return Abstract.isUnary(stmt, StandardNames.PTN) && testPipe(Abstract.unaryArg(stmt), CompilerUtils::isPatternRule);
  }

  public static IAbstract patternRules(IAbstract stmt) {
    assert isPatternStatement(stmt);

    if (isPrivate(stmt))
      return patternRules(privateTerm(stmt));
    else
      return Abstract.unaryArg(stmt);
  }

  public static IAbstract pattern(Location loc, IAbstract... equations) {
    assert equations.length > 0;
    IAbstract rules = equations[equations.length - 1];

    for (int ix = equations.length - 1; ix > 0; ix--)
      rules = Abstract.binary(loc, StandardNames.PIPE, equations[ix - 1], rules);

    return Abstract.unary(loc, StandardNames.PTN, rules);
  }

  public static IAbstract pattern(Location loc, List<IAbstract> equations) {
    assert equations.size() > 0;
    IAbstract rules = equations.get(equations.size() - 1);

    for (int ix = equations.size() - 1; ix > 0; ix--)
      rules = Abstract.binary(loc, StandardNames.PIPE, equations.get(ix - 1), rules);

    return Abstract.unary(loc, StandardNames.PTN, rules);
  }

  public static boolean isPatternRule(IAbstract term) {
    return Abstract.isBinary(term, StandardNames.FROM);
  }

  public static boolean isLambdaPattern(IAbstract term) {
    return Abstract.isBinary(term, StandardNames.FROM) && Abstract.isTupleTerm(Abstract.binaryLhs(term));
  }

  public static IAbstract patternRuleHead(IAbstract term) {
    assert isPatternRule(term);

    return Abstract.binaryLhs(term);
  }

  public static IAbstract patternRuleBody(IAbstract term) {
    assert isPatternRule(term);

    return Abstract.binaryRhs(term);
  }

  public static IAbstract patternRule(Location loc, IAbstract head, IAbstract body) {
    return Abstract.binary(loc, StandardNames.FROM, head, body);
  }

  public static int arityOfPatternRule(IAbstract term) {
    assert isPatternRule(term);

    IAbstract lhs = patternRuleHead(term);

    return ((Apply) lhs).getArgs().size();
  }

  public static int arityOfPatternLambda(IAbstract term) {
    IAbstract lhs = Abstract.binaryLhs(term);
    if (Abstract.isBinary(lhs, StandardNames.WHERE))
      lhs = Abstract.binaryLhs(lhs);
    if (Abstract.isTupleTerm(lhs))
      return Abstract.tupleArity(lhs);
    else
      return 1;
  }

  public static boolean isBoundTo(IAbstract trm) {
    return Abstract.isBinary(trm, StandardNames.BOUND_TO) || Abstract.isBinary(trm, StandardNames.MATCHES);
  }

  public static IAbstract boundToPtn(IAbstract term) {
    assert isBoundTo(term);
    if (Abstract.isBinary(term, StandardNames.BOUND_TO))
      return Abstract.binaryLhs(term);
    else
      return Abstract.binaryRhs(term);
  }

  public static IAbstract boundToExp(IAbstract term) {
    assert isBoundTo(term);
    if (Abstract.isBinary(term, StandardNames.BOUND_TO))
      return Abstract.binaryRhs(term);
    else
      return Abstract.binaryLhs(term);
  }

  public static IAbstract boundTo(Location loc, IAbstract ptn, IAbstract value) {
    return Abstract.binary(loc, StandardNames.MATCHES, value, ptn);
  }

  public static boolean isTypeStmt(IAbstract term) {
    return isTypeDefn(term) || isTypeAlias(term) || isTypeWitness(term);
  }

  public static boolean isTypeDefn(IAbstract term) {
    return Abstract.isUnary(term, StandardNames.TYPE) && Abstract.isBinary(Abstract.unaryArg(term), StandardNames.IS);
  }

  public static IAbstract typeDefnType(IAbstract stmt) {
    assert isTypeDefn(stmt);
    return Abstract.binaryLhs(Abstract.unaryArg(stmt));
  }

  public static String typeDefnName(IAbstract stmt) {
    assert isTypeDefn(stmt);
    IAbstract tp = Abstract.deParen(Abstract.binaryLhs(Abstract.unaryArg(stmt)));
    return typeLabel(tp);
  }

  public static IAbstract typeDefnConstructors(IAbstract term) {
    assert isTypeDefn(term);

    return Abstract.binaryRhs(Abstract.unaryArg(term));
  }

  public static String typeLabel(IAbstract tp) {
    if (Abstract.isBinary(tp, StandardNames.WHERE))
      return typeLabel(Abstract.binaryLhs(tp));
    else if (Abstract.isParenTerm(tp))
      return typeLabel(Abstract.deParen(tp));
    else if (Abstract.isIdentifier(tp))
      return Abstract.getId(tp);
    else if (Abstract.isBinary(tp, StandardNames.OF) && Abstract.isIdentifier(Abstract.binaryLhs(tp)))
      return Abstract.getId(Abstract.binaryLhs(tp));
    else if (isAnonAggConLiteral(tp))
      return anonRecordTypeLabel(tp);
    else if (Abstract.isTupleTerm(tp))
      return ((Apply) tp).getOp();
    else
      return null;
  }

  public static boolean isTypeVar(IAbstract term) {
    return (Abstract.isUnary(term, StandardNames.TYPEVAR) || Abstract.isUnary(term, StandardNames.DBLCENT)) && Abstract
        .isIdentifier(Abstract.unaryArg(term));
  }

  public static IAbstract typeVName(IAbstract tp) {
    assert isTypeVar(tp) || isTypeFunVar(tp);
    return Abstract.deParen(Abstract.unaryArg(tp));
  }

  public static String typeVarName(IAbstract tp) {
    assert isTypeVar(tp);
    return Abstract.getId(Abstract.unaryArg(tp));
  }

  public static IAbstract typeVar(Location loc, IAbstract name) {
    return Abstract.unary(loc, StandardNames.TYPEVAR, name);
  }

  public static boolean isTypeFunVar(IAbstract term) {
    return Abstract.isUnary(term, StandardNames.DBLCENT) && Abstract.isIdentifier(Abstract.unaryArg(term));
  }

  public static String typeFunVarName(IAbstract tp) {
    assert isTypeFunVar(tp) || isTypeVar(tp);
    return Abstract.getId(Abstract.unaryArg(tp));
  }

  public static boolean isDerived(IAbstract term) {
    return Abstract.isBinary(term, StandardNames.DETERMINES);
  }

  public static IAbstract derivedType(IAbstract term) {
    assert isDerived(term);
    return Abstract.binaryRhs(term);
  }

  public static IAbstract dependsType(IAbstract term) {
    assert isDerived(term);
    return Abstract.binaryLhs(term);
  }

  public static boolean isImplementsConstraint(IAbstract term) {
    return Abstract.isBinary(term, StandardNames.IMPLEMENTS) && isBlockTerm(Abstract.binaryRhs(term));
  }

  public static IAbstract implementConType(IAbstract term) {
    assert isImplementsConstraint(term);
    return Abstract.binaryLhs(term);
  }

  public static IAbstract implementsConstraints(IAbstract term) {
    assert isImplementsConstraint(term);
    return blockContent(Abstract.binaryRhs(term));
  }

  public static boolean isTypeAnnotation(IAbstract stmt) {
    if (isPrivate(stmt))
      return isTypeAnnotation(privateTerm(stmt));
    else
      return Abstract.isBinary(stmt, StandardNames.HAS_TYPE);
  }

  public static IAbstract typeAnnotation(IAbstract stmt) {
    assert isTypeAnnotation(stmt);
    if (isPrivate(stmt))
      return typeAnnotation(privateTerm(stmt));
    else if (Abstract.isBinary(stmt, StandardNames.HAS_TYPE))
      return Abstract.binaryRhs(stmt);
    else
      return Abstract.unaryArg(Abstract.binaryRhs(stmt));
  }

  public static IAbstract typeAnnotationStmt(Location loc, IAbstract name, IAbstract type) {
    return Abstract.binary(loc, StandardNames.HAS_TYPE, name, type);
  }

  public static IAbstract typeAnnotatedTerm(IAbstract stmt) {
    assert isTypeAnnotation(stmt);
    return Abstract.binaryLhs(stmt);
  }

  public static boolean isKindAnnotation(IAbstract stmt) {
    if (isPrivate(stmt))
      return isKindAnnotation(privateTerm(stmt));
    else
      return Abstract.isBinary(stmt, StandardNames.HAS_KIND) && Abstract.isIdentifier(Abstract.binaryLhs(stmt))
          || Abstract.isBinary(stmt, StandardNames.WHERE) && isKindAnnotation(Abstract.binaryLhs(stmt));
  }

  public static IAbstract kindAnnotation(IAbstract stmt) {
    assert isKindAnnotation(stmt);
    if (isPrivate(stmt))
      return kindAnnotation(privateTerm(stmt));
    else if (Abstract.isBinary(stmt, StandardNames.WHERE))
      return kindAnnotation(Abstract.binaryLhs(stmt));
    else
      return Abstract.binaryRhs(stmt);
  }

  public static IAbstract kindAnnotatedTerm(IAbstract stmt) {
    assert isKindAnnotation(stmt);
    if (Abstract.isBinary(stmt, StandardNames.WHERE))
      return kindAnnotatedTerm(Abstract.binaryLhs(stmt));
    else
      return Abstract.binaryLhs(stmt);
  }

  public static IAbstract kindAnnotatedConstraint(IAbstract stmt) {
    assert isKindAnnotation(stmt);
    if (Abstract.isBinary(stmt, StandardNames.WHERE))
      return Abstract.binaryRhs(stmt);
    else
      return null;
  }

  public static IAbstract functionType(Location loc, List<IAbstract> argTypes, IAbstract resType) {
    IAbstract args = Abstract.tupleTerm(loc, argTypes);
    return Abstract.binary(loc, StandardNames.FUN_ARROW, args, resType);
  }

  public static boolean isProgramType(IAbstract tp) {
    while (Abstract.isBinary(tp, StandardNames.UNI_TILDA))
      tp = Abstract.binaryRhs(tp);
    return Abstract.isApply(tp, StandardNames.FUN_ARROW) || Abstract.isApply(tp, StandardNames.ACTION_TYPE) || Abstract
        .isApply(tp, StandardNames.PTN_TYPE);
  }

  public static boolean isUniversalType(IAbstract tp) {
    if (Abstract.isBinary(tp, StandardNames.S_T)) {
      IAbstract lhs = Abstract.binaryLhs(tp);
      return Abstract.isUnary(lhs, StandardNames.FOR_ALL);
    }

    return false;
  }

  public static IAbstract universalTypeVars(IAbstract tp) {
    assert isUniversalType(tp);

    if (Abstract.isBinary(tp, StandardNames.UNI_TILDA))
      return Abstract.binaryLhs(tp);
    else
      return Abstract.unaryArg(Abstract.binaryLhs(tp));
  }

  public static IAbstract universalBoundType(IAbstract tp) {
    assert isUniversalType(tp);

    return Abstract.binaryRhs(tp);
  }

  public static IAbstract universalType(Location loc, List<IAbstract> bv, IAbstract bt) {
    if (!bv.isEmpty()) {
      IAbstract bvars = tupleUp(loc, StandardNames.COMMA, bv);
      return Abstract.binary(loc, StandardNames.S_T, Abstract.unary(loc, StandardNames.FOR_ALL, bvars), bt);
    } else
      return bt;
  }

  public static IAbstract unwrapQuants(IAbstract tp) {
    while (isUniversalType(tp))
      tp = universalBoundType(tp);
    return tp;
  }

  public static boolean isExistentialType(IAbstract tp) {
    if (Abstract.isBinary(tp, StandardNames.S_T)) {
      IAbstract lhs = Abstract.binaryLhs(tp);
      return Abstract.isUnary(lhs, StandardNames.EXISTS);
    }

    return false;
  }

  public static IAbstract existentialTypeVars(IAbstract tp) {
    assert isExistentialType(tp);

    return Abstract.unaryArg(Abstract.binaryLhs(tp));
  }

  public static IAbstract existentialBoundType(IAbstract tp) {
    assert isExistentialType(tp);

    return Abstract.binaryRhs(tp);
  }

  public static List<IAbstract> findTypeVarsInType(IAbstract tp, final Dictionary dict, final Collection<String> tVars) {
    final List<IAbstract> vars = new ArrayList<>();

    IAbstractVisitor finder = new DefaultAbstractVisitor() {

      @Override
      public void visitApply(Apply tp) {
        if (isTypeVar(tp)) {
          String name = typeFunVarName(tp);
          IAbstract var = Abstract.unaryArg(tp);
          if (!tVars.contains(name) && !vars.contains(var))
            vars.add(var);
        } else if (Abstract.isBinary(tp, StandardNames.OF)) {
          Abstract.binaryLhs(tp).accept(this);
          Abstract.binaryRhs(tp).accept(this);
        } else if (Abstract.isTupleTerm(tp)) {
          for (IValue el : Abstract.tupleArgs(tp))
            ((IAbstract) el).accept(this);
        } else if (Abstract.isBinary(tp, StandardNames.WHERE))
          Abstract.binaryLhs(tp).accept(this);
        else if (Abstract.isParenTerm(tp))
          Abstract.deParen(tp).accept(this);
        else if (isAnonAggConLiteral(tp)) {
          for (IAbstract el : unWrap(anonAggEls(tp))) {
            if (isTypeAnnotation(el))
              typeAnnotation(el).accept(this);
            else if (isTypeEquality(el))
              typeEqualType(el).accept(this);
            else if (isTypeWitness(el))
              typeWitness(el).accept(this);
          }
        }
      }

      @Override
      public void visitName(Name name) {
        String id = Abstract.getId(name);
        if (!vars.contains(name) && !tVars.contains(id) && dict.getTypeDescription(id) == null)
          vars.add(name);
      }
    };

    tp.accept(finder);
    return vars;
  }

  public static String defaultLabel(String typeLabel, String specLabel, String member) {
    return typeLabel + "#" + specLabel + "$" + member;
  }

  public static String integrityLabel(String typeLabel, String specLabel) {
    return typeLabel + "##" + specLabel;
  }

  public static SortedMap<String, Integer> buildInterfaceIndex(Map<String, IType> memberTypes) {
    SortedMap<String, Integer> index = new TreeMap<>();

    int ix = 0;
    for (Entry<String, IType> entry : memberTypes.entrySet()) {
      String fieldName = entry.getKey();
      index.put(fieldName, ix++);
    }
    return index;
  }

  public static IAbstract nameOfPattern(IAbstract stmt) {
    if (isPrivate(stmt))
      return nameOfPattern(privateTerm(stmt));
    else if (Abstract.isUnary(stmt, StandardNames.PTN))
      return nameOfPattern(Abstract.unaryArg(stmt));
    else if (Abstract.isBinary(stmt, StandardNames.PIPE))
      return nameOfPattern(Abstract.getArg(stmt, 0));
    else if (Abstract.isBinary(stmt, StandardNames.FROM)) {
      IAbstract lhs = Abstract.binaryLhs(stmt);

      if (lhs instanceof Apply)
        return ((Apply) lhs).getOperator();
      else
        return lhs;
    } else
      return null;
  }

  public static boolean isProgramHeadPtn(IAbstract term) {
    if (Abstract.isBinary(term, StandardNames.WHERE))
      return isProgramHeadPtn(Abstract.getArg(term, 0));
    else if (Abstract.isUnary(term, StandardNames.DEFAULT))
      return isProgramHeadPtn(Abstract.unaryArg(term));
    else if (Abstract.isParenTerm(term))
      return isProgramHeadPtn(Abstract.deParen(term));
    else
      return term instanceof Apply && !Abstract.isTupleTerm(term) && !isAnonAggConLiteral(term) && !StandardNames
          .isKeyword(Abstract.getOperator(term));
  }

  public static IAbstract nameOfFunction(IAbstract def) {
    if (isPrivate(def))
      return nameOfFunction(privateTerm(def));
    else if (Abstract.isUnary(def, StandardNames.FUN))
      return nameOfFunction(Abstract.unaryArg(def));
    else if (Abstract.isBinary(def, StandardNames.PIPE))
      return nameOfFunction(Abstract.getArg(def, 0));
    else {
      assert Abstract.isBinary(def, StandardNames.IS);

      IAbstract lhs = Abstract.getArg(def, 0);

      if (Abstract.isBinary(lhs, StandardNames.WHERE) || Abstract.isUnary(lhs, StandardNames.DEFAULT))
        lhs = Abstract.getArg(lhs, 0);
      if (lhs instanceof Apply)
        return ((Apply) lhs).getOperator();
      else
        return lhs;
    }
  }

  public static IAbstract functionHead(IAbstract def) {
    if (isPrivate(def))
      return functionHead(privateTerm(def));
    else if (Abstract.isUnary(def, StandardNames.FUN))
      return functionHead(Abstract.unaryArg(def));
    else if (Abstract.isBinary(def, StandardNames.PIPE))
      return functionHead(Abstract.binaryLhs(def));
    else {
      assert Abstract.isBinary(def, StandardNames.IS);

      IAbstract lhs = Abstract.binaryLhs(def);

      if (Abstract.isBinary(lhs, StandardNames.WHERE) || Abstract.isUnary(lhs, StandardNames.DEFAULT))
        lhs = Abstract.getArg(lhs, 0);
      return lhs;
    }
  }

  public static <T> T defaultFor(T access, T deflt) {
    if (access == null)
      return deflt;
    else
      return access;
  }

  public static boolean isTypeAlias(IAbstract stmt) {
    if (Abstract.isUnary(stmt, AbstractType.TYPE)) {
      stmt = Abstract.unaryArg(stmt);
      if (Abstract.isBinary(stmt, StandardNames.IS)) {
        stmt = Abstract.binaryRhs(stmt);
        while (Abstract.isBinary(stmt)) {
          if (Abstract.isBinary(stmt, StandardNames.OF) && Abstract.isName(Abstract.binaryLhs(stmt),
              StandardNames.ALIAS))
            return true;
          stmt = Abstract.binaryLhs(stmt);
        }
        return false;
      } else
        return false;
    } else
      return false;
  }

  public static IAbstract typeAliasType(IAbstract stmt) {
    assert isTypeAlias(stmt);

    return Abstract.binaryLhs(Abstract.unaryArg(stmt));
  }

  public static IAbstract typeAliasAlias(IAbstract stmt) {
    assert isTypeAlias(stmt);

    stmt = Abstract.unaryArg(stmt);
    if (Abstract.isBinary(stmt, StandardNames.IS))
      return typeAliasExtract(Abstract.binaryRhs(stmt));
    else
      return null;
  }

  private static IAbstract typeAliasExtract(IAbstract term) {
    if (Abstract.isBinary(term, StandardNames.OF) && Abstract.isName(Abstract.binaryLhs(term), StandardNames.ALIAS))
      return Abstract.binaryRhs(term);
    else if (Abstract.isBinary(term)) {
      IAbstract ptn = typeAliasExtract(Abstract.binaryLhs(term));
      if (ptn != null)
        return Abstract.binary(term.getLoc(), ((Apply) term).getOperator(), ptn, Abstract.binaryRhs(term));
      else
        return null;
    } else
      return null;
  }

  public static String typeName(IAbstract tp) {
    tp = Abstract.deParen(tp);
    if (tp instanceof Name)
      return Abstract.getId(tp);
    else if (Abstract.isBinary(tp, StandardNames.WHERE))
      return typeName(Abstract.binaryLhs(tp));
    else if (Abstract.isBinary(tp, StandardNames.OF) && Abstract.isIdentifier(Abstract.binaryLhs(tp)))
      return Abstract.getId(Abstract.binaryLhs(tp));
    else if (Abstract.isBinary(tp, StandardNames.FUN_ARROW))
      return StandardNames.FUN_ARROW;
    else if (Abstract.isBinary(tp, StandardNames.OVERLOADED_TYPE))
      return StandardNames.OVERLOADED_TYPE;
    else if (Abstract.isBinary(tp, StandardNames.ACTION_TYPE))
      return StandardNames.ACTION_TYPE;
    else if (Abstract.isBinary(tp, StandardNames.PTN_TYPE))
      return StandardNames.PTN_TYPE;
    else if (Abstract.isBinary(tp, StandardNames.CONSTRUCTOR_TYPE))
      return StandardNames.CONSTRUCTOR_TYPE;
    else if (Abstract.isBinary(tp, StandardNames.OVERLOADED_TYPE))
      return StandardNames.OVERLOADED_TYPE;

    else if (isTypeVar(tp))
      return typeVarName(tp);
    else if (isTypeFunVar(tp))
      return typeVarName(tp);
    else if (isRef(tp))
      return StandardNames.REF;

    else if (isUniversalType(tp))
      return typeName(universalBoundType(tp));
    else if (isExistentialType(tp))
      return typeName(existentialBoundType(tp));
    else if (Abstract.isBinary(tp, StandardNames.DETERMINES))
      return typeName(Abstract.binaryLhs(tp));
    else
      return null;
  }

  public static boolean isTypeWitness(IAbstract stmt) {
    return Abstract.isUnary(stmt, AbstractType.TYPE) && Abstract.isBinary(Abstract.unaryArg(stmt), StandardNames.COUNTS_AS);
  }

  public static IAbstract typeWitness(IAbstract stmt) {
    assert isTypeWitness(stmt);
    return Abstract.binaryLhs(Abstract.unaryArg(stmt));
  }

  public static IAbstract witnessedType(IAbstract stmt) {
    assert isTypeWitness(stmt);
    return Abstract.binaryRhs(Abstract.unaryArg(stmt));
  }

  public static boolean isTypeEquality(IAbstract stmt) {
    if (Abstract.isUnary(stmt, AbstractType.TYPE)) {
      IAbstract typeForm = Abstract.unaryArg(stmt);
      if (Abstract.isBinary(typeForm, StandardNames.EQUAL))
        return Abstract.isBinary(typeForm, StandardNames.EQUAL) && Abstract.isIdentifier(Abstract.binaryLhs(typeForm));
      else
        return Abstract.isIdentifier(typeForm) || isTypeVar(typeForm);
    } else
      return false;
  }

  public static IAbstract typeEqualField(IAbstract stmt) {
    assert isTypeEquality(stmt);
    IAbstract typeForm = Abstract.unaryArg(stmt);
    if (Abstract.isBinary(typeForm, StandardNames.EQUAL))
      return Abstract.binaryLhs(Abstract.unaryArg(stmt));
    else
      return typeForm;
  }

  public static IAbstract typeEqualType(IAbstract stmt) {
    assert isTypeEquality(stmt);
    IAbstract typeForm = Abstract.unaryArg(stmt);
    if (Abstract.isBinary(typeForm, StandardNames.EQUAL))
      return Abstract.binaryRhs(typeForm);
    else
      return typeForm;

  }

  public static IAbstract nameOfProcedure(IAbstract stmt) {
    if (isPrivate(stmt))
      return nameOfProcedure(privateTerm(stmt));
    else if (Abstract.isUnary(stmt, StandardNames.PRC))
      return nameOfProcedure(Abstract.unaryArg(stmt));
    else if (Abstract.isBinary(stmt, StandardNames.PIPE))
      return nameOfProcedure(Abstract.getArg(stmt, 0));
    else {
      assert Abstract.isBinary(stmt, StandardNames.DO) || isBraceTerm(stmt);

      IAbstract lhs = Abstract.isBinary(stmt, StandardNames.DO) ? Abstract.getArg(stmt, 0) : braceLabel(stmt);

      if (Abstract.isBinary(lhs, StandardNames.WHERE) || Abstract.isUnary(lhs, StandardNames.DEFAULT))
        lhs = Abstract.getArg(lhs, 0);
      if (lhs instanceof Apply)
        return ((Apply) lhs).getOperator();
      else
        return lhs;
    }
  }

  public static IContentExpression integerLiteral(final Location loc, int value) {
    return new ConstructorTerm(loc, StandardTypes.INTEGER, integerType, new Scalar(loc, rawIntegerType, value));
  }

  public static IContentExpression longLiteral(Location loc, long value) {
    return new ConstructorTerm(loc, StandardTypes.LONG, longType, new Scalar(loc, rawLongType, value));
  }

  public static IContentExpression trueLiteral(Location loc) {
    return new ConstructorTerm(loc, StandardNames.TRUE, booleanType);
  }

  public static boolean isTrueLiteral(IContentExpression exp) {
    return exp instanceof ConstructorTerm && ((ConstructorTerm) exp).getLabel().equals(StandardNames.TRUE)
        && ((ConstructorTerm) exp).arity() == 0;
  }

  public static IContentExpression falseLiteral(Location loc) {
    return new ConstructorTerm(loc, StandardNames.FALSE, booleanType);
  }

  public static IContentExpression stringLiteral(Location loc, String str) {
    return new ConstructorTerm(loc, StandardTypes.STRING, stringType, new Scalar(loc, rawStringType, str));
  }

  public static IContentPattern stringPattern(Location loc, String str) {
    return new ConstructorPtn(loc, StandardTypes.STRING, stringType, new ScalarPtn(loc, rawStringType, str));
  }

  public static boolean isRawLiteral(IAbstract term) {
    return Abstract.isUnary(term, StandardNames.RAW);
  }

  public static IAbstract rawTerm(IAbstract term) {
    assert isRawLiteral(term);
    return Abstract.unaryArg(term);
  }

  public static IAbstract rawLiteral(Location loc, IAbstract term) {
    return Abstract.unary(loc, StandardNames.RAW, term);
  }

  public static ConstructorTerm continueWith(Location loc, IContentExpression exp) {
    return new ConstructorTerm(loc, StandardNames.CONTINUEWITH, TypeUtils.iterstateType(exp.getType()), exp);
  }

  public static ConstructorPtn continuePtn(Location loc, IContentPattern ptn) {
    return new ConstructorPtn(loc, StandardNames.CONTINUEWITH, TypeUtils.iterstateType(ptn.getType()), ptn);
  }

  public static ConstructorTerm noMore(Location loc, IContentExpression exp) {
    return new ConstructorTerm(loc, StandardNames.NOMORE, TypeUtils.iterstateType(exp.getType()), exp);
  }

  public static ConstructorPtn noMorePtn(final Location loc, IContentPattern ptn) {
    return new ConstructorPtn(loc, StandardNames.NOMORE, TypeUtils.iterstateType(ptn.getType()), ptn);
  }

  public static IContentExpression noneFound(Location loc, IType stType) {
    return new ConstructorTerm(loc, StandardNames.NONEFOUND, TypeUtils.iterstateType(stType));
  }

  public static IContentPattern noneFoundPtn(Location loc, IType stType) {
    return new ConstructorPtn(loc, StandardNames.NONEFOUND, TypeUtils.iterstateType(stType));
  }

  public static ConstructorTerm abortIter(Location loc, IType type, IContentExpression exp) {
    return new ConstructorTerm(loc, StandardNames.ABORT_ITER, TypeUtils.iterstateType(type), exp);
  }

  public static ConstructorPtn abortIterPtn(final Location loc, IType type, IContentPattern ptn) {
    return new ConstructorPtn(loc, StandardNames.ABORT_ITER, TypeUtils.iterstateType(type), ptn);
  }

  public static ConstructorTerm possible(Location loc, IContentExpression exp) {
    IType resltType = TypeUtils.typeExp(StandardNames.POSSIBLE, exp.getType());
    return new ConstructorTerm(loc, StandardNames.POSSIBLE, resltType, exp);
  }

  public static ConstructorPtn possiblePtn(final Location loc, IType type, IContentPattern ptn) {
    return new ConstructorPtn(loc, StandardNames.POSSIBLE, TypeUtils.typeExp(StandardNames.POSSIBLE, type), ptn);
  }

  public static ConstructorTerm impossible(Location loc) {
    TypeVar tv = new TypeVar();
    IType resltType = TypeUtils.typeExp(StandardNames.POSSIBLE, tv);
    return new ConstructorTerm(loc, StandardNames.IMPOSSIBLE, resltType);
  }

  public static ConstructorTerm impossible(Location loc, IType t) {
    IType resltType = TypeUtils.typeExp(StandardNames.POSSIBLE, t);
    return new ConstructorTerm(loc, StandardNames.IMPOSSIBLE, resltType);
  }

  public static IContentExpression none(Location loc, IType stType) {
    return new ConstructorTerm(loc, Option.None.label, stType);
  }

  public static boolean isTuplePattern(IContentPattern tpl) {
    return tpl instanceof ConstructorPtn && TypeUtils.isTupleType(tpl.getType());
  }

  public static boolean isTupleTerm(IContentExpression term) {
    return term instanceof ConstructorTerm && TypeUtils.isTupleType(term.getType());
  }
}
