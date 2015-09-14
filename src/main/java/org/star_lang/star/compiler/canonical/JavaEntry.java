package org.star_lang.star.compiler.canonical;

import java.util.Collection;

import org.star_lang.star.compiler.sources.JavaInfo;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.type.Visibility;
import org.star_lang.star.compiler.util.FixedList;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.Location;

@SuppressWarnings("serial")
public class JavaEntry extends EnvironmentEntry
{
  private final String className;
  private final JavaInfo pkg;

  public JavaEntry(String className, Location loc, JavaInfo pkg, Visibility visibility)
  {
    super(loc, visibility);
    this.className = className;
    this.pkg = pkg;
  }

  public String getClassName()
  {
    return className;
  }

  public JavaInfo getJava()
  {
    return pkg;
  }

  @Override
  public boolean defines(String name)
  {
    return false;
  }

  @Override
  public Collection<String> definedFields()
  {
    return FixedList.create();
  }

  @Override
  public Collection<String> definedTypes()
  {
    return FixedList.create();
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitJavaEntry(this);
  }

  @Override
  public <A, E, P, C, D, T> D transform(TransformStatement<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformJavaEntry(this, context);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.appendWord(StandardNames.JAVA);
    disp.appendWord(className);
  }
}