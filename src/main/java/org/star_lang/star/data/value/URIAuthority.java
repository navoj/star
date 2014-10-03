package org.star_lang.star.data.value;

import java.util.SortedMap;
import java.util.TreeMap;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IConstructor;
import org.star_lang.star.data.IRecord;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.IValueVisitor;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.type.TypeInterfaceType;

/**
 * A Resource URI identifies a resource by name. The URIAuthority encapsulates the authority part of
 * the uri
 * 
 * Follows the Star type definitions:
 * 
 * <pre>
 * type uriAuthority is authority{
 *   user has type string;
 *   host has type string;
 *   port has type integer
 *   }
 * or noAuthority;
 * </pre>
 * 
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
public abstract class URIAuthority implements PrettyPrintable, IConstructor
{
  public static final String authority = "authority";
  public static final IType type = TypeUtils.typeExp(authority);

  public static final IType stringType = StandardTypes.stringType;
  public static final IType integerType = StandardTypes.integerType;

  // Field names in an authority
  public static final String USER = "scheme";
  public static final String HOST = "authority";
  public static final String PORT = "path";

  private static final String[] authorityMembers = { USER, HOST, PORT };
  private static final int userIx = 0;
  private static final int hostIx = 1;
  private static final int portIx = 2;
  private static final int authFieldCount = 3;

  public static final String noAuthority = "noAuthority";
  public static final NoAuthority noAuthorityEnum = new NoAuthority();

  public static final SortedMap<String, Integer> index;

  public static final TypeInterfaceType face;

  static {
    index = new TreeMap<>();
    index.put(USER, userIx);
    index.put(HOST, hostIx);
    index.put(PORT, portIx);

    SortedMap<String, IType> fields = new TreeMap<>();
    fields.put(USER, stringType);
    fields.put(HOST, stringType);
    fields.put(PORT, integerType);
    face = new TypeInterfaceType(fields);
  }

  public static class Authority extends URIAuthority implements IRecord
  {
    private final String user;
    private final String host;
    private final int port;

    public Authority(String user, String host, int port)
    {
      if (user == null)
        this.user = "";
      else
        this.user = user;

      this.host = host;
      this.port = port;
    }

    public Authority(IValue user, IValue host, IValue port, IValue lineOffset, IValue length)
        throws EvaluationException
    {
      this.user = Factory.stringValue(user);
      this.host = Factory.stringValue(host);
      this.port = Factory.intValue(port);
    }

    @Override
    public int conIx()
    {
      return 0;
    }

    @Override
    public String getLabel()
    {
      return authority;
    }

    @Override
    public int size()
    {
      return authFieldCount;
    }

    public String getUser()
    {
      return user;
    }

    public String getHost()
    {
      return host;
    }

    public int getPort()
    {
      return port;
    }

    @Override
    public boolean equals(Object obj)
    {
      return obj instanceof Authority && ((Authority) obj).user.equals(user) && ((Authority) obj).port == port
          && ((Authority) obj).host.equals(host);
    }

    @Override
    public int hashCode()
    {
      return ((user.hashCode() * 37 + host.hashCode()) * 37 + port) * 3;
    }

    @Override
    public IRecord copy()
    {
      return new Authority(user, host, port);
    }

    @Override
    public IRecord shallowCopy()
    {
      return copy();
    }

    @Override
    public void prettyPrint(PrettyPrintDisplay disp)
    {
      disp.append("//");
      if (user != null && !user.isEmpty()) {
        disp.append(user);
        disp.append("@");
      }
      disp.append(host);
      if (port >= 0) {
        disp.append(":");
        disp.append(port);
      }
    }

    @Override
    public IValue getCell(int index)
    {
      switch (index) {
      case userIx:
        return Factory.newString(user);
      case hostIx:
        return Factory.newString(host);
      case portIx:
        return Factory.newInt(port);
      }
      throw new IllegalArgumentException("index out of range");
    }

    public String get___0()
    {
      return user;
    }

    public String get___1()
    {
      return host;
    }

    public int get___2()
    {
      return port;
    }

    @Override
    public IValue getMember(String memberName)
    {
      Integer ix = index.get(memberName);
      if (ix != null)
        switch (ix) {
        case userIx:
          return Factory.newString(user);
        case hostIx:
          return Factory.newString(host);
        case portIx:
          return Factory.newInt(port);
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
      return authorityMembers;
    }

    @Override
    public void accept(IValueVisitor visitor)
    {
      visitor.visitRecord(this);
    }
  }

  public static class NoAuthority extends URIAuthority
  {
    private NoAuthority()
    {
    };

    @Override
    public void prettyPrint(PrettyPrintDisplay disp)
    {
      disp.append(noAuthority);
    }

    @Override
    public int conIx()
    {
      return 1;
    }

    @Override
    public String getLabel()
    {
      return noAuthority;
    }

    @Override
    public int hashCode()
    {
      return 0;
    }

    @Override
    public boolean equals(Object obj)
    {
      return obj instanceof NoAuthority;
    }

    @Override
    public int size()
    {
      return 0;
    }

    @Override
    public IValue getCell(int index)
    {
      throw new IllegalAccessError("index out of range");
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

    protected Object readResolve()
    {
      return noAuthorityEnum;
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

  @Override
  public IValue[] getCells()
  {
    throw new UnsupportedOperationException("not permitted");
  }

  public static SortedMap<String, Integer> getIndex()
  {
    return index;
  }
}
