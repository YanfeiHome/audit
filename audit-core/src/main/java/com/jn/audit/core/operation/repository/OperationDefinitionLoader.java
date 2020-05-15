package com.jn.audit.core.operation.repository;

import com.jn.audit.core.model.OperationDefinition;
import com.jn.audit.core.model.OperationImportance;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.configuration.FullLoadConfigurationLoader;

import java.util.List;
import java.util.Map;

public interface OperationDefinitionLoader extends FullLoadConfigurationLoader<OperationDefinition> {
    @NonNull
    List<OperationDefinition> reload(@Nullable Map<String, OperationImportance> importances);

    void setDefinitionFilePath(@NonNull String definitionFilePath);
}
