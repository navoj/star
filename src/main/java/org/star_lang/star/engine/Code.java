package org.star_lang.star.engine;

import org.star_lang.star.data.type.Location;

public class Code {
  private final Location loc;
  private final Instruction ins[];

  public Code(Location loc, Instruction ins[]) {
    this.loc = loc;
    this.ins = ins;
  }

  public Location getLoc() {
    return loc;
  }

  public Instruction[] getIns() {
    return ins;
  }
}
