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


package org.wetator.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.lang.StringUtils;
import org.wetator.Version;
import org.wetator.core.WetCommand;
import org.wetator.core.WetConfiguration;
import org.wetator.core.WetContext;
import org.wetator.core.WetEngine;
import org.wetator.core.WetProgressListener;
import org.wetator.exception.AssertionFailedException;

/**
 * Simple progress listener that writes to stdout.
 * 
 * @author rbri
 * @author frank.danek
 */
public class StdOutProgressListener implements WetProgressListener {

  private static final int DOTS_PER_LINE = 100;

  /** The output used. */
  protected Output output;
  private long stepsCount;
  private long errorCount;
  private long failureCount;
  private int dotCount;
  private int contextDeep;

  /**
   * The constructor.
   */
  public StdOutProgressListener() {
    output = new Output(new PrintWriter(System.out), "  ");
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.WetProgressListener#init(WetEngine)
   */
  @Override
  public void init(final WetEngine aWetEngine) {
    println(Version.getProductName() + " " + Version.getVersion());
    output.indent();
    println("using " + com.gargoylesoftware.htmlunit.Version.getProductName() + " version "
        + com.gargoylesoftware.htmlunit.Version.getProductVersion());

    final File tmpConfigFile = aWetEngine.getConfigFile();
    if (null != tmpConfigFile) {
      println("Config:     '" + tmpConfigFile.getAbsolutePath() + "'");
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.WetProgressListener#start(WetEngine)
   */
  @Override
  public void start(final WetEngine aWetEngine) {
    stepsCount = 0;
    errorCount = 0;
    failureCount = 0;
    contextDeep = 0;

    final WetConfiguration tmpConfiguration = aWetEngine.getWetConfiguration();
    if (tmpConfiguration != null) {
      if (StringUtils.isNotEmpty(tmpConfiguration.getProxyHost())) {
        println("    proxy:  '" + tmpConfiguration.getProxyHost() + ":" + tmpConfiguration.getProxyPort() + "'");
      }

      println("OutputDir:  '" + tmpConfiguration.getOutputDir().getAbsolutePath() + "'");

      boolean tmpFirst = true;
      for (String tmpTemplate : tmpConfiguration.getXslTemplates()) {
        if (tmpFirst) {
          println("Templates:  '" + tmpTemplate + "'");
          tmpFirst = false;
          output.indent().indent().indent().indent().indent().indent();
        } else {
          println("'" + tmpTemplate + "'");
        }
      }
    }
    output.unindent().unindent().unindent().unindent().unindent().unindent();

    if (aWetEngine.getTestFiles().isEmpty()) {
      println("TestFiles: none");
      return;
    }

    boolean tmpFirst = true;
    for (File tmpTestFile : aWetEngine.getTestFiles()) {
      if (tmpFirst) {
        println("TestFiles:  '" + tmpTestFile.getAbsolutePath() + "'");
        tmpFirst = false;
        output.indent().indent().indent().indent().indent().indent();
      } else {
        println("'" + tmpTestFile.getAbsolutePath() + "'");
      }
    }
    output.unindent().unindent().unindent().unindent().unindent().unindent();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.WetProgressListener#testCaseStart(String)
   */
  @Override
  public void testCaseStart(final String aTestName) {
    println("Test: '" + aTestName + "'");
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.WetProgressListener#testRunStart(String)
   */
  @Override
  public void testRunStart(final String aBrowserName) {
    output.indent();
    println(aBrowserName);
    dotCount = 1;
    contextDeep = 0;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.WetProgressListener#testFileStart(String)
   */
  @Override
  public void testFileStart(final String aFileName) {
    contextDeep++;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.WetProgressListener#executeCommandStart(org.wetator.core.WetContext,
   *      org.wetator.core.WetCommand)
   */
  @Override
  public void executeCommandStart(final WetContext aWetContext, final WetCommand aWommand) {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.WetProgressListener#executeCommandEnd()
   */
  @Override
  public void executeCommandEnd() {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.WetProgressListener#executeCommandError(java.lang.Throwable)
   */
  @Override
  public void executeCommandError(final Throwable aThrowable) {
    stepsCount++;
    errorCount++;
    printProgressSign("E");
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.WetProgressListener#executeCommandFailure(org.wetator.exception.AssertionFailedException)
   */
  @Override
  public void executeCommandFailure(final AssertionFailedException anAssertionFailedException) {
    stepsCount++;
    failureCount++;
    printProgressSign("F");
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.WetProgressListener#executeCommandSuccess()
   */
  @Override
  public void executeCommandSuccess() {
    stepsCount++;
    printProgressSign(".");
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.WetProgressListener#testFileEnd()
   */
  @Override
  public void testFileEnd() {
    contextDeep--;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.WetProgressListener#testRunEnd()
   */
  @Override
  public void testRunEnd() {
    println("");
    output.unindent();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.WetProgressListener#testCaseEnd()
   */
  @Override
  public void testCaseEnd() {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.WetProgressListener#end(WetEngine)
   */
  @Override
  public void end(final WetEngine aWetEngine) {
    // print summary
    println("");
    println("Steps: " + stepsCount + ",  Failures: " + failureCount + ",  Errors: " + errorCount);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.WetProgressListener#responseStored(java.lang.String)
   */
  @Override
  public void responseStored(final String aResponseFileName) {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.WetProgressListener#warn(java.lang.String, java.lang.String[])
   */
  @Override
  public void warn(final String aMessageKey, final String[] aParameterArray) {
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.WetProgressListener#info(java.lang.String, java.lang.String[])
   */
  @Override
  public void info(final String aMessageKey, final String[] aParameterArray) {
  }

  /**
   * The worker that does the real output.
   * 
   * @param aString the output
   */
  protected void println(final String aString) {
    try {
      output.println(aString);
      output.flush();
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * The worker that does the real output.
   * 
   * @param aString the output
   */
  protected void print(final String aString) {
    try {
      output.print(aString);
      output.flush();
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * The printing of the progress output.
   * 
   * @param aProgressSign the output
   */
  protected void printProgressSign(final String aProgressSign) {
    if (dotCount == DOTS_PER_LINE) {
      println(aProgressSign);
      dotCount = 1;
      return;
    }
    print(aProgressSign);
    dotCount++;
  }

  /**
   * @return the errorCount
   */
  public long getErrorCount() {
    return errorCount;
  }

  /**
   * @return the failureCount
   */
  public long getFailureCount() {
    return failureCount;
  }
}