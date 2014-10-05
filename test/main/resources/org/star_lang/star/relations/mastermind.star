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
  
  choices is multicat(map(NofM(3,list of {1;2;3;4;5;6;7;8;9}),permute))
  
  score(Test,Secret) is (countBlacks(Test,Secret),countWhites(Test,Secret)) using {
    countBlacks(list of {},list of {}) is 0;
    countBlacks(list of {E;..L},list of {E;..R}) is countBlacks(L,R)+1;
    countBlacks(list of {_;..L},list of {_;..R}) is countBlacks(L,R);
    
    stripBlacks(list of {},L,list of {},R) is (L,R);
    stripBlacks(list of {E;..L},LL,list of {E;..R},RR) is stripBlacks(L,LL,R,RR);
    stripBlacks(list of {E1;..L},LL,list of {E2;..R},RR) is stripBlacks(L,list of {E1;..LL},R,list of {E2;..RR});
    
    countWhites(L,R) where stripBlacks(L,list of {},R,list of {}) matches (LL,RR) is size(list of {E where E in LL and E in RR}); 
  };
  
  verify(Test,list of {}) is true;
  verify(Test,list of {(Prev,(B,W));..Trials}) where score(Test,Prev)=(B,W) is verify(Test,Trials);
  verify(Test,_) default is false;
  
  filterPerms(Scores,Perms) is list of { P where P in Perms and verify(P,Scores)};
  
  guessSecret(Secret) is valof{
    var P := choices;
    var Scores := list of {};
    while true do{
      Guess is P[0];
    
      Sc is score(Guess,Secret);
      logMsg(info,"guess = $Guess, score=$Sc");
      Scores := list of {(Guess,Sc);..Scores};
      P := filterPerms(Scores,P);
      if Sc=(3,0) then
        valis Guess
    }
  }
  
  main() do {    
    Secret is choices[random(size(choices))];
    logMsg(info,"secret is $Secret");
    
    Find is guessSecret(Secret);
    
    logMsg(info,"found $Find");
  }
} 
