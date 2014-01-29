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
peopleAgents is package{
  type repoType is alias of actor of {parent has type ref relation of ((string,string));
                               males has type ref relation of (string);
                               addSon has type action(string,string);
                               addChild has type action(string,string);
                              };
  repo is actor{
    parent has type ref relation of ((string,string));
    
    var parent := relation of {("J","S"); ("P","S"); ("J","T"); ("P","T")};
    var males := relation of{ "J"; "T"};
    
    addSon(P,C) do {
      extend parent with (P,C);    
      extend males with C;
    }
    
    addChild(P,C) do
      extend parent with (P,C);
  }

  parentOf has type (repoType,string) => list of string;
  parentOf(Ac,P) is query Ac's parent with all X where (X,P) in parent order by X;
  
  qActor has type (repoType)=>actor of{ parentOf has type (string)=>relation of string};
  qActor(O) is actor{
     parentOf(P) is query O's parent with relation of { all X where (X,P) in parent };
  };
  
  main() do {
    logMsg(info,"Q parents of S are $(query repo's parent with all X where (X,"S") in parent order by X)");
    logMsg(info,"parents of S are $(parentOf(repo,"S"))");
    assert "J" in parentOf(repo,"S");
    assert parentOf(repo,"S")=list{"J";"P"};
    
    logMsg(info,"parents of J are $(parentOf(repo,"J"))");
    assert parentOf(repo,"J")=list{};
    
    logMsg(info,"qA is $(qActor(repo))");
    
    logMsg(info,"parents of qActor S are $(query qActor(repo)'s parentOf with parentOf("S"))");
    assert (query qActor(repo)'s parentOf with parentOf("S"))=relation{"P";"J"};
    
    request repo's addChild to addChild("MM","J");
    request repo's addChild to addChild("KK","J");
    
    assert parentOf(repo,"J") = list{"KK";"MM"};
    logMsg(info,"parents of J are $(parentOf(repo,"J"))");
  }
}