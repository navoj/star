mapupdate is package{
  type props is props{
    values has type ref dictionary of (string,string);
  };
  type person is person{
    name has type string;
    atts has type props;
  };
  
  prc main() do
  {
    var Joe := person{name = "Joe"; atts = props{values:=dictionary of ["alpha"->"beta"] }};
    
    var prop := "alpha";
    
   (Joe.atts.values)[prop] := "gamma";
    
    prop := "one";
    (Joe.atts.values)[prop]:="gamma";
    
    logMsg(info,"$Joe");
    
    assert (Joe.atts.values)[prop] has value "gamma"; 
  }
}