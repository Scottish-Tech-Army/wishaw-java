-- ══════════════════════════════════════════════════
-- Seed data for Wishaw YMCA Badge Portal
-- ══════════════════════════════════════════════════

-- ── 1. Badges (5 core categories) ────────────────
INSERT INTO badge (name, description) VALUES
    ('Game Mastery', 'Demonstrates skill and understanding of game mechanics'),
    ('Teamwork', 'Works effectively with others in team-based activities'),
    ('Esports Citizen', 'Shows positive online behaviour and sportsmanship'),
    ('Personal Development', 'Develops confidence, communication and leadership skills'),
    ('Digital Skills', 'Builds technical and digital literacy skills');

-- ── 2. Levels (default thresholds) ───────────────
INSERT INTO level (name, min_points, max_points, display_order) VALUES
    ('BRONZE', 0, 30, 1),
    ('SILVER', 31, 70, 2),
    ('GOLD', 71, 120, 3),
    ('PLATINUM', 121, -1, 4);

-- ── 3. Centres ───────────────────────────────────
INSERT INTO centre (name, code) VALUES ('Wishaw YMCA', 'WISHAW');
INSERT INTO centre (name, code) VALUES ('Edinburgh YMCA', 'EDINBURGH');
INSERT INTO centre (name, code) VALUES ('Glasgow YMCA', 'GLASGOW');

-- ── 4. Users ─────────────────────────────────────
-- All passwords are BCrypt-encoded "password123"
-- Generated via: new BCryptPasswordEncoder().encode("password123")
INSERT INTO users (username, password, display_name, dob, role, centre_id) VALUES
  ('admin',            '$2a$10$7ZVt0sq77uhUa.MaT4Bxhu9CdkAhzv/oQJy0ASyG7C7QMhqto/A1W', 'Main Admin',              '1985-03-15', 'MAIN_ADMIN',    1),
  ('cadmin_wishaw',    '$2a$10$7ZVt0sq77uhUa.MaT4Bxhu9CdkAhzv/oQJy0ASyG7C7QMhqto/A1W', 'Wishaw Centre Admin',     '1990-07-22', 'CENTRE_ADMIN',  1),
  ('cadmin_edinburgh', '$2a$10$7ZVt0sq77uhUa.MaT4Bxhu9CdkAhzv/oQJy0ASyG7C7QMhqto/A1W', 'Edinburgh Centre Admin',  '1988-11-05', 'CENTRE_ADMIN',  2),
  ('alex_mc',          '$2a$10$7ZVt0sq77uhUa.MaT4Bxhu9CdkAhzv/oQJy0ASyG7C7QMhqto/A1W', 'Alex McGregor',           '2008-01-10', 'USER',          1),
  ('jamie_w',          '$2a$10$7ZVt0sq77uhUa.MaT4Bxhu9CdkAhzv/oQJy0ASyG7C7QMhqto/A1W', 'Jamie Wilson',            '2009-04-18', 'USER',          1),
  ('sophie_r',         '$2a$10$7ZVt0sq77uhUa.MaT4Bxhu9CdkAhzv/oQJy0ASyG7C7QMhqto/A1W', 'Sophie Robertson',        '2007-09-30', 'USER',          1),
  ('callum_b',         '$2a$10$7ZVt0sq77uhUa.MaT4Bxhu9CdkAhzv/oQJy0ASyG7C7QMhqto/A1W', 'Callum Brown',            '2010-06-12', 'USER',          2),
  ('isla_m',           '$2a$10$7ZVt0sq77uhUa.MaT4Bxhu9CdkAhzv/oQJy0ASyG7C7QMhqto/A1W', 'Isla Murray',             '2008-12-25', 'USER',          2),
  ('lewis_k',          '$2a$10$7ZVt0sq77uhUa.MaT4Bxhu9CdkAhzv/oQJy0ASyG7C7QMhqto/A1W', 'Lewis Kelly',             '2009-08-03', 'USER',          3),
  ('emma_s',           '$2a$10$7ZVt0sq77uhUa.MaT4Bxhu9CdkAhzv/oQJy0ASyG7C7QMhqto/A1W', 'Emma Stewart',            '2010-02-14', 'USER',          3),
  ('ryan_t',           '$2a$10$7ZVt0sq77uhUa.MaT4Bxhu9CdkAhzv/oQJy0ASyG7C7QMhqto/A1W', 'Ryan Thomson',            '2007-05-20', 'USER',          1);

-- ── 5. Modules ───────────────────────────────────
INSERT INTO module (name, description, approved, centre_id) VALUES
  ('Minecraft Basics',       '12-week intro to Minecraft teamwork and building',     TRUE,  1),
  ('Rocket League Season 1', '16-week competitive Rocket League programme',          TRUE,  1),
  ('Fortnite Creative',      '14-week Fortnite creative and strategy module',        TRUE,  1),
  ('Minecraft Advanced',     'Advanced redstone and survival module',                TRUE,  2),
  ('Rocket League Rookies',  'Beginner Rocket League programme',                     FALSE, 2),
  ('Minecraft PvP',          'PvP-focused Minecraft module',                         TRUE,  3);

-- ── 6. Sub-badges / Challenges ───────────────────
-- Badges: 1=Game Mastery, 2=Teamwork, 3=Esports Citizen, 4=Personal Dev, 5=Digital Skills
-- Modules: 1=MC Basics, 2=RL S1, 3=Fortnite, 4=MC Advanced, 5=RL Rookies, 6=MC PvP

-- Minecraft Basics (module 1)
INSERT INTO sub_badge (name, points, badge_id, module_id) VALUES
  ('Build a Shelter',        10, 1, 1),
  ('Craft Diamond Tools',    15, 1, 1),
  ('Team Build Challenge',   10, 2, 1),
  ('Respectful Chat',         5, 3, 1),
  ('Lead a Build Session',   10, 4, 1),
  ('Screenshot and Share',    5, 5, 1),
  ('Redstone Circuit',       15, 5, 1),
  ('Survive 10 Nights',      10, 1, 1);

-- Rocket League Season 1 (module 2)
INSERT INTO sub_badge (name, points, badge_id, module_id) VALUES
  ('Aerial Goal',            15, 1, 2),
  ('Assist Hat-trick',       10, 2, 2),
  ('Good Sportsmanship',      5, 3, 2),
  ('Analyse Replay',         10, 5, 2),
  ('Team Captain',           10, 4, 2),
  ('Win a Tournament Match', 20, 1, 2);

-- Fortnite Creative (module 3)
INSERT INTO sub_badge (name, points, badge_id, module_id) VALUES
  ('Design a Map',           15, 5, 3),
  ('Squad Victory Royale',   15, 2, 3),
  ('Creative Mode Showcase', 10, 4, 3),
  ('Fair Play Award',         5, 3, 3);

-- Minecraft Advanced (module 4)
INSERT INTO sub_badge (name, points, badge_id, module_id) VALUES
  ('Redstone Calculator',    20, 5, 4),
  ('Nether Fortress Run',    15, 1, 4),
  ('Team Survival',          10, 2, 4);

-- Minecraft PvP (module 6)
INSERT INTO sub_badge (name, points, badge_id, module_id) VALUES
  ('PvP Tournament Win',     20, 1, 6),
  ('Duo Combo Attack',       10, 2, 6),
  ('GG Every Match',          5, 3, 6);

-- ── 7. Groups ────────────────────────────────────
INSERT INTO game_group (name, centre_id) VALUES
  ('Wishaw Minecraft Crew',  1),
  ('Wishaw Rocket League',   1),
  ('Wishaw Fortnite Squad',  1),
  ('Edinburgh Miners',       2),
  ('Glasgow PvP Warriors',   3);

-- ── 8. Group Members ─────────────────────────────
-- Users: 1=admin, 2=cadmin_wishaw, 3=cadmin_edinburgh, 4=alex, 5=jamie, 6=sophie, 7=callum, 8=isla, 9=lewis, 10=emma, 11=ryan
INSERT INTO group_member (game_group_id, user_id) VALUES
  (1, 4), (1, 5), (1, 6), (1, 11),   -- Wishaw Minecraft: alex, jamie, sophie, ryan
  (2, 4), (2, 5),                      -- Wishaw RL: alex, jamie
  (3, 6), (3, 11),                     -- Wishaw Fortnite: sophie, ryan
  (4, 7), (4, 8),                      -- Edinburgh Miners: callum, isla
  (5, 9), (5, 10);                     -- Glasgow PvP: lewis, emma

-- ── 9. Sub-badge Completions ─────────────────────
-- Sub-badges: 1-8=MC Basics, 9-14=RL S1, 15-18=Fortnite, 19-21=MC Adv, 22-24=MC PvP

-- Alex (user 4) — avid Minecraft & Rocket League
INSERT INTO sub_badge_completion (user_id, sub_badge_id) VALUES
  (4, 1), (4, 2), (4, 3), (4, 4), (4, 5), (4, 6), (4, 7), (4, 9), (4, 10), (4, 14);

-- Jamie (user 5) — solid all-rounder
INSERT INTO sub_badge_completion (user_id, sub_badge_id) VALUES
  (5, 1), (5, 3), (5, 4), (5, 8), (5, 9), (5, 11);

-- Sophie (user 6) — Fortnite + Minecraft mix
INSERT INTO sub_badge_completion (user_id, sub_badge_id) VALUES
  (6, 1), (6, 15), (6, 16), (6, 17), (6, 18);

-- Callum (user 7) — Edinburgh, Minecraft Advanced
INSERT INTO sub_badge_completion (user_id, sub_badge_id) VALUES
  (7, 19), (7, 20), (7, 21);

-- Isla (user 8) — Edinburgh, getting started
INSERT INTO sub_badge_completion (user_id, sub_badge_id) VALUES
  (8, 19), (8, 21);

-- Lewis (user 9) — Glasgow PvP
INSERT INTO sub_badge_completion (user_id, sub_badge_id) VALUES
  (9, 22), (9, 23), (9, 24);

-- Emma (user 10) — Glasgow, one badge done
INSERT INTO sub_badge_completion (user_id, sub_badge_id) VALUES
  (10, 24);

-- Ryan (user 11) — Wishaw, Fortnite fan + some Minecraft
INSERT INTO sub_badge_completion (user_id, sub_badge_id) VALUES
  (11, 15), (11, 16), (11, 17), (11, 18), (11, 1), (11, 2);

-- ── 10. User Progress (XP per badge) ─────────────
-- Calculated from the sub-badge completions above

-- Alex (user 4): Game Mastery=60, Teamwork=20, Esports Citizen=5, Personal Dev=10, Digital Skills=20
INSERT INTO user_progress (user_id, badge_id, total_points) VALUES
  (4, 1, 60), (4, 2, 20), (4, 3, 5), (4, 4, 10), (4, 5, 20);

-- Jamie (user 5): Game Mastery=35, Teamwork=10, Esports Citizen=10
INSERT INTO user_progress (user_id, badge_id, total_points) VALUES
  (5, 1, 35), (5, 2, 10), (5, 3, 10);

-- Sophie (user 6): Game Mastery=10, Teamwork=15, Esports Citizen=5, Personal Dev=10, Digital Skills=15
INSERT INTO user_progress (user_id, badge_id, total_points) VALUES
  (6, 1, 10), (6, 2, 15), (6, 3, 5), (6, 4, 10), (6, 5, 15);

-- Callum (user 7): Game Mastery=15, Teamwork=10, Digital Skills=20
INSERT INTO user_progress (user_id, badge_id, total_points) VALUES
  (7, 1, 15), (7, 2, 10), (7, 5, 20);

-- Isla (user 8): Teamwork=10, Digital Skills=20
INSERT INTO user_progress (user_id, badge_id, total_points) VALUES
  (8, 2, 10), (8, 5, 20);

-- Lewis (user 9): Game Mastery=20, Teamwork=10, Esports Citizen=5
INSERT INTO user_progress (user_id, badge_id, total_points) VALUES
  (9, 1, 20), (9, 2, 10), (9, 3, 5);

-- Emma (user 10): Esports Citizen=5
INSERT INTO user_progress (user_id, badge_id, total_points) VALUES
  (10, 3, 5);

-- Ryan (user 11): Game Mastery=25, Teamwork=15, Esports Citizen=5, Personal Dev=10, Digital Skills=15
INSERT INTO user_progress (user_id, badge_id, total_points) VALUES
  (11, 1, 25), (11, 2, 15), (11, 3, 5), (11, 4, 10), (11, 5, 15);

-- ── 11. Legacy Points (pre-system historical points per user per badge) ──────
-- These represent points earned before the digital system was introduced.
-- They have no sub-badge breakdown — just a total per badge.

-- Alex (user 4) — had prior Game Mastery and Teamwork recognition
INSERT INTO legacy_points (user_id, badge_id, points, reason) VALUES
  (4, 1, 25, 'Pre-system game tournament achievements'),
  (4, 2, 15, 'Prior teamwork recognition from coaches');

-- Jamie (user 5) — had prior Esports Citizen and Personal Dev points
INSERT INTO legacy_points (user_id, badge_id, points, reason) VALUES
  (5, 3, 20, 'Good sportsmanship awards from previous sessions'),
  (5, 4, 10, 'Leadership in earlier programmes');

-- Sophie (user 6) — had prior Digital Skills points
INSERT INTO legacy_points (user_id, badge_id, points, reason) VALUES
  (6, 5, 30, 'IT skills workshop completions before system launch');

-- Callum (user 7) — had prior Teamwork and Esports Citizen points
INSERT INTO legacy_points (user_id, badge_id, points, reason) VALUES
  (7, 2, 20, 'Team captain in previous seasonal events'),
  (7, 3, 10, 'Positive behaviour recognition');

-- Lewis (user 9) — had prior Game Mastery points
INSERT INTO legacy_points (user_id, badge_id, points, reason) VALUES
  (9, 1, 35, 'Tournament winner before digital tracking');

-- Ryan (user 11) — had prior Personal Development points
INSERT INTO legacy_points (user_id, badge_id, points, reason) VALUES
  (11, 4, 15, 'Mentoring younger players in earlier cohort');
