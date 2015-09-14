package org.star_lang.star.compiler.cafe.type;

import java.util.SortedMap;

import org.star_lang.star.compiler.type.DisplayType;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.RecordSpecifier;

@SuppressWarnings("serial")
public class CafeRecordSpecifier extends RecordSpecifier
{
  public CafeRecordSpecifier(Location loc, String label, int conIx, String javaClassName, String javaOwner,
      String javaConSig, String javaSafeName, SortedMap<String, Integer> index, IType conType)
  {
    super(loc, label, conIx, conType, javaClassName, javaConSig, javaOwner, javaSafeName);
  }

  public CafeRecordSpecifier(Location loc, String name, int conIx, SortedMap<String, Integer> index, IType conType)
  {
    super(loc, name, conIx, conType, null, null, null, null);
  }

  public CafeRecordSpecifier(Location loc, String label, String javaSafeName, int conIx,
      SortedMap<String, Integer> index, IType conType, Class<?> cafeClass, Class<?> cafeOwner)
  {
    super(loc, label, conIx, null, conType, cafeClass, cafeOwner);
  }

  @Override
  public ICafeConstructorSpecifier cleanCopy()
  {
    return new CafeRecordSpecifier(getLoc(), getLabel(), getConIx(), getJavaClassName(), getJavaOwner(),
        getJavaConSig(), getJavaSafeName(), getIndex(), getConType());
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.appendId(getLabel());
    DisplayType.display(disp, TypeUtils.getConstructorArgType(TypeUtils.unwrap(getConType())));
  }
}
