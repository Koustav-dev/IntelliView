-- V5: Widen VARCHAR columns that are too short for AI-generated content
ALTER TABLE code_submissions ALTER COLUMN time_complexity_detected TYPE VARCHAR(500);
ALTER TABLE code_submissions ALTER COLUMN space_complexity_detected TYPE VARCHAR(500);
