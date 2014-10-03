package org.star_lang.star.compiler.transform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.canonical.AssertAction;
import org.star_lang.star.compiler.canonical.Assignment;
import org.star_lang.star.compiler.canonical.CaseAction;
import org.star_lang.star.compiler.canonical.ConditionalAction;
import org.star_lang.star.compiler.canonical.ConstructorTerm;
import org.star_lang.star.compiler.canonical.ExceptionHandler;
import org.star_lang.star.compiler.canonical.ExpressionTransformer;
import org.star_lang.star.compiler.canonical.ICondition;
import org.star_lang.star.compiler.canonical.IContentAction;
import org.star_lang.star.compiler.canonical.IContentExpression;
import org.star_lang.star.compiler.canonical.IContentPattern;
import org.star_lang.star.compiler.canonical.Ignore;
import org.star_lang.star.compiler.canonical.LetAction;
import org.star_lang.star.compiler.canonical.NullAction;
import org.star_lang.star.compiler.canonical.ProcedureCallAction;
import org.star_lang.star.compiler.canonical.RaiseAction;
import org.star_lang.star.compiler.canonical.Sequence;
import org.star_lang.star.compiler.canonical.SyncAction;
import org.star_lang.star.compiler.canonical.ValisAction;
import org.star_lang.star.compiler.canonical.VarDeclaration;
import org.star_lang.star.compiler.canonical.WhileAction;
import org.star_lang.star.compiler.type.Dictionary;
import org.star_lang.star.compiler.util.Pair;
import org.star_lang.star.data.type.IType;
/**
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA
 * 
 * @author fgm
 *
 */

public class ActionTransformer extends ExpressionTransformer
{
  private ActionTransformer(IType returnType, Dictionary dict, ErrorReport errors)
  {
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
    install(new SyncActionTransform());
    install(new ValisTransform());
    install(new VarDeclarationTransform());
    install(new RaiseTransform());
  }

  public static IContentAction transformValis(IContentAction action, IType returnType, Dictionary dict,
      ErrorReport errors)
  {
    ActionTransformer transformer = new ActionTransformer(returnType, dict, errors);
    return transformer.transform(action);
  }

  // Most of these transformers do not 'go into' the actions

  private class AssertTransform implements TransformAction
  {
    @Override
    public IContentAction transformAction(IContentAction act)
    {
      return act;
    }

    @Override
    public Class<? extends IContentAction> transformClass()
    {
      return AssertAction.class;
    }
  }

  private class Assignmentransform implements TransformAction
  {
    @Override
    public IContentAction transformAction(IContentAction act)
    {
      return act;
    }

    @Override
    public Class<? extends IContentAction> transformClass()
    {
      return Assignment.class;
    }
  }

  private class CaseActionTransform implements TransformAction
  {
    @Override
    public IContentAction transformAction(IContentAction act)
    {
      CaseAction cA = (CaseAction) act;
      List<Pair<IContentPattern, IContentAction>> cases = new ArrayList<Pair<IContentPattern, IContentAction>>();
      for (Pair<IContentPattern, IContentAction> entry : cA.getCases()) {
        cases.add(Pair.pair(entry.getKey(), transform(entry.getValue())));
      }
      return new CaseAction(act.getLoc(), cA.getSelector(), cases, transform(cA.getDeflt()));
    }

    @Override
    public Class<? extends IContentAction> transformClass()
    {
      return CaseAction.class;
    }
  }

  private class ConditionalActionTransform implements TransformAction
  {
    @Override
    public IContentAction transformAction(IContentAction act)
    {
      ConditionalAction cnd = (ConditionalAction) act;
      return new ConditionalAction(act.getLoc(), cnd.getCond(), transform(cnd.getThPart()), transform(cnd.getElPart()));
    }

    @Override
    public Class<? extends IContentAction> transformClass()
    {
      return ConditionalAction.class;
    }
  }

  private class ExceptionHandlerTransform implements TransformAction
  {

    @Override
    public IContentAction transformAction(IContentAction act)
    {
      ExceptionHandler except = (ExceptionHandler) act;

      return new ExceptionHandler(act.getLoc(), transform(except.getBody()), transform(except.getHandler()));
    }

    @Override
    public Class<? extends IContentAction> transformClass()
    {
      return ExceptionHandler.class;
    }
  }

  private class IgnoreTransform implements TransformAction
  {
    @Override
    public IContentAction transformAction(IContentAction act)
    {
      return act;
    }

    @Override
    public Class<? extends IContentAction> transformClass()
    {
      return Ignore.class;
    }
  }

  private class LetActionTransform implements TransformAction
  {
    @Override
    public IContentAction transformAction(IContentAction act)
    {
      LetAction let = (LetAction) act;

      return new LetAction(act.getLoc(), let.getEnvironment(), transform(let.getBoundAction()));
    }

    @Override
    public Class<? extends IContentAction> transformClass()
    {
      return LetAction.class;
    }
  }

  private class LoopActionTransform implements TransformAction
  {
    @Override
    public IContentAction transformAction(IContentAction act)
    {
      WhileAction loop = (WhileAction) act;
      return new WhileAction(act.getLoc(), transform(loop.getControl()), transform(loop.getBody()));
    }

    @Override
    public Class<? extends IContentAction> transformClass()
    {
      return WhileAction.class;
    }
  }

  private class NullActionTransform implements TransformAction
  {
    @Override
    public IContentAction transformAction(IContentAction act)
    {
      return act;
    }

    @Override
    public Class<? extends IContentAction> transformClass()
    {
      return NullAction.class;
    }
  }

  private class ProcedureCallTransform implements TransformAction
  {
    @Override
    public IContentAction transformAction(IContentAction act)
    {
      return act;
    }

    @Override
    public Class<? extends IContentAction> transformClass()
    {
      return ProcedureCallAction.class;
    }
  }

  private class RaiseTransform implements TransformAction
  {
    @Override
    public IContentAction transformAction(IContentAction act)
    {
      RaiseAction raise = (RaiseAction) act;
      IContentExpression value = raise.getRaised();
      ConstructorTerm noMore = CompilerUtils.abortIter(act.getLoc(), act.getType(), value);
      return new ValisAction(act.getLoc(), noMore);
    }

    @Override
    public Class<? extends IContentAction> transformClass()
    {
      return RaiseAction.class;
    }
  }

  private class SequenceTransform implements TransformAction
  {
    @Override
    public IContentAction transformAction(IContentAction act)
    {
      Sequence seq = (Sequence) act;
      List<IContentAction> lst = new ArrayList<IContentAction>();
      for (IContentAction acts : seq.getActions())
        lst.add(transform(acts));
      return new Sequence(act.getLoc(), act.getType(), lst);
    }

    @Override
    public Class<? extends IContentAction> transformClass()
    {
      return Sequence.class;
    }
  }

  private class SyncActionTransform implements TransformAction
  {

    @Override
    public IContentAction transformAction(IContentAction act)
    {
      SyncAction sync = (SyncAction) act;
      IContentExpression sel = sync.getSel();
      Map<ICondition, IContentAction> conditions = new HashMap<ICondition, IContentAction>();
      for (Entry<ICondition, IContentAction> entry : sync.getBody().entrySet()) {
        conditions.put(entry.getKey(), transform(entry.getValue()));
      }
      return new SyncAction(sync.getLoc(), act.getType(), sel, conditions);
    }

    @Override
    public Class<? extends IContentAction> transformClass()
    {
      return SyncAction.class;
    }
  }

  private class ValisTransform implements TransformAction
  {
    @Override
    public IContentAction transformAction(IContentAction act)
    {
      ValisAction valis = (ValisAction) act;
      IContentExpression value = valis.getValue();
      ConstructorTerm noMore = CompilerUtils.noMore(act.getLoc(), value);
      return new ValisAction(act.getLoc(), noMore);
    }

    @Override
    public Class<? extends IContentAction> transformClass()
    {
      return ValisAction.class;
    }
  }

  private class VarDeclarationTransform implements TransformAction
  {
    @Override
    public IContentAction transformAction(IContentAction act)
    {
      return act;
    }

    @Override
    public Class<? extends IContentAction> transformClass()
    {
      return VarDeclaration.class;
    }
  }

}
