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


package org.wetator.core;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.wetator.backend.IBrowser;
import org.wetator.backend.IBrowser.BrowserType;
import org.wetator.exception.ResourceException;

/**
 * @author frank.danek
 */
public class WetatorEngineExecuteTestsTest {

  private TestCase testCase1;
  private TestCase testCase2;

  private WetatorConfiguration configuration;

  private BrowserType browserType1;
  private BrowserType browserType2;

  private IBrowser browser;

  private WetatorEngine engine;

  private WetatorContext context;

  @Before
  public void setupMocks() {
    testCase1 = new TestCase("testCase1", new File("file1"));
    testCase2 = new TestCase("testCase2", new File("file2"));

    configuration = mock(WetatorConfiguration.class);

    browserType1 = BrowserType.INTERNET_EXPLORER_6;
    browserType2 = BrowserType.FIREFOX_3_6;

    browser = mock(IBrowser.class);

    context = mock(WetatorContext.class);

    engine = mock(WetatorEngine.class);

    when(configuration.getBrowserTypes()).thenReturn(Arrays.asList(browserType1, browserType2));

    when(engine.getConfiguration()).thenReturn(configuration);
    when(engine.getBrowser()).thenReturn(browser);
    when(engine.getTestCases()).thenReturn(Arrays.asList(testCase1, testCase2));
    when(engine.createWetatorContext(any(File.class), any(BrowserType.class))).thenReturn(context);
    doCallRealMethod().when(engine).executeTests();
  }

  /**
   * Test for the engine.<br/>
   * <br/>
   * Assertion: If everything is ok, all commands for all browsers of all tests should be executed.
   */
  @Test
  public void ok() {
    // run
    engine.executeTests();

    // assert
    InOrder tmpInOrder = inOrder(engine, context, browser, configuration);
    tmpInOrder.verify(engine).addDefaultProgressListeners();
    tmpInOrder.verify(engine).informListenersStart();
    tmpInOrder.verify(engine).informListenersTestCaseStart(testCase1.getName());
    assertTestRun(tmpInOrder, testCase1.getFile(), browserType1);
    assertTestRun(tmpInOrder, testCase1.getFile(), browserType2);
    tmpInOrder.verify(engine).informListenersTestCaseEnd();
    tmpInOrder.verify(engine).informListenersTestCaseStart(testCase2.getName());
    assertTestRun(tmpInOrder, testCase2.getFile(), browserType1);
    assertTestRun(tmpInOrder, testCase2.getFile(), browserType2);
    tmpInOrder.verify(engine).informListenersTestCaseEnd();
    tmpInOrder.verify(engine).informListenersEnd();

    verify(engine, never()).informListenersError(any(Throwable.class));
  }

  /**
   * Test for the engine.<br/>
   * <br/>
   * Assertion: If there was a {@link RuntimeException} starting a new session, the run for the current browser
   * should be aborted.
   */
  @Test
  public void browserStartNewSessionRuntimeException() {
    // setup
    doThrow(new RuntimeException("mocker")).doNothing().when(browser).startNewSession(browserType1);

    // run
    engine.executeTests();

    // assert
    InOrder tmpInOrder = inOrder(engine, context, browser, configuration);
    tmpInOrder.verify(engine).addDefaultProgressListeners();
    tmpInOrder.verify(engine).informListenersStart();
    tmpInOrder.verify(engine).informListenersTestCaseStart(testCase1.getName());
    tmpInOrder.verify(engine).informListenersTestRunStart(browserType1.getLabel());
    tmpInOrder.verify(browser).startNewSession(browserType1);
    tmpInOrder.verify(engine).informListenersError(any(RuntimeException.class));
    tmpInOrder.verify(engine).informListenersTestRunEnd();
    assertTestRun(tmpInOrder, testCase1.getFile(), browserType2);
    tmpInOrder.verify(engine).informListenersTestCaseEnd();
    tmpInOrder.verify(engine).informListenersTestCaseStart(testCase2.getName());
    assertTestRun(tmpInOrder, testCase2.getFile(), browserType1);
    assertTestRun(tmpInOrder, testCase2.getFile(), browserType2);
    tmpInOrder.verify(engine).informListenersTestCaseEnd();
    tmpInOrder.verify(engine).informListenersEnd();

    verify(engine, times(1)).informListenersError(any(Throwable.class));
  }

  /**
   * Test for the engine.<br/>
   * <br/>
   * Assertion: If there was a {@link ResourceException} executing a test file, the run for the current browser
   * should be aborted.
   */
  @Test
  public void contextExecuteResourceException() {
    // setup
    doThrow(new ResourceException("mocker")).doNothing().when(context).execute();

    // run
    engine.executeTests();

    // assert
    InOrder tmpInOrder = inOrder(engine, context, browser, configuration);
    tmpInOrder.verify(engine).addDefaultProgressListeners();
    tmpInOrder.verify(engine).informListenersStart();
    tmpInOrder.verify(engine).informListenersTestCaseStart(testCase1.getName());
    tmpInOrder.verify(engine).informListenersTestRunStart(browserType1.getLabel());
    tmpInOrder.verify(browser).startNewSession(browserType1);
    tmpInOrder.verify(engine).informListenersError(any(ResourceException.class));
    tmpInOrder.verify(engine).informListenersTestRunEnd();
    assertTestRun(tmpInOrder, testCase1.getFile(), browserType2);
    tmpInOrder.verify(engine).informListenersTestCaseEnd();
    tmpInOrder.verify(engine).informListenersTestCaseStart(testCase2.getName());
    assertTestRun(tmpInOrder, testCase2.getFile(), browserType1);
    assertTestRun(tmpInOrder, testCase2.getFile(), browserType2);
    tmpInOrder.verify(engine).informListenersTestCaseEnd();
    tmpInOrder.verify(engine).informListenersEnd();

    verify(engine, times(1)).informListenersError(any(Throwable.class));
  }

  // TODO what about WetatorException?

  /**
   * Test for the engine.<br/>
   * <br/>
   * Assertion: If there was a {@link RuntimeException} executing a test file, the run for the current browser
   * should be aborted.
   */
  @Test
  public void contextExecuteRuntimeException() {
    // setup
    doThrow(new RuntimeException("mocker")).doNothing().when(context).execute();

    // run
    engine.executeTests();

    // assert
    InOrder tmpInOrder = inOrder(engine, context, browser, configuration);
    tmpInOrder.verify(engine).addDefaultProgressListeners();
    tmpInOrder.verify(engine).informListenersStart();
    tmpInOrder.verify(engine).informListenersTestCaseStart(testCase1.getName());
    tmpInOrder.verify(engine).informListenersTestRunStart(browserType1.getLabel());
    tmpInOrder.verify(browser).startNewSession(browserType1);
    tmpInOrder.verify(engine).informListenersError(any(RuntimeException.class));
    tmpInOrder.verify(engine).informListenersTestRunEnd();
    assertTestRun(tmpInOrder, testCase1.getFile(), browserType2);
    tmpInOrder.verify(engine).informListenersTestCaseEnd();
    tmpInOrder.verify(engine).informListenersTestCaseStart(testCase2.getName());
    assertTestRun(tmpInOrder, testCase2.getFile(), browserType1);
    assertTestRun(tmpInOrder, testCase2.getFile(), browserType2);
    tmpInOrder.verify(engine).informListenersTestCaseEnd();
    tmpInOrder.verify(engine).informListenersEnd();

    verify(engine, times(1)).informListenersError(any(Throwable.class));
  }

  private void assertTestRun(InOrder anInOrder, File aFile, BrowserType aBrowserType) {
    anInOrder.verify(engine).informListenersTestRunStart(aBrowserType.getLabel());
    anInOrder.verify(browser).startNewSession(aBrowserType);
    anInOrder.verify(engine).createWetatorContext(aFile, aBrowserType);
    anInOrder.verify(context).execute();
    anInOrder.verify(engine).informListenersTestRunEnd();
  }
}