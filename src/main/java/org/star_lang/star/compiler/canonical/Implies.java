package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.operator.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.Location;

@SuppressWarnings("serial")
public class Implies extends Condition
{
  private final ICondition generate, test;

  public Implies(Location loc, ICondition test, ICondition generate)
  {
    super(loc);
    this.generate = generate;
    this.test = test;
  }

  public ICondition getGenerate()
  {
    return generate;
  }

  public ICondition getTest()
  {
    return test;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    generate.prettyPrint(disp);
    disp.appendWord(StandardNames.IMPLIES);
    test.prettyPrint(disp);
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitImplies(this);
  }

  @Override
  public <A, E, P, C, D, T> C transform(TransformCondition<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformImplies(this, context);
  }
}
