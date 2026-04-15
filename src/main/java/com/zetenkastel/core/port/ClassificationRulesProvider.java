package com.zetenkastel.core.port;

import java.util.List;
import java.util.Map;

public interface ClassificationRulesProvider {

    ClassificationRulesSnapshot load();

    record ClassificationRulesSnapshot(
            List<String> canonicalMetadataKeys,
            Map<String, String> referenceDocuments
    ) { }
}
