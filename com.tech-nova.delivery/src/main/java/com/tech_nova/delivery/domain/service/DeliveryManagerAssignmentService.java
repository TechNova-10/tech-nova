package com.tech_nova.delivery.domain.service;

import com.tech_nova.delivery.domain.model.delivery.DeliveryCompanyRouteRecord;
import com.tech_nova.delivery.domain.model.delivery.DeliveryRouteRecord;
import com.tech_nova.delivery.domain.model.manager.DeliveryManager;
import com.tech_nova.delivery.domain.repository.DeliveryManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryManagerAssignmentService {
    private final DeliveryManagerRepository deliveryManagerRepository;

    // 업체 배송 담당자 배정 로직
    public List<DeliveryManager> assignmentCompanyDeliveryManager(DeliveryCompanyRouteRecord companyRouteRecord) {
        return new ArrayList<>();
    }

    // 허브 배송 담당자 배정 로직
    public void assignmentHubDeliveryManager(List<DeliveryRouteRecord> routeRecord) {

    }
}
