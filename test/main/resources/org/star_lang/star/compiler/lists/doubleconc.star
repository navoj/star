worksheet{
  conc has type for all t,e such that
    (t,t)=>t where sequence over t determines e;
  conc([],X) is X;
  conc([E,..X],Y) is [E,..conc(X,Y)]
  
  R is conc(list of [1,2],conc([3],[4,5]))
  
  show R
  
  assert R = list of [1,2,3,4,5]
  
  Em is list of [];
  show Em
  assert list of [] = Em;
}