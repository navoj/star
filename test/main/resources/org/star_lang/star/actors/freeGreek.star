freeGreek is package{
  import actors;
  
  type GreekData is GreekData {
   	s has type string;
   	i has type integer;
  } implementing quotable
  
  
  def handler is actor{
    Check has type action(GreekData);
    prc Check(data) do
      logMsg(info,"I was asked to check $data");
  };
  
  prc main() do {
    def X is 10;
    var input:=GreekData{s="inString";i=1}
    for count in  iota(1,X,1) do {
   	  input:=GreekData{s="inString";i=count};
   	  request handler's Check  to Check(input);
	}
  }
}
    