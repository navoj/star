package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.data.value.QuoteDisplay;

@SuppressWarnings("serial")
public class ScalarPtn extends ContentPattern
{
  private final IValue value;

  public ScalarPtn(Location loc, IType type, IValue value)
  {
    super(loc, type);
    this.value = value;
  }

  public ScalarPtn(Location loc, IType type, int value)
  {
    this(loc, type, Factory.newInt(value));
  }

  public ScalarPtn(Location loc, IType type, String value)
  {
    this(loc, type, Factory.newString(value));
  }

  public IValue getValue()
  {
    return value;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    QuoteDisplay.display(disp, value);
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitScalarPtn(this);
  }

  @Override
  public <A, E, P, C, D, T> P transformPattern(TransformPattern<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformScalarPtn(this, context);
  }

  @Override
  public int hashCode()
  {
    return value.hashCode();
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof ScalarPtn)
      return value.equals(((ScalarPtn) obj).value);
    else
      return false;
  }

  public Scalar asScalar()
  {
    return new Scalar(getLoc(), getType(), getValue());
  }
}
