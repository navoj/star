helloAfternoon is package {
  fun hello(who,morning) default is "Good Morning, " ++ who
   |  hello(who,morning) where not morning is "Hello " ++ who;
}