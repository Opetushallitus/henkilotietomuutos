alter table vtj_data_event rename to vtj_data_event_backup;
drop index if exists vtj_data_timestamp_idx;
drop index if exists vtj_data_hetu_idx;

create table if not exists vtj_data_event (
	id int8 not null,
	version int8 not null,
	hetu varchar(255),
	vtjdata_timestamp timestamp,
	type varchar(255),
	primary key (id)
);
insert into vtj_data_event(hetu, id, version, vtjdata_timestamp, type)
select distinct on (hetu) hetu, id, version, vtjdata_timestamp, type from vtj_data_event_backup order by hetu, id;

create index if not exists vtj_data_timestamp_idx on vtj_data_event(vtjdata_timestamp) where vtjdata_timestamp is null;
create index if not exists vtj_data_hetu_idx on vtj_data_event(hetu);
