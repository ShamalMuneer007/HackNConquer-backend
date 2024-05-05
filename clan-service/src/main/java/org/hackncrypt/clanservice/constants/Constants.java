package org.hackncrypt.clanservice.constants;

import java.util.List;

public class Constants {
    private static final List<String> PROTECTED_URI_PATTERNS = List.of(
            "/api/v1/user/**",
            "/api/v1/admin/**"
    );
    public static List<String> getProtectedURIPatterns() {
        return PROTECTED_URI_PATTERNS;
    }
}

