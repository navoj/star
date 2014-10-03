package org.star_lang.star.operators.system.runtime;

import java.util.Map.Entry;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IMap;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.operators.CafeEnter;

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
public class SystemUtils
{
  private static final IType stringType = StandardTypes.stringType;
  private static final IType integerType = StandardTypes.integerType;

  public static class Exit implements IFunction
  {
    public static final String name = "exit";

    @CafeEnter
    public static IValue enter(int code)
    {
      System.exit(code);
      return StandardTypes.unit;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter(Factory.intValue(args[0]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.procedureType(integerType);
    }
  }

  public static class GetEnv implements IFunction
  {
    public static final String name = "getenv";

    @CafeEnter
    public static IValue enter(IValue name, IValue deflt) throws EvaluationException
    {
      String envVar = System.getenv(Factory.stringValue(name));
      if (envVar == null)
        return deflt;
      return Factory.newString(envVar);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter(args[0], args[1]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(stringType, stringType, stringType);
    }
  }

  public static class Exec implements IFunction
  {
    public static final String name = "exec";

    @CafeEnter
    public static IValue enter(IValue cmd, IMap env) throws EvaluationException
    {
      String envp[] = null;

      if (!env.isEmpty()) {
        envp = new String[env.size()];
        int ix = 0;
        for (Entry<IValue, IValue> entry : env) {
          String key = Factory.stringValue(entry.getKey());
          String val = Factory.stringValue(entry.getValue());
          envp[ix++] = key + "=" + val;
        }
      }

      Runtime runTime = Runtime.getRuntime();
      try {
        return Factory.newInt(runTime.exec(Factory.stringValue(cmd), envp).waitFor());
      } catch (Exception e) {
        throw new EvaluationException(e.getMessage());
      }
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter(args[0], (IMap) args[1]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(stringType, TypeUtils.dictionaryType(stringType, stringType), StandardTypes.integerType);
    }
  }

  public static class Gc implements IFunction
  {
    public static final String name = "_gc";

    @CafeEnter
    public static IValue enter()
    {
      Runtime runTime = Runtime.getRuntime();
      runTime.gc();
      return StandardTypes.unit;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter();
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.procedureType();
    }
  }

  public static class AvailableProcessors implements IFunction
  {
    public static final String name = "_availableProcessors";

    @CafeEnter
    public static IValue enter()
    {
      Runtime runTime = Runtime.getRuntime();
      return Factory.newInt(runTime.availableProcessors());
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter();
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(StandardTypes.integerType);
    }
  }

  public static class FreeMemory implements IFunction
  {
    public static final String name = "_freeMemory";

    @CafeEnter
    public static long enter()
    {
      Runtime runTime = Runtime.getRuntime();
      return runTime.freeMemory();
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLong(enter());
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(StandardTypes.longType);
    }
  }

  public static class TotalMemory implements IFunction
  {
    public static final String name = "_totalMemory";

    @CafeEnter
    public static long enter()
    {
      Runtime runTime = Runtime.getRuntime();
      return runTime.totalMemory();
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLong(enter());
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(StandardTypes.longType);
    }
  }

  public static class MaxMemory implements IFunction
  {
    public static final String name = "_maxMemory";

    @CafeEnter
    public static long enter()
    {
      Runtime runTime = Runtime.getRuntime();
      return runTime.maxMemory();
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newLong(enter());
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(StandardTypes.longType);
    }
  }
}
