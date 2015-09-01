package org.star_lang.star.compiler.transform;

import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.canonical.*;
import org.star_lang.star.compiler.type.Dictionary;
import org.star_lang.star.compiler.util.Pair;
import org.star_lang.star.data.type.IType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;


/*
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

public class ActionTransformer extends ExpressionTransformer {
  private ActionTransformer(Dictionary dict) {
    super(dict);

    // Actions
    install(new AssertTransform());
    install(new Assignmentransform());
    install(new CaseActionTransform());
    install(new ConditionalActionTransform());
    install(new ExceptionHandlerTransform());
    install(new IgnoreTransform());
    install(new LetActionTransform());
    install(new LoopActionTransform());
    install(new NullActionTransform());
    install(new ProcedureCallTransform());
    install(new SequenceTransform());
    install(new ValisTransform());
    install(new VarDeclarationTransform());
    install(new RaiseTransform());
  }

  public static IContentAction transformValis(IContentAction action, IType returnType, Dictionary dict,
                                              ErrorReport errors) {
    ActionTransformer transformer = new ActionTransformer(dict);
    return transformer.transform(action);
  }

  // Most of these transformers do not 'go into' the actions

  private class AssertTransform implements TransformAction {
    @Override
    public IContentAction transformAction(IContentAction act) {
      return act;
    }

    @Override
    public Class<? extends IContentAction> transformClass() {
      return AssertAction.class;
    }
  }

  private class Assignmentransform implements TransformAction {
    @Override
    public IContentAction transformAction(IContentAction act) {
      return act;
    }

    @Override
    public Class<? extends IContentAction> transformClass() {
      return Assignment.class;
    }
  }

  private class CaseActionTransform implements TransformAction {
    @Override
    public IContentAction transformAction(IContentAction act) {
      CaseAction cA = (CaseAction) act;
      List<Pair<IContentPattern, IContentAction>> cases = new ArrayList<>();
      for (Pair<IContentPattern, IContentAction> entry : cA.getCases()) {
        cases.add(Pair.pair(entry.getKey(), transform(entry.getValue())));
      }
      return new CaseAction(act.getLoc(), cA.getSelector(), cases, transform(cA.getDeflt()));
    }

    @Override
    public Class<? extends IContentAction> transformClass() {
      return CaseAction.class;
    }
  }

  private class ConditionalActionTransform implements TransformAction {
    @Override
    public IContentAction transformAction(IContentAction act) {
      ConditionalAction cnd = (ConditionalAction) act;
      return new ConditionalAction(act.getLoc(), cnd.getCond(), transform(cnd.getThPart()), transform(cnd.getElPart()));
    }

    @Override
    public Class<? extends IContentAction> transformClass() {
      return ConditionalAction.class;
    }
  }

  private class ExceptionHandlerTransform implements TransformAction {

    @Override
    public IContentAction transformAction(IContentAction act) {
      ExceptionHandler except = (ExceptionHandler) act;

      return new ExceptionHandler(act.getLoc(), transform(except.getBody()), transform(except.getHandler()));
    }

    @Override
    public Class<? extends IContentAction> transformClass() {
      return ExceptionHandler.class;
    }
  }

  private class IgnoreTransform implements TransformAction {
    @Override
    public IContentAction transformAction(IContentAction act) {
      return act;
    }

    @Override
    public Class<? extends IContentAction> transformClass() {
      return Ignore.class;
    }
  }

  private class LetActionTransform implements TransformAction {
    @Override
    public IContentAction transformAction(IContentAction act) {
      LetAction let = (LetAction) act;

      return new LetAction(act.getLoc(), let.getEnvironment(), transform(let.getBoundAction()));
    }

    @Override
    public Class<? extends IContentAction> transformClass() {
      return LetAction.class;
    }
  }

  private class LoopActionTransform implements TransformAction {
    @Override
    public IContentAction transformAction(IContentAction act) {
      WhileAction loop = (WhileAction) act;
      return new WhileAction(act.getLoc(), transform(loop.getControl()), transform(loop.getBody()));
    }

    @Override
    public Class<? extends IContentAction> transformClass() {
      return WhileAction.class;
    }
  }

  private class NullActionTransform implements TransformAction {
    @Override
    public IContentAction transformAction(IContentAction act) {
      return act;
    }

    @Override
    public Class<? extends IContentAction> transformClass() {
      return NullAction.class;
    }
  }

  private class ProcedureCallTransform implements TransformAction {
    @Override
    public IContentAction transformAction(IContentAction act) {
      return act;
    }

    @Override
    public Class<? extends IContentAction> transformClass() {
      return ProcedureCallAction.class;
    }
  }

  private class RaiseTransform implements TransformAction {
    @Override
    public IContentAction transformAction(IContentAction act) {
      RaiseAction raise = (RaiseAction) act;
      IContentExpression value = raise.getRaised();
      ConstructorTerm noMore = CompilerUtils.abortIter(act.getLoc(), act.getType(), value);
      return new ValisAction(act.getLoc(), noMore);
    }

    @Override
    public Class<? extends IContentAction> transformClass() {
      return RaiseAction.class;
    }
  }

  private class SequenceTransform implements TransformAction {
    @Override
    public IContentAction transformAction(IContentAction act) {
      Sequence seq = (Sequence) act;
      List<IContentAction> lst = seq.getActions().stream().map(ActionTransformer.this::transform).collect(Collectors.toList());
      return new Sequence(act.getLoc(), act.getType(), lst);
    }

    @Override
    public Class<? extends IContentAction> transformClass() {
      return Sequence.class;
    }
  }

  private class ValisTransform implements TransformAction {
    @Override
    public IContentAction transformAction(IContentAction act) {
      ValisAction valis = (ValisAction) act;
      IContentExpression value = valis.getValue();
      ConstructorTerm noMore = CompilerUtils.noMore(act.getLoc(), value);
      return new ValisAction(act.getLoc(), noMore);
    }

    @Override
    public Class<? extends IContentAction> transformClass() {
      return ValisAction.class;
    }
  }

  private class VarDeclarationTransform implements TransformAction {
    @Override
    public IContentAction transformAction(IContentAction act) {
      return act;
    }

    @Override
    public Class<? extends IContentAction> transformClass() {
      return VarDeclaration.class;
    }
  }

}
