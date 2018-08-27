create index if not exists tietoryhma_id_idx on tietoryhma(tietoryhma_id);
create index if not exists henkilo_muutostieto_rivi_tiedosto_id_idx on henkilo_muutostieto_rivi(tiedosto_id);

-- Configure autovacuum to run at every 1000 tuple (insert, update, delete) for every big table (10k+ rows)
ALTER TABLE henkilo_muutostieto_rivi SET (autovacuum_vacuum_scale_factor = 0.0);
ALTER TABLE henkilo_muutostieto_rivi SET (autovacuum_vacuum_threshold = 1000);
ALTER TABLE henkilo_muutostieto_rivi SET (autovacuum_analyze_scale_factor = 0.0);
ALTER TABLE henkilo_muutostieto_rivi SET (autovacuum_analyze_threshold = 1000);

ALTER TABLE tietoryhma SET (autovacuum_vacuum_scale_factor = 0.0);
ALTER TABLE tietoryhma SET (autovacuum_vacuum_threshold = 10000);
ALTER TABLE tietoryhma SET (autovacuum_analyze_scale_factor = 0.0);
ALTER TABLE tietoryhma SET (autovacuum_analyze_threshold = 10000);
