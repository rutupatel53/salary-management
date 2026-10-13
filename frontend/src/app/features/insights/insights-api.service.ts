import { Injectable,inject } from '@angular/core';
import { ApiBaseService } from '../../core/api-base.service';
import { ApiResponse } from '../employees/employee-api.types';
import { InsightDimension,PayInsights,SalaryOutlier } from './insights.models';
@Injectable({providedIn:'root'})
export class InsightsApiService{
 private readonly api=inject(ApiBaseService);
 pay(filters:{groupBy:InsightDimension;country:string;department:string;jobTitle:string}){return this.api.get<ApiResponse<PayInsights>>('/insights/pay',filters);}
 outliers(filters:{country:string;department:string;jobTitle:string}){return this.api.get<ApiResponse<SalaryOutlier[]>>('/insights/outliers',filters);}
}
