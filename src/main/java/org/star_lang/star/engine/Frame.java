package org.star_lang.star.engine;

import org.star_lang.star.data.IValue;

public class Frame {
  private final IValue locals[];
  private final IValue args[];
  private final Closure closure;
  private final int offset;

  public Frame(IValue args[], int count, Closure closure, int offset) {
    this.args = args;
    this.locals = new IValue[count];
    this.closure = closure;
    this.offset = offset;
  }

  public IValue[] getLocals() {
    return locals;
  }

  public IValue[] getArgs() {
    return args;
  }

  public Closure getClosure() {
    return closure;
  }

  public int getOffset() {
    return offset;
  }
}
