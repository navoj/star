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
mastermind is package{
  import permute;
  
  def choices is multicat(map(permute,NofM(3,list of [1,2,3,4,5,6,7,8,9])))
  
  fun score(Test,Secret) is (countBlacks(Test,Secret),countWhites(Test,Secret)) using {
    fun countBlacks(list of [],list of []) is 0
     |  countBlacks(list of [E,..L],list of [E,..R]) is countBlacks(L,R)+1
     |  countBlacks(list of [_,..L],list of [_,..R]) is countBlacks(L,R)
    
    fun stripBlacks(list of [],L,list of [],R) is (L,R)
     |  stripBlacks(list of [E,..L],LL,list of [E,..R],RR) is stripBlacks(L,LL,R,RR)
     |  stripBlacks(list of [E1,..L],LL,list of [E2,..R],RR) is stripBlacks(L,list of [E1,..LL],R,list of [E2,..RR])
    
    fun countWhites(L,R) where stripBlacks(L,list of [],R,list of []) matches (LL,RR) is size(list of {all E where E in LL and E in RR}) 
  }
  
  fun verify(Test,[]) is true
   |  verify(Test,[(Prev,(B,W)),..Trials]) where score(Test,Prev)=(B,W) is verify(Test,Trials)
   |  verify(Test,_) default is false
  
  fun filterPerms(Scores,Perms) is list of { all P where P in Perms and verify(P,Scores)};
  
  fun guessSecret(Secret) is valof{
    var P := choices;
    var Scores := list of [];
    while true do{
      def Guess is someValue(P[0]);
    
      def Sc is score(Guess,Secret);
      logMsg(info,"guess = $Guess, score=$Sc");
      Scores := list of [(Guess,Sc),..Scores];
      P := filterPerms(Scores,P);
      if Sc=(3,0) then
        valis Guess
    }
  }
  
  prc main() do {    
    def Secret is someValue(choices[random(size(choices))]);
    logMsg(info,"secret is $Secret");
    
    def Find is guessSecret(Secret);
    
    logMsg(info,"found $Find");
  }
} 
