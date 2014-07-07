package org.star_lang.star.data.value;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.star_lang.star.compiler.cafe.type.CafeTypeDescription;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.compiler.util.StringUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IConstructor;
import org.star_lang.star.data.IRecord;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.IValueVisitor;
import org.star_lang.star.data.type.ConstructorSpecifier;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.ITypeContext;
import org.star_lang.star.data.type.ITypeDescription;
import org.star_lang.star.data.type.IValueSpecifier;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.RecordSpecifier;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.type.TypeInterfaceType;
import org.star_lang.star.data.value.URIAuthority.NoAuthority;
import org.star_lang.star.resource.ResourceException;
import org.star_lang.star.resource.URIUtils;

/**
 * A Resource URI identifies a resource by name.
 * <p/>
 * Follows the Star type definitions:
 * <p/>
 * 
 * <pre>
 * type uri is uri{
 *   scheme has type string;
 *   authority has type uriAuthority;
 *   path has type uriPath;
 *   query has type string;
 *   fragment has type string
 *   }
 *   or noUri
 * </pre>
 * <p/>
 * The path is either an absolute path or a relative path:
 * <p/>
 * 
 * <pre>
 * type uriPath is absolutePath(sequence of string)
 *   or relativePath(sequence of string)
 *    or noPath
 * </pre>
 * 
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
public abstract class ResourceURI implements PrettyPrintable, IConstructor, Comparable<ResourceURI>
{
  public static final String uri = "uri";
  public static final String noUri = "noUri";
  public static final IType type = TypeUtils.typeExp(uri);

  public static final IType stringType = StandardTypes.stringType;
  public static final IType integerType = StandardTypes.integerType;

  public static final ResourceURI noUriEnum = new NoUri();

  // Field names in a uri
  public static final String SCHEME = "scheme";
  public static final String AUTHORITY = "authority";
  public static final String PATH = "path";
  public static final String QUERY = "query";
  public static final String FRAGMENT = "fragment";

  private static final String[] uriMembers = { SCHEME, AUTHORITY, PATH, QUERY, FRAGMENT };

  private static final int schemeIx = 0;
  private static final int authorityIx = 1;
  private static final int pathIx = 2;
  private static final int queryIx = 3;
  private static final int fragmentIx = 4;
  private static final int uriFieldCount = 5;

  public static final SortedMap<String, Integer> index;

  public static final TypeInterfaceType face;

  static {
    index = new TreeMap<>();
    index.put(SCHEME, schemeIx);
    index.put(AUTHORITY, authorityIx);
    index.put(PATH, pathIx);
    index.put(QUERY, queryIx);
    index.put(FRAGMENT, fragmentIx);

    SortedMap<String, IType> fields = new TreeMap<>();
    fields.put(SCHEME, stringType);
    fields.put(AUTHORITY, URIAuthority.type);
    fields.put(PATH, stringType);
    fields.put(QUERY, stringType);
    fields.put(FRAGMENT, stringType);
    face = new TypeInterfaceType(fields);
  }

  public abstract String getScheme();

  public abstract URIAuthority getAuthority();

  public abstract String getPath();

  public abstract String getQuery();

  public abstract String getFragment();

  public abstract ResourceURI parent();

  public static class URI extends ResourceURI implements IRecord
  {
    private final String scheme;
    private final URIAuthority authority;
    private final String path;
    private final String query;
    private final String fragment;

    public URI(String scheme, URIAuthority authority, String path, String query, String fragment)
    {
      this.scheme = scheme;

      this.authority = authority;
      this.path = path;
      this.query = query;
      this.fragment = fragment;
    }

    public URI(IValue scheme, IValue authority, IValue path, IValue query, IValue fragment) throws EvaluationException
    {
      this.scheme = Factory.stringValue(scheme);
      this.authority = (URIAuthority) authority;
      this.path = Factory.stringValue(path);
      this.query = Factory.stringValue(query);
      this.fragment = Factory.stringValue(fragment);
    }

    @Override
    public int conIx()
    {
      return 0;
    }

    @Override
    public String getLabel()
    {
      return uri;
    }

    @Override
    public int size()
    {
      return uriFieldCount;
    }

    @Override
    public String getScheme()
    {
      return scheme;
    }

    @Override
    public URIAuthority getAuthority()
    {
      return authority;
    }

    @Override
    public String getPath()
    {
      return path;
    }

    @Override
    public String getQuery()
    {
      return query;
    }

    @Override
    public String getFragment()
    {
      return fragment;
    }

    @Override
    public ResourceURI parent()
    {
      String path = getPath();
      if (path.indexOf('/') >= 0)
        path = path.substring(0, path.lastIndexOf('/') + 1);
      else
        path = "";
      return new URI(getScheme(), getAuthority(), path, getQuery(), getFragment());
    }

    @Override
    public boolean equals(Object obj)
    {
      if (obj instanceof URI) {
        URI uri = (URI) obj;
        return stringEqual(uri.scheme, scheme) && uri.authority.equals(authority) && stringEqual(uri.getPath(), path)
            && stringEqual(uri.getQuery(), query) && stringEqual(uri.fragment, fragment);
      }
      return false;
    }

    private static boolean stringEqual(String s1, String s2)
    {
      if (s1 == null || s2 == null)
        return s1 == null && s2 == null;
      else
        return s1.equals(s2);
    }

    private static int stringHash(String s)
    {
      if (s == null)
        return 0;
      else
        return s.hashCode();
    }

    @Override
    public int hashCode()
    {
      return (((stringHash(scheme) * 37 + authority.hashCode()) * 37 + stringHash(path)) * 37 + stringHash(query)) * 37
          + stringHash(fragment);
    }

    @Override
    public int compareTo(ResourceURI o)
    {
      if (o == noUriEnum)
        return 1;
      else {
        assert o instanceof ResourceURI;

        int comp = 0;
        if (o.getScheme() != null && getScheme() != null)
          comp = getScheme().compareTo(o.getScheme());

        if (comp == 0 && getPath() != null && o.getPath() != null)
          comp = getPath().compareTo(o.getPath());
        if (comp == 0 && getQuery() != null && o.getQuery() != null)
          comp = getQuery().compareTo(o.getQuery());
        if (comp == 0 && getFragment() != null && o.getFragment() != null)
          comp = getFragment().compareTo(o.getFragment());
        return comp;
      }
    }

    @Override
    public IRecord copy() throws EvaluationException
    {
      return new URI(scheme, (URIAuthority) authority.copy(), path, query, fragment);
    }

    @Override
    public IRecord shallowCopy()
    {
      return new URI(scheme, authority, path, query, fragment);
    }

    @Override
    public void accept(IValueVisitor visitor)
    {
      visitor.visitRecord(this);
    }

    @Override
    public void prettyPrint(PrettyPrintDisplay disp)
    {
      if (scheme != null) {
        encodeFragment(scheme, disp);
        disp.append(":");
      }
      if (authority != URIAuthority.noAuthorityEnum)
        authority.prettyPrint(disp);
      if (path != null) {
        encodeFragment(path, disp);
      }
      if (!StringUtils.isTrivial(query)) {
        disp.append("?");
        encodeQuery(query, disp);
      }
      if (!StringUtils.isTrivial(fragment)) {
        disp.append("#");
        encodeFragment(fragment, disp);
      }
    }

    private static void encodeFragment(String frag, PrettyPrintDisplay disp)
    {
      disp.append(URIUtils.encodeURIFragment(frag, "", ""));
    }

    private static void encodeQuery(String frag, PrettyPrintDisplay disp)
    {
      disp.append(URIUtils.encodeURIFragment(frag, "=", ";/?:@&+,$"));
    }

    @Override
    public IValue getCell(int index)
    {
      switch (index) {
      case schemeIx:
        return Factory.newString(scheme);
      case authorityIx:
        return authority;
      case pathIx:
        return Factory.newString(path);
      case queryIx:
        return Factory.newString(query);
      case fragmentIx:
        return Factory.newString(fragment);
      }
      throw new IllegalArgumentException("index out of range");
    }

    public String get___0()
    {
      return scheme;
    }

    public URIAuthority get___1()
    {
      return authority;
    }

    public String get___2()
    {
      return path;
    }

    public String get___3()
    {
      return query;
    }

    public String get___4()
    {
      return fragment;
    }

    @Override
    public IValue[] getCells()
    {
      return new IValue[] { Factory.newString(scheme), authority, Factory.newString(path), Factory.newString(query),
          Factory.newString(fragment) };
    }

    @Override
    public IValue getMember(String memberName)
    {
      switch (index.get(memberName)) {
      case schemeIx:
        return Factory.newString(scheme);
      case authorityIx:
        return authority;
      case pathIx:
        return Factory.newString(path);
      case queryIx:
        return Factory.newString(query);
      case fragmentIx:
        return Factory.newString(fragment);
      }
      return null;
    }

    @Override
    public void setMember(String memberName, IValue value) throws EvaluationException
    {
      throw new EvaluationException("not permitted");
    }

    @Override
    public String[] getMembers()
    {
      return uriMembers;
    }
  }

  public static class NoUri extends ResourceURI
  {

    @Override
    public void prettyPrint(PrettyPrintDisplay disp)
    {
      disp.append(noUri);
    }

    @Override
    public int conIx()
    {
      return 1;
    }

    @Override
    public String getLabel()
    {
      return noUri;
    }

    @Override
    public int size()
    {
      return 0;
    }

    @Override
    public int hashCode()
    {
      return 0;
    }

    @Override
    public boolean equals(Object obj)
    {
      return obj instanceof NoUri;
    }

    @Override
    public String getScheme()
    {
      return null;
    }

    @Override
    public URIAuthority getAuthority()
    {
      return URIAuthority.noAuthorityEnum;
    }

    @Override
    public String getPath()
    {
      return null;
    }

    @Override
    public String getQuery()
    {
      return null;
    }

    @Override
    public String getFragment()
    {
      return null;
    }

    @Override
    public ResourceURI parent()
    {
      return this;
    }

    @Override
    public int compareTo(ResourceURI o)
    {
      if (o == this)
        return 0;
      else
        return -1;
    }

    @Override
    public IValue getCell(int index)
    {
      throw new IllegalAccessError("index out of range");
    }

    @Override
    public IValue[] getCells()
    {
      return new IValue[] {};
    }

    @Override
    public IConstructor copy() throws EvaluationException
    {
      return this;
    }

    @Override
    public IConstructor shallowCopy()
    {
      return this;
    }

    @Override
    public void accept(IValueVisitor visitor)
    {
      visitor.visitConstructor(this);
    }
  }

  @Override
  public IType getType()
  {
    return type;
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

  @Override
  public void setCell(int index, IValue value) throws EvaluationException
  {
    throw new IllegalArgumentException("not permitted");
  }

  public static SortedMap<String, Integer> getIndex()
  {
    return index;
  }

  public static ResourceURI parseURI(String uri) throws ResourceException
  {
    return URIUtils.parseUri(uri);
  }

  public ResourceURI resolve(ResourceURI other) throws ResourceException
  {
    if (other instanceof URI) {
      URI otherURI = (URI) other;
      if ((otherURI.getScheme() == null || otherURI.getScheme().equals(getScheme()))
          && otherURI.getAuthority() == URIAuthority.noAuthorityEnum) {
        String otherPath = otherURI.getPath();
        if (otherPath.startsWith("/"))
          return new URI(getScheme(), getAuthority(), otherPath, otherURI.getQuery(), otherURI.getFragment());
        else if (!getPath().equals("."))
          return new URI(getScheme(), getAuthority(), URIUtils.straightenPath(getPath() + "/../" + otherPath), otherURI
              .getQuery(), otherURI.getFragment());
        else
          return new URI(getScheme(), getAuthority(), otherPath, otherURI.getQuery(), otherURI.getFragment());
      }
    }
    return other;
  }

  public ResourceURI resolve(String uri) throws ResourceException
  {
    return resolve(parseURI(uri));
  }

  public ResourceURI relativize(ResourceURI other)
  {
    try {
      other = resolve(other); // first convert to absolute uri

      if (other.getScheme() != null) {
        if (other.getScheme().equals(getScheme()) && other.getAuthority().equals(getAuthority())) {
          String relPath = stripCommonPrefix(getPath(), other.getPath());

          return new URI(getScheme(), getAuthority(), relPath, other.getQuery(), other.getFragment());
        }
      }
    } catch (ResourceException e) {
      return other;
    }

    return other;
  }

  private String stripCommonPrefix(String left, String right)
  {
    String[] leftPieces = left.split("/");
    String[] rightPieces = right.split("/");

    int lmx = leftPieces.length;
    int rmx = rightPieces.length;

    int lx = 0;
    int rx = 0;

    while (lx < lmx && rx < rmx) {
      if (leftPieces[lx].equals(""))
        lx++;
      else if (rightPieces[rx].equals(""))
        rx++;
      else if (leftPieces[lx].equals(rightPieces[rx])) {
        lx++;
        rx++;
      } else
        break;
    }

    StringBuilder common = new StringBuilder();
    String sep = "";
    while (rx < rmx) {
      if (!rightPieces[rx].equals("")) {
        common.append(sep);
        sep = "/";
        common.append(rightPieces[rx]);
      }
      rx++;
    }

    return common.toString();
  }

  public static void declare(ITypeContext cxt)
  {
    IType authorityType = URIAuthority.type;
    IType uriType = type;

    ConstructorSpecifier uriSpec = new RecordSpecifier(Location.nullLoc, uri, 0, null, TypeUtils.constructorType(
        ResourceURI.URI.face, uriType), URI.class, ResourceURI.class);

    ConstructorSpecifier nullSpec = new ConstructorSpecifier(Location.nullLoc, null, noUri, 1, TypeUtils
        .constructorType(uriType), NoUri.class, ResourceURI.class);

    List<IValueSpecifier> specs = new ArrayList<IValueSpecifier>();
    specs.add(uriSpec);
    specs.add(nullSpec);

    ITypeDescription uriDesc = new CafeTypeDescription(Location.nullLoc, type, ResourceURI.class.getName(), specs);

    cxt.defineType(uriDesc);

    ConstructorSpecifier authSpec = new RecordSpecifier(Location.nullLoc, URIAuthority.authority, 0, null, TypeUtils
        .constructorType(URIAuthority.face, authorityType), URIAuthority.Authority.class, URIAuthority.class);

    ConstructorSpecifier noAuthSpec = new ConstructorSpecifier(Location.nullLoc, null, URIAuthority.noAuthority, 1,
        TypeUtils.constructorType(authorityType), NoAuthority.class, URIAuthority.class);

    List<IValueSpecifier> authSpecs = new ArrayList<IValueSpecifier>();
    authSpecs.add(authSpec);
    authSpecs.add(noAuthSpec);

    ITypeDescription authDesc = new CafeTypeDescription(Location.nullLoc, authorityType, URIAuthority.class.getName(),
        authSpecs);

    cxt.defineType(authDesc);
  }
}
