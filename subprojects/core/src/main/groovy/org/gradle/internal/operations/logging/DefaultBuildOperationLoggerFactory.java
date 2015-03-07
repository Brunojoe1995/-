/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.internal.operations.logging;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.internal.UncheckedException;
import org.gradle.util.GFileUtils;

import java.io.*;

public class DefaultBuildOperationLoggerFactory implements BuildOperationLoggerFactory {
    private final Logger logger;

    DefaultBuildOperationLoggerFactory(Logger logger) {
        this.logger = logger;
    }

    public DefaultBuildOperationLoggerFactory() {
        this(Logging.getLogger(DefaultBuildOperationLoggerFactory.class));
    }


    @Override
    public BuildOperationLogger newOperationLogger(String taskName, File outputDir) {
        return newOperationLogger(taskName, outputDir, Integer.MAX_VALUE);
    }

    @Override
    public BuildOperationLogger newOperationLogger(String taskName, File outputDir, int maximumFailures) {
        final File outputFile = createOutputFile(outputDir);
        final PrintWriter logWriter = createWriter(outputFile);
        final BuildOperationLogInfo configuration = createLogInfo(taskName, outputFile, maximumFailures);
        return new DefaultBuildOperationLogger(configuration, logger, logWriter);
    }

    protected File createOutputFile(File outputDir) {
        GFileUtils.mkdirs(outputDir);
        return new File(outputDir, "output.txt");
    }

    protected PrintWriter createWriter(File outputFile) {
        PrintWriter logWriter = null;
        try {
            logWriter = new PrintWriter(new FileWriter(outputFile), true);
        } catch (IOException e) {
            UncheckedException.throwAsUncheckedException(e);
        }
        return logWriter;
    }

    protected BuildOperationLogInfo createLogInfo(String taskName, File outputFile, int maximumFailures) {
        final BuildOperationLogInfo configuration;
        if (logger.isDebugEnabled()) {
            // show all operation output when debug is enabled
            configuration = new BuildOperationLogInfo(taskName, outputFile, Integer.MAX_VALUE);
        } else {
            configuration = new BuildOperationLogInfo(taskName, outputFile, maximumFailures);
        }
        return configuration;
    }
}
