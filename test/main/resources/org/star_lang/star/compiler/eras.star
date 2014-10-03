/**
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
eras is package{  
  append(nil,X) is X;
  append(cons(H,T),X) is cons(H,append(T,X));
  
  reverse(nil) is nil;
  reverse(cons(H,T)) is append(reverse(T),cons(H,nil));
  
  iot(K,K2,_) where K>=K2 is cons(K,nil);
  iot(M,X,S) is cons(M,iot(M+S,X,S));
  
  length(nil) is 0;
  length(cons(_,T)) is length(T)+1;
  
  sieve(nil) is nil;
  sieve(cons(H,T)) is cons(H,sieve(filter(T,H)));
  
  filter(nil,_) is nil;
  filter(cons(H,T),K) where H%K=0 is filter(T,K);
  filter(cons(H,T),K) default is cons(H,filter(T,K));
  
  init(Length) is
    iot(3,Length,2);
    
  run(Data) do {
    Pr is sieve(Data);
  }
  
  NoTh() do {};
  
  bench(Run,Data) is valof{
    EpS is nanos();
    for Ix in iota(1,Run,1) do{
      NoTh();
    };
    ETm is nanos()-EpS;
    logMsg(info,"empty time is $ETm");
    
    St is nanos();
    for Ix in iota(1,Run,1) do{
      run(Data);
    }
    valis ((nanos()-St-ETm)as float)/(Run as float);
  }
  
  main() do
  {
    data is iot(3,1000,2);
    primes is sieve(data);
    -- logMsg(info,"primes up 1000 is $primes");  
    PrCount is length(primes);
    assert PrCount=167; -- 167 primes between 3 and 1000
    
    Tm is bench(1000,iot(3,1000,2));
    
    logMsg(info,"$PrCount primes in $Tm nanos at $(Tm/(PrCount as float)) nanos/prime");
  }
}