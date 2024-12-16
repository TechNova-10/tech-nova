package com.tech_nova.delivery.infrastructure.dto;


import lombok.Data;

import java.util.List;

@Data
public class NaverApiResponse {
    private String code;  // 응답 코드
    private String message;  // 메시지
    private String currentDateTime;  // 현재 시간
    private Route route;  // 경로 정보

    @Data
    public static class Route {
        private List<Traoptimal> traoptimal;  // 경로 최적화 정보

        @Data
        public static class Traoptimal {
            private Summary summary;  // 경로 요약 정보

            @Data
            public static class Summary {
                private Location start;  // 출발지 정보
                private Location goal;   // 목적지 정보
                private int distance;    // 거리 (미터)
                private int duration;    // 소요 시간 (밀리초)
                private String departureTime;  // 출발 시간
                private List<List<Double>> bbox;  // 경로를 감싸는 박스 좌표
                private int tollFare;    // 통행료
                private int taxiFare;    // 택시 요금
                private int fuelPrice;   // 연료 요금
            }

            @Data
            public static class Location {
                private List<Double> location;  // 위치 (경도, 위도)
                private Integer dir;  // 방향
            }
        }
    }
}