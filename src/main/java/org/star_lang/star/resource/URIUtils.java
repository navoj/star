package org.star_lang.star.resource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.star_lang.star.LanguageException;
import org.star_lang.star.StarCompiler;
import org.star_lang.star.compiler.cafe.compile.Utils;
import org.star_lang.star.compiler.util.StringIterator;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.data.value.URIAuthority;
import org.star_lang.star.data.value.URIAuthority.Authority;
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


/**
 * Utilities for handling resource URIs
 *
 * @author fgm
 */
public class URIUtils {
  private static String fileSeparator = File.separator;

  /**
   * Remove clutter from a uri path to obtain the package NAME.
   * <p>
   * This is the sequence of letters that is after the last / and before the first following .
   *
   * @param uri
   * @return the package NAME extracted from the uri
   */
  public static String getPackageName(ResourceURI uri) {
    String path = uri.getPath();

    int slash = path.lastIndexOf('/');
    if (slash >= 0)
      path = path.substring(slash + 1);
    int dot = path.indexOf('.');
    if (dot > 0)
      path = path.substring(0, dot);
    return path;
  }

  public static String stripExtension(String path) {
    for (String ext : StarCompiler.standardExtensions) {
      if (path.endsWith(ext))
        return path.substring(0, path.length() - ext.length());
    }
    return path;
  }

  public static ResourceURI create(String scheme, URIAuthority authority, String path, String query, String fragment) {
    return new ResourceURI.URI(scheme, authority, path, query, fragment);
  }

  public static ResourceURI create(String scheme, String path) {
    return new ResourceURI.URI(scheme, URIAuthority.noAuthorityEnum, path, null, null);
  }

  public static ResourceURI createFileURI(File file) {
    return new ResourceURI.URI(Resources.FILE, URIAuthority.noAuthorityEnum, uriFilePath(file), null, null);
  }

  public static String uriFilePath(File file) {
    try {
      String path = file.getCanonicalPath();
      return uriFilePath(path);
    } catch (IOException e) {
      return file.toString();
    }
  }

  public static String uriFilePath(String path) {
    String uriPath = path.replace(fileSeparator, "/");
    int windozeColon = uriPath.indexOf(':');
    if (windozeColon > 0 && windozeColon < uriPath.indexOf('/'))
      uriPath = "/" + uriPath;
    return uriPath;
  }

  public static ResourceURI createQuotedURI(String name, String text) {
    return createQuotedURI(name, text, null);
  }

  public static ResourceURI createQuotedURI(String name, String text, String query) {
    return new ResourceURI.URI(Resources.QSCHEME, URIAuthority.noAuthorityEnum, name, query, text);
  }

  public static ResourceURI uriWithFragment(ResourceURI uri, String fragment) {
    return new ResourceURI.URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), uri.getQuery(), fragment);
  }

  public static ResourceURI uriWithQuery(ResourceURI uri, String query) {
    return new ResourceURI.URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), query, uri.getFragment());
  }

  public static String straightenPath(String path) throws ResourceException {
    StringBuilder blder = new StringBuilder();
    String[] segs = path.split("/");
    List<String> out = new ArrayList<>();
    String sep = path.startsWith("/") ? "/" : "";

    for (String seg : segs) {
      if (!seg.isEmpty()) {
        switch (seg) {
          case "..":
            if (out.isEmpty())
              throw new ResourceException("attempt to .. out of root");
            else
              out.remove(out.size() - 1);
            break;
          case ".":
            break;
          default:
            out.add(seg);
            break;
        }
      }
    }

    if (!out.isEmpty())
      for (String seg : out) {
        blder.append(sep);
        sep = "/";
        blder.append(seg);
      }
    else
      blder.append(".");
    return blder.toString();
  }

  public static String rootPath(ResourceURI uri) {
    int hash = uri.hashCode();
    String pkgName = getPackageName(uri);
    if (hash < 0)
      pkgName = pkgName + "_" + -hash;
    else
      pkgName = pkgName + hash;
    return Utils.javaIdentifierOf(pkgName);
  }

  public static ResourceURI parseUri(String src) throws ResourceException {
    final String scheme;
    final URIAuthority authority;
    final String path;
    final String query;
    final String fragment;

    if (src.equals(ResourceURI.noUri))
      return ResourceURI.noUriEnum;
    else {

      int colonPos = src.indexOf(':');
      if (colonPos > 0) {
        scheme = condition(src.substring(0, colonPos));
        src = src.substring(colonPos + 1);
        if (src.startsWith("//")) {
          // We have an authority component
          src = src.substring("//".length());
          String user;
          String host;
          int port;

          int atPos = src.indexOf('@');
          if (atPos >= 0) {
            user = condition(src.substring(0, atPos));
            src = src.substring(atPos + 1);
          } else
            user = null;

          int portPos = src.indexOf(':');
          if (portPos >= 0) {
            host = condition(src.substring(0, portPos));
            portPos++;
            int p = 0;
            while (portPos < src.length() && Character.isDigit(src.codePointAt(portPos))) {
              p = p * 10 + Character.digit(src.codePointAt(portPos), 10);
              portPos = src.offsetByCodePoints(portPos, 1);
            }
            port = p;
            src = src.substring(portPos);
            authority = new Authority(user, host, port);
          } else {
            int slPos = src.indexOf('/');
            if (slPos > 0) {
              host = condition(src.substring(0, slPos));
              src = src.substring(slPos);
              port = -1;
              authority = new Authority(user, host, port);
            } else
              authority = URIAuthority.noAuthorityEnum;
          }
        } else
          authority = URIAuthority.noAuthorityEnum;
      } else {
        scheme = null; // the scheme is not known
        authority = URIAuthority.noAuthorityEnum;
      }

      int queryPos = src.indexOf('?');
      if (queryPos > 0) {
        path = condition(src.substring(0, queryPos));
        int fragPos = src.indexOf('#', queryPos);
        if (fragPos > 0) {
          fragment = condition(src.substring(fragPos + 1));
          query = condition(src.substring(queryPos + 1, fragPos));
        } else {
          fragment = null;
          query = condition(src.substring(queryPos + 1));
        }
      } else {
        query = null;
        int fragPos = src.indexOf('#');
        if (fragPos > 0) {
          path = condition(src.substring(0, fragPos));
          fragment = condition(src.substring(fragPos + 1));
        } else {
          path = condition(src);
          fragment = null;
        }
      }

      return new ResourceURI.URI(scheme, authority, path, query, fragment);
    }
  }

  /**
   * The query part of a URI looks like: scheme://Path/...?key=val;..;key=val The queryURI function
   * accesses the query looking for a particular value associated with a keyword
   *
   * @param uri
   * @param keyword
   * @return
   * @throws ResourceException
   */
  public static String queryKeyword(ResourceURI uri, String keyword) {
    String query = uri.getQuery();
    String[] frags = query.split(";");
    for (String pair : frags) {
      int pos = pair.indexOf('=');
      if (pos > 0 && pair.substring(0, pos).equals(keyword))
        return pair.substring(pos + 1);
    }
    return null;
  }

  public static boolean hasKeyword(ResourceURI uri, String keyword) {
    String query = uri.getQuery();
    if (query != null) {
      String[] frags = query.split(";");
      for (String pair : frags) {
        int pos = pair.indexOf('=');
        if (pos > 0 && pair.substring(0, pos).equals(keyword))
          return true;
      }
    }
    return false;
  }

  public static ResourceURI stripKeyword(ResourceURI uri, String keyword) {
    String query = uri.getQuery();

    if (query != null) {
      boolean found = false;
      String[] frags = query.split(";");
      StringBuilder result = new StringBuilder();
      String sep = "";
      for (String pair : frags) {
        int pos = pair.indexOf('=');
        if (pos > 0 && pair.substring(0, pos).equals(keyword))
          found = true;
        else {
          result.append(sep);
          sep = ";";
          result.append(pair);
        }
      }
      if (found)
        return create(uri.getScheme(), uri.getAuthority(), uri.getPath(), result.toString(), uri.getFragment());
    }
    return uri;
  }

  public static ResourceURI setKeyword(ResourceURI uri, String keyword, String value) {
    String query = uri.getQuery();
    String[] frags = query != null ? query.split(";") : new String[]{};
    StringBuilder result = new StringBuilder();
    String sep = "";
    boolean found = false;
    for (String pair : frags) {
      int pos = pair.indexOf('=');
      if (pos > 0 && pair.substring(0, pos).equals(keyword)) {
        found = true;
        if (value != null) {
          result.append(sep);
          sep = ";";
          result.append(keyword);
          result.append("=");
          result.append(value);
        }
      } else {
        result.append(sep);
        sep = ";";
        result.append(pair);
      }
    }
    if (!found && value != null) {
      result.append(sep);
      result.append(keyword);
      result.append("=");
      result.append(value);
    }
    return create(uri.getScheme(), uri.getAuthority(), uri.getPath(), result.toString(), uri.getFragment());
  }

  // Pick up on the UTF-8 encoding scheme:
  // 0000 0000-0000 007F 0xxxxxxx
  // 0000 0080-0000 07FF 110xxxxx 10xxxxxx
  // 0000 0800-0000 FFFF 1110xxxx 10xxxxxx 10xxxxxx
  //
  // 0001 0000-001F FFFF 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
  // 0020 0000-03FF FFFF 111110xx 10xxxxxx 10xxxxxx 10xxxxxx 10xxxxxx
  // 0400 0000-7FFF FFFF 1111110x 10xxxxxx ... 10xxxxxx

  private static String condition(String uri) {
    // replace %xx with appropriate characters
    StringBuilder blder = new StringBuilder();
    Iterator<Integer> it = new StringIterator(uri);

    while (it.hasNext()) {
      Integer ch = it.next();
      if (ch == '%') {
        int hi = Character.digit(it.next(), 16);
        int lo = Character.digit(it.next(), 16);
        int code = (hi << 4) | lo;
        if ((code & 0x80) == 0)
          blder.appendCodePoint(code);
        else if ((code & 0xe0) == 0xc0) { // 0000 0080-0000 07FF 110xxxxx 10xxxxxx
          code = code & 0x1f;
          code = (code << 6) | hex(it);
          blder.appendCodePoint(code);
        } else if ((code & 0xe0) == 0xe0) {// 0000 0800-0000 FFFF 1110xxxx 10xxxxxx 10xxxxxx
          code = code & 0xf;
          code = (code << 6) | hex(it);
          code = (code << 6) | hex(it);
          blder.appendCodePoint(code);
        } else if ((code & 0xf8) == 0xf0) {// 0001 0000-001F FFFF 11110xxx 10xxxxxx 10xxxxxx
          // 10xxxxxx
          code = code & 0xf;
          code = (code << 6) | hex(it);
          code = (code << 6) | hex(it);
          code = (code << 6) | hex(it);
          blder.appendCodePoint(code);
        } else if ((code & 0xfc) == 0xf8) {// 0020 0000-03FF FFFF 111110xx 10xxxxxx 10xxxxxx
          // 10xxxxxx 10xxxxxx
          code = code & 0xf;
          code = (code << 6) | hex(it);
          code = (code << 6) | hex(it);
          code = (code << 6) | hex(it);
          code = (code << 6) | hex(it);
          blder.appendCodePoint(code);
        } else if ((code & 0xfe) == 0xfc) {// 0400 0000-7FFF FFFF 1111110x 10xxxxxx ... 10xxxxxx
          code = code & 0xf;
          code = (code << 6) | hex(it);
          code = (code << 6) | hex(it);
          code = (code << 6) | hex(it);
          code = (code << 6) | hex(it);
          code = (code << 6) | hex(it);
          blder.appendCodePoint(code);
        } else
          blder.appendCodePoint(0xfffd); // sustitute char
      } else
        blder.appendCodePoint(ch);
    }
    return blder.toString();
  }

  public static String encodeURIFragment(String frag, String allow, String reserved) {
    // replace %xx with appropriate characters
    StringBuilder blder = new StringBuilder();
    Iterator<Integer> it = new StringIterator(frag);

    while (it.hasNext()) {
      Integer ch = it.next();

      if (((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9') || ch == '_' || ch == '@'
              || ch == '/' || ch == '.' || ch == '+' || ch == '-' || allow.indexOf(ch) >= 0)
              && reserved.indexOf(ch) < 0)
        blder.appendCodePoint(ch);
      else {
        blder.append("%");
        if (ch <= 0x7f)
          enByte(ch, blder);
        else if (ch >= 0x80 && ch <= 0x7ff) { // 0000 0080-0000 07FF 110xxxxx 10xxxxxx
          enByte(0xc0 | ((ch >> 6) & 0x1f), blder);
          enByte(0x80 | (ch & 0x3f), blder);
        } else if (ch >= 0x800 && ch <= 0xffff) { // 0000 0800-0000 FFFF 1110xxxx 10xxxxxx 10xxxxxx
          enByte(0xe0 | ((ch >> 12) & 0xf), blder);
          enByte(0x80 | ((ch >> 6) & 0x3f), blder);
          enByte(0x80 | (ch & 0x3f), blder);
        } else if (ch >= 0x10000 && ch <= 0x1ffff) {// 0001 0000-001F FFFF 11110xxx 10xxxxxx
          // 10xxxxxx
          // 10xxxxxx
          enByte(0xf0 | ((ch >> 21) & 0x7), blder);
          enByte(0x80 | ((ch >> 18) & 0x3f), blder);
          enByte(0x80 | ((ch >> 12) & 0x3f), blder);
          enByte(0x80 | ((ch >> 6) & 0x3f), blder);
          enByte(0x80 | (ch & 0x3f), blder);
        } else if (ch >= 0x200000 && ch <= 0x3fffff) {// 0020 0000-03FF FFFF 111110xx 10xxxxxx
          // 10xxxxxx
          // 10xxxxxx 10xxxxxx
          enByte(0xf8 | ((ch >> 24) & 0x3), blder);
          enByte(0x80 | ((ch >> 18) & 0x3f), blder);
          enByte(0x80 | ((ch >> 12) & 0x3f), blder);
          enByte(0x80 | ((ch >> 6) & 0x3f), blder);
          enByte(0x80 | (ch & 0x3f), blder);
        } else {// 0400 0000-7FFF FFFF 1111110x 10xxxxxx ... 10xxxxxx
          enByte(0xfc | ((ch >> 30) & 0x3), blder);
          enByte(0x80 | ((ch >> 24) & 0x3f), blder);
          enByte(0x80 | ((ch >> 18) & 0x3f), blder);
          enByte(0x80 | ((ch >> 12) & 0x3f), blder);
          enByte(0x80 | ((ch >> 6) & 0x3f), blder);
          enByte(0x80 | (ch & 0x3f), blder);
        }
      }
    }
    return blder.toString();
  }

  private static int hex(Iterator<Integer> it) throws IndexOutOfBoundsException {
    if (it.hasNext()) {
      Integer nx = it.next();
      if (nx == '%') {
        int hi = Character.digit(it.next(), 16);
        int lo = Character.digit(it.next(), 16);
        return ((hi << 4) | lo) & 0x3f;
      } else
        return nx;
    } else
      throw new IndexOutOfBoundsException();
  }

  private static void enNibble(int xx, StringBuilder blder) {
    if (xx == 0)
      blder.append("0");
    else if (xx <= 9)
      blder.appendCodePoint('0' + xx);
    else
      blder.appendCodePoint('A' + xx - 10);
  }

  private static void enByte(int xx, StringBuilder blder) {
    enNibble((xx >> 4) & 0xf, blder);
    enNibble(xx & 0xf, blder);
  }

  public static void setupUriTransducerRule(String rule) throws LanguageException {
    TransducerGenerator.generate(rule);
  }

  public static void setupStarURI(String tgtDir) throws LanguageException {
    setupUriTransducerRule("star:([^/]*:O)/([^/]*:D)(/([^/]*:V))?==>file://" + tgtDir + "/$D.star");
  }

  public static void setupStarURI(File dir) throws LanguageException {
    setupStarURI(URIUtils.uriFilePath(dir));
  }
}
