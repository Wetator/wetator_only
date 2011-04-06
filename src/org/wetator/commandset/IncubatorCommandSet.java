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


package org.wetator.commandset;

import java.net.URL;
import java.util.Properties;

import org.wetator.backend.ControlFinder;
import org.wetator.backend.WPath;
import org.wetator.backend.WeightedControlList;
import org.wetator.backend.WetBackend;
import org.wetator.backend.control.Control;
import org.wetator.core.WetCommand;
import org.wetator.core.WetContext;
import org.wetator.exception.AssertionFailedException;
import org.wetator.util.Assert;
import org.wetator.util.SecretString;

/**
 * The implementation of all experimental commands that Wetator
 * supports at the moment.<br>
 * We are not sure, that these commands are useful extension of
 * the current command set. So we have this set to play a bit.
 * 
 * @author rbri
 */
public final class IncubatorCommandSet extends AbstractCommandSet {

  @Override
  protected void registerCommands() {
    registerCommand("Assert Focus", new CommandAssertFocus());
    registerCommand("Save Bookmark", new CommandSaveBookmark());
    registerCommand("Open Bookmark", new CommandOpenBookmark());
  }

  /**
   * Command 'Assert Focus'.
   */
  public final class CommandAssertFocus implements WetCommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.wetator.commandset.WetCommandImplementation#execute(org.wetator.core.WetContext,
     *      org.wetator.core.WetCommand)
     */
    @Override
    public void execute(final WetContext aWetContext, final WetCommand aWetCommand) throws AssertionFailedException {
      final WPath tmpWPath = new WPath(aWetCommand.getRequiredFirstParameterValues(aWetContext));
      aWetCommand.assertNoUnusedSecondParameter(aWetContext);

      final WetBackend tmpBackend = getWetBackend(aWetContext);
      final ControlFinder tmpControlFinder = tmpBackend.getControlFinder();

      // TextInputs / PasswordInputs / TextAreas / FileInputs
      final WeightedControlList tmpFoundElements = tmpControlFinder.getAllSettables(tmpWPath);
      tmpFoundElements.addAll(tmpControlFinder.getAllSelectables(tmpWPath));
      tmpFoundElements.addAll(tmpControlFinder.getAllClickables(tmpWPath));

      // search for special elements
      // e.g. selects by label, name, id
      tmpFoundElements.addAll(tmpControlFinder.getAllOtherControls(tmpWPath));

      // clickable Text
      tmpFoundElements.addAll(tmpControlFinder.getAllControlsForText(tmpWPath));

      final Control tmpControl = getRequiredFirstHtmlElementFrom(aWetContext, tmpFoundElements, tmpWPath);

      final boolean tmpIsDisabled = tmpControl.hasFocus(aWetContext);
      Assert.assertTrue(tmpIsDisabled, "elementNotFocused", new String[] { tmpControl.getDescribingText() });
    }
  }

  /**
   * Command 'Open Bookmark'.
   */
  public final class CommandOpenBookmark implements WetCommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.wetator.commandset.WetCommandImplementation#execute(org.wetator.core.WetContext,
     *      org.wetator.core.WetCommand)
     */
    @Override
    public void execute(final WetContext aWetContext, final WetCommand aWetCommand) throws AssertionFailedException {
      final SecretString tmpBookmarkName = aWetCommand.getRequiredFirstParameterValue(aWetContext);
      aWetCommand.assertNoUnusedSecondParameter(aWetContext);

      final WetBackend tmpBackend = getWetBackend(aWetContext);
      final URL tmpUrl = tmpBackend.getBookmark(tmpBookmarkName.getValue());
      Assert.assertNotNull(tmpUrl, "unknownBookmark", new String[] { tmpBookmarkName.getValue() });

      aWetContext.informListenersInfo("openUrl", new String[] { tmpUrl.toString() });
      tmpBackend.openUrl(tmpUrl);

      tmpBackend.saveCurrentWindowToLog();
    }
  }

  /**
   * Command 'Save Bookmark'.
   */
  public final class CommandSaveBookmark implements WetCommandImplementation {
    /**
     * {@inheritDoc}
     * 
     * @see org.wetator.commandset.WetCommandImplementation#execute(org.wetator.core.WetContext,
     *      org.wetator.core.WetCommand)
     */
    @Override
    public void execute(final WetContext aWetContext, final WetCommand aWetCommand) throws AssertionFailedException {
      final SecretString tmpBookmarkName = aWetCommand.getRequiredFirstParameterValue(aWetContext);
      aWetCommand.assertNoUnusedSecondParameter(aWetContext);

      final WetBackend tmpBackend = getWetBackend(aWetContext);
      tmpBackend.bookmarkPage(tmpBookmarkName.getValue());
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.commandset.WetCommandSet#initialize(java.util.Properties)
   */
  @Override
  public void initialize(final Properties aConfiguration) {
    // nothing to do at the moment
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.commandset.WetCommandSet#cleanup()
   */
  @Override
  public void cleanup() {
    // nothing to do at the moment
  }
}