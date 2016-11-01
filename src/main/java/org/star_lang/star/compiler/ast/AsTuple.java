package org.star_lang.star.compiler.ast;

import java.util.List;

import org.star_lang.star.compiler.util.ListUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IConstructor;
import org.star_lang.star.data.IList;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.value.Array;

@SuppressWarnings("serial")
public class AsTuple extends ASyntax {
  public static final String name = "tupleAst";

  private final StringLiteral brackets;
  private final IList args;

  public AsTuple(Location loc, StringLiteral brackts, IList args) {
    super(loc);
    this.brackets = brackts;
    this.args = args;
  }

  public AsTuple(Location loc, StringLiteral brkts, List<IAbstract> args) {
    this(loc, brkts, new Array(args));
    assert ListUtils.assertNoNulls(args);
  }

  public StringLiteral getBrackets() {
    return brackets;
  }

  public IList getArgs() {
    return args;
  }

  @Override
  public void accept(IAbstractVisitor visitor) {
    visitor.visitTuple(this);
  }

  @Override
  public astType astType() {
    return astType.Tuple;
  }

  @Override
  public int conIx() {
    return tupleIx;
  }

  @Override
  public String getLabel() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int size() {
    return args.size();
  }

  @Override
  public IValue getCell(int index) {
    return args.getCell(index);
  }

  @Override
  public IValue[] getCells() {
    return new IValue[] { getLoc(), args };
  }

  @Override
  public IConstructor shallowCopy() throws EvaluationException {
    return new AsTuple(getLoc(), brackets, args);
  }

  @Override
  public boolean isTernaryOperator(String op) {
    return false;
  }

  @Override
  public boolean isBinaryOperator(String op) {
    return false;
  }

  @Override
  public boolean isUnaryOperator(String op) {
    return false;
  }

  @Override
  public boolean isApply(String op) {
    return false;
  }
}
