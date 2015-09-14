cmlTestAux is package {

  import cml;
  import task;
  
  throwBack has type (channel of %a, integer) => task of void
  throwBack(ch, 0) is taskReturn(void)
  throwBack(ch, count) is
    taskBind(await(recvRv(ch)),
      (function (v) is
        taskBind(send(ch, v),
          (function (_) is
            throwBack(ch, count-1)))))
    

}
