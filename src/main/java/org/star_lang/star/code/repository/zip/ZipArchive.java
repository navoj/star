package org.star_lang.star.code.repository.zip;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

import org.star_lang.star.code.Manifest;
import org.star_lang.star.code.repository.CodeCatalog;
import org.star_lang.star.code.repository.CodeDirectory;
import org.star_lang.star.code.repository.CodeHash;
import org.star_lang.star.code.repository.CodeTree;
import org.star_lang.star.code.repository.RepositoryException;
import org.star_lang.star.compiler.util.FileUtil;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.data.value.URIAuthority;
import org.star_lang.star.resource.ResourceException;
import org.star_lang.star.resource.Resources;
import org.star_lang.star.resource.URIUtils;

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

@SuppressWarnings("serial")
public class ZipArchive implements CodeCatalog
{
  public static final String EXTENSION = ".sar";

  private final String path;
  private final Map<String, CodeTree> entries = new HashMap<>();
  private final Map<String, ResourceURI> uriTransMap = new HashMap<>();

  static {
    Resources.recordTransducer(ZipTransducer.SCHEME, new ZipTransducer());
  }

  private ZipArchive(String path)
  {
    this.path = path;
  }

  @Override
  public String getPath()
  {
    return path;
  }

  @Override
  public String getExtension()
  {
    return EXTENSION;
  }

  @Override
  public void write(File output) throws IOException
  {
    throw new IOException("not permitted");
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append(ZipTransducer.SCHEME);
    disp.append(":");
    disp.appendQuoted(path);
  }

  @Override
  public Iterator<Entry<String, CodeTree>> iterator()
  {
    return entries.entrySet().iterator();
  }

  @Override
  public void addCodeEntry(String path, CodeTree code) throws RepositoryException
  {
    throw new RepositoryException("not permitted");
  }

  @Override
  public CodeTree resolve(String path, String extension) throws RepositoryException
  {
    if (path.contains("/")) {
      int slash = path.indexOf('/');
      String dir = path.substring(0, slash);
      String rest = path.substring(slash + 1);
      CodeTree code = findInEntries(dir, null);
      if (code instanceof CodeCatalog)
        return ((CodeCatalog) code).resolve(rest, extension);
      else
        return null;
    } else
      return findInEntries(path, extension);
  }

  @Override
  public CodeCatalog subCatalog(String path) throws RepositoryException
  {
    if (path.contains("/")) {
      int slash = path.indexOf('/');
      String dir = path.substring(0, slash);
      String rest = path.substring(slash + 1);
      CodeTree code = findInEntries(dir, null);
      if (code instanceof CodeCatalog)
        return ((CodeCatalog) code).subCatalog(rest);
      else
        return null;
    } else
      return findSubCatalog(path);
  }

  private CodeTree findInEntries(String path, String extension)
  {
    if (extension != null && entries.get(safeName(path) + "." + extension) != null) {
      return entries.get(safeName(path) + "." + extension);
    } else if (entries.get(safeName(path)) != null) {
      return entries.get(safeName(path));
    } else
      return entries.get(path);
  }

  private CodeCatalog findSubCatalog(String path)
  {
    if (entries.get(safeName(path)) != null) {
      return (CodeCatalog) entries.get(safeName(path));
    } else
      return (CodeCatalog) entries.get(path);
  }

  @Override
  public CodeTree fork(String path) throws RepositoryException
  {
    throw new RepositoryException("not permitted");
  }

  @Override
  public boolean isPresent(String path)
  {
    if (path.contains("/")) {
      int slash = path.indexOf('/');
      String dir = path.substring(0, slash);
      String rest = path.substring(slash + 1);
      CodeTree code = entries.get(dir);
      if (code instanceof CodeCatalog)
        return ((CodeCatalog) code).isPresent(rest);
      else
        return false;
    } else
      return entries.containsKey(path);
  }

  @Override
  public boolean isPresent(String path, String extension)
  {
    if (path.contains("/")) {
      int slash = path.indexOf('/');
      String dir = path.substring(0, slash);
      String rest = path.substring(slash + 1);
      CodeTree code = entries.get(dir);
      if (code instanceof CodeCatalog)
        return ((CodeCatalog) code).isPresent(rest, extension);
      else
        return false;
    } else
      return findInEntries(path, extension) != null;
  }

  @Override
  public boolean isReadWrite()
  {
    return false;
  }

  @Override
  public void mergeEntries(CodeCatalog other) throws RepositoryException
  {
    throw new RepositoryException("not permitted");
  }

  private void recordEntry(ZipEntry zEntry, ZipInputStream zip, String path) throws ZipException, RepositoryException,
      ResourceException, IOException
  {
    ZipArchive zipDir = this;
    String name = URIUtils.uriFilePath(zEntry.getName());
    int slash = name.indexOf('/');
    while (slash > 0 && slash < name.length() - 1) {
      String dir = name.substring(0, slash);
      CodeTree code = zipDir.entries.get(dir);
      if (code == null) {
        code = new ZipArchive(dir);
        zipDir.entries.put(dir, code);
        zipDir = (ZipArchive) code;
        name = name.substring(slash + 1);
        slash = name.indexOf('/');
      } else if (code instanceof ZipArchive) {
        zipDir = (ZipArchive) code;
        name = name.substring(slash + 1);
        slash = name.indexOf('/');
      } else
        throw new ZipException("(internal) illegal entry in ZipDirectory");
    }
    int extensionStart = name.lastIndexOf('.');
    String extension = "";
    if (extensionStart > 0) {
      extension = name.substring(extensionStart + 1);
    } else if (name.equals(CodeHash.HASH)) {
      try {
        BufferedReader rdr = new BufferedReader(new InputStreamReader(zip));
        String hash = rdr.readLine();
        if (hash != null) {
          int hashPos = hash.indexOf('#');
          CodeHash cHash = new CodeHash(hash.substring(0, hashPos));
          zipDir.entries.put(name, cHash);
          return;
        } else
          throw new ZipException("(internal) illegal " + CodeHash.HASH + " in ZipDirectory");
      } catch (IOException e) {
        throw new RepositoryException("Could not read hash: " + e.getMessage());
      }
    } else
      return; // Everything should have an extension.

    byte[] content = FileUtil.readFileIntoBytes(zip);

    ResourceURI zipUri = URIUtils.create(ZipTransducer.SCHEME, URIAuthority.noAuthorityEnum, path, null, zEntry
        .getName());
    CodeTree code = CodeDirectory.parse(zipUri, new ByteArrayInputStream(content), extension);

    zipDir.entries.put(name, code);
    if (extension.equals(Manifest.EXTENSION)) {
      uriTransMap.put(zipDir.path, ((Manifest) code).getUri());
    }
  }

  public static ZipArchive openArchive(InputStream str, String path) throws RepositoryException, ResourceException,
      IOException
  {
    ZipInputStream zip = new ZipInputStream(str);
    ZipArchive root = new ZipArchive("");

    ZipEntry ent = null;
    while ((ent = zip.getNextEntry()) != null) {
      if (!ent.getName().startsWith("META-INF/"))
        root.recordEntry(ent, zip, path);
    }

    return root;
  }

  public static ZipArchive openArchive(File file) throws ZipException, IOException, RepositoryException,
      ResourceException
  {
    try (InputStream zipStream = new FileInputStream(file)) {
      return openArchive(zipStream, URIUtils.uriFilePath(file));
    }
  }

  public ResourceURI nameToURI(String name)
  {
    return uriTransMap.get(name);
  }

  private static final int MAX_FILENAME = 32;

  /**
   * Introduce a hash into the file NAME so that upper/lower case file systems dont get confused and
   * we truncate the file NAME to avoid over-long file names.
   * 
   * @param name
   * @return
   */

  private static String safeName(String name)
  {
    int hashCode = name.hashCode();
    int bound = Math.min(name.length(), MAX_FILENAME);
    if (hashCode >= 0)
      return name.substring(0, bound) + hashCode;
    else
      return name.substring(0, bound) + "_" + Math.abs(hashCode);
  }
}
