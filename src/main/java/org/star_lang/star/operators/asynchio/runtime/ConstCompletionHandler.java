package org.star_lang.star.operators.asynchio.runtime;

import java.nio.channels.CompletionHandler;

import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.value.Result;

/*
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
/**
 * The VoidCompletionHandler is used when the result to return is predefined.
 * 
 * @author fgm
 * 
 */
public class ConstCompletionHandler implements CompletionHandler<Void, IFunction>
{
  private final IValue token;

  public ConstCompletionHandler(IValue token)
  {
    this.token = token;
  }

  @Override
  public void completed(Void result, IFunction att)
  {
    try {
      att.enter(Result.success(token));
    } catch (EvaluationException e) {
    }
  }

  @Override
  public void failed(Throwable t, IFunction att)
  {
    try {
      EvaluationException e = t instanceof EvaluationException ? (EvaluationException) t : new EvaluationException(
          "failed", t);
      att.enter(Result.failed(e));
    } catch (EvaluationException e) {
    }
  }
};
