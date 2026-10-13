package com.acme.salary.dto;
import java.util.List;
public record PayInsightsResponse(String groupBy,List<PaySummaryRow> groups){}
