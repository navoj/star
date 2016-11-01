package org.star_lang.star.compiler.cafe.compile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.star_lang.star.code.repository.CodeCatalog;
import org.star_lang.star.code.repository.CodeRepository;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.ast.AApply;
import org.star_lang.star.compiler.ast.DefaultAbstractVisitor;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.ast.Name;
import org.star_lang.star.compiler.cafe.CafeSyntax;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.compiler.util.TopologySort;
import org.star_lang.star.compiler.util.TopologySort.IDefinition;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.Location;

/**
 *
 * Copyright (c) 2015. Francis G. McCabe
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

public class Dependencies
{

  public static List<List<IAbstract>> dependencySort(CodeRepository repository, List<IAbstract> defs, Location loc,
      CodeCatalog bldCat, ErrorReport errors)
  {
    Set<String> allList = pickList(repository, defs, loc, bldCat, errors);
    List<ThetaDefn> initGroup = new ArrayList<>();
    for (IAbstract def : defs)
      initGroup.add(new ThetaDefn(def, pickList(repository, def, loc, bldCat, errors), allList));
    List<List<IDefinition<String>>> groups = TopologySort.sort(initGroup);
    List<List<IAbstract>> sortd = new ArrayList<>();
    for (List<IDefinition<String>> entry : groups) {
      List<IAbstract> group = new ArrayList<>();
      for (IDefinition<String> def : entry)
        group.add(((ThetaDefn) def).defn);
      sortd.add(group);
    }

    return sortd;
  }

  private static Set<String> pickList(CodeRepository repository, List<IAbstract> definitions, Location loc,
      CodeCatalog bldCat, ErrorReport errors)
  {
    Set<String> pick = new HashSet<>();
    for (IAbstract def : definitions) {
      defined(pick, def);
    }
    return pick;
  }

  private static Set<String> pickList(CodeRepository repository, IAbstract def, Location loc, CodeCatalog bldCat,
      ErrorReport errors)
  {
    Set<String> pick = new HashSet<>();

    defined(pick, def);

    return pick;
  }

  private static void defined(Set<String> pick, IAbstract def)
  {
    if (CafeSyntax.isTypeDef(def)) {
      pick.add(CafeSyntax.typeDefName(def));
    } else if (CafeSyntax.isFunctionDefn(def)) {
      pick.add(CafeSyntax.definedFunctionName(def));
    } else if (CafeSyntax.isVarDeclaration(def)) {
      for (String v : Patterns.declaredVars(CafeSyntax.varDeclLval(def)))
        pick.add(v);
    } else if (CafeSyntax.isIsDeclaration(def)) {
      for (String v : Patterns.declaredVars(CafeSyntax.isDeclLval(def)))
        pick.add(v);
    }
  }

  private static Collection<String> allReferences(IAbstract defn, Set<String> defs, Set<String> pickList)
  {
    ReferenceFinder finder = new ReferenceFinder(defs, pickList);
    if (CafeSyntax.isIsDeclaration(defn))
      defn = CafeSyntax.isDeclValue(defn);
    else if (CafeSyntax.isVarDeclaration(defn))
      defn = CafeSyntax.varDeclValue(defn);

    defn.accept(finder);
    return finder.references;
  }

  private static class ReferenceFinder extends DefaultAbstractVisitor
  {
    private final Collection<String> pickList;
    private final Collection<String> references = new HashSet<>();
    private final Collection<String> excludes = new HashSet<>();

    ReferenceFinder(Set<String> defined, Set<String> pickList)
    {
      this.pickList = pickList;
      this.excludes.addAll(defined);
    }

    @Override
    public void visitName(Name name)
    {
      String n = name.getId();
      if (pickList.contains(n) && !excludes.contains(n))
        references.add(n);
    }

    @Override
    public void visitApply(AApply app)
    {
      if (CafeSyntax.isVarDeclaration(app)) {
        addVarToExcludes(CafeSyntax.varDeclLval(app));
        CafeSyntax.varDeclValue(app).accept(this);
      } else if (CafeSyntax.isIsDeclaration(app)) {
        addVarToExcludes(CafeSyntax.isDeclLval(app));
        CafeSyntax.isDeclValue(app).accept(this);
      } else if (CafeSyntax.isDot(app))
        CafeSyntax.dotRecord(app).accept(this);
      else if (CafeSyntax.isFunCall(app)) {
        CafeSyntax.funCallOperator(app).accept(this);
        CafeSyntax.funCallArgs(app).accept(this);
      } else if (CafeSyntax.isEscape(app)) {
        for (IValue arg : CafeSyntax.escapeArgs(app))
          ((IAbstract) arg).accept(this);
      } else if (CafeSyntax.isConstructor(app)) {
        CafeSyntax.constructorName(app).accept(this);
        for (IValue arg : CafeSyntax.constructorArgs(app))
          ((IAbstract) arg).accept(this);
      } else if (CafeSyntax.isRecord(app)) {
        CafeSyntax.recordLbl(app).accept(this);
        for (IValue arg : CafeSyntax.recordArgs(app)) {
          IAbstract el = (IAbstract) arg;
          if (CafeSyntax.isField(el))
            CafeSyntax.fieldValue(el).accept(this);
        }
      } else if (!CafeSyntax.isImport(app))
        super.visitApply(app);
    }

    private void addVarToExcludes(IAbstract ptn)
    {
      if (CafeSyntax.isTypedTerm(ptn))
        addVarToExcludes(CafeSyntax.typedTerm(ptn));
      else if (ptn instanceof Name)
        excludes.add(Abstract.getId(ptn));
    }
  }

  @SuppressWarnings("serial")
  private static class ThetaDefn implements IDefinition<String>, PrettyPrintable
  {
    private final IAbstract defn;

    private final Set<String> defs;
    private final Collection<String> references;

    public ThetaDefn(IAbstract defn, Set<String> defs, Set<String> allList)
    {
      this.defn = defn;
      this.defs = defs;
      this.references = allReferences(defn, this.defs, allList);
    }

    @Override
    public boolean defines(String obj)
    {
      return defs.contains(obj);
    }

    @Override
    public Collection<String> definitions()
    {
      return defs;
    }

    @Override
    public Collection<String> references()
    {
      return references;
    }

    @Override
    public void prettyPrint(PrettyPrintDisplay disp)
    {
      disp.append("[");
      disp.appendWords(defs, ", ");
      disp.append("]:{");
      disp.appendWords(references, ", ");
      disp.append("}");
    }

    @Override
    public String toString()
    {
      return PrettyPrintDisplay.toString(this);
    }
  }
}
