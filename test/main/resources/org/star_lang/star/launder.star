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
launder is package{
  type alert is warning(string);
  
  type eventTime is alias of long;
  
  type ac is ac(string);
  
  type tx is newAccount
    or accountClose
    or depositTx(float)
    or withdrawTx(float);
     
  type bankAgent is alias of actor{
    open has type (string) => ac;
    close has type action(ac);
    deposit has type action(ac,float);
    withdraw has type action(ac,float);
    balance has type (ac) => float;
    
  } originates {
    txs has type stream of ((ac,tx,eventTime));
  };
  
  bankAgent has type ()=>bankAgent;
  bankAgent() is actor{
    relation of((ac, float)) var accounts := indexed{};
    
    open(Name){
      Wh is now();
      extend accounts with (ac(Name),0.0);
      notify (ac(Name), newAccount,Wh) on txs;
      return ac(Name);
    };
    
    close(Ac){
      Wh is now();
      delete (Ac,_) in accounts;
      notify (Ac, accountClose,Wh) on txs;
    };
    
    deposit(Ac,Amnt){
      Wh is now();
      update (Ac,Bal) in accounts with (Ac,Bal+Amnt);
      notify (Ac,depositTx(Amnt),Wh) on txs;
    };
    
    withdraw(Ac,Amnt){
      Wh is now();
      update (Ac,Bal) in accounts with (Ac,Bal-Amnt);
      notify (Ac,withdrawTx(Amnt),Wh) on txs;
    };
    
    balance(Ac) where (Ac,Bal) in accounts is Bal;
  };
  
  agency has type actor{
    txs has type stream of ((ac,tx,eventTime));
    msgs has type stream of alert;
  };
  agency is actor{
    on (Ac,Tx,Wh) on txs and (Ac,MA) in monitors do
      notify MA with (Tx,Wh) on txs;
    on (Ac,Tx,Wh) on txs and not (Ac,MA) in monitors do{
      MA is monitor(Ac);
      volunteer notify MA with X as notify X;
      extend monitors with (Ac,MA);
      notify MA with (Tx,Wh) on txs
    };
    on warning(W) on msgs do
      logMsg(info,"agency has received a warning: $W");

    relation (ac,monitorAgentType) var monitors := indexed{};
  };
  
  type monitorAgentType is alias of actor{
    txs has type stream of ((tx,eventTime));
  } originates {
    msgs has type stream of alert
  };
  
  monitor has type (ac) => monitorAgentType;
  monitor(Ac) is actor{
    on (depositTx(Amnt),Td) on txs and
       (withdrawTx(Amnt),Tw) on txs and
       Td+20<=Tw do
     notify warning("deposit followed by withdrawal within $(Tw-Td) by $Ac") on msgs
  };
  
  citibank is bankAgent();
  
  wells is bankAgent();
  
  customer(Name){
    request { Ac is open(Name); deposit(Ac,1000.0); withdraw(Ac,1000.0); } to citibank;
  }
  
  main() do {
    volunteer notify citibank with X on txs as notify agency with X on txs;  
    volunteer notify wells with X on txs as notify agency with X on txs;  
    
    customer("john");
  }
}