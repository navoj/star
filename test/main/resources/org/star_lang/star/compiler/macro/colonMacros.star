colonMacros is package{
  -- Extract colon labels and special buffer functions

  #postfix((h),100);
  #right((h),100);
  #postfix((m),100);
  #right((m),100);
  #postfix((s),100);
  
  # ?H h ?T ==> T+H*3600000;
  # ?H h ==> H*3600000;
  # ?S s ==> S*1000;
  # ?M m ?T ==> T+M*60000;
  # ?M m ==> M*60000;
  
  -- 24h 12m 34s ==> 24*3600000+12*60000+34*1000
  
  #parseColon(?E) ==> unfold(empty, empty, empty, ?E)  ## {
    #unfold(?P, ?R, ?F, ?Ex./Average(?Tm)) ==> unfold(P, R, #(F;def #$"ave" is Average(Tm))#, Ex./#(#$"ave")#(Buffer));
    #unfold(?P, ?R, ?F, ?Ex./Count(?Tm)) ==> unfold(P, R, #(F;def #$"count" is Count(Tm))#, Ex./#(#$"count")#(Buffer));
    #unfold(?P, ?R, ?F, ?Ex./StdDev(?Tm)) ==> unfold(P, R, #(F;def #$"stdDev" is StdDev(Tm))#, Ex./#(#$"stdDev")#(Buffer));
    #unfold(?P, ?R, ?F, ?Ex./Forecast(?Tm,?Ftr)) ==> unfold(P, R, #(F;def #$"forecast" is Forecast(Tm,Ftr))#, Ex./#(#$"forecast")#(Buffer));
    #unfold(?P, ?R, ?F, ?Ex./Min(?Tm)) ==> unfold(P, R, #(F;def #$"min" is Min(Tm))#, Ex./#(#$"min")#(Buffer));
    #unfold(?P, ?R, ?F, ?Ex./Max(?Tm)) ==> unfold(P, R, #(F;def #$"max" is Max(Tm))#, Ex./#(#$"max")#(Buffer));
    #unfold(?P, ?R, ?F, ?Ex ./ #(?N : ?Nval)#) ==> unfold(#(P;def N is Nval)#, #(N=N;R)#, F, Ex./?N);
    #unfold(?P, ?R, ?F, ?Ex) ==> let{expand(F)} in body(P,eval(R, Ex));
    #body(empty, ?Exp) ==> Exp;
    #body( ?decs,?Exp) ==> let {expand(?decs) } in ?Exp;
--    #expand(#(empty,?A)#) ==> ?A;
--    #expand(#(?A,empty)#) ==> ?A;
--    #expand(#(?A,?B)#) ==> #(expand(A);expand(B))#;
    #expand(#(empty;?A)#) ==> ?A;
    #expand(#(?A;empty)#) ==> ?A;
    #expand(#(?A;?B)#) ==> #(expand(A);expand(B))#;
    #expand(?A) ==> A;
    #eval (empty, ?Ex) ==> { result = ?Ex };
    #eval (#(?P; ?R)#, ?Ex) ==> { expand(#(?P; ?R)#); result=?Ex };
  };
}