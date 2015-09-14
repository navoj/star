anonRefRecord is package{
  type socialNetworkType is alias of {
    nodeTable has type list of socialNetNode;
    linkTable has type list of socialNetLink;
  };

  -- The actual (not-yet-active) model, an (initially empty) social network
  socialNetwork has type ref(socialNetworkType);
  var socialNetwork := {
      nodeTable = list of []; 
      linkTable = list of [];
  };
  
  prc main() do {
    assert socialNetwork.nodeTable = list of []
  }
}