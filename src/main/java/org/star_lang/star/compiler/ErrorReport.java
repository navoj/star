package org.star_lang.star.compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.star_lang.star.StarCompiler;
import org.star_lang.star.compiler.util.Instrument;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.data.type.Location;

/**
 * An ErrorReport holds a record of what errors have occurred and where
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
@SuppressWarnings("serial")
public class ErrorReport implements PrettyPrintable
{
  private final Map<MsgType, List<CompilerMessage>> allMsgs = new TreeMap<>();

  private final Instrument instrument = new Instrument("");

  private void reportMsg(MsgType type, String text, Location locs[])
  {
    CompilerMessage msg = new CompilerMessage(type, text, locs);

    reportMsg(msg);

    // System.err.println(msg);
  }

  private void reportMsg(CompilerMessage msg)
  {
    List<CompilerMessage> msgs = allMsgs.get(msg.getMsgType());
    if (msgs == null) {
      msgs = new ArrayList<>();
      allMsgs.put(msg.getMsgType(), msgs);
    }
    if (!msgs.contains(msg))
      msgs.add(msg);
  }

  /**
   * Report a compilation error.
   * 
   * @param msg
   *          The string that should be displayed to the user
   * @param locs
   *          Where in the source the error was detected. There may be more than one location.
   */
  public void reportError(String msg, Location... locs)
  {
    reportMsg(MsgType.error, msg, locs);
  }

  /**
   * Report a compiler warning.
   * 
   * @param msg
   *          The string that should be displayed to the user
   * @param locs
   *          Where in the source the warning was detected
   */
  public void reportWarning(String msg, Location... locs)
  {
    reportMsg(MsgType.warning, msg, locs);
  }

  /**
   * Report some 'information'.
   * 
   * @param msg
   *          The string that should be displayed to the user
   * @param locs
   *          Where in the source the warning was detected
   */
  public void reportInfo(String msg, Location... locs)
  {
    reportMsg(MsgType.info, msg, locs);
  }

  /**
   * Report a debugging message.
   * 
   * @param msg
   *          The string that should be displayed to the user
   * @param locs
   *          Where in the source the debugging message relates to
   */
  public void reportDebug(String msg, Location... locs)
  {
    reportMsg(MsgType.debug, msg, locs);
  }

  public void mergeReport(ErrorReport other)
  {
    for (Entry<MsgType, List<CompilerMessage>> entry : other.allMsgs.entrySet()) {
      for (CompilerMessage msg : entry.getValue()) {
        reportMsg(msg);
      }
    }
  }

  private int countMsgs(MsgType type)
  {
    List<CompilerMessage> msgs = allMsgs.get(type);
    if (msgs == null)
      return 0;
    else
      return msgs.size();
  }

  public int errorCount()
  {
    return countMsgs(MsgType.error);
  }

  public boolean noNewErrors(int count)
  {
    return errorCount() == count;
  }

  private List<CompilerMessage> getMsgs(MsgType type)
  {
    return allMsgs.get(type);
  }

  public List<CompilerMessage> getErrors()
  {
    return getMsgs(MsgType.error);
  }

  public List<CompilerMessage> getWarnings()
  {
    return getMsgs(MsgType.warning);
  }

  public List<CompilerMessage> getInfos()
  {
    return getMsgs(MsgType.info);
  }

  public List<CompilerMessage> getDebugs()
  {
    return getMsgs(MsgType.debug);
  }

  /**
   * Occasionally we need to gate a later phase by ensuring that there are no errors so far.
   * 
   * @return true if there are no errors
   */
  public boolean isErrorFree()
  {
    return countMsgs(MsgType.error) == 0;
  }

  public boolean isWarningAndErrorFree()
  {
    return countMsgs(MsgType.error) == 0 && countMsgs(MsgType.warning) == 0;
  }

  public boolean isEmpty()
  {
    return allMsgs.isEmpty();
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    int mark = disp.markIndent();
    for (List<CompilerMessage> msgs : allMsgs.values()) {
      for (CompilerMessage msg : msgs) {
        msg.prettyPrint(disp);
        disp.append("\n");
      }
    }

    disp.popIndent(mark);

    if (StarCompiler.SHOWTIMING)
      instrument.prettyPrint(disp);
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

  public void clear()
  {
    allMsgs.clear();
  }

  public void startTimer(String subtitle)
  {
    instrument.startTimer(subtitle);
  }

  public void recordTime(String subtitle)
  {
    instrument.recordTime(subtitle);
  }

  public void addToCount(String subtitle, long amnt)
  {
    instrument.addToCount(subtitle, amnt);
  }

  public static class NullErrorReporter extends ErrorReport
  {
    @Override
    public void reportError(String msg, Location... locs)
    {
    }

    @Override
    public void reportWarning(String msg, Location... locs)
    {
    }

    @Override
    public void reportInfo(String msg, Location... locs)
    {
    }

    @Override
    public void reportDebug(String msg, Location... locs)
    {
    }
  }
}
