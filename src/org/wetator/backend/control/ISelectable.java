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


package org.wetator.backend.control;

import org.wetator.core.WetatorContext;
import org.wetator.exception.AssertionFailedException;

/**
 * This interface marks all selectable {@link IControl}s. These controls are returned by
 * {@link org.wetator.backend.IControlFinder#getAllSelectables(org.wetator.backend.WPath)}.
 * 
 * @author frank.danek
 */
public interface ISelectable extends IControl {

  /**
   * Selects the control.
   * 
   * @param aContext the context
   * @throws AssertionFailedException if the control supports no select
   */
  public void select(WetatorContext aContext) throws AssertionFailedException;

  /**
   * @param aContext the context
   * @return true, if the control is selected
   * @throws AssertionFailedException if the check is not supported for the control
   */
  public boolean isSelected(WetatorContext aContext) throws AssertionFailedException;
}