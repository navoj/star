routeActor is package {

  type arouteData is arouteData{
    routesMap has type ref dictionary of (string, integer);
  }
    
  def routesActor is actor {
    rD has type ref arouteData;
    private var rD := arouteData{
      routesMap := dictionary of [];
    };
    on V on mapChannel do {
      def key is "";
      def value is rD.routesMap[key] or else 0;
      rD.routesMap[key] := value+1;
    }
  }
}
