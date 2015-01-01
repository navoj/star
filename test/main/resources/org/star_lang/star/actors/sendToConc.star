sendToConc is package{
  import concurrency;
  
  type stockTick is stockTick{
    symbol has type string;
    price has type float
  }

  C has type concurrent actor of { tick has type occurrence of stockTick };
  C is concurrent actor{
    on X on tick do
      logMsg(info,"#(X.symbol) price: $(X.price)")
  }

  sendMsg(A) do {
    notify A with stockTick{ symbol="AAPL"; price=512.0} on tick
  }

  main() do {
    sendMsg(C);
  }
}