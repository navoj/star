queryer is package{
  queryer has type actor{
    ageOf has type (string)=>integer;
  } originates {
    nameQ has type relation of {
      name has type string;
      age has type integer;
    }
  }
  
  def queryer is actor{
    fun ageOf(N) is query an yof A where {name=N;age=A} in nameQ;
  };
  
  prc startActors() do
  { 
    logMsg(info,"starting");
    sleep(1000);
    logMsg(info,"asking for the age of fred: $(query queryer with ageOf("fred"))");
  }
}