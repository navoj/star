internTest is package {

 # makeEmpty(#(#(?F)#{?init})#(?X)) ==> let {
   #$empty has type #~#(F#+"State")# of X;
   def #$empty is #(#~#("empty"#+F)#)#{init}; }
 in #$empty;

 type initializedIdentityState of %t is initIdentity {
   res has type %t;
   count has type integer;
 } or emptyinitializedIdentity {
   res has type %t;
 };

 prc main() do {
   foo has type initializedIdentityState of integer;
   def foo is emptyinitializedIdentity{res=10};
   def e is makeEmpty(initializedIdentity{res=10}(integer));
   logMsg(info, "$e");
   assert e.res = 10;
 }

}