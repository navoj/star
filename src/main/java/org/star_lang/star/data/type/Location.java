package org.star_lang.star.data.type;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.star_lang.star.compiler.ast.DisplayLocation;
import org.star_lang.star.compiler.cafe.type.CafeTypeDescription;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IConstructor;
import org.star_lang.star.data.IRecord;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.IValueVisitor;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.data.value.IntWrap;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.resource.ResourceException;

/**
 * Encapsulate a location in an input stream.
 *
 * Locations are also IValues, which enables their participation in meta-programming
 *
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
public abstract class Location implements PrettyPrintable, IConstructor {
  public static final String location = "astLocation";
  public static final String nowhere = "noWhere";
  public static final String somewhere = "_somewhere";
  public static final IType type = TypeUtils.typeExp(location);
  public static final IType integer = StandardTypes.integerType;

  public static final String URI = "uri";
  public static final String SOURCE = "source";
  public static final String CHARCOUNT = "charCount";
  public static final String LINECOUNT = "lineCount";
  public static final String LINEOFFSET = "lineOffset";
  public static final String LENGTH = "length";

  private static final String[] members = {URI, CHARCOUNT, LINECOUNT, LINEOFFSET, LENGTH};

  public static NoWhere noWhereEnum = new NoWhere();
  public static Location nullLoc = noWhereEnum;

  private static final int uriIndex = 0;
  private static final int charCountIndex = 1;
  private static final int lineCountIndex = 2;
  private static final int lineOffsetIndex = 3;
  private static final int lengthIndex = 4;

  private static final int fieldCount = 5;

  public static final SortedMap<String, Integer> index;

  public static final TypeInterfaceType face;

  static {
    index = new TreeMap<>();
    index.put(URI, uriIndex);
    index.put(CHARCOUNT, charCountIndex);
    index.put(LINECOUNT, lineCountIndex);
    index.put(LINEOFFSET, lineOffsetIndex);
    index.put(LENGTH, lengthIndex);

    SortedMap<String, IType> fields = new TreeMap<>();
    fields.put(URI, ResourceURI.type);
    fields.put(CHARCOUNT, integer);
    fields.put(LINECOUNT, integer);
    fields.put(LINEOFFSET, integer);
    fields.put(LENGTH, integer);

    face = new TypeInterfaceType(fields);
  }

  public abstract int getCharCnt();

  public abstract IValue getCharCount();

  public abstract int getLineCnt();

  public abstract IValue getLineCount();

  public abstract int getLineOff();

  public abstract IValue getLineOffset();

  public abstract int getLen();

  public abstract IValue getLength();

  public abstract String getSrc();

  public abstract ResourceURI getUri();

  public abstract Location extendWith(Location other);

  public abstract Location offset(int offset, int len);

  public abstract boolean sameLine(Location other);

  public static class SomeWhere extends Location implements IRecord {
    private final ResourceURI uri;
    private final int charCount;
    private final int lineCount;
    private final int lineOffset;
    private final int length;

    public SomeWhere(String source, int charCount, int lineCount, int lineOffset, int length) {
      this.uri = getUri(source);
      this.charCount = charCount;
      this.lineCount = lineCount;
      this.lineOffset = lineOffset;
      this.length = length;
    }

    public SomeWhere(int charCount, int lineCount, int lineOffset, int length, ResourceURI uri) {
      this.uri = uri;
      this.charCount = charCount;
      this.lineCount = lineCount;
      this.lineOffset = lineOffset;
      this.length = length;
    }

    public SomeWhere(int charCount, int lineCount, int lineOffset, int length, IValue uri) throws EvaluationException {
      this.uri = (uri instanceof ResourceURI ? ((ResourceURI) uri) : getUri(Factory.stringValue(uri)));
      this.charCount = charCount;
      this.lineCount = lineCount;
      this.lineOffset = lineOffset;
      this.length = length;
    }

    // These must be in alphabetic order of names
    public SomeWhere(IValue charCount, IValue length, IValue lineCount, IValue lineOffset, IValue uri)
            throws EvaluationException {
      this.uri = (uri instanceof ResourceURI ? ((ResourceURI) uri) : getUri(Factory.stringValue(uri)));
      this.charCount = Factory.intValue(charCount);
      this.lineCount = Factory.intValue(lineCount);
      this.lineOffset = Factory.intValue(lineOffset);
      this.length = Factory.intValue(length);
    }

    private static ResourceURI getUri(String source) {
      try {
        if (source == null)
          return ResourceURI.noUriEnum;
        else
          return ResourceURI.parseURI(source);
      } catch (ResourceException e) {
        return ResourceURI.noUriEnum;
      }
    }

    @Override
    public int conIx() {
      return 0;
    }

    @Override
    public String getLabel() {
      return somewhere;
    }

    @Override
    public int size() {
      return fieldCount;
    }

    @Override
    public int getCharCnt() {
      return charCount;
    }

    @Override
    public IValue getCharCount() {
      return Factory.newInt(charCount);
    }

    @Override
    public IValue getLineCount() {
      return Factory.newInt(lineCount);
    }

    @Override
    public IValue getLineOffset() {
      return Factory.newInt(lineOffset);
    }

    @Override
    public IValue getLength() {
      return Factory.newInt(length);
    }

    @Override
    public int getLineCnt() {
      return lineCount;
    }

    @Override
    public int getLineOff() {
      return lineOffset;
    }

    @Override
    public int getLen() {
      return length;
    }

    @Override
    public String getSrc() {
      return uri.toString();
    }

    @Override
    public ResourceURI getUri() {
      return uri;
    }

    @Override
    public Location extendWith(Location loc) {
      if (loc instanceof SomeWhere) {
        SomeWhere other = (SomeWhere) loc;

        if (other.charCount >= charCount)
          return new SomeWhere(charCount, lineCount, lineOffset, other.charCount + other.length - charCount, uri);
        else
          return new SomeWhere(other.charCount, other.lineCount, other.lineOffset,
                  charCount + length - other.charCount, uri);
      } else
        return this;
    }

    @Override
    public Location offset(int offset, int len) {
      return new SomeWhere(charCount + offset, lineCount, lineOffset + offset, len, uri);
    }

    @Override
    public boolean equals(Object obj) {
      return obj instanceof SomeWhere && ((SomeWhere) obj).uri.equals(uri) && ((SomeWhere) obj).lineCount == lineCount
              && ((SomeWhere) obj).lineOffset == lineOffset;
    }

    @Override
    public int hashCode() {
      return (uri.hashCode() * 37 + lineCount) * 37 + lineOffset;
    }

    @Override
    public IRecord copy() {
      return new SomeWhere(charCount, lineCount, lineOffset, length, uri);
    }

    @Override
    public IRecord shallowCopy() {
      return copy();
    }

    @Override
    public boolean sameLine(Location other) {
      if (other instanceof SomeWhere)
        return other.getLineCnt() == getLineCnt();
      else
        return false;
    }

    @Override
    public void prettyPrint(PrettyPrintDisplay disp) {
      uri.prettyPrint(disp);
      disp.append("/");
      disp.append(lineCount);
      disp.append("[");
      disp.append(lineOffset);
      disp.append(":");
      disp.append(length);
      disp.append("]");
    }

    @Override
    public IValue getCell(int index) {
      switch (index) {
        case uriIndex:
          return uri;
        case charCountIndex:
          return Factory.newInt(charCount);
        case lineCountIndex:
          return Factory.newInt(lineCount);
        case lineOffsetIndex:
          return Factory.newInt(lineOffset);
        case lengthIndex:
          return Factory.newInt(length);
      }
      throw new IllegalArgumentException("index out of range");
    }

    public ResourceURI get___0() {
      return uri;
    }

    public int get___1() {
      return charCount;
    }

    public int get___2() {
      return lineCount;
    }

    public int get___3() {
      return lineOffset;
    }

    public int get___4() {
      return length;
    }

    @Override
    public IValue[] getCells() {
      return new IValue[]{uri, Factory.newInt(charCount), Factory.newInt(lineCount), Factory.newInt(lineOffset),
              Factory.newInt(length)};
    }

    @Override
    public IValue getMember(String memberName) {
      switch (memberName) {
        case URI:
          return uri;
        case SOURCE:
          return Factory.newString(uri.toString());
        case CHARCOUNT:
          return Factory.newInt(charCount);
        case LINECOUNT:
          return Factory.newInt(lineCount);
        case LINEOFFSET:
          return Factory.newInt(lineOffset);
        case LENGTH:
          return Factory.newInt(length);
        default:
          return null;
      }
    }

    @Override
    public void setMember(String memberName, IValue value) throws EvaluationException {
      throw new EvaluationException("not permitted");
    }

    @Override
    public String[] getMembers() {
      return members;
    }

    public static IType conType() {
      return TypeUtils.constructorType(face, type);
    }
  }

  public static Location location(String source, int charCount, int lineCount, int lineOffset, int length) {
    return new SomeWhere(source, charCount, lineCount, lineOffset, length);
  }

  public static Location location(ResourceURI uri, int charCount, int lineCount, int lineOffset, int length) {
    return new SomeWhere(charCount, lineCount, lineOffset, length, uri);
  }

  public static Location current() {
    StackTraceElement trace = Thread.currentThread().getStackTrace()[1];
    return new SomeWhere(trace.getClassName(), 0, 1, trace.getLineNumber(), 0);
  }

  public static Location location(ResourceURI uri) {
    return location(uri, 0, 0, 0, 0);
  }

  public static Location[] merge(Location loc, Location... locs) {
    if (loc == null)
      return locs;
    else if (locs.length == 0)
      return new Location[]{loc};
    else {
      for (Location loc1 : locs)
        if (loc1.equals(loc))
          return locs;
      Location merge[] = new Location[locs.length + 1];
      for (int ix = 0; ix < locs.length; ix++)
        merge[ix + 1] = locs[ix];
      merge[0] = loc;
      return merge;
    }
  }

  public static class NoWhere extends Location {

    @Override
    public void prettyPrint(PrettyPrintDisplay disp) {
      disp.append("nullLoc");
    }

    @Override
    public int conIx() {
      return 1;
    }

    @Override
    public String getLabel() {
      return nowhere;
    }

    @Override
    public int size() {
      return 0;
    }

    @Override
    public IValue getCell(int index) {
      throw new IllegalAccessError("index out of range");
    }

    @Override
    public IValue[] getCells() {
      return new IValue[]{};
    }

    @Override
    public IConstructor copy() throws EvaluationException {
      return this;
    }

    @Override
    public IConstructor shallowCopy() {
      return this;
    }

    @Override
    public int getCharCnt() {
      return 0;
    }

    @Override
    public int getLineCnt() {
      return 0;
    }

    @Override
    public int getLineOff() {
      return 0;
    }

    @Override
    public int getLen() {
      return 0;
    }

    @Override
    public IValue getCharCount() {
      return IntWrap.nonIntegerEnum;
    }

    @Override
    public IValue getLineCount() {
      return IntWrap.nonIntegerEnum;
    }

    @Override
    public IValue getLineOffset() {
      return IntWrap.nonIntegerEnum;
    }

    @Override
    public IValue getLength() {
      return IntWrap.nonIntegerEnum;
    }

    @Override
    public String getSrc() {
      return "";
    }

    @Override
    public ResourceURI getUri() {
      return ResourceURI.noUriEnum;
    }

    @Override
    public Location extendWith(Location other) {
      return other;
    }

    @Override
    public Location offset(int offset, int len) {
      return this;
    }

    @Override
    public boolean sameLine(Location other) {
      return other instanceof NoWhere;
    }
  }

  @Override
  public IType getType() {
    return type;
  }

  public static IType conType() {
    return TypeUtils.constructorType(type);
  }

  @Override
  public void accept(IValueVisitor visitor) {
    visitor.visitConstructor(this);
  }

  @Override
  public String toString() {
    return PrettyPrintDisplay.toString(this);
  }

  @Override
  public void setCell(int index, IValue value) throws EvaluationException {
    throw new IllegalArgumentException("not permitted");
  }

  public static void declare(Intrinsics cxt) {
    ConstructorSpecifier locSpec = new RecordSpecifier(nullLoc, somewhere, 0, null, TypeUtils.constructorType(face,
            type), SomeWhere.class, Location.class);

    ConstructorSpecifier nullSpec = new ConstructorSpecifier(nullLoc, null, nowhere, 1,
            TypeUtils.constructorType(type), NoWhere.class, Location.class);

    List<IValueSpecifier> specs = new ArrayList<>();
    specs.add(locSpec);
    specs.add(nullSpec);

    ITypeDescription locDesc = new CafeTypeDescription(nullLoc, type, Location.class.getName(), specs);

    cxt.defineType(locDesc);
    cxt.declareBuiltin(new Builtin(DisplayLocation.name, DisplayLocation.type(), DisplayLocation.class));
  }

  public static SortedMap<String, Integer> getIndex() {
    return index;
  }
}
