import { Pool } from 'pg';
export function createDatabase() {
  return new Pool({ connectionString: process.env.DB_URL ?? process.env.DATABASE_URL ?? 'postgresql://salary:salary@127.0.0.1:5432/salary_management' });
}
