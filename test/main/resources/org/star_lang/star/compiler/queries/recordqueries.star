recordqueries is package{

  type gender is male or female;
  
  type foo is foo{
    name has type string;
    gender has type gender;
    add has type address;
  }
  
  type address is street{
    street has type string;
    unit has type integer;
    city has type string;
  };
  
  persons has type ref list of foo;
  
  var persons := list of [ foo{name="alpha"; gender=male; add=street{ street="main st"; unit=1; city="my city"}},
                           foo{name="beta"; gender=female; add=street{street="union st"; unit=100; city="ny"}},
                           foo{name="gamma"; gender=male; add=street{street="water st"; unit=110; city="ny"}}];
                                   
  prc main() do {
    assert foo{name="alpha"} in persons;
    
    logMsg(info,"$(all P where P in persons and P.name>="alpha" order by P.add.unit)");
  }
}
                              
                                   