package org.star_lang.star.operators.ast.runtime;

import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.ast.*;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.operators.CafeEnter;

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
public class AstMacroKey implements IFunction
{
  public static final String name = "astMacroKey";

  public static final String decimalKey = "%decimal";
  public static final String booleanKey = "%bool";
  public static final String charKey = "%char";
  public static final String floatKey = "%float";
  public static final String integerKey = "%integer";
  public static final String longKey = "%long";

  @CafeEnter
  public static IValue enter(ASyntax term) throws EvaluationException
  {
    try {
      return Factory.newString(astMacroKey(term));
    } catch (EvaluationException e) {
      return Factory.nullString();
    }
  }

  @Override
  public IValue enter(IValue... args) throws EvaluationException
  {
    return enter((ASyntax) args[0]);
  }

  @Override
  public IType getType()
  {
    return funType();
  }

  public static IType funType()
  {
    return TypeUtils.functionType(ASyntax.type, StandardTypes.stringType);
  }

  public static String astMacroKey(IAbstract term) throws EvaluationException
  {
    if (Abstract.isBinary(term, StandardNames.WFF_DEFINES))
      return astMacroKey(Abstract.binaryLhs(term));
    else if (Abstract.isBinary(term, StandardNames.QUESTION))
      return astMacroKey(Abstract.binaryLhs(term));
    else if (Abstract.isBinary(term, StandardNames.BRACES)) {
      IAbstract lbl = Abstract.binaryLhs(term);
      if (Abstract.isName(lbl))
        return Abstract.getId(lbl) + StandardNames.BRACES;
      else
        return StandardNames.BRACES;
    } else if (Abstract.isUnary(term) && Abstract.isName(Abstract.unaryArg(term), StandardNames.BRACES)) {
      IAbstract lbl = ((Apply) term).getOperator();
      if (Abstract.isName(lbl))
        return Abstract.getId(lbl) + StandardNames.BRACES;
      else
        return StandardNames.BRACES;
    } else if (CompilerUtils.isSquareTerm(term))
      return StandardNames.SQUARE;
    else if (term instanceof Apply) {
      IAbstract op = ((Apply) term).getOperator();

      if (op instanceof Name)
        return ((Name) op).getId() + "()";
      else
        return "()@"; // no macro will match this
    } else if (term instanceof Name)
      return ((Name) term).getId();
    else if (term instanceof BooleanLiteral)
      return booleanKey;
    else if (term instanceof CharLiteral)
      return charKey;
    else if (term instanceof StringLiteral)
      return ((StringLiteral) term).getLit();
    else if (term instanceof IntegerLiteral)
      return integerKey;
    else if (term instanceof LongLiteral)
      return longKey;
    else if (term instanceof FloatLiteral)
      return floatKey;
    else if (term instanceof BigDecimalLiteral)
      return decimalKey;
    else
      throw new EvaluationException("cannot determine macro key of " + term);
  }
}
