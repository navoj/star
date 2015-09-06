package org.star_lang.star.compiler.generate;

import java.util.HashMap;
import java.util.Map;

import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.canonical.IContentExpression;
import org.star_lang.star.compiler.canonical.MethodVariable;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.operators.general.runtime.GeneralEq;


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

public class DefaultContracts
{
  static private Map<String, ExpressionGenerator> contractDefaults = new HashMap<>();

  static {
    // contract defaults
    install(StandardNames.EQUALITY, new EqualityCompiler());
    install(StandardNames.PPRINT, new pPrintCompiler());
  }

  public static void install(String contract, ExpressionGenerator compiler)
  {
    assert !contractDefaults.containsKey(contract);
    contractDefaults.put(contract, compiler);
  }

  static public ExpressionGenerator getDefaultContract(String contractName)
  {
    return contractDefaults.get(contractName);
  }

  static public boolean hasDefaultImplementation(String contractName)
  {
    return contractDefaults.containsKey(contractName);
  }

  public static class pPrintCompiler implements ExpressionGenerator
  {
    @Override
    public IAbstract generateExpression(IContentExpression exp, boolean isDeep, CContext cxt)
    {
      return Abstract.name(exp.getLoc(), "pPrintAll");
    }

    @Override
    public Class<? extends IContentExpression> expressionClass()
    {
      return MethodVariable.class;
    }
  }

  public static class EqualityCompiler implements ExpressionGenerator
  {
    @Override
    public IAbstract generateExpression(IContentExpression exp, boolean isDeep, CContext cxt)
    {
      return Abstract.name(exp.getLoc(), GeneralEq.name);
    }

    @Override
    public Class<? extends IContentExpression> expressionClass()
    {
      return MethodVariable.class;
    }
  }
}
