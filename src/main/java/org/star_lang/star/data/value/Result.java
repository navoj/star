package org.star_lang.star.data.value;

import java.util.ArrayList;
import java.util.List;

import org.star_lang.star.compiler.cafe.type.CafeTypeDescription;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IConstructor;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.IValueVisitor;
import org.star_lang.star.data.type.ConstructorSpecifier;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.ITypeContext;
import org.star_lang.star.data.type.ITypeDescription;
import org.star_lang.star.data.type.IValueSpecifier;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.TypeExp;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.type.UniversalType;
import org.star_lang.star.operators.string.runtime.ValueDisplay;

/*
 * Copyright (c) 2015. Francis G. McCabe
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
/**
 * Builtin implementation of the result type:
 * 
 * type result of %a is success(%a) or error(exception) or denied;
 * 
 * @author fgm
 */
@SuppressWarnings("serial")
public abstract class Result implements IConstructor, PrettyPrintable
{
  public static final String typeLabel = "result";

  public abstract IValue getContent();

  public static class Success extends Result
  {
    public static final int conIx = 0;
    public static final String label = "success";

    private static final int valOffset = 0;

    private final IValue val;

    public Success(IValue val)
    {
      this.val = val;
    }

    @Override
    public int conIx()
    {
      return conIx;
    }

    @Override
    public String getLabel()
    {
      return label;
    }

    @Override
    public int size()
    {
      return 1;
    }

    @Override
    public IValue getCell(int index)
    {
      switch (index) {
      case valOffset:
        return val;
      default:
        throw new IllegalAccessError("index out of range");
      }
    }

    public IValue get___0()
    {
      return val;
    }

    @Override
    public IValue getContent()
    {
      return val;
    }

    @Override
    public IValue[] getCells()
    {
      return new IValue[] { val };
    }

    @Override
    public IConstructor copy() throws EvaluationException
    {
      return new Success(val.copy());
    }

    @Override
    public IConstructor shallowCopy() throws EvaluationException
    {
      return new Success(val);
    }

    @Override
    public IType getType()
    {
      return new TypeExp(typeLabel, val.getType());
    }

    public static IType conType()
    {
      TypeVar tv = new TypeVar();
      return new UniversalType(tv, TypeUtils.tupleConstructorType(tv, new TypeExp(typeLabel, tv)));
    }

    @Override
    public boolean equals(Object obj)
    {
      if (obj instanceof Success) {
        Success cons = (Success) obj;
        return cons.val.equals(val);
      } else
        return false;
    }

    @Override
    public int hashCode()
    {
      return (label.hashCode() * 37) + +val.hashCode();
    }
  }

  public static class Failed extends Result
  {
    public static final int conIx = 1;
    public static final String label = "failed";

    private static final int valOffset = 0;

    private final IValue val;

    public Failed(IValue val)
    {
      this.val = val;
    }

    @Override
    public int conIx()
    {
      return conIx;
    }

    @Override
    public String getLabel()
    {
      return label;
    }

    @Override
    public int size()
    {
      return 1;
    }

    @Override
    public IValue getCell(int index)
    {
      switch (index) {
      case valOffset:
        return val;
      default:
        throw new IllegalAccessError("index out of range");
      }
    }

    public IValue get___0()
    {
      return val;
    }

    @Override
    public IValue getContent()
    {
      return val;
    }

    @Override
    public IValue[] getCells()
    {
      return new IValue[] { val };
    }

    @Override
    public IConstructor copy() throws EvaluationException
    {
      return new Success(val.copy());
    }

    @Override
    public IConstructor shallowCopy() throws EvaluationException
    {
      return new Success(val);
    }

    @Override
    public IType getType()
    {
      return new TypeExp(typeLabel, val.getType());
    }

    public static IType conType()
    {
      TypeVar tv = new TypeVar();
      return new UniversalType(tv, TypeUtils.tupleConstructorType(tv, new TypeExp(typeLabel, tv)));
    }

    @Override
    public boolean equals(Object obj)
    {
      if (obj instanceof Failed) {
        Failed cons = (Failed) obj;
        return cons.val.equals(val);
      } else
        return false;
    }

    @Override
    public int hashCode()
    {
      return (label.hashCode() * 37) + +val.hashCode();
    }
  }

  public static class Denied extends Result
  {
    public static final int conIx = 2;
    public static final String label = "denied";

    private static final int valOffset = 0;

    private final Reason reason;

    public Denied(Reason reason)
    {
      this.reason = reason;
    }

    public Denied(IValue reason)
    {
      this.reason = (Reason) reason;
    }

    public Reason getReason()
    {
      return reason;
    }

    @Override
    public int conIx()
    {
      return conIx;
    }

    @Override
    public String getLabel()
    {
      return label;
    }

    @Override
    public int size()
    {
      return 1;
    }

    @Override
    public IValue getCell(int index)
    {
      switch (index) {
      case valOffset:
        return reason;
      default:
        throw new IllegalAccessError("index out of range");
      }
    }

    public IValue get___0()
    {
      return reason;
    }

    @Override
    public IValue getContent()
    {
      return reason;
    }

    @Override
    public IValue[] getCells()
    {
      return new IValue[] { reason };
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
    public IType getType()
    {
      return new TypeExp(typeLabel, new TypeVar());
    }

    public static IType conType()
    {
      TypeVar tv = new TypeVar();
      return new UniversalType(tv, TypeUtils.constructorType(Reason.type, new TypeExp(typeLabel, tv)));
    }

    @Override
    public boolean equals(Object obj)
    {
      return obj instanceof Denied;
    }

    @Override
    public int hashCode()
    {
      return label.hashCode();
    }
  }

  @Override
  public void setCell(int index, IValue value) throws EvaluationException
  {
    throw new IllegalAccessError("not permitted");
  }

  @Override
  public void accept(IValueVisitor visitor)
  {
    visitor.visitConstructor(this);
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    ValueDisplay.display(disp, this);
  }

  public static Result success(IValue el)
  {
    return new Success(el);
  }

  public static Result failed(EvaluationException e)
  {
    return new Failed(e);
  }

  public static Result denied(Reason reason)
  {
    return new Denied(reason);
  }

  public static void declare(ITypeContext cxt)
  {
    TypeVar tv = new TypeVar();

    IType resultType = new TypeExp(typeLabel, tv); // avoid issues with initialization of
    // intrinsics
    IType conConType = new UniversalType(tv, TypeUtils.tupleConstructorType(tv, resultType));
    Location nullLoc = Location.nullLoc;

    ConstructorSpecifier someSpec = new ConstructorSpecifier(nullLoc, null, Success.label, Success.conIx, conConType,
        Success.class, Result.class);

    IType failConType = new UniversalType(tv, TypeUtils.tupleConstructorType(EvaluationException.type, resultType));
    ConstructorSpecifier failSpec = new ConstructorSpecifier(nullLoc, null, Failed.label, Failed.conIx, failConType,
        Failed.class, Result.class);

    IType nilConType = new UniversalType(tv, TypeUtils.constructorType(resultType));
    ConstructorSpecifier noneSpec = new ConstructorSpecifier(nullLoc, null, Denied.label, Denied.conIx, nilConType,
        Denied.class, Result.class);

    List<IValueSpecifier> specs = new ArrayList<>();
    specs.add(someSpec);
    specs.add(failSpec);
    specs.add(noneSpec);

    ITypeDescription desc = new CafeTypeDescription(nullLoc, new UniversalType(tv, resultType), Result.class.getName(),
        specs);

    cxt.defineType(desc);
  }
}
