basicPerform is package{
  import task;

  var flag := false;
  def t1 is task {
    flag := true;
    valis ();
  }

  prc main() do {
   perform t1; -- on abort { _ do logMsg(info, "XXX"); };
   assert(flag);
  }
}