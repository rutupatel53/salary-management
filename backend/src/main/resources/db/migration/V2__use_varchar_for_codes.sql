ALTER TABLE employees DROP CONSTRAINT employees_country_code_fkey;

ALTER TABLE countries
    ALTER COLUMN country_code TYPE VARCHAR(2) USING btrim(country_code)::VARCHAR(2),
    ALTER COLUMN currency_code TYPE VARCHAR(3) USING btrim(currency_code)::VARCHAR(3);

ALTER TABLE employees
    ALTER COLUMN country_code TYPE VARCHAR(2) USING btrim(country_code)::VARCHAR(2);

ALTER TABLE employees
    ADD CONSTRAINT fk_employees_country FOREIGN KEY (country_code) REFERENCES countries(country_code);

ALTER TABLE salary_versions
    ALTER COLUMN currency_code TYPE VARCHAR(3) USING btrim(currency_code)::VARCHAR(3);
