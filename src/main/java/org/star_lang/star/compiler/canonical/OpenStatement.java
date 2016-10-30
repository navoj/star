package org.star_lang.star.compiler.canonical;

import java.util.Collection;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.type.Visibility;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.TypeInterfaceType;

@SuppressWarnings("serial")
public class OpenStatement implements IStatement
{
  private final IContentExpression record;
  private final TypeInterfaceType face;
  private final Location loc;
  private final Visibility visibility;

  public OpenStatement(Location loc, IContentExpression record, TypeInterfaceType face, Visibility visibility)
  {
    this.loc = loc;
    this.visibility = visibility;
    this.record = record;
    this.face = face;
  }

  @Override
  public Location getLoc()
  {
    return loc;
  }

  public IContentExpression getRecord()
  {
    return record;
  }

  public TypeInterfaceType getFace()
  {
    return face;
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitOpenStatement(this);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    visibility.display(disp);
    disp.appendWord(StandardNames.OPEN);
    record.prettyPrint(disp);
  }

  @Override
  public boolean defines(String name)
  {
    return face.getAllFields().containsKey(name) || face.getAllTypes().containsKey(name);
  }

  @Override
  public Collection<String> definedFields()
  {
    return face.getAllFields().keySet();
  }

  @Override
  public Collection<String> definedTypes()
  {
    return face.getAllTypes().keySet();
  }

  @Override
  public Visibility getVisibility()
  {
    return visibility;
  }

  @Override
  public <A, E, P, C, D, T> D transform(TransformStatement<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformOpenStatement(this, context);
  }

}
