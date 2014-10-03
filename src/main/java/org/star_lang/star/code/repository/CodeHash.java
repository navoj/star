package org.star_lang.star.code.repository;

import java.io.File;
import java.io.IOException;

import org.star_lang.star.compiler.util.FileUtil;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;

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
public class CodeHash implements CodeTree
{
  private final String hash;
  public static final String HASH = "source$hash";

  public CodeHash(String hash)
  {
    this.hash = hash;
  }

  public String getHash()
  {
    return hash;
  }

  @Override
  public void write(File output) throws IOException
  {
    FileUtil.writeFile(output, hash);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.appendQuoted(hash);
  }

  @Override
  public String getPath()
  {
    return HASH;
  }

  @Override
  public String getExtension()
  {
    return null;
  }

}
