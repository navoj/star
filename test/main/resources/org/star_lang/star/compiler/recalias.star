recalias is package {

type GFChamber is alias of GFTool;

type GFTool is alias of GFRecipe;

type GFProcSpec is
     GFProcSpecChamber(GFChamber, GFRecipe);

type GFRecipe is GFRecipe{
    spec has type GFProcSpec;
};

type GFComponentClass is GFComponentClass{
  chooseTrackInEntry has type GFChamber;
};

}