package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.data.value.QuoteDisplay;

@SuppressWarnings("serial")
public class Scalar extends BaseExpression {
  private final IValue value;

  public Scalar(Location loc, IType type, IValue value) {
    super(loc, type);
    this.value = value;
  }

  public Scalar(Location loc, IValue value) {
    this(loc, value.getType(), value);
  }

  public Scalar(Location loc, IType type, int value) {
    this(loc, type, Factory.newInt(value));
  }

  public Scalar(Location loc, IType type, long value) {
    this(loc, type, Factory.newLng(value));
  }

  public Scalar(Location loc, IType type, String value) {
    this(loc, type, Factory.newString(value));
  }

  public Scalar(Location loc, IType type, double value) {
    this(loc, type, Factory.newFlt(value));
  }

  public IValue getValue() {
    return value;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp) {
    QuoteDisplay.display(disp, value);
  }

  @Override
  public void accept(CanonicalVisitor visitor) {
    visitor.visitScalar(this);
  }

  @Override
  public <A, E, P, C, D, T> E transform(TransformExpression<A, E, P, C, D, T> transform, T context) {
    return transform.transformScalar(this, context);
  }
}
