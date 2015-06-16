worksheet{
  contract four over t is {
    plus has type (t,t)=>t;
    sub has type (t,t)=>t;
    mul has type(t,t)=>t;
    div has type (t,t)=>t;
  }
  fun double(X) is plus(X,X)
      
  show double(2)
}