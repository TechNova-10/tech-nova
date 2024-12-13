package com.tech_nova.movement.application.sevice;

import com.tech_nova.movement.application.dtos.req.MovementRequestDto;
import com.tech_nova.movement.application.dtos.res.MovementResponseDto;
import com.tech_nova.movement.domain.model.Movement;
import com.tech_nova.movement.domain.repository.MovementRepository;
import com.tech_nova.movement.infrastructure.client.HubClient;
import com.tech_nova.movement.infrastructure.client.HubResponseDto;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovementService {

  private final MovementRepository movementRepository;
  private final HubClient hubClient;

  private static final double EARTH_RADIUS = 6371.0;
  private static final double AVERAGE_SPEED_KMH = 60.0;
  private static final Map<String, List<String>> regionAdjacency = new HashMap<>();

  static {
    // 경기 남부 중앙 허브
    regionAdjacency.put("경기도 남부 센터",
        List.of("경기도 북부 센터", "서울특별시 센터", "인천광역시 센터", "경기도 남부 센터", "강원도 자치도 센터"));
    // 대전 중앙 허브
    regionAdjacency.put("대전광역시 센터",
        List.of("충청남도 센터", "충청북도 센터", "세종특별자치시 센터", "대전광역시 센터", "전북 특광역시 센터", "광주광역시 센터",
            "전라남도 센터"));
    // 대구 중앙 허브
    regionAdjacency.put("대구광역시 센터", List.of("경북 센터", "대구광역시 센터", "경남 센터", "부산광역시 센터", "울산광역시 센터"));
  }

  public void createMovement(MovementRequestDto movementRequestDto, UUID userId, String role) {

    validateMasterRole(role);

    List<HubResponseDto> hubs = hubClient.getHubList();

    HubResponseDto departureHub = findHub(
        movementRequestDto.getDepartureHubId(), hubs, "출발 허브를 찾을 수 없습니다.");
    HubResponseDto arrivalHub = findHub(
        movementRequestDto.getArrivalHubId(), hubs, "도착 허브를 찾을 수 없습니다.");

    String intermediateHubName = findIntermediateHub(departureHub.getName());

    HubResponseDto intermediateHub = findIntermediateHubByName(intermediateHubName, hubs);

    double totalDistance = calculateTotalDistance(departureHub, intermediateHub, arrivalHub);
    double expectedTime = calculateExpectedTime(totalDistance);

    totalDistance = roundToTwoDecimalPlaces(totalDistance);
    expectedTime = roundToTwoDecimalPlaces(expectedTime);

    Movement movement = Movement.creatMovement(
        movementRequestDto.getDepartureHubId(),
        intermediateHub.getHubId(),
        movementRequestDto.getArrivalHubId(),
        expectedTime,
        totalDistance,
        userId
    );

    movementRepository.save(movement);
  }

  private double calculateTotalDistance(
      HubResponseDto departureHub,
      HubResponseDto intermediateHub,
      HubResponseDto arrivalHub
  ) {
    double departureToIntermediate = calculateDistance(departureHub, intermediateHub);
    double intermediateToArrival = calculateDistance(intermediateHub, arrivalHub);
    return departureToIntermediate + intermediateToArrival;
  }

  private double calculateDistance(HubResponseDto fromHub, HubResponseDto toHub) {
    return calculateDistance(
        fromHub.getLatitude(), fromHub.getLongitude(),
        toHub.getLatitude(), toHub.getLongitude()
    );
  }

  private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {

    double latDistance = Math.toRadians(lat2 - lat1);
    double lonDistance = Math.toRadians(lon2 - lon1);

    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
        + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
        * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    return EARTH_RADIUS * c;
  }

  private static double calculateExpectedTime(double distance) {
    return distance / AVERAGE_SPEED_KMH;
  }

  private HubResponseDto findHub(UUID hubId, List<HubResponseDto> hubs, String errorMessage) {
    return hubs.stream()
        .filter(hub -> hub.getHubId().equals(hubId))
        .findFirst()
        .orElseThrow(() -> new RuntimeException(errorMessage));
  }

  public static String findIntermediateHub(String departureHubName) {

    departureHubName = normalizeString(departureHubName.trim());

    if (regionAdjacency.containsKey(departureHubName)) {
      return departureHubName;
    }

    for (String key : regionAdjacency.keySet()) {
      List<String> values = regionAdjacency.get(key);

      for (String value : values) {
        if (value.equals(departureHubName)) {
          return key;
        }
      }
    }
    throw new IllegalArgumentException("해당 데이터가 존재하지 않습니다.");
  }

  private HubResponseDto findIntermediateHubByName(String intermediateHubName,
      List<HubResponseDto> hubs) {
    return hubs.stream()
        .filter(hub -> hub.getName().equalsIgnoreCase(intermediateHubName))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("해당 데이터가 존재하지 않습니다."));
  }

  private static double roundToTwoDecimalPlaces(double value) {
    return Math.round(value * 100.0) / 100.0;
  }

  private void validateMasterRole(String role) {
    if (!"MASTER".equals(role)) {
      throw new IllegalArgumentException("마스터 권한을 가진 사용자가 아닙니다.");
    }
  }

  public static String normalizeString(String str) {
    return Normalizer.normalize(str, Normalizer.Form.NFC);
  }
//
//  public List<MovementResponseDto> getMovementList() {
//    return null;
//  }
}
