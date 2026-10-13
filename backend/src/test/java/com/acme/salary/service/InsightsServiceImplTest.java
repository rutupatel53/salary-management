package com.acme.salary.service;
import com.acme.salary.dto.PaySummaryRow;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
class InsightsServiceImplTest{
 @Test void queryUsesGeneratedGrossGroupsByCurrencyAndReturnsRoundedSummary(){
  NamedParameterJdbcTemplate jdbc=mock(NamedParameterJdbcTemplate.class);PaySummaryRow row=new PaySummaryRow("India","INR",200,new BigDecimal("4500.00"),new BigDecimal("4500.00"),new BigDecimal("3000.00"),new BigDecimal("25000.00"),new BigDecimal("22000.00"));
  when(jdbc.query(anyString(),any(MapSqlParameterSource.class),any(RowMapper.class))).thenReturn(List.of(row));
  var result=new InsightsServiceImpl(jdbc).pay("country",null,null,null);
  assertEquals(1,result.groups().size());assertEquals("INR",result.groups().get(0).currencyCode());assertEquals(new BigDecimal("4500.00"),result.groups().get(0).average());
  var sql=org.mockito.ArgumentCaptor.forClass(String.class);verify(jdbc).query(sql.capture(),any(MapSqlParameterSource.class),any(RowMapper.class));
  assertTrue(sql.getValue().contains("avg(salary.gross)"));assertTrue(sql.getValue().contains("percentile_cont(0.5)"));assertTrue(sql.getValue().contains("GROUP BY country_ref.name, salary.currency_code"));
 }
 @Test void outlierSqlUsesTheApprovedGroupsAndFiveSalaryMinimum(){
  NamedParameterJdbcTemplate jdbc=mock(NamedParameterJdbcTemplate.class);when(jdbc.query(anyString(),any(MapSqlParameterSource.class),any(RowMapper.class))).thenReturn(List.of());
  new InsightsServiceImpl(jdbc).outliers(null,null,null);
  var sql=org.mockito.ArgumentCaptor.forClass(String.class);verify(jdbc).query(sql.capture(),any(MapSqlParameterSource.class),any(RowMapper.class));
  assertTrue(sql.getValue().contains("GROUP BY employee.job_title, employee.country_code, salary.currency_code"));assertTrue(sql.getValue().contains("HAVING count(*) >= 5"));assertTrue(sql.getValue().contains("1.5 * (bounds.q3 - bounds.q1)"));
 }
}
