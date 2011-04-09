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


package org.wetator.test;

import java.io.File;
import java.util.Properties;

import org.eclipse.jetty.http.security.Constraint;
import org.eclipse.jetty.http.security.Credential;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.wetator.core.WetConfiguration;
import org.wetator.core.WetEngine;
import org.wetator.test.jetty.HttpHeaderServlet;
import org.wetator.test.jetty.MultiPartFilter;
import org.wetator.test.jetty.RedirectServlet;
import org.wetator.test.jetty.SnoopyServlet;
import org.wetator.util.StdOutProgressListener;

/**
 * Base test class for all WetEngine tests that need a web server.
 * 
 * @author frank.danek
 */
public abstract class AbstractWebServerTest extends AbstractBrowserTest {

  /** The listener port for the web server. */
  public static final int DEFAULT_PORT = Integer.valueOf(System.getProperty("wetator.test.port", "4711"));
  protected static final String DEFAULT_DOCUMENT_ROOT = "webpages";

  private static Server server;

  private WetEngine wetEngine;
  private JUnitProgressListener listener;

  /**
   * Starts the web server.<br/>
   * The default port is {@link #DEFAULT_PORT}.
   * The default document root is {@link #DEFAULT_DOCUMENT_ROOT}.<br/>
   * 
   * @throws Exception if an error occurs starting the web server
   */
  @BeforeClass
  public static void startWebServer() throws Exception {
    if (server != null) {
      throw new IllegalStateException("startWebServer() can not be called twice");
    }
    server = new Server(DEFAULT_PORT);

    // resources
    ResourceHandler tmpResourceHandler = new ResourceHandler();
    tmpResourceHandler.setDirectoriesListed(true);
    tmpResourceHandler.setWelcomeFiles(new String[] { "index.html" });
    tmpResourceHandler.setResourceBase(DEFAULT_DOCUMENT_ROOT);

    // servlets
    ServletContextHandler tmpContextHandler = new ServletContextHandler();
    tmpContextHandler.setContextPath("/testcases");
    tmpContextHandler.addServlet(new ServletHolder(new HttpHeaderServlet()), "/http_header.php");
    tmpContextHandler.addServlet(new ServletHolder(new RedirectServlet()), "/redirect_header.php");
    tmpContextHandler.addServlet(new ServletHolder(new RedirectServlet()), "/redirect_js.php");
    tmpContextHandler.addServlet(new ServletHolder(new RedirectServlet()), "/redirect_meta.php");
    tmpContextHandler.addServlet(new ServletHolder(new SnoopyServlet()), "/snoopy.php");
    tmpContextHandler.addServlet(new ServletHolder(new SnoopyServlet()), "/snoopyAuth.php");

    FilterHolder tmpFilterHolder = new FilterHolder(new MultiPartFilter());
    tmpFilterHolder.setInitParameter("deleteFiles", "true");
    tmpContextHandler.addFilter(tmpFilterHolder, "/snoopy.php", 15);

    HandlerList tmpHandlers = new HandlerList();
    tmpHandlers.setHandlers(new Handler[] { tmpContextHandler, tmpResourceHandler, new DefaultHandler() });
    server.setHandler(tmpHandlers);

    // security
    final Constraint tmpConstraint = new Constraint();
    tmpConstraint.setName(Constraint.__BASIC_AUTH);
    tmpConstraint.setRoles(new String[] { "user" });
    tmpConstraint.setAuthenticate(true);

    final ConstraintMapping tmpConstraintMapping = new ConstraintMapping();
    tmpConstraintMapping.setConstraint(tmpConstraint);
    tmpConstraintMapping.setPathSpec("/snoopyAuth.php");

    final HashLoginService tmpLoginService = new HashLoginService("wetator");
    tmpLoginService.putUser("wetator", Credential.getCredential("secret"), new String[] { "user" });

    final ConstraintSecurityHandler tmpSecurityHandler = new ConstraintSecurityHandler();
    tmpSecurityHandler.setLoginService(tmpLoginService);
    tmpSecurityHandler.addConstraintMapping(tmpConstraintMapping);

    tmpContextHandler.setSecurityHandler(tmpSecurityHandler);

    // time to start
    server.start();
  }

  /**
   * Creates a WetEngine and configures it.
   */
  @Before
  public void createWetEngine() {
    Properties tmpProperties = new Properties();
    tmpProperties.setProperty(WetConfiguration.PROPERTY_BASE_URL, "http://localhost:" + DEFAULT_PORT + "/testcases");
    if (getBrowser() != null) {
      tmpProperties.setProperty(WetConfiguration.PROPERTY_BROWSER, getBrowser().getSymbol());
    }
    tmpProperties.setProperty(WetConfiguration.PROPERTY_XSL_TEMPLATES, "./xsl/SimpleHtml.xsl");
    tmpProperties.setProperty(WetConfiguration.PROPERTY_COMMAND_SETS, "org.wetator.commandset.IncubatorCommandSet, "
        + "org.wetator.commandset.SqlCommandSet, " + "org.wetator.commandset.TestCommandSet");
    tmpProperties.setProperty("wetator.db.connections", "wetdb, secondDb");

    tmpProperties.setProperty("wetator.basicAuthUser", "wetator");
    tmpProperties.setProperty("wetator.basicAuthPassword", "secret");

    tmpProperties.setProperty("wetator.db.wetdb.driver", "org.hsqldb.jdbcDriver");
    tmpProperties.setProperty("wetator.db.wetdb.url", "jdbc:hsqldb:mem:wetdb");
    tmpProperties.setProperty("wetator.db.wetdb.user", "sa");
    tmpProperties.setProperty("wetator.db.wetdb.password", "");

    tmpProperties.setProperty("wetator.db.secondDb.driver", "org.hsqldb.jdbcDriver");
    tmpProperties.setProperty("wetator.db.secondDb.url", "jdbc:hsqldb:mem:second_db");
    tmpProperties.setProperty("wetator.db.secondDb.user", "sa");
    tmpProperties.setProperty("wetator.db.secondDb.password", "");

    tmpProperties.setProperty("$app_user", "dobby");
    tmpProperties.setProperty("$$app_password", "secret");

    tmpProperties.setProperty("$wet", "Wetator");
    tmpProperties.setProperty("$$wet-secret", "Wetator");

    WetConfiguration tmpWetConfiguration = new WetConfiguration(new File("."), tmpProperties, null);

    listener = new JUnitProgressListener();

    wetEngine = new WetEngine();
    wetEngine.addProgressListener(listener);
    wetEngine.addProgressListener(new StdOutProgressListener());
    wetEngine.init(tmpWetConfiguration);
  }

  /**
   * Stops the web server.
   * 
   * @throws Exception if an error occurs stopping the web server
   */
  @AfterClass
  public static void stopWebServer() throws Exception {
    if (server != null) {
      server.stop();
    }
    server = null;
  }

  protected void executeTestFile(File aTestFile) {
    wetEngine.addTestFile(aTestFile);
    wetEngine.executeTests();
  }

  /**
   * @return the number of errors
   * @see org.wetator.test.JUnitProgressListener#getErrors()
   */
  public int getErrors() {
    return listener.getErrors();
  }

  /**
   * @return the number of failures
   * @see org.wetator.test.JUnitProgressListener#getFailures()
   */
  public int getFailures() {
    return listener.getFailures();
  }

  /**
   * @return the number of steps
   * @see org.wetator.test.JUnitProgressListener#getSteps()
   */
  public int getSteps() {
    return listener.getSteps();
  }
}
