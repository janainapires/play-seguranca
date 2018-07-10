# --- !Ups

create table produto (
  id                            bigserial not null,
  titulo                        varchar(255),
  codigo                        varchar(255),
  tipo                          varchar(255),
  descricao                     varchar(255),
  preco                         float,
  constraint pk_produto primary key (id)
);
# --- !Downs

drop table if exists produto cascade;


