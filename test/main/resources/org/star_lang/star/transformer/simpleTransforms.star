simpleTransforms is package{
  import transform;
  import person;
  import account;
  
  type accountHistory is actHist{
    id has type actId;
    owner has type string;
    balance has type long;
    history has type list of tx;
  };
  
  def GP is transformer{
    depends on accounts'n transactions;
    produces historical'n transactions;
    
    actHist{ id=Id; owner=Owner; balance=bal; history=Hist} in historical if
      account{ id=Id; owner=Owner; balance=bal; lastTx = last} in accounts and 
      Hist bound to list of { all Tx where Tx in transactions and (Tx.source=Id or Tx.dest=Id) order descending by Tx.timestamp};
  }
  
  prc main() do {
/*    request GP to extend accounts with account{id=0L;owner="fred";balance=100L;lastTx=nonLong};
    request GP to extend accounts with account{id=1L;owner="peter";balance=0L;lastTx=nonLong};

    request GP to extend transactions with tx{id=100L;timestamp=now(); source=0L; dest=1L; amnt=55L};
    request GP to update Ac matching account{id=0L;balance=B} in accounts with Ac substitute {balance=B-55L};
    request GP to update Ac matching account{id=1L;balance=B} in accounts with Ac substitute {balance=B+55L};
    
    request GP to extend transactions with tx{id=101L;timestamp=now(); source=1L; dest=0L; amnt=5L};
    request GP to update Ac matching account{id=1L;balance=B} in accounts with Ac substitute {balance=B-5L};
    request GP to update Ac matching account{id=0L;balance=B} in accounts with Ac substitute {balance=B+5L};
    */
  }
} 
    