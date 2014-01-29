package org.star_lang.star.compiler.canonical;

/**
 * 
 * Copyright (C) 2013 Starview Inc
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
public interface TransformCondition<A, E, P, C, D, T>
{
  C transformConditionCondition(ConditionCondition conditionCondition, T context);

  C transformConjunction(Conjunction conjunction, T context);

  C transformDisjunction(Disjunction disjunction, T context);

  C transformFalseCondition(FalseCondition falseCondition, T context);

  C transformImplies(Implies implies, T context);

  C transformIsTrue(IsTrue i, T context);

  C transformListSearch(ListSearch ptn, T context);

  C transformMatches(Matches matches, T context);

  C transformNegation(Negation negation, T context);

  C transformOtherwise(Otherwise otherwise, T context);

  C transformSearch(Search predication, T context);

  C transformTrueCondition(TrueCondition trueCondition, T context);
}
