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
public class AstMacroKey implements IFunction
{
  public static final String name = "astMacroKey";

  private static final String decimalKey = "%decimal";
  private static final String booleanKey = "%bool";
  private static final String floatKey = "%float";
  private static final String integerKey = "%integer";
  private static final String longKey = "%long";

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
