/*
 * Copyright (c) 2008-2016 wetator.org
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

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.wetator.backend.WPath;
import org.wetator.backend.WeightedControlList;
import org.wetator.exception.InvalidInputException;
import org.wetator.util.SecretString;

/**
 * @author rbri
 * @author frank.danek
 */
public class HtmlUnitAnchorIdentifierTest extends AbstractHtmlUnitControlIdentifierTest {

  @Before
  public void setupIdentifier() {
    identifier = new HtmlUnitAnchorIdentifier();
  }

  @Test
  public void byId() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<a id='myId' href='snoopy.php'>TestAnchor</a>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("myId");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlAnchor 'TestAnchor' (id='myId')] found by: BY_ID coverage: 0 distance: 0 start: 0 index: 5",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byName() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<a id='myId' name='MyName' href='snoopy.php'>TestAnchor</a>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("MyName");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlAnchor 'TestAnchor' (id='myId') (name='MyName')] found by: BY_NAME coverage: 0 distance: 0 start: 0 index: 5",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byText() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<a id='myId' name='MyName' href='snoopy.php'>TestAnchor</a>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("TestAnchor");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlAnchor 'TestAnchor' (id='myId') (name='MyName')] found by: BY_LABEL_TEXT coverage: 0 distance: 0 start: 0 index: 5",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byTitle() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<a id='myId' name='MyName' href='snoopy.php' title='AnchorTitle'>TestAnchor</a>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("AnchorTitle");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlAnchor 'TestAnchor' (id='myId') (name='MyName')] found by: BY_TITLE_ATTRIBUTE coverage: 0 distance: 0 start: 0 index: 5",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byIdNameText() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<a id='myAnchor' name='myAnchor' href='snoopy.php'>myAnchor</a>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("myAnchor");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "myAnchor");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlAnchor 'myAnchor' (id='myAnchor') (name='myAnchor')] found by: BY_ID coverage: 0 distance: 0 start: 0 index: 5",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byInnerImage_Name() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img src='picture.png' name='MyImageName'>"
        + "</a>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("MyImageName");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlAnchor 'image: picture.png' (id='myId') (name='MyName')] found by: BY_INNER_NAME coverage: 0 distance: 0 start: 0 index: 5",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byInnerImage_Alt() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img src='picture.png' name='MyImageName' alt='MyAlt'>"
        + "</a>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("MyAlt");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlAnchor 'image: picture.png' (id='myId') (name='MyName')] found by: BY_LABEL_TEXT coverage: 0 distance: 0 start: 0 index: 5",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byInnerImage_Title() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img src='picture.png' name='MyImageName' title='MyTitle'>"
        + "</a>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("MyTitle");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlAnchor 'image: picture.png' (id='myId') (name='MyName')] found by: BY_INNER_IMG_TITLE_ATTRIBUTE coverage: 0 distance: 0 start: 0 index: 5",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byInnerImage_Src() throws IOException, InvalidInputException {
    // @formatter:off
    final String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<a id='myId' name='MyName' href='snoopy.php'>"
        + "<img src='picture.png' name='MyImageName' title='MyTitle'>"
        + "</a>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    final SecretString tmpSearch = new SecretString("picture.png");

    final WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch, config), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlAnchor 'image: picture.png' (id='myId') (name='MyName')] found by: BY_INNER_IMG_SRC_ATTRIBUTE coverage: 0 distance: 0 start: 0 index: 5",
        tmpFound.getEntriesSorted().get(0).toString());
  }
}
