package org.star_lang.star.compiler.ast;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.IList;
import org.star_lang.star.data.type.Location;

import java.util.List;

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

public abstract class Abstract {

  public static IAbstract deParen(IAbstract term) {
    while (isParenTerm(term))
      term = getArg(term, 0);

    return term;
  }

  public static boolean isParenTerm(IAbstract term) {
    return isUnary(term, StandardNames.PARENS_LABEL);
  }

  public static IAbstract parenTerm(Location loc, IAbstract term) {
    return unary(loc, StandardNames.PARENS_LABEL, term);
  }

  public static Name name(Location loc, String id) {
    return new Name(loc, id);
  }

  public static boolean isAnon(IAbstract a) {
    return isName(a, StandardNames.ANONYMOUS);
  }

  public static IAbstract anon(Location loc) {
    return new Name(loc, StandardNames.ANONYMOUS);
  }

  public static boolean isName(IAbstract ab, String name) {
    return ab instanceof Name && ((Name) ab).getId().equals(name);
  }

  public static boolean isName(IAbstract ab) {
    return ab instanceof Name;
  }

  public static String getId(IAbstract term) {
    if (term instanceof Name)
      return ((Name) term).getId();
    else if (isParenTerm(term))
      return getId(deParen(term));
    else
      throw new IllegalArgumentException("expecting an identifier");
  }

  public static boolean isIdentifier(IAbstract term) {
    term = deParen(term);
    return isName(term);
  }

  public static boolean isIdentifier(IAbstract term, String name) {
    term = deParen(term);
    return isName(term, name);
  }

  public static boolean isString(IAbstract term) {
    term = deParen(term);
    return term instanceof StringLiteral;
  }

  public static String getString(IAbstract term) {
    assert isString(term);
    term = deParen(term);
    return ((StringLiteral) term).getLit();
  }

  public static boolean isInt(IAbstract term) {
    return term instanceof IntegerLiteral;
  }

  public static int getInt(IAbstract term) {
    assert term instanceof IntegerLiteral;
    return ((IntegerLiteral) term).getLit();
  }

  public static String getOp(IAbstract trm) {
    assert trm instanceof Apply;
    return ((Apply) trm).getOp();
  }

  public static IAbstract getOperator(IAbstract trm) {
    assert trm instanceof Apply;
    return ((Apply) trm).getOperator();
  }

  public static int arity(IAbstract trm) {
    assert trm instanceof Apply;
    return ((Apply) trm).getArgs().size();
  }

  public static BooleanLiteral newBoolean(Location loc, boolean trueVal) {
    return new BooleanLiteral(loc, trueVal);
  }

  public static FloatLiteral newFloat(Location loc, double d) {
    return new FloatLiteral(loc, d);
  }

  public static IntegerLiteral newInteger(Location loc, int l) {
    return new IntegerLiteral(loc, l);
  }

  public static LongLiteral newLong(Location loc, long l) {
    return new LongLiteral(loc, l);
  }

  public static StringLiteral newString(Location loc, String s) {
    return new StringLiteral(loc, s);
  }

  public static boolean isApply(IAbstract term) {
    return term instanceof Apply;
  }

  public static boolean isApply(IAbstract term, String op) {
    return term instanceof Apply && isName(((Apply) term).getOperator(), op);
  }

  public static boolean isApply(IAbstract term, String op, int arity) {
    if (term instanceof Apply) {
      Apply app = (Apply) term;
      return isName(app.getOperator(), op) && app.arity() == arity;
    } else
      return false;
  }

  private static boolean isApply(IAbstract term, int arity) {
    return term instanceof Apply && ((Apply) term).getOperator() instanceof Name && ((Apply) term).arity() == arity;
  }

  public static boolean isTernary(IAbstract term) {
    return isApply(term, 3);
  }

  public static boolean isTernary(IAbstract term, String op) {
    return isApply(term, op, 3);
  }

  public static IAbstract ternaryLhs(IAbstract term) {
    assert isTernary(term);
    return getArg(term, 0);
  }

  public static IAbstract ternaryMid(IAbstract term) {
    assert isTernary(term);
    return getArg(term, 1);
  }

  public static IAbstract ternaryRhs(IAbstract term) {
    assert isTernary(term);
    return getArg(term, 2);
  }

  public static boolean isBinary(IAbstract term, String op) {
    return isApply(term, op, 2);
  }

  public static boolean isBinary(IAbstract term) {
    return term instanceof Apply && arity(term) == 2;
  }

  public static IAbstract binaryLhs(IAbstract term) {
    assert isBinary(term);
    return getArg(term, 0);
  }

  public static IAbstract binaryRhs(IAbstract term) {
    assert isBinary(term);
    return getArg(term, 1);
  }

  public static boolean isUnary(IAbstract term, String op) {
    return isApply(term, op, 1);
  }

  public static boolean isUnary(IAbstract term) {
    return term instanceof Apply && Abstract.arity(term) == 1;
  }

  public static IAbstract unary(Location loc, String op, IAbstract lhs) {
    return new Apply(loc, op, lhs);
  }

  public static IAbstract unary(Location loc, IAbstract op, IAbstract lhs) {
    return new Apply(loc, op, new IAbstract[]{lhs});
  }

  public static IAbstract unaryArg(IAbstract term) {
    assert isUnary(term);
    return getArg(term, 0);
  }

  public static IAbstract zeroary(Location loc, String op) {
    return new Apply(loc, op);
  }

  public static IAbstract zeroary(Location loc, IAbstract op) {
    return new Apply(loc, op);
  }

  public static boolean isZeroary(IAbstract exp) {
    return exp instanceof Apply && Abstract.arity(exp) == 0;
  }

  public static boolean isZeroary(IAbstract exp, String name) {
    return exp instanceof Apply && Abstract.arity(exp) == 0 && isName(((Apply) exp).getOperator(), name);
  }

  public static String zeroOp(IAbstract exp) {
    assert isZeroary(exp);

    return getId(((Apply) exp).getOperator());
  }

  public static IAbstract binary(Location loc, String op, IAbstract lhs, IAbstract rhs) {
    return new Apply(loc, op, lhs, rhs);
  }

  public static IAbstract binary(Location loc, IAbstract op, IAbstract lhs, IAbstract rhs) {
    return new Apply(loc, op, lhs, rhs);
  }

  public static IAbstract ternary(Location loc, String op, IAbstract lhs, IAbstract mhs, IAbstract rhs) {
    return new Apply(loc, op, lhs, mhs, rhs);
  }

  public static IAbstract ternary(Location loc, IAbstract op, IAbstract lhs, IAbstract mhs, IAbstract rhs) {
    return new Apply(loc, op, lhs, mhs, rhs);
  }

  public static Apply apply(Location loc, IAbstract op, IList args) {
    return new Apply(loc, op, args);
  }

  public static Apply apply(Location loc, String op, IAbstract... args) {
    return new Apply(loc, op, args);
  }

  public static Apply apply(Location loc, IAbstract op, IAbstract... args) {
    return new Apply(loc, op, args);
  }

  public static Apply apply(Location loc, IAbstract op, List<IAbstract> args) {
    return new Apply(loc, op, args);
  }

  public static IAbstract unary(Location loc, String op, IAbstract lhs, Object... atts) {
    Apply apply = new Apply(loc, op, lhs);
    setAttributes(apply, atts);
    return apply;
  }

  private static void setAttributes(IAbstract term, Object[] atts) {
    for (int ix = 0; ix < atts.length; ix += 2) {
      term.setAttribute((String) atts[ix], (IAttribute) atts[ix + 1]);
    }
  }

  public static IAbstract binary(Location loc, String op, IAbstract lhs, IAbstract rhs, Object... atts) {
    Apply apply = new Apply(loc, op, lhs, rhs);
    setAttributes(apply, atts);
    return apply;
  }

  public static IAbstract binary(Location loc, IAbstract op, IAbstract lhs, IAbstract rhs, Object... atts) {
    Apply apply = new Apply(loc, op, lhs, rhs);
    setAttributes(apply, atts);
    return apply;
  }

  public static IAbstract ternary(Location loc, String op, IAbstract lhs, IAbstract mhs, IAbstract rhs, Object... atts) {
    Apply apply = new Apply(loc, op, lhs, mhs, rhs);
    setAttributes(apply, atts);
    return apply;
  }

  public static IAbstract getArg(IAbstract term, int ix) {
    assert term instanceof Apply;
    return ((Apply) term).getArg(ix);
  }

  public static IAbstract argPath(IAbstract term, Integer... path) {
    for (Integer aPath : path) {
      assert term instanceof Apply;
      term = ((Apply) term).getArg(aPath);
    }
    return term;
  }

  public static IList getArgs(IAbstract ap) {
    assert ap instanceof Apply;
    return ((Apply) ap).getArgs();
  }

  public static boolean isTupleTerm(IAbstract term) {
    return term instanceof Apply && isTupleLabel(((Apply) term).getOperator());
  }

  public static boolean isTupleLabel(IAbstract trm) {
    return Abstract.isIdentifier(trm) && TypeUtils.isTupleLabel(Abstract.getId(trm));
  }

  public static boolean isTupleTerm(IAbstract term, int arity) {
    return term instanceof Apply && isIdentifier(((Apply) term).getOperator())
        && TypeUtils.isTupleLabel(((Apply) term).getOp()) && arity(term) == arity;
  }

  public static int tupleArity(IAbstract ptn) {
    assert ptn instanceof Apply;
    return ((Apply) ptn).arity();
  }

  public static IList tupleArgs(IAbstract term) {
    assert isTupleTerm(term);

    return ((Apply) term).getArgs();
  }

  public static IAbstract tupleArg(IAbstract term, int ix) {
    return (IAbstract) tupleArgs(term).getCell(ix);
  }

  public static IAbstract tupleTerm(Location loc, IList els) {
    String label = TypeUtils.tupleLabel(els.size());
    return new Apply(loc, new Name(loc, label), els);
  }

  public static IAbstract tupleTerm(Location loc, List<IAbstract> els) {
    String label = TypeUtils.tupleLabel(els.size());
    return new Apply(loc, new Name(loc, label), els);
  }

  public static IAbstract tupleTerm(Location loc, IAbstract... els) {
    String label = TypeUtils.tupleLabel(els.length);
    return new Apply(loc, new Name(loc, label), els);
  }

  public static boolean isRoundTerm(IAbstract term, String label) {
    return isApply(term, label);
  }

  public static boolean isRoundTerm(IAbstract term) {
    return term instanceof Apply && isIdentifier(((Apply) term).getOperator());
  }

  public static IList roundTermArgs(IAbstract term) {
    assert isRoundTerm(term);

    return ((Apply) term).getArgs();
  }

  public static String roundTermName(IAbstract term) {
    assert isRoundTerm(term);
    return getId(((Apply) term).getOperator());
  }
}
