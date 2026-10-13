import { Injectable, inject } from '@angular/core';
import { ApiBaseService } from '../../core/api-base.service';
import { ApiResponse } from './employee-api.types';
import { SalaryChangeRequest, SalaryVersion } from './salary.models';
@Injectable({providedIn:'root'})
export class SalaryApiService{
 private readonly api=inject(ApiBaseService);
 history(id:string){return this.api.get<ApiResponse<SalaryVersion[]>>(`/employees/${id}/salary-history`);}
 append(id:string,value:SalaryChangeRequest){return this.api.post<ApiResponse<SalaryVersion>>(`/employees/${id}/salary-history`,value);}
}
