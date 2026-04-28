-- V4: Clear seeded data to allow DataSeeder to re-populate with expanded problem set (20 problems)
-- This is safe because we only delete seed data, not user submissions

-- Delete problems (cascade will handle related submissions)
DELETE FROM code_submissions;
DELETE FROM problems;

-- Delete behavioral questions and responses to allow re-seeding
DELETE FROM behavioral_responses;
DELETE FROM behavioral_questions;

-- Delete company patterns to allow re-seeding
DELETE FROM company_patterns;
