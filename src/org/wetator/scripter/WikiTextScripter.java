/*
 * Copyright (c) 2008-2012 wetator.org
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


package org.wetator.scripter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.wetator.core.Command;
import org.wetator.core.IScripter;
import org.wetator.core.Parameter;
import org.wetator.exception.WetatorException;
import org.wetator.util.NormalizedString;

/**
 * Scripter for text files.
 * 
 * @author rbri
 */
public final class WikiTextScripter implements IScripter {
  private static final String FILE_EXTENSION = ".wett";
  private static final int COMMAND_NAME_COLUMN_NO = 0;
  private static final int FIRST_PARAM_COLUMN_NO = 1;
  private static final int SECOND_PARAM_COLUMN_NO = 2;
  private static final int THIRD_PARAM_COLUMN_NO = 3;

  private File file;
  private List<Command> commands;

  /**
   * Standard constructor.
   */
  public WikiTextScripter() {
    super();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IScripter#script(java.io.File)
   */
  @Override
  public void script(final File aFile) throws WetatorException {
    file = aFile;

    commands = readCommands();
  }

  /**
   * @return the file
   */
  public File getFile() {
    return file;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IScripter#isSupported(java.io.File)
   */
  @Override
  public IScripter.IsSupportedResult isSupported(final File aFile) {
    final String tmpFileName = aFile.getName().toLowerCase();
    final boolean tmpResult = tmpFileName.endsWith(FILE_EXTENSION);
    if (tmpResult) {
      return IScripter.IS_SUPPORTED;
    }

    return new IScripter.IsSupportedResult("File '" + aFile.getName()
        + "' not supported by WikiTextScripter. Extension is not '" + FILE_EXTENSION + "'.");
  }

  private List<Command> readCommands() throws WetatorException {
    final List<Command> tmpResult = new LinkedList<Command>();

    final BufferedReader tmpReader;
    try {
      tmpReader = new BufferedReader(new FileReader(getFile().getAbsoluteFile()));
    } catch (final FileNotFoundException e) {
      throw new WetatorException("File '" + getFile().getAbsolutePath() + "' not available.", e);
    }
    try {
      int tmpLineNo = 0;
      String tmpLine;
      while (null != (tmpLine = tmpReader.readLine())) {
        tmpLineNo++;
        tmpLine.trim();

        // ignore blank lines
        if (StringUtils.isBlank(tmpLine)) {
          continue;
        }

        boolean tmpComment = false;
        if (tmpLine.startsWith("#")) {
          tmpComment = true;
          tmpLine = tmpLine.substring(1);
        } else if (tmpLine.startsWith("//")) {
          tmpComment = true;
          tmpLine = tmpLine.substring(2);
        }

        final String[] tmpParts = StringUtils.splitByWholeSeparator(tmpLine, "||");

        String tmpCommandName = "";
        if (tmpParts.length > COMMAND_NAME_COLUMN_NO) {
          tmpCommandName = tmpParts[COMMAND_NAME_COLUMN_NO];
          tmpCommandName = tmpCommandName.trim();
        }
        // normalize command name
        if (StringUtils.isNotEmpty(tmpCommandName) && !(tmpComment && tmpParts.length < 2)) {
          tmpCommandName = tmpCommandName.replace(' ', '-').replace('_', '-').toLowerCase();
        }
        tmpCommandName = new NormalizedString(tmpCommandName).toString();

        // empty command means comment
        if (tmpComment && StringUtils.isEmpty(tmpCommandName)) {
          tmpCommandName = "Comment";
        }

        if (!StringUtils.isEmpty(tmpCommandName)) {
          final Command tmpCommand = new Command(tmpCommandName, tmpComment);

          String tmpParameter = null;
          if (tmpParts.length > FIRST_PARAM_COLUMN_NO) {
            tmpParameter = tmpParts[FIRST_PARAM_COLUMN_NO];
            tmpParameter = tmpParameter.trim();
          }
          if (null != tmpParameter) {
            tmpCommand.setFirstParameter(new Parameter(tmpParameter));
          }

          tmpParameter = null;
          if (tmpParts.length > SECOND_PARAM_COLUMN_NO) {
            tmpParameter = tmpParts[SECOND_PARAM_COLUMN_NO];
            tmpParameter = tmpParameter.trim();
          }
          if (null != tmpParameter) {
            tmpCommand.setSecondParameter(new Parameter(tmpParameter));
          }

          tmpParameter = null;
          if (tmpParts.length > THIRD_PARAM_COLUMN_NO) {
            tmpParameter = tmpParts[THIRD_PARAM_COLUMN_NO];
            tmpParameter = tmpParameter.trim();
          }
          if (null != tmpParameter) {
            tmpCommand.setThirdParameter(new Parameter(tmpParameter));
          }

          tmpCommand.setLineNo(tmpLineNo);

          tmpResult.add(tmpCommand);
        }
      }
      return tmpResult;
    } catch (final IOException e) {
      throw new WetatorException("IO Problem reading file '" + getFile().getAbsolutePath() + "'.", e);
    } finally {
      try {
        tmpReader.close();
      } catch (final IOException e) {
        throw new WetatorException("IO Problem closing file '" + getFile().getAbsolutePath() + "'.", e);
      }
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IScripter#getCommands()
   */
  @Override
  public List<Command> getCommands() {
    return commands;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.wetator.core.IScripter#initialize(java.util.Properties)
   */
  @Override
  public void initialize(final Properties aConfiguration) {
    // nothing to do
  }
}