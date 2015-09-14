metamodeldefn is package {

-- This package defines the 5.3 Application Meta Model

/*
    CONTENT LANGUAGE - BNF Notation

SpeechAction ::= Notify | Query | Request | Volunteer
Notify ::= notify Expression
Query ::= query Expression
Request ::= request Action
Volunteer ::= volunteer ActionPtn [where Condition] as Action
Action ::= { Action ; ... ; Action }
         | SpeechAction
         | Name(Expression ,..., Expression)
         | Action where Condition
Condition ::= Condition and Condition
         | Condition or Condition
         | Condition otherwise Condition
         | not Condition
         | (Condition ? Condition | Condition)
         | Ptn in Expression
         | Expression Relop Expression
Expression ::= Aggregate | Tuple | List | Relation | Map | ScalarLiteral | Path | QueryExpression | (Expression)
ScalarLiteral ::= Integer | Float | String | true | false
Aggregate ::= [Name] { Name = Expression ; ... ; Name = Expression }
Tuple ::= [Name] ( Expression ,..., Expression)
List ::= [ Expression ,..., Expression ]
Relation ::= { Expression ; ... ; Expression }
Map ::= dictionary of { Expression -> Expression; ... ;Expression -> Expression}
QueryExpression ::= (all|one|Integer of) Expression where Condition
          [group by Expression] [sort [descending] by Expression]
Scalar ::= Expression ScalarOp Expression | UnaryOp Expression
ScalarOp ::= + | - | * | / | Relop
Relop ::= = | != | < | =< | > | >=
UnaryOp ::= - | sqrt |abs | ...
Path ::= Name | Path . Name | Path [ Expression ] | Path -> Expression
Ptn ::= TuplePtn | AggregatePtn | ScalarLiteral | WherePtn | MatchingPtn | (Ptn)
TuplePtn ::= [Name] ( Ptn , ... , Ptn )
AggregatePtn ::= [Name] { Name = Ptn ; ... ; Name = Ptn }
WherePtn ::= ( Ptn where Condition )
MatchingPtn ::= Ptn matching Ptn
ActionPtn ::= Name | SpeechActionPtn | Name ( Ptn , ... , Ptn )
SpeechActionPtn ::= notify Ptn | request ActionPtn


*/


-- BEGIN: meta-model for Types & Content Language (Speech Action)----------

type mType is mvType {
  typeLabel has type string;
} or mvTypeExpression {
  typeLabel has type string;
  typeArguments has type list of mType;
  typeArguments default is list of [];
} or mvTypeInterfaceType {
  typeLabel has type string;
  schema has type mSchema;
  schema default is noneSchema;
} or mvTypeVar {
  typeLabel has type string;
  contractDependencies has type list of (string);
  contractDependencies default is list of [];
} or mvUniversalType {         -- mQuantifiedType
  typeLabel has type string;
  typeLabel default is "~";
  boundVar has type mType;
  bound has type mType;
  contractDependencies has type list of (string);
  contractDependencies default is list of [];
};

-- TBD...
-- type mCatalog of %T is dictionary of (string, %T);
-- super package...
-- type mSRPackage is mvSourcePkg(string)
-- or mvSuperPkg(mCatalog of mSRPackage, string);
-- ---

type mTypeDescription is mvTypeDescription {
  tp has type mType;
  valueSpecifiers has type dictionary of (string, mValueSpecifier)
  valueSpecifiers default is dictionary of [];
  -- contracts has type dictionary of (string, mContractInstance);
  -- superType has type mType;
  -- attributeKeys has type dictionary of (string, integer);
  -- constraints has type list of (mExpression);
  -- defltValue has type dictionary of (string, mExpression);
} or noneTypeDescription;

type mValueSpecifier is mvPositionalVSP {
  label has type string;
  tp has type mType;
  -- constraint has type mExpression;
  argTypes has type list of (mType);
  argTypes default is list of [];
  -- funType has type mType --??
} or mvAggregateVSP {
  label has type string;
  tp has type mType;
  memberIndex has type dictionary of (string, integer);
  memberIndex default is dictionary of [];
  memberTypes has type list of mType;
  memberTypes default is list of [];
  defaults has type dictionary of (string, mExpression);
  defaults default is dictionary of [];
  implClass has type string;
  implClass default is "com.starview.platform.language.data.AggregateValue";
} or noneVSP;


type mSchema is mvSchema{
  memberTypes has type dictionary of (string, mType);
  memberTypes default is dictionary of [];
  memberReferences has type list of (string); -- maintains a list of attributes which are references.
  memberReferences default is list of [];
  defaults has type dictionary of (string, mExpression);
  defaults default is dictionary of [];
  constraints has type list of mCondition;
  constraints default is list of [];
} or noneSchema;


-- mExpression corresponds to ContentLanguage (SA) Expression...

type mExpression is mvAggregate(mType, string, dictionary of (string, mExpression))
  or mvTuple(mType, string, list of mExpression) -- //Default name <string> of the Tuple is "()"
  or mvRelation(mType, list of mExpression)
  or mvList(mType, list of (mExpression))
  or mvMap(mType, dictionary of (mExpression, mExpression))
  or mvLiteral(mScalarLiteral)
  or mvVariable(mType, string)
  or mvReference(mType, string)
  or mvDot(mType, mExpression, string)
  -- or mvIndex(mType, mExpression, mExpression) -- to Support Slice operator
  or mvIndex(mType, mExpression, mSlice)
  or mvGet(mType, mExpression, mExpression)
  or mvQueryExpression(mSearchQuantifier, mExpression, mCondition, list of mModifier)
  or mvScalarExpn(mExpression, mScalarOp, mExpression)
  or mvScalarUnaryExpn(mUnaryScalarOp, mExpression)
  or mvConditionExpn(mCondition)
  or mvTicket(string) -- mvTicket(<attributeName>)
  or noneExpression;

type mSlice is mvSlice(mExpression)
  or mvSliceRange(mExpression, mExpression)
  or mvSliceLast
  or mvSliceToEnd(mExpression);

type mCollection is mvCollection{
   values has type dictionary of (string, mExpression);
   values default is dictionary of [];
   schema has type mSchema;
   schema default is noneSchema;
} or mvSRCollection{
  values has type dictionary of (string, mExpression); -- (<attributeName>, <StarRulesString representing its value>)
  values default is dictionary of [];
} or noneCollection;

type mScalarLiteral is mvInteger(mType, integer)
  or mvString(mType, string)
  or mvPassword(mType, string)
--  or mvDouble(mType, double)
  or mvFloat(mType, float)
  or mvLong(mType, long)
  or mvBoolean(boolean)
  or mvBinary(mType, any)
  or mvByteArray(mType, list of any)
  or mvCode(mFunction)
  or noneScalarLiteral;

type mFunction is mvFunSrc(mType,string,string) -- mFunSrc(type, language, file name)
 or mvFunction(mType, string, string) -- mFunction(type, language, src text)
 or mvFunctionName(mType, string); -- mFunction(type, java class name)
 

type mSearchQuantifier is mvAll or mvOne or mvIntegerOf(mExpression);

type mCondition is mvConj(mCondition, mCondition)
  or mvDisj(mCondition, mCondition)
  or mvOtherwise(mCondition, mCondition)
  or mvNegation(mCondition)
  or mvConditionalCon(mCondition, mCondition, mCondition)
  or mvRelCon(mExpression, mRelOp, mExpression)
  or mvInCon(mPtn, mExpression)
  or mvCurrent(mPtn, mExpression)
  or mvMapSearch(mPtn, mPtn, mExpression)
  or mvMatches(mExpression, mPtn);

type mRelOp is mvEqual or mvNotEqual or mvLess or mvGreater or mvLessEquals or mvGreaterEquals;

type mPtn is mvTuplePtn(mType, string, list of mPtn)
  or mvAggregatePtn(mType, string, dictionary of (string, mPtn))
  or mvLiteralPtn(mScalarLiteral)
  or mvWherePtn(mType, mPtn, mCondition)
  or mvMatchingPtn(mPtn, mPtn)
  or mvVariablePtn(mType, string)
  or mvRegExpPtn(string, list of mPtn);

type mModifier is mvGrpBy(mExpression)
        or mvSortBy(boolean, mExpression);


-- SA...

type mSpeechAction is mvNotify(mExpression, string)
  or mvQuery(mExpression)
  or mvRequest(mAction)
  or mvVolunteer(mSpeechActionPtn, mAction)
  or mvVolunteerWhere(mSpeechActionPtn, mCondition, mAction); -- To support optional 'where' clause

type mAction is mvSequence(list of mAction)
  or mvSpeech(mSpeechAction)
  or mvRelationInsertAction(mExpression, mExpression) -- list and tuple
  or mvRelationUpdateAction(mPtn, mCondition, mExpression, mExpression) -- pattern, condition, list and tuple
  or mvRelationDeleteAction(mPtn, mCondition, mExpression) -- pattern, condition and list
  or mvNamedAction(string, list of mExpression)
  or mvActionWhere(mAction, mCondition)
  or mvActionVar(string);

type mSpeechActionPtn is mvSpeechActionPtnVar(string)
  or mvNotifyPtn(mPtn, string) -- string = "" - matching notify without occurrence name
  or mvRequestPtn(mActionPtn)
  or mvQueryPtn(mCondition);
  -- or mvSpeechActionPtnWhere(mSpeechActionPtn, mCondition) -- supported at mSpeechAction level...

type mActionPtn is mvNamedPtn(string, list of mPtn)

type mScalarOp is mvPlus or mvMinus or mvTimes or mvDivide or mvRelOp(mRelOp);

type mUnaryScalarOp is mvUMinus or mvSqrt or mvAbs;

-- END:  meta-model for Types & Content Language (Speech Action)---


-- BEGIN: Model of Graph Elements -----------------------------

type mVisibility is mvPeer or mvGlobal or mvPrivate;

type mResource is mvTypeResource{
    id has type mIdType;
    rtp has type mTypeDescription;
  } or mvTypeResourceSRules{ -- to define any complex types or variables via starRules
    id has type mIdType;
    sruleScript has type mStarRulesCode;
  } or mvMetadataResource{
    id has type mIdType;
    md has type mMetadataAttribute;
  } or mvGenericResource {
    id has type mIdType;
    tp has type mType;
    name has type string;
    description has type string;
    value has type mExpression;
    value default is noneExpression;
  } or mvCatalog{
    id has type string;
    name has type string;
    description has type string;
    resources has type dictionary of (string, mResource); -- key is id of mResource
    resources default is dictionary of [];
  } or noneResource;

type mMetadataAttribute is mvMetadataAttribute {
  id has type mIdType;
  operation has type string;
  metaAttrName has type string;
  metaAttrType has type mType;
  cfHandlers has type dictionary of (string, mFunction) -- result type of mFunction=metaAttrType
  cfHandlers default is dictionary of [];
}

type mNotifyProcessor is mvStandardNotifyProcessor {
  streamHandlers has type list of ((string, mHandlerPurpose, mScalarLiteral));
                      -- example: {("streamName", mvRollbackHandler, mvCode(mvFunction(...)));
                      --           ("streamName", mvFailHandler, mvCode(mvFunction(...)));
                      --           ("streamName", mvDoHandler, mvCode(mvFunction(...)))};
} or mvCustomNotifyProcessor(mScalarLiteral)
  or noneNotifyProcessor;

type mRequestProcessor is mvStandardRequestProcessor {
  requestHandlers has type list of ((string, mHandlerPurpose, mScalarLiteral));
                      -- example: {("login", mvRollbackHandler, mvCode(mvFunction(...)));
                      --           ("login", mvFailHandler, mvCode(mvFunction(...)));
                      --           ("login", mvDoHandler, mvCode(mvFunction(...)))};
} or mvCustomRequestProcessor(mScalarLiteral)
  or noneRequestProcessor;

type mQueryProcessor is mvStandardQueryProcessor {
  queryHandlers has type list of ((string, mHandlerPurpose, mScalarLiteral));
                      -- example: {("schemaMember", mvRollbackHandler, mvCode(mvFunction(...)));
                      --           ("schemaMember", mvFailHandler, mvCode(mvFunction(...)));
                      --           ("schemaMember", mvDoHandler, mvCode(mvFunction(...)))};
} or mvCustomQueryProcessor(mScalarLiteral)
  or noneQueryProcessor;


type mModel is mvModelElement {
  id has type mIdType;
  modelName has type string;
  modelName default is "  ";
  modelVersion has type string;
  modelVersion default is "1.0";
  ontologyName has type string;
  ontologyName default is "Runtime Model";
  ontologyDescription has type string;
  ontologyDescription default is "Executable Model";
  ontologyVersion has type string;
  ontologyVersion default is "6.2.1";
  operation has type string;
  operation default is "Insert";
  category has type string;
  category default is "none"
  description has type string;
  description default is " Application model for ...";
  root has type mModel;
  resources has type list of ((mVisibility, mResource));  -- depricated... do not use it...
  resources default is list of [(mvPrivate, noneResource)];
  lastModifiedDate has type long;
  lastModifiedDate default is 0L;
} -- where isEmpty(all X where X in root.inputPorts and X.name!="iConfig" and X.name!="iManage") and isEmpty(root.outputPorts)
  -- Constraints on Type Definitions are not implemented yet...
 or mvComponent {
    id has type mIdType;
    operation has type string;
    operation default is "Insert";
    name has type string;
    name default is "compName";
    resourceClass has type string;
    resourceClass default is "...";
    resourceType has type string;
    resourceType default is "...";
    category has type string;
    category default is "none";
    attributes has type mCollection;
    attributes default is noneCollection;
    inputPorts has type dictionary of (string, mPort);
    inputPorts default is dictionary of [];
    outputPorts has type dictionary of (string, mPort);
    outputPorts default is dictionary of [];
    -- handlers has type dictionary of (string, mScalarLiteral); <handlers need to be associated to a Port>...
	resources has type list of ((mVisibility, mResource));
    resources default is list of [(mvPrivate, noneResource)];
    notes has type dictionary of (string, string);
    notes default is dictionary of [];
    lastModifiedDate has type long;
    lastModifiedDate default is 0L;
} or mvScriptableComponent {
    id has type mIdType;
    operation has type string;
    operation default is "Insert";
    name has type string;
    name default is "scrCompName";
    resourceClass has type string;
    resourceClass default is "...";
    resourceType has type string;
    resourceType default is "...";
    category has type string;
    category default is "none";
    attributes has type mCollection;
    attributes default is noneCollection;
    daemons has type dictionary of (string, mDaemon);
    daemons default is dictionary of [];
    inputPorts has type dictionary of (string, mPort);
    inputPorts default is dictionary of [];
    outputPorts has type dictionary of (string, mPort);
    outputPorts default is dictionary of [];
    -- handlers has type dictionary of (string, mScalarLiteral);   <handlers need to be associated to a Port>...
	resources has type list of ((mVisibility, mResource));
    resources default is list of [(mvPrivate, noneResource)];
    notes has type dictionary of (string, string);
    notes default is dictionary of [];
    lastModifiedDate has type long;
    lastModifiedDate default is 0L;
}
 or mvCompositeComponent {
    id has type mIdType;
    operation has type string;
    operation default is "Insert";
    name has type string;
    name default is "CCName";
    resourceClass has type string;
    resourceClass default is "Composite";
    resourceType has type string;
    resourceType default is "Composite";
    category has type string;
    category default is "none";
    attributes has type mCollection;
    attributes default is noneCollection;
    inputPorts has type dictionary of (string, mPort);
    inputPorts default is dictionary of [];
    outputPorts has type dictionary of (string, mPort);
    outputPorts default is dictionary of [];
    -- handlers has type dictionary of (string, mScalarLiteral);     <handlers need to be associated to a Port>...
	resources has type list of ((mVisibility, mResource));
    resources default is list of [(mvPrivate, noneResource)];
    connections has type list of ((mIdType, mIdType, list of mConnectionContract));
    connections default is list of [];
    subComponents has type dictionary of (string, mModel);
    subComponents default is dictionary of [];
    notes has type dictionary of (string, string);
    notes default is dictionary of [];
    lastModifiedDate has type long;
    lastModifiedDate default is 0L;
} or nullModel;

type mPort is mvPort {
  id has type mIdType;
  operation has type string;
  operation default is "Insert";
  name has type string;
  name default is "portName";
  category has type string;
  category default is "none";
  attributes has type mCollection;
  attributes default is noneCollection;
  belongsTo has type string; -- ID of parent Component
  portSchema has type mSchema;
  portSchema default is noneSchema;
  isMalleableSchema has type boolean;
  isMalleableSchema default is false;
  handlers has type list of ((string, mHandlerClass, mHandlerPurpose, mScalarLiteral));  -- list[Name/Label, mvNotifyHandler, mvRollbackHandler, <handlerCode/file>]
  handlers default is list of [];
  notifyProcessor has type mNotifyProcessor;
  notifyProcessor default is noneNotifyProcessor;
  requestProcessor has type mRequestProcessor;
  requestProcessor default is noneRequestProcessor;
  queryProcessor has type mQueryProcessor;
  queryProcessor default is noneQueryProcessor;

  notes has type dictionary of (string, string);
  notes default is dictionary of [];
  lastModifiedDate has type long;
  lastModifiedDate default is 0L;
 };

type mAdapter is alias of mModel;

type mNotes is alias of dictionary of (string, string);

type mConnectionContract
  is mvContract(mSpeechAction, mQos, mNotes)
  or mvContractStr(mSAString, mQos, mNotes);

type mIdType is alias of string;

-- PavelK: It is unusable. We use now TypeSystem.starRulesCodeType
type mStarRulesCode is alias of string;

type mSAString is alias of string;

type mJavaCode is alias of string;

type mXML is alias of string;

type mQos is alias of string;  -- "voting" / "nonVoting" member of the transaction

type mHandlerClass 	is noneHandlerClass
					or mvNotifyHandler 
					or mvQueryHandler 
					or mvRequestHandler  
                	or mvDDPrepareHandler 
					or mvDDCanHandlePortsPausingHandler
					or mvDDPausePortsHandler
					or mvDDExecuteHandler 
					or mvDDResumePortsHandler
					or mvDDPostExecuteHandler;

type mHandlerPurpose is noneHandlerPurpose
					or mvDoHandler 
					or mvRollbackHandler 
					or mvFailHandler
					or mvDDHandler;

type mDaemonType is mvFixedRate or mvFixedDelay;

type mUnitOfTime is mvMilliSeconds or mvMicroSeconds or mvSeconds or mvMinutes or mvHours;


type mDaemon is mvTimerDaemon {
  id has type mIdType;
  name has type string;
  name default is "tdName";
  daemonType has type mDaemonType;
  prepareHandler has type mScalarLiteral;
  prepareHandler default is noneScalarLiteral;
  doHandler has type mScalarLiteral;
  doHandler default is noneScalarLiteral;
  postDoHandler has type mScalarLiteral;
  postDoHandler default is noneScalarLiteral;
  initialDelay has type long;
  initialDelay default is 0L;
  delay has type long;
  period has type long;
  unitOfTime has type mUnitOfTime;
  stopOnError has type boolean;
  notes has type dictionary of (string, string);
  notes default is dictionary of [];
  lastModifiedDate has type long;
  lastModifiedDate default is 0L;
} or mvListenerDaemon {
  id has type mIdType;
  name has type string;
  name default is "ldName";
  prepareHandler has type mScalarLiteral;
  prepareHandler default is noneScalarLiteral;
  doHandler has type mScalarLiteral;
  doHandler default is noneScalarLiteral
  lastModifiedDate has type long;
  lastModifiedDate default is 0L;
};
}
