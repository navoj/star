package org.star_lang.star.compiler.canonical;

import java.util.List;

import org.star_lang.star.compiler.operator.Operators;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.FixedList;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;

@SuppressWarnings("serial")
public class ConstructorTerm extends BaseExpression {
  /**
   * The Tuple content expression constructs a labeled or unlabeled positional term
   */

  private final List<IContentExpression> elements;
  private final String label;

  public ConstructorTerm(Location loc, String label, IType type, List<IContentExpression> arguments) {
    super(loc, type);
    this.elements = arguments;
    this.label = label;
  }

  public ConstructorTerm(Location loc, String label, IType type, IContentExpression... args) {
    this(loc, label, type, FixedList.create(args));
  }

  public String getLabel() {
    return label;
  }

  public List<IContentExpression> getElements() {
    return elements;
  }

  public int arity() {
    return elements.size();
  }

  public IContentExpression getArg(int ix) {
    return elements.get(ix);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp) {
    if (label == null || label.isEmpty() || TypeUtils.isTupleLabel(label)) {
      disp.append("(");
      disp.prettyPrint(elements, ", ");
      disp.append(")");
    } else if (arity() == 2 && Operators.isRootInfixOperator(label) != null) {
      elements.get(0).prettyPrint(disp);
      disp.appendId(label);
      elements.get(1).prettyPrint(disp);
    } else if (arity() == 1 && Operators.isRootPrefixOperator(label) != null) {
      disp.appendId(label);
      elements.get(0).prettyPrint(disp);
    } else if (arity() == 1 && Operators.isRootPostfixOperator(label) != null) {
      elements.get(0).prettyPrint(disp);
      disp.appendId(label);
    } else {
      disp.appendId(label);
      if (arity() > 0) {
        disp.append("(");
        disp.prettyPrint(elements, ", ");
        disp.append(")");
      }
    }
  }

  @Override
  public void accept(CanonicalVisitor visitor) {
    visitor.visitConstructor(this);
  }

  @Override
  public <A, E, P, C, D, T> E transform(TransformExpression<A, E, P, C, D, T> transform, T context) {
    return transform.transformConstructor(this, context);
  }
}
