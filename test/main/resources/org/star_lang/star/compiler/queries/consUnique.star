consUnique is package {
  def R is list of [{ F="U"}]

  constCons has type ((%a) => cons of string);
  fun constCons(l) is cons of ["a"];

  def foo is
    let {
      def routeOperationNames is list of { unique x.F where x in R order by x.F};
    } in 
      constCons(routeOperationNames);
  
  prc main() do {
    logMsg(info,"$foo");
  }
}