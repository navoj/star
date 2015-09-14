personA is package{
  type person is someone { 
    name has type string;
    -- name default is "someone belonging to $spouse";
    spouse has type person;
    spouse default is noone;
    
    gender has type gender;
    gender default is male;
    
    age has type option of float;
    age default is none;
    
    assert age=none or (age has value A and A>0.0);
  } or noone implementing quotable;
  
  type gender is male or female implementing quotable;
  
  implementation comparable over person is {
    (<) = person_less;
    (=<) = person_le;
    (>) = person_gt;
    (>=) = person_ge;
  } using {
    fun person_less(noone,someone{}) is true
     |  person_less(someone{name=N1},someone{name=N2}) is N1<N2
     |  person_less(_,_) default is false;
    
    fun person_le(X,X) is true
     |  person_le(X,Y) default is person_less(X,Y);
    
    fun person_gt(X,Y) is person_less(Y,X)
    fun person_ge(X,Y) is person_le(Y,X);
  }
  
  implementation equality over person is {
    (=) = person_eq;
  } using {
    fun person_eq(someone{name=N1},someone{name=N2}) is N1=N2
     |  person_eq(noone,noone) is true
     |  person_eq(_,_) default is false;
  }
}