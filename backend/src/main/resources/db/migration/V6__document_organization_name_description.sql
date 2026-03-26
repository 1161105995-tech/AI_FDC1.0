ALTER TABLE md_document_organization
    ADD COLUMN IF NOT EXISTS document_organization_name VARCHAR(128);

ALTER TABLE md_document_organization
    ADD COLUMN IF NOT EXISTS description VARCHAR(500);

UPDATE md_document_organization
SET document_organization_name = COALESCE(document_organization_name, document_organization_code),
    description = COALESCE(description, document_organization_code)
WHERE document_organization_name IS NULL
   OR description IS NULL;

ALTER TABLE md_document_organization
    ALTER COLUMN document_organization_name SET NOT NULL;

ALTER TABLE md_document_organization
    ALTER COLUMN description SET NOT NULL;
