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
workedex is package{
  import account;
  import counter;
  import treemap;
  import bitstring;
  
  #infix("bound to",900);
  #?X bound to ?Y ==> Y matches X;
  
  -- this is a worked example of some key algorithms in the transformer
  
  type fullAccount is fullAc{
    id has type actId;
    owner has type string;
    balance has type long;
    history has type list of tx;
  };
  
  
  /*
   * Implement the transformation rule:
   *  fullAc{ id=Id; owner=Owner; balance=bal; history=Hist} in historical if
   *    account{ id=Id; owner=Owner; balance=bal; lastTx = last} in accounts and 
   *    Hist bound to list of { all Tx where Tx in transactions and (Tx.source=Id or Tx.dest=Id) order descending by Tx.timestamp};
   *
   * as a special function
   */
   
  type dep is dep(string,long) or multiDep(list of dep) or allDep(string) or noDep;
  
  -- encapsulate the query into a generator function 
  fullAcGen(Txs,Acts) is all (fullAc{ id=Id; owner=Owner; balance=bal; history=Hist},multiDep(list of {dep("accounts",AcK);..Keys})) where
        AcK->account{ id=Id; owner=Owner; balance=bal; lastTx = last} in Acts and 
        (Hist,Keys) bound to unravel(list of { all (Tx,dep("transactions",TxK)) where TxK->Tx in Txs and 
                                                                                  (Tx.source=Id or Tx.dest=Id)
                                                                               order descending by Tx.timestamp});

  unravel(Ls) is valof{
    var Lin := Ls;
    var LO := list of {};
    var RO := list of {};
    while Lin matches list of {(L,R);..LinR} do{
      LO := list of {LO..;L};
      RO := list of {RO..;R};
      Lin := LinR;
    }
    valis (LO,RO);
  }
  
  var transactions := treemap of {};
  var transactionIndex := treemap of {};
  var transFollow := treemap of {}; -- what depends on transaction entries

  var accounts := treemap of {};
  var accountIndex := treemap of {};
  var accntFollow := treemap of {}; -- what depends on account entries
  
  var full := treemap of {};
  var fullDeps := treemap of {};
  
  genIndex(R,key,ref index) is valof{
    H is key(R);
    Id is nextId();
    Ix is index[H] default cons of {};
    index[H] := cons of {(Id,R);..Ix};
    valis Id;
  }
  
  genTxIndex(Tx) is genIndex(Tx,idKey,ref transactionIndex);
  genAccountIndex(Act) is genIndex(Act,idKey,ref accountIndex);
  
  getAccountIndex(Act) is any of Id where (Id,_) in accountIndex[idKey(Act)] default nonLong;
  
  addAccount(Act) do {
    Id is genAccountIndex(Act); -- pick up a unique id for the new tuple
    accounts[Id] := Act;
    SnglAc is treemap of {(Id,Act)};
    for (F,D) in fullAcGen(transactions,SnglAc) do{
      FId is nextId();
      full[FId] := F;
      fullDeps[FId] := D;
      updateActFollow(D,FId);
      updateTxFollow(D,FId);
    }
  }
  
  updateAccnt(Ptn,Updater) do {
    Ix is getAccountIndex(Ptn);
    
    if Ix!=nonLong then{ 
      accounts[Ix] := Updater(accounts[Ix]);
      redoResult(accntFollow[Ix]);
    }
  }
  
  updateAccount(Ptn,Updater) do let{
    var track := array of {};
    
    updateAct((Ix,Act)) is valof{
      ActFoll is accntFollow[Ix];
      track := array of {ActFoll;..track};
      
      UpAc is Updater(Act);

      valis (Ix,UpAc);
    };
    testAct() from (_,Ptn());
    
    followTrack() do {
      for Foll in track do
        redoResult(Foll);
    }
  } in {
    accounts := _update(accounts,testAct,updateAct);
    followTrack();
  }
  
  redoResult(dep("fullAccount",Ix)) do
    redoFullAccount(Ix);
  redoResult(multiDep(L)) do { 
    for D in L do
      redoResult(D);
  }
  redoResult(noDep) do nothing;
  redoResult(allDep("fullAccount")) do {
    for Ix in (all Ix where Ix->_ in full) do
      redoFullAccount(Ix); 
  }
  
  redoFullAccount(Ix) do {
    -- logMsg(info,"redo full $Ix");
    D is fullDeps[Ix];
    
    -- logMsg(info,"depends = $D");
    Acts is getDepAccount(D); 
    -- logMsg(info,"Acts=$Acts");

    -- logMsg(info,"current transactions: $transactions");
    Txs is getDepTxs(D);
    -- logMsg(info,"Txs=$Txs");

    for (NF,ND) in fullAcGen(Txs,Acts) do{
      full[Ix] := NF;
      fullDeps[Ix] := ND;
      -- logMsg(info,"full acc: $NF, depends on $ND");
        
      updateActFollow(ND,Ix);
      updateTxFollow(ND,Ix);
    }
  }

  removeResult(dep("fullAccount",Ix)) do
    removeFullAccount(Ix);
  removeResult(multiDep(L)) do { 
    for D in L do
      removeResult(D);
  }
  removeResult(noDep) do nothing;
  removeResult(allDep("fullAccount")) do {
    for Ix in (all Ix where Ix->_ in full) do
      removeFullAccount(Ix); 
  }
  
  removeFullAccount(Ix) do {
    remove full[Ix];
    remove fullDeps[Ix];
  }

  removeAccnt(Rec) do {
     Ky is getAccountIndex(Rec);
    
    if Ky!=nonLong then{ 
      remove accounts[Ky];
      removeResult(accntFollow[Ky]);
    }
  }
  
  removeAccount(Ptn) do let{
    var track := array of {};
    
    removeAct(Ix) is valof{
      ActFoll is accntFollow[Ix];
      track := array of {ActFoll;..track};
      valis true;
    };
    testAct() from (Ix,Ptn()) where removeAct(Ix);
    
    followTrack() do {
      for Foll in track do
        removeResult(Foll);
    }
  } in {
    accounts := _delete(accounts,testAct);
    followTrack();
  }

  addToTxs(Tx) do {
    IxT is genTxIndex(Tx);
    transactions[IxT] := Tx;

    FF is all (F,Ix) where Ix->F in full; -- this is a work-around.
    for (F,Ix) in FF do{
      D is fullDeps[Ix];
      Acts is getDepAccount(D);
      
      Txs is treemap of {(IxT,Tx);..getDepTxs(D)};

      for (NF,ND) in fullAcGen(Txs,Acts) do{
        full[Ix] := NF;
        fullDeps[Ix] := ND;
        
        updateActFollow(ND,Ix);
        updateTxFollow(ND,Ix);
      };
    }
  }
  
  updateTrans(Ptn,Updater) do let{
    var track := array of {};
    
    updateTx((Ix,Tx)) is valof{
      ActFoll is transFollow[Ix];
      track := array of {ActFoll;..track};
      
      valis (Ix,Updater(Tx));
    };
    testTx() from (_,Ptn());
    
    followTrack() do {
      for Foll in track do
        redoResult(Foll);
    }  
  } in {
    transactions := _update(transactions,testTx,updateTx);
    followTrack();
    logMsg(info,"full after tx update: $full");
  }
  
  getDepAccount(dep("accounts",Id)) is treemap of {(Id,accounts[Id])};
  getDepAccount(allDep("accounts")) is accounts;
  getDepAccount(multiDep(L)) is treemap of {all (Id,accounts[Id]) where dep("accounts",Id) in L };
  
  getDepTxs(dep("transactions",Id)) is treemap of {(Id,transactions[Id])};
  getDepTxs(allDep("transactions")) is transactions;
  getDepTxs(multiDep(L)) is treemap of {all (Id,transactions[Id]) where dep("transactions",Id) in L};
  
  -- update a follows table based on the new set up dependencies
  /*@
   The updateFollow function takes a dependency that was computed during a query and updates a table of
   consequences. The idea is that each source of a result tuple has a set of consequential tuples in output tables.
   E.g., when a transaction entry results in an entry in the fullAccount table, then there will be entries (often
   multiple entries) in the transactionFollows table corresponding to the fullAccount entries that depend on the
   transaction.
   
   @arg Deps A dependency structure. Typically computed by a query
   @arg Lbl the name of the table to record follows of.
   @arg Follow the table of follows from an input table
   @arg FollowLbl the name of the result table
   @arg FollowIx the identity of the tuple in the result table
   @return a table updated with new record of consequential entries
  */
  updateFollow(Deps,Lbl,Flw,FollowLbl,FollowIx) is let{
    updateFlw(Follow,dep(Lb,Id)) where Lb=Lbl is let{
      updateDeps(noDep) is dep(FollowLbl,FollowIx);
      updateDeps(dep(F,Fix)) where F=FollowLbl and Fix=FollowIx is dep(F,Fix);
      updateDeps(dep(F,Fix)) is multiDep(list of {dep(F,Fix);dep(FollowLbl,FollowIx)});
      updateDeps(multiDep(L)) is multiDep((list of {dep(FollowLbl,FollowIx)}) union L);
      updateDeps(allDep(Lbls)) is allDep(Lbls);
    } in _set_indexed(Follow,Id,updateDeps((Follow[Id] default noDep)));
    updateFlw(Follow,dep(_,Id)) is Follow;
    updateFlw(Follow,multiDep(L)) is leftFold(updateFlw,Follow,L)
  } in updateFlw(Flw,Deps);
  
  updateActFollow(Deps,FIx) do 
    accntFollow := updateFollow(Deps,"accounts",accntFollow,"fullAccount",FIx);
  updateTxFollow(Deps,FIx) do
    transFollow := updateFollow(Deps,"transactions",transFollow,"fullAccount",FIx);
  
  main() do {
--    extend accounts with account{id=0L;owner="fred";balance=100L;lastTx=0L};
    addAccount(account{id=0L;owner="fred";balance=100L;lastTx=0L});

--    extend accounts with account{id=1L;owner="peter";balance=0L;lastTx=0L};
    addAccount(account{id=1L;owner="peter";balance=0L;lastTx=0L});

--    extend transactions with tx{id=100L;timestamp=now(); source=0L; dest=1L; amnt=55L};
    addToTxs(tx{id=100L;timestamp=now(); source=0L; dest=1L; amnt=55L});
    
--    extend transactions with tx{id=101L;timestamp=now(); source=1L; dest=0L; amnt=5L};
    addToTxs(tx{id=101L;timestamp=now(); source=1L; dest=0L; amnt=5L});
	logMsg(info,"full=$full");
	logMsg(info,"accounts=$accounts");
	logMsg(info,"trans=$transactions");
	
	logMsg(info,"follows of Acc = $accntFollow");
	logMsg(info,"follows of Txs = $transFollow");
	
--    update Ac matching account{id=0L;balance=B} in accounts with Ac substitute {balance=B-55L};

--    updateAccount((pattern() from account{id=0L}),
--                 (function(Ac) is Ac substitute {balance=Ac.balance-55L}));
                 
    updateAccnt({id=0L},(function(Ac) is Ac substitute {balance=Ac.balance-55L}));

--    update Ac matching account{id=1L;balance=B} in accounts with Ac substitute {balance=B+55L};
--    updateAccount((pattern() from account{id=1L}),
--                 (function(Ac) is Ac substitute {balance=Ac.balance+55L}));

    updateAccnt({id=1L},(function(Ac) is Ac substitute {balance=Ac.balance+55L}));
                 
--    update Tx matching tx{id=100L} in transactions with Tx substitute { amnt=Tx.amnt+10L};
    updateTrans((pattern() from tx{id=100L}),
                (function(Tx) is Tx substitute { amnt = Tx.amnt+10L})); 
                 
--    update Ac matching account{id=1L;balance=B} in accounts with Ac substitute {balance=B-5L};
--    update Ac matching account{id=0L;balance=B} in accounts with Ac substitute {balance=B+5L};

--    removeAccount((pattern() from account{id=0L}));
    removeAccnt({id=0L});
    
    logMsg(info,"full at end = $full");
    logMsg(info,"fullDep = $fullDeps");
	logMsg(info,"accounts=$accounts");
	logMsg(info,"trans=$transactions");
  }
}
    