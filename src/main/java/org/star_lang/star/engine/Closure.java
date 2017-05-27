package org.star_lang.star.engine;

import org.star_lang.star.data.IValue;

public class Closure {
  private final Code code;
  private final IValue bound[];

  public Closure(Code code, IValue bound[]) {
    this.code = code;
    this.bound = bound;
  }

  public Code getCode() {
    return code;
  }

  public IValue[] getBound() {
    return bound;
  }
}
