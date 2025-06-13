package com.example.blog_search_platform.config;

import com.example.blog_search_platform.document.PostDocument;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.index.Settings;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;

/**
 * 'posts' 인덱스에 Nori 형태소 분석기를 적용하기 위한 설정 클래스
 */
@Configuration
@RequiredArgsConstructor
public class NoriAnalyzerConfig {

    private final ElasticsearchOperations elasticsearchOperations;
    private static final String INDEX_NAME = "posts";

    /**
     * 애플리케이션 시작 시, 'posts' 인덱스가 존재하지 않으면
     * nori 분석기 설정을 포함하여 새로 생성합니다.
     */
    @PostConstruct
    public void configureNoriAnalyzer() {
        IndexOperations indexOperations = elasticsearchOperations.indexOps(IndexCoordinates.of(INDEX_NAME));

        if (!indexOperations.exists()) {
            // 1. 분석기(Analyzer)와 토크나이저(Tokenizer) 설정을 포함하는 Settings 객체 생성
            Settings settings = new Settings();

            // -- 분석기 정의 (Analyzer Definition) --
            // 'nori_analyzer_custom'라는 이름의 커스텀 분석기를 정의합니다.
            settings.put("index.analysis.analyzer.nori_analyzer_custom.type", "custom");
            // 이 분석기는 'nori_tokenizer'라는 내장 토크나이저를 사용하도록 설정합니다.
            // 'nori' 플러그인이 제공하는 기본 토크나이저 이름입니다.
            settings.put("index.analysis.analyzer.nori_analyzer_custom.tokenizer", "nori_tokenizer");
            // 토큰 필터를 설정합니다. (소문자 변환, 동의어/활용형 처리 등)
            settings.put("index.analysis.analyzer.nori_analyzer_custom.filter", new String[]{"lowercase", "nori_readingform"});

            // 2. 정의된 설정으로 인덱스 생성
            indexOperations.create(settings);

            // 3. PostDocument 클래스를 기반으로 매핑 정보 적용
            // 이 과정을 통해 @Field(analyzer = "nori_analyzer_custom") 설정이 인덱스에 반영됩니다.
            indexOperations.putMapping(indexOperations.createMapping(PostDocument.class));
        }
    }
}
