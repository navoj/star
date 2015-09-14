queryref is package{
  type pp is pp{
    id has type string;
    value has type ref string;
    age has type ref integer;
  };
  
  def LL is list of [pp{id="1"; value:="alpha"; age:=0}, pp{id="2"; value:="beta"; age:=1}, pp{id="3"; value:="gamma"; age:=2}];
  
  prc main() do {
    logMsg(info,"LL=$LL");
    
    def XX is all X.value where X in LL and X.age<2;
    
    logMsg(info,"XX=$XX");
    assert XX = list of ["alpha","beta"]
  }
} 