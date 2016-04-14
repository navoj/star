package org.star_lang.star.compiler.sources;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.ast.ASyntax;
import org.star_lang.star.compiler.cafe.compile.Types;
import org.star_lang.star.compiler.cafe.compile.Utils;
import org.star_lang.star.compiler.canonical.*;
import org.star_lang.star.compiler.type.Freshen;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.StringSequence;
import org.star_lang.star.compiler.util.Sequencer.SequenceException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.ITypeDescription;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.type.TypeConstraintException;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.ICafeBuiltin;
import org.star_lang.star.operators.arith.runtime.NumericWrapper.UnwrapBool;
import org.star_lang.star.operators.arith.runtime.NumericWrapper.UnwrapDbl;
import org.star_lang.star.operators.arith.runtime.NumericWrapper.UnwrapDouble;
import org.star_lang.star.operators.arith.runtime.NumericWrapper.UnwrapFloat;
import org.star_lang.star.operators.arith.runtime.NumericWrapper.UnwrapFlt;
import org.star_lang.star.operators.arith.runtime.NumericWrapper.UnwrapInt;
import org.star_lang.star.operators.arith.runtime.NumericWrapper.UnwrapInteger;
import org.star_lang.star.operators.arith.runtime.NumericWrapper.UnwrapLng;
import org.star_lang.star.operators.arith.runtime.NumericWrapper.UnwrapLong;
import org.star_lang.star.operators.arith.runtime.NumericWrapper.WrapBool;
import org.star_lang.star.operators.arith.runtime.NumericWrapper.WrapDbl;
import org.star_lang.star.operators.arith.runtime.NumericWrapper.WrapDouble;
import org.star_lang.star.operators.arith.runtime.NumericWrapper.WrapFloat;
import org.star_lang.star.operators.arith.runtime.NumericWrapper.WrapFlt;
import org.star_lang.star.operators.arith.runtime.NumericWrapper.WrapInt;
import org.star_lang.star.operators.arith.runtime.NumericWrapper.WrapInteger;
import org.star_lang.star.operators.arith.runtime.NumericWrapper.WrapLng;
import org.star_lang.star.operators.arith.runtime.NumericWrapper.WrapLong;
import org.star_lang.star.operators.string.runtime.StringWrappers.Raw2String;
import org.star_lang.star.operators.string.runtime.StringWrappers.String2Raw;
import org.star_lang.star.operators.string.runtime.StringWrappers.UnwrapChar;
import org.star_lang.star.operators.string.runtime.StringWrappers.WrapChar;
import org.star_lang.star.operators.system.runtime.RawWrappers.UnwrapRaw;
import org.star_lang.star.operators.system.runtime.RawWrappers.WrapRaw;

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

public class JavaImport {
  private static final String ifuncName = IFunction.class.getCanonicalName();
  public static final String PREFIX = "__java_";
  private static final String DEFINED_TYPES = "definedTypes";

  @SuppressWarnings("unchecked")
  public static JavaInfo importJavaSchema(String className, ClassLoader loader, ErrorReport errors) {
    try {
      Class<?> klass = loader.loadClass(className);
      SortedMap<String, ICafeBuiltin> funs = new TreeMap<>();
      generateSchema(klass, funs);

      if (implementsIFunc(klass))
        declareIFunc(funs, (Class<? extends IFunction>) klass, errors);

      for (Class<?> inner : klass.getClasses()) {
        if (implementsIFunc(inner))
          declareIFunc(funs, (Class<? extends IFunction>) inner, errors);
      }

      List<ITypeDescription> types = generateTypeSpecs(klass);

      return new JavaInfo(className, funs, types);
    } catch (ClassNotFoundException e) {
      return null;
    }
  }

  public static String javaName(String name) {
    return PREFIX + name;
  }

  private static void generateSchema(Class<?> klass, SortedMap<String, ICafeBuiltin> funs) {
    for (Method method : klass.getDeclaredMethods()) {
      int modifiers = method.getModifiers();
      if (!method.getName().equals(DEFINED_TYPES) && Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers)) {
        funs.put(method.getName(), generateJavaEscape(klass, method));
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static List<ITypeDescription> generateTypeSpecs(Class<?> klass) {
    try {
      for (Method method : klass.getMethods()) {
        int modifiers = method.getModifiers();
        if (method.getName().equals(DEFINED_TYPES) && Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers)) {
          if (method.getParameterTypes().length == 0 && method.getReturnType() == List.class) {
            return (List<ITypeDescription>) method.invoke(null);
          }
        }
      }
    } catch (Exception e) {
    }
    return new ArrayList<>();
  }

  private static boolean implementsIFunc(Class<?> klass) {
    int modifiers = klass.getModifiers();
    if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers)) {
      for (Type type : klass.getGenericInterfaces()) {
        if (type instanceof Class<?>) {
          Class<?> face = (Class<?>) type;
          if (face.getCanonicalName().equals(ifuncName))
            return true;
        }
      }
    }
    return false;
  }

  private static void declareIFunc(SortedMap<String, ICafeBuiltin> funs, Class<? extends IFunction> klass,
                                   ErrorReport errors) {
    try {
      String name = klass.getName();
      if (name.contains("$"))
        name = name.substring(name.lastIndexOf('$') + 1);
      funs.put(name, genIFuncEscape(name, klass));
    } catch (Exception e) {
      errors.reportWarning("problem in generating escape from IFunction: " + e.getMessage());
    }
  }

  private static ICafeBuiltin genIFuncEscape(final String name, final Class<? extends IFunction> klass)
      throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException,
      IllegalAccessException, InvocationTargetException {
    final IType type = klass.getConstructor().newInstance().getType();
    return new NestedBuiltin(name, type, klass);
  }

  private static ICafeBuiltin generateJavaEscape(Class<?> klass, Method method) {
    return new Builtin(method.getName(), methodType(method), klass, method);
  }

  private static IType methodType(Method method) {
    StringSequence str = new StringSequence(Utils.javaInvokeSig(method));

    List<IType> argTypes = new ArrayList<>();

    try {
      if (str.next() == '(') {
        while (str.peek() != ')')
          argTypes.add(javaType(str));
        if (str.next() != ')')
          throw new TypeConstraintException("invalid type");
        if (str.peek() == 'V')
          return TypeUtils.procedureType(argTypes);
        else
          return TypeUtils.functionType(argTypes, javaType(str));
      }
    } catch (SequenceException | TypeConstraintException e) {
      e.printStackTrace();
    }
    return StandardTypes.voidType;
  }

  private static IType javaType(StringSequence str) throws TypeConstraintException, SequenceException {
    switch (str.next().intValue()) {
      case 'Z':
        return StandardTypes.rawBoolType;
      case 'C':
        return StandardTypes.rawCharType;
      case 'I':
        return StandardTypes.rawIntegerType;
      case 'J':
        return StandardTypes.rawLongType;
      case 'D':
        return StandardTypes.rawFloatType;
      case 'L': {
        StringBuilder buff = new StringBuilder();
        for (; str.hasNext() && str.peek() != ';'; )
          buff.appendCodePoint(str.next());
        str.next(); // skip over trailing semi

        String buffContent = buff.toString();
        if (buffContent.equals(Types.JAVA_STRING_TYPE))
          return StandardTypes.rawStringType;
        else if (buffContent.equals(Types.JAVA_INTEGER_TYPE))
          return StandardTypes.rawIntegerType;
        else if (buffContent.equals(Types.JAVA_LONG_TYPE))
          return StandardTypes.rawLongType;
        else if (buffContent.equals(Types.JAVA_DECIMAL_TYPE))
          return StandardTypes.rawDecimalType;
        else if (buffContent.equals(Types.IVALUE))
          return new TypeVar();
        else if (buffContent.equals(Types.URI))
          return ResourceURI.type;
        else if (buffContent.equals(Types.QUOTED))
          return ASyntax.type;
        else
          return StandardTypes.rawBinaryType;
      }
      case '[':
      default:
        throw new TypeConstraintException("cannot handle java type: " + str.prev());
    }
  }

  /**
   * Convert a java imported function to a regular one by wrapping:
   * <p>
   * <p>
   * <pre>
   * string_ __Fun(integer_)
   * </pre>
   * <p>
   * -->
   * <p>
   * <p>
   * <pre>
   * Fun(I) is wrap_string(__javaFun(unwrap_int(I)))
   * </pre>
   */

  public static FunctionLiteral javaWrapper(String funName, JavaInfo info, Location loc, ErrorReport errors) {
    ICafeBuiltin builtin = info.getMethods().get(funName);
    if (builtin != null) {
      // We need to do this carefully because Java's types are not identical to Star's
      String javaInvokeSig = builtin.getJavaInvokeSignature();
      StringSequence seq = new StringSequence(javaInvokeSig);
      IType builtinType = builtin.getType();
      IType rawArgTypes[] = TypeUtils.getFunArgTypes(builtinType);

      IContentPattern args[] = new IContentPattern[rawArgTypes.length];
      IContentExpression eArgs[] = new IContentExpression[rawArgTypes.length];
      IType argTypes[] = new IType[rawArgTypes.length];

      try {
        if (seq.next() == '(') {

          int ix = 0;
          while (seq.peek() != ')') {
            String vName = "J_" + ix;
            IType cookedType = TypeUtils.cookedType(rawArgTypes[ix]);
            argTypes[ix] = cookedType;
            Variable var = new Variable(loc, cookedType, vName);
            args[ix] = var;
            eArgs[ix] = unwrap(var, seq, errors);
            ix++;
          }

          seq.next();
          final IContentExpression inner;
          final IType resultType;
          Variable escVar = new Variable(loc, builtinType, javaName(funName));

          if (seq.peek() == 'V') {
            inner = new ValofExp(loc, StandardTypes.unitType, new ProcedureCallAction(loc, escVar, new TupleTerm(loc, eArgs)),
                new ValisAction(loc, new VoidExp(loc)));
            resultType = StandardTypes.unitType;
          } else {
            resultType = TypeUtils.cookedType(TypeUtils.getFunResultType(builtinType));
            inner = wrap(Application.apply(loc, TypeUtils.getFunResultType(builtinType), escVar, eArgs), seq, errors);
          }

          IType functionType = Freshen.generalizeType(TypeUtils.functionType(argTypes, resultType));
          return new FunctionLiteral(loc, funName, functionType, args, inner, new Variable[]{escVar});
        } else {
          errors.reportError("invalid type signature for " + funName, loc);
          return null;
        }
      } catch (SequenceException e) {
        errors.reportError("cannot handle type signature for " + funName, loc);
        return null;
      } catch (ImportException e) {
        errors.reportError("cannot handle type signature for " + funName + "\nbecause " + e.getMessage(), loc);
        return null;
      }
    } else
      return null;
  }

  private static IContentExpression wrap(IContentExpression var, StringSequence javaSig, ErrorReport errors)
      throws SequenceException, ImportException {
    Location loc = var.getLoc();

    switch (javaSig.next().intValue()) {
      case 'Z':
        return unary(loc, StandardTypes.booleanType, WrapBool.WRAP_BOOL, WrapBool.type(), var);
      case 'C':
        return unary(loc, StandardTypes.charType, WrapChar.WRAP_CHAR, WrapChar.type(), var);
      case 'I':
        return unary(loc, StandardTypes.integerType, WrapInt.WRAP_INT, WrapInt.type(), var);
      case 'J':
        return unary(loc, StandardTypes.longType, WrapLng.WRAP_LNG, WrapLng.type(), var);
      case 'F':
        return unary(loc, StandardTypes.floatType, WrapFlt.WRAP_FLT, WrapFlt.type(), var);
      case 'D':
        return unary(loc, StandardTypes.floatType, WrapDbl.WRAP_DBL, WrapDbl.type(), var);
      case 'L': {
        StringBuilder buff = new StringBuilder();
        for (; javaSig.hasNext() && javaSig.peek() != ';'; )
          buff.appendCodePoint(javaSig.next());
        javaSig.next(); // skip over trailing semi

        String buffContent = buff.toString();
        if (buffContent.equals(Types.JAVA_STRING_TYPE))
          return unary(loc, StandardTypes.stringType, Raw2String.WRAP_STRING, Raw2String.type(), var);
        else if (buffContent.equals(Types.JAVA_INTEGER_TYPE))
          return unary(loc, StandardTypes.integerType, WrapInteger.WRAP_INTEGER, WrapInteger.type(), var);
        else if (buffContent.equals(Types.JAVA_LONG_TYPE))
          return unary(loc, StandardTypes.longType, WrapLong.WRAP_LONG, WrapLong.type(), var);
        else if (buffContent.equals(Types.JAVA_FLOAT_TYPE))
          return unary(loc, StandardTypes.floatType, WrapFloat.WRAP_FLOAT, WrapFloat.type(), var);
        else if (buffContent.equals(Types.JAVA_DOUBLE_TYPE))
          return unary(loc, StandardTypes.floatType, WrapDouble.WRAP_DOUBLE, WrapDouble.type(), var);
        else if (buffContent.equals(Types.IVALUE) || buffContent.equals(Types.URI) || buffContent.equals(Types.QUOTED))
          return var;
        else
          return unary(loc, StandardTypes.binaryType, WrapRaw.WRAP_RAW, WrapRaw.type(), var);
      }
      case '[':
      default:
        errors.reportError("cannot handle java type: " + javaSig.prev(), loc);
        throw new ImportException("cannot handle java type", loc);
    }
  }

  private static IContentExpression unary(Location loc, IType type, String name, IType fType, IContentExpression arg) {
    return Application.apply(loc, type, new Variable(loc, fType, name), arg);
  }

  private static IContentExpression unwrap(IContentExpression var, StringSequence javaSig, ErrorReport errors)
      throws ImportException, SequenceException {
    Location loc = var.getLoc();

    switch (javaSig.next().intValue()) {
      case 'Z':
        return unary(loc, StandardTypes.rawBoolType, UnwrapBool.UNWRAP_BOOL, UnwrapBool.type(), var);
      case 'C':
        return unary(loc, StandardTypes.rawCharType, UnwrapChar.UNWRAP_CHAR, UnwrapChar.type(), var);
      case 'I':
        return unary(loc, StandardTypes.rawIntegerType, UnwrapInt.UNWRAP_INT, UnwrapInt.type(), var);
      case 'J':
        return unary(loc, StandardTypes.rawLongType, UnwrapLng.UNWRAP_LNG, UnwrapLng.type(), var);
      case 'F':
        return unary(loc, StandardTypes.rawFloatType, UnwrapFlt.UNWRAP_FLT, UnwrapFlt.type(), var);
      case 'D':
        return unary(loc, StandardTypes.rawFloatType, UnwrapDbl.UNWRAP_DBL, UnwrapDbl.type(), var);
      case 'L': {
        StringBuilder buff = new StringBuilder();
        for (; javaSig.hasNext() && javaSig.peek() != ';'; )
          buff.appendCodePoint(javaSig.next());
        javaSig.next(); // skip over trailing semi

        String buffContent = buff.toString();
        if (buffContent.equals(Types.JAVA_STRING_TYPE))
          return unary(loc, StandardTypes.rawStringType, String2Raw.UNWRAP_STRING, String2Raw.type(), var);
        else if (buffContent.equals(Types.JAVA_INTEGER_TYPE))
          return unary(loc, StandardTypes.rawIntegerType, UnwrapInteger.UNWRAP_INTEGER, UnwrapInteger.type(), var);
        else if (buffContent.equals(Types.JAVA_LONG_TYPE))
          return unary(loc, StandardTypes.rawLongType, UnwrapLong.UNWRAP_LONG, UnwrapLong.type(), var);
        else if (buffContent.equals(Types.JAVA_FLOAT_TYPE))
          return unary(loc, StandardTypes.rawFloatType, UnwrapFloat.UNWRAP_FLOAT, UnwrapFloat.type(), var);
        else if (buffContent.equals(Types.JAVA_DOUBLE_TYPE))
          return unary(loc, StandardTypes.rawFloatType, UnwrapDouble.UNWRAP_DOUBLE, UnwrapDouble.type(), var);
        else if (buffContent.equals(Types.IVALUE) || buffContent.equals(Types.URI) || buffContent.equals(Types.QUOTED))
          return var;
        else
          return unary(loc, StandardTypes.rawBinaryType, UnwrapRaw.UNWRAP_RAW, UnwrapRaw.type(), var);
      }
      case '[':
      default:
        errors.reportError("cannot handle java type: " + javaSig.prev(), loc);
        throw new ImportException("cannot handle java type", loc);
    }
  }

  public static boolean isJavaName(String name) {
    return name.startsWith(PREFIX);
  }
}
