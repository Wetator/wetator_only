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

import org.rbri.wet.backend.WeightedControlList;
import org.rbri.wet.backend.WeightedControlList.FoundType;
import org.rbri.wet.backend.htmlunit.util.DomNodeText;
import org.rbri.wet.backend.htmlunit.util.FindSpot;
import org.rbri.wet.core.searchpattern.SearchPattern;

import com.gargoylesoftware.htmlunit.html.HtmlElement;

/**
 * This matcher checks if the attribute 'title' of the given element matches the criteria.
 * 
 * @author frank.danek
 */
public class ByImageTitleAttributeMatcher extends AbstractByAttributeMatcher {

  /**
   * The constructor.<br/>
   * Creates a new matcher with the given criteria.
   * 
   * @param aDomNodeText the {@link DomNodeText} of the page the match is based on
   * @param aPathSearchPattern the {@link SearchPattern} describing the path to the element
   * @param aPathSpot the {@link FindSpot} the path was found first
   * @param aSearchPattern the {@link SearchPattern} describing the element
   * @param aFoundElements the result list to add the found elements to
   */
  public ByImageTitleAttributeMatcher(DomNodeText aDomNodeText, SearchPattern aPathSearchPattern, FindSpot aPathSpot,
      SearchPattern aSearchPattern, WeightedControlList aFoundElements) {
    super(aDomNodeText, aPathSearchPattern, aPathSpot, aSearchPattern, aFoundElements, FoundType.BY_IMG_TITLE_ATTRIBUTE);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.backend.htmlunit.matcher.AbstractByAttributeMatcher#getAttributeValue(com.gargoylesoftware.htmlunit.html.HtmlElement)
   */
  @Override
  protected String getAttributeValue(HtmlElement aHtmlElement) {
    return aHtmlElement.getAttribute("title");
  }
}