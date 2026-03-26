-- Unify delete_flag semantics:
-- N = active (displayed), Y = soft deleted (hidden)

UPDATE doc_document_type
SET delete_flag = CASE delete_flag WHEN 'Y' THEN 'N' WHEN 'N' THEN 'Y' ELSE delete_flag END
WHERE delete_flag IN ('Y', 'N');

UPDATE md_country
SET delete_flag = CASE delete_flag WHEN 'Y' THEN 'N' WHEN 'N' THEN 'Y' ELSE delete_flag END
WHERE delete_flag IN ('Y', 'N');

UPDATE md_company_project_org_category
SET delete_flag = CASE delete_flag WHEN 'Y' THEN 'N' WHEN 'N' THEN 'Y' ELSE delete_flag END
WHERE delete_flag IN ('Y', 'N');

UPDATE md_company_project
SET delete_flag = CASE delete_flag WHEN 'Y' THEN 'N' WHEN 'N' THEN 'Y' ELSE delete_flag END
WHERE delete_flag IN ('Y', 'N');

UPDATE md_company_project_line
SET delete_flag = CASE delete_flag WHEN 'Y' THEN 'N' WHEN 'N' THEN 'Y' ELSE delete_flag END
WHERE delete_flag IN ('Y', 'N');

UPDATE md_document_organization_city
SET delete_flag = CASE delete_flag WHEN 'Y' THEN 'N' WHEN 'N' THEN 'Y' ELSE delete_flag END
WHERE delete_flag IN ('Y', 'N');

UPDATE md_document_organization
SET delete_flag = CASE delete_flag WHEN 'Y' THEN 'N' WHEN 'N' THEN 'Y' ELSE delete_flag END
WHERE delete_flag IN ('Y', 'N');

ALTER TABLE doc_document_type ALTER COLUMN delete_flag SET DEFAULT 'N';
ALTER TABLE md_country ALTER COLUMN delete_flag SET DEFAULT 'N';
ALTER TABLE md_company_project_org_category ALTER COLUMN delete_flag SET DEFAULT 'N';
ALTER TABLE md_company_project ALTER COLUMN delete_flag SET DEFAULT 'N';
ALTER TABLE md_company_project_line ALTER COLUMN delete_flag SET DEFAULT 'N';
ALTER TABLE md_document_organization_city ALTER COLUMN delete_flag SET DEFAULT 'N';
ALTER TABLE md_document_organization ALTER COLUMN delete_flag SET DEFAULT 'N';

DROP INDEX IF EXISTS uk_doc_document_type_code_active;
CREATE UNIQUE INDEX IF NOT EXISTS uk_doc_document_type_code_active
    ON doc_document_type (type_code)
    WHERE delete_flag = 'N';

DROP INDEX IF EXISTS uk_md_country_active;
CREATE UNIQUE INDEX IF NOT EXISTS uk_md_country_active
    ON md_country (country_code)
    WHERE delete_flag = 'N';

DROP INDEX IF EXISTS uk_md_company_project_org_category_active;
CREATE UNIQUE INDEX IF NOT EXISTS uk_md_company_project_org_category_active
    ON md_company_project_org_category (category_code)
    WHERE delete_flag = 'N';

DROP INDEX IF EXISTS uk_md_company_project_code_active;
CREATE UNIQUE INDEX IF NOT EXISTS uk_md_company_project_code_active
    ON md_company_project (company_project_code)
    WHERE delete_flag = 'N';

DROP INDEX IF EXISTS uk_md_company_project_line_code_active;
CREATE UNIQUE INDEX IF NOT EXISTS uk_md_company_project_line_code_active
    ON md_company_project_line (company_project_code, organization_code)
    WHERE delete_flag = 'N';

DROP INDEX IF EXISTS uk_md_document_org_city_active;
CREATE UNIQUE INDEX IF NOT EXISTS uk_md_document_org_city_active
    ON md_document_organization_city (country_code, city_code)
    WHERE delete_flag = 'N';

DROP INDEX IF EXISTS uk_md_document_organization_code_active;
CREATE UNIQUE INDEX IF NOT EXISTS uk_md_document_organization_code_active
    ON md_document_organization (document_organization_code)
    WHERE delete_flag = 'N';
