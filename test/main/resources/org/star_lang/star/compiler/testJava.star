 testJava is package{
  java org.star_lang.star.compiler.SimpleFuns;
  java (java).lang.System;
    
  prc main() do {
    logMsg(info,"invoking javaFoo(23,45): $(javaFoo(23,45))");
    logMsg(info,"invoking javaString(34): $(javaString(34))");
    doSomething("hello",34.56D);
    doSomething(javaFoo(23,45),45.23);

    logMsg(info,"current time: $(currentTimeMillis())");
    
    logMsg(info,"current properties: $(getProperties())");
    
    logMsg(info,"ifunc(4,5)=$(ifunc(4,5))");
  }
}