CREATE TABLE users (
  id UUID PRIMARY KEY,
  email VARCHAR(255) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  failed_login_count INT NOT NULL DEFAULT 0,
  locked_until TIMESTAMPTZ NULL,
  enabled BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMPTZ NOT NULL,
  updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE user_roles (
  id UUID PRIMARY KEY,
  user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  role VARCHAR(40) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL,
  updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE workspaces (
  id UUID PRIMARY KEY,
  name VARCHAR(200) NOT NULL,
  owner_id UUID NOT NULL REFERENCES users(id),
  created_at TIMESTAMPTZ NOT NULL,
  updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE workspace_memberships (
  id UUID PRIMARY KEY,
  workspace_id UUID NOT NULL REFERENCES workspaces(id) ON DELETE CASCADE,
  user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  role VARCHAR(40) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL,
  updated_at TIMESTAMPTZ NOT NULL,
  UNIQUE (workspace_id, user_id)
);

CREATE TABLE projects (
  id UUID PRIMARY KEY,
  workspace_id UUID NOT NULL REFERENCES workspaces(id) ON DELETE CASCADE,
  name VARCHAR(200) NOT NULL,
  description VARCHAR(1000),
  created_at TIMESTAMPTZ NOT NULL,
  updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE api_keys (
  id UUID PRIMARY KEY,
  workspace_id UUID NOT NULL REFERENCES workspaces(id) ON DELETE CASCADE,
  name VARCHAR(200) NOT NULL,
  prefix VARCHAR(12) NOT NULL,
  key_hash VARCHAR(64) NOT NULL,
  revoked_at TIMESTAMPTZ NULL,
  created_at TIMESTAMPTZ NOT NULL,
  updated_at TIMESTAMPTZ NOT NULL,
  UNIQUE (prefix)
);

CREATE TABLE invitations (
  id UUID PRIMARY KEY,
  workspace_id UUID NOT NULL REFERENCES workspaces(id) ON DELETE CASCADE,
  email VARCHAR(255) NOT NULL,
  token_hash VARCHAR(64) NOT NULL,
  role VARCHAR(40) NOT NULL,
  status VARCHAR(40) NOT NULL,
  expires_at TIMESTAMPTZ NOT NULL,
  accepted_at TIMESTAMPTZ NULL,
  created_at TIMESTAMPTZ NOT NULL,
  updated_at TIMESTAMPTZ NOT NULL,
  UNIQUE (token_hash)
);

CREATE TABLE refresh_tokens (
  id UUID PRIMARY KEY,
  user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  token_hash VARCHAR(64) NOT NULL,
  expires_at TIMESTAMPTZ NOT NULL,
  revoked_at TIMESTAMPTZ NULL,
  replaced_by_token_id UUID NULL,
  ip_address VARCHAR(64) NULL,
  user_agent VARCHAR(255) NULL,
  created_at TIMESTAMPTZ NOT NULL,
  updated_at TIMESTAMPTZ NOT NULL,
  UNIQUE (token_hash)
);

CREATE TABLE audit_logs (
  id UUID PRIMARY KEY,
  actor_id UUID NULL,
  actor_email VARCHAR(255) NULL,
  action VARCHAR(120) NOT NULL,
  target VARCHAR(255) NULL,
  result VARCHAR(40) NOT NULL,
  request_id VARCHAR(120) NOT NULL,
  ip_address VARCHAR(64) NULL,
  user_agent VARCHAR(255) NULL,
  created_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE roles (
  name VARCHAR(40) PRIMARY KEY,
  description VARCHAR(255)
);

CREATE TABLE permissions (
  name VARCHAR(60) PRIMARY KEY,
  description VARCHAR(255)
);

CREATE TABLE role_permissions (
  role_name VARCHAR(40) NOT NULL REFERENCES roles(name) ON DELETE CASCADE,
  permission_name VARCHAR(60) NOT NULL REFERENCES permissions(name) ON DELETE CASCADE,
  PRIMARY KEY (role_name, permission_name)
);

CREATE INDEX idx_workspace_memberships_user ON workspace_memberships(user_id);
CREATE INDEX idx_projects_workspace ON projects(workspace_id);
CREATE INDEX idx_api_keys_workspace ON api_keys(workspace_id);
CREATE INDEX idx_invitations_workspace ON invitations(workspace_id);
CREATE INDEX idx_refresh_tokens_user ON refresh_tokens(user_id);
CREATE INDEX idx_audit_logs_actor ON audit_logs(actor_id);
CREATE INDEX idx_audit_logs_created ON audit_logs(created_at);

INSERT INTO roles (name, description) VALUES
  ('OWNER', 'Workspace owner'),
  ('ADMIN', 'Workspace admin'),
  ('MEMBER', 'Workspace member'),
  ('API_KEY', 'API key access');

INSERT INTO permissions (name, description) VALUES
  ('WORKSPACE_READ', 'Read workspace metadata'),
  ('WORKSPACE_WRITE', 'Modify workspace settings'),
  ('PROJECT_READ', 'Read projects'),
  ('PROJECT_WRITE', 'Create/update projects'),
  ('API_KEY_READ', 'Read api keys'),
  ('API_KEY_WRITE', 'Create/revoke api keys'),
  ('INVITATION_READ', 'Read invitations'),
  ('INVITATION_WRITE', 'Create invitations');

INSERT INTO role_permissions (role_name, permission_name) VALUES
  ('OWNER', 'WORKSPACE_READ'),
  ('OWNER', 'WORKSPACE_WRITE'),
  ('OWNER', 'PROJECT_READ'),
  ('OWNER', 'PROJECT_WRITE'),
  ('OWNER', 'API_KEY_READ'),
  ('OWNER', 'API_KEY_WRITE'),
  ('OWNER', 'INVITATION_READ'),
  ('OWNER', 'INVITATION_WRITE'),
  ('ADMIN', 'WORKSPACE_READ'),
  ('ADMIN', 'WORKSPACE_WRITE'),
  ('ADMIN', 'PROJECT_READ'),
  ('ADMIN', 'PROJECT_WRITE'),
  ('ADMIN', 'API_KEY_READ'),
  ('ADMIN', 'API_KEY_WRITE'),
  ('ADMIN', 'INVITATION_READ'),
  ('ADMIN', 'INVITATION_WRITE'),
  ('MEMBER', 'WORKSPACE_READ'),
  ('MEMBER', 'PROJECT_READ'),
  ('MEMBER', 'INVITATION_READ'),
  ('API_KEY', 'PROJECT_READ'),
  ('API_KEY', 'PROJECT_WRITE');
