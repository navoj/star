package org.star_lang.star.compiler.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

/**
 * Handy file utility class for reading/writing character files. Related to this is a listener
 * interface that will be called for each line of the file.
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
public class FileUtil
{

  public static File writeFile(File theFile, String content) throws IOException
  {
    try (Writer out = new FileWriter(theFile)) {
      out.append(content);
    }
    return theFile;
  }

  public static void writeContent(OutputStream out, String data) throws IOException
  {
    try (Writer wrOut = new OutputStreamWriter(out)) {
      wrOut.append(data);
    }
  }

  public static void copyFile(File src, File dest) throws IOException
  {
    try (InputStream in = new FileInputStream(src)) {
      try (OutputStream out = new FileOutputStream(dest)) {
        byte[] buffer = new byte[16384];

        do {
          int len = in.read(buffer);
          if (len >= 0)
            out.write(buffer, 0, len);
          else
            return;
        } while (true);
      }
    }
  }

  public static File createTempDir() throws IOException
  {
    File systemTmpDir = new File(System.getProperty("java.io.tmpdir"));

    if (!systemTmpDir.isDirectory())
      throw new IOException("could not access system temp directory");

    File tmpDir = new File(systemTmpDir, GenSym.genSym("star"));

    // sometimes the temporary directory is not released. Make sure it is empty.
    if (tmpDir.isDirectory())
      FileUtil.rmRf(tmpDir);
    if (!tmpDir.isDirectory() && !tmpDir.mkdir())
      throw new IOException("could not create temp directory");
    tmpDir.deleteOnExit();

    return tmpDir;
  }

  public static void rmRf(File fl)
  {
    if (fl.isDirectory()) {
      String entries[] = fl.list();
      for (String entry : entries)
        rmRf(new File(fl, entry));
      fl.delete();
    } else if (fl.exists())
      fl.delete();
  }

  public static byte[] readFileIntoBytes(InputStream rdr) throws IOException
  {
    int available = rdr.available();
    ByteBuilder buff = new ByteBuilder();

    byte[] chBuff = new byte[available];
    for (int len = rdr.read(chBuff); len > 0; len = rdr.read(chBuff))
      buff.append(chBuff, 0, len);

    return buff.toBytes();
  }

  public static String readFileIntoString(File fl) throws IOException
  {
    StringBuilder buff = new StringBuilder();
    try (Reader rdr = new FileReader(fl)) {
      char[] chBuff = new char[16384];
      for (int len = rdr.read(chBuff); len > 0; len = rdr.read(chBuff))
        buff.append(chBuff, 0, len);
    }

    return buff.toString();
  }

  public static String readFileIntoString(Reader rdr) throws IOException
  {
    StringBuilder buff = new StringBuilder();

    char[] chBuff = new char[16384];
    if (rdr != null)
      for (int len = rdr.read(chBuff); len > 0; len = rdr.read(chBuff))
        buff.append(chBuff, 0, len);

    return buff.toString();
  }
}
