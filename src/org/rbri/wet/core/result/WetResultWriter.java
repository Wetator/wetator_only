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


package org.rbri.wet.core.result;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rbri.wet.Version;
import org.rbri.wet.commandset.WetCommandSet;
import org.rbri.wet.core.Parameter;
import org.rbri.wet.core.WetCommand;
import org.rbri.wet.core.WetConfiguration;
import org.rbri.wet.core.WetContext;
import org.rbri.wet.core.WetEngine;
import org.rbri.wet.core.WetProgressListener;
import org.rbri.wet.core.variable.Variable;
import org.rbri.wet.exception.AssertionFailedException;
import org.rbri.wet.i18n.Messages;
import org.rbri.wet.util.Output;
import org.rbri.wet.util.SecretString;
import org.rbri.wet.util.StringUtil;
import org.rbri.wet.util.XmlUtil;

/**
 * The class that generates the output.
 * 
 * @author rbri
 * @author frank.danek
 */
public class WetResultWriter implements WetProgressListener {
  private static final Log LOG = LogFactory.getLog(WetResultWriter.class);

  private static final String TAG_WET = "wet";
  private static final String TAG_ABOUT = "about";
  private static final String TAG_PRODUCT = "product";
  private static final String TAG_VERSION = "version";
  private static final String TAG_BUILD = "build";
  private static final String TAG_START_TIME = "startTime";
  private static final String TAG_TEST_FILE = "testFile";
  private static final String TAG_EXECUTION_TIME = "executionTime";
  private static final String TAG_TESTCASE = "testcase";
  private static final String TAG_TESTRUN = "testrun";
  private static final String TAG_TESTFILE = "testfile";
  private static final String TAG_COMMAND = "command";
  private static final String TAG_FIRST_PARAM = "param0";
  private static final String TAG_SECOND_PARAM = "param1";
  private static final String TAG_RESPONSE = "response";
  private static final String TAG_LOG = "log";
  private static final String TAG_LEVEL = "level";
  private static final String TAG_MESSAGE = "message";
  private static final String TAG_ERROR = "error";
  private static final String TAG_CONFIGURATION = "configuration";
  private static final String TAG_VARIABLES = "variables";
  private static final String TAG_VARIABLE = "variable";
  private static final String TAG_PROPERTY = "property";
  private static final String TAG_COMMAND_SET = "commandSet";

  private Writer writer;
  private Output output;
  private XmlUtil xmlUtil;
  private File resultFile;
  private File outputDir;
  private List<String> xslTemplates;

  private long tagId;
  private long wetExecutionStartTime;
  private long commandExecutionStartTime;

  /**
   * Constructor
   */
  public WetResultWriter() {
    tagId = 0;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#start(WetEngine)
   */
  @Override
  public void start(WetEngine aWetEngine) {
    try {
      WetConfiguration tmpWetConfiguration = aWetEngine.getWetConfiguration();

      outputDir = tmpWetConfiguration.getOutputDir();
      xslTemplates = tmpWetConfiguration.getXslTemplates();
      resultFile = new File(outputDir, "wetresult.xml");

      writer = new FileWriterWithEncoding(resultFile, "UTF-8");
      output = new Output(writer, "  ");
      xmlUtil = new XmlUtil("UTF-8");

      // start writing
      output.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
      output.println();

      printlnStartTag(TAG_WET);

      printlnStartTag(TAG_ABOUT);

      printlnNode(TAG_PRODUCT, Version.getProductName());
      printlnNode(TAG_VERSION, Version.getVersion());
      printlnNode(TAG_BUILD, Version.getBuild());

      printlnEndTag(TAG_ABOUT);

      // print the configuration
      printlnStartTag(TAG_CONFIGURATION);

      printConfigurationProperty(WetConfiguration.PROPERTY_BASE_URL, tmpWetConfiguration.getBaseUrl());
      printConfigurationProperty(WetConfiguration.PROPERTY_ACCEPT_LANGUAGE, tmpWetConfiguration.getAcceptLanaguage());
      printConfigurationProperty(WetConfiguration.PROPERTY_OUTPUT_DIR, tmpWetConfiguration.getOutputDir()
          .getAbsolutePath());
      printConfigurationProperty(WetConfiguration.PROPERTY_PROXY_HOST, tmpWetConfiguration.getProxyHost());
      printConfigurationProperty(WetConfiguration.PROPERTY_PROXY_PORT, "" + tmpWetConfiguration.getProxyPort());
      // writeConfigurationProperty(WetConfiguration.PROPERTY_PROXY_HOSTS_TO_BYPASS,
      // aWetConfiguration.getProxyHostsToBypass());
      printConfigurationProperty(WetConfiguration.PROPERTY_PROXY_USER, tmpWetConfiguration.getProxyUser());
      printConfigurationProperty(WetConfiguration.PROPERTY_BASIC_AUTH_USER, tmpWetConfiguration.getBasicAuthUser());

      printlnStartTag(TAG_VARIABLES);

      List<Variable> tmpVariables = tmpWetConfiguration.getVariables();
      for (Variable tmpVariable : tmpVariables) {
        printStartTagOpener(TAG_VARIABLE);
        output.print("name=\"");
        output.print(xmlUtil.normalizeAttributeValue(tmpVariable.getName()));
        output.print("\" value=\"");
        output.print(xmlUtil.normalizeAttributeValue(tmpVariable.getValue().toString()));
        output.println("\" />");
      }

      printlnEndTag(TAG_VARIABLES);

      List<WetCommandSet> tmpCommandSets = tmpWetConfiguration.getCommandSets();
      for (WetCommandSet tmpCommandSet : tmpCommandSets) {
        printStartTagOpener(TAG_COMMAND_SET);
        output.print("class=\"");
        output.print(xmlUtil.normalizeAttributeValue(tmpCommandSet.getClass().toString()));
        output.println("\" />");
      }

      printlnEndTag(TAG_CONFIGURATION);

      printlnNode(TAG_START_TIME, StringUtil.formatDate(new Date()));
      for (File tmpFile : aWetEngine.getTestFiles()) {
        printlnNode(TAG_TEST_FILE, tmpFile.getAbsolutePath());
      }

      wetExecutionStartTime = System.currentTimeMillis();
    } catch (IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#testCaseStart(String)
   */
  @Override
  public void testCaseStart(String aTestName) {
    try {
      printStartTagOpener(TAG_TESTCASE);
      output.print("name=\"");
      output.print(xmlUtil.normalizeAttributeValue(aTestName));
      output.println("\">");
      output.indent();
    } catch (IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#testRunStart(String)
   */
  @Override
  public void testRunStart(String aBrowserName) {
    try {
      printStartTagOpener(TAG_TESTRUN);
      output.print("browser=\"");
      output.print(xmlUtil.normalizeAttributeValue(aBrowserName));
      output.println("\">");
      output.indent();
    } catch (IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#testFileStart(String)
   */
  @Override
  public void testFileStart(String aFileName) {
    try {
      printStartTagOpener(TAG_TESTFILE);
      output.print("file=\"");
      output.print(xmlUtil.normalizeAttributeValue(aFileName));
      output.println("\">");
      output.indent();
    } catch (IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#executeCommandStart(org.rbri.wet.core.WetContext,
   *      org.rbri.wet.core.WetCommand)
   */
  @Override
  public void executeCommandStart(WetContext aWetContext, WetCommand aWetCommand) {
    try {
      printStartTagOpener(TAG_COMMAND);
      output.print("name=\"");
      output.print(xmlUtil.normalizeAttributeValue(aWetCommand.getName()));
      output.print("\" line=\"" + aWetCommand.getLineNo());
      if (aWetCommand.isComment()) {
        output.print("\" isComment=\"true");
      }
      output.println("\" >");
      output.indent();

      Parameter tmpParameter = aWetCommand.getFirstParameter();
      printStartTag(TAG_FIRST_PARAM);
      if (null != tmpParameter) {
        output.print(xmlUtil.normalizeBodyValue(tmpParameter.getValue(aWetContext).toString()));
      }
      printEndTag(TAG_FIRST_PARAM);
      output.println();

      tmpParameter = aWetCommand.getSecondParameter();
      printStartTag(TAG_SECOND_PARAM);
      if (null != tmpParameter) {
        output.print(xmlUtil.normalizeBodyValue(tmpParameter.getValue(aWetContext).toString()));
      }
      printEndTag(TAG_SECOND_PARAM);
      output.println();

      commandExecutionStartTime = System.currentTimeMillis();
    } catch (IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#executeCommandEnd()
   */
  @Override
  public void executeCommandEnd() {
    try {
      printlnNode(TAG_EXECUTION_TIME, "" + (System.currentTimeMillis() - commandExecutionStartTime));

      printlnEndTag(TAG_COMMAND);
    } catch (IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#executeCommandSuccess()
   */
  @Override
  public void executeCommandSuccess() {
    // nothing to do
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#executeCommandFailure(org.rbri.wet.exception.AssertionFailedException)
   */
  @Override
  public void executeCommandFailure(AssertionFailedException anAssertionFailedException) {
    try {
      printErrorStart(anAssertionFailedException);

      Throwable tmpThrowable = anAssertionFailedException.getCause();
      if (null != tmpThrowable) {
        executeCommandError(tmpThrowable);
      }
      printErrorEnd();
      flush();
    } catch (IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#executeCommandError(java.lang.Throwable)
   */
  @Override
  public void executeCommandError(Throwable aThrowable) {
    try {
      printErrorStart(aThrowable);

      Throwable tmpThrowable = aThrowable.getCause();
      if (null != tmpThrowable) {
        executeCommandError(tmpThrowable);
      }
      printErrorEnd();
      flush();
    } catch (IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#testFileEnd()
   */
  @Override
  public void testFileEnd() {
    try {
      printlnEndTag(TAG_TESTFILE);
    } catch (IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#testRunEnd()
   */
  @Override
  public void testRunEnd() {
    try {
      printlnEndTag(TAG_TESTRUN);
      flush();
    } catch (IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#testCaseEnd()
   */
  @Override
  public void testCaseEnd() {
    try {
      printlnEndTag(TAG_TESTCASE);
      flush();
    } catch (IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#responseStored(java.lang.String)
   */
  @Override
  public void responseStored(String aResponseFileName) {
    try {
      printlnNode(TAG_RESPONSE, aResponseFileName);
    } catch (IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#end(WetEngine)
   */
  @Override
  public void end(WetEngine aWetEngine) {
    try {
      printlnNode(TAG_EXECUTION_TIME, "" + (System.currentTimeMillis() - wetExecutionStartTime));

      printlnEndTag(TAG_WET);
      output.flush();
      writer.close();

      XslTransformer tmpXslTransformer = new XslTransformer(resultFile);
      tmpXslTransformer.transform(xslTemplates, outputDir);
    } catch (IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#warn(java.lang.String, java.lang.String[])
   */
  @Override
  public void warn(String aMessageKey, String[] aParameterArray) {
    try {
      String tmpMessage = Messages.getMessage(aMessageKey, aParameterArray);
      if (LOG.isWarnEnabled()) {
        LOG.warn(tmpMessage);
      }
      printLogMessage("WARN", tmpMessage);
    } catch (IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.rbri.wet.core.WetProgressListener#info(java.lang.String, java.lang.String[])
   */
  @Override
  public void info(String aMessageKey, String[] aParameterArray) {
    try {
      String tmpMessage = Messages.getMessage(aMessageKey, aParameterArray);
      if (LOG.isInfoEnabled()) {
        LOG.info(tmpMessage);
      }
      printLogMessage("INFO", tmpMessage);
    } catch (IOException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  private void printLogMessage(String aLevel, String aMessage) throws IOException {
    printlnStartTag(TAG_LOG);

    printlnNode(TAG_LEVEL, aLevel);
    printlnNode(TAG_MESSAGE, aMessage);

    printlnEndTag(TAG_LOG);
  }

  private void printErrorStart(Throwable aThrowable) throws IOException {
    printlnStartTag(TAG_ERROR);
    printlnNode(TAG_MESSAGE, aThrowable.getMessage());

    // TODO trace
  }

  private void printErrorEnd() throws IOException {
    printlnEndTag(TAG_ERROR);
  }

  private void printConfigurationProperty(String aKey, String aValue) throws IOException {
    printStartTagOpener(TAG_PROPERTY);
    output.print("key=\"");
    output.print(xmlUtil.normalizeAttributeValue(aKey));
    if (null != aValue) {
      output.print("\" value=\"");
      output.print(xmlUtil.normalizeAttributeValue(aValue));
    }
    output.println("\" />");
  }

  private void printConfigurationProperty(String aKey, SecretString aValue) throws IOException {
    printStartTagOpener(TAG_PROPERTY);
    output.print("key=\"");
    output.print(xmlUtil.normalizeAttributeValue(aKey));
    if (null != aValue) {
      output.print("\" value=\"");
      output.print(xmlUtil.normalizeAttributeValue(aValue.toString()));
    }
    output.println("\" />");
  }

  private void printlnNode(String aNodeName, String aNodeValue) throws IOException {
    printStartTag(aNodeName);
    output.print(xmlUtil.normalizeBodyValue(aNodeValue));
    printEndTag(aNodeName);
    output.println();
  }

  private void printlnStartTag(String aName) throws IOException {
    printStartTag(aName);
    output.println();
    output.indent();
  }

  private void printStartTag(String aName) throws IOException {
    printStartTagOpener(aName);
    output.print(">");
  }

  private void printlnEndTag(String aName) throws IOException {
    output.unindent();
    printEndTag(aName);
    output.println();
  }

  private void printEndTag(String aName) throws IOException {
    output.print("</").print(aName).print(">");
  }

  private void printStartTagOpener(String aName) throws IOException {
    output.print("<").print(aName).print(" id=\"" + tagId++).print("\" ");
  }

  private void flush() throws IOException {
    output.flush();
  }
}