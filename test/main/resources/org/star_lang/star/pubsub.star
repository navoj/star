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
pubsub is package{
  -- Test querying etc between actors.
  -- Simulates a simple pub-sub system of listeners and spammers
  -- This tests quite a large fraction of the capabilities of StarRules
   
  type topic is alias of string;
  type newsItem is item(list of topic,string);
  
  server has type actor{
    report has type stream of newsItem;
    catalog has type relation of ((topic, newsItem));
  } originates {
    news has type stream of topic;
  };
  
  server is actor{
    on Itm matching item(topics,text) on report do{
      for top in topics do{
        addItem(top,Itm);
        notify top on news;
      }
    };
  } using {
    var catalog := indexed {};
    addItem(tpix,txt){
      extend catalog with (tpix,txt)
    };
  };
  
  type listenerActorType is alias of actor {
    news has type stream of topic;
  } originates{
    catalog has type relation of {topic has type topic; item has type newsItem};
  };
  listener has type (string,topic) => listenerActorType;
  /* This is how it should be. But not today ...
  listener(Id,Tp) is actor{
    on Tp on news do
        logMsg(info,"$Id received news of $Tp: $(query all N where {topic=Tp;item=N} in catalog)");
    volunteer notify X on news from server as notify X on news; 
    volunteer query {topic=T;item=I} in catalog as query (T,I) in catalog to server;
  };
  */
  -- This is how we do it today ...
  listener(Id,Tp) is valof{
    listenerActorType var L := actor{
        on Tp on news do
          logMsg(info,"$Id received news of $Tp: $(query all N where {topic=Tp;item=N} in catalog)");
      };
    volunteer notify X on news from server as notify X on news to L; 
    volunteer query {topic=T;item=I} in catalog from L as query (T,I) in catalog to server; 
    valis L;
  };
  
  spammer has type (relation of topic) => actor{
    topics has type relation of topic;
    blast has type action(integer);
  } originates{
    report has type stream of newsItem;
  };
  
  spammer(tops) is actor{
    T in topics if T in tops;
    blast(Count){
      for Ix in iota(1,Count,1) do
        notify item(tops,"We have news item $Ix") on report
    };
  };
  
  main() do {
    L1 is listener("L1",["tech","business","health"]);
    L2 is listener("L2",["tech"]);
    L3 is listener("L3",["business","health"]);
    L4 is listener("L4",[]);
    
    S1 is spammer(["tech"]);
    S2 is spammer(["business"]);
    S3 is spammer(["health"]);
    S4 is spammer(["tech","business","health"]);
    
    volunteer X from S1 as X to server;
    volunteer X from S2 as X to server;
    volunteer X from S3 as X to server;
    volunteer X from S4 as X to server;
    
    request S1 to blast(4);
    request S2 to { blast(2); blast(4); };
    request S3 to blast(Ix) where Ix in [1,2,3,4];
    request S4 to blast(0);
  }
}
   