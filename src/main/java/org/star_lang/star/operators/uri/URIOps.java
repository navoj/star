package org.star_lang.star.operators.uri;

import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.uri.runtime.GetURI;
import org.star_lang.star.operators.uri.runtime.URIResolve;
import org.star_lang.star.operators.uri.runtime.URIParse.String2URI;
import org.star_lang.star.operators.uri.runtime.URIParse.URI2String;

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
