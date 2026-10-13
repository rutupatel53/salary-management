package com.acme.salary.service;
import com.acme.salary.dto.*;
import com.acme.salary.enums.InsightDimension;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Service
public class InsightsServiceImpl implements InsightsService{
 private final NamedParameterJdbcTemplate jdbc;
 public InsightsServiceImpl(NamedParameterJdbcTemplate jdbc){this.jdbc=jdbc;}
 @Override public PayInsightsResponse pay(String groupBy,String country,UUID department,String jobTitle){
  InsightDimension dimension=InsightDimension.parse(groupBy);
  String sql="WITH current_salary AS (SELECT DISTINCT ON (employee_id) employee_id,gross,currency_code FROM salary_versions WHERE effective_date<=CURRENT_DATE ORDER BY employee_id,effective_date DESC) "+
   "SELECT "+dimension.expression()+" AS group_name,s.currency_code,count(*) AS employee_count,avg(s.gross) AS average,percentile_cont(0.5) WITHIN GROUP (ORDER BY s.gross) AS median,min(s.gross) AS minimum,max(s.gross) AS maximum "+
   "FROM employees e JOIN current_salary s USING(employee_id) JOIN countries c ON c.country_code=e.country_code JOIN departments d ON d.department_id=e.department_id "+
   "WHERE e.archived_at IS NULL "+filters(country,department,jobTitle)+" GROUP BY "+dimension.expression()+",s.currency_code ORDER BY group_name,s.currency_code";
  var rows=jdbc.query(sql,params(country,department,jobTitle),(rs,n)->summary(rs));
  return new PayInsightsResponse(groupBy,rows);
 }
 @Override public List<SalaryOutlierResponse> outliers(String country,UUID department,String jobTitle){
  String sql="WITH current_salary AS (SELECT DISTINCT ON (employee_id) employee_id,gross,currency_code FROM salary_versions WHERE effective_date<=CURRENT_DATE ORDER BY employee_id,effective_date DESC), "+
   "bounds AS (SELECT e.job_title,e.country_code,s.currency_code,percentile_cont(0.25) WITHIN GROUP (ORDER BY s.gross) AS q1,percentile_cont(0.75) WITHIN GROUP (ORDER BY s.gross) AS q3 "+
   "FROM employees e JOIN current_salary s USING(employee_id) WHERE e.archived_at IS NULL "+filters(country,null,jobTitle)+" GROUP BY e.job_title,e.country_code,s.currency_code HAVING count(*)>=5) "+
   "SELECT e.employee_id,e.employee_number,e.first_name,e.last_name,e.job_title,c.name AS country_name,s.currency_code,s.gross,(b.q1-1.5*(b.q3-b.q1)) AS lower_bound,(b.q3+1.5*(b.q3-b.q1)) AS upper_bound "+
   "FROM employees e JOIN current_salary s USING(employee_id) JOIN countries c ON c.country_code=e.country_code JOIN bounds b ON b.job_title=e.job_title AND b.country_code=e.country_code AND b.currency_code=s.currency_code "+
   "WHERE e.archived_at IS NULL AND (s.gross < b.q1-1.5*(b.q3-b.q1) OR s.gross > b.q3+1.5*(b.q3-b.q1))"+
   (department==null?"":" AND e.department_id=:department")+
   "ORDER BY e.job_title,c.name,s.currency_code,e.employee_number";
  return jdbc.query(sql,params(country,department,jobTitle),(rs,n)->new SalaryOutlierResponse(
   rs.getObject("employee_id",UUID.class),rs.getString("employee_number"),rs.getString("first_name"),rs.getString("last_name"),rs.getString("job_title"),rs.getString("country_name"),rs.getString("currency_code"),rs.getBigDecimal("gross"),rs.getBigDecimal("lower_bound"),rs.getBigDecimal("upper_bound")));
 }
 private String filters(String country,UUID department,String jobTitle){StringBuilder b=new StringBuilder();if(country!=null)b.append(" AND e.country_code=:country");if(department!=null)b.append(" AND e.department_id=:department");if(jobTitle!=null&&!jobTitle.isBlank())b.append(" AND lower(e.job_title)=lower(:jobTitle)");return b.toString();}
 private MapSqlParameterSource params(String country,UUID department,String jobTitle){return new MapSqlParameterSource().addValue("country",country).addValue("department",department).addValue("jobTitle",jobTitle);}
 private PaySummaryRow summary(ResultSet rs)throws SQLException{BigDecimal minimum=rs.getBigDecimal("minimum"),maximum=rs.getBigDecimal("maximum");return new PaySummaryRow(rs.getString("group_name"),rs.getString("currency_code"),rs.getLong("employee_count"),rs.getBigDecimal("average").setScale(2,RoundingMode.HALF_UP),rs.getBigDecimal("median").setScale(2,RoundingMode.HALF_UP),minimum,maximum,maximum.subtract(minimum));}
}
