package org.star_lang.star.operators.spawn.runtime;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RecursiveAction;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.type.UniversalType;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.operators.CafeEnter;
import org.star_lang.star.operators.spawn.runtime.SpawnIdent.ThreadId;

public class SpawnOps
{

  public static class SpawnExp implements IFunction
  {

    public static final String name = "__spawnExp";

    @CafeEnter
    public static SpawnIdent enter(IFunction function)
    {
      FutureTask<IValue> task = new FutureTask<>(new Task(function));
      Task.scheduleFuture(task);

      return new ThreadId(TypeUtils.getFunResultType(function.getType()), task);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((IFunction) args[0]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar tv = new TypeVar();
      return new UniversalType(tv, TypeUtils.functionType(TypeUtils.functionType(tv), TypeUtils.threadType(tv)));
    }
  }

  public static class SpawnAction implements IFunction
  {
    public static final String name = "__spawnAction";

    @CafeEnter
    public static SpawnIdent enter(IFunction function)
    {
      FutureTask<IValue> task = new FutureTask<>(new Task(function));
      Task.scheduleFuture(task);

      return new ThreadId(StandardTypes.voidType, task);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((IFunction) args[0]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(TypeUtils.procedureType(), TypeUtils.threadType(StandardTypes.voidType));
    }
  }

  public static class SpawnQueuedAction implements IFunction
  {

    public static final String name = "__spawnQueuedAction";

    @CafeEnter
    public static IValue enter(final IFunction function)
    {
      @SuppressWarnings("serial")
      ForkJoinTask<Void> task = new RecursiveAction() {
        @Override
        protected void compute()
        {
          try {
            function.enter();
          } catch (Exception t) {
            System.out.println("Task failed due to an unhandled exception: " + t.toString());
            t.printStackTrace();
            throw new RuntimeException(t);
          }
        }
      };
      Task.scheduleFutureQueued(task);

      return StandardTypes.unit;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((IFunction) args[0]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.procedureType(TypeUtils.procedureType());
    }
  }

  public static class SpawnDelayedAction implements IFunction
  {

    public static final String name = "__spawnDelayedAction";

    @CafeEnter
    public static SpawnIdent enter(IFunction function, long delayInMS)
    {
      FutureTask<IValue> task = new FutureTask<>(new Task(function));
      Task.scheduleFutureDelayed(task, delayInMS);

      return new ThreadId(StandardTypes.voidType, task);
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((IFunction) args[0], Factory.lngValue(args[1]));
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      return TypeUtils.functionType(TypeUtils.procedureType(), StandardTypes.rawLongType, TypeUtils
          .threadType(StandardTypes.voidType));
    }
  }

  public static class Waitfor implements IFunction
  {
    public static final String name = "__waitforThread";

    @CafeEnter
    public static IValue enter(SpawnIdent spawn) throws EvaluationException
    {
      if (spawn instanceof ThreadId) {
        ThreadId threadId = (ThreadId) spawn;
        try {
          return threadId.getValue().get();
        } catch (InterruptedException e) {
          throw new EvaluationException("spawned action: " + spawn + " interrupted");
        } catch (ExecutionException e) {
          if (e.getCause() instanceof EvaluationException)
            throw (EvaluationException) e.getCause();
          else
            throw new EvaluationException(e.getMessage(), Location.nullLoc);
        }
      } else
        throw new EvaluationException("cannot waitfor a non-thread");
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((SpawnIdent) args[0]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar tv = new TypeVar();
      return new UniversalType(tv, TypeUtils.functionType(TypeUtils.threadType(tv), tv));
    }
  }
}
