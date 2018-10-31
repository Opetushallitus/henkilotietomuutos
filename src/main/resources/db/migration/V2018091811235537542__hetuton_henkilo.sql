alter table tietoryhma
  add column hetuton_henkilo bigint;

update tietoryhma t
set hetuton_henkilo = nextColumn.id
from tietoryhma nextColumn
where nextColumn.tietoryhma_type = 'henkilotunnukseton_henkilo'
  AND nextColumn.id = t.id + 1
  AND (t.tietoryhma_type = 'huoltaja' OR t.tietoryhma_type = 'edunvalvoja' OR t.tietoryhma_type = 'edunvalvonta_valtuutettu')
  AND t.hetu = ''
  AND t.hetuton_henkilo is null;
