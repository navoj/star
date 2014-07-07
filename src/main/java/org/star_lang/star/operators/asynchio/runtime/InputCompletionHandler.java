package org.star_lang.star.operators.asynchio.runtime;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.data.value.NTuple;
import org.star_lang.star.data.value.Result;

class InputCompletionHandler implements CompletionHandler<Integer, IFunction>
{
  private final ByteBuffer buffer;

  public InputCompletionHandler(ByteBuffer buffer)
  {
    this.buffer = buffer;
  }

  @Override
  public void completed(Integer result, IFunction att)
  {
    try {
      buffer.flip();

      String content = new String(buffer.array(), 0, buffer.limit());
      att.enter(Result.success(NTuple.tuple(Factory.newInt(result), Factory.newString(content))));
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