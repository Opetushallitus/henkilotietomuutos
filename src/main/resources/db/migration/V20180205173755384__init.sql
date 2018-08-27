create table public.henkilo_muutostieto_rivi (
	id int8 not null,
	version int8 not null,
	process_timestamp timestamp,
	query_hetu varchar(255),
	rivi int4,
	tiedosto_id int8,
	primary key (id)
);

create sequence hibernate_sequence start 1 increment 1;

create table tiedosto (
	id int8 not null,
	version int8 not null,
	tiedosto_nimi varchar(255),
	part_count int4 not null,
	primary key (id)
);

create table Tietoryhma (
	tietoryhma_type varchar(31) not null,
	id int8 not null,
	version int8 not null,
	muutostapa varchar(255),
	end_date date,
	hetu varchar(255),
	start_date date,
	name varchar(255),
	non_standard_characters boolean,
	type varchar(255),
	additional_information varchar(255),
	language_code varchar(255),
	code varchar(255),
	move_date date,
	country_code varchar(255),
	gender int4,
	issue_date date,
	passivointi_date date,
	passivointi_date_vtj date,
	save_date_vtj date,
	tietolahde varchar(255),
	ulkomainen_henkilonumero_id varchar(255),
	valid boolean,
	valid_vtj boolean,
	huollonjako int4,
	laji varchar(255),
	resolution_date date,
	voimassa boolean,
	postinumero varchar(255),
	postiosoite varchar(255),
	postiosoite_sv varchar(255),
	email varchar(255),
	lajikoodi varchar(255),
	first_names varchar(255),
	last_name varchar(255),
	last_update_date date,
	has_non_standard_characters boolean,
	name_type varchar(255),
	date_of_birth date,
	nationality varchar(255),
	municipality varchar(255),
	street_address varchar(255),
	kuntakoodi varchar(255),
	description varchar(255),
	duties_started boolean,
	edunvalvojat int8,
	edunvalvontatieto varchar(255),
	edunvalvoja_valtuutetut int8,
	huonenumero varchar(255),
	jakokirjain varchar(255),
	katunumero varchar(255),
	lahiosoite varchar(255),
	lahiosoite_sv varchar(255),
	porraskirjain varchar(255),
	active boolean,
	date_of_death date,
	location varchar(255),
	municipality_code varchar(255),
	oikeusaputoimisto_koodi varchar(255),
	y_tunnus varchar(255),
	tietoryhma_id int8,
	primary key (id)
);

alter table tiedosto
	add constraint UK_svwnt9irsjmsvp7tochcf0lio unique (tiedosto_nimi);

alter table public.henkilo_muutostieto_rivi
	add constraint FK26d9mq26ppbdrqg9c2cfgie7b foreign key (tiedosto_id)references tiedosto;

alter table Tietoryhma
	add constraint FKf4gehuq8txmut04m49yjbw227 foreign key (tietoryhma_id)references public.henkilo_muutostieto_rivi;

