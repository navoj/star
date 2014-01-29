package org.star_lang.star.operators.asynchio.runtime;

import java.nio.channels.CompletionHandler;

import com.starview.platform.data.EvaluationException;
import com.starview.platform.data.IFunction;
import com.starview.platform.data.value.Factory;
import com.starview.platform.data.value.Result;

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