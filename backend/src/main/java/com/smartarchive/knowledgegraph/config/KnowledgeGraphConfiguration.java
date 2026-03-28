package com.smartarchive.knowledgegraph.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(KnowledgeGraphNeo4jProperties.class)
public class KnowledgeGraphConfiguration {
}
