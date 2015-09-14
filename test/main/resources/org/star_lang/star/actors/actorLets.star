letdefs is package{

  fun multi() is let{
    thisActor has type ()=>actor of {
      drop has type action()
    };
    def thisActor is memo actor{
      prc drop() do
        logMsg(info,"i am $(thisOne())")
    }
    def thisOne is memo thisActor;
  }
  in thisActor();
    
  prc main() do {
    def M1 is multi();
    def M2 is multi();
    
    request M1 to drop();
    request M2 to drop();
  }
}