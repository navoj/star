dotacc is package{
  import person;
  
  var people := list of [
    someone{ name="fred" },
    someone{ name="peter"},
    someone{ name="jane" }
  ];
  
  def A is actor{
    def Ap is people;
  };
  
  prc main() do {
    def F is list of { all P where P in people and P.name="fred" };
    logMsg(info,"F=$F");
    
    def G is query A with list of { all P where P in people and P.name="fred" };
    logMsg(info,"G=$G");
  }
} 
 