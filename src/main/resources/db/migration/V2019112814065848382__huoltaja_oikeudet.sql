create table huoltaja_oikeudet (
	Huoltaja_id int8 not null,
	oikeudet_id int8 not null,
	primary key (Huoltaja_id,oikeudet_id)
);

alter table tietoryhma add column koodi varchar(255);

alter table if
	exists huoltaja_oikeudet add constraint huoltaja_oikeudet_oikeudet_id_uk unique (oikeudet_id);

alter table if
	exists huoltaja_oikeudet add constraint huoltaja_oikeudet_oikeudet_id_fk foreign key (oikeudet_id)references Tietoryhma;

alter table if
	exists huoltaja_oikeudet add constraint huoltaja_oikeudet_huoltaja_id_fk foreign key (Huoltaja_id)references Tietoryhma;
