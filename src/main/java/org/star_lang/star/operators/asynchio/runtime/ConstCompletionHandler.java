package org.star_lang.star.operators.asynchio.runtime;

import java.nio.channels.CompletionHandler;

import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.value.Result;

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
    } catch (EvaluationException ignored) {
    }
  }

  @Override
  public void failed(Throwable t, IFunction att)
  {
    try {
      EvaluationException e = t instanceof EvaluationException ? (EvaluationException) t : new EvaluationException(
          "failed", t);
      att.enter(Result.failed(e));
    } catch (EvaluationException ignored) {
    }
  }
}
