account is package{

  type tx is deposit(float) or withdraw(float);

  type account is act{
    id has type integer;
    owner has type string;
    balance has type ref float;
    hist has type ref list of tx;
  }
  
  type accountActor is alias of actor of { txs has type occurrence of tx };
}
