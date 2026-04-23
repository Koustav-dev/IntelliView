-- V1: Initial Schema
-- Users table
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    username VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    avatar_url VARCHAR(500),
    bio TEXT,
    github_url VARCHAR(500),
    linkedin_url VARCHAR(500),
    target_companies TEXT[],
    experience_level VARCHAR(50) DEFAULT 'BEGINNER',
    role VARCHAR(50) DEFAULT 'USER',
    is_verified BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    verification_token VARCHAR(255),
    reset_token VARCHAR(255),
    reset_token_expiry TIMESTAMP,
    streak_count INTEGER DEFAULT 0,
    last_active_date DATE,
    total_interviews INTEGER DEFAULT 0,
    total_problems_solved INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Problems table
CREATE TABLE problems (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(500) NOT NULL,
    slug VARCHAR(500) UNIQUE NOT NULL,
    description TEXT NOT NULL,
    difficulty VARCHAR(20) NOT NULL CHECK (difficulty IN ('EASY', 'MEDIUM', 'HARD')),
    category VARCHAR(100),
    tags TEXT[],
    companies TEXT[],
    constraints TEXT,
    examples JSONB,
    hints TEXT[],
    solution_template JSONB,
    optimal_solution JSONB,
    time_complexity VARCHAR(100),
    space_complexity VARCHAR(100),
    acceptance_rate DECIMAL(5,2) DEFAULT 0,
    total_submissions INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    premium_only BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Interview sessions table
CREATE TABLE interview_sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    session_type VARCHAR(50) NOT NULL CHECK (session_type IN ('TECHNICAL', 'BEHAVIORAL', 'SYSTEM_DESIGN', 'MIXED')),
    company_target VARCHAR(100),
    difficulty VARCHAR(20),
    status VARCHAR(50) DEFAULT 'IN_PROGRESS' CHECK (status IN ('IN_PROGRESS', 'COMPLETED', 'ABANDONED')),
    total_score INTEGER DEFAULT 0,
    max_score INTEGER DEFAULT 100,
    duration_minutes INTEGER,
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    ai_feedback TEXT,
    improvement_areas TEXT[],
    strengths TEXT[],
    metadata JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Code submissions table
CREATE TABLE code_submissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    problem_id UUID REFERENCES problems(id) ON DELETE CASCADE,
    session_id UUID REFERENCES interview_sessions(id) ON DELETE SET NULL,
    language VARCHAR(50) NOT NULL,
    code TEXT NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'RUNNING', 'ACCEPTED', 'WRONG_ANSWER', 'TIME_LIMIT_EXCEEDED', 'MEMORY_LIMIT_EXCEEDED', 'RUNTIME_ERROR', 'COMPILE_ERROR')),
    runtime_ms INTEGER,
    memory_kb INTEGER,
    test_cases_passed INTEGER DEFAULT 0,
    total_test_cases INTEGER DEFAULT 0,
    output TEXT,
    error_message TEXT,
    ai_code_review JSONB,
    time_complexity_detected VARCHAR(100),
    space_complexity_detected VARCHAR(100),
    code_quality_score INTEGER,
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Behavioral questions table
CREATE TABLE behavioral_questions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    question TEXT NOT NULL,
    category VARCHAR(100),
    companies TEXT[],
    difficulty VARCHAR(20) DEFAULT 'MEDIUM',
    sample_answer TEXT,
    keywords TEXT[],
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Behavioral responses table
CREATE TABLE behavioral_responses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    question_id UUID REFERENCES behavioral_questions(id) ON DELETE CASCADE,
    session_id UUID REFERENCES interview_sessions(id) ON DELETE SET NULL,
    response_text TEXT,
    audio_url VARCHAR(500),
    ai_feedback JSONB,
    star_score INTEGER,
    clarity_score INTEGER,
    relevance_score INTEGER,
    overall_score INTEGER,
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User performance metrics
CREATE TABLE user_metrics (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    date DATE NOT NULL,
    problems_solved INTEGER DEFAULT 0,
    interviews_completed INTEGER DEFAULT 0,
    avg_score DECIMAL(5,2) DEFAULT 0,
    time_spent_minutes INTEGER DEFAULT 0,
    languages_used TEXT[],
    difficulties_solved JSONB,
    UNIQUE(user_id, date)
);

-- Company interview patterns
CREATE TABLE company_patterns (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_name VARCHAR(100) NOT NULL,
    logo_url VARCHAR(500),
    description TEXT,
    interview_rounds JSONB,
    preferred_languages TEXT[],
    focus_areas TEXT[],
    difficulty_distribution JSONB,
    tips TEXT[],
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Roadmaps table
CREATE TABLE roadmaps (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    target_role VARCHAR(100),
    duration_weeks INTEGER,
    difficulty VARCHAR(20),
    topics JSONB,
    resources JSONB,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User roadmap progress
CREATE TABLE user_roadmap_progress (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    roadmap_id UUID REFERENCES roadmaps(id) ON DELETE CASCADE,
    progress_percentage INTEGER DEFAULT 0,
    completed_topics TEXT[],
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    UNIQUE(user_id, roadmap_id)
);

-- Leaderboard entries
CREATE TABLE leaderboard_entries (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    period VARCHAR(20) NOT NULL CHECK (period IN ('DAILY', 'WEEKLY', 'MONTHLY', 'ALL_TIME')),
    score INTEGER DEFAULT 0,
    rank INTEGER,
    problems_solved INTEGER DEFAULT 0,
    interviews_done INTEGER DEFAULT 0,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, period)
);

-- Achievements
CREATE TABLE achievements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    icon VARCHAR(100),
    condition_type VARCHAR(100),
    condition_value INTEGER,
    points INTEGER DEFAULT 0,
    rarity VARCHAR(50) DEFAULT 'COMMON',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User achievements
CREATE TABLE user_achievements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    achievement_id UUID REFERENCES achievements(id) ON DELETE CASCADE,
    earned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, achievement_id)
);

-- Refresh tokens
CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    token VARCHAR(500) UNIQUE NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_problems_difficulty ON problems(difficulty);
CREATE INDEX idx_problems_slug ON problems(slug);
CREATE INDEX idx_problems_tags ON problems USING gin(tags);
CREATE INDEX idx_problems_companies ON problems USING gin(companies);
CREATE INDEX idx_submissions_user ON code_submissions(user_id);
CREATE INDEX idx_submissions_problem ON code_submissions(problem_id);
CREATE INDEX idx_sessions_user ON interview_sessions(user_id);
CREATE INDEX idx_metrics_user_date ON user_metrics(user_id, date);
