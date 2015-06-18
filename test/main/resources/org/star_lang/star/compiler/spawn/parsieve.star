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
parsieve is package{
  -- parallel implementation of naive sieve of eratosthenes, based on Reppy's implementation
  
  fun filter(P,inCh) is let{
    def outCh is channel();
    
    fun loop() is task{
      while true do {
        def I is valof (wait for incoming inCh);
        if not I%P=0 then
          perform send(outCh,I);
      }
    };
    
    { ignore background loop() }
  } in outCh;
  
  fun sieve() is let{
    def primes is channel();
    
    fun head(ch) is task{
      def p is valof recv(ch);
      logMsg(info,"next prime is $p");
      perform send(primes,p);
      perform head(filter(p,ch));
    };
    
    { ignore background head(natStream(2)) }
  } in primes; 
  
  fun natStream(S) is valof{
    def ch is channel();
    ignore background task {
      var counter := S;
      while true do {
        -- logMsg(info,"Sending integer $counter");
        perform send(ch,counter);
        counter := counter+1;
      }
    };
    valis ch;
  }
  
  fun primes(N) is let{
    def s is sieve();
    
    fun loop(0,L) is reverse(L)
     |  loop(I,L) is loop(I-1,cons of [valof recv(s),..L])
  } in loop(N,cons of []);
  
  consumer has type (channel of integer)=>task of (());
  fun consume(inCh) is task {
    while true do {
      def M is valof recv(inCh);
      logMsg(info,"We got $M");
    }
  }
  
  prc main() do {
    logMsg(info,"primes up 100 = $(primes(100))");
  
    -- logMsg(info,"primes up 1000 = $(primes(1000))");
    -- start is nanos();
    -- Primes is primes(1000);
    -- time is (nanos()-start) as float/1.0e9;
    -- logMsg(info,"primes up 10000 = $(Primes) in $time seconds");
  }  
}