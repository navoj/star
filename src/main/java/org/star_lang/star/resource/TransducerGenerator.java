package org.star_lang.star.resource;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.star_lang.star.LanguageException;
import org.star_lang.star.compiler.util.Pair;
import org.star_lang.star.compiler.util.RegexpUtils;
import org.star_lang.star.compiler.util.StringUtils;
import org.star_lang.star.compiler.util.Template;
import org.star_lang.star.compiler.util.TemplateString;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.value.ResourceURI;

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
public class TransducerGenerator
{
  /**
   * Construct a transducer based on a regular-expression based rule:
   * 
   * <pre>
   * myScheme:(.*:A) ==> file://MyDir/$A.star"
   * </pre>
   * 
   * maps the myScheme uris to the file uri.
   * 
   * @throws LanguageException
   * 
   */

  public static void generate(String rule) throws LanguageException
  {
    int arrowPos = rule.indexOf("==>");
    if (arrowPos > 0) {
      String ptnRegexp = rule.substring(0, arrowPos);
      String repl = rule.substring(arrowPos + "==>".length());
      int colonPos = ptnRegexp.indexOf(':');
      if (colonPos < 0)
        throw new LanguageException("expecting a scheme identifier in " + ptnRegexp);
      else {
        String scheme = ptnRegexp.substring(0, colonPos);
        Template replTemplate = TemplateString.parseTemplate(repl);
        Pair<String, List<String>> info = RegexpUtils
            .analyseRegexp(ptnRegexp.substring(colonPos + 1), Location.nullLoc);

        List<String> templateVars = info.right();

        for (String var : replTemplate.getVars()) {
          if (!templateVars.contains(var))
            throw new LanguageException(StringUtils.msg("bad transducer mapping\ntemplate var ", var,
                " not generated by regexp ", ptnRegexp));
        }

        RegexpTransducer transducer = new RegexpTransducer(scheme + ":" + info.left(), replTemplate, templateVars);
        Resources.recordTransducer(scheme, transducer);
      }
    } else
      throw new LanguageException(StringUtils.msg("incorrect form of transducer rule: ", rule,
          "\nexpecting <schema>:<ptn>==><schem>:<repl>"));
  }

  protected static class RegexpTransducer implements Transducer
  {
    private final Pattern regexp;
    private final Template template;
    private final List<String> vars;

    RegexpTransducer(String regexp, Template template, List<String> vars)
    {
      this.template = template;
      this.vars = vars;
      this.regexp = Pattern.compile(regexp);
    }

    @Override
    public Reader getReader(ResourceURI uri) throws ResourceException
    {
      ResourceURI replUri = convertUri(uri);
      return Resources.getReader(replUri);
    }

    @Override
    public InputStream getInputStream(ResourceURI uri) throws ResourceException
    {
      ResourceURI replUri = convertUri(uri);
      return Resources.getInputStream(replUri);
    }

    @Override
    public void putResource(ResourceURI uri, String resource) throws ResourceException
    {
      ResourceURI replUri = convertUri(uri);
      Resources.putResource(replUri, resource);
    }

    @Override
    public boolean exists(ResourceURI uri) throws ResourceException
    {
      ResourceURI replUri = convertUri(uri);
      return Resources.exists(replUri);
    }

    @Override
    public OutputStream getOutputStream(ResourceURI uri) throws ResourceException
    {
      ResourceURI replUri = convertUri(uri);
      return Resources.getOutputStream(replUri);
    }

    private ResourceURI convertUri(ResourceURI uri) throws ResourceException
    {
      String uriRep = uri.toString();
      Matcher match = regexp.matcher(uriRep);
      if (match.matches()) {
        Map<String, String> replvarMap = new HashMap<>();
        for (int ix = 0; ix < match.groupCount(); ix++) {
          String sub = match.group(ix + 1);
          String var = vars.get(ix);
          replvarMap.put(var, sub);
        }
        String replacement = template.applyTemplate(replvarMap);
        return URIUtils.parseUri(replacement);
      } else
        throw new ResourceException("invalid uri: " + uri);
    }

  }
}
