-- V2: Supporting tables for ElementCollection mappings
-- These are automatically created by Hibernate but defined here for Flyway awareness

-- User target companies (if not created by Hibernate)
CREATE TABLE IF NOT EXISTS user_target_companies (
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    company VARCHAR(255)
);

-- Problem tags
CREATE TABLE IF NOT EXISTS problem_tags (
    problem_id UUID REFERENCES problems(id) ON DELETE CASCADE,
    tag VARCHAR(255)
);

-- Problem companies
CREATE TABLE IF NOT EXISTS problem_companies (
    problem_id UUID REFERENCES problems(id) ON DELETE CASCADE,
    company VARCHAR(255)
);

-- Problem hints
CREATE TABLE IF NOT EXISTS problem_hints (
    problem_id UUID REFERENCES problems(id) ON DELETE CASCADE,
    hint TEXT
);

-- Session improvement areas
CREATE TABLE IF NOT EXISTS session_improvement_areas (
    session_id UUID REFERENCES interview_sessions(id) ON DELETE CASCADE,
    area TEXT
);

-- Session strengths
CREATE TABLE IF NOT EXISTS session_strengths (
    session_id UUID REFERENCES interview_sessions(id) ON DELETE CASCADE,
    strength TEXT
);

-- Behavioral question companies
CREATE TABLE IF NOT EXISTS bq_companies (
    question_id UUID REFERENCES behavioral_questions(id) ON DELETE CASCADE,
    company VARCHAR(255)
);

-- Behavioral question keywords
CREATE TABLE IF NOT EXISTS bq_keywords (
    question_id UUID REFERENCES behavioral_questions(id) ON DELETE CASCADE,
    keyword VARCHAR(255)
);

-- Metric languages used
CREATE TABLE IF NOT EXISTS metric_languages (
    metric_id UUID REFERENCES user_metrics(id) ON DELETE CASCADE,
    language VARCHAR(100)
);

-- Company preferred languages
CREATE TABLE IF NOT EXISTS company_languages (
    company_id UUID REFERENCES company_patterns(id) ON DELETE CASCADE,
    language VARCHAR(100)
);

-- Company focus areas
CREATE TABLE IF NOT EXISTS company_focus_areas (
    company_id UUID REFERENCES company_patterns(id) ON DELETE CASCADE,
    area VARCHAR(255)
);

-- Company tips
CREATE TABLE IF NOT EXISTS company_tips (
    company_id UUID REFERENCES company_patterns(id) ON DELETE CASCADE,
    tip TEXT
);
