create table if not exists vtj_data_event (
	id int8 not null,
	version int8 not null,
	hetu varchar(255),
	vtjdata_timestamp timestamp,
	type varchar(255),
	primary key (id)
);

create index if not exists vtj_data_timestamp_idx on vtj_data_event(vtjdata_timestamp) where vtjdata_timestamp is null;

