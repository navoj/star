import worksheet
worksheet{
  #infix("~=",900);

  -- Approximate equality
  fun X ~= Y is abs((X-Y)/X)<0.0001;

 
  contract distribution over t determines (k,v) where arithmetic over v is {
    _prob has type (t,k)=>float;
    _update_data has type (t,k,v) => t;
  }

  type discrete of (k,v) where arithmetic over v is discrete{
    data has type dictionary of (k,v);
    total has type v;
    total default is project1(leftFold1(((_,A),(K,V))=>(K,A+V),data));
  }
  
  private fun project1((_,X)) is X;

  implementation distribution over discrete of (%k,%v) determines (%k,%v) 
     where coercion over (%v,float) is {
    fun _update_data(D,K,V) where D.data[K] has value OV is discrete{data=D.data[K->V+OV]; total=D.total+V}
    |   _update_data(D,K,V) default is discrete{ data = D.data[K->V]; total = D.total+V }
    fun _prob(D,K) is (someValue(D.data[K]) as float)/(D.total as float);
  }

  var cookies := discrete{data = dictionary of { "Bowl 1" -> 0.5; "Bowl 2" -> 0.5 }}

  show cookies;
  
  assert cookies.total = 1.0;
  assert _prob(cookies,"Bowl 1")~=0.5;
  
  cookies := _update_data(cookies,"Bowl 1",0.25)
  
  show cookies;
}
