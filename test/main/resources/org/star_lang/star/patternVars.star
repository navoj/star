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
patternVars is package{

  type event is event{
    str has type string;
  };
  
  type possible of %t is possible(%t) or impossible;

  actorFun has type (string)=> actor{ 
    events has type occurrence of event;
  };
  
  actorFun(St) is actor{
    on Evt matching event{str=St} on events do
      logMsg(info,"We have $Evt");
    on event{str=S} on events do 
      logMsg(info,"event $S");
  }
  
  mapper(St) where St->X in wordMap is possible(X);
  mapper(_) default is impossible;
  
  wordMap is dictionary of {
    "hello" -> "Ok"
  };
  
  tester(Str) is valof{
    if mapper(Str) matches possible(X) and X!="" then{
      logMsg(info,"We have a match");
      valis "$X!";
    }
    else
      valis "$Str??"
  }
  
  main() do {
    A is actorFun("hello");
    
    notify A with event{str="hello"} on events;
    notify A with event{str="again"} on events;
    
    notify A with event{str=tester("hello")} on events;
    notify A with event{str=tester("huh")} on events;
  }
    
}