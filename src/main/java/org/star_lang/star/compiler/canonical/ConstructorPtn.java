package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("serial")
public class ConstructorPtn extends ContentPattern {
  /**
   * The Tuple content expression matches a labeled or unlabeled positional term
   */
  private final String label;
  private final List<IContentPattern> elements;

  public ConstructorPtn(Location loc, String label, IType type, List<IContentPattern> arguments) {
    super(loc, type);
    this.label = label;
    this.elements = arguments;
  }

  public ConstructorPtn(Location loc, String label, IType type, IContentPattern... args) {
    this(loc, label, type, makeArgs(args));
  }

  public String getLabel() {
    return label;
  }

  public IContentExpression getFun() {
    return new Variable(getLoc(), StandardTypes.voidType, label);
  }

  public List<IContentPattern> getElements() {
    return elements;
  }

  public IContentPattern getArg(int ix) {
    return elements.get(ix);
  }

  public int arity() {
    return elements.size();
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp) {
    disp.appendId(label);
    disp.append("(");
    disp.prettyPrint(elements, ", ");
    disp.append(")");
  }

  @Override
  public void accept(CanonicalVisitor visitor) {
    visitor.visitConstructorPtn(this);
  }

  @Override
  public <A, E, P, C, D, T> P transformPattern(TransformPattern<A, E, P, C, D, T> transform, T context) {
    return transform.transformConstructorPtn(this, context);
  }

  private static List<IContentPattern> makeArgs(IContentPattern args[]) {
    List<IContentPattern> list = new ArrayList<>();
    Collections.addAll(list, args);
    return list;
  }
}
