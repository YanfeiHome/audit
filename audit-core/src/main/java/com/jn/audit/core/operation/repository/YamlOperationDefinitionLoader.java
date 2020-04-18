package com.jn.audit.core.operation.repository;

import com.jn.audit.core.model.OperationDefinition;
import com.jn.audit.core.model.OperationImportance;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.io.resource.Resource;
import com.jn.langx.io.resource.Resources;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.io.IOs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class YamlOperationDefinitionLoader implements OperationDefinitionLoader {
    private static final Logger logger = LoggerFactory.getLogger(YamlOperationDefinitionLoader.class);
    private String definitionFilePath;


    @Override
    public List<OperationDefinition> loadAll() {
        return reload(null);
    }

    @NonNull
    @Override
    public List<OperationDefinition> reload(List<OperationImportance> importances) {
        return Collects.newArrayList(reloadAll(importances).values());
    }

    private Map<String, OperationDefinition> reloadAll(List<OperationImportance> importances) {
        final Map<String, OperationImportance> importanceMap = new HashMap<String, OperationImportance>();
        Collects.forEach(importances, new Consumer<OperationImportance>() {
            @Override
            public void accept(OperationImportance importance) {
                importanceMap.put(importance.getName(), importance);
            }
        });

        Map<String, OperationDefinition> definitionMap = Collects.<String, OperationDefinition>emptyHashMap();

        Resource operationDefinitionResource = Resources.loadFileResource(definitionFilePath, YamlOperationDefinitionLoader.class.getClassLoader());

        InputStream inputStream = null;
        try {
            inputStream = operationDefinitionResource.getInputStream();
            Yaml yaml = new Yaml();
            Iterable<Object> iterable = yaml.loadAll(inputStream);
            Iterator iterator = iterable.iterator();
            while (iterator.hasNext()) {
                Object obj = iterator.next();
                if (obj instanceof Map) {
                    System.out.println(obj.toString());
                }
            }
        } catch (Throwable ex) {
            logger.warn(ex.getMessage(), ex);
        } finally {
            IOs.close(inputStream);
        }
        return definitionMap;
    }

    @Override
    public OperationDefinition load(String id) {
        return reloadAll(null).get(id);
    }

    public void setDefinitionFilePath(String definitionFilePath) {
        this.definitionFilePath = definitionFilePath;
    }
}