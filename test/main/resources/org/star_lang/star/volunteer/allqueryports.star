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
allqueryports is package{
  import volallquery;
  import ports;
  
  RR is p0rt{
    var Folder := relation{
      ("alpha",true);
      ("beta",false);
      ("gamma",true);
    };
    
    DELETE(K) where (K,_) in Folder is valof{
      delete ((Kk,_) where K=Kk) in Folder;
      valis true;
    };
    DELETE(K) default is false;
    
    report() do logMsg(info,"Folder is $Folder");
  };
  
  PP is connectPort_0(RR);
  
  main() do {
    A1 is query PP's Folder with relation of {all X where (X,true) in Folder};
    
    logMsg(info,"$A1");
    assert A1=relation of {"gamma";"alpha"};
  }
}