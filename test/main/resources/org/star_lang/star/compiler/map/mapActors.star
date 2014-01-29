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
mapActors is package{

  type msg is msg{
    details has type map of (string,msgContent);
    count has type integer;
  }
  
  type msgContent is content{
    data has type string
  }
  
  recep has type (string)=>actor of {
    dataChannel has type stream of any;
  }
  recep(Key) is actor{
    on Data on dataChannel do
      logMsg(info,"Actor $Key got $Data");
  }
  
  portActor is actor{
    on msg{count=Cx;details=Body} on msgChannel do{
  	  for K->content{data=D} in Body do {
  	    send(K,D)
  	  } using{
        send("flight",F) do
          notify Flights with (F cast any) on dataChannel;
        send("rec",R) do
          notify Rec with (R cast any) on dataChannel;
        send(KK,M) default do 
          logMsg(info,"Funny input: $K/$M");
      }
  	}
  } 
  
  Flights is recep("flightAgent");
  Rec is recep("receiverAgent");
  
  main() do {
    notify portActor with msg{count=4; details=map of {"flight"->content{data="UA838"}; "rec"->content{data="24Tons"};"??"->content{data="GIGO"}}} on msgChannel
  }
}