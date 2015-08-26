package org.star_lang.star.operators.asynchio.runtime;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.data.value.Result;
import org.star_lang.star.operators.CafeEnter;

public class AsynchSocketOps
{
  private static final IOCompletionHandler ioCompletionHandler = new IOCompletionHandler();
  private static final AcceptCompletionHandler acceptCompletionHandler = new AcceptCompletionHandler();

  public static class AsynchTcpConnect implements IFunction
  {
    public static final String name = "__tcp_connect";

    @CafeEnter
    public static IValue enter(String host, int port, IFunction callback) throws EvaluationException
    {
      try {
        InetAddress addr = InetAddress.getByName(host);

        AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open();
        SocketChnl ioChannel = new SocketChnl(socketChannel);

        // Kick off connection establishment
        socketChannel.connect(new InetSocketAddress(addr, port), callback, new ConstCompletionHandler(ioChannel));
        return StandardTypes.unit;
      } catch (IOException e) {
        throw new EvaluationException(e.getMessage());
      }
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter(Factory.stringValue(args[0]), Factory.intValue(args[1]), (IFunction) args[2]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      // (string,integer, (result of __asynchChannel)=>() )=>()

      return TypeUtils.procedureType(StandardTypes.rawStringType, StandardTypes.rawIntegerType, TypeUtils
          .procedureType(TypeUtils.resultType(SocketChnl.type)));
    }
  }

  public static class AsynchTcpListen implements IFunction
  {
    public static final String name = "__tcp_listen";

    @CafeEnter
    public static IValue enter(int port) throws EvaluationException
    {
      try {
        InetSocketAddress addr = new InetSocketAddress(port);

        AsynchronousServerSocketChannel listenChannel = AsynchronousServerSocketChannel.open().bind(addr);

        return new ServerChnl(listenChannel, port);
      } catch (IOException e) {
        throw new EvaluationException(e.getMessage());
      }
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter(Factory.intValue(args[0]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      // (integer_) => __serverSocket
      return TypeUtils.procedureType(StandardTypes.rawIntegerType, ServerChnl.type);
    }
  }

  public static class AsynchTcpAccept implements IFunction
  {
    public static final String name = "__tcp_accept";

    @CafeEnter
    public static IValue enter(ServerChnl listen, IFunction callback) throws EvaluationException
    {
      listen.getListenChannel().accept(callback, acceptCompletionHandler);

      return StandardTypes.unit;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((ServerChnl) args[0], (IFunction) args[1]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      // (__listenSocket, (result of __asynchChannel)=>() )=>()

      return TypeUtils.procedureType(ServerChnl.type, TypeUtils.procedureType(TypeUtils.resultType(SocketChnl.type)));
    }
  }

  public static class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, IFunction>
  {

    @Override
    public void completed(AsynchronousSocketChannel channel, IFunction att)
    {
      try {
        att.enter(Result.success(new SocketChnl(channel)));
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

  public static class AsynchTcpRead implements IFunction
  {
    public static final String name = "__asynch_read";

    @CafeEnter
    public static IValue enter(SocketChnl channel, IFunction callback) throws EvaluationException
    {
      channel.getChannel().read(channel.getInputBuffer(), callback, ioCompletionHandler);
      return StandardTypes.unit;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((SocketChnl) args[0], (IFunction) args[1]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      // (__asynchChannel, (result of integer)=>() )=>()

      return TypeUtils.procedureType(SocketChnl.type, TypeUtils.procedureType(TypeUtils
          .resultType(StandardTypes.integerType)));
    }
  }

  public static class AsynchTcpWrite implements IFunction
  {
    public static final String name = "__asynch_write";

    @CafeEnter
    public static IValue enter(SocketChnl channel, IFunction callback) throws EvaluationException
    {
      channel.getChannel().write(channel.getOutputBuffer(), callback, ioCompletionHandler);
      return StandardTypes.unit;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((SocketChnl) args[0], (IFunction) args[1]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      // (__asynchChannel, (result of integer)=>() )=>()

      return TypeUtils.procedureType(SocketChnl.type, TypeUtils.procedureType(TypeUtils
          .resultType(StandardTypes.integerType)));
    }
  }

  public static class AsynchTcpClose implements IFunction
  {
    public static final String name = "__asynch_close";

    @CafeEnter
    public static IValue enter(SocketChnl channel) throws EvaluationException
    {
      try {
        channel.getChannel().close();
        return StandardTypes.unit;
      } catch (IOException e) {
        throw new EvaluationException(e.getMessage());
      }
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((SocketChnl) args[0]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      // (__asynchChannel)=>()
      return TypeUtils.procedureType(SocketChnl.type);
    }
  }

  public static class AsynchServerClose implements IFunction
  {
    public static final String name = "__tcp_listen_close";

    @CafeEnter
    public static IValue enter(ServerChnl server) throws EvaluationException
    {
      try {
        server.getListenChannel().close();
        return StandardTypes.unit;
      } catch (IOException e) {
        throw new EvaluationException(e.getMessage());
      }
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((ServerChnl) args[0]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      // (__serverSocket)=>()
      return TypeUtils.procedureType(ServerChnl.type);
    }
  }
}
