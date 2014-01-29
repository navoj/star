package org.star_lang.star.compiler.cafe.compile;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.star_lang.star.code.repository.CodeCatalog;
import org.star_lang.star.code.repository.CodeRepository;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.cafe.Names;
import org.star_lang.star.compiler.type.DisplayType;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.StringSequence;
import org.star_lang.star.compiler.util.Sequencer.SequenceException;

import com.starview.platform.data.type.IType;
import com.starview.platform.data.type.ITypeContext;
import com.starview.platform.data.type.Location;
import com.starview.platform.data.type.StandardTypes;
import com.starview.platform.data.type.TypeVar;

/**
 * 
 * Copyright (C) 2013 Starview Inc
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA
 * 
 * @author fgm
 * 
 */
@SuppressWarnings("serial")
public class SrcSpec implements ISpec
{
  private final Location loc;
  private final IType type;
  private final String javaType;
  private final String javaSig;
  private final String javaInvokeSig;
  private final String javaInvokeName;
  private final JavaKind javaKind;
  private final Object frameRep;

  public static final ISpec voidSrc = new SrcSpec(StandardTypes.voidType, Location.nullLoc, Types.IVALUE,
      Types.IVALUE_SIG, null, null);
  public static final ISpec prcSrc = new SrcSpec(StandardTypes.unitType, Location.nullLoc, Types.IVALUE,
      Types.IVALUE_SIG, null, null);
  public static final ISpec rawBoolSrc = new SrcSpec(StandardTypes.rawBoolType, Location.nullLoc, "bool", "Z", null,
      null);
  public static final ISpec rawCharSrc = new SrcSpec(StandardTypes.rawCharType, Location.nullLoc, "int", "I", null,
      null);
  public static final ISpec rawIntSrc = new SrcSpec(StandardTypes.rawIntegerType, Location.nullLoc, "int", "I", null,
      null);
  public static final ISpec rawIntegerSrc = new SrcSpec(StandardTypes.rawIntegerType, Location.nullLoc,
      Types.JAVA_INTEGER_TYPE, Types.JAVA_INTEGER_SIG, null, null);
  public static final ISpec rawLngSrc = new SrcSpec(StandardTypes.rawLongType, Location.nullLoc, "long", "J", null,
      null);
  public static final ISpec rawLongSrc = new SrcSpec(StandardTypes.rawLongType, Location.nullLoc, Types.JAVA_LONG_TYPE,
      Types.JAVA_LONG_SIG, null, null);
  public static final ISpec rawFltSrc = new SrcSpec(StandardTypes.rawFloatType, Location.nullLoc, "float", "F", null,
      null);
  public static final ISpec rawFloatSrc = new SrcSpec(StandardTypes.rawFloatType, Location.nullLoc,
      Types.JAVA_FLOAT_TYPE, Types.JAVA_FLOAT_SIG, null, null);
  public static final ISpec rawDblSrc = new SrcSpec(StandardTypes.rawFloatType, Location.nullLoc, "double", "D", null,
      null);
  public static final ISpec rawDoubleSrc = new SrcSpec(StandardTypes.rawFloatType, Location.nullLoc,
      Types.JAVA_DOUBLE_TYPE, Types.JAVA_DOUBLE_SIG, null, null);
  public static final ISpec rawDecimalSrc = new SrcSpec(StandardTypes.rawDecimalType, Location.nullLoc,
      Types.JAVA_DECIMAL_TYPE, Types.JAVA_DECIMAL_SIG, null, null);
  public static final ISpec rawStringSrc = new SrcSpec(StandardTypes.rawStringType, Location.nullLoc,
      Types.JAVA_STRING_TYPE, Types.JAVA_STRING_SIG, null, null);
  public static final ISpec rawBinarySrc = new SrcSpec(StandardTypes.rawBinaryType, Location.nullLoc,
      Types.JAVA_OBJECT_TYPE, Types.JAVA_OBJECT_SIG, null, null);
  public static final ISpec generalSrc = new SrcSpec(new TypeVar(), Location.nullLoc, Types.IVALUE, Types.IVALUE_SIG,
      null, null);
  public static final ISpec anonSrc = new SrcSpec(new TypeVar(), Location.nullLoc, Types.ANON_RECORD_TYPE,
      Types.ANON_RECORD_SIG, null, null);
  public static final ISpec constructorSrc = new SrcSpec(new TypeVar(), Location.nullLoc, Types.ICONSTRUCTOR,
      Types.ICONSTRUCTOR_SIG, null, null);

  public static final ISpec arraySrc = new SrcSpec(TypeUtils.arrayType(new TypeVar()), Location.nullLoc,
      Types.IVALUE_ARRAY, Types.IVALUE_ARRAY, null, null);

  public SrcSpec(IType type, Location loc, String javaType, String javaSig, String javaInvokeSig, String javaInvokeName)
  {
    this.loc = loc;
    this.type = type;
    this.javaType = javaType;
    this.javaSig = javaSig;
    this.javaKind = Types.varType(type);
    this.javaInvokeSig = javaInvokeSig;
    this.javaInvokeName = javaInvokeName;
    this.frameRep = frameRep(javaSig);
  }

  public static SrcSpec typeSpec(Location loc, IType type, ITypeContext dict, CodeCatalog bldCat, ErrorReport errors)
  {
    type = TypeUtils.unwrap(type);
    String javaType = Types.javaType(type, dict, bldCat, errors, loc);
    String javaInvokeSig = TypeUtils.isProgramType(type) ? Types.javaMethodSig(type) : null;
    String javaSig = Types.javaSig(type, dict, bldCat, errors, loc);
    String javaInvokeName = TypeUtils.isProgramType(type) ? Names.ENTER : null;
    return new SrcSpec(type, loc, javaType, javaSig, javaInvokeSig, javaInvokeName);
  }

  public static ISpec[] typeSpecs(IType[] types, ITypeContext dict, CodeCatalog bldCat, CodeRepository repository,
      ErrorReport errors, Location loc)
  {
    ISpec[] specs = new ISpec[types.length];
    for (int ix = 0; ix < types.length; ix++)
      specs[ix] = typeSpec(loc, types[ix], dict, bldCat, errors);
    return specs;
  }

  public static SrcSpec generic(Location loc, IType type, ITypeContext dict, CodeRepository repository,
      ErrorReport errors)
  {
    CodeCatalog synCat = repository.synthCodeCatalog();
    type = TypeUtils.unwrap(type);
    IType generic = Types.genericFunType(type);
    String javaType = Types.javaType(generic, dict, synCat, errors, loc);
    String javaSig = Types.javaSig(generic, dict, synCat, errors, loc);
    String javaInvokeSig = TypeUtils.isProgramType(type) ? Types.javaMethodSig(type) : null;
    String javaInvokeName = TypeUtils.isProgramType(type) ? Names.ENTER : null;
    return new SrcSpec(type, loc, javaType, javaSig, javaInvokeSig, javaInvokeName);
  }

  public static ISpec[] generics(IType funType, ITypeContext dict, CodeCatalog bldCat, CodeRepository repository,
      ErrorReport errors, Location loc)
  {
    assert TypeUtils.isProgramType(funType);
    IType generic = Types.genericFunType(funType);
    int arity = TypeUtils.arityOfFunctionType(generic);
    IType[] types = TypeUtils.getFunArgTypes(generic);
    ISpec[] specs = new ISpec[arity + 1];
    for (int ix = 0; ix < arity; ix++)
      specs[ix] = generic(loc, types[ix], dict, repository, errors);
    specs[arity] = generic(loc, TypeUtils.getFunResultType(generic), dict, repository, errors);
    return specs;
  }

  public static ISpec[] genericConstructorSpecs(IType conType, ITypeContext dict, CodeCatalog bldCat,
      CodeRepository repository, ErrorReport errors, Location loc)
  {
    assert TypeUtils.isConstructorType(conType);
    IType generic = Types.genericConstructorType(conType);
    int arity = TypeUtils.arityOfConstructorType(generic);
    IType[] types = TypeUtils.getConstructorArgTypes(generic);
    ISpec[] specs = new ISpec[arity + 1];
    for (int ix = 0; ix < arity; ix++)
      specs[ix] = generic(loc, types[ix], dict, repository, errors);
    specs[arity] = generic(loc, TypeUtils.getConstructorResultType(generic), dict, repository, errors);
    return specs;
  }

  public static ISpec[] typeSpecs(String javaSig, ITypeContext dict, CodeCatalog bldCat, ErrorReport errors,
      Location loc)
  {
    StringSequence seq = new StringSequence(javaSig);

    List<ISpec> specs = new ArrayList<ISpec>();
    try {
      if (seq.next() != '(')
        errors.reportError("invalid java method signature: " + javaSig, loc);

      while (seq.hasNext() && seq.peek() != ')')
        specs.add(javaType(seq, loc, errors));
      if (seq.next() != ')')
        errors.reportError("invalid java method signature: " + javaSig, loc);
      specs.add(javaType(seq, loc, errors));
    } catch (SequenceException e) {
      errors.reportError("invalid java signature: " + javaSig, loc);
    }
    return specs.toArray(new ISpec[specs.size()]);
  }

  private static ISpec javaType(StringSequence str, Location loc, ErrorReport errors) throws SequenceException
  {
    switch (str.next().intValue()) {
    case 'Z':
      return rawBoolSrc;
    case 'C':
      return rawCharSrc;
    case 'I':
      return rawIntSrc;
    case 'J':
      return rawLngSrc;
    case 'F':
      return rawFltSrc;
    case 'D':
      return rawDblSrc;
    case 'V':
      return prcSrc;
    case 'L': {
      StringBuilder buff = new StringBuilder();
      for (; str.hasNext() && str.peek() != ';';)
        buff.appendCodePoint(str.next());
      str.next(); // skip over trailing semi

      String buffContent = buff.toString();
      if (buffContent.equals(Types.JAVA_STRING_TYPE))
        return rawStringSrc;
      else if (buffContent.equals(Types.JAVA_INTEGER_TYPE))
        return rawIntegerSrc;
      else if (buffContent.equals(Types.JAVA_LONG_TYPE))
        return rawLongSrc;
      else if (buffContent.equals(Types.JAVA_DECIMAL_TYPE))
        return rawDecimalSrc;
      else if (buffContent.equals(Types.JAVA_OBJECT_TYPE))
        return rawBinarySrc;
      else
        return new SrcSpec(StandardTypes.anyType, loc, buffContent, "L" + buffContent + ";", null, null);
    }
    case '[':
    default:
      throw new SequenceException("cannot handle java type: " + str.prev());
    }
  }

  public static SrcSpec generic(Location loc)
  {
    return new SrcSpec(new TypeVar(), loc, Types.IVALUE, Types.IVALUE_SIG, null, null);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    DisplayType.display(disp, type);
    disp.append("=");
    disp.append(javaType);
    if (javaInvokeSig != null) {
      disp.append("{");
      disp.append(javaInvokeSig);
      disp.append("}");
    }
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

  @Override
  public Location getLoc()
  {
    return loc;
  }

  @Override
  public IType getType()
  {
    return type;
  }

  @Override
  public String getJavaType()
  {
    return javaType;
  }

  @Override
  public String getJavaSig()
  {
    return javaSig;
  }

  @Override
  public String getJavaInvokeSig()
  {
    return javaInvokeSig;
  }

  @Override
  public String getJavaInvokeName()
  {
    return javaInvokeName;
  }

  @Override
  public int slotSize()
  {
    return Types.stackAmnt(javaKind);
  }

  @Override
  public Object getFrameCode()
  {
    return frameRep;
  }

  public static Object frameRep(String javaType)
  {
    if (javaType.equals(Types.JAVA_INT_SIG))
      return Opcodes.INTEGER;
    else if (javaType.equals(Types.JAVA_LNG_SIG))
      return Opcodes.LONG;
    else if (javaType.equals(Types.JAVA_DBL_SIG))
      return Opcodes.DOUBLE;
    else
      return javaType;
  }

  public JavaKind getJavaKind()
  {
    return javaKind;
  }

}
