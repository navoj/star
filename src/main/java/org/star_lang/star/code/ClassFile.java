package org.star_lang.star.code;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.star_lang.star.code.repository.CodeTree;
import org.star_lang.star.compiler.util.FileUtil;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;

@SuppressWarnings("serial")
public class ClassFile implements CodeTree, HasCode
{
  private final File file;
  private final String path;

  public ClassFile(File file, String path)
  {
    this.file = file;
    this.path = path;
  }

  @Override
  public void write(File output) throws IOException
  {
    FileUtil.copyFile(file, output);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append("code file ").append(file.toString());
  }

  @Override
  public String getPath()
  {
    return path;
  }

  @Override
  public String getExtension()
  {
    return CafeCode.EXTENSION;
  }

  @Override
  public byte[] getCode()
  {
    try (FileInputStream rdr = new FileInputStream(file)) {
      return FileUtil.readFileIntoBytes(rdr);
    } catch (IOException e) {
      return null;
    }
  }
}
