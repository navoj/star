casebug is package {
  myStatus has type outletStatus;
  def myStatus is oPen;
  
  type outletStatus is oPen or closed or underRepair or unknown;

  prc main () do {
    switch myStatus in {
      case oPen do logMsg(info,"it is open");
      case _ default do nothing;
    }
  }
}