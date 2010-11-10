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

import org.rbri.wet.backend.WeightedControlList;
import org.rbri.wet.backend.htmlunit.control.HtmlUnitInputCheckBox;
import org.rbri.wet.backend.htmlunit.matcher.AbstractHtmlUnitElementMatcher.MatchResult;
import org.rbri.wet.backend.htmlunit.matcher.ByHtmlLabelMatcher;
import org.rbri.wet.backend.htmlunit.matcher.ByIdMatcher;
import org.rbri.wet.backend.htmlunit.matcher.ByLabelTextAfterMatcher;
import org.rbri.wet.backend.htmlunit.matcher.ByNameAttributeMatcher;
import org.rbri.wet.backend.htmlunit.util.FindSpot;
import org.rbri.wet.core.searchpattern.SearchPattern;
import org.rbri.wet.util.SecretString;

import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlLabel;

/**
 * The identifier for a {@link HtmlUnitInputCheckBox}.<br />
 * It can be identified by:
 * <ul>
 * <li>the label text after</li>
 * <li>it's name</li>
 * <li>it's id</li>
 * <li>a label</li>
 * </ul>
 * 
 * @author frank.danek
 */
public class HtmlUnitInputCheckBoxIdentifier extends AbstractHtmlUnitControlIdentifier {

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.htmlunit.control.identifier.AbstractHtmlUnitControlIdentifier#isHtmlElementSupported(com.gargoylesoftware.htmlunit.html.HtmlElement)
   */
  @Override
  public boolean isHtmlElementSupported(HtmlElement aHtmlElement) {
    return (aHtmlElement instanceof HtmlCheckBoxInput) || (aHtmlElement instanceof HtmlLabel);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.htmlunit.control.identifier.AbstractHtmlUnitControlIdentifier#identify(java.util.List,
   *      com.gargoylesoftware.htmlunit.html.HtmlElement)
   */
  @Override
  public WeightedControlList identify(List<SecretString> aSearch, HtmlElement aHtmlElement) {
    SearchPattern tmpSearchPattern = aSearch.get(aSearch.size() - 1).getSearchPattern();
    SearchPattern tmpPathSearchPattern = SearchPattern.createFromList(aSearch, aSearch.size() - 1);
    FindSpot tmpPathSpot = domNodeText.firstOccurence(tmpPathSearchPattern);

    if (null == tmpPathSpot) {
      return new WeightedControlList();
    }

    List<MatchResult> tmpMatches = new LinkedList<MatchResult>();
    if (aHtmlElement instanceof HtmlCheckBoxInput) {
      tmpMatches.addAll(new ByLabelTextAfterMatcher(domNodeText, tmpPathSearchPattern, tmpPathSpot, tmpSearchPattern)
          .matches(aHtmlElement));
      tmpMatches.addAll(new ByNameAttributeMatcher(domNodeText, tmpPathSearchPattern, tmpPathSpot, tmpSearchPattern)
          .matches(aHtmlElement));
      tmpMatches.addAll(new ByIdMatcher(domNodeText, tmpPathSearchPattern, tmpPathSpot, tmpSearchPattern)
          .matches(aHtmlElement));

    } else if (aHtmlElement instanceof HtmlLabel) {
      tmpMatches.addAll(new ByHtmlLabelMatcher(domNodeText, tmpPathSearchPattern, tmpPathSpot, tmpSearchPattern,
          htmlPage, HtmlCheckBoxInput.class).matches(aHtmlElement));
    }
    WeightedControlList tmpResult = new WeightedControlList();
    for (MatchResult tmpMatch : tmpMatches) {
      tmpResult.add(new HtmlUnitInputCheckBox((HtmlCheckBoxInput) tmpMatch.getHtmlElement()), tmpMatch.getFoundType(),
          tmpMatch.getCoverage(), tmpMatch.getDistance());
    }
    return tmpResult;
  }

}
