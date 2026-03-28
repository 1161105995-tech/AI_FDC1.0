package com.smartarchive.knowledgegraph.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "knowledge-graph.neo4j")
public class KnowledgeGraphNeo4jProperties {
    private boolean enabled;
    private String uri;
    private String username;
    private String password;
    private String database = "neo4j";
}
