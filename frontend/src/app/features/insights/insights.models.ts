export type InsightDimension='country'|'department'|'jobTitle';
export interface PaySummaryRow{groupName:string;currencyCode:string;employeeCount:number;average:number;median:number;minimum:number;maximum:number;range:number;}
export interface PayInsights{groupBy:InsightDimension;groups:PaySummaryRow[];}
export interface SalaryOutlier{employeeId:string;employeeNumber:string;firstName:string;lastName:string;jobTitle:string;countryName:string;currencyCode:string;gross:number;lowerBound:number;upperBound:number;}
