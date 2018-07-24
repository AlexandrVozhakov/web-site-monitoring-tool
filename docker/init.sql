CREATE SCHEMA statistics;

CREATE TABLE statistics.web_sites(
  name VARCHAR(32) NOT NULL UNIQUE,
  browser_id INT,
  country_id INT,
  day_of_week_id INT,
  time_of_day_id INT,
  web_site_pages_id INT
);

CREATE TABLE statistics.browsers(
  id SERIAL PRIMARY KEY,
  browser TEXT NOT NULL,
  count INT
);

CREATE TABLE statistics.countries(
  name VARCHAR(12) NOT NULL,
  web_site_id INT NOT NULL,
  quantity INT,
  UNIQUE (name, web_site_id)
);

CREATE TABLE statistics.day_of_weeks(
  name VARCHAR(12) NOT NULL,
  web_site_id INT NOT NULL,
  quantity INT,
  UNIQUE (name, web_site_id)
);

CREATE TABLE statistics.time_of_days(
  name VARCHAR(12) NOT NULL,
  web_site_id INT NOT NULL,
  quantity INT,
  UNIQUE (name, web_site_id)
);

CREATE TABLE statistics.web_site_pages(
  page VARCHAR(32) NOT NULL,
  web_site_id INT NOT NULL,
  quantity INT,
  UNIQUE (page, web_site_id)
);

CREATE TABLE statistics.urls(
  id SERIAL PRIMARY KEY,
  url TEXT NOT NULL,
  count INT NOT NULL
);