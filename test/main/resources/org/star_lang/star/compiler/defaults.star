defaults is package{

  type person is someone { 
    name has type string;
    -- name default is "someone belonging to $spouse";
    spouse has type person;
    spouse default is noone;
    
    gender has type gender;
    gender default is male;
    
    age has type option of float;
    age default is none;
  } or noone;
  
  type gender is male or female;
  
  def JJ is someone{name="fred"};
  
  main has type action();
  prc main() do {
    logMsg(info,"$JJ");
    
    assert JJ.name="fred";
    assert JJ.age=none;
  }
}
  