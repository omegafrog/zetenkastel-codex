package com.zetenkastel.core.port;

import java.util.Optional;

public interface LlmPort {
    LlmResponse analyze(String noteContent, String instruction);
    
    record LlmResponse(
            ActionType action,
            String targetNoteType,
            String newContent,
            String reason
    ) {
        public enum ActionType {
            MOVE,
            REWRITE,
            ARCHIVE,
            KEEP
        }
    }
}