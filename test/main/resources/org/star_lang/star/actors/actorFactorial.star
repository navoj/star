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
actorFactorial is package{
  
  -- the worlds worst way of doing factorial!
  -- ask an actor to do it.
  
  -- A basic actor that know how to factorialize integers
  type factActor is alias of actor of {
    fact has type (integer)=>integer;
  };
  
  factorActor has type factActor;
  def factorActor is actor{
    fun fact(0) is 1
     |  fact(N) is N*fact(N-1);
  };
  
  -- This actor knows how to ask another actor to do a factorial
  type factQuery is alias of actor of{
    probe has type action(integer,integer);
  }
  
  queryActor has type (factActor)=>factQuery;
  fun queryActor(A) is actor{
    prc probe(X,T) do {
      logMsg(info,"starting probe of factor $X");

      def F is query A's fact with fact(X);
      
      logMsg(info,"probe returns $F");
      assert F=T;
    }
  };
  
  prc main() do {
    -- link the query actor to the provider actor for factorial
    def Q is queryActor(factorActor);
        
    -- Ask for a probe
    request Q's probe to probe(10,3628800);
    
    -- directly ask for factorial
    def FF is query factorActor's fact with fact(15);
    
    logMsg(info,"query fact(15) is $FF"); 
    
    assert (query factorActor's fact with fact(15))=2004310016;
  }
}