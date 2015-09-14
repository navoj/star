caseTask is package{
  import task;
  
  fun recf(n) is task {
    def next is (k) => task { valis k/2 };
    switch n in {
      case 0 do valis ();
      case _ default do valis valof recf(valof next(n));
    }
  };
  
  prc main() do {
    perform recf(1) on abort { case _ do logMsg(info,"something wrong"); }
  }
}