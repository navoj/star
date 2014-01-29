package org.star_lang.star.operators.resource;

import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.resource.runtime.ResourceOps.GetResource;
import org.star_lang.star.operators.resource.runtime.ResourceOps.PutResource;

public class ResourceOps
{
  public static void declare(Intrinsics cxt)
  {
    cxt.declareBuiltin(new Builtin(GetResource.name, GetResource.type(), GetResource.class));
    cxt.declareBuiltin(new Builtin(PutResource.name, PutResource.type(), PutResource.class));
  }
}
