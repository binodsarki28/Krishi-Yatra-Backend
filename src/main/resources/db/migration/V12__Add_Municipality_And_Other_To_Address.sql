-- Migration to add municipality and other fields to address table
-- 1. Add municipality
ALTER TABLE ADDRESS ADD COLUMN MUNICIPALITY VARCHAR(255);

-- 2. Rename house_number to other
ALTER TABLE ADDRESS RENAME COLUMN HOUSE_NUMBER TO OTHER;
