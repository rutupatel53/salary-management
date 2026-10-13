package com.acme.salary.service;

import com.acme.salary.dto.PayInsightsResponse;
import com.acme.salary.dto.SalaryOutlierResponse;
import com.acme.salary.repository.InsightsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class InsightsServiceImpl implements InsightsService {
    private final InsightsRepository insightsRepository;

    public InsightsServiceImpl(InsightsRepository insightsRepository) {
        this.insightsRepository = insightsRepository;
    }

    @Override
    public PayInsightsResponse pay(String groupBy, String country, UUID departmentId, String jobTitle) {
        return insightsRepository.pay(groupBy, country, departmentId, jobTitle);
    }

    @Override
    public List<SalaryOutlierResponse> outliers(String country, UUID departmentId, String jobTitle) {
        return insightsRepository.outliers(country, departmentId, jobTitle);
    }
}
