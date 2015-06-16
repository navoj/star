import worksheet
worksheet{
  contract C over %state is {
    functionCall has type ((%state)=>(%state));
  };

  temporalAddElementToState(n matching node{f=oldState; C\#state=I; type state=%s}) is node{f is oldState; C\#state is I; type %s counts as state;};

  type R is node {
    state has kind type where C over state
    f has type state;
  };

  implementation C over string is {
    functionCall(a) is a;
  };

  def tmp is temporalAddElementToState(node{f="fgh"});
}