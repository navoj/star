import worksheet
worksheet{
  contract C over %state is {
    functionCall has type ((%state)=>(%state));
  };

  fun temporalAddElementToState(n matching node{f=oldState; C\#state=I; type state=%s}) is node{def f is oldState; def C\#state is I; type %s counts as state;};

  type R is node {
    state has kind type where C over state
    f has type state;
  };

  implementation C over string is {
    fun functionCall(a) is a;
  };

  def tmp is temporalAddElementToState(node{f="fgh"});
}