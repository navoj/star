person is package{
  type person is someone { 
    name has type string;
    
    gender has type gender;
    gender default is male;
    
    dob has type date;
    dob default is never;
  } or noone;
  
  type gender is male or female;
  
  implementation comparable over person is {
    (<) = person_less;
    (=<) = person_le;
    (>) = person_gt;
    (>=) = person_ge;
  } using {
    person_less(noone,someone{}) is true;
    person_less(someone{name=N1},someone{name=N2}) is N1<N2;
    person_less(_,_) default is false;
    
    person_le(X,X) is true;
    person_le(X,Y) default is person_less(X,Y);
    
    person_gt(X,Y) is person_less(Y,X);
    person_ge(X,Y) is person_le(Y,X);
  }
  
  implementation equality over person is {
    (=) = person_eq;
  } using {
    person_eq(someone{name=N1},someone{name=N2}) is N1=N2;
    person_eq(noone,noone) is true;
    person_eq(_,_) default is false;
  }
}