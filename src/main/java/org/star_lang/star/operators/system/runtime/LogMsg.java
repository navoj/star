package org.star_lang.star.operators.system.runtime;

import static org.star_lang.star.data.type.StandardTypes.rawStringType;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.data.value.NTuple;
import org.star_lang.star.operators.CafeEnter;

public class LogMsg implements IFunction
{
  private static final Logger logger = Logger.getAnonymousLogger();
  public static final String name = "__logMsg";

  @CafeEnter
  public static IValue __logMsg(String lvl, String cat, String msg) throws EvaluationException
  {
    Level level = Level.parse(lvl);

    if (logger.isLoggable(level)) {
      logger.logp(level, cat, "", msg);
    }
    return NTuple.$0Enum;
  }

  @Override
  public IValue enter(IValue... args) throws EvaluationException
  {
    return __logMsg(Factory.stringValue(args[0]), Factory.stringValue(args[1]), Factory.stringValue(args[2]));
  }

  @Override
  public IType getType()
  {
    return __logMsgType();
  }

  public static IType __logMsgType()
  {
    return TypeUtils.procedureType(rawStringType, rawStringType, rawStringType);
  }
}
