package org.star_lang.star.operators.spawn.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.star_lang.star.compiler.cafe.compile.Utils;
import org.star_lang.star.compiler.cafe.type.CafeTypeDescription;
import org.star_lang.star.compiler.operator.StandardNames;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IConstructor;
import org.star_lang.star.data.IScalar;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.IValueVisitor;
import org.star_lang.star.data.type.ConstructorSpecifier;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.ITypeContext;
import org.star_lang.star.data.type.IValueSpecifier;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.type.UniversalType;

@SuppressWarnings("serial")
public abstract class SpawnIdent implements PrettyPrintable, IConstructor
{
  public static final NoSpawn nonThreadEnum = new NoSpawn();

  public static void declare(ITypeContext cxt)
  {
    TypeVar tv = new TypeVar();
    IType thrType = TypeUtils.typeExp(StandardNames.THREAD, tv); // We do not use
    // StandardTypes here

    IType conType = new UniversalType(tv, TypeUtils.tupleConstructorType(tv, thrType));

    ConstructorSpecifier strSpec = new ConstructorSpecifier(Location.nullLoc, null, StandardNames.THREAD,
        ThreadId.CONIX, conType, ThreadId.class, SpawnIdent.class);
    ConstructorSpecifier nonSpec = new ConstructorSpecifier(Location.nullLoc, null, StandardNames.NON_THREAD,
        NoSpawn.CONIX, conType, NoSpawn.class, SpawnIdent.class);

    List<IValueSpecifier> specs = new ArrayList<>();
    specs.add(strSpec);
    specs.add(nonSpec);
    cxt.defineType(new CafeTypeDescription(Location.nullLoc, new UniversalType(tv, thrType), Utils
        .javaInternalClassName(SpawnIdent.class), specs));
  }

  @Override
  public IType getType()
  {
    return TypeUtils.typeExp(StandardNames.THREAD, new TypeVar());
  }

  @Override
  public IConstructor copy() throws EvaluationException
  {
    return this;
  }

  @Override
  public IConstructor shallowCopy() throws EvaluationException
  {
    return this;
  }

  @Override
  public IValue getCell(int index)
  {
    throw new UnsupportedOperationException("not permitted");
  }

  @Override
  public IValue[] getCells()
  {
    throw new UnsupportedOperationException("not permitted");
  }

  @Override
  public void setCell(int index, IValue value) throws EvaluationException
  {
    throw new UnsupportedOperationException("not permitted");
  }

  @Override
  public void accept(IValueVisitor visitor)
  {
    visitor.visitConstructor(this);
  }

  public static class ThreadId extends SpawnIdent implements IScalar<Future<IValue>>
  {
    private static final int CONIX = 0;
    private final Future<IValue> future;
    private final IType type;

    public ThreadId(IType type, Future<IValue> future)
    {
      this.type = type;
      this.future = future;
    }

    @Override
    public int conIx()
    {
      return CONIX;
    }

    @Override
    public String getLabel()
    {
      return StandardNames.THREAD;
    }

    @Override
    public void accept(IValueVisitor visitor)
    {
      visitor.visitScalar(this);
    }

    @Override
    public void prettyPrint(PrettyPrintDisplay disp)
    {
      disp.append(StandardNames.THREAD);
      disp.append("(");
      disp.append(future.toString());
      disp.append(")");
    }

    @Override
    public int size()
    {
      return 0;
    }

    @Override
    public Future<IValue> getValue()
    {
      return future;
    }

    // This is here to allow Cafe to access the future
    public Future<IValue> get___0()
    {
      return future;
    }

    @Override
    public IType getType()
    {
      return TypeUtils.threadType(type);
    }
  }

  public static class NoSpawn extends SpawnIdent
  {
    private static final int CONIX = 1;

    @Override
    public int conIx()
    {
      return CONIX;
    }

    @Override
    public String getLabel()
    {
      return StandardNames.NON_THREAD;
    }

    @Override
    public void prettyPrint(PrettyPrintDisplay disp)
    {
      disp.appendWord(getLabel());
    }

    @Override
    public int size()
    {
      return 0;
    }

    @Override
    public boolean equals(Object arg0)
    {
      return arg0 instanceof NoSpawn;
    }

    @Override
    public int hashCode()
    {
      return super.hashCode();
    }

    @Override
    public IValue[] getCells()
    {
      return new IValue[] {};
    }
  }
}
