package org.star_lang.star.operators.asynchio.runtime;

import java.nio.channels.CompletionHandler;

import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.data.value.Result;

class IOCompletionHandler implements CompletionHandler<Integer, IFunction>
{

  @Override
  public void completed(Integer result, IFunction att)
  {
    try {
      att.enter(Result.success(Factory.newInteger(result)));
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
}