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


package org.wetator.backend.htmlunit.control.identifier;

import java.util.ArrayList;
import java.util.List;

import org.wetator.backend.WPath;
import org.wetator.backend.control.Control;
import org.wetator.backend.htmlunit.control.HtmlUnitTextArea;
import org.wetator.backend.htmlunit.matcher.AbstractHtmlUnitElementMatcher;
import org.wetator.backend.htmlunit.matcher.ByHtmlLabelMatcher;
import org.wetator.backend.htmlunit.matcher.ByIdMatcher;
import org.wetator.backend.htmlunit.matcher.ByLabelTextBeforeMatcher;
import org.wetator.backend.htmlunit.matcher.ByNameAttributeMatcher;
import org.wetator.backend.htmlunit.matcher.ByTableCoordinatesMatcher;
import org.wetator.backend.htmlunit.matcher.ByWholeTextBeforeMatcher;
import org.wetator.backend.htmlunit.util.FindSpot;
import org.wetator.core.searchpattern.SearchPattern;
import org.wetator.util.SecretString;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlLabel;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;

/**
 * The identifier for a {@link HtmlUnitTextArea}.<br />
 * It can be identified by:
 * <ul>
 * <li>the whole text before</li>
 * <li>the label text before</li>
 * <li>it's name</li>
 * <li>it's id</li>
 * <li>a label</li>
 * <li>table coordinates</li>
 * </ul>
 * 
 * @author frank.danek
 */
public class HtmlUnitTextAreaIdentifier extends AbstractMatcherBasedIdentifier {

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.htmlunit.control.identifier.AbstractHtmlUnitControlIdentifier#isHtmlElementSupported(com.gargoylesoftware.htmlunit.html.HtmlElement)
   */
  @Override
  public boolean isHtmlElementSupported(final HtmlElement aHtmlElement) {
    return (aHtmlElement instanceof HtmlTextArea) || (aHtmlElement instanceof HtmlLabel);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.htmlunit.control.identifier.AbstractMatcherBasedIdentifier#addMatchers(org.wetator.backend.WPath,
   *      com.gargoylesoftware.htmlunit.html.HtmlElement, java.util.List)
   */
  @Override
  protected void addMatchers(final WPath aWPath, final HtmlElement aHtmlElement,
      final List<AbstractHtmlUnitElementMatcher> aMatchers) {
    final SearchPattern tmpPathSearchPattern = SearchPattern.createFromList(aWPath.getPathNodes());
    final FindSpot tmpPathSpot = htmlPageIndex.firstOccurence(tmpPathSearchPattern);

    if (null == tmpPathSpot) {
      return;
    }

    if (aWPath.getLastNode() != null) {
      // normal matchers
      final SearchPattern tmpSearchPattern = aWPath.getLastNode().getSearchPattern();
      final List<SecretString> tmpWholePath = new ArrayList<SecretString>(aWPath.getPathNodes());
      tmpWholePath.add(aWPath.getLastNode());
      final SearchPattern tmpWholePathSearchPattern = SearchPattern.createFromList(tmpWholePath);
      if (aHtmlElement instanceof HtmlTextArea) {
        // whole text before
        aMatchers.add(new ByWholeTextBeforeMatcher(htmlPageIndex, tmpPathSearchPattern, tmpPathSpot,
            tmpWholePathSearchPattern));

        aMatchers.add(new ByLabelTextBeforeMatcher(htmlPageIndex, tmpPathSearchPattern, tmpPathSpot, tmpSearchPattern));
        aMatchers.add(new ByNameAttributeMatcher(htmlPageIndex, tmpPathSearchPattern, tmpPathSpot, tmpSearchPattern));
        aMatchers.add(new ByIdMatcher(htmlPageIndex, tmpPathSearchPattern, tmpPathSpot, tmpSearchPattern));

      } else if (aHtmlElement instanceof HtmlLabel) {
        aMatchers.add(new ByHtmlLabelMatcher(htmlPageIndex, tmpPathSearchPattern, tmpPathSpot, tmpSearchPattern,
            HtmlTextArea.class));
      }
    } else if (!aWPath.getTableCoordinates().isEmpty()) {
      // table matcher
      // we have to use the reversed table coordinates to work from the inner most (last) to the outer most (first)
      aMatchers.add(new ByTableCoordinatesMatcher(htmlPageIndex, tmpPathSearchPattern, tmpPathSpot, aWPath
          .getTableCoordinatesReversed(), HtmlTextArea.class));
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.htmlunit.control.identifier.AbstractMatcherBasedIdentifier#createControl(com.gargoylesoftware.htmlunit.html.HtmlElement)
   */
  @Override
  protected Control createControl(final HtmlElement aHtmlElement) {
    return new HtmlUnitTextArea((HtmlTextArea) aHtmlElement);
  }
}