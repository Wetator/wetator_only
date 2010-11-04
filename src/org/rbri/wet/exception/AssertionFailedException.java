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


package org.rbri.wet.exception;

/**
 * Special error in case an assertion fails
 * 
 * @author rbri
 */
public class AssertionFailedException extends Exception {

  private static final long serialVersionUID = -1587032805061848761L;

  /**
   * Constructor
   * 
   * @param aMessage the message text
   */
  public AssertionFailedException(String aMessage) {
    super(aMessage);
  }

  /**
   * Constructor
   * 
   * @param aMessage the message text
   * @param aCause the cause
   */
  public AssertionFailedException(String aMessage, Throwable aCause) {
    super(aMessage, aCause);
  }
}