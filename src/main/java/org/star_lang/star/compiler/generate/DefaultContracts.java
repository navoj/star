package org.star_lang.star.compiler.generate;

import java.util.HashMap;
import java.util.Map;

import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.canonical.IContentExpression;
import org.star_lang.star.compiler.canonical.MethodVariable;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.operators.general.runtime.GeneralEq;

/**
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
