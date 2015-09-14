qfun is package{
  married is { ("john","mary"); ("peter","sue"); };
  children is { ("john","peter"); ("mary","peter") };
  
  childinlaw(P) where (P,C) in children and (C,W) in married is W;
  childinlaw(_) default is "";

  main() do {
    logMsg(info,"childinlaw of john is $(childinlaw(\"john\"))");
  }
}