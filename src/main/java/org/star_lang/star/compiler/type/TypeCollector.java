package org.star_lang.star.compiler.type;

import java.util.Map;
import java.util.Map.Entry;

import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.canonical.Variable;
import org.star_lang.star.compiler.transform.Over;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.compiler.util.StringUtils;

import com.starview.platform.data.type.ContractConstraint;
import com.starview.platform.data.type.IType;
import com.starview.platform.data.type.ITypeConstraint;
import com.starview.platform.data.type.Location;
import com.starview.platform.data.type.TypeConstraintException;
import com.starview.platform.data.type.TypeExists;
import com.starview.platform.data.type.TypeVar;

public class TypeCollector implements ITypeCollector
{
  private final Map<String, IType> elements;
  private final Map<String, IType> exists;
  private final ErrorReport errors;
  private final Dictionary dict;
  private final ITypeCollector parent;

  public TypeCollector(Map<String, IType> elements, Map<String, IType> types, ErrorReport errors, Dictionary dict,
      ITypeCollector parent)
  {
    this.elements = elements;
    this.exists = types;
    this.errors = errors;
    this.dict = dict;
    this.parent = parent;
  }

  @Override
  public void fieldAnnotation(Location loc, String name, IType type)
  {
    elements.put(name, type);
    dict.declareVar(name, new Variable(loc, type, name), AccessMode.readOnly, Visibility.priVate, true);
    parent.fieldAnnotation(loc, name, type);
  }

  @Override
  public void kindAnnotation(Location loc, String name, IType type)
  {
    IType t = exists.get(name);
    if (t == null) {
      exists.put(name, type);
      dict.defineType(new TypeExists(loc, name, type));
    } else {
      assert t instanceof TypeVar;
      try {
        Subsume.subsume(t, type, loc, dict);
      } catch (TypeConstraintException e) {
        errors.reportError(StringUtils.msg("kind of ", name, " already declared to be ", t.kind()));
      }
    }
    parent.kindAnnotation(loc, name, type);
  }

  @Override
  public void completeInterface(Location loc)
  {
    for (Entry<String, IType> e : exists.entrySet()) {
      if (e.getValue() instanceof TypeVar) {
        TypeVar v = (TypeVar) e.getValue();
        for (ITypeConstraint con : v) {
          if (con instanceof ContractConstraint) {
            ContractConstraint contract = (ContractConstraint) con;
            String instanceName = Over.instanceFunName(contract.getContract());
            IType instanceType = Over.computeDictionaryType(contract.getContract(), loc, AccessMode.readWrite);
            fieldAnnotation(loc, instanceName, instanceType);
          }
        }
      }
    }
  }
}