package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;

@SuppressWarnings("serial")
public class FunctionLiteral extends ProgramLiteral implements IRule<IContentExpression>
{
  private final IContentPattern args[];
  private final IContentExpression result;

  public FunctionLiteral(Location loc, String name, IType type, IContentPattern args[], IContentExpression result,
      Variable[] freeVars)
  {
    super(loc, type, name, freeVars);

    this.args = args;
    this.result = result;
  }

  @Override
  public IContentPattern[] getArgs()
  {
    return args;
  }

  @Override
  public IContentExpression getBody()
  {
    return result;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.appendWord(StandardNames.FUN);
    disp.appendId(name);
    disp.append("(");
    String sep = "";
    for (IContentPattern arg : args) {
      disp.append(sep);
      sep = ", ";
      arg.prettyPrint(disp);
    }
    disp.append(")");
    disp.appendWord(StandardNames.IS);
    result.prettyPrint(disp);
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitFunctionLiteral(this);
  }

  @Override
  public <A, E, P, C, D, T> E transform(TransformExpression<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformFunctionLiteral(this, context);
  }
}
