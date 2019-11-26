/*
 * Copyright 2019 Scott Logic Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.scottlogic.datahelix.generator.orchestrator;

import com.scottlogic.datahelix.generator.orchestrator.generate.GenerateCommandLine;
import com.scottlogic.datahelix.generator.orchestrator.violate.ViolateCommandLine;
import com.scottlogic.datahelix.generator.profile.ProfileConfiguration;
import picocli.CommandLine;

import java.util.stream.Collectors;

@CommandLine.Command(
    name = "datahelix",
    version = { ProfileConfiguration.PROFILE_SCHEMA_VERSION_TEXT })
public class App implements Runnable {
    @CommandLine.Option(
        names = { CommonOptionInfo.VERSION_SHORT_OPTION, CommonOptionInfo.VERSION_LONG_OPTION },
        versionHelp = true,
        description = CommonOptionInfo.VERSION_DESCRIPTION)
    boolean versionRequested;

    @CommandLine.Option(
        names = { CommonOptionInfo.HELP_SHORT_OPTION, CommonOptionInfo.HELP_LONG_OPTION },
        usageHelp = true,
        description = CommonOptionInfo.HELP_DESCRIPTION)
    private boolean help;

    private static final CommandLine picoCliCommandLine = new CommandLine(new App())
        .addSubcommand("generate", new GenerateCommandLine())
        .addSubcommand("violate", new ViolateCommandLine())
        .setCaseInsensitiveEnumValuesAllowed(true);

    public static void main(String[] args) {
        picoCliCommandLine
            .setExecutionExceptionHandler(new ValidationExceptionHandler())
            .execute(args);
    }

    @Override
    public void run() {
        String commandListString = picoCliCommandLine.getSubcommands()
            .keySet().stream()
            .sorted()
            .collect(Collectors.joining(", "));

        System.err.println("Valid commands: " + commandListString);
    }
}
