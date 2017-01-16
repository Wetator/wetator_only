/*
 * Copyright (c) 2008-2017 wetator.org
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


package org.wetator.backend.htmlunit.matcher;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.wetator.backend.WeightedControlList.FoundType;
import org.wetator.backend.htmlunit.util.HtmlPageIndex;
import org.wetator.core.searchpattern.SearchPattern;
import org.wetator.util.FindSpot;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlLabel;

/**
 * This matcher checks if the given {@link HtmlLabel} matches the criteria and labels the needed type of element.
 *
 * @author frank.danek
 */
public class ByHtmlLabelMatcher extends AbstractHtmlUnitElementMatcher {

  private Class<? extends HtmlElement> clazz;

  /**
   * The constructor.<br>
   * Creates a new matcher with the given criteria.
   *
   * @param aHtmlPageIndex the {@link HtmlPageIndex} of the page the match is based on
   * @param aPathSearchPattern the {@link SearchPattern} describing the path to the element or <code>null</code> if no
   *        path given
   * @param aPathSpot the {@link FindSpot} the path was found first or <code>null</code> if no path given
   * @param aSearchPattern the {@link SearchPattern} describing the element
   * @param aClass the class of the {@link HtmlElement} the matching label labels
   */
  public ByHtmlLabelMatcher(final HtmlPageIndex aHtmlPageIndex, final SearchPattern aPathSearchPattern,
      final FindSpot aPathSpot, final SearchPattern aSearchPattern, final Class<? extends HtmlElement> aClass) {
    super(aHtmlPageIndex, aPathSearchPattern, aPathSpot, aSearchPattern);
    clazz = aClass;
  }

  @Override
  public List<MatchResult> matches(final HtmlElement aHtmlElement) {
    if (!(aHtmlElement instanceof HtmlLabel)) {
      return Collections.emptyList();
    }

    // was the path found at all
    if (FindSpot.NOT_FOUND == pathSpot) {
      return Collections.emptyList();
    }

    // has the search pattern something to match
    if (searchPattern.getMinLength() == 0) {
      return Collections.emptyList();
    }

    // has the node the text before
    FindSpot tmpNodeSpot = htmlPageIndex.getPosition(aHtmlElement);
    if (pathSpot == null || pathSpot.getEndPos() <= tmpNodeSpot.getStartPos()) {
      final HtmlLabel tmpLabel = (HtmlLabel) aHtmlElement;

      // found a label with this text
      final String tmpText = htmlPageIndex.getAsTextWithoutFormControls(tmpLabel);
      final int tmpCoverage = searchPattern.noOfSurroundingCharsIn(tmpText);
      if (tmpCoverage > -1) {
        final List<MatchResult> tmpMatches = new LinkedList<MatchResult>();

        final String tmpForAttribute = tmpLabel.getForAttribute();
        // label contains a for-attribute => find corresponding element
        if (StringUtils.isNotEmpty(tmpForAttribute)) {
          try {
            final HtmlElement tmpElementForLabel = htmlPageIndex.getHtmlElementById(tmpForAttribute);
            if (clazz.isAssignableFrom(tmpElementForLabel.getClass()) && tmpElementForLabel.isDisplayed()) {
              tmpNodeSpot = htmlPageIndex.getPosition(aHtmlElement);
              final String tmpTextBefore = htmlPageIndex.getTextBefore(tmpLabel);
              final int tmpDistance;
              if (pathSearchPattern != null) {
                tmpDistance = pathSearchPattern.noOfCharsAfterLastShortestOccurenceIn(tmpTextBefore);
              } else {
                tmpDistance = tmpTextBefore.length();
              }
              tmpMatches.add(new MatchResult(tmpElementForLabel, FoundType.BY_LABEL, tmpCoverage, tmpDistance,
                  tmpNodeSpot.getStartPos()));
            }
          } catch (final ElementNotFoundException e) {
            // not found
          }
        }

        // Element must be a nested element of label
        final Iterable<HtmlElement> tmpChilds = tmpLabel.getHtmlElementDescendants();
        for (final HtmlElement tmpChildElement : tmpChilds) {
          if (clazz.isAssignableFrom(tmpChildElement.getClass()) && tmpChildElement.isDisplayed()) {
            tmpNodeSpot = htmlPageIndex.getPosition(aHtmlElement);
            final String tmpTextBefore = htmlPageIndex.getTextBefore(tmpLabel);
            final int tmpDistance;
            if (pathSearchPattern != null) {
              tmpDistance = pathSearchPattern.noOfCharsAfterLastShortestOccurenceIn(tmpTextBefore);
            } else {
              tmpDistance = tmpTextBefore.length();
            }
            tmpMatches.add(new MatchResult(tmpChildElement, FoundType.BY_LABEL, tmpCoverage, tmpDistance,
                tmpNodeSpot.getStartPos()));
          }
        }
        return tmpMatches;
      }
    }

    return Collections.emptyList();
  }
}
