/**
 * 
 * Copyright (C) 2013 Starview Inc
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA
 * 
 * @author fgm
 *
 */
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
    #unfold(?P, ?R, ?F, ?Ex./Average(?Tm)) ==> unfold(P, R, #(F;#$"ave" is Average(Tm))#, Ex./#(#$"ave")#(Buffer));
    #unfold(?P, ?R, ?F, ?Ex./Count(?Tm)) ==> unfold(P, R, #(F;#$"count" is Count(Tm))#, Ex./#(#$"count")#(Buffer));
    #unfold(?P, ?R, ?F, ?Ex./StdDev(?Tm)) ==> unfold(P, R, #(F;#$"stdDev" is StdDev(Tm))#, Ex./#(#$"stdDev")#(Buffer));
    #unfold(?P, ?R, ?F, ?Ex./Forecast(?Tm,?Ftr)) ==> unfold(P, R, #(F;#$"forecast" is Forecast(Tm,Ftr))#, Ex./#(#$"forecast")#(Buffer));
    #unfold(?P, ?R, ?F, ?Ex./Min(?Tm)) ==> unfold(P, R, #(F;#$"min" is Min(Tm))#, Ex./#(#$"min")#(Buffer));
    #unfold(?P, ?R, ?F, ?Ex./Max(?Tm)) ==> unfold(P, R, #(F;#$"max" is Max(Tm))#, Ex./#(#$"max")#(Buffer));
    #unfold(?P, ?R, ?F, ?Ex ./ #(?N : ?Nval)#) ==> unfold(#(P;N is Nval)#, #(N=N;R)#, F, Ex./?N);
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