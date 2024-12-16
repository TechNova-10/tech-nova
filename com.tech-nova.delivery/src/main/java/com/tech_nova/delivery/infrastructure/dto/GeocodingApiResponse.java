package com.tech_nova.delivery.infrastructure.dto;

import lombok.Data;

import java.util.List;

@Data
public class GeocodingApiResponse {
    private String status;  // 상태
    private Meta meta;      // 메타 정보
    private List<Address> addresses;  // 주소 리스트

    @Data
    public static class Meta {
        private int totalCount;  // 총 검색 결과 수
        private int page;        // 현재 페이지 번호
        private int count;       // 현재 페이지의 결과 개수
    }

    @Data
    public static class Address {
        private String roadAddress;      // 도로명 주소
        private String jibunAddress;     // 지번 주소
        private String englishAddress;   // 영문 주소
        private List<AddressElement> addressElements;  // 주소 요소
        private String x;                // 경도
        private String y;                // 위도

        @Data
        public static class AddressElement {
            private List<String> types;  // 주소 요소 종류
            private String longName;     // 주소의 전체 이름
            private String shortName;    // 주소의 짧은 이름
            private String code;         // 주소 코드
        }
    }
}
