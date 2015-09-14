bank is package{
  import account;
  import counter;
  
  type custTx is newAccount(string) or removeAccount(string);
  
  def AllAccounts is actor{
    private var allActs := dictionary of []; 
    
    on newAccount(Nm) on cust do
      allActs[Nm] := account(Nm);
    
    on removeAccount(Nm) on cust do
      remove allActs[Nm];
      
    fun getAccount(Nm) is allActs[Nm];
  }
  
  def bank is actor{
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
  fun account(owner) is actor{
    private def ac is act{
      id = newCounterNo();
      owner = owner;
      balance := 0.0;
      hist := list of []
    };
    
    on deposit(Amnt) on txs do{
      ac.balance := ac.balance+Amnt;
      ac.hist := list of [ac.hist..,deposit(Amnt)];
      logMsg(info,"Deposited $Amnt into account $(ac.id)");
    };
    
    on withdraw(Amnt) on txs where Amnt=<ac.balance do{
      ac.balance := ac.balance-Amnt;
      ac.hist := list of [ac.hist..,withdraw(Amnt)];
      logMsg(info,"Withdraw $Amnt from account $(ac.id)");
    }
  }
  
  prc main() do{
    notify bank with newAccount("fred") on cust;
    notify bank with newAccount("peter") on cust;
  }
}