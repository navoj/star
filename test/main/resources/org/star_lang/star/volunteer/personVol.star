Person is package{
  type Person is someone { 
    name has type string;
    -- name default is "someone belonging to $spouse";
    spouse has type Person;
    spouse default is noone;
    
    gender has type gender;
    gender default is male;
    
    age has type float;
    age default is nonFloat;
  } or noone;
  
  type gender is male or female;
  
  implementation comparable over Person is {
    (<) = Person_less;
    (=<) = Person_le;
    (>) = Person_gt;
    (>=) = Person_ge;
  } using {
    fun Person_less(noone,someone{}) is true
     |  Person_less(someone{name=N1},someone{name=N2}) is N1<N2
     |  Person_less(_,_) default is false
    
    fun Person_le(X,X) is true
     |  Person_le(X,Y) default is Person_less(X,Y)
    
    fun Person_gt(X,Y) is Person_less(Y,X)
    
    fun Person_ge(X,Y) is Person_le(Y,X);
  }

  implementation equality over Person is {
      (=) = person_eq;
      hashCode = personHash
    } using {
      fun person_eq(someone{name=N1},someone{name=N2}) is N1=N2
       |  person_eq(noone,noone) is true
       |  person_eq(_,_) default is false;
      fun personHash(noone) is hashCode("none")
       |  personHash(someone{name=N}) is hashCode("someone")*37+hashCode(N)
    }
}