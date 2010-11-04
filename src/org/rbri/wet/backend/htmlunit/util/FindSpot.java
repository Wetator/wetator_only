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

/**
 * An object that stores the start and end position of a match.
 * It's more a struct than an object.
 * 
 * @author rbri
 */
public final class FindSpot {

  /**
   * static object to reduce the number of objects in use
   */
  public static final FindSpot NOT_FOUND = new FindSpot();

  /**
   * the start position of a match
   */
  public int startPos;
  /**
   * the end position of a match
   */
  public int endPos;

  /**
   * Constructor
   */
  public FindSpot() {
    this(-1, -1);
  }

  /**
   * Constructor
   * 
   * @param aStartPos the startPos
   * @param anEndPos the endPos
   */
  public FindSpot(int aStartPos, int anEndPos) {
    startPos = aStartPos;
    endPos = anEndPos;
  }

  @Override
  public int hashCode() {
    final int tmpPrime = 31;
    int tmpHash = 1;
    tmpHash = tmpPrime * tmpHash + endPos;
    tmpHash = tmpPrime * tmpHash + startPos;
    return tmpHash;
  }

  @Override
  public boolean equals(Object anObject) {
    if (this == anObject) {
      return true;
    }
    if (anObject == null) {
      return false;
    }
    if (getClass() != anObject.getClass()) {
      return false;
    }
    FindSpot tmpOtherFindSpot = (FindSpot) anObject;
    if (endPos != tmpOtherFindSpot.endPos) {
      return false;
    }
    if (startPos != tmpOtherFindSpot.startPos) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "FindSpot(" + startPos + ", " + endPos + ")";
  }

}