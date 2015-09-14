cmlTestAux2 is package {

  import cml;
  import task;
  
  throwIn has type (channel of %a, %a, integer) => task of %a
  throwIn(ch, v, 0) is taskReturn(v)
  throwIn(ch, v, count) is
    taskBind(send(ch, v),
      (function (_) is
        taskBind(await(recvRv(ch)),
          (function (v2) is
            throwIn(ch, v2, count-1)))))
  
}
