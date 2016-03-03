typeDecl is package{
  -- aa has type (() => task of integer) => task of integer
  fun aa(f) is task {
    try {
      valis valof f();
    } on abort {
      case E do { logMsg(info, "recovering from $E"); valis 1 };
    }
  }
  prc main() do {
    def F is () => task { logMsg(info,"do F"); raise "F malfunction"; };
    assert valof aa(F) = 1;
  }
};
