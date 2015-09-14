matchComp is package{

  fun peel(list of [],list of []) is list of []
   |  peel(list of [X,..XX],list of [X,..YY]) is list of [X,..peel(XX,YY)]
   |  peel(list of [X,..XX],list of [Y,..YY]) where X!=Y is peel(XX,YY)
  
  prc main() do {
    logMsg(info,"peel of [1,2,3],[1,3,3] is $(peel(list of [1,2,3],list of [1,3,3]))");
    assert peel(list of [1,2,3],list of [1,3,3])=list of [1,3]
  }
}