package org.star_lang.star.operators.spawn.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinTask;

import org.star_lang.star.compiler.cafe.compile.Utils;
import org.star_lang.star.compiler.cafe.type.CafeTypeDescription;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IScalar;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.IValueVisitor;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.IValueSpecifier;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.type.UniversalType;
import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.CafeEnter;
import org.star_lang.star.operators.Intrinsics;

public class ForkJoin
{

  @SuppressWarnings("serial")
  public static class ForkJoinFuture extends ForkJoinTask<IValue>
  {

    // It's always ready/completed in the initial state, use reinitialize to set back
    ForkJoinFuture(IValue initial)
    {
      this.complete(initial);
    }

    private IValue result;

    @Override
    public IValue getRawResult()
    {
      return result;
    }

    @Override
    protected void setRawResult(IValue value)
    {
      result = value;
    }

    @Override
    protected boolean exec()
    {
      // never completes by itself, only if complete is called from outside
      return false;
    }

  }

  // Wrapper for ForkJoinTask objects in star
  @SuppressWarnings("serial")
  public static class ForkJoinTaskWrap implements IScalar<ForkJoinTask<IValue>>
  {
    private final IType type;
    private final ForkJoinTask<IValue> value;
    // private final IType valueType;
    public static final String name = "__forkJoinTask";

    public ForkJoinTaskWrap(ForkJoinTask<IValue> value, IType valueType)
    {
      // this.valueType = valueType;
      this.value = value;
      this.type = type(valueType);
    }

    @Override
    public IType getType()
    {
      return this.type;
    }

    @Override
    public IValue copy() throws EvaluationException
    {
      return this;
    }

    @Override
    public IValue shallowCopy() throws EvaluationException
    {
      return this;
    }

    @Override
    public void accept(IValueVisitor visitor)
    {
    }

    @Override
    public ForkJoinTask<IValue> getValue()
    {
      return this.value;
    }

    public static IType type(IType valueType)
    {
      return TypeUtils.typeExp(name, valueType);
    }

  }

  // special "degenerated" type of ForkJoinTask, which is "computed" asynchronously
  public static class CreateForkJoinTaskFuture implements IFunction
  {

    public static final String name = "__fjtFuture";

    @CafeEnter
    public static ForkJoinTaskWrap enter(IValue initial)
    {
      ForkJoinFuture v = new ForkJoinFuture(initial);
      return new ForkJoinTaskWrap(v, initial.getType());
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter(args[0]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar tv = new TypeVar();
      return new UniversalType(tv, TypeUtils.functionType(tv, ForkJoinTaskWrap.type(tv)));
    }
  }

  public static class ForkJoinTaskJoinReinit implements IFunction
  {

    public static final String name = "__fjtJoinReinit";

    @CafeEnter
    public static IValue enter(ForkJoinTaskWrap f)
    {
      IValue res = f.value.join();
      f.value.reinitialize();
      return res;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((ForkJoinTaskWrap) args[0]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar tv = new TypeVar();
      return new UniversalType(tv, TypeUtils.functionType(ForkJoinTaskWrap.type(tv), tv));
    }
  }

  public static class ForkJoinTaskComplete implements IFunction
  {

    public static final String name = "__fjtComplete";

    @CafeEnter
    public static IValue enter(ForkJoinTaskWrap f, IValue result)
    {
      f.value.complete(result);
      return StandardTypes.unit;
    }

    @Override
    public IValue enter(IValue... args) throws EvaluationException
    {
      return enter((ForkJoinTaskWrap) args[0], args[1]);
    }

    @Override
    public IType getType()
    {
      return type();
    }

    public static IType type()
    {
      TypeVar tv = new TypeVar();
      return new UniversalType(tv, TypeUtils.procedureType(ForkJoinTaskWrap.type(tv), tv));
    }
  }

  public static void declare(Intrinsics cxt)
  {
    TypeVar tv = new TypeVar();
    IType fjtType = TypeUtils.typeExp(ForkJoinTaskWrap.name, tv);

    List<IValueSpecifier> specs = new ArrayList<>();

    cxt.defineType(new CafeTypeDescription(Location.nullLoc, new UniversalType(tv, fjtType), Utils
        .javaInternalClassName(ForkJoinTaskWrap.class), specs));

    cxt.declareBuiltin(new Builtin(CreateForkJoinTaskFuture.name, CreateForkJoinTaskFuture.type(),
        CreateForkJoinTaskFuture.class));
    cxt.declareBuiltin(new Builtin(ForkJoinTaskJoinReinit.name, ForkJoinTaskJoinReinit.type(),
        ForkJoinTaskJoinReinit.class));
    cxt.declareBuiltin(new Builtin(ForkJoinTaskComplete.name, ForkJoinTaskComplete.type(), ForkJoinTaskComplete.class));
  }

}
