actorbank is package{
  type tx is deposit(float) or withdraw(float);
  
  fun account((Nm has type string), Acct) is actor {
    private var bal := 0.0;
    
    on deposit(Amnt) on Tx do
      logMsg(info,"depositing $Amnt in #Nm's account");
    on deposit(Amnt) on Tx where Amnt>=0.0 do
      bal := bal+Amnt;
    on deposit(Amnt) on Tx where Amnt<0.0 do
      logMsg(info,"Cannot deposit negative amnt: $Amnt into $Nm");
    
    on withdraw(Amnt) on Tx do
      logMsg(info,"withdrawing $Amnt from #Nm's account");
    on withdraw(Amnt) on Tx where Amnt>=0.0 and Amnt=<bal do
      bal := bal-Amnt;
    on withdraw(Amnt) on Tx where Amnt<0.0 or Amnt>bal do
      logMsg(info,"Cannot withdraw amnt: $Amnt from $Nm");
   
    fun balance() is bal;
    def name is Nm;
  }
  
  fun bank(Nm) is actor{
    var accounts := dictionary of [];
    var acctNo := 0;
    
    fun custBals() is all (Nm,Bal) where K->A in accounts and (query A's name 'n balance with (name,balance())) matches (Nm,Bal);
    
    fun newAccount(Name) is valof{
      acctNo := acctNo+1;
      accounts[acctNo] := account(Name,acctNo);
      valis acctNo
    }
    
    fun custBal(acct) where accounts[acct] has value A is some((query A with balance()))
     |  custBal(_) default is none;
    
    on (acNo,T) on Tx where accounts[acNo] has value A do
      notify A with T on Tx;
  } 

  prc main() do {
    def B is bank("Super");
    
    def JJ is query B with newAccount("joe");
        
    logMsg(info,"customers of bank are $(query B's custBals with custBals())");
    
    notify B with (JJ,deposit(10.0)) on Tx;
    notify B with (JJ,withdraw(5.0)) on Tx;
    
    logMsg(info,"Joe's account has $(query B with custBal(JJ))");
    assert (query B with custBal(JJ))=some(5.0);
    
    notify B with (JJ,deposit(-1.0)) on Tx;
    notify B with (JJ,withdraw(10.0)) on Tx;

    assert (query B with custBal(JJ))=some(5.0);
    
    logMsg(info,"customers of bank are $(query B's custBals with custBals())");
  }
}