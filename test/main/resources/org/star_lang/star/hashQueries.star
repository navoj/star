hashQueries is package{

  var table := dictionary of [ "A"->1, "B"->2, "C"->3, "D"->4 ];
  
  main() do {
    logMsg(info,"$table");
    
    for K->V in table do
      logMsg(info,"K=$K, V=$V");
  }
}