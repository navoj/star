account is package{
  type account is account{
    id has type actId;
    owner has type string;
    balance has type long;
    lastTx has type txId;
  }
  
  idKey has type (%t)=>long where %t implements { id has type long };
  fun idKey(R) is R.id;
  
  type txId is alias of long;
  type actId is alias of long;
  
  type tx is tx{
    id has type txId;
    timestamp has type date;
    source has type actId;
    dest has type actId;
    amnt has type long;
  }
  
  implementation equality over account is {
    fun X = Y is same_account(X,Y);
    fun hashCode(A) is account_hash(A)
  } using {
    fun same_account(account{id=N1},account{id=N2}) is N1=N2
     |  same_account(_,_) default is false;
    fun account_hash(account{id=N}) is hashCode(N)
  }
}