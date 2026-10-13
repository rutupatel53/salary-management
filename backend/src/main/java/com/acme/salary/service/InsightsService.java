package com.acme.salary.service;
import com.acme.salary.dto.PayInsightsResponse;
import com.acme.salary.dto.SalaryOutlierResponse;
import java.util.List;
import java.util.UUID;
public interface InsightsService{
 PayInsightsResponse pay(String groupBy,String country,UUID department,String jobTitle);
 List<SalaryOutlierResponse> outliers(String country,UUID department,String jobTitle);
}
