CREATE TABLE ai_news_source (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    source_type VARCHAR(50) NOT NULL,
    url TEXT NOT NULL UNIQUE,
    credibility_score NUMERIC(5,2) DEFAULT 5.00,
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ai_news_article (
    id BIGSERIAL PRIMARY KEY,
    source_id BIGINT REFERENCES ai_news_source(id),
    title TEXT NOT NULL,
    url TEXT NOT NULL UNIQUE,
    author VARCHAR(255),
    published_at TIMESTAMP,
    raw_content TEXT,
    content_hash VARCHAR(128),
    crawled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'NEW'
);

CREATE INDEX idx_ai_news_article_status ON ai_news_article(status);
CREATE INDEX idx_ai_news_article_published_at ON ai_news_article(published_at);
CREATE INDEX idx_ai_news_article_hash ON ai_news_article(content_hash);

CREATE TABLE ai_article_score (
    id BIGSERIAL PRIMARY KEY,
    article_id BIGINT NOT NULL REFERENCES ai_news_article(id) ON DELETE CASCADE,
    freshness_score NUMERIC(5,2),
    credibility_score NUMERIC(5,2),
    ai_relevance_score NUMERIC(5,2),
    enterprise_relevance_score NUMERIC(5,2),
    final_score NUMERIC(5,2),
    scoring_reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ai_article_summary (
    id BIGSERIAL PRIMARY KEY,
    article_id BIGINT NOT NULL REFERENCES ai_news_article(id) ON DELETE CASCADE,
    short_summary TEXT,
    detailed_summary TEXT,
    why_it_matters TEXT,
    generated_by VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE linkedin_post_draft (
    id BIGSERIAL PRIMARY KEY,
    article_id BIGINT REFERENCES ai_news_article(id),
    title VARCHAR(500),
    source_url TEXT,
    post_body TEXT NOT NULL,
    hashtags TEXT,
    status VARCHAR(50) DEFAULT 'PENDING_APPROVAL',
    approved_by VARCHAR(150),
    approved_at TIMESTAMP,
    published_at TIMESTAMP,
    linkedin_post_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_linkedin_post_draft_status ON linkedin_post_draft(status);

CREATE TABLE linkedin_oauth_token (
    id BIGSERIAL PRIMARY KEY,
    provider VARCHAR(50) DEFAULT 'LINKEDIN',
    access_token TEXT NOT NULL,
    refresh_token TEXT,
    expires_at TIMESTAMP,
    member_urn VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE agent_audit_log (
    id BIGSERIAL PRIMARY KEY,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100),
    entity_id BIGINT,
    status VARCHAR(50),
    message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO ai_news_source(name, source_type, url, credibility_score, enabled) VALUES
('OpenAI News', 'RSS', 'https://openai.com/news/rss.xml', 10, true),
('Google DeepMind Blog', 'RSS', 'https://deepmind.google/blog/rss.xml', 10, true),
('Anthropic News', 'RSS', 'https://www.anthropic.com/news/rss.xml', 10, true),
('Hugging Face Blog', 'RSS', 'https://huggingface.co/blog/feed.xml', 9, true),
('LangChain Blog', 'RSS', 'https://blog.langchain.dev/rss/', 8, true),
('LlamaIndex Blog', 'RSS', 'https://www.llamaindex.ai/blog/rss.xml', 8, true),
('NVIDIA AI Blog', 'RSS', 'https://blogs.nvidia.com/blog/category/deep-learning/feed/', 9, true),
('TechCrunch AI', 'RSS', 'https://techcrunch.com/category/artificial-intelligence/feed/', 7, true),
('VentureBeat AI', 'RSS', 'https://venturebeat.com/category/ai/feed/', 7, true)
ON CONFLICT (url) DO NOTHING;
