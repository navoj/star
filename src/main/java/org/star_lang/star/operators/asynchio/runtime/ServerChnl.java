package org.star_lang.star.operators.asynchio.runtime;

import java.nio.channels.AsynchronousServerSocketChannel;

import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IScalar;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.IValueVisitor;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Type;
import org.star_lang.star.data.type.TypeDescription;
import org.star_lang.star.operators.Intrinsics;

@SuppressWarnings("serial")
public class ServerChnl implements IScalar<ServerChnl>, PrettyPrintable
{
  public static final String typeLabel = "__listenChannel";
  public static final IType type = new Type(typeLabel);

  private final int port;
  private final AsynchronousServerSocketChannel listenChannel;

  public ServerChnl(AsynchronousServerSocketChannel listenChannel, int port)
  {
    this.port = port;
    this.listenChannel = listenChannel;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append(typeLabel);
    disp.append(":");
    disp.append(port);
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
  public ServerChnl getValue()
  {
    return this;
  }

  public AsynchronousServerSocketChannel getListenChannel()
  {
    return listenChannel;
  }

  public static void declare(Intrinsics intrinsics)
  {
    intrinsics.defineType(new TypeDescription(type));
  }
}
