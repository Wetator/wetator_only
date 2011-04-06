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


package org.wetator.backend.htmlunit.finder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.wetator.backend.WPath;
import org.wetator.backend.WeightedControlList;
import org.wetator.backend.htmlunit.control.identifier.AbstractHtmlUnitControlIdentifier;
import org.wetator.backend.htmlunit.util.HtmlPageIndex;
import org.wetator.exception.WetException;

import com.gargoylesoftware.htmlunit.html.HtmlElement;

/**
 * This finder uses {@link AbstractHtmlUnitControlIdentifier}s to identify if a {@link HtmlElement} matches a given
 * search.<br />
 * The identifiers must be added by {@link #addIdentifier(Class)} or {@link #addIdentifiers(List)} before
 * executing {@link #find(WPath)}. For all visible {@link HtmlElement}s all configured identifiers are executed, even if
 * a match is found. So the returned {@link WeightedControlList} may contain multiple
 * {@link org.wetator.backend.control.Control}s (multiple times).
 * 
 * @author frank.danek
 */
public class IdentifierBasedHtmlUnitControlsFinder extends AbstractHtmlUnitControlsFinder {

  /**
   * The thread pool to use for worker threads.
   */
  protected ThreadPoolExecutor threadPool;
  /**
   * The supported identifiers.
   */
  protected List<Class<? extends AbstractHtmlUnitControlIdentifier>> identifiers = new ArrayList<Class<? extends AbstractHtmlUnitControlIdentifier>>();

  private List<Future<?>> futures = new LinkedList<Future<?>>();

  /**
   * The constructor.
   * 
   * @param aHtmlPageIndex the {@link HtmlPageIndex} index of the page
   * @param aThreadPool the thread pool to use for worker threads; may be null
   */
  public IdentifierBasedHtmlUnitControlsFinder(final HtmlPageIndex aHtmlPageIndex, final ThreadPoolExecutor aThreadPool) {
    super(aHtmlPageIndex);

    threadPool = aThreadPool;
    if (null == threadPool) {
      // no executor was given, this mainly happens when called from unit tests
      threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
      threadPool.prestartAllCoreThreads();
    }
  }

  /**
   * @param anIdentifier the identifier to add
   */
  public void addIdentifier(final Class<? extends AbstractHtmlUnitControlIdentifier> anIdentifier) {
    identifiers.add(anIdentifier);
  }

  /**
   * @param anIdentifierList the list containing the identifiers to add
   */
  public void addIdentifiers(final List<Class<? extends AbstractHtmlUnitControlIdentifier>> anIdentifierList) {
    identifiers.addAll(anIdentifierList);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.backend.htmlunit.finder.AbstractHtmlUnitControlsFinder#find(WPath)
   */
  @Override
  public WeightedControlList find(final WPath aWPath) {
    final WeightedControlList tmpFoundControls = new WeightedControlList();
    for (HtmlElement tmpHtmlElement : htmlPageIndex.getAllVisibleHtmlElements()) {
      for (Class<? extends AbstractHtmlUnitControlIdentifier> tmpIdentifierClass : identifiers) {
        try {
          final AbstractHtmlUnitControlIdentifier tmpIdentifier = tmpIdentifierClass.newInstance();
          tmpIdentifier.initializeForAsynch(htmlPageIndex, tmpHtmlElement, aWPath, tmpFoundControls);
          if (tmpIdentifier.isHtmlElementSupported(tmpHtmlElement)) {
            execute(tmpIdentifier);
          }
        } catch (final IllegalAccessException e) {
          throw new WetException("Could not access identifier class '" + tmpIdentifierClass.getName() + "'.", e);
        } catch (final InstantiationException e) {
          throw new WetException("Could not instantiate identifier for class '" + tmpIdentifierClass.getName() + "'.",
              e);
        }
      }
    }
    waitUntilExecuted();
    return tmpFoundControls;
  }

  /**
   * Executes the given identifier asynchronously in the thread pool.
   * 
   * @param anIdentifier the identifier
   */
  protected void execute(final AbstractHtmlUnitControlIdentifier anIdentifier) {
    futures.add(threadPool.submit(anIdentifier));
  }

  /**
   * Waits until the execution of all identifiers in the thread pool finished.
   */
  protected void waitUntilExecuted() {
    for (Future<?> tmpFuture : futures) {
      try {
        tmpFuture.get();
      } catch (final InterruptedException e) {
        throw new WetException("Exception waiting for executed threads.", e);
      } catch (final ExecutionException e) {
        throw new WetException("Exception occured in executed thread.", e.getCause());
      }
    }
  }
}