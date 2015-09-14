typeBug is package {
  import task;
  import cml;

  fun serve(ch) is task {
    valis valof
      (switch await(recv(ch)) in {
        case true is task { valis valof serve(ch); }
        case false is task { valis (); }
      })
  }

  prc main() do {
    def ch is channel();
    def _ is valof backgroundF(serve(ch));
    assert (valof send(ch, true)) = ();
    assert (valof send(ch, false)) = ();
  }

};