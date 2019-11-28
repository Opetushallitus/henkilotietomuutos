alter table tietoryhma drop column laji;
alter table tietoryhma drop column huollonjako;
alter table tietoryhma drop column resolution_date;
alter table tietoryhma drop column voimassa;

alter table tietoryhma add column asuminen varchar(255);
alter table tietoryhma add column asuminen_alkupvm date;
alter table tietoryhma add column asuminen_loppupvm date;
alter table tietoryhma add column laji varchar(255);
alter table tietoryhma add column rooli varchar(255);
