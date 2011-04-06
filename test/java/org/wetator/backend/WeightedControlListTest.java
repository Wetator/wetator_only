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


package org.wetator.backend;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.wetator.backend.WeightedControlList.Entry;
import org.wetator.backend.htmlunit.control.HtmlUnitAnchor;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl;
import org.wetator.backend.htmlunit.util.PageUtil;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author rbri
 */
public class WeightedControlListTest {

  @Test
  public void testIsEmpty() {
    WeightedControlList tmpWeightedControlList = new WeightedControlList();

    Assert.assertTrue(tmpWeightedControlList.isEmpty());
    Assert.assertTrue(tmpWeightedControlList.getEntriesSorted().isEmpty());
  }

  @Test
  public void testOneEntry() throws IOException {
    HtmlUnitBaseControl<?> tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    WeightedControlList tmpWeightedControlList = new WeightedControlList();

    tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 100, 11, 1);

    Assert.assertFalse(tmpWeightedControlList.isEmpty());
    Assert.assertFalse(tmpWeightedControlList.getEntriesSorted().isEmpty());
  }

  @Test
  public void testTwoEntries() throws IOException {
    HtmlUnitBaseControl<?> tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    WeightedControlList tmpWeightedControlList = new WeightedControlList();

    tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 100, 11, 1);
    tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 200, 11, 1);

    Assert.assertFalse(tmpWeightedControlList.isEmpty());

    Assert.assertFalse(tmpWeightedControlList.getEntriesSorted().isEmpty());
  }

  @Test
  public void testGetElementsSorted_Distance() throws IOException {
    WeightedControlList tmpWeightedControlList = new WeightedControlList();

    HtmlUnitBaseControl<?> tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 100, 11, 1);

    tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 100, 12, 1);

    tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 100, 10, 1);

    List<Entry> tmpSorted = tmpWeightedControlList.getEntriesSorted();

    Assert.assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID coverage: 100 distance: 10 start: 1",
        tmpSorted.get(0).toString());
    Assert.assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID coverage: 100 distance: 11 start: 1",
        tmpSorted.get(1).toString());
    Assert.assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID coverage: 100 distance: 12 start: 1",
        tmpSorted.get(2).toString());
  }

  @Test
  public void testGetElementsSorted_Coverage() throws IOException {
    WeightedControlList tmpWeightedControlList = new WeightedControlList();

    HtmlUnitBaseControl<?> tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 9, 11, 1);

    tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 10, 12, 1);

    tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 4, 10, 1);

    tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 4, 11, 1);

    List<Entry> tmpSorted = tmpWeightedControlList.getEntriesSorted();

    Assert.assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID coverage: 4 distance: 10 start: 1", tmpSorted.get(0)
        .toString());
    Assert.assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID coverage: 4 distance: 11 start: 1", tmpSorted.get(1)
        .toString());
    Assert.assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID coverage: 9 distance: 11 start: 1", tmpSorted.get(2)
        .toString());
    Assert.assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID coverage: 10 distance: 12 start: 1", tmpSorted
        .get(3).toString());
  }

  @Test
  public void testGetElementsSorted_FoundType() throws IOException {
    WeightedControlList tmpWeightedControlList = new WeightedControlList();

    HtmlUnitBaseControl<?> tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 9, 11, 1);

    tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_LABEL, 10, 12, 1);

    tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 4, 10, 1);

    tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 4, 11, 1);

    List<Entry> tmpSorted = tmpWeightedControlList.getEntriesSorted();

    Assert.assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID coverage: 4 distance: 10 start: 1", tmpSorted.get(0)
        .toString());
    Assert.assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID coverage: 4 distance: 11 start: 1", tmpSorted.get(1)
        .toString());
    Assert.assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID coverage: 9 distance: 11 start: 1", tmpSorted.get(2)
        .toString());
    Assert.assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_LABEL coverage: 10 distance: 12 start: 1", tmpSorted
        .get(3).toString());
  }

  @Test
  public void testGetElementsSorted_Start() throws IOException {
    WeightedControlList tmpWeightedControlList = new WeightedControlList();

    HtmlUnitBaseControl<?> tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 1, 1, 3);

    tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 1, 1, 1);

    tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 1, 1, 4);

    tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 1, 1, 2);

    List<Entry> tmpSorted = tmpWeightedControlList.getEntriesSorted();

    Assert.assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID coverage: 1 distance: 1 start: 1", tmpSorted.get(0)
        .toString());
    Assert.assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID coverage: 1 distance: 1 start: 2", tmpSorted.get(1)
        .toString());
    Assert.assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID coverage: 1 distance: 1 start: 3", tmpSorted.get(2)
        .toString());
    Assert.assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID coverage: 1 distance: 1 start: 4", tmpSorted.get(3)
        .toString());
  }

  @Test
  public void testGetElementsSorted_SameControl() throws IOException {
    WeightedControlList tmpWeightedControlList = new WeightedControlList();

    HtmlUnitBaseControl<?> tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 9, 11, 1);
    tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_LABEL, 10, 12, 1);
    tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 4, 10, 1);

    tmpControl = new HtmlUnitAnchor(constructHtmlAnchor());
    tmpWeightedControlList.add(tmpControl, WeightedControlList.FoundType.BY_ID, 4, 11, 1);

    List<Entry> tmpSorted = tmpWeightedControlList.getEntriesSorted();

    Assert.assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID coverage: 4 distance: 10 start: 1", tmpSorted.get(0)
        .toString());
    Assert.assertEquals("[HtmlAnchor 'AnchorText'] found by: BY_ID coverage: 4 distance: 11 start: 1", tmpSorted.get(1)
        .toString());
  }

  private HtmlAnchor constructHtmlAnchor() throws IOException {
    String tmpHtmlCode = "<html><body>" + "<a href='wet.html'>AnchorText</a>" + "</body></html>";
    HtmlPage tmpHtmlPage = PageUtil.constructHtmlPage(tmpHtmlCode);

    Iterator<HtmlElement> tmpHtmlElements = tmpHtmlPage.getHtmlElementDescendants().iterator();

    tmpHtmlElements.next();
    tmpHtmlElements.next();
    tmpHtmlElements.next();

    HtmlAnchor tmpAnchor = (HtmlAnchor) tmpHtmlElements.next();
    return tmpAnchor;
  }
}