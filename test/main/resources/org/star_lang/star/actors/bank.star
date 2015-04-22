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
bank is package{
  import account;
  import counter;
  
  type custTx is newAccount(string) or removeAccount(string);
  
  AllAccounts is actor{
    private var allActs := dictionary of {}; 
    
    on newAccount(Nm) on cust do
      allActs[Nm] := account(Nm);
    
    on removeAccount(Nm) on cust do
      remove allActs[Nm];
      
    getAccount(Nm) is allActs[Nm];
  }
  
  bank is actor{
    on (Tx,Id) on tx do{      
      if (query AllAccounts with getAccount(Id)) has value AC then
        notify AC with Tx on txs
      else
        logMsg(info,"do not know customer $Id");
    };
    
    on Tx on cust do
      notify AllAccounts with Tx on cust;
  };
  
  account has type (string)=>accountActor;
  account(owner) is actor{
    private ac is act{
      id = newCounterNo();
      owner = owner;
      balance := 0.0;
      hist := list of {}
    };
    
    on deposit(Amnt) on txs do{
      ac.balance := ac.balance+Amnt;
      ac.hist := list of {ac.hist..;deposit(Amnt)};
      logMsg(info,"Deposited $Amnt into account $(ac.id)");
    };
    
    on withdraw(Amnt) on txs where Amnt<=ac.balance do{
      ac.balance := ac.balance-Amnt;
      ac.hist := list of {ac.hist..;withdraw(Amnt)};
      logMsg(info,"Withdraw $Amnt from account $(ac.id)");
    }
  }
  
  main() do{
    notify bank with newAccount("fred") on cust;
    notify bank with newAccount("peter") on cust;
  }
}