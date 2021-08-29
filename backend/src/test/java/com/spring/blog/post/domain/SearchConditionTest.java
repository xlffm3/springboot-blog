package com.spring.blog.post.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("SearchCondition 도메인 단위 테스트")
class SearchConditionTest {

    @DisplayName("isCustomSearchCondition 메서드는")
    @Nested
    class Describe_isCustomSearchCondition {

        @DisplayName("조회 타입이나 키워드 둘 중 하나라도 null이면")
        @Nested
        class Context_searchType_or_keyword_null {

            @DisplayName("false를 반환한다.")
            @Test
            void it_returns_false() {
                // given
                SearchCondition searchCondition = new SearchCondition(null, "a");

                // when, then
                assertThat(searchCondition.isCustomSearchCondition()).isFalse();
            }
        }

        @DisplayName("조회 타입이나 키워드 둘다 null이 아니면")
        @Nested
        class Context_searchType_and_keyword_not_null {

            @DisplayName("true를 반환한다.")
            @Test
            void it_returns_false() {
                // given
                SearchCondition searchCondition = new SearchCondition("title", "a");

                // when, then
                assertThat(searchCondition.isCustomSearchCondition()).isTrue();
            }
        }
    }
}
