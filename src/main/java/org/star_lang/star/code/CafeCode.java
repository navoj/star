package org.star_lang.star.code;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.star_lang.star.code.repository.CodeTree;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;



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
public class CafeCode implements CodeTree, HasCode {
  private final byte[] code;
  private final String path;
  public static final String EXTENSION = "class";

  public CafeCode(String path, byte[] code) {
    this.code = code;
    this.path = path;
  }

  @Override
  public String getPath() {
    return path;
  }

  @Override
  public byte[] getCode() {
    return code;
  }

  @Override
  public int hashCode() {
    return path.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof CafeCode) {
      CafeCode other = (CafeCode) obj;
      return path.equals(other.path);
    } else
      return false;
  }

  @Override
  public void write(File file) throws IOException {
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
  public void prettyPrint(PrettyPrintDisplay disp) {
    disp.append(path);
    disp.append("[");
    disp.append(code.length);
    disp.append(" bytes");
    disp.append("]");
  }

  @Override
  public String toString() {
    return PrettyPrintDisplay.toString(this);
  }

  @Override
  public String getExtension() {
    return CafeCode.EXTENSION;
  }
}
