package org.star_lang.star.operators;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.star_lang.star.compiler.cafe.type.CafeTypeDescription;
import org.star_lang.star.compiler.type.BuiltinInfo;
import org.star_lang.star.compiler.type.Dict;
import org.star_lang.star.compiler.type.Freshen;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.LayeredHash;
import org.star_lang.star.compiler.util.LayeredMap;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IMap;
import org.star_lang.star.data.ISet;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.ITypeDescription;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.type.TypeDescription;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.value.Array;
import org.star_lang.star.data.value.BoolWrap;
import org.star_lang.star.data.value.Cons;
import org.star_lang.star.data.value.FloatWrap;
import org.star_lang.star.data.value.IntWrap;
import org.star_lang.star.data.value.LongWrap;
import org.star_lang.star.data.value.NTuple;
import org.star_lang.star.data.value.Option;
import org.star_lang.star.data.value.Reason;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.data.value.Result;
import org.star_lang.star.data.value.StringWrap;
import org.star_lang.star.data.value.VoidWrap;
import org.star_lang.star.operators.arith.BoolCompare;
import org.star_lang.star.operators.arith.FloatBinary;
import org.star_lang.star.operators.arith.FloatCompare;
import org.star_lang.star.operators.arith.FloatTrig;
import org.star_lang.star.operators.arith.FloatUnary;
import org.star_lang.star.operators.arith.IntBinary;
import org.star_lang.star.operators.arith.IntCompare;
import org.star_lang.star.operators.arith.IntUnary;
import org.star_lang.star.operators.arith.IntegerBitString;
import org.star_lang.star.operators.arith.LongBinary;
import org.star_lang.star.operators.arith.LongBitString;
import org.star_lang.star.operators.arith.LongCompare;
import org.star_lang.star.operators.arith.LongUnary;
import org.star_lang.star.operators.arith.Number2Number;
import org.star_lang.star.operators.arith.NumericWrapper;
import org.star_lang.star.operators.arrays.ArrayOps;
import org.star_lang.star.operators.assignment.runtime.AtomicCell;
import org.star_lang.star.operators.assignment.runtime.AtomicInt;
import org.star_lang.star.operators.assignment.runtime.RefCell;
import org.star_lang.star.operators.ast.AstOperators;
import org.star_lang.star.operators.asynchio.AsynchIo;
import org.star_lang.star.operators.general.General;
import org.star_lang.star.operators.hash.HashTreeOps;
import org.star_lang.star.operators.misc.MiscOps;
import org.star_lang.star.operators.resource.ResourceOps;
import org.star_lang.star.operators.sets.SetOpsDecl;
import org.star_lang.star.operators.spawn.SpawnOps;
import org.star_lang.star.operators.string.DateOps;
import org.star_lang.star.operators.string.DisplayValue;
import org.star_lang.star.operators.string.KeyGen;
import org.star_lang.star.operators.string.Number2String;
import org.star_lang.star.operators.string.RegexpOps;
import org.star_lang.star.operators.string.String2Number;
import org.star_lang.star.operators.string.StringCompare;
import org.star_lang.star.operators.string.StringOps;
import org.star_lang.star.operators.string.StringWrappers;
import org.star_lang.star.operators.system.Clock;
import org.star_lang.star.operators.system.GStopHere;
import org.star_lang.star.operators.system.SystemUtils;
import org.star_lang.star.operators.uri.URIOps;

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
public class Intrinsics extends Dict {
  private static final Intrinsics intrinsics = new Intrinsics();

  private final LayeredMap<String, ICafeBuiltin> builtins = new LayeredHash<>();

  static {
    intrinsics.defineType(new TypeDescription(StandardTypes.rawBoolType));
    intrinsics.defineType(new TypeDescription(StandardTypes.rawIntegerType));
    intrinsics.defineType(new TypeDescription(StandardTypes.rawLongType));
    intrinsics.defineType(new CafeTypeDescription(StandardTypes.rawFloatType, Double.class.getCanonicalName()));
    intrinsics.defineType(new CafeTypeDescription(StandardTypes.rawStringType, String.class.getCanonicalName()));
    intrinsics.defineType(new CafeTypeDescription(StandardTypes.fileType, File.class.getCanonicalName()));
    intrinsics.defineType(new CafeTypeDescription(StandardTypes.voidType, IValue.class.getCanonicalName()));
    intrinsics.defineType(new CafeTypeDescription(StandardTypes.voidType, Object.class.getCanonicalName()));
    intrinsics.defineType(new CafeTypeDescription(StandardTypes.anyType, IValue.class.getCanonicalName()));
    intrinsics.defineType(new CafeTypeDescription(
        Freshen.generalizeType(TypeUtils.dictionaryType(new TypeVar(), new TypeVar())), IMap.class.getCanonicalName()));
    intrinsics.defineType(new CafeTypeDescription(Freshen.generalizeType(TypeUtils.setType(new TypeVar())),
        ISet.class.getCanonicalName()));

    // Define standard types
    VoidWrap.declare(intrinsics);
    EvaluationException.declare(intrinsics);
    BoolWrap.declare(intrinsics);
    StringWrap.declare(intrinsics);
    Location.declare(intrinsics);
    ResourceURI.declare(intrinsics);
    RefCell.declare();
    AtomicCell.declare(intrinsics);
    AtomicInt.declare(intrinsics);
    Cons.declare(intrinsics);
    Option.declare(intrinsics);
    Reason.declare();
    Result.declare(intrinsics);
    Array.declare();
    ArrayOps.declare();

    IntWrap.declare(intrinsics);
    LongWrap.declare(intrinsics);
    FloatWrap.declare(intrinsics);

    NumericWrapper.declare(intrinsics);
    StringWrappers.declare(intrinsics);

    LongBitString.declare(intrinsics);
    IntegerBitString.declare(intrinsics);

    SystemUtils.declare(intrinsics);
    Clock.declare(intrinsics);
    DisplayValue.declare(intrinsics);
    FloatBinary.declare(intrinsics);
    FloatCompare.declare(intrinsics);
    FloatUnary.declare(intrinsics);
    FloatTrig.declare(intrinsics);
    General.declare(intrinsics);
    IntBinary.declare(intrinsics);
    IntCompare.declare(intrinsics);
    IntUnary.declare(intrinsics);
    KeyGen.declare(intrinsics);
    LongBinary.declare(intrinsics);
    LongCompare.declare(intrinsics);
    LongUnary.declare(intrinsics);
    HashTreeOps.declare(intrinsics);
    SetOpsDecl.declare(intrinsics);
    MiscOps.declare(intrinsics);
    Number2Number.declare(intrinsics);
    RegexpOps.declare(intrinsics);
    AsynchIo.declare();
    SpawnOps.declare(intrinsics);
    BoolCompare.declare(intrinsics);
    StringOps.declare(intrinsics);
    String2Number.declare(intrinsics);
    StringCompare.declare(intrinsics);
    DateOps.declare(intrinsics);
    Number2String.declare(intrinsics);
    URIOps.declare();
    ResourceOps.declare(intrinsics);

    AstOperators.declare();

    NTuple.declare(intrinsics);

    GStopHere.declare(intrinsics);
  }

  private Intrinsics() {
    super(null);
  }

  /**
   * Declare a built-in function to the type context.
   * <p>
   * Although an instance value is passed to the {@code declareBuiltin} method,
   * this value is NOT used in actual code. It MUST be the case that a new copy
   * of the passed-in built-in is sufficient for execution.
   * <p>
   * I.e., the following code fragments MUST be equivalent:
   * <p>
   * 
   * <pre>
   * XX = builtin.enter(X, Y);
   * </pre>
   * <p>
   * and
   * <p>
   * 
   * <pre>
   * Class<? extends ICafeBuiltin> klass = builtin.getClass();
   *   ...
   * ICafeBuiltin nFun = klass.newInstance();
   *   ...
   * XX = nFun.enter(X,Y)
   * </pre>
   *
   * @param builtin
   *          an instance of the built-in function object.
   */

  public void declareBuiltin(ICafeBuiltin builtin) {
    String name = builtin.getName();
    assert !builtins.containsKey(name);
    builtins.put(name, builtin);
    declareVar(name, new BuiltinInfo(builtin));
  }

  public static void declare(ICafeBuiltin builtin) {
    intrinsics.declareBuiltin(builtin);
  }

  public static void declare(ITypeDescription desc) {
    intrinsics.defineType(desc);
  }

  /**
   * Retrieve a built-in operator associated with a given NAME.
   *
   * @param name
   * @return the built-in function object (if it exists) associated with a given
   *         NAME
   */

  public ICafeBuiltin getBuiltinOperator(String name) {
    return builtins.get(name);
  }

  public boolean isBuiltin(String name) {
    return builtins.containsKey(name);
  }

  public static Collection<? extends ICafeBuiltin> allBuiltins() {
    return intrinsics.builtins.values();
  }

  public static Collection<ITypeDescription> builtinTypes() {
    return intrinsics.getAllTypes().values();
  }

  public static ICafeBuiltin getBuiltin(String name) {
    return intrinsics.getBuiltinOperator(name);
  }

  public static boolean isIntrinsicType(String name) {
    return intrinsics.getTypeDescription(name) != null;
  }

  public static Intrinsics intrinsics() {
    return intrinsics;
  }

  public static Map<String, IType> standardTypes() {
    Map<String, IType> standards = new TreeMap<>();

    for (Entry<String, ITypeDescription> entry : intrinsics.getAllTypes().entrySet()) {
      standards.put(entry.getKey(), entry.getValue().getType());
    }

    return standards;
  }

}
