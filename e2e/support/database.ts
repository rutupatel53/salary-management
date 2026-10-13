import { Pool } from 'pg';
export const database=new Pool({connectionString:process.env.DATABASE_URL??'postgresql://salary:salary@127.0.0.1:5432/salary_management'});
