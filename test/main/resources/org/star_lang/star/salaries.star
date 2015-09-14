salaries is package{

  type employeeType is alias of relation of{
    name has type string;
    role has type string;
    salary has type integer;
  };
  
  employees has type employeeType;
  var employees := indexed{
    {name="alpha"; role="manager"; salary=10000};
    {name="beta"; role="manager"; salary=9000};
    {name="zeta"; role="worker"; salary=1000};
    {name="iota"; role="worker";salary=20000}
  }
  
  managerOf has type relation of {name has type string; member has type string};
  var managerOf := indexed{
    {name="alpha"; member="beta"};
    {name="beta";member="zeta"};
    {name="beta";member="iota"}
  };
  
  alphaDog has type relation of ((string));
  M in alphaDog if {name=M;role="manager";salary=MS} in employees and 
      not( {name=M;member=E} in managerOf and {name=E;salary=ES} in employees and ES>MS);
  
  main() do {
    logMsg(info,"is alpha an alphaDog? $(\"alpha\" in alphaDog)");
    logMsg(info,"is beta an alphaDog? $(\"beta\" in alphaDog)");
    
    logMsg(info,"all alphaDogs: $(all A where A in alphaDog)");
  }
  
}