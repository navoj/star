package org.star_lang.star.compiler.format;

import java.util.Map;
import java.util.Map.Entry;

import org.star_lang.star.compiler.ast.IAttribute;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.data.type.Location;

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
      if (att.equals(StandardNames.FMT_INDENT))
        indent = ((NumericAttribute) entry.getValue()).attribute(indentPolicy);
      else if (att.equals(StandardNames.FMT_LINES))
        blankLine = ((NumericAttribute) entry.getValue()).attribute(blankLinePolicy);
      else if (att.equals(StandardNames.FMT_COMMENT_COLUMN))
        lineComment = ((NumericAttribute) entry.getValue()).attribute(lineCommentColumnPolicy);
      else if (att.equals(StandardNames.FMT_COMMENT_WRAP))
        wrap = ((BooleanAttribute) entry.getValue()).attribute(commentWrap);
      else if (att.equals(StandardNames.FMT_WRAP_COLUMN))
        columnWrap = ((NumericAttribute) entry.getValue()).attribute(commentWrapColumn);
      else if (att.equals(StandardNames.FMT_COMMENT_COLUMN))
        lineComment = ((NumericAttribute) entry.getValue()).attribute(lineCommentColumnPolicy);
      else if (att.equals(StandardNames.FMT_BREAK_BEFORE))
        before = ((BooleanAttribute) entry.getValue()).attribute(before);
      else if (att.equals(StandardNames.FMT_BREAK_AFTER))
        after = ((BooleanAttribute) entry.getValue()).attribute(after);
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