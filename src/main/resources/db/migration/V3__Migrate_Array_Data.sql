-- V3: Migrate data from ElementCollection join tables into native TEXT[] columns
-- This fixes the mismatch where data was stored in join tables but queries read TEXT[] columns

-- Migrate problem tags from join table into problems.tags TEXT[]
UPDATE problems p
SET tags = (
    SELECT ARRAY_AGG(pt.tag)
    FROM problem_tags pt
    WHERE pt.problem_id = p.id
)
WHERE EXISTS (SELECT 1 FROM problem_tags pt WHERE pt.problem_id = p.id);

-- Migrate problem companies from join table into problems.companies TEXT[]
UPDATE problems p
SET companies = (
    SELECT ARRAY_AGG(pc.company)
    FROM problem_companies pc
    WHERE pc.problem_id = p.id
)
WHERE EXISTS (SELECT 1 FROM problem_companies pc WHERE pc.problem_id = p.id);

-- Migrate problem hints from join table into problems.hints TEXT[]
UPDATE problems p
SET hints = (
    SELECT ARRAY_AGG(ph.hint)
    FROM problem_hints ph
    WHERE ph.problem_id = p.id
)
WHERE EXISTS (SELECT 1 FROM problem_hints ph WHERE ph.problem_id = p.id);

-- Migrate behavioral question companies from join table into behavioral_questions.companies TEXT[]
UPDATE behavioral_questions bq
SET companies = (
    SELECT ARRAY_AGG(bc.company)
    FROM bq_companies bc
    WHERE bc.question_id = bq.id
)
WHERE EXISTS (SELECT 1 FROM bq_companies bc WHERE bc.question_id = bq.id);

-- Migrate behavioral question keywords from join table into behavioral_questions.keywords TEXT[]
UPDATE behavioral_questions bq
SET keywords = (
    SELECT ARRAY_AGG(bk.keyword)
    FROM bq_keywords bk
    WHERE bk.question_id = bq.id
)
WHERE EXISTS (SELECT 1 FROM bq_keywords bk WHERE bk.question_id = bq.id);

-- Migrate user target companies from join table into users.target_companies TEXT[]
UPDATE users u
SET target_companies = (
    SELECT ARRAY_AGG(utc.company)
    FROM user_target_companies utc
    WHERE utc.user_id = u.id
)
WHERE EXISTS (SELECT 1 FROM user_target_companies utc WHERE utc.user_id = u.id);

-- Migrate company preferred languages from join table into company_patterns.preferred_languages TEXT[]
UPDATE company_patterns cp
SET preferred_languages = (
    SELECT ARRAY_AGG(cl.language)
    FROM company_languages cl
    WHERE cl.company_id = cp.id
)
WHERE EXISTS (SELECT 1 FROM company_languages cl WHERE cl.company_id = cp.id);

-- Migrate company focus areas from join table into company_patterns.focus_areas TEXT[]
UPDATE company_patterns cp
SET focus_areas = (
    SELECT ARRAY_AGG(cfa.area)
    FROM company_focus_areas cfa
    WHERE cfa.company_id = cp.id
)
WHERE EXISTS (SELECT 1 FROM company_focus_areas cfa WHERE cfa.company_id = cp.id);

-- Migrate company tips from join table into company_patterns.tips TEXT[]
UPDATE company_patterns cp
SET tips = (
    SELECT ARRAY_AGG(ct.tip)
    FROM company_tips ct
    WHERE ct.company_id = cp.id
)
WHERE EXISTS (SELECT 1 FROM company_tips ct WHERE ct.company_id = cp.id);

-- Migrate interview session improvement areas into interview_sessions.improvement_areas TEXT[]
UPDATE interview_sessions s
SET improvement_areas = (
    SELECT ARRAY_AGG(sia.area)
    FROM session_improvement_areas sia
    WHERE sia.session_id = s.id
)
WHERE EXISTS (SELECT 1 FROM session_improvement_areas sia WHERE sia.session_id = s.id);

-- Migrate interview session strengths into interview_sessions.strengths TEXT[]
UPDATE interview_sessions s
SET strengths = (
    SELECT ARRAY_AGG(ss.strength)
    FROM session_strengths ss
    WHERE ss.session_id = s.id
)
WHERE EXISTS (SELECT 1 FROM session_strengths ss WHERE ss.session_id = s.id);
