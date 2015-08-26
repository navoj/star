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
