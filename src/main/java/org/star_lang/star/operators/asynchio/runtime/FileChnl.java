package org.star_lang.star.operators.asynchio.runtime;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;

import org.star_lang.star.compiler.cafe.type.CafeTypeDescription;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IScalar;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.IValueVisitor;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Type;
import org.star_lang.star.operators.Intrinsics;

@SuppressWarnings("serial")
public class FileChnl implements IScalar<FileChnl>, PrettyPrintable
{
  public static final String typeLabel = "__fileChannel";
  public static final IType type = new Type(typeLabel);

  private static final int CAPACITY = 80/*8192*/;

  private final ByteBuffer inputBuffer;
  private final ByteBuffer outputBuffer;
  private final AsynchronousFileChannel channel;

  public FileChnl(AsynchronousFileChannel channel)
  {
    this.inputBuffer = ByteBuffer.allocate(CAPACITY);
    this.outputBuffer = ByteBuffer.allocate(CAPACITY);
    this.channel = channel;
  }

  public AsynchronousFileChannel getChannel()
  {
    return channel;
  }

  public ByteBuffer getInputBuffer()
  {
    return inputBuffer;
  }

  public ByteBuffer getOutputBuffer()
  {
    return outputBuffer;
  }

  @Override
  public IType getType()
  {
    return type;
  }

  @Override
  public IValue copy() throws EvaluationException
  {
    throw new EvaluationException("not permitted");
  }

  @Override
  public IValue shallowCopy() throws EvaluationException
  {
    throw new EvaluationException("not permitted");
  }

  @Override
  public void accept(IValueVisitor visitor)
  {
    visitor.visitScalar(this);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append(typeLabel);
    disp.append(":");
    disp.append(channel.toString());
  }

  @Override
  public FileChnl getValue()
  {
    return this;
  }

  public static void declare()
  {
    Intrinsics.declare(new CafeTypeDescription(type, FileChnl.class.getCanonicalName()));
  }
}
