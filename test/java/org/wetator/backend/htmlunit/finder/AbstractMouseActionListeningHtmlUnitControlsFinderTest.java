/*
 * Copyright (c) 2008-2020 wetator.org
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


package org.wetator.backend.htmlunit.finder;

import static org.wetator.backend.htmlunit.finder.MouseActionHtmlCodeCreator.CONTENT;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.junit.Before;
import org.junit.BeforeClass;
import org.wetator.backend.WPath;
import org.wetator.backend.WeightedControlList;
import org.wetator.backend.htmlunit.HtmlUnitControlRepository;
import org.wetator.backend.htmlunit.MouseAction;
import org.wetator.backend.htmlunit.control.HtmlUnitAnchor;
import org.wetator.backend.htmlunit.control.HtmlUnitButton;
import org.wetator.backend.htmlunit.control.HtmlUnitImage;
import org.wetator.backend.htmlunit.control.HtmlUnitInputButton;
import org.wetator.backend.htmlunit.control.HtmlUnitInputCheckBox;
import org.wetator.backend.htmlunit.control.HtmlUnitInputFile;
import org.wetator.backend.htmlunit.control.HtmlUnitInputImage;
import org.wetator.backend.htmlunit.control.HtmlUnitInputPassword;
import org.wetator.backend.htmlunit.control.HtmlUnitInputRadioButton;
import org.wetator.backend.htmlunit.control.HtmlUnitInputReset;
import org.wetator.backend.htmlunit.control.HtmlUnitInputSubmit;
import org.wetator.backend.htmlunit.control.HtmlUnitInputText;
import org.wetator.backend.htmlunit.control.HtmlUnitOption;
import org.wetator.backend.htmlunit.control.HtmlUnitOptionGroup;
import org.wetator.backend.htmlunit.control.HtmlUnitSelect;
import org.wetator.backend.htmlunit.control.HtmlUnitTextArea;
import org.wetator.backend.htmlunit.control.identifier.AbstractMatcherBasedIdentifier;
import org.wetator.backend.htmlunit.finder.WeightedControlListEntryAssert.SortedEntryExpectation;
import org.wetator.backend.htmlunit.util.HtmlPageIndex;
import org.wetator.backend.htmlunit.util.PageUtil;
import org.wetator.core.WetatorConfiguration;
import org.wetator.exception.InvalidInputException;
import org.wetator.util.SecretString;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Common ground for {@link MouseActionListeningHtmlUnitControlsFinder} tests.
 *
 * @author tobwoerk
 */
public abstract class AbstractMouseActionListeningHtmlUnitControlsFinderTest {

  private WetatorConfiguration config;
  private HtmlUnitControlRepository repository;

  protected MouseAction mouseAction;

  protected MouseActionListeningHtmlUnitControlsFinder finder;
  // FIXME [UNKNOWN] remove as soon as included in MouseActionListeningHtmlUnitControlsFinder
  protected UnknownHtmlUnitControlsFinder finderUnknown;

  static {
    setMouseActionInCreator();
  }

  @BeforeClass
  public static void setMouseActionInCreator() {
    MouseActionHtmlCodeCreator.resetOnMouseAction();
  }

  @Before
  public void createWetatorConfiguration() {
    final Properties tmpProperties = new Properties();
    tmpProperties.setProperty(WetatorConfiguration.PROPERTY_BASE_URL, "http://localhost/");
    config = new WetatorConfiguration(new File("."), tmpProperties, null);
  }

  @Before
  public void createControlRepository() {
    repository = new HtmlUnitControlRepository();
    repository.add(HtmlUnitAnchor.class);
    repository.add(HtmlUnitButton.class);
    repository.add(HtmlUnitImage.class);
    repository.add(HtmlUnitInputButton.class);
    repository.add(HtmlUnitInputCheckBox.class);
    repository.add(HtmlUnitInputFile.class);
    repository.add(HtmlUnitInputImage.class);
    repository.add(HtmlUnitInputPassword.class);
    repository.add(HtmlUnitInputRadioButton.class);
    repository.add(HtmlUnitInputReset.class);
    repository.add(HtmlUnitInputSubmit.class);
    repository.add(HtmlUnitInputText.class);
    repository.add(HtmlUnitOption.class);
    repository.add(HtmlUnitOptionGroup.class);
    repository.add(HtmlUnitSelect.class);
    repository.add(HtmlUnitTextArea.class);
  }

  public void checkFoundElements(final Object anHtmlCode, final SortedEntryExpectation anExpected) throws Exception {
    if (anHtmlCode instanceof MouseActionHtmlCodeBuilder) {
      checkFoundElements(((MouseActionHtmlCodeBuilder) anHtmlCode).build(), anExpected);
    } else if (anHtmlCode instanceof MouseActionHtmlCodeSelectBuilder) {
      checkFoundElements(((MouseActionHtmlCodeSelectBuilder) anHtmlCode).build(), anExpected);
    } else if (anHtmlCode instanceof MouseActionHtmlCodeTableBuilder) {
      checkFoundElements(((MouseActionHtmlCodeTableBuilder) anHtmlCode).build(), anExpected);
    } else if (anHtmlCode instanceof String) {
      checkFoundElements((String) anHtmlCode, anExpected);
    } else {
      throw new RuntimeException("'" + anHtmlCode + "' of wrong type (" + anHtmlCode.getClass().getSimpleName() + ").");
    }
  }

  private void checkFoundElements(final String anHtmlCode, final SortedEntryExpectation anExpected) throws Exception {
    setup(anHtmlCode);
    final WeightedControlList tmpFound = find();
    assertion(anExpected, tmpFound);
  }

  protected void setup(final String anHtmlCode) throws IOException {
    final HtmlPage tmpHtmlPage = PageUtil
        .constructHtmlPage(MouseActionHtmlCodeCreator.pageStart() + anHtmlCode + MouseActionHtmlCodeCreator.pageEnd());
    final HtmlPageIndex tmpHtmlPageIndex = new HtmlPageIndex(tmpHtmlPage);

    finder = new MouseActionListeningHtmlUnitControlsFinder(tmpHtmlPageIndex, null, mouseAction, repository);
    finderUnknown = new UnknownHtmlUnitControlsFinder(tmpHtmlPageIndex, repository);
  }

  public void setMouseAction(final MouseAction aMouseAction) {
    mouseAction = aMouseAction;
  }

  @SafeVarargs
  protected final void addIdentifiers(final Class<? extends AbstractMatcherBasedIdentifier>... anIdentifiers) {
    for (Class<? extends AbstractMatcherBasedIdentifier> tmpIdentifier : anIdentifiers) {
      finder.addIdentifier(tmpIdentifier);
    }
  }

  protected final WeightedControlList find() throws InvalidInputException {
    final WPath tmpWPath = new WPath(new SecretString(getWPath()), config);
    final WeightedControlList tmpList = finder.find(tmpWPath);
    tmpList.addAll(finderUnknown.find(tmpWPath));
    return tmpList;
  }

  protected String getWPath() {
    return CONTENT;
  }

  protected final void assertion(final SortedEntryExpectation anExpected, final WeightedControlList anActual) {
    WeightedControlListEntryAssert.assertEntriesSorted(anExpected, anActual);
  }

  protected static MouseActionHtmlCodeBuilder a(final String anId, final MouseActionHtmlCodeBuilder aContent) {
    return a(anId, aContent.build());
  }

  protected static MouseActionHtmlCodeBuilder a(final String anId, final String aContent) {
    return new MouseActionHtmlCodeBuilder().a(anId, aContent);
  }

  protected static MouseActionHtmlCodeBuilder a(final String anId) {
    return new MouseActionHtmlCodeBuilder().a(anId);
  }

  protected static MouseActionHtmlCodeBuilder button(final String anId, final MouseActionHtmlCodeBuilder aContent) {
    return button(anId, aContent.build());
  }

  protected static MouseActionHtmlCodeBuilder button(final String anId, final String aContent) {
    return new MouseActionHtmlCodeBuilder().button(anId, aContent);
  }

  protected static MouseActionHtmlCodeBuilder button(final String anId) {
    return new MouseActionHtmlCodeBuilder().button(anId);
  }

  protected static MouseActionHtmlCodeBuilder checkbox(final String anId) {
    return new MouseActionHtmlCodeBuilder().checkbox(anId);
  }

  protected static MouseActionHtmlCodeBuilder div(final String anId, final MouseActionHtmlCodeBuilder aContent) {
    return div(anId, aContent.build());
  }

  protected static MouseActionHtmlCodeBuilder div(final String anId, final String aContent) {
    return new MouseActionHtmlCodeBuilder().div(anId, aContent);
  }

  protected static MouseActionHtmlCodeBuilder div(final String anId) {
    return new MouseActionHtmlCodeBuilder().div(anId);
  }

  protected static MouseActionHtmlCodeBuilder image(final String anId, final String anAltText) {
    return new MouseActionHtmlCodeBuilder().image(anId, anAltText);
  }

  protected static MouseActionHtmlCodeBuilder image(final String anId) {
    return new MouseActionHtmlCodeBuilder().image(anId);
  }

  protected static MouseActionHtmlCodeBuilder inputButton(final String anId, final String aValue) {
    return new MouseActionHtmlCodeBuilder().inputButton(anId, aValue);
  }

  protected static MouseActionHtmlCodeBuilder inputButton(final String anId) {
    return new MouseActionHtmlCodeBuilder().inputButton(anId);
  }

  protected static MouseActionHtmlCodeBuilder inputImage(final String anId, final String anAltTetxValue) {
    return new MouseActionHtmlCodeBuilder().inputImage(anId, anAltTetxValue);
  }

  protected static MouseActionHtmlCodeBuilder inputImage(final String anId) {
    return new MouseActionHtmlCodeBuilder().inputImage(anId);
  }

  protected static MouseActionHtmlCodeBuilder inputReset(final String anId, final String aValue) {
    return new MouseActionHtmlCodeBuilder().inputReset(anId, aValue);
  }

  protected static MouseActionHtmlCodeBuilder inputReset(final String anId) {
    return new MouseActionHtmlCodeBuilder().inputReset(anId);
  }

  protected static MouseActionHtmlCodeBuilder inputSubmit(final String anId) {
    return new MouseActionHtmlCodeBuilder().inputSubmit(anId);
  }

  protected static MouseActionHtmlCodeBuilder inputText(final String anId, final String aPlaceholder) {
    return new MouseActionHtmlCodeBuilder().inputText(anId, aPlaceholder);
  }

  protected static MouseActionHtmlCodeBuilder inputText(final String anId) {
    return new MouseActionHtmlCodeBuilder().inputText(anId);
  }

  protected static MouseActionHtmlCodeBuilder label(final String anId, final MouseActionHtmlCodeBuilder aContent) {
    return label(anId, aContent.build());
  }

  protected static MouseActionHtmlCodeBuilder label(final String aForId, final String aContent) {
    return new MouseActionHtmlCodeBuilder().label(aForId, aContent);
  }

  protected static MouseActionHtmlCodeBuilder label(final String aForId) {
    return new MouseActionHtmlCodeBuilder().label(aForId);
  }

  protected static MouseActionHtmlCodeBuilder radio(final String anId) {
    return new MouseActionHtmlCodeBuilder().radio(anId);
  }

  protected static MouseActionHtmlCodeSelectBuilder select(final String anId, final String aName) {
    return MouseActionHtmlCodeSelectBuilder.select(anId, aName);
  }

  protected static MouseActionHtmlCodeSelectBuilder select(final String anId) {
    return MouseActionHtmlCodeSelectBuilder.select(anId);
  }

  protected static MouseActionHtmlCodeBuilder span(final String anId, final MouseActionHtmlCodeBuilder aContent) {
    return span(anId, aContent.build());
  }

  protected static MouseActionHtmlCodeBuilder span(final String anId, final String aContent) {
    return new MouseActionHtmlCodeBuilder().span(anId, aContent);
  }

  protected static MouseActionHtmlCodeBuilder span(final String anId) {
    return new MouseActionHtmlCodeBuilder().span(anId);
  }

  protected static MouseActionHtmlCodeTableBuilder table(final String anId) {
    return MouseActionHtmlCodeTableBuilder.table(anId);
  }
}
