package org.star_lang.star.operators.asynchio.runtime;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Paths;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.operators.CafeEnter;

public class AsynchFileOps
{
  private static final IOCompletionHandler ioCompletionHandler = new IOCompletionHandler();

  public static class AsynchFileOpen implements IFunction
  {
    public static final String name = "__asynch_open_file";

    @CafeEnter
    public static IValue enter(String path) throws EvaluationException
    {
      try {
        return new FileChnl(AsynchronousFileChannel.open(Paths.get(path)));
      } catch (IOException e) {
        throw new EvaluationException("problem in open", e);
      }
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter(Factory.stringValue(args[0]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      // (string_) => __fileChannel

      return TypeUtils.functionType(StandardTypes.rawStringType, FileChnl.type);
    }
  }

  public static class AsynchFileRead implements IFunction
  {
    public static final String name = "__file_read";

    @CafeEnter
    public static IValue enter(FileChnl channel, long pos, IFunction callback) throws EvaluationException
    {
      ByteBuffer inputBuffer = channel.getInputBuffer();
      inputBuffer.clear();
      channel.getChannel().read(inputBuffer, pos, callback, new InputCompletionHandler(channel.getInputBuffer()));
      return StandardTypes.unit;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((FileChnl) args[0], Factory.lngValue(args[1]), (IFunction) args[2]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      // (__asynchChannel, long_(result of ((integer,string))=>() )=>()

      return TypeUtils.procedureType(FileChnl.type, StandardTypes.longType, TypeUtils.procedureType(TypeUtils
          .resultType(TypeUtils.tupleType(StandardTypes.integerType, StandardTypes.stringType))));
    }
  }

  public static class AsynchFileWrite implements IFunction
  {
    public static final String name = "__file_write";

    @CafeEnter
    public static IValue enter(FileChnl channel, long pos, IFunction callback) throws EvaluationException
    {
      channel.getChannel().write(channel.getOutputBuffer(), pos, callback, ioCompletionHandler);
      return StandardTypes.unit;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((FileChnl) args[0], Factory.lngValue(args[1]), (IFunction) args[2]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      // (__fileChannel, integer_,(result of integer)=>() )=>()

      return TypeUtils.procedureType(FileChnl.type, StandardTypes.rawIntegerType, TypeUtils.procedureType(TypeUtils
          .resultType(StandardTypes.integerType)));
    }
  }

  public static class AsynchFileClose implements IFunction
  {
    public static final String name = "__file_close";

    @CafeEnter
    public static IValue enter(FileChnl channel) throws EvaluationException
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
      return enter((FileChnl) args[0]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      // (__fileChannel)=>()
      return TypeUtils.procedureType(FileChnl.type);
    }
  }
}
