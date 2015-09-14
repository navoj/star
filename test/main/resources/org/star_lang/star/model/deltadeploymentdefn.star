DeltaDeployment is package {

-- This package defines the 6.2 Types used by platform for delta deployment

-- change list related ...

type mChangeList is mvChangeList {
  taskId has type long;
  modelName has type string;
  componentChangeListMap has type dictionary of(string, mComponentChangeList);
};

type mComponentChangeList is mvComponentChangeList {
  taskId has type long;
  componentName has type string;
  changeListElementMap has type dictionary of(string, mComponentChangeListElement);
};

type mComponentChangeListElement is mvComponentChangeListElement {
  taskId has type long;
  componentName has type string;
  locator has type string;
  operation has type string;
  newValue has type any;
};


-- prepare step related ...

type mDeploymentPrepareResult is mvDeploymentPrepareResult {
  componentName has type string;
  wholeComponentPauseRequired has type boolean;
  portNamesRequiringPause has type list of string;
};

-- execute step related ...

type mDeploymentExecuteResult is mvDeploymentExecuteResult {
  executeSuccessful has type boolean;
};

-- port pause check step related ...

type mDeploymentPortPauseCheckRequest is mvDeploymentPortPauseCheckRequest {
  componentName has type string;
  portNamesCheckingForPause has type list of string;
};

type mDeploymentPortPauseCheckResult is mvDeploymentPostPauseCheckResult {
  componentName has type string;
  canHandlePortPauses has type boolean;
};

-- port pause step related ...

type mDeploymentPortPause is mvDeploymentPortPauseRequest {
  componentName has type string;
  wholeComponentPauseRequired has type boolean;
  portNamesToPause has type list of string;
};

type mDeploymentPortPauseResult is mvDeploymentPortPauseResult {
  componentName has type string;
  wholeComponentPauseRequired has type boolean;
  portPausesSuccessful has type boolean;
};

-- post execute step related ...

type mDeploymentPostExecuteResult is mvDeploymentPostExecuteResult {
  postExecuteSuccessful has type boolean;
};

 main has type action();
  main() do {
         logMsg(info, "deltadeploymentdefn.srule:: 6.2.1 Runtime Delta Deployment Declarations completed...");
  };

}