beginEnd is package{
  #prefix("return",1200);
  #pair("procedure","end",2000);
  #pair("begin","end",2000);
  #begin ?B end ==> {B};
  #begin end ==> {};
  
  #procedure ?Tmpl ; ?body end :: statement :- body;*action;
  #begin ?B end :: action :- B;*action;
  #begin end :: action;

  #procedure ?Tmpl ; ?body./#(return ?E)# end ==> fun Tmpl is valof {body./#(valis E)#};
  #procedure ?Tmpl ; ?body end ==> prc Tmpl do body ;
}