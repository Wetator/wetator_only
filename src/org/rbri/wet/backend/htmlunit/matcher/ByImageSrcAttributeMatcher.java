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

import org.rbri.wet.backend.WeightedControlList.FoundType;
import org.rbri.wet.backend.htmlunit.util.DomNodeText;
import org.rbri.wet.backend.htmlunit.util.FindSpot;
import org.rbri.wet.core.searchpattern.SearchPattern;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlImageInput;

/**
 * This matcher checks if the attribute 'src' of the given image ({@link HtmlImage} or {@link HtmlImageInput}) matches
 * the criteria.
 * 
 * @author frank.danek
 */
public class ByImageSrcAttributeMatcher extends AbstractByAttributeMatcher {

  /**
   * The constructor.<br/>
   * Creates a new matcher with the given criteria.
   * 
   * @param aDomNodeText the {@link DomNodeText} of the page the match is based on
   * @param aPathSearchPattern the {@link SearchPattern} describing the path to the element
   * @param aPathSpot the {@link FindSpot} the path was found first
   * @param aSearchPattern the {@link SearchPattern} describing the element
   */
  public ByImageSrcAttributeMatcher(DomNodeText aDomNodeText, SearchPattern aPathSearchPattern, FindSpot aPathSpot,
      SearchPattern aSearchPattern) {
    super(aDomNodeText, aPathSearchPattern, aPathSpot, aSearchPattern, FoundType.BY_IMG_SRC_ATTRIBUTE);
    matchType = MatchType.ENDS_WITH;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.htmlunit.matcher.AbstractByAttributeMatcher#getAttributeValue(com.gargoylesoftware.htmlunit.html.HtmlElement)
   */
  @Override
  protected String getAttributeValue(HtmlElement aHtmlElement) {
    String tmpValue = null;
    if (aHtmlElement instanceof HtmlImage) {
      tmpValue = ((HtmlImage) aHtmlElement).getSrcAttribute();
    }
    if (aHtmlElement instanceof HtmlImageInput) {
      tmpValue = ((HtmlImageInput) aHtmlElement).getSrcAttribute();
    }
    return tmpValue;
  }
}
