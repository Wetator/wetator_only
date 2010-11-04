/*
 * Copyright (c) 2008-2010 Ronald Brill
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.rbri.wet.backend.htmlunit.control.identifier;

import java.util.LinkedList;
import java.util.List;

import org.rbri.wet.backend.htmlunit.control.HtmlUnitInputFile;
import org.rbri.wet.backend.htmlunit.matcher.AbstractHtmlUnitElementMatcher.MatchResult;
import org.rbri.wet.backend.htmlunit.matcher.ByHtmlLabelMatcher;
import org.rbri.wet.backend.htmlunit.matcher.ByIdMatcher;
import org.rbri.wet.backend.htmlunit.matcher.ByLabelTextBeforeMatcher;
import org.rbri.wet.backend.htmlunit.matcher.ByNameAttributeMatcher;
import org.rbri.wet.backend.htmlunit.matcher.ByWholeTextBeforeMatcher;
import org.rbri.wet.backend.htmlunit.util.FindSpot;
import org.rbri.wet.core.searchpattern.SearchPattern;
import org.rbri.wet.util.SecretString;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlFileInput;
import com.gargoylesoftware.htmlunit.html.HtmlLabel;

/**
 * XXX add class jdoc
 * 
 * @author frank.danek
 */
public class HtmlUnitInputFileIdentifier extends AbstractHtmlUnitControlIdentifier {

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.htmlunit.control.identifier.AbstractHtmlUnitControlIdentifier#isElementSupported(com.gargoylesoftware.htmlunit.html.HtmlElement)
   */
  @Override
  public boolean isElementSupported(HtmlElement aHtmlElement) {
    return (aHtmlElement instanceof HtmlFileInput) || (aHtmlElement instanceof HtmlLabel);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.htmlunit.control.identifier.AbstractHtmlUnitControlIdentifier#identify(java.util.List,
   *      com.gargoylesoftware.htmlunit.html.HtmlElement)
   */
  @Override
  public void identify(List<SecretString> aSearch, HtmlElement aHtmlElement) {
    SearchPattern tmpSearchPattern = aSearch.get(aSearch.size() - 1).getSearchPattern();
    SearchPattern tmpPathSearchPattern = SearchPattern.createFromList(aSearch, aSearch.size() - 1);
    FindSpot tmpPathSpot = domNodeText.firstOccurence(tmpPathSearchPattern);
    SearchPattern tmpWholePathSearchPattern = SearchPattern.createFromList(aSearch);

    if (null == tmpPathSpot) {
      return;
    }

    List<MatchResult> tmpMatches = new LinkedList<MatchResult>();
    if (aHtmlElement instanceof HtmlFileInput) {
      // whole text before
      tmpMatches.addAll(new ByWholeTextBeforeMatcher(domNodeText, tmpPathSearchPattern, tmpPathSpot,
          tmpWholePathSearchPattern, foundElements).matches(aHtmlElement));

      tmpMatches.addAll(new ByLabelTextBeforeMatcher(domNodeText, tmpPathSearchPattern, tmpPathSpot, tmpSearchPattern,
          foundElements).matches(aHtmlElement));
      tmpMatches.addAll(new ByNameAttributeMatcher(domNodeText, tmpPathSearchPattern, tmpPathSpot, tmpSearchPattern,
          foundElements).matches(aHtmlElement));
      tmpMatches
          .addAll(new ByIdMatcher(domNodeText, tmpPathSearchPattern, tmpPathSpot, tmpSearchPattern, foundElements)
              .matches(aHtmlElement));

    } else if (aHtmlElement instanceof HtmlLabel) {
      tmpMatches.addAll(new ByHtmlLabelMatcher(domNodeText, tmpPathSearchPattern, tmpPathSpot, tmpSearchPattern,
          foundElements, htmlPage, HtmlFileInput.class).matches(aHtmlElement));
    }
    for (MatchResult tmpMatch : tmpMatches) {
      foundElements.add(new HtmlUnitInputFile((HtmlFileInput) tmpMatch.getHtmlElement()), tmpMatch.getFoundType(),
          tmpMatch.getCoverage(), tmpMatch.getDistance());
    }
  }

}