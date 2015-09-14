package org.star_lang.star.operators.system.runtime;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.operators.CafeEnter;

public class SimpleLog implements IFunction
{
  private static final Logger logger = Logger.getAnonymousLogger();
  public static final String name = "__log";

  @Override
  public IType getType()
  {
    return type();
  }

  public static IType type()
  {
    return TypeUtils.procedureType(StandardTypes.stringType);
  }

  @CafeEnter
  public static IValue enter(IValue msg) throws EvaluationException
  {
    if (logger.isLoggable(Level.INFO))
      logger.logp(Level.INFO, "", "", msg.toString());
    return StandardTypes.unit;
  }

  @Override
  public IValue enter(IValue... args) throws EvaluationException
  {
    return enter(args[0]);
  }
}
