package com.example.blog_search_platform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * Elasticsearch 연결 및 Repository 설정을 위한 클래스
 */
@Configuration
@EnableElasticsearchRepositories(basePackages = "com.example.blog_search_platform.repository.elasticsearch")
public class ElasticsearchConfig extends ElasticsearchConfiguration {

    @Value("${spring.elasticsearch.uris}")
    private String elasticsearchUris;

    /**
     * Elasticsearch 클라이언트 구성을 정의합니다.
     * application.properties 파일의 'spring.elasticsearch.uris' 값을 사용하여 연결 설정을 구성합니다.
     * @return ClientConfiguration 객체
     */
    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(elasticsearchUris)
                .build();
    }
}