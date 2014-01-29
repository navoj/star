package org.star_lang.star.code.repository.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.star_lang.star.code.repository.AbstractCodeRepository;
import org.star_lang.star.code.repository.CodeCatalog;
import org.star_lang.star.code.repository.CodeHash;
import org.star_lang.star.code.repository.CodeTree;
import org.star_lang.star.code.repository.RepositoryException;
import org.star_lang.star.code.repository.RepositoryNode;
import org.star_lang.star.code.repository.RepositoryNodeImpl;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.util.FileUtil;

import com.starview.platform.data.value.ResourceURI;
import com.starview.platform.resource.ResourceException;
import com.starview.platform.resource.URIUtils;
import com.starview.platform.resource.catalog.CatalogException;

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

public class ZipCodeRepository extends AbstractCodeRepository
{
  private final ZipArchive zipDir;
  private final Map<ResourceURI, RepositoryNode> cache = new HashMap<ResourceURI, RepositoryNode>();
  private final RepositoryNode generics;

  public ZipCodeRepository(File file, ClassLoader loader, ErrorReport errors) throws IOException, RepositoryException,
      ResourceException
  {
    super(loader, errors);
    zipDir = ZipArchive.openArchive(file);
    for (Entry<String, CodeTree> ent : zipDir) {
      ResourceURI uri = zipDir.nameToURI(ent.getKey());
      if (uri != null)
        installNode(uri, ent.getValue());
    }
    generics = new RepositoryNodeImpl(ResourceURI.noUriEnum, null, (CodeCatalog) zipDir.resolve("__std", null));
  }

  public ZipCodeRepository(File file, ErrorReport errors) throws IOException, RepositoryException, ResourceException
  {
    this(file, Thread.currentThread().getContextClassLoader(), errors);
  }

  public ZipCodeRepository(InputStream str, ClassLoader loader, String path, ErrorReport errors)
      throws RepositoryException, ResourceException, IOException
  {
    super(loader, errors);
    zipDir = ZipArchive.openArchive(str, path);
    for (Entry<String, CodeTree> ent : zipDir) {
      ResourceURI uri = zipDir.nameToURI(ent.getKey());
      if (uri != null)
        installNode(uri, ent.getValue());
    }
    generics = new RepositoryNodeImpl(ResourceURI.noUriEnum, null, (CodeCatalog) zipDir.resolve("__std", null));
  }

  @Override
  public RepositoryNode addRepositoryNode(ResourceURI uri, String hash, CodeCatalog code) throws RepositoryException
  {
    throw new UnsupportedOperationException("not permitted");
  }

  @Override
  public void removeRepositoryNode(ResourceURI uri) throws CatalogException
  {
    throw new CatalogException("not permitted");
  }

  @Override
  public String findHash(ResourceURI uri)
  {
    RepositoryNode node = findNode(uri);
    if (node != null)
      return node.getHash();
    return null;
  }

  @Override
  public RepositoryNode findNode(ResourceURI uri)
  {
    String path = URIUtils.rootPath(uri);
    RepositoryNode node = cache.get(uri);

    if (node != null)
      return node;
    else {
      try {
        CodeTree entry = zipDir.resolve(path, null);

        if (entry instanceof CodeCatalog) {
          node = readRepositoryNodeHash((CodeCatalog) entry, uri);
          if (node != null) {
            cache.put(uri, node);
            triggerUpdates(node);
          }
          return node;
        }
      } catch (Exception e) {
      }
    }
    return null;
  }

  private RepositoryNode installNode(ResourceURI uri, CodeTree entry)
  {
    RepositoryNode node = cache.get(uri);

    if (node != null)
      return node;
    else {
      try {
        if (entry instanceof CodeCatalog) {
          node = readRepositoryNodeHash((CodeCatalog) entry, uri);
          if (node != null) {
            cache.put(uri, node);
            triggerUpdates(node);
          }
          return node;
        }
      } catch (Exception e) {
      }
    }
    return null;
  }

  @Override
  public CodeCatalog synthCodeCatalog()
  {
    return generics.getCode();
  }

  private RepositoryNode readRepositoryNodeHash(final CodeCatalog sub, ResourceURI uri) throws IOException,
      RepositoryException
  {
    final CodeTree hashCode = sub.resolve(CodeHash.HASH, null);

    if (hashCode instanceof CodeHash)
      return new ZipNode(uri, ((CodeHash) hashCode).getHash(), sub);
    else
      throw new IllegalStateException("missing " + CodeHash.HASH);
  }

  @Override
  public Iterator<RepositoryNode> iterator()
  {
    return cache.values().iterator();
  }

  public static File createZarFromDir(File dir) throws IOException
  {
    File zip = new File(dir.getAbsolutePath() + ZipArchive.EXTENSION);

    createZarFromDir(dir, zip);
    return zip;
  }

  public static void createZarFromDir(File dir, File ret) throws IOException
  {
    assert dir.isDirectory();
    ZipOutputStream os = new ZipOutputStream(new FileOutputStream(ret));
    Stack<File> dirs = new Stack<File>();
    dirs.add(dir);
    int tmpDirNameLength = dir.getPath().length();
    try {
      while (!dirs.empty()) {
        File d = dirs.pop();
        for (File ent : d.listFiles()) {
          if (ent.isDirectory()) {
            dirs.add(ent);
          } else {
            ZipEntry ze = new ZipEntry(ent.getPath().substring(tmpDirNameLength + 1));
            os.putNextEntry(ze);
            FileInputStream rdr = new FileInputStream(ent);
            byte[] bt = FileUtil.readFileIntoBytes(rdr);
            rdr.close();
            os.write(bt);
          }
        }
      }
    } finally {
      os.close();
    }
  }

  @SuppressWarnings("serial")
  private class ZipNode extends RepositoryNodeImpl
  {
    ZipNode(ResourceURI uri, String hash, CodeCatalog code)
    {
      super(uri, hash, code);
    }
  }

}
