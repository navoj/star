package org.star_lang.star.operators.ast.runtime;

import java.math.BigDecimal;
import java.util.Stack;

import org.star_lang.star.compiler.ast.ASyntax;
import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.operators.CafeEnter;

import com.starview.platform.data.EvaluationException;
import com.starview.platform.data.IConstructor;
import com.starview.platform.data.IFunction;
import com.starview.platform.data.IList;
import com.starview.platform.data.IMap;
import com.starview.platform.data.IPattern;
import com.starview.platform.data.IRecord;
import com.starview.platform.data.IRelation;
import com.starview.platform.data.IScalar;
import com.starview.platform.data.IValue;
import com.starview.platform.data.IValueVisitor;
import com.starview.platform.data.type.IType;
import com.starview.platform.data.type.Location;
import com.starview.platform.data.type.StandardTypes;
import com.starview.platform.data.type.TypeVar;
import com.starview.platform.data.type.UniversalType;

public class AstQuoter implements IFunction
{

  public static final String name = "__quote";

  @CafeEnter
  public ASyntax quote(IValue term) throws EvaluationException
  {
    Quoter quoter = new Quoter();
    return quoter.quote(term);
  }

  @Override
  public IValue enter(IValue... args) throws EvaluationException
  {
    return quote(args[0]);
  }

  @Override
  public IType getType()
  {
    return type();
  }

  public static IType type()
  {
    TypeVar tv = new TypeVar();
    return UniversalType.universal(new TypeVar[] { tv }, TypeUtils.functionType(tv, ASyntax.type));
  }

  private static class Quoter implements IValueVisitor
  {
    private Stack<ASyntax> stack = new Stack<>();

    ASyntax quote(IValue term)
    {
      stack.clear();
      term.accept(this);
      assert stack.size() == 1;
      return stack.pop();
    }

    @Override
    public void visitScalar(IScalar<?> scalar)
    {
      stack.push(abstractValue(Location.noWhereEnum, scalar, scalar.getType()));
    }
    
    public static ASyntax abstractValue(Location loc, Object val, IType type)
    {
      if (type.equals(StandardTypes.charType))
        return Abstract.newChar(loc, (Integer) val);
      else if (val instanceof Integer)
        return Abstract.newInteger(loc, ((Integer) val));
      else if (val instanceof Double)
        return Abstract.newFloat(loc, ((Double) val));
      else if (val instanceof Character)
        return Abstract.newChar(loc, ((Character) val));
      else if (val instanceof String)
        return Abstract.newString(loc, ((String) val));
      else if (val instanceof Long)
        return Abstract.newLong(loc, (Long) val);
      else if (val instanceof Boolean)
        return Abstract.newBoolean(loc, (Boolean) val);
      else if (val instanceof BigDecimal)
        return Abstract.newBigdecimal(loc, (BigDecimal) val);
      else if (val instanceof IScalar<?>)
        return abstractValue(loc, ((IScalar<?>) val).getValue(), type);
      else
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void visitRecord(IRecord agg)
    {
      // TODO Auto-generated method stub

    }

    @Override
    public void visitList(IList list)
    {
      // TODO Auto-generated method stub

    }

    @Override
    public void visitRelation(IRelation relation)
    {
      // TODO Auto-generated method stub

    }

    @Override
    public void visitFunction(IFunction fn)
    {
      throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void visitPattern(IPattern ptn)
    {
      throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void visitConstructor(IConstructor con)
    {
      // TODO Auto-generated method stub

    }

    @Override
    public void visitMap(IMap map)
    {
      // TODO Auto-generated method stub

    }

  }
}
