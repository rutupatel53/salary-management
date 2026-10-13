package com.acme.salary.enums;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class InsightDimensionTest{
 @Test void onlyAllowsKnownGroupingDimensions(){assertEquals(InsightDimension.COUNTRY,InsightDimension.parse("country"));assertEquals(InsightDimension.DEPARTMENT,InsightDimension.parse("department"));assertEquals(InsightDimension.JOB_TITLE,InsightDimension.parse("jobTitle"));assertThrows(IllegalArgumentException.class,()->InsightDimension.parse("currency"));}
}
