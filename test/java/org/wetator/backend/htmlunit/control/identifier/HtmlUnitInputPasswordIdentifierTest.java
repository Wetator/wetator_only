/*
 * Copyright (c) 2008-2013 wetator.org
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
import java.util.ArrayList;
import java.util.List;

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
public class HtmlUnitInputPasswordIdentifierTest extends AbstractHtmlUnitControlIdentifierTest {

  @Before
  public void setupIdentifier() {
    identifier = new HtmlUnitInputPasswordIdentifier();
  }

  @Test
  public void byId() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='myId' name='myName' type='password'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("myId"));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlPasswordInput (id='myId') (name='myName')] found by: BY_ID coverage: 0 distance: 0 start: 0", tmpFound
            .getEntriesSorted().get(0).toString());
  }

  @Test
  public void byName() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='myId' name='myName' type='password'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("myName"));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlPasswordInput (id='myId') (name='myName')] found by: BY_NAME coverage: 0 distance: 0 start: 0", tmpFound
            .getEntriesSorted().get(0).toString());
  }

  @Test
  public void byLabelTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<input id='otherId' name='otherName' type='password'>"
        + "<p>Marker</p>"
        + "<input id='myId' name='myName' type='password'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Marker"));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "otherId", "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlPasswordInput (id='myId') (name='myName')] found by: BY_LABEL_TEXT coverage: 0 distance: 0 start: 6",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byWholeTextBefore() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<p>Marker1</p>"
        + "<input id='otherId' name='otherName' type='checkbox'>"
        + "<p>Marker2</p>"
        + "<input id='myId' name='myName' type='password'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Marker1"));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "otherId", "myId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlPasswordInput (id='myId') (name='myName')] found by: BY_TEXT coverage: 8 distance: 15 start: 15",
        tmpFound.getEntriesSorted().get(0).toString());
  }

  @Test
  public void byHtmlLabel_Text() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='labelId' for='myId'>Label</label>"
        + "<input id='myId' name='myName' type='password'>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Label"));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "labelId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlPasswordInput (id='myId') (name='myName')] found by: BY_LABEL coverage: 0 distance: 0 start: 5", tmpFound
            .getEntriesSorted().get(0).toString());
  }

  @Test
  public void byHtmlLabelChild_Text() throws IOException, InvalidInputException {
    // @formatter:off
    String tmpHtmlCode = "<html><body>"
        + "<form action='test'>"
        + "<label id='labelId'>Label"
        + "<input id='myId' name='myName' type='password'>"
        + "</label>"
        + "</form>"
        + "</body></html>";
    // @formatter:on

    List<SecretString> tmpSearch = new ArrayList<SecretString>();
    tmpSearch.add(new SecretString("Label"));

    WeightedControlList tmpFound = identify(tmpHtmlCode, new WPath(tmpSearch), "labelId");

    Assert.assertEquals(1, tmpFound.getEntriesSorted().size());

    Assert.assertEquals(
        "[HtmlPasswordInput (id='myId') (name='myName')] found by: BY_LABEL coverage: 0 distance: 0 start: 5", tmpFound
            .getEntriesSorted().get(0).toString());
  }
}
