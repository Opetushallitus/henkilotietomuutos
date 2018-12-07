create index concurrently tietoryhma_hetu_tietoryhmaid_idx on tietoryhma (hetu, tietoryhma_id)
where tietoryhma_type = 'henkilotunnuskorjaus';
