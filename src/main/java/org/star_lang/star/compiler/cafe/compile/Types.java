package org.star_lang.star.compiler.cafe.compile;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.star_lang.star.code.CafeCode;
import org.star_lang.star.code.repository.CodeCatalog;
import org.star_lang.star.code.repository.CodeRepository;
import org.star_lang.star.code.repository.ContextLoader;
import org.star_lang.star.code.repository.RepositoryException;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.ast.ASyntax;
import org.star_lang.star.compiler.cafe.Names;
import org.star_lang.star.compiler.cafe.type.CafeTypeDescription;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.StringIterator;
import org.star_lang.star.compiler.util.StringUtils;
import org.star_lang.star.data.ConstructorFunction;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IConstructor;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IPattern;
import org.star_lang.star.data.IRecord;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.IValueVisitor;
import org.star_lang.star.data.type.ExistentialType;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.ITypeContext;
import org.star_lang.star.data.type.ITypeVisitor;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.QuantifiedType;
import org.star_lang.star.data.type.TupleType;
import org.star_lang.star.data.type.Type;
import org.star_lang.star.data.type.TypeExp;
import org.star_lang.star.data.type.TypeInterface;
import org.star_lang.star.data.type.TypeInterfaceType;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.type.UniversalType;
import org.star_lang.star.data.value.AnonRecord;
import org.star_lang.star.data.value.BigNumWrap;
import org.star_lang.star.data.value.BoolWrap;
import org.star_lang.star.data.value.CharWrap;
import org.star_lang.star.data.value.FailureException;
import org.star_lang.star.data.value.FloatWrap;
import org.star_lang.star.data.value.IntWrap;
import org.star_lang.star.data.value.LongWrap;
import org.star_lang.star.data.value.NTuple;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.data.value.StringWrap;
import org.star_lang.star.data.value.NTuple.NTpl;
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


public class Types
{
  public static final String FUN_PREFIX = "function_";
  public static final String PRC_PREFIX = "procedure_";
  public static final String PTN_PREFIX = "pattern_";
  public static final String CON_PREFIX = "constructor_";
  public static final String TPL_PREFIX = "tuple_";

  public static final String JAVA_BOOL_SIG = "Z";
  public static final String JAVA_INT_SIG = "I";
  public static final String JAVA_LNG_SIG = "J";
  public static final String JAVA_DBL_SIG = "D";
  public static final String JAVA_INTEGER_TYPE = Utils.javaInternalClassName(Integer.class);
  public static final String JAVA_INTEGER_SIG = "L" + JAVA_INTEGER_TYPE + ";";
  public static final String JAVA_LONG_TYPE = Utils.javaInternalClassName(Long.class);
  public static final String JAVA_LONG_SIG = "L" + JAVA_LONG_TYPE + ";";
  public static final String JAVA_STRING_TYPE = Utils.javaInternalClassName(String.class);
  public static final String JAVA_STRING_SIG = "L" + JAVA_STRING_TYPE + ";";
  public static final String JAVA_STRING_ARRAY_TYPE = Utils.javaInternalClassName(String[].class);
  public static final String JAVA_BOOLEAN_TYPE = Utils.javaInternalClassName(Boolean.class);
  public static final String JAVA_FLOAT_TYPE = Utils.javaInternalClassName(Float.class);
  public static final String JAVA_FLOAT_SIG = "L" + JAVA_FLOAT_TYPE + ";";
  public static final String JAVA_DOUBLE_TYPE = Utils.javaInternalClassName(Double.class);
  public static final String JAVA_DOUBLE_SIG = "L" + JAVA_DOUBLE_TYPE + ";";
  public static final String JAVA_FILE_TYPE = Utils.javaInternalClassName(PrintStream.class);
  public static final String JAVA_FILE_SIG = "L" + JAVA_FILE_TYPE + ";";
  public static final String JAVA_DECIMAL_TYPE = Utils.javaInternalClassName(BigDecimal.class);
  public static final String JAVA_DECIMAL_SIG = "L" + JAVA_DECIMAL_TYPE + ";";
  public static final String JAVA_OBJECT_TYPE = Utils.javaInternalClassName(Object.class);
  public static final String JAVA_OBJECT_SIG = "L" + JAVA_OBJECT_TYPE + ";";
  public static final String BOOLEAN_TYPE = Utils.javaInternalClassName(BoolWrap.class);
  public static final String BOOLEAN_SIG = "L" + BOOLEAN_TYPE + ";";
  public static final String CHAR_TYPE = Utils.javaInternalClassName(CharWrap.class);
  public static final String CHAR_SIG = "L" + CHAR_TYPE + ";";
  public static final String INTEGER_TYPE = Utils.javaInternalClassName(IntWrap.class);
  public static final String INTEGER_SIG = "L" + INTEGER_TYPE + ";";
  public static final String LONG_TYPE = Utils.javaInternalClassName(LongWrap.class);
  public static final String LONG_SIG = "L" + LONG_TYPE + ";";
  public static final String FLOAT_TYPE = Utils.javaInternalClassName(FloatWrap.class);
  public static final String FLOAT_SIG = "L" + FLOAT_TYPE + ";";
  public static final String DECIMAL_TYPE = Utils.javaInternalClassName(BigNumWrap.class);
  public static final String DECIMAL_SIG = "L" + DECIMAL_TYPE + ";";
  public static final String STRING_TYPE = Utils.javaInternalClassName(StringWrap.class);
  public static final String STRING_SIG = "L" + STRING_TYPE + ";";
  public static final String IVALUE = Utils.javaInternalClassName(IValue.class);
  public static final String IVALUE_SIG = "L" + IVALUE + ";";
  public static final String IVALUE_ARRAY = Utils.javaInternalClassName(IValue[].class);
  public static final String IVALUEVISITOR = Utils.javaInternalClassName(IValueVisitor.class);
  public static final String IVALUEVISITOR_SIG = "L" + IVALUEVISITOR + ";";
  public static final String ITYPECONTEXT = Utils.javaInternalClassName(ITypeContext.class);
  public static final String ITYPECONTEXT_SIG = "L" + ITYPECONTEXT + ";";
  public static final String EVALUATION_EXCEPTION = Utils.javaInternalClassName(EvaluationException.class);
  public static final String FAILURE_EXCEPTION = Utils.javaInternalClassName(FailureException.class);
  public static final String EXCEPTION = Utils.javaInternalClassName(Exception.class);
  public static final String IFUNC = Utils.javaInternalClassName(IFunction.class);
  public static final String IFUNCTION_SIG = "L" + IFUNC + ";";
  public static final String IPATTERN = Utils.javaInternalClassName(IPattern.class);
  public static final String IPATTERN_SIG = "L" + IPATTERN + ";";
  public static final String ICONSTRUCTOR = Utils.javaInternalClassName(IConstructor.class);
  public static final String IRECORD = Utils.javaInternalClassName(IRecord.class);
  public static final String ICONSTRUCTOR_SIG = "L" + ICONSTRUCTOR + ";";
  public static final String ICONSTRUCTOR_FUNCTION = Utils.javaInternalClassName(ConstructorFunction.class);
  public static final String ICONSTRUCTOR_FUNCTION_SIG = "L" + ICONSTRUCTOR_FUNCTION + ";";
  public static final String IRECORD_SIG = "L" + IRECORD + ";";
  public static final String ANON_RECORD_TYPE = Utils.javaInternalClassName(AnonRecord.class);
  public static final String ANON_RECORD_SIG = "L" + ANON_RECORD_TYPE + ";";
  public static final String OBJECT = Utils.javaInternalClassName(Object.class);
  public static final String OBJECT_SIG = "L" + OBJECT + ";";
  public static final String COMPARABLE = Utils.javaInternalClassName(Comparable.class);
  public static final String EQUALS_SIG = "(" + OBJECT_SIG + ")Z";
  public static final String TUPLE_TYPE = Utils.javaInternalClassName(NTpl.class);
  public static final String HASH_SIG = "()I";
  public static final String INIT = "<init>";
  public static final String CLASS_INIT = "<clinit>";
  public static final String VOID_SIG = "()V";
  public static final String CLASS = Utils.javaInternalClassName(Class.class);
  public static final String CLASS_SIG = "L" + CLASS + ";";
  public static final String CLASSLOADER = Utils.javaInternalClassName(ClassLoader.class);
  public static final String CLASSLOADER_SIG = "L" + CLASSLOADER + ";";
  public static final String CONTEXTLOADER = Utils.javaInternalClassName(ContextLoader.class);
  public static final String CONTEXTLOADER_SIG = "L" + CONTEXTLOADER + ";";
  public static final String TYPEVAR = Utils.javaInternalClassName(TypeVar.class);
  public static final String TYPEVAR_SIG = "L" + TYPEVAR + ";";
  public static final String ITYPE = Utils.javaInternalClassName(IType.class);
  public static final String ITYPE_SIG = "L" + ITYPE + ";";
  public static final String GET_ITYPE_SIG = "()" + ITYPE_SIG;
  public static final String TYPEMAP = Utils.javaInternalClassName(TreeMap.class);
  public static final String TYPEMAP_SIG = "L" + TYPEMAP + ";";
  public static final String SORTEDMAP = Utils.javaInternalClassName(SortedMap.class);
  public static final String SORTEDMAP_SIG = "L" + SORTEDMAP + ";";
  public static final String MAP = Utils.javaInternalClassName(Map.class);
  public static final String MAP_SIG = "L" + MAP + ";";
  public static final String MAP_PUTSIG = "(" + OBJECT_SIG + OBJECT_SIG + ")" + OBJECT_SIG;
  public static final String MATCH_SIG = "(" + JAVA_STRING_SIG + ")[" + JAVA_STRING_SIG;
  public static final String URI = Utils.javaInternalClassName(ResourceURI.class);
  public static final String QUOTED = Utils.javaInternalClassName(ASyntax.class);

  public static final String TOSTRING = "toString";
  public static final String TOSTRING_SIG = "()" + JAVA_STRING_SIG;
  public static final String VALUEDISPLAY = Utils.javaInternalClassName(ValueDisplay.class);

  public static final String ENUM_SFX = "Enum";
  public static final String CON_SFX = "Con";

  public static String javaTypeSig(IType type, ITypeContext dict)
  {
    StringBuilder blder = new StringBuilder();
    JavaTypeSig sigBuilder = new JavaTypeSig(blder, dict);
    type.accept(sigBuilder, null);

    return blder.toString();
  }

  private static class JavaTypeSig implements ITypeVisitor<Void>
  {
    StringBuilder blder;
    private final ITypeContext dict;

    JavaTypeSig(StringBuilder blder, ITypeContext dict)
    {
      this.dict = dict;
      this.blder = blder;
    }

    @Override
    public void visitSimpleType(Type t, Void cxt)
    {
      String label = t.typeLabel();

      if (TypeUtils.isRawBoolType(label))
        blder.append("Z");
      else if (TypeUtils.isRawCharType(label))
        blder.append("C");
      else if (TypeUtils.isRawIntType(label))
        blder.append("I");
      else if (TypeUtils.isRawLongType(label))
        blder.append("J");
      else if (TypeUtils.isRawFloatType(label))
        blder.append("D");
      else if (TypeUtils.isRawStringType(label))
        blder.append(JAVA_STRING_SIG);
      else if (TypeUtils.isRawFileType(label))
        blder.append(JAVA_FILE_SIG);
      else if (TypeUtils.isRawDecimalType(label))
        blder.append(JAVA_DECIMAL_SIG);
      else {
        CafeTypeDescription desc = (CafeTypeDescription) dict.getTypeDescription(label);
        if (desc != null) {
          blder.append("L");
          blder.append(desc.getJavaName());
          blder.append(";");
        } else
          blder.append(IVALUE_SIG);
      }
    }

    @Override
    public void visitTypeExp(TypeExp type, Void cxt)
    {
      if (TypeUtils.isRawBoolType(type))
        blder.append("Z");
      else if (TypeUtils.isRawCharType(type))
        blder.append("C");
      else if (TypeUtils.isRawIntType(type))
        blder.append("I");
      else if (TypeUtils.isRawLongType(type))
        blder.append("J");
      else if (TypeUtils.isRawFloatType(type))
        blder.append("D");
      else if (TypeUtils.isRawBinaryType(type))
        blder.append(JAVA_OBJECT_SIG);
      else if (TypeUtils.isRawStringType(type))
        blder.append(JAVA_STRING_SIG);
      else if (TypeUtils.isRawFileType(type))
        blder.append(JAVA_FILE_SIG);
      else if (TypeUtils.isRawDecimalType(type))
        blder.append(JAVA_DECIMAL_SIG);
      else {
        CafeTypeDescription desc = (CafeTypeDescription) dict.getTypeDescription(type.typeLabel());
        if (desc != null) {
          blder.append("L");
          blder.append(desc.getJavaName());
          blder.append(";");
        } else
          blder.append(IVALUE_SIG);
      }
    }

    @Override
    public void visitTupleType(TupleType t, Void cxt)
    {
      blder.append(Types.TUPLE_TYPE);
    }

    @Override
    public void visitTypeVar(TypeVar var, Void cxt)
    {
      blder.append(IVALUE_SIG);
    }

    @Override
    public void visitExistentialType(ExistentialType t, Void cxt)
    {
      t.getBoundType().accept(this, cxt);
    }

    @Override
    public void visitUniversalType(UniversalType univ, Void cxt)
    {
      TypeUtils.unwrap(univ).accept(this, cxt);
    }

    @Override
    public void visitTypeInterface(TypeInterfaceType t, Void cxt)
    {
      blder.append("L");
      blder.append(Utils.javaIdentifierOf(t.typeLabel()));
      blder.append(";");
    }
  }

  public static String javaMethodSig(IType type)
  {
    StringBuilder blder = new StringBuilder();
    JavaMethodSig sigBuilder = new JavaMethodSig(blder);
    type.accept(sigBuilder, null);
    return blder.toString();
  }

  public static String javaConstructorSig(List<IType> argTypes)
  {
    StringBuilder blder = new StringBuilder();
    blder.append('(');
    JavaMethodSig visitor = new JavaMethodSig(blder);
    visitor.argTypesSig(TypeUtils.tupleType(argTypes));
    blder.append(')');
    blder.append('V');
    assert blder.length() < Short.MAX_VALUE;
    return blder.toString();
  }

  public static String javaConstructorSig(IType conType)
  {
    StringBuilder blder = new StringBuilder();
    JavaMethodSig methodSigGen = new JavaMethodSig(blder);

    blder.append('(');
    TypeUtils.getConstructorArgType(conType).accept(methodSigGen, null);
    blder.append(')');
    blder.append('V');

    assert blder.length() < Short.MAX_VALUE;
    return blder.toString();
  }

  private static class JavaMethodSig implements ITypeVisitor<Void>
  {
    StringBuilder blder;

    JavaMethodSig(StringBuilder blder)
    {
      this.blder = blder;
    }

    @Override
    public void visitSimpleType(Type t, Void cxt)
    {
      String label = t.typeLabel();

      if (TypeUtils.isRawBoolType(label))
        blder.append("Z");
      else if (TypeUtils.isRawCharType(label))
        blder.append("C");
      else if (TypeUtils.isRawIntType(label))
        blder.append("I");
      else if (TypeUtils.isRawLongType(label))
        blder.append("J");
      else if (TypeUtils.isRawFloatType(label))
        blder.append("D");
      else if (TypeUtils.isRawStringType(label))
        blder.append(JAVA_STRING_SIG);
      else if (TypeUtils.isRawFileType(label))
        blder.append(JAVA_FILE_SIG);
      else if (TypeUtils.isRawDecimalType(label))
        blder.append(JAVA_DECIMAL_SIG);
      else
        blder.append(IVALUE_SIG);
    }

    @Override
    public void visitTypeExp(TypeExp type, Void cxt)
    {
      if (TypeUtils.isFunType(type)) {
        blder.append('(');
        argTypesSig(TypeUtils.getFunArgType(type));
        blder.append(')');
        argTypeSig(TypeUtils.getFunResultType(type));
      } else if (TypeUtils.isPatternType(type)) {
        blder.append('(');
        argTypeSig(TypeUtils.getPtnMatchType(type));
        blder.append(')');
        argTypeSig(TypeUtils.getPtnResultType(type));
      } else if (TypeUtils.isConstructorType(type)) {
        blder.append('(');
        argTypesSig(TypeUtils.getConstructorArgType(type));
        blder.append(')');
        argTypeSig(TypeUtils.getConstructorResultType(type));
      } else if (TypeUtils.isTupleType(type))
        assert false : "should not happen (tpl 1)";
      else
        argTypeSig(type);
    }

    @Override
    public void visitTupleType(TupleType t, Void cxt)
    {
      argTypesSig(t);
    }

    private void argTypeSig(IType type)
    {
      if (TypeUtils.isRawBoolType(type))
        blder.append("Z");
      else if (TypeUtils.isRawCharType(type))
        blder.append("I");
      else if (TypeUtils.isRawIntType(type))
        blder.append("I");
      else if (TypeUtils.isRawLongType(type))
        blder.append("J");
      else if (TypeUtils.isRawFloatType(type))
        blder.append("D");
      else if (TypeUtils.isRawBinaryType(type))
        blder.append(JAVA_OBJECT_SIG);
      else if (TypeUtils.isRawStringType(type))
        blder.append(JAVA_STRING_SIG);
      else if (TypeUtils.isRawFileType(type))
        blder.append(JAVA_FILE_SIG);
      else if (TypeUtils.isRawDecimalType(type))
        blder.append(JAVA_DECIMAL_SIG);
      else
        blder.append(IVALUE_SIG);
    }

    void argTypesSig(IType tpl)
    {
      tpl = TypeUtils.unwrap(tpl);

      if (TypeUtils.isTupleType(tpl)) {
        IType[] args = TypeUtils.tupleTypes(tpl);
        if (args.length < Theta.MAX_ARGS)
          for (IType arg : args)
            argTypeSig(arg);
        else
          blder.append(IVALUE_ARRAY);
      } else if (TypeUtils.isTypeInterface(tpl)) {
        Map<String, IType> fields = TypeUtils.getInterfaceFields(tpl);
        if (fields.size() < Theta.MAX_ARGS) {
          for (Entry<String, IType> entry : fields.entrySet())
            argTypeSig(entry.getValue());
        } else
          blder.append(IVALUE_ARRAY);
      } else if (TypeUtils.isAnonRecordLabel(TypeUtils.typeLabel(tpl).typeLabel())) {
        IType[] args = TypeUtils.typeArgs(tpl);
        if (args.length < Theta.MAX_ARGS)
          for (IType arg : args)
            argTypeSig(arg);
        else
          blder.append(IVALUE_ARRAY);
      } else
        throw new IllegalArgumentException("invalid type for function args");
    }

    @Override
    public void visitTypeVar(TypeVar var, Void cxt)
    {
      blder.append(IVALUE_SIG);
    }

    @Override
    public void visitExistentialType(ExistentialType t, Void cxt)
    {
      t.getBoundType().accept(this, cxt);
    }

    @Override
    public void visitUniversalType(UniversalType univ, Void cxt)
    {
      TypeUtils.unwrap(univ).accept(this, cxt);
    }

    @Override
    public void visitTypeInterface(TypeInterfaceType t, Void cxt)
    {
      for (Entry<String, IType> entry : t.getAllFields().entrySet())
        argTypeSig(entry.getValue());
    }
  }

  public static String javaType(IType type, ITypeContext dict, CodeCatalog bldCat, ErrorReport errors, Location loc)
  {
    StringBuilder blder = new StringBuilder();
    JavaType sigBuilder = new JavaType(blder, dict, errors, loc);
    type.accept(sigBuilder, null);
    String javaType = blder.toString();
    if (TypeUtils.isProgramType(type)) {
      try {
        if (bldCat.resolve(javaType, CafeCode.EXTENSION) == null)
          if (TypeUtils.isPatternType(type))
            Theta.buildPatternInterface(type, bldCat, errors, loc, javaType);
          else if (TypeUtils.isConstructorType(type))
            Theta.buildConstructorInterface(type, bldCat, errors, loc, javaType);
          else if (TypeUtils.isFunType(type))
            Theta.buildFunInterface(type, bldCat, errors, loc, javaType);
          else
            errors.reportError(StringUtils.msg("(internal) cannot compute java type of ", type), loc);
      } catch (RepositoryException e) {
        errors.reportError("cannot access type type: " + javaType + "\nbecause " + e.getMessage(), loc);
      }
    }
    return javaType;
  }

  public static String javaSig(IType type, ITypeContext dict, CodeCatalog bldCat, ErrorReport errors, Location loc)
  {
    if (TypeUtils.isRawBoolType(type))
      return "Z";
    else if (TypeUtils.isRawCharType(type))
      return "I";
    else if (TypeUtils.isRawIntType(type))
      return "I";
    else if (TypeUtils.isRawLongType(type))
      return "J";
    else if (TypeUtils.isRawFloatType(type))
      return "D";
    else if (TypeUtils.isRawBinaryType(type))
      return JAVA_OBJECT_SIG;
    else if (TypeUtils.isRawStringType(type))
      return JAVA_STRING_SIG;
    else if (TypeUtils.isRawFileType(type))
      return JAVA_FILE_SIG;
    else if (TypeUtils.isRawDecimalType(type))
      return JAVA_DECIMAL_SIG;
    else
      return "L" + javaType(type, dict, bldCat, errors, loc) + ";";
  }

  public static String genericJavaType(IType type, ITypeContext dict, CodeCatalog bldCat, CodeRepository repository,
      ErrorReport errors, Location loc)
  {
    StringBuilder blder = new StringBuilder();
    JavaType sigBuilder = new JavaType(blder, dict, errors, loc);
    type.accept(sigBuilder, null);
    String javaType = blder.toString();
    if (TypeUtils.isProgramType(type)) {
      try {
        if (bldCat.resolve(javaType, CafeCode.EXTENSION) == null)
          if (TypeUtils.isPatternType(type))
            Theta.buildPatternInterface(type, bldCat, errors, loc, javaType);
          else
            Theta.buildFunctionInterface(type, bldCat, errors, loc, javaType);
      } catch (RepositoryException e) {
        errors.reportError("cannot access type type: " + javaType + "\nbecause " + e.getMessage(), loc);
      }
    }
    return javaType;
  }

  public static String genericJavaSig(IType type, CodeCatalog bldCat, ErrorReport errors, Location loc)
  {
    if (TypeUtils.isRawBoolType(type))
      return "Z";
    else if (TypeUtils.isRawCharType(type))
      return "C";
    else if (TypeUtils.isRawIntType(type))
      return "I";
    else if (TypeUtils.isRawLongType(type))
      return "J";
    else if (TypeUtils.isRawFloatType(type))
      return "D";
    else if (TypeUtils.isRawBinaryType(type))
      return JAVA_OBJECT_SIG;
    else if (TypeUtils.isRawStringType(type))
      return JAVA_STRING_SIG;
    else if (TypeUtils.isRawFileType(type))
      return JAVA_FILE_SIG;
    else if (TypeUtils.isRawDecimalType(type))
      return JAVA_DECIMAL_SIG;
    else
      return IVALUE_SIG;
  }

  private static class JavaType implements ITypeVisitor<Void>
  {
    StringBuilder blder;
    private final ITypeContext dict;
    private final ErrorReport errors;
    private final Location loc;

    JavaType(StringBuilder blder, ITypeContext dict, ErrorReport errors, Location loc)
    {
      this.dict = dict;
      this.blder = blder;
      this.errors = errors;
      this.loc = loc;
    }

    private void javaType(IType type)
    {
      if (TypeUtils.isRawBoolType(type))
        blder.append("bool");
      else if (TypeUtils.isRawCharType(type))
        blder.append("int");
      else if (TypeUtils.isRawIntType(type))
        blder.append("int");
      else if (TypeUtils.isRawLongType(type))
        blder.append("long");
      else if (TypeUtils.isRawFloatType(type))
        blder.append("double");
      else if (TypeUtils.isRawBinaryType(type))
        blder.append(JAVA_OBJECT_SIG);
      else if (TypeUtils.isRawStringType(type))
        blder.append(JAVA_STRING_TYPE);
      else if (TypeUtils.isRawDecimalType(type))
        blder.append(JAVA_DECIMAL_TYPE);
      else if (TypeUtils.isRawFileType(type))
        blder.append(JAVA_FILE_TYPE);
      else if (TypeUtils.isTupleType(type))
        blder.append(TUPLE_TYPE);
      else if (TypeUtils.isAnonRecordLabel(type.typeLabel()))
        blder.append(ANON_RECORD_TYPE);
      else {
        IType tyCon = TypeUtils.getTypeCon(type);
        if (TypeUtils.isTypeVar(tyCon))
          blder.append(IVALUE);
        else {
          CafeTypeDescription desc = (CafeTypeDescription) dict.getTypeDescription(tyCon.typeLabel());
          if (desc != null)
            blder.append(desc.getJavaName());
          else
            errors.reportError("cannot find description of type " + type.typeLabel(), loc);
        }
      }
    }

    @Override
    public void visitTupleType(TupleType t, Void cxt)
    {
      blder.append(TUPLE_TYPE);
    }

    @Override
    public void visitSimpleType(Type t, Void cxt)
    {
      String label = t.typeLabel();

      if (TypeUtils.isRawBoolType(label))
        blder.append("bool");
      else if (TypeUtils.isRawCharType(label))
        blder.append("int");
      else if (TypeUtils.isRawIntType(label))
        blder.append("int");
      else if (TypeUtils.isRawLongType(label))
        blder.append("long");
      else if (TypeUtils.isRawFloatType(label))
        blder.append("double");
      else if (TypeUtils.isRawStringType(label))
        blder.append(JAVA_STRING_TYPE);
      else if (TypeUtils.isRawDecimalType(label))
        blder.append(JAVA_DECIMAL_TYPE);
      else if (TypeUtils.isRawFileType(label))
        blder.append(JAVA_FILE_TYPE);
      else if (TypeUtils.isRawBinaryType(label))
        blder.append(JAVA_OBJECT_TYPE);
      else if (TypeUtils.isTupleType(t))
        assert false : "should not happen (tpl 2)";
      else {
        CafeTypeDescription desc = (CafeTypeDescription) dict.getTypeDescription(label);
        if (desc != null)
          blder.append(desc.getJavaName());
        else
          blder.append(IVALUE);
      }
    }

    @Override
    public void visitTypeExp(TypeExp type, Void cxt)
    {
      if (TypeUtils.isFunType(type) || TypeUtils.isPatternType(type) || TypeUtils.isConstructorType(type))
        new FunctionClassNameGenerator(blder).visitTypeExp(type, cxt);
      else
        javaType(type);
    }

    @Override
    public void visitTypeVar(TypeVar var, Void cxt)
    {
      blder.append(IVALUE);
    }

    @Override
    public void visitExistentialType(ExistentialType t, Void cxt)
    {
      t.getBoundType().accept(this, cxt);
    }

    @Override
    public void visitUniversalType(UniversalType univ, Void cxt)
    {
      // This is where type erasure can hurt us.
      TypeUtils.unwrap(univ).accept(this, cxt);
    }

    @Override
    public void visitTypeInterface(TypeInterfaceType t, Void cxt)
    {
      javaType(t);
    }
  }

  public static String javaTypeName(String path, String label)
  {
    if (TypeUtils.isTupleLabel(label))
      return TUPLE_TYPE;
    else if (isAnonType(label))
      return Utils.javaIdentifierOf(label);
    else
      return path + "/type/" + Utils.javaIdentifierOf(label);
  }

  public static boolean isAnonType(String label)
  {
    return label.startsWith(Names.FACE);
  }

  public static String genericFunctionClassName(IType type)
  {
    StringBuilder str = new StringBuilder();

    type = TypeUtils.unwrap(type);

    if (TypeUtils.isProcedureType(type)) {
      str.append(PRC_PREFIX);
      funArgType(str, TypeUtils.getProcArgType(type));
    } else if (TypeUtils.isFunctionType(type)) {
      str.append(FUN_PREFIX);
      funArgType(str, TypeUtils.getFunArgType(type));
      argType(str, TypeUtils.getFunResultType(type));
    } else if (TypeUtils.isPatternType(type)) {
      IType argTypes = TypeUtils.getPtnResultType(type);
      str.append(PTN_PREFIX);
      argType(str, argTypes);
      argType(str, TypeUtils.getPtnMatchType(type));
    } else if (TypeUtils.isConstructorType(type)) {
      str.append(CON_PREFIX);
      funArgType(str, TypeUtils.getConstructorArgType(type));
      argType(str, TypeUtils.getConstructorResultType(type));
    }

    assert str.length() < Short.MAX_VALUE;

    return str.toString();
  }

  private static void funArgType(StringBuilder str, IType argType)
  {
    argType = TypeUtils.deRef(argType);

    if (TypeUtils.isTupleType(argType)) {
      IType argTypes[] = TypeUtils.tupleTypes(argType);
      str.append(argTypes.length);

      if (argTypes.length < Theta.MAX_ARGS) {
        for (IType tA : argTypes)
          argType(str, tA);
      } else
        str.append("__argArray");
    } else if (argType instanceof TypeInterface) {
      TypeInterface face = (TypeInterface) argType;
      str.append(face.numOfFields());
      for (Entry<String, IType> entry : face.getAllFields().entrySet())
        argType(str, entry.getValue());
    }
  }

  private static void argType(StringBuilder blder, IType tA)
  {
    if (TypeUtils.isRawBoolType(tA))
      blder.append("__boolean");
    else if (TypeUtils.isRawCharType(tA))
      blder.append("__char");
    else if (TypeUtils.isRawIntType(tA))
      blder.append("__integer");
    else if (TypeUtils.isRawLongType(tA))
      blder.append("__long");
    else if (TypeUtils.isRawFloatType(tA))
      blder.append("__float");
    else if (TypeUtils.isRawBinaryType(tA))
      blder.append("__binary");
    else if (TypeUtils.isRawFileType(tA))
      blder.append("__file");
    else if (TypeUtils.isRawStringType(tA))
      blder.append("__string");
    else
      blder.append("_V");
  }

  public static String functionClassName(IType type)
  {
    StringBuilder str = new StringBuilder();

    FunctionClassNameGenerator gen = new FunctionClassNameGenerator(str);
    TypeUtils.unwrap(type).accept(gen, null);
    return str.toString();
  }

  private static class FunctionClassNameGenerator implements ITypeVisitor<Void>
  {
    private final StringBuilder str;

    FunctionClassNameGenerator(StringBuilder str)
    {
      this.str = str;
    }

    @Override
    public void visitSimpleType(Type t, Void cxt)
    {
      String label = t.typeLabel();
      for (StringIterator it = new StringIterator(label); it.hasNext();) {
        int ch = it.next();
        if (Character.isJavaIdentifierPart(ch))
          str.appendCodePoint(ch);
        else
          str.append("_");
      }
    }

    @Override
    public void visitTypeExp(TypeExp type, Void cxt)
    {
      IType[] argTypes = type.getTypeArgs();
      if (TypeUtils.isProcedureType(type)) {
        str.append(PRC_PREFIX);
        visitFunArg(TypeUtils.getProcArgType(type), cxt);
        return;
      } else if (TypeUtils.isFunctionType(type)) {
        str.append(FUN_PREFIX);
        visitFunArg(TypeUtils.getFunArgType(type), cxt);
        str.append("_");
        TypeUtils.getFunResultType(type).accept(this, cxt);
        return;
      } else if (TypeUtils.isPatternType(type)) {
        str.append(PTN_PREFIX);
        str.append(argTypes.length - 1);
      } else if (TypeUtils.isConstructorType(type)) {
        str.append(CON_PREFIX);
        visitFunArg(TypeUtils.getConstructorArgType(type), cxt);
        str.append("_");
        TypeUtils.getConstructorResultType(type).accept(this, cxt);
        return;
      } else {
        String label = type.typeLabel();
        for (StringIterator it = new StringIterator(label); it.hasNext();) {
          int ch = it.next();
          if (Character.isJavaIdentifierPart(ch))
            str.appendCodePoint(ch);
          else
            str.append("_");
        }
      }
      for (IType tA : argTypes) {
        str.append("_");
        tA.accept(this, cxt);
      }
    }

    @Override
    public void visitTupleType(TupleType t, Void cxt)
    {
      str.append(NTuple.label);
    }

    private void visitFunArg(IType argType, Void cxt)
    {
      argType = TypeUtils.unwrap(argType);

      if (TypeUtils.isTupleType(argType)) {
        str.append(TypeUtils.tupleTypeArity(argType));
        IType[] argTypes = TypeUtils.tupleTypes(argType);

        for (IType tA : argTypes) {
          str.append("_");
          tA.accept(this, cxt);
        }
      } else if (argType instanceof TypeExp && TypeUtils.isAnonRecordLabel(argType.typeLabel())) {
        IType argTypes[] = TypeUtils.typeArgs(argType);
        str.append(argTypes.length);
        for (IType tA : argTypes) {
          str.append("_");
          tA.accept(this, cxt);
        }
      } else {
        assert TypeUtils.isTypeInterface(argType);
        TypeInterface face = (TypeInterface) argType;
        str.append(face.numOfFields());
        for (Entry<String, IType> entry : face.getAllFields().entrySet()) {
          str.append("_");
          entry.getValue().accept(this, cxt);
        }
      }
    }

    @Override
    public void visitTypeVar(TypeVar var, Void cxt)
    {
      str.append("V");
    }

    @Override
    public void visitExistentialType(ExistentialType t, Void cxt)
    {
      str.append("exists_");
      IType tp = t;
      while (tp instanceof ExistentialType) {
        QuantifiedType e = (QuantifiedType) tp;
        e.getBoundVar().accept(this, cxt);
        str.append("_");
        tp = e.getBoundType();
      }
      tp.accept(this, cxt);
    }

    @Override
    public void visitUniversalType(UniversalType univ, Void cxt)
    {
      str.append("all_");
      IType tp = univ;
      while (tp instanceof UniversalType) {
        UniversalType e = (UniversalType) tp;
        e.getBoundVar().accept(this, cxt);
        str.append("_");
        tp = e.getBoundType();
      }
      tp.accept(this, cxt);
    }

    @Override
    public void visitTypeInterface(TypeInterfaceType t, Void cxt)
    {
      String label = t.typeLabel();
      for (StringIterator it = new StringIterator(label); it.hasNext();) {
        int ch = it.next();
        if (Character.isJavaIdentifierPart(ch))
          str.appendCodePoint(ch);
        else
          str.append("_");
      }
      for (IType tA : t.getAllFields().values()) {
        str.append("_");
        tA.accept(this, cxt);
      }
    }
  }

  public static boolean isStdType(String name)
  {
    return name.startsWith(Types.FUN_PREFIX) || name.startsWith(Types.PRC_PREFIX) || name.startsWith(Types.PTN_PREFIX)
        || name.startsWith(Names.FACE) || name.startsWith(Types.CON_PREFIX);
  }

  public static JavaKind varType(IType type)
  {
    type = TypeUtils.deRef(type);

    if (TypeUtils.isRawBoolType(type))
      return JavaKind.rawBool;
    else if (TypeUtils.isRawCharType(type))
      return JavaKind.rawChar;
    else if (TypeUtils.isRawIntType(type))
      return JavaKind.rawInt;
    else if (TypeUtils.isRawLongType(type))
      return JavaKind.rawLong;
    else if (TypeUtils.isRawFloatType(type))
      return JavaKind.rawFloat;
    else if (TypeUtils.isRawDecimalType(type))
      return JavaKind.rawDecimal;
    else if (TypeUtils.isRawStringType(type))
      return JavaKind.rawString;
    else if (TypeUtils.isRawBinaryType(type))
      return JavaKind.rawBinary;
    else
      return JavaKind.general;
  }

  public static int stackAmnt(JavaKind kind)
  {
    switch (kind) {
    case rawBool:
    case rawChar:
    case rawInt:
    case general:
    case rawBinary:
    case rawString:
    case rawDecimal:
    default:
      return 1;
    case rawLong:
    case rawFloat:
      return 2;
    }
  }

  public static IType genericFunType(IType type)
  {
    type = TypeUtils.unwrap(type);

    if (TypeUtils.isTupleFunctionType(type)) {
      IType fArgs[] = TypeUtils.getFunArgTypes(type);
      IType argTypes[] = new IType[fArgs.length];
      for (int ix = 0; ix < argTypes.length; ix++)
        argTypes[ix] = genericType(fArgs[ix]);
      return TypeUtils.functionType(argTypes, genericType(TypeUtils.getFunResultType(type)));
    } else if (TypeUtils.isOverloadedType(type)) {
      IType fArgs[] = TypeUtils.getOverloadRequirements(type);
      IType argTypes[] = new IType[fArgs.length];
      for (int ix = 0; ix < argTypes.length; ix++)
        argTypes[ix] = genericType(fArgs[ix]);
      return TypeUtils.functionType(argTypes, genericType(TypeUtils.getOverloadedType(type)));
    } else if (TypeUtils.isRecordFunctionType(type)) {
      TypeInterface face = TypeUtils.getRecordFunctionArgs(type);
      IType argTypes[] = new IType[face.numOfFields()];

      int ix = 0;
      for (Entry<String, IType> entry : face.getAllFields().entrySet()) {
        argTypes[ix++] = genericType(entry.getValue());
      }

      return TypeUtils.functionType(argTypes, genericType(TypeUtils.getFunResultType(type)));
    } else if (TypeUtils.isPatternType(type)) {
      IType pArgs[] = TypeUtils.tupleTypes(TypeUtils.getPtnResultType(type));
      IType pArg = TypeUtils.getPtnMatchType(type);

      IType argTypes[] = new IType[pArgs.length];
      for (int ix = 0; ix < argTypes.length; ix++)
        argTypes[ix] = genericType(pArgs[ix]);

      return TypeUtils.patternType(TypeUtils.tupleType(argTypes), genericType(pArg));
    } else if (TypeUtils.isConstructorType(type)) {
      IType conArgType = TypeUtils.unwrap(TypeUtils.getConstructorArgType(type));
      if (TypeUtils.isTupleType(conArgType)) {
        IType fArgs[] = TypeUtils.tupleTypes(conArgType);
        IType argTypes[] = new IType[fArgs.length];
        for (int ix = 0; ix < argTypes.length; ix++)
          argTypes[ix] = genericType(fArgs[ix]);
        return TypeUtils.constructorType(TypeUtils.tupleType(argTypes), genericType(TypeUtils
            .getConstructorResultType(type)));
      } else if (TypeUtils.isTypeInterface(conArgType)) {
        TypeInterface face = (TypeInterface) TypeUtils.unwrap(conArgType);
        IType argTypes[] = new IType[face.numOfFields()];

        int ix = 0;
        for (Entry<String, IType> entry : face.getAllFields().entrySet()) {
          argTypes[ix++] = genericType(entry.getValue());
        }

        return TypeUtils.constructorType(TypeUtils.tupleType(argTypes), genericType(TypeUtils
            .getConstructorResultType(type)));
      } else if (TypeUtils.isAnonRecordLabel(conArgType.typeLabel())) {
        IType fArgs[] = TypeUtils.typeArgs(conArgType);
        IType argTypes[] = new IType[fArgs.length];
        for (int ix = 0; ix < argTypes.length; ix++)
          argTypes[ix] = genericType(fArgs[ix]);
        return TypeUtils.constructorType(TypeUtils.tupleType(argTypes), genericType(TypeUtils
            .getConstructorResultType(type)));
      } else
        return TypeUtils
            .constructorType(genericType(conArgType), genericType(TypeUtils.getConstructorResultType(type)));
    } else
      return type;
  }

  public static IType genericConstructorType(IType type)
  {
    type = TypeUtils.unwrap(type);

    assert TypeUtils.isConstructorType(type);

    IType argType = TypeUtils.unwrap(TypeUtils.getConstructorArgType(type));
    IType resType = genericType(TypeUtils.getConstructorResultType(type));

    if (argType instanceof TypeInterface) {
      TypeInterface face = (TypeInterface) argType;
      IType argTypes[] = new IType[face.numOfFields()];

      int ix = 0;

      for (Entry<String, IType> entry : face.getAllFields().entrySet())
        argTypes[ix++] = genericType(entry.getValue());

      return TypeUtils.constructorType(TypeUtils.tupleType(argTypes), resType);
    } else if (TypeUtils.isAnonRecordLabel(argType.typeLabel())) {
      IType aTypes[] = TypeUtils.typeArgs(argType);
      IType rTypes[] = new IType[aTypes.length];
      for (int ix = 0; ix < aTypes.length; ix++)
        rTypes[ix] = genericType(aTypes[ix]);
      return TypeUtils.constructorType(TypeUtils.tupleType(rTypes), resType);
    } else {
      assert TypeUtils.isTupleType(argType);
      IType aTypes[] = TypeUtils.tupleTypes(argType);
      IType argTypes[] = new IType[aTypes.length];
      for (int ix = 0; ix < aTypes.length; ix++)
        argTypes[ix] = genericType(aTypes[ix]);
      return TypeUtils.constructorType(TypeUtils.tupleType(argTypes), resType);
    }
  }

  private static IType genericType(IType tp)
  {
    if (TypeUtils.isRawType(tp))
      return tp;
    else
      return new TypeVar();
  }

  public static String setterName(String field)
  {
    return Names.capName("set", field);
  }

  public static String getterName(String field)
  {
    return Names.capName("get", field);
  }
}
