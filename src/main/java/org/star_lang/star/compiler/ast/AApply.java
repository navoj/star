package org.star_lang.star.compiler.ast;

import java.util.List;
import java.util.Map;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.ListUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IArray;
import org.star_lang.star.data.IConstructor;
import org.star_lang.star.data.IList;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.ConstructorSpecifier;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.value.Array;

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

@SuppressWarnings("serial")
public class AApply extends ASyntax
{
  public static final String name = "applyAst";

  private static final int locIndex = 0;
  private static final int opIndex = 1;
  private static final int argIndex = 2;
  public static final IType conType = TypeUtils.tupleConstructorType(Location.type, ASyntax.type, TypeUtils
      .arrayType(ASyntax.type), ASyntax.type);

  private final IAbstract op;
  private final IList args;

  public AApply(Location loc, IAbstract op, IList args)
  {
    super(loc);
    this.op = op;
    this.args = args;
  }

  public AApply(Location loc, IAbstract op, IList args, List<String> categories, Map<String, IAttribute> attributes)
  {
    super(loc, categories, attributes);
    this.op = op;
    this.args = args;
  }

  public AApply(Location loc, String op, IList args)
  {
    this(loc, new Name(loc, op), args);
  }

  public AApply(Location loc, String op, List<IAbstract> args)
  {
    this(loc, new Name(loc, op), new Array(args));
    assert ListUtils.assertNoNulls(args);
  }

  public AApply(Location loc, String op, IAbstract... args)
  {
    this(loc, new Name(loc, op), Array.newArray(args));
    assert ListUtils.assertNoNulls(args);
  }

  public AApply(Location loc, IAbstract op, List<IAbstract> args)
  {
    this(loc, op, new Array(args));
    assert op != null && ListUtils.assertNoNulls(args);
  }

  public AApply(Location loc, IAbstract op, List<IAbstract> args, Map<String, IAttribute> attributes)
  {
    super(loc, attributes);
    this.op = op;
    this.args = new Array(args);

    assert op != null && ListUtils.assertNoNulls(args);
  }

  public AApply(Location loc, IAbstract op, List<IAbstract> args, List<String> categories,
      Map<String, IAttribute> attributes)
  {
    super(loc, categories, attributes);
    this.op = op;
    this.args = new Array(args);

    assert op != null && ListUtils.assertNoNulls(args);
  }

  public AApply(Location loc, IAbstract op, IAbstract... args)
  {
    this(loc, op, Array.newArray(args));

    assert op != null & ListUtils.assertNoNulls(args);
  }

  public AApply(IValue loc, IValue op, IValue args)
  {
    super((Location) loc);
    this.op = (IAbstract) op;
    this.args = (IArray) args;
  }

  @Override
  public astType astType()
  {
    return astType.Apply;
  }

  public static IType conType()
  {
    return conType;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof AApply) {
      AApply other = (AApply) obj;
      return other.op.equals(op) && other.args.equals(args);
    }
    return false;
  }

  @Override
  public int hashCode()
  {
    return ((name.hashCode() * 37) + op.hashCode() * 37) + args.hashCode();
  }

  public static ConstructorSpecifier spec()
  {
    return new ConstructorSpecifier(Location.nullLoc, null, name, applyIx, conType, AApply.class, ASyntax.class);
  }

  @Override
  public void accept(IAbstractVisitor visitor)
  {
    visitor.visitApply(this);
  }

  public String getOp()
  {
    return Abstract.getId(op);
  }

  public IAbstract getOperator()
  {
    return op;
  }

  public IList getArgs()
  {
    return args;
  }

  public IAbstract getArg(int ix)
  {
    return (IAbstract) args.getCell(ix);
  }

  @Override
  public boolean isApply(String op)
  {
    return Abstract.isName(this.op, op);
  }

  @Override
  public boolean isBinaryOperator(String op)
  {
    return Abstract.isName(this.op, op) && arity() == 2;
  }

  @Override
  public boolean isTernaryOperator(String op)
  {
    return Abstract.isName(this.op, op) && arity() == 3;

  }

  @Override
  public boolean isUnaryOperator(String op)
  {
    return Abstract.isName(this.op, op) && arity() == 1;
  }

  @Override
  public int conIx()
  {
    return applyIx;
  }

  @Override
  public String getLabel()
  {
    return name;
  }

  @Override
  public int size()
  {
    return 3;
  }

  public int arity()
  {
    return args.size();
  }

  @Override
  public IValue getCell(int index)
  {
    switch (index) {
    case locIndex:
      return getLoc();
    case opIndex:
      return op;
    case argIndex:
      return args;
    default:
      throw new IllegalArgumentException("index out of range");
    }
  }

  @Override
  public IValue[] getCells()
  {
    return new IValue[] { getLoc(), op, args };
  }

  public IValue get___1()
  {
    return op;
  }

  public IValue get___2() throws EvaluationException
  {
    return args;
  }

  @Override
  public IConstructor shallowCopy() throws EvaluationException
  {
    return new AApply(getLoc(), op, args);
  }
}
