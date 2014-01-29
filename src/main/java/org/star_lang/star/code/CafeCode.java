package org.star_lang.star.code;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.star_lang.star.code.repository.CodeTree;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;

/**
 * CafeCode 'holds' the compiled code of a Cafe program. Since this is just JVM code, it could
 * easily be extended to allow for any Java compiled code.
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
public class CafeCode implements CodeTree, HasCode
{
  private final byte[] code;
  private final String path;
  public static final String EXTENSION = "class";

  public CafeCode(String path, byte[] code)
  {
    this.code = code;
    this.path = path;
  }

  @Override
  public String getPath()
  {
    return path;
  }

  @Override
  public byte[] getCode()
  {
    return code;
  }

  @Override
  public int hashCode()
  {
    return path.hashCode();
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof CafeCode) {
      CafeCode other = (CafeCode) obj;
      return path.equals(other.path);
    } else
      return false;
  }

  @Override
  public void write(File file) throws IOException
  {
    try {
      try (OutputStream output = new FileOutputStream(file)) {
        output.write(code);
      } catch (IOException e) {
        throw new IOException("error in writing entry: " + file.getName(), e);
      }
    } catch (FileNotFoundException e) {
      throw new IOException("error in writing entry: " + file.getName(), e);
    }
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append(path);
    disp.append("[");
    disp.append(code.length);
    disp.append(" bytes");
    disp.append("]");
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

  @Override
  public String getExtension()
  {
    return CafeCode.EXTENSION;
  }
}
