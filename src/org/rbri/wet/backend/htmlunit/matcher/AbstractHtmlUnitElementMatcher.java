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

import java.util.List;

import org.rbri.wet.backend.WeightedControlList.FoundType;
import org.rbri.wet.backend.htmlunit.util.DomNodeText;
import org.rbri.wet.backend.htmlunit.util.FindSpot;
import org.rbri.wet.core.searchpattern.SearchPattern;

import com.gargoylesoftware.htmlunit.html.HtmlElement;

/**
 * The base class for all matchers.<br/>
 * A matcher checks if a {@link HtmlElement} matches one or multiple criteria. If it matches it is added to the result
 * list. A control may be added multiple times if it matches multiple criteria.
 * 
 * @author frank.danek
 */
public abstract class AbstractHtmlUnitElementMatcher {

  /**
   * The {@link DomNodeText} of the page the match is based on.
   */
  protected DomNodeText domNodeText;
  /**
   * The {@link SearchPattern} describing the path to the element.
   */
  protected SearchPattern pathSearchPattern;
  /**
   * The {@link FindSpot} the path was found first.
   */
  protected FindSpot pathSpot;
  /**
   * The {@link SearchPattern} describing the element.
   */
  protected SearchPattern searchPattern;

  /**
   * The constructor.<br/>
   * Creates a new matcher with the given criteria.
   * 
   * @param aDomNodeText the {@link DomNodeText} of the page the match is based on
   * @param aPathSearchPattern the {@link SearchPattern} describing the path to the element
   * @param aPathSpot the {@link FindSpot} the path was found first
   * @param aSearchPattern the {@link SearchPattern} describing the element
   */
  public AbstractHtmlUnitElementMatcher(DomNodeText aDomNodeText, SearchPattern aPathSearchPattern, FindSpot aPathSpot,
      SearchPattern aSearchPattern) {
    domNodeText = aDomNodeText;
    pathSpot = aPathSpot;
    searchPattern = aSearchPattern;
    pathSearchPattern = aPathSearchPattern;
  }

  /**
   * @param aHtmlElement the element to match
   * @return true if the given element matches at least one criterion
   */
  public abstract List<MatchResult> matches(HtmlElement aHtmlElement);

  /**
   * This is a container for the result of a match.
   * 
   * @author frank.danek
   */
  public static class MatchResult {

    private HtmlElement htmlElement;
    private FoundType foundType;
    private int coverage;
    private int distance;

    /**
     * @param aHtmlElement the matching {@link HtmlElement}
     * @param aFoundType the {@link FoundType}
     * @param aCoverage the coverage
     * @param aDistance the distance
     */
    public MatchResult(HtmlElement aHtmlElement, FoundType aFoundType, int aCoverage, int aDistance) {
      super();
      htmlElement = aHtmlElement;
      foundType = aFoundType;
      coverage = aCoverage;
      distance = aDistance;
    }

    /**
     * @return the htmlElement
     */
    public HtmlElement getHtmlElement() {
      return htmlElement;
    }

    /**
     * @return the foundType
     */
    public FoundType getFoundType() {
      return foundType;
    }

    /**
     * @return the coverage
     */
    public int getCoverage() {
      return coverage;
    }

    /**
     * @return the distance
     */
    public int getDistance() {
      return distance;
    }
  }
}
