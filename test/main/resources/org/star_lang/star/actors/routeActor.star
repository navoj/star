routeActor is package {

  type arouteData is arouteData{
    routesMap has type ref dictionary of (string, integer);
  }
    
  routesActor is actor {
    rD has type ref arouteData;
    private var rD := arouteData{
      routesMap := dictionary of {};
    };
    on V on mapChannel do {
      var key is "";
      var value is rD.routesMap[key] default 0;
      rD.routesMap[key] := value+1;
    }
  }
}
