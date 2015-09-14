ports is package{
  import actors;
  
  type port of %u is port{
    _query has type for all %s such that ((%u)=>%s,()=>quoted,()=>dictionary of (string,quoted))=> %s;
    _request has type ((%u)=>(),()=>quoted,()=>dictionary of (string,quoted)) => ();
    _notify has type ((%u)=>()) => ();
  };
  
  #p0rt{?S} ==> let{ def #$"Schema" is actorTheta(S); } in port{
    prc _notify(Fn) do Fn(#$"Schema");
    prc _request(Fn,Qt,Fr) do Fn(#$"Schema");
    fun _query(Fn,Qt,Fr) is Fn(#$"Schema");
  };
  
  type p0rt of %t is alias of port of %t;
  
  implementation speech over port of %schema determines (%schema,action) is {
    fun _query(P,Qf,Qt,Fr) is action{ valis P._query(Qf,Qt,Fr)};
    fun _request(P,Qf,Qt,Fr) is action{ P._request(Qf,Qt,Fr)};
    fun _notify(P,Np) is action{ P._notify(Np)};
  };
}