# --- !Ups
create table usuario (
  id                            bigserial not null,
  nome                          varchar(255),
  constraint pk_usuario primary key (id)
);

# --- !Downs

drop table if exists produto cascade;


