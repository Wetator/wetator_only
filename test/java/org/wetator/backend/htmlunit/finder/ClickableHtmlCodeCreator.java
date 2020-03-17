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

/**
 * Creator for HTML code of clickable elements.
 *
 * @author tobwoerk
 */
public abstract class ClickableHtmlCodeCreator {

  public static final String CONTENT = "test";
  private static final String ONCLICK = "onclick=''";

  public static String pageStart() {
    return "<html><body>";
  }

  public static String pageEnd() {
    return "</body></html>";
  }

  public static String anchor(final String anAnchorId, final String aContent) {
    return anchorStart(anAnchorId) + aContent + anchorEnd();
  }

  public static String anchorStart(final String anAnchorId) {
    return "<a id='" + anAnchorId + "' href='#'>";
  }

  public static String anchorEnd() {
    return "</a>";
  }

  public static String button(final String aButtonId, final String aContent) {
    return buttonStart(aButtonId) + aContent + buttonEnd();
  }

  public static String buttonStart(final String aButtonId) {
    return "<button id='" + aButtonId + "' type='button'>";
  }

  public static String buttonEnd() {
    return "</button>";
  }

  public static String checkbox(final String aCheckboxId) {
    return input("checkbox", aCheckboxId);
  }

  public static String div(final String aDivId) {
    return divStart(aDivId) + divEnd();
  }

  public static String div(final String aDivId, final String aContent) {
    return divStart(aDivId) + aContent + divEnd();
  }

  public static String divStart(final String aDivId) {
    return "<div id='" + aDivId + "' " + ONCLICK + '>';
  }

  public static String divEnd() {
    return "</div>";
  }

  public static String inputText(final String anInputID) {
    return input("text", anInputID);
  }

  public static String inputText(final String anInputID, final String aPlaceholder) {
    return input("text", anInputID, "placeholder='" + aPlaceholder + "'");
  }

  public static String image(final String anImageId, final String anAltText) {
    return "<image id='" + anImageId + "' src='pathtoimg' " + ONCLICK + " alt='" + anAltText + "' />";
  }

  public static String label(final String aFor, final String aContent) {
    return labelStart(aFor, false) + aContent + labelEnd();
  }

  public static String labelClickable(final String aFor, final String aContent) {
    return labelStart(aFor, true) + aContent + labelEnd();
  }

  public static String labelStart(final String aFor, final boolean anIsClickable) {
    return "<label id='lbl-" + aFor + "' for='" + aFor + '\'' + (anIsClickable ? ' ' + ONCLICK : "") + '>';
  }

  public static String labelEnd() {
    return "</label>";
  }

  public static String radio(final String aRadioId) {
    return input("radio", aRadioId);
  }

  public static String span(final String aSpanId) {
    return spanStart(aSpanId) + spanEnd();
  }

  public static String span(final String aSpanId, final String aContent) {
    return spanStart(aSpanId) + aContent + spanEnd();
  }

  public static String spanStart(final String aSpanId) {
    return "<span id='" + aSpanId + "' " + ONCLICK + '>';
  }

  public static String spanEnd() {
    return "</span>";
  }

  public static String tableStart(final String aTableId) {
    return "<table id='" + aTableId + "' " + ONCLICK + "><tbody id='" + aTableId + "-body' " + ONCLICK + '>';
  }

  public static String tableEnd() {
    return "</tbody></table>";
  }

  public static String tableRowStart(final String aTableId, final String aRowId) {
    return "<tr id='" + aTableId + '-' + aRowId + "' " + ONCLICK + '>';
  }

  public static String tableRowEnd() {
    return "</tr>";
  }

  public static String tableRowWithCols(final String aTableId, final String aRowId, final int aColumnCount) {
    String tmpRow = tableRowStart(aTableId, aRowId);
    for (int i = 1; i <= aColumnCount; i++) {
      tmpRow += "<td id='" + aTableId + '-' + aRowId + "-td" + (aColumnCount > 1 ? i : "") + "' " + ONCLICK + '>'
          + CONTENT + "</td>";
    }
    tmpRow += tableRowEnd();

    return tmpRow;
  }

  private static String input(final String aType, final String anInputID) {
    return input(aType, anInputID, null);
  }

  private static String input(final String aType, final String anInputID, final String anOptional) {
    return "<input type = '" + aType + "' id='" + anInputID + "' " + anOptional + "/>";
  }
}