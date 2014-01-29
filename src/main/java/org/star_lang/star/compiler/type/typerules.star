-- Experiment with notation for type checking...

/*
  Type assignment predicate:
  
  E |- Expression : Type
  
  Type inference rule
  
  # Tp1 & .. & Tpn ==> Tpk
  
  where Tpi are type predicates. The environment part may be omitted, in which case it is assumed to be shared by all type predicates in a rule.
  
  Type safety predicate
  
  E |= Expression
  
  E.g., type of function application:
  
  # E |- F:argTypes=>resType & A:* argTypes ==> E|- F@A : resType;
  
  or, omitting explicit environments.
  
  # F : argTypes=>resType & A :* argTypes ==> F@A : resType
  
  E.g., type of a variable
  
  # E |- var (A,t) in E ==> A:refresh(E,t)
  
  Type of a record structure
  
  #  Lbl: {FieldTypes}=>Rt & all |?F is ?E| in Fields => (F:Ft in FieldTypes & E:Ft) ==> |?Lbl{?Fields}| : Rt
  
  
  
  