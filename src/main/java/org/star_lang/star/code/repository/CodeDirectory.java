package org.star_lang.star.code.repository;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.star_lang.star.code.CafeClassParser;
import org.star_lang.star.code.CafeCode;
import org.star_lang.star.code.ClassFile;
import org.star_lang.star.code.Manifest;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.cafe.compile.CafeManifest;
import org.star_lang.star.compiler.cafe.compile.ClassRoot;
import org.star_lang.star.compiler.sources.ManifestParser;
import org.star_lang.star.compiler.sources.MetaRules;
import org.star_lang.star.compiler.util.FileUtil;
import org.star_lang.star.compiler.util.NullIterator;
import org.star_lang.star.compiler.util.Pair;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.StringUtils;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.resource.ResourceException;
import org.star_lang.star.resource.URIUtils;

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
@SuppressWarnings("serial")
public class CodeDirectory implements CodeCatalog
{
  private static String fileSeparator = File.separator;
  private final String path;
  private final File dir;
  private final Map<File, CodeTree> cache = new HashMap<File, CodeTree>();

  private static final int MAX_FILENAME = 32;
  private final boolean overwrite;

  private final static Map<String, CodeParser> parsers = new HashMap<String, CodeParser>();

  static {
    registerParser(Manifest.EXTENSION, new ManifestParser());
    registerParser(CafeCode.EXTENSION, new CafeClassParser());
    registerParser(MetaRules.EXTENSION, new MetaRules());
    registerParser(CafeManifest.EXTENSION, new CafeManifest());
    registerParser(ClassRoot.EXTENSION, new ClassRoot(null, null));
  }

  public CodeDirectory(File dir) throws RepositoryException
  {
    this("", dir, false);
  }

  public CodeDirectory(String path, File dir, boolean overwrite) throws RepositoryException
  {
    this.path = path;
    this.dir = dir;
    this.overwrite = overwrite;
    if (dir.exists()) {
      if (overwrite) {
        FileUtil.rmRf(dir);
        if (!dir.mkdirs())
          throw new RepositoryException("cannot create repository directory " + path);
      } else if (!dir.isDirectory())
        throw new RepositoryException("cannot use " + path + " as a code directory, because it is not a directory");
    } else if (!dir.mkdirs())
      throw new RepositoryException("cannot create repository directory " + path);
  }

  @Override
  public String getPath()
  {
    return path;
  }

  @Override
  public String getExtension()
  {
    // Code directories do not have an extension
    return null;
  }

  public File getDir()
  {
    return dir;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    int mark = disp.markIndent(2);
    disp.appendQuoted(getPath());
    disp.append("is codeDirectory{\n");
    if (dir.exists()) {
      String files[] = dir.list();
      String sep = "";
      for (String fl : files) {
        disp.append(sep);
        sep = ";\n";
        disp.appendQuoted(fl);
      }
    }
    disp.popIndent(mark);
    disp.append("\n}");
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

  @Override
  public Iterator<Entry<String, CodeTree>> iterator()
  {
    if (dir.exists()) {
      final String files[] = dir.list();

      return new Iterator<Entry<String, CodeTree>>() {
        int ix = 0;

        @Override
        public boolean hasNext()
        {
          return ix < files.length;
        }

        @Override
        public Entry<String, CodeTree> next()
        {
          String fileName = files[ix++];
          File entry = new File(dir, fileName);
          if (entry.isDirectory()) {
            try {
              return Pair.pair(fileName, (CodeTree) new CodeDirectory(entry.getAbsolutePath(), entry, false));
            } catch (RepositoryException e) {
              throw new IllegalStateException(e);
            }
          } else {
            return Pair.pair(fileName, (CodeTree) new ClassFile(entry, fileName));
            // try {
            // FileInputStream str = new FileInputStream(entry);
            // try {
            // byte[] bt = FileUtil.readFileIntoBytes(str);
            // return Pair.pair(fileName, (CodeTree) new CafeCode(fileName, bt));
            // } catch (IOException e) {
            // throw new IllegalStateException(e);
            // } finally {
            // try {
            // str.close();
            // } catch (IOException e) {
            // }
            // }
            // } catch (FileNotFoundException e) {
            // throw new IllegalStateException(e);
            // }
          }
        }

        @Override
        public void remove()
        {
          throw new UnsupportedOperationException("not permitted");
        }

      };

    } else
      return new NullIterator<Entry<String, CodeTree>>();
  }

  @Override
  public void addCodeEntry(String path, CodeTree code) throws RepositoryException
  {
    String segments[] = pathSegments(path);
    File dir = this.dir;

    for (int ix = 0; ix < segments.length - 1; ix++) {
      File child = new File(dir, segments[ix]);
      if (child.isDirectory())
        dir = child;
      else if (!child.exists()) {
        if (!child.mkdir())
          throw new RepositoryException("cannot create directory: " + child);
        dir = child;
      }
    }
    File child = nodeFile(dir, segments[segments.length - 1], code.getExtension());
    try {
      code.write(child);
    } catch (IOException e) {
      throw new RepositoryException("cannot write " + path, e);
    }
    cache.put(child, code);
  }

  private static File nodeFile(File dir, String name, String extension)
  {
    if (extension != null)
      return new File(dir, name + "." + extension);
    else
      return new File(dir, name);
  }

  @SuppressWarnings("unused")
  private static File createNodeFile(File dir, String path, String extension)
  {
    int from = 0;
    int pos = path.indexOf('/');
    StringBuilder b = new StringBuilder();
    String sep = "";

    while (pos >= 0) {
      b.append(sep);
      sep = fileSeparator;
      b.append(safeFileName(path.substring(from, pos)));
      from = pos + 1;
      pos = path.indexOf('/', from);
    }
    String name = path.substring(from);

    dir = new File(dir, b.toString());
    dir.mkdirs();

    if (extension != null)
      return new File(dir, name + "." + extension);
    else
      return new File(dir, name);
  }

  private static File findNodeFile(File dir, String path, String extension)
  {
    int from = 0;
    int pos = path.indexOf('/');
    StringBuilder b = new StringBuilder();
    String sep = "";

    while (pos >= 0) {
      b.append(sep);
      sep = fileSeparator;
      b.append(safeFileName(path.substring(from, pos)));
      from = pos + 1;
      pos = path.indexOf('/', from);
    }
    String name = safeFileName(path.substring(from));

    dir = new File(dir, b.toString());

    if (extension != null)
      return new File(dir, name + "." + extension);
    else
      return new File(dir, name);
  }

  private static File findNodeFile(File dir, String path)
  {
    int from = 0;
    int pos = path.indexOf('/');
    StringBuilder b = new StringBuilder();
    String sep = "";

    while (pos >= 0) {
      b.append(sep);
      sep = fileSeparator;
      b.append(safeFileName(path.substring(from, pos)));
      from = pos + 1;
      pos = path.indexOf('/', from);
    }
    String name = safeFileName(path.substring(from));

    return new File(b.toString(), name);
  }

  @SuppressWarnings("unused")
  private static File locateNodeFile(File dir, String path, String extension)
  {
    String segments[] = pathSegments(path);

    for (int ix = 0; ix < segments.length - 1; ix++) {
      File child = new File(dir, segments[ix]);
      if (child.isDirectory())
        dir = child;
      else
        return null;
    }
    if (extension != null)
      return new File(dir, segments[segments.length - 1] + "." + extension);
    else
      return new File(dir, segments[segments.length - 1]);
  }

  @Override
  public CodeTree resolve(String path, String extension) throws RepositoryException
  {
    File child = findNodeFile(this.dir, path, extension);

    if (cache.containsKey(child))
      return cache.get(child);
    else if (child.isDirectory()) {
      CodeDirectory codeDir = new CodeDirectory(child);
      cache.put(child, codeDir);
      return codeDir;
    } else if (child.canRead()) {
      CodeParser parser = parsers.get(extension);
      if (parser == null)
        throw new RepositoryException("cannot find parser for extension: " + extension);
      else {
        ErrorReport errors = new ErrorReport();
        try {
          CodeTree code = parser.parse(URIUtils.createFileURI(child), errors);
          cache.put(child, code);
          return code;
        } catch (ResourceException e) {
          throw new RepositoryException(e);
        } finally {
          if (!errors.isErrorFree())
            throw new RepositoryException("problem in parsing " + child + ": " + errors);
        }
      }
    } else
      return null;
  }

  @Override
  public CodeCatalog subCatalog(String path) throws RepositoryException
  {
    File child = findNodeFile(this.dir, path);

    if (cache.containsKey(child))
      return (CodeCatalog) cache.get(child);
    else if (child.isDirectory()) {
      CodeDirectory codeDir = new CodeDirectory(child);
      cache.put(child, codeDir);
      return codeDir;
    } else
      return null;
  }

  public static CodeTree parse(ResourceURI uri, String extension) throws RepositoryException, ResourceException
  {
    CodeParser parser = parsers.get(extension);
    if (parser == null)
      throw new RepositoryException("cannot find parser for extension: " + extension);
    ErrorReport errors = new ErrorReport();
    try {
      return parser.parse(uri, errors);
    } finally {
      if (!errors.isErrorFree())
        throw new RepositoryException("cannot parse " + uri);
    }
  }

  public static CodeTree parse(ResourceURI uri, InputStream input, String extension) throws RepositoryException,
      ResourceException
  {
    CodeParser parser = parsers.get(extension);
    if (parser == null)
      throw new RepositoryException("cannot find parser for extension: " + extension);
    ErrorReport errors = new ErrorReport();
    try {
      return parser.parse(uri, input, errors);
    } finally {
      if (!errors.isErrorFree())
        throw new RepositoryException("cannot parse " + uri);
    }
  }

  @Override
  public CodeTree fork(String path) throws RepositoryException
  {
    String segments[] = pathSegments(path);
    File dir = this.dir;

    for (int ix = 0; ix < segments.length - 1; ix++) {
      File child = new File(dir, segments[ix]);
      if (child.isDirectory())
        dir = child;
      else if (child.exists())
        throw new RepositoryException("could not add " + path + " to catalog because it is not a catalog");
      else {
        child.mkdir();
        dir = child;
      }
    }

    String last = segments[segments.length - 1];
    File child = new File(dir, last);
    if (child.isDirectory())
      return new CodeDirectory(last, child, false);
    else if (child.exists())
      throw new RepositoryException("could not add " + path + " to catalog because it is not a catalog");
    else {
      child.mkdir();
      return new CodeDirectory(last, child, false);
    }
  }

  @Override
  public boolean isPresent(String path)
  {
    File tgt = findNodeFile(dir, path);

    return tgt.exists() && !tgt.isDirectory();
  }

  @Override
  public boolean isPresent(String path, String extension)
  {
    File tgt = findNodeFile(dir, path, extension);

    return tgt.exists() && !tgt.isDirectory();
  }

  @Override
  public boolean isReadOnly()
  {
    return !dir.canWrite();
  }

  @Override
  public void mergeEntries(CodeCatalog other) throws RepositoryException
  {
    for (Entry<String, CodeTree> entry : other) {
      String extension = entry.getValue().getExtension();
      String childName = entry.getKey();

      CodeTree child = resolve(childName, extension);
      CodeTree otherChild = other.resolve(childName, extension);

      if (otherChild instanceof CodeCatalog) {
        if (child instanceof CodeCatalog)
          ((CodeCatalog) child).mergeEntries((CodeCatalog) otherChild);
        else if (child != null)
          throw new RepositoryException("tried to merge a code catalog into a non-catalog entry");
        else {
          String childFile = safeFileName(childName);
          File childDir = new File(dir, childFile.replace("/", fileSeparator));
          assert !childDir.exists();
          childDir.mkdir();
          CodeDirectory childD = new CodeDirectory(childFile, childDir, overwrite);
          childD.mergeEntries((CodeCatalog) otherChild);
        }
      } else
        addCodeEntry(childName, otherChild);
    }
  }

  private static String[] pathSegments(String path)
  {
    String bits[] = StringUtils.split(path, '/');
    int lastIx = bits.length;
    for (int ix = 0; ix < lastIx; ix++)
      bits[ix] = safeFileName(bits[ix]);

    return bits;
  }

  /**
   * Introduce a hash into the file name so that upper/lower case file systems dont get confused and
   * we truncate the file name to avoid over-long file names.
   * 
   * @param fragment
   * @return
   */

  private static String safeFileName(String name)
  {
    int hashCode = name.hashCode();
    int bound = Math.min(name.length(), MAX_FILENAME);
    if (hashCode > 0)
      return name.substring(0, bound) + hashCode;
    else if (hashCode == 0)
      return name.substring(0, bound);
    else
      return name.substring(0, bound) + "_" + Math.abs(hashCode);
  }

  @Override
  public void write(File out) throws IOException
  {
    PrettyPrintDisplay.write(out, this);
  }

  public static void registerParser(String extension, CodeParser parser)
  {
    parsers.put(extension, parser);
  }
}
