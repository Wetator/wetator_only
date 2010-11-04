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


package org.rbri.wet.backend.htmlunit.matcher;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.rbri.wet.backend.WeightedControlList;
import org.rbri.wet.backend.WeightedControlList.FoundType;
import org.rbri.wet.backend.htmlunit.util.DomNodeText;
import org.rbri.wet.backend.htmlunit.util.FindSpot;
import org.rbri.wet.core.searchpattern.SearchPattern;

import com.gargoylesoftware.htmlunit.html.HtmlElement;

/**
 * This is a base class for all matchers checking if an attribute of a {@link HtmlElement} matches a criteria.
 * 
 * @author frank.danek
 */
public abstract class AbstractByAttributeMatcher extends AbstractHtmlUnitElementMatcher {

  /**
   * The {@link MatchType} the matcher should use. It influences the way the criteria must be matched.
   */
  protected MatchType matchType = MatchType.CONTAINS;
  private FoundType foundType;

  /**
   * The constructor.<br/>
   * Creates a new matcher with the given criteria.
   * 
   * @param aDomNodeText the {@link DomNodeText} of the page the match is based on
   * @param aPathSearchPattern the {@link SearchPattern} describing the path to the element
   * @param aPathSpot the {@link FindSpot} the path was found first
   * @param aSearchPattern the {@link SearchPattern} describing the element
   * @param aFoundElements the result list to add the found elements to
   * @param aFoundType the {@link FoundType} the matcher should use when adding the element
   */
  public AbstractByAttributeMatcher(DomNodeText aDomNodeText, SearchPattern aPathSearchPattern, FindSpot aPathSpot,
      SearchPattern aSearchPattern, WeightedControlList aFoundElements, FoundType aFoundType) {
    super(aDomNodeText, aPathSearchPattern, aPathSpot, aSearchPattern, aFoundElements);
    foundType = aFoundType;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.htmlunit.matcher.AbstractHtmlUnitElementMatcher#matches(com.gargoylesoftware.htmlunit.html.HtmlElement)
   */
  @Override
  public List<MatchResult> matches(HtmlElement aHtmlElement) {
    List<MatchResult> tmpFound = new LinkedList<MatchResult>();
    // has the node the text before
    FindSpot tmpNodeSpot = domNodeText.getPosition(aHtmlElement);
    if (null != pathSpot && pathSpot.endPos <= tmpNodeSpot.startPos) {

      String tmpValue = getAttributeValue(aHtmlElement);
      if (StringUtils.isNotEmpty(tmpValue)) {
        if (MatchType.CONTAINS == matchType || MatchType.STARTS_WITH == matchType
            || (MatchType.EXACT == matchType && searchPattern.matches(tmpValue))
            || (MatchType.ENDS_WITH == matchType && searchPattern.matchesAtEnd(tmpValue))) {

          int tmpCoverage;
          if (MatchType.ENDS_WITH == matchType) {
            tmpCoverage = searchPattern.noOfCharsBeforeLastOccurenceIn(tmpValue);
          } else if (MatchType.STARTS_WITH == matchType) {
            tmpCoverage = searchPattern.noOfCharsAfterLastOccurenceIn(tmpValue);
          } else {
            tmpCoverage = searchPattern.noOfSurroundingCharsIn(tmpValue);
          }
          if (tmpCoverage > -1) {
            String tmpTextBefore = domNodeText.getTextBefore(aHtmlElement);
            tmpTextBefore = processTextForDistance(tmpTextBefore);
            int tmpDistance = pathSearchPattern.noOfCharsAfterLastOccurenceIn(tmpTextBefore);
            tmpFound.add(new MatchResult(aHtmlElement, foundType, tmpCoverage, tmpDistance));
          }
        }
      }
    }
    return tmpFound;
  }

  /**
   * @param aHtmlElement the {@link HtmlElement} to get the attribute value of
   * @return the value of the attribute to be matched
   */
  protected abstract String getAttributeValue(HtmlElement aHtmlElement);

  /**
   * Processed the text used to calculate the distance.<br/>
   * The default implementation just returns the original text. Override to change this behavior.
   * 
   * @param aTextBefore the text to process
   * @return the processed text
   */
  protected String processTextForDistance(String aTextBefore) {
    return aTextBefore;
  }

  /**
   * This enum contains all types a match could be done.
   * 
   * @author frank.danek
   */
  protected static enum MatchType {
    /**
     * The attribute value contains the search pattern describing the element.
     */
    CONTAINS,
    /**
     * The attribute value matches exactly the search pattern describing the element.
     */
    EXACT,
    /**
     * The attribute value starts with the search pattern describing the element.
     */
    STARTS_WITH,
    /**
     * The attribute value ends with the search pattern describing the element.
     */
    ENDS_WITH
  }
}