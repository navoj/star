package org.star_lang.star.operators.string.runtime;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.operators.CafeEnter;

public abstract class CharCompare
{
  private static final IType rawCharType = StandardTypes.rawCharType;
  private static final IType booleanType = StandardTypes.booleanType;

  public static class CharEQ implements IFunction
  {
    @CafeEnter
    public static boolean enter(int ix1, int ix2)
    {
      return ix1 == ix2;
    }

    public static final String name = "__char_eq";

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(rawCharType, rawCharType, booleanType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newBool(enter(Factory.charValue(args[0]), Factory.charValue(args[1])));
    }
  }

  public static class CharNE implements IFunction
  {
    public static final String name = "__char_ne";

    @CafeEnter
    public static boolean enter(int ix1, int ix2)
    {
      return ix1 != ix2;
    }

    @Override
    public IType getType()
    {
      return TypeUtils.functionType(rawCharType, rawCharType, booleanType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newBool(enter(Factory.charValue(args[0]), Factory.charValue(args[1])));
    }
  }

  public static class CharLE implements IFunction
  {
    public static final String name = "__char_le";

    @CafeEnter
    public static boolean enter(int ix1, int ix2)
    {
      return ix1 <= ix2;
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(rawCharType, rawCharType, booleanType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newBool(enter(Factory.charValue(args[0]), Factory.charValue(args[1])));
    }
  }

  public static class CharLT implements IFunction
  {
    public static final String name = "__char_lt";

    @CafeEnter
    public static boolean enter(int ix1, int ix2)
    {
      return ix1 < ix2;
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(rawCharType, rawCharType, booleanType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newBool(enter(Factory.charValue(args[0]), Factory.charValue(args[1])));
    }
  }

  public static class CharGT implements IFunction
  {
    public static final String name = "__char_gt";

    @CafeEnter
    public static boolean enter(int ix1, int ix2)
    {
      return ix1 > ix2;
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(rawCharType, rawCharType, booleanType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newBool(enter(Factory.charValue(args[0]), Factory.charValue(args[1])));
    }
  }

  public static class CharGE implements IFunction
  {
    public static final String name = "__char_ge";

    @CafeEnter
    public static boolean enter(int ix1, int ix2)
    {
      return ix1 >= ix2;
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(rawCharType, rawCharType, booleanType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newBool(enter(Factory.charValue(args[0]), Factory.charValue(args[1])));
    }
  }

  public static class CharMin implements IFunction
  {
    public static final String name = "__char_min";

    @CafeEnter
    public static int enter(int ix1, int ix2)
    {
      return ix1 > ix2 ? ix2 : ix1;
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(rawCharType, rawCharType, rawCharType);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return Factory.newChar(enter(Factory.charValue(args[0]), Factory.charValue(args[1])));
    }
  }

}
