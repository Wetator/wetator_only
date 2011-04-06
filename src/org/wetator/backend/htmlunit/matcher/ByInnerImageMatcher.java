/*
 * Copyright (c) 2008-2011 wetator.org
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

import java.util.LinkedList;
import java.util.List;

import org.wetator.backend.WeightedControlList.FoundType;
import org.wetator.backend.htmlunit.util.FindSpot;
import org.wetator.backend.htmlunit.util.HtmlPageIndex;
import org.wetator.core.searchpattern.SearchPattern;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlImageInput;

/**
 * This matcher checks if the given element contains an image ({@link HtmlImage} or {@link HtmlImageInput}) and this
 * image matches the criteria.
 * 
 * @author frank.danek
 */
public class ByInnerImageMatcher extends AbstractHtmlUnitElementMatcher {

  /**
   * The constructor.<br/>
   * Creates a new matcher with the given criteria.
   * 
   * @param aHtmlPageIndex the {@link HtmlPageIndex} of the page the match is based on
   * @param aPathSearchPattern the {@link SearchPattern} describing the path to the element
   * @param aPathSpot the {@link FindSpot} the path was found first
   * @param aSearchPattern the {@link SearchPattern} describing the element
   */
  public ByInnerImageMatcher(final HtmlPageIndex aHtmlPageIndex, final SearchPattern aPathSearchPattern,
      final FindSpot aPathSpot, final SearchPattern aSearchPattern) {
    super(aHtmlPageIndex, aPathSearchPattern, aPathSpot, aSearchPattern);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.htmlunit.matcher.AbstractHtmlUnitElementMatcher#matches(com.gargoylesoftware.htmlunit.html.HtmlElement)
   */
  @Override
  public List<MatchResult> matches(final HtmlElement aHtmlElement) {
    final List<MatchResult> tmpFound = new LinkedList<MatchResult>();
    // has the node the text before
    final FindSpot tmpNodeSpot = htmlPageIndex.getPosition(aHtmlElement);
    if (pathSpot.endPos <= tmpNodeSpot.startPos) {

      // now check for the including image
      final Iterable<HtmlElement> tmpAllchildElements = aHtmlElement.getHtmlElementDescendants();
      for (HtmlElement tmpInnerElement : tmpAllchildElements) {
        if (tmpInnerElement instanceof HtmlImage) {
          // check for the image alt tag is not needed, alt text is part of the outer element's text

          // does image title-text match?
          tmpFound.addAll(new ByInnerImageTitleAttributeMatcher(htmlPageIndex, pathSearchPattern, pathSpot,
              searchPattern, tmpInnerElement).matches(aHtmlElement));

          // does image filename match?
          tmpFound.addAll(new ByInnerImageSrcAttributeMatcher(htmlPageIndex, pathSearchPattern, pathSpot,
              searchPattern, tmpInnerElement).matches(aHtmlElement));

          tmpFound.addAll(new ByInnerNameMatcher(htmlPageIndex, pathSearchPattern, pathSpot, searchPattern,
              tmpInnerElement).matches(aHtmlElement));
        }
      }
    }
    return tmpFound;
  }

  /**
   * This matcher checks if the attribute 'src' of the given image ({@link HtmlImage} or {@link HtmlImageInput}) matches
   * the criteria.
   * 
   * @author frank.danek
   */
  protected static class ByInnerImageSrcAttributeMatcher extends AbstractByAttributeMatcher {

    private HtmlElement innerHtmlElement;

    /**
     * The constructor.<br/>
     * Creates a new matcher with the given criteria.
     * 
     * @param aHtmlPageIndex the {@link HtmlPageIndex} of the page the match is based on
     * @param aPathSearchPattern the {@link SearchPattern} describing the path to the element
     * @param aPathSpot the {@link FindSpot} the path was found first
     * @param aSearchPattern the {@link SearchPattern} describing the element
     * @param anInnerHtmlElement the inner image element
     */
    public ByInnerImageSrcAttributeMatcher(final HtmlPageIndex aHtmlPageIndex, final SearchPattern aPathSearchPattern,
        final FindSpot aPathSpot, final SearchPattern aSearchPattern, final HtmlElement anInnerHtmlElement) {
      super(aHtmlPageIndex, aPathSearchPattern, aPathSpot, aSearchPattern, FoundType.BY_INNER_IMG_SRC_ATTRIBUTE);
      innerHtmlElement = anInnerHtmlElement;
      matchType = MatchType.ENDS_WITH;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.wetator.backend.htmlunit.matcher.AbstractByAttributeMatcher#getAttributeValue(com.gargoylesoftware.htmlunit.html.HtmlElement)
     */
    @Override
    protected String getAttributeValue(final HtmlElement aHtmlElement) {
      String tmpValue = null;
      if (innerHtmlElement instanceof HtmlImage) {
        tmpValue = ((HtmlImage) innerHtmlElement).getSrcAttribute();
      }
      if (innerHtmlElement instanceof HtmlImageInput) {
        tmpValue = ((HtmlImageInput) innerHtmlElement).getSrcAttribute();
      }
      return tmpValue;
    }
  }

  /**
   * This matcher checks if the attribute 'title' of the given image ({@link HtmlImage} or {@link HtmlImageInput})
   * matches the criteria.
   * 
   * @author frank.danek
   */
  protected static class ByInnerImageTitleAttributeMatcher extends AbstractByAttributeMatcher {

    private HtmlElement innerHtmlElement;

    /**
     * The constructor.<br/>
     * Creates a new matcher with the given criteria.
     * 
     * @param aHtmlPageIndex the {@link HtmlPageIndex} of the page the match is based on
     * @param aPathSearchPattern the {@link SearchPattern} describing the path to the element
     * @param aPathSpot the {@link FindSpot} the path was found first
     * @param aSearchPattern the {@link SearchPattern} describing the element
     * @param anInnerHtmlElement the inner image element
     */
    public ByInnerImageTitleAttributeMatcher(final HtmlPageIndex aHtmlPageIndex,
        final SearchPattern aPathSearchPattern, final FindSpot aPathSpot, final SearchPattern aSearchPattern,
        final HtmlElement anInnerHtmlElement) {
      super(aHtmlPageIndex, aPathSearchPattern, aPathSpot, aSearchPattern, FoundType.BY_INNER_IMG_TITLE_ATTRIBUTE);
      innerHtmlElement = anInnerHtmlElement;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.wetator.backend.htmlunit.matcher.AbstractByAttributeMatcher#getAttributeValue(com.gargoylesoftware.htmlunit.html.HtmlElement)
     */
    @Override
    protected String getAttributeValue(final HtmlElement aHtmlElement) {
      return innerHtmlElement.getAttribute("title");
    }
  }

  /**
   * This matcher checks if the attribute 'name' of the given image ({@link HtmlImage} or {@link HtmlImageInput})
   * matches the criteria.
   * 
   * @author frank.danek
   */
  protected static class ByInnerNameMatcher extends AbstractByAttributeMatcher {

    private HtmlElement innerHtmlElement;

    /**
     * The constructor.<br/>
     * Creates a new matcher with the given criteria.
     * 
     * @param aHtmlPageIndex the {@link HtmlPageIndex} of the page the match is based on
     * @param aPathSearchPattern the {@link SearchPattern} describing the path to the element
     * @param aPathSpot the {@link FindSpot} the path was found first
     * @param aSearchPattern the {@link SearchPattern} describing the element
     * @param anInnerHtmlElement the inner image element
     */
    public ByInnerNameMatcher(final HtmlPageIndex aHtmlPageIndex, final SearchPattern aPathSearchPattern,
        final FindSpot aPathSpot, final SearchPattern aSearchPattern, final HtmlElement anInnerHtmlElement) {
      super(aHtmlPageIndex, aPathSearchPattern, aPathSpot, aSearchPattern, FoundType.BY_INNER_NAME);
      innerHtmlElement = anInnerHtmlElement;
      matchType = MatchType.EXACT;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.wetator.backend.htmlunit.matcher.AbstractByAttributeMatcher#getAttributeValue(com.gargoylesoftware.htmlunit.html.HtmlElement)
     */
    @Override
    protected String getAttributeValue(final HtmlElement aHtmlElement) {
      return innerHtmlElement.getAttribute("name");
    }
  }
}