package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.operator.StandardNames;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;

@SuppressWarnings("serial")
public class Application extends BaseExpression
{
  private final IContentExpression function;
  private final IContentExpression args;

  public Application(Location loc, IType type, IContentExpression function, IContentExpression args)
  {
    super(loc, type);
    this.function = function;
    this.args = args;
    assert function != null && args != null && type != null;
  }

  public Application(Location loc, IType type, IContentExpression function, IContentExpression... args)
  {
    this(loc, type, function, new TupleTerm(loc, args));
  }

  public static IContentExpression apply(Location loc, IType type, IContentExpression function,
      IContentExpression... args)
  {
    return new Application(loc, type, function, new TupleTerm(loc, args));
  }

  public IContentExpression getFunction()
  {
    return function;
  }

  public IContentExpression getArgs()
  {
    return args;
  }

  public IContentExpression getArg(int ix)
  {
    assert args instanceof TupleTerm;
    return ((TupleTerm) args).getArg(ix);
  }

  public int arity()
  {
    return TypeUtils.typeArity(getArgs().getType());
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    function.prettyPrint(disp);
    if (!(args instanceof ConstructorTerm || args instanceof RecordTerm))
      disp.append(StandardNames.APPLY);
    args.prettyPrint(disp);
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitApplication(this);
  }

  @Override
  public <A, E, P, C, D, T> E transform(TransformExpression<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformApplication(this, context);
  }
}
