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


package org.rbri.wet.backend.htmlunit.util;

import java.io.IOException;
import java.net.URL;

import org.apache.commons.lang.StringUtils;
import org.rbri.wet.exception.AssertionFailedException;
import org.rbri.wet.util.Assert;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.StringWebResponse;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HTMLParser;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.XHtmlPage;

/**
 * Util class for page handling.
 * 
 * @author rbri
 */
public final class PageUtil {

  // /**
  // * Wait for finishing of all jobs scheduled for execution
  // * in the next 1s
  // *
  // * @param aPage the page
  // */
  // public static void waitForThreads(final Page aPage, long aTimeoutInSeconds) {
  // // no wait
  // if (aTimeoutInSeconds < 1) {
  // // at least execute all immediate jobs
  // JavaScriptJobManager tmpJobManager = aPage.getEnclosingWindow().getJobManager();
  //
  // tmpJobManager.waitForJobsStartingBefore(1000); // one second
  // return;
  // }
  //
  // // try with wait
  // int tmpNoOfJobsLeft = 1;
  // long tmpEndTime = System.currentTimeMillis() + aTimeoutInSeconds * 1000;
  // while (tmpNoOfJobsLeft > 0 && System.currentTimeMillis() < tmpEndTime) {
  // long tmpWaitTime = tmpEndTime - System.currentTimeMillis();
  // tmpNoOfJobsLeft = tmpJobManager.waitForJobsStartingBefore(Math.max(1000, tmpWaitTime));
  //
  // // current page is changed, we have to make another try
  // if (tmpHtmlPage != getCurrentHtmlPage()) {
  // tmpNoOfJobsLeft = 1;
  // tmpHtmlPage = getCurrentHtmlPage();
  // tmpCurrentTitle = tmpHtmlPage.getTitleText();
  // }
  // }
  //
  // // TODO make max wait time configurable
  //
  // }

  /**
   * Helper for tests
   * 
   * @param anHtmlCode the html source of the page
   * @return the HtmlPage result of parsing the source
   * @throws IOException in case of problems
   */
  public static HtmlPage constructHtmlPage(final String anHtmlCode) throws IOException {
    StringWebResponse tmpResponse = new StringWebResponse(anHtmlCode, new URL("http://www.rbri.org/wet/test.html"));
    WebClient tmpWebClient = new WebClient();
    HtmlPage tmpPage = HTMLParser.parseHtml(tmpResponse, tmpWebClient.getCurrentWindow());

    return tmpPage;
  }

  /**
   * Helper for tests
   * 
   * @param anXHtmlCode the XHtml source of the page
   * @return the XHtmlPage result of parsing the source
   * @throws IOException in case of problems
   */
  public static XHtmlPage constructXHtmlPage(final String anXHtmlCode) throws IOException {
    StringWebResponse tmpResponse = new StringWebResponse(anXHtmlCode, new URL("http://www.rbri.org/wet/test.xhtml"));
    WebClient tmpWebClient = new WebClient();
    XHtmlPage tmpPage = HTMLParser.parseXHtml(tmpResponse, tmpWebClient.getCurrentWindow());

    return tmpPage;
  }

  /**
   * Check, if the given Anchor is on the page
   * 
   * @param aRef the anchor ref
   * @param aPage the page
   * @throws AssertionFailedException if the anchor is not on the page
   */
  public static void checkAnchor(String aRef, Page aPage) throws AssertionFailedException {
    if (null == aPage) {
      return;
    }

    if ((aPage instanceof HtmlPage) && StringUtils.isNotEmpty(aRef)) {
      HtmlPage tmpHtmlPage = (HtmlPage) aPage;
      try {
        // check first with id
        tmpHtmlPage.getHtmlElementById(aRef);
      } catch (ElementNotFoundException e) {
        // maybe there is an anchor with this name
        // the browser jumps to the first one
        try {
          tmpHtmlPage.getAnchorByName(aRef);
        } catch (ElementNotFoundException eNF) {
          Assert.fail("noAnchor", new String[] { aRef });
        }
      }
    }
  }

  /**
   * Private constructor to be invisible
   */
  private PageUtil() {
    super();
  }
}