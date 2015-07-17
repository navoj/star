package org.star_lang.star.operators.streamio.runtime;

import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IScalar;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.IValueVisitor;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Type;
import org.star_lang.star.data.type.TypeDescription;
import org.star_lang.star.operators.Intrinsics;

import java.io.InputStream;

/**
 *
 * Created by fgm on 7/13/15.
 */
public class InChnl implements IScalar<InChnl>, PrettyPrintable {
  public static final String typeLabel = "__inputStream";
  public static final IType type = new Type(typeLabel);


  protected final InputStream stream;

  protected InChnl(InputStream stream) {
    this.stream = stream;
  }

  @Override
  public IType getType() {
    return type;
  }

  @Override
  public IValue copy() throws EvaluationException {
    throw new EvaluationException("not permitted");
  }

  @Override
  public IValue shallowCopy() throws EvaluationException {
    throw new EvaluationException("not permitted");
  }

  @Override
  public void accept(IValueVisitor visitor) {
    visitor.visitScalar(this);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp) {
    disp.append(typeLabel);
    disp.append(":");
    disp.append(stream.toString());
  }

  @Override
  public InChnl getValue() {
    return this;
  }

  public static void declare(Intrinsics intrinsics) {
    intrinsics.defineType(new TypeDescription(type));
  }


}
