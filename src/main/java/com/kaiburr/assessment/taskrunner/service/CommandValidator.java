
package com.kaiburr.assessment.taskrunner.service;

import java.util.Arrays;
import java.util.List;

public class CommandValidator {
    private static final List<String> forbiddenTokens = Arrays.asList(
            "&&", ";", "|", ">", "<", "$(", "`", "sudo", "rm", "reboot",
            "shutdown", "mkfs", "dd", "nc", "curl", "wget"
    );

    public static boolean isSafe(String command) {
        if (command == null || command.isBlank()) return false;
        String lower = command.toLowerCase();
        for (String tok : forbiddenTokens) {
            if (lower.contains(tok)) return false;
        }
        return true;
    }
}
