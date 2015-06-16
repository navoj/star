sendToConc is package{
  
  type stockTick is stockTick{
    symbol has type string;
    price has type float
  }

  C has type concurrent actor of { tick has type occurrence of stockTick };
  def C is concurrent actor{
    on X on tick do
      logMsg(info,"#(X.symbol) price: $(X.price)")
  }

  prc sendMsg(A) do {
    notify A with stockTick{ symbol="AAPL"; price=512.0} on tick
  }

  prc main() do {
    sendMsg(C);
  }
}