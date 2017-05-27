package org.star_lang.star.engine;

public abstract class Instruction {
  public abstract void execute(Machine m);

  public static class LoadEnv extends Instruction {
    private final int off;

    public LoadEnv(int off) {
      this.off = off;
    }

    @Override
    public void execute(Machine m) {
      m.pushStack(m.currFrame().getClosure().getBound()[off]);
    }
  }

  public static class LoadLocal extends Instruction {
    private final int off;

    public LoadLocal(int off) {
      this.off = off;
    }

    @Override
    public void execute(Machine m) {
      m.pushStack(m.currFrame().getLocals()[off]);
    }
  }
}
