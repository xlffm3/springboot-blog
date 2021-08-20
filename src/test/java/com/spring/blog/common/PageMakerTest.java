package com.spring.blog.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("PageMaker 유틸 단위 테스트")
class PageMakerTest {

    @DisplayName("전체 아이템이 30개이며")
    @Nested
    class Describe_totalItemCounts_30 {

        @DisplayName("페이지 블락이 5개이고, 한 페이지에 5개씩 보여줄 때")
        @Nested
        class Context_page_block_5_and_size_5 {

            @DisplayName("0번째 페이지의 경우, 시작 페이지는 1이며 페이지 블락 5개에 다음 버튼이 활성화된다.")
            @Test
            void it_returns_five_page_and_next_activated() {
                PageMaker pageMaker = new PageMaker(0, 5, 5, 30);

                assertThat(pageMaker.getStartPage()).isOne();
                assertThat(pageMaker.getEndPage()).isEqualTo(5);
                assertThat(pageMaker.isPrev()).isFalse();
                assertThat(pageMaker.isNext()).isTrue();
            }

            @DisplayName("5번째 페이지의 경우, 시작 페이지는 6이며 페이지 블락 1개에 이전 버튼이 활성화된다.")
            @Test
            void it_returns_one_page_and_prev_activated() {
                PageMaker pageMaker = new PageMaker(5, 5, 5, 30);

                assertThat(pageMaker.getStartPage()).isEqualTo(6);
                assertThat(pageMaker.getEndPage()).isEqualTo(6);
                assertThat(pageMaker.isPrev()).isTrue();
                assertThat(pageMaker.isNext()).isFalse();
            }
        }

        @DisplayName("페이지 블락이 10개이고, 한 페이지에 3개씩 보여줄 때")
        @Nested
        class Context_page_block_3_and_size_3 {

            @DisplayName("0번째 페이지의 경우, 시작 페이지는 1이며 총 페이지 블락 10개에 다음 버튼은 없다.")
            @Test
            void it_returns_five_page_and_next_activated() {
                PageMaker pageMaker = new PageMaker(0, 3, 10, 30);

                assertThat(pageMaker.getStartPage()).isOne();
                assertThat(pageMaker.getEndPage()).isEqualTo(10);
                assertThat(pageMaker.isPrev()).isFalse();
                assertThat(pageMaker.isNext()).isFalse();
            }
        }

        @DisplayName("페이지 블락이 7개이고, 한 페이지에 3개씩 보여줄 때")
        @Nested
        class Context_page_block_7_and_size_3 {

            @DisplayName("7번째 페이지의 경우, 시작 페이지는 8이며 총 페이지 블락 3개에 다음 버튼은 없다.")
            @Test
            void it_returns_five_page_and_next_activated() {
                PageMaker pageMaker = new PageMaker(7, 3, 7, 30);

                assertThat(pageMaker.getStartPage()).isEqualTo(8);
                assertThat(pageMaker.getEndPage()).isEqualTo(10);
                assertThat(pageMaker.isPrev()).isTrue();
                assertThat(pageMaker.isNext()).isFalse();
            }
        }
    }
}
