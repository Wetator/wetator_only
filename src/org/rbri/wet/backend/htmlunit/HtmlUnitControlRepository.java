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


package org.rbri.wet.backend.htmlunit;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.rbri.wet.backend.Control;
import org.rbri.wet.backend.Control.SupportedAction;
import org.rbri.wet.backend.Control.SupportedActions;
import org.rbri.wet.backend.htmlunit.HtmlUnitControl.Identifiers;
import org.rbri.wet.backend.htmlunit.control.identifier.AbstractHtmlUnitElementIdentifier;

/**
 * Central repository for all supported {@link HtmlUnitControl}s.
 * 
 * @author frank.danek
 */
public class HtmlUnitControlRepository {

  private List<Class<? extends AbstractHtmlUnitElementIdentifier>> setableIdentifiers = new LinkedList<Class<? extends AbstractHtmlUnitElementIdentifier>>();
  private List<Class<? extends AbstractHtmlUnitElementIdentifier>> clickableIdentifiers = new LinkedList<Class<? extends AbstractHtmlUnitElementIdentifier>>();
  private List<Class<? extends AbstractHtmlUnitElementIdentifier>> selectableIdentifiers = new LinkedList<Class<? extends AbstractHtmlUnitElementIdentifier>>();
  private List<Class<? extends AbstractHtmlUnitElementIdentifier>> deselectableIdentifiers = new LinkedList<Class<? extends AbstractHtmlUnitElementIdentifier>>();
  private List<Class<? extends AbstractHtmlUnitElementIdentifier>> otherIdentifiers = new LinkedList<Class<? extends AbstractHtmlUnitElementIdentifier>>();

  /**
   * @param aControlClassList the classes of the controls to add
   */
  @SuppressWarnings("unchecked")
  public void addAll(List<Class<? extends HtmlUnitControl>> aControlClassList) {
    if (aControlClassList != null) {
      for (Class<? extends Control> tmpControlClass : aControlClassList) {
        add((Class<? extends HtmlUnitControl>) tmpControlClass);
      }
    }
  }

  /**
   * @param aControlClass the class of the control to add
   */
  public void add(Class<? extends HtmlUnitControl> aControlClass) {
    if (aControlClass == null) {
      return;
    }
    if (HtmlUnitControl.class.isAssignableFrom(aControlClass)) {
      Identifiers tmpIdentifiers = aControlClass.getAnnotation(Identifiers.class);
      if (tmpIdentifiers != null) {
        List<Class<? extends AbstractHtmlUnitElementIdentifier>> tmpIdentifierClasses = Arrays.asList(tmpIdentifiers
            .value());

        SupportedActions tmpSupportedActions = aControlClass.getAnnotation(SupportedActions.class);
        boolean tmpFound = false;
        if (tmpSupportedActions != null) {
          if (ArrayUtils.contains(tmpSupportedActions.value(), SupportedAction.SETABLE)) {
            tmpFound = true;
            setableIdentifiers.addAll(tmpIdentifierClasses);
          }
          if (ArrayUtils.contains(tmpSupportedActions.value(), SupportedAction.CLICKABLE)) {
            tmpFound = true;
            clickableIdentifiers.addAll(tmpIdentifierClasses);
          }
          if (ArrayUtils.contains(tmpSupportedActions.value(), SupportedAction.SELECTABLE)) {
            tmpFound = true;
            selectableIdentifiers.addAll(tmpIdentifierClasses);
          }
          if (ArrayUtils.contains(tmpSupportedActions.value(), SupportedAction.DESELECTABLE)) {
            tmpFound = true;
            deselectableIdentifiers.addAll(tmpIdentifierClasses);
          }
        }
        if (!tmpFound) {
          otherIdentifiers.addAll(tmpIdentifierClasses);
        }
      }
    }
  }

  /**
   * @return the setableIdentifiers
   */
  public List<Class<? extends AbstractHtmlUnitElementIdentifier>> getSetableIdentifiers() {
    return setableIdentifiers;
  }

  /**
   * @return the clickableIdentifiers
   */
  public List<Class<? extends AbstractHtmlUnitElementIdentifier>> getClickableIdentifiers() {
    return clickableIdentifiers;
  }

  /**
   * @return the selectableIdentifiers
   */
  public List<Class<? extends AbstractHtmlUnitElementIdentifier>> getSelectableIdentifiers() {
    return selectableIdentifiers;
  }

  /**
   * @return the deselectableIdentifiers
   */
  public List<Class<? extends AbstractHtmlUnitElementIdentifier>> getDeselectableIdentifiers() {
    return deselectableIdentifiers;
  }

  /**
   * @return the otherIdentifiers
   */
  public List<Class<? extends AbstractHtmlUnitElementIdentifier>> getOtherIdentifiers() {
    return otherIdentifiers;
  }
}
