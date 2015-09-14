package org.star_lang.star.compiler.format;

import java.util.Map;
import java.util.Map.Entry;

import org.star_lang.star.compiler.ast.IAttribute;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.data.type.Location;

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
public class FormatPolicy
{
  final int indentPolicy;
  final int blankLinePolicy;
  final int lineCommentColumnPolicy;
  final boolean commentWrap;
  final int commentWrapColumn;
  final boolean breakBefore;
  final boolean breakAfter;
  final String breakAfterToken;
  final int start;
  final int end;

  FormatPolicy(Location loc, int indentPolicy, int blankLinePolicy, int lineCommentColumnPolicy, boolean commentWrap,
      int commentWrapColumn, boolean breakBefore, boolean breakAfter, String breakAfterToken)
  {
    this(loc.getCharCnt(), loc.getCharCnt() + loc.getLen(), indentPolicy, blankLinePolicy, lineCommentColumnPolicy,
        commentWrap, commentWrapColumn, breakBefore, breakAfter, breakAfterToken);
  }

  FormatPolicy(int start, int end, int indentPolicy, int blankLinePolicy, int lineCommentColumnPolicy,
      boolean commentWrap, int commentWrapColumn, boolean breakBefore, boolean breakAfter, String breakAfterToken)
  {
    this.indentPolicy = indentPolicy;
    this.blankLinePolicy = blankLinePolicy;
    this.lineCommentColumnPolicy = lineCommentColumnPolicy;
    this.commentWrap = commentWrap;
    this.commentWrapColumn = commentWrapColumn;
    this.breakBefore = breakBefore;
    this.breakAfter = breakAfter;
    this.breakAfterToken = breakAfterToken;
    this.start = start;
    this.end = end;
  }

  FormatPolicy(int start, int end)
  {
    this(start, end, 0, 1, 50, false, 80, false, false, null);
  }

  FormatPolicy adjustPolicies(Location loc, Map<String, IAttribute> attributes)
  {
    int indent = indentPolicy;
    int blankLine = blankLinePolicy;
    int lineComment = lineCommentColumnPolicy;
    boolean wrap = commentWrap;
    int columnWrap = commentWrapColumn;
    boolean before = breakBefore;
    boolean after = breakAfter;

    for (Entry<String, IAttribute> entry : attributes.entrySet()) {
      String att = entry.getKey();
      switch (att) {
        case StandardNames.FMT_INDENT:
          indent = ((NumericAttribute) entry.getValue()).attribute(indentPolicy);
          break;
        case StandardNames.FMT_LINES:
          blankLine = ((NumericAttribute) entry.getValue()).attribute(blankLinePolicy);
          break;
        case StandardNames.FMT_COMMENT_COLUMN:
          lineComment = ((NumericAttribute) entry.getValue()).attribute(lineCommentColumnPolicy);
          break;
        case StandardNames.FMT_COMMENT_WRAP:
          wrap = ((BooleanAttribute) entry.getValue()).attribute(commentWrap);
          break;
        case StandardNames.FMT_WRAP_COLUMN:
          columnWrap = ((NumericAttribute) entry.getValue()).attribute(commentWrapColumn);
          break;
        case StandardNames.FMT_BREAK_BEFORE:
          before = ((BooleanAttribute) entry.getValue()).attribute(before);
          break;
        case StandardNames.FMT_BREAK_AFTER:
          after = ((BooleanAttribute) entry.getValue()).attribute(after);
          break;
      }
    }
    return new FormatPolicy(loc, indent, blankLine, lineComment, wrap, columnWrap, before, after, breakAfterToken);
  }

  public FormatPolicy adjustPolicies(int start, Integer end, Map<String, IAttribute> atts)
  {
    int indent = indentPolicy;
    int blankLine = blankLinePolicy;
    int lineComment = lineCommentColumnPolicy;
    boolean wrap = commentWrap;
    int columnWrap = commentWrapColumn;
    boolean before = false;
    boolean after = false;
    String breakAfter = null;

    for (Entry<String, IAttribute> entry : atts.entrySet()) {
      String key = entry.getKey();

      switch (key) {
      case StandardNames.FMT_INDENT:
        indent = ((NumericAttribute) entry.getValue()).attribute(indentPolicy);
        break;
      case StandardNames.FMT_LINES:
        blankLine = ((NumericAttribute) entry.getValue()).attribute(blankLinePolicy);
        break;
      case StandardNames.FMT_COMMENT_COLUMN:
        lineComment = ((NumericAttribute) entry.getValue()).attribute(lineCommentColumnPolicy);
        break;
      case StandardNames.FMT_COMMENT_WRAP:
        wrap = ((BooleanAttribute) entry.getValue()).attribute(commentWrap);
        break;
      case StandardNames.FMT_WRAP_COLUMN:
        columnWrap = ((NumericAttribute) entry.getValue()).attribute(commentWrapColumn);
        break;
      case StandardNames.FMT_BREAK_BEFORE:
        before = ((BooleanAttribute) entry.getValue()).attribute(before);
        break;

      case StandardNames.FMT_BREAK_AFTER: {
        IAttribute att = entry.getValue();
        if (att instanceof StringAttribute) {
          breakAfter = ((StringAttribute) att).getStr();
          after = true;
        } else if (att instanceof BooleanAttribute)
          after = ((BooleanAttribute) att).attribute(after);
      }
      }
    }
    return new FormatPolicy(start, end, indent, blankLine, lineComment, wrap, columnWrap, before, after, breakAfter);
  }

  boolean expiredLocation(int ix)
  {
    return end <= ix;
  }

  @Override
  public String toString()
  {
    return "{ start= " + start + ", end= " + end + ", indent=" + indentPolicy + ", lines=" + blankLinePolicy
        + ", breakBefore: " + breakBefore + ", breakAfter: " + breakAfter
        + (breakAfterToken != null ? ", breakAfterToken=" + breakAfterToken : "") + "}";
  }

}