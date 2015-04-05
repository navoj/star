package org.star_lang.star.data.type;

import org.star_lang.star.compiler.type.DisplayType;
import org.star_lang.star.compiler.type.TypeUtils;

@SuppressWarnings("serial")
public class TupleType implements IType
{
  private final IType[] elTypes;

  public TupleType(IType[] elTypes)
  {
    this.elTypes = elTypes;
  }

  public int arity()
  {
    return elTypes.length;
  }

  public IType nth(int ix)
  {
    return elTypes[ix];
  }

  public IType[] getElTypes()
  {
    return elTypes;
  }

  @Override
  public <T, C, X> T transform(TypeTransformer<T, C, X> former, X cxt)
  {
    return former.transformTupleType(this, cxt);
  }

  @Override
  public String typeLabel()
  {
    return TypeUtils.tupleLabel(arity());
  }

  @Override
  public Kind kind()
  {
    return Kind.type;
  }

  @Override
  public <C> void accept(ITypeVisitor<C> visitor, C cxt)
  {
    visitor.visitTupleType(this, cxt);
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o) // Shortcut eval
      return true;

    if (o instanceof IType) {
      IType itype = TypeUtils.deRef((IType) o);
      if (itype instanceof TupleType) {
        TupleType other = (TupleType) itype;
        if (other.arity() == arity()) {
          for (int ix = 0; ix < elTypes.length; ix++) {
            if (!elTypes[ix].equals(other.elTypes[ix]))
              return false;
          }

          return true;
        }
      }
      return false;
    }
    return false;
  }

  @Override
  public int hashCode()
  {
    int result = 0;
    for (int i = 0; i < elTypes.length; i++) {
      result *= 31;
      result += elTypes[i].hashCode();
    }
    return result;
  }

  @Override
  public String toString()
  {
    return DisplayType.toString(this);
  }
}
