package org.star_lang.star.resource;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.star_lang.star.StarRules;
import org.star_lang.star.compiler.util.FileUtil;
import org.star_lang.star.compiler.util.StringUtils;
import org.star_lang.star.data.value.ResourceURI;
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

public class Resources
{
  public static final String FILE = "file";
  public static final String STDSCHEME = "std";
  public static final String QSCHEME = "$quoted$";
  public static final String HTTP = "http";
  public static final String STAR = "star";

  private static final Map<String, Transducer> transducers = new HashMap<>();

  static {
    transducers.put(FILE, new FileTransducer());
    transducers.put(STDSCHEME, new StdTransducer());
    transducers.put(QSCHEME, new QuoteTransducer());
    transducers.put(HTTP, new HttpTransducer());
  }

  public static Reader getReader(ResourceURI uri) throws ResourceException
  {
    Transducer transducer = transducers.get(uri.getScheme());
    if (transducer != null)
      return transducer.getReader(uri);
    else
      throw new ResourceException("cannot find transducer for " + uri);
  }

  public static InputStream getInputStream(ResourceURI uri) throws ResourceException
  {
    Transducer transducer = transducers.get(uri.getScheme());
    if (transducer != null)
      return transducer.getInputStream(uri);
    else
      throw new ResourceException("cannot find transducer for " + uri);
  }

  public static String getUriContent(ResourceURI uri) throws ResourceException
  {
    try (Reader rdr = getReader(uri)) {
      return FileUtil.readFileIntoString(rdr);
    } catch (IOException e) {
      throw new ResourceException(e.getMessage());
    }
  }

  public static boolean exists(ResourceURI uri) throws ResourceException
  {
    Transducer transducer = transducers.get(uri.getScheme());
    if (transducer != null)
      return transducer.exists(uri);
    else
      throw new ResourceException("cannot find transducer for " + uri);
  }

  public static void putResource(ResourceURI uri, String obj) throws ResourceException
  {
    Transducer transducer = transducers.get(uri.getScheme());
    if (transducer != null)
      transducer.putResource(uri, obj);
    else
      throw new ResourceException("cannot find transducer for " + uri);
  }

  public static OutputStream getOutputStream(ResourceURI uri) throws ResourceException
  {
    Transducer transducer = transducers.get(uri.getScheme());
    if (transducer != null)
      return transducer.getOutputStream(uri);
    else
      throw new ResourceException("cannot find transducer for " + uri);
  }

  public static byte[] getUriData(ResourceURI uri) throws ResourceException
  {
    try (InputStream str = getInputStream(uri)) {
      return FileUtil.readFileIntoBytes(str);
    } catch (IOException e) {
      throw new ResourceException("could not read bytes from " + uri, e);
    }
  }

  public static void recordTransducer(String scheme, Transducer transducer)
  {
    transducers.put(scheme, transducer);
  }

  public static class JarTransducer implements Transducer
  {
    private final Class<?> rootClass;

    public JarTransducer(Class<?> rootClass)
    {
      this.rootClass = rootClass;
    }

    @Override
    public Reader getReader(ResourceURI uri) throws ResourceException
    {
      String fullResourceName = uri.getPath();
      InputStream istream = rootClass.getResourceAsStream(fullResourceName);

      if (istream == null && !fullResourceName.startsWith("/")) {
        fullResourceName = "/" + fullResourceName;
        istream = StarRules.class.getResourceAsStream(fullResourceName);
      }
      if (istream != null)
        return new InputStreamReader(istream);
      else
        throw new ResourceException(uri + " not accessible");
    }

    @Override
    public InputStream getInputStream(ResourceURI uri) throws ResourceException
    {
      String fullResourceName = uri.getPath();
      InputStream istream = rootClass.getResourceAsStream(fullResourceName);

      if (istream == null && !fullResourceName.startsWith("/")) {
        fullResourceName = "/" + fullResourceName;
        istream = StarRules.class.getResourceAsStream(fullResourceName);
      }
      if (istream != null)
        return istream;
      else
        throw new ResourceException(uri + " not accessible");
    }

    @Override
    public void putResource(ResourceURI uri, String resource) throws ResourceException
    {
      throw new ResourceException("not permitted");
    }

    @Override
    public OutputStream getOutputStream(ResourceURI uri) throws ResourceException
    {
      throw new ResourceException("not permitted");
    }

    @Override
    public boolean exists(ResourceURI uri) throws ResourceException
    {
      String path = uri.getPath();
      return rootClass.getResource(path) != null;
    }
  }

  private static class QuoteTransducer implements Transducer
  {
    @Override
    public Reader getReader(ResourceURI uri) throws ResourceException
    {
      String fragment = uri.getFragment();
      if (fragment == null)
        throw new ResourceException("QuoteTransducer cannot find fragment in " + uri);
      return new StringReader(fragment);
    }

    @Override
    public InputStream getInputStream(ResourceURI uri) throws ResourceException
    {
      String fragment = uri.getFragment();
      if (fragment == null)
        throw new ResourceException("QuoteTransducer cannot find fragment in " + uri);
      return new ByteArrayInputStream(fragment.getBytes());
    }

    @Override
    public void putResource(ResourceURI uri, String resource) throws ResourceException
    {
      throw new ResourceException("not permitted");
    }

    @Override
    public boolean exists(ResourceURI uri) throws ResourceException
    {
      return uri.getFragment() != null;
    }

    @Override
    public OutputStream getOutputStream(ResourceURI uri) throws ResourceException
    {
      throw new ResourceException("not permitted");
    }
  }

  public static String resourceHash(ResourceURI uri) throws ResourceException
  {
    String text = getUriContent(uri);
    if (text != null)
      return resourceHash(text);
    else
      throw new ResourceException("cannot access " + uri);
  }

  public static String resourceHash(String srcText) throws ResourceException
  {
    byte[] data = srcText.getBytes();
    try {
      MessageDigest md5 = MessageDigest.getInstance("MD5");
      md5.update(data);
      byte[] digest = md5.digest();

      return StringUtils.hexString(digest);
    } catch (NoSuchAlgorithmException e) {
      throw new ResourceException("cannot compute hash of " + srcText, e);
    }
  }
}
