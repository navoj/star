package org.star_lang.star.compiler.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Utility to help instrument timing
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
public class Instrument implements PrettyPrintable
{
  public static final double NANOS_PER_SECOND = 1.0e9;
  public static final long NANOS_PER_MILLI = 1000000;

  private final String title;
  private final long start;
  private final List<Pair<String, Long>> timeSlots = new ArrayList<>();
  private final Map<String, Long> starts = new HashMap<>();
  private final Map<String, Stats> counts = new HashMap<>();
  private long lastTime;

  public Instrument(String title)
  {
    this.title = title;
    this.start = lastTime = System.nanoTime();
  }

  public void startTimer(String subtitle)
  {
    lastTime = System.nanoTime();
    starts.put(subtitle, lastTime);
  }

  public void recordTime(String subtitle)
  {
    long mark = System.nanoTime();
    if (starts.containsKey(subtitle)) {
      Long start = starts.remove(subtitle);
      lastTime = mark;
      timeSlots.add(Pair.pair(subtitle, mark - start));
    } else {
      long delta = mark - lastTime;
      lastTime = mark;
      timeSlots.add(Pair.pair(subtitle, delta));
    }
  }

  public void addToCount(String unit, long amnt)
  {
    Stats cnt = counts.get(unit);
    if (cnt == null)
      counts.put(unit, new Stats(amnt));
    else
      cnt.accumulate(amnt);
  }

  public String report()
  {
    return report("total ");
  }

  public String report(String msg)
  {
    StringBuilder report = new StringBuilder();

    report.append(title);
    report.append("\n");

    for (Entry<String, Long> entry : timeSlots) {
      double msecs = entry.getValue() / (double) NANOS_PER_MILLI;
      report.append(entry.getKey()).append(" ").append(Double.toString(msecs)).append(" millisecs\n");
    }

    double total = ((double) (lastTime - start)) / 1.0e9;
    report.append(msg).append(Double.toString(total)).append("secs\n");

    for (Entry<String, Stats> entry : counts.entrySet()) {
      report.append(entry.getKey());
      report.append(" = ");
      report.append(entry.getValue()).append("\n");
    }

    timeSlots.clear();
    counts.clear();
    starts.clear();

    return report.toString();
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append(title);
    disp.append("\n");

    for (Entry<String, Long> entry : timeSlots) {
      double msecs = entry.getValue() / (double) NANOS_PER_MILLI;
      disp.append(entry.getKey()).append(" ").append(Double.toString(msecs)).append(" millisecs\n");
    }

    double total = ((double) (lastTime - start)) / 1.0e9;
    disp.append("Total ").append(Double.toString(total)).append("secs\n");

    for (Entry<String, Stats> entry : counts.entrySet()) {
      disp.append(entry.getKey());
      disp.append(" = ");
      entry.getValue().prettyPrint(disp);
      disp.append("\n");
    }

    timeSlots.clear();
    counts.clear();
    starts.clear();
  }

  private static class Stats implements PrettyPrintable
  {
    long count;
    double sum;

    public Stats(long sum)
    {
      this.count = 1;
      this.sum = sum;
    }

    void accumulate(long sum)
    {
      count++;
      this.sum += sum;
    }

    double average()
    {
      return sum / count;
    }

    @Override
    public void prettyPrint(PrettyPrintDisplay disp)
    {
      disp.append(average());
      disp.append(" over ");
      disp.append(count);
    }

    @Override
    public String toString()
    {
      return PrettyPrintDisplay.toString(this);
    }

  }
}
