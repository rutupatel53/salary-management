CREATE TABLE salary_templates (
    template_id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL CHECK (length(trim(name)) > 0),
    country_code VARCHAR(2) NOT NULL REFERENCES countries(country_code),
    currency_code VARCHAR(3) NOT NULL,
    status VARCHAR(16) NOT NULL CHECK (status IN ('DRAFT','ACTIVE','ARCHIVED')),
    revision BIGINT NOT NULL DEFAULT 0,
    components JSONB NOT NULL CHECK (jsonb_typeof(components) = 'array' AND jsonb_array_length(components) BETWEEN 1 AND 20)
);
CREATE UNIQUE INDEX ux_template_name_country ON salary_templates(country_code, lower(name));
CREATE INDEX ix_template_country_status ON salary_templates(country_code, status, name);
ALTER TABLE salary_versions ADD COLUMN template_id UUID REFERENCES salary_templates(template_id),
    ADD COLUMN template_name VARCHAR(100), ADD COLUMN template_revision BIGINT,
    ADD COLUMN annual_ctc NUMERIC(14,2) CHECK (annual_ctc > 0),
    ADD COLUMN assignment_breakdown JSONB;
ALTER TABLE activity_log DROP CONSTRAINT activity_log_action_check;
ALTER TABLE activity_log ADD CONSTRAINT activity_log_action_check CHECK (action IN (
    'EMPLOYEE_CREATED','EMPLOYEE_UPDATED','EMPLOYEE_ARCHIVED','SALARY_CHANGED',
    'IMPORT_CONFIRMED','EXPORT_CREATED','TEMPLATE_CREATED','TEMPLATE_UPDATED'));

INSERT INTO salary_templates(template_id,name,country_code,currency_code,status,components)
SELECT md5('acme-standard-template-' || country_code)::uuid, 'Standard compensation', country_code, currency_code, 'ACTIVE',
 '[{"name":"Basic pay","category":"BASIC","calculation":"PERCENT_CTC","value":40},{"name":"Special allowance","category":"ALLOWANCE","calculation":"BALANCE","value":0}]'::jsonb
FROM countries;
