package org.star_lang.star.operators.uri;

import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.uri.runtime.GetURI;
import org.star_lang.star.operators.uri.runtime.URIResolve;
import org.star_lang.star.operators.uri.runtime.URIParse.String2URI;
import org.star_lang.star.operators.uri.runtime.URIParse.URI2String;

/**
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
public class URIOps
{
  public static final String string2uri = "__string2uri";
  public static final String uri2string = "__uri2string";
  public static final String getUri = "__getUri";
  public static final String resolveUri = "__resolveUri";

  public static void declare()
  {
    Intrinsics.declare(new Builtin(string2uri, String2URI.type(), String2URI.class));
    Intrinsics.declare(new Builtin(uri2string, URI2String.type(), URI2String.class));
    Intrinsics.declare(new Builtin(getUri, GetURI.type(), GetURI.class));
    Intrinsics.declare(new Builtin(resolveUri, URIResolve.type(), URIResolve.class));
  }
}
