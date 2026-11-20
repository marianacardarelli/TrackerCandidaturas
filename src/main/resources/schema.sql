create table if not exists Empresa
(
    id             int auto_increment primary key,
    nombre         varchar(100) not null unique,
    sector         varchar(100) null,
    web            varchar(255) null,
    fecha_registro date         not null default (current_date)
    );

create table if not exists Oferta
(
    id                int auto_increment primary key,
    id_empresa        int          not null,
    titulo            varchar(150) not null,
    descripcion       text         null,
    estado            enum ('pendiente', 'entrevista', 'rechazado', 'aceptado') not null default 'pendiente',
    fecha_postulacion date         null,
    enlace            varchar(500) null,
    salario           decimal(10, 2) null,
    notas             text         null,
    constraint fk_oferta_empresa foreign key (id_empresa) references Empresa (id) on delete restrict,
    index idx_oferta_estado (estado),
    index idx_oferta_fecha (fecha_postulacion)
    );

create table if not exists Entrevista
(
    id         int auto_increment primary key,
    id_oferta  int  not null,
    fecha      date not null,
    tipo       enum ('telefonica', 'tecnica', 'presencial', 'online') not null,
    notas      text null,
    resultado  enum ('pendiente', 'positivo', 'negativo') not null default 'pendiente',
    constraint fk_entrevista_oferta foreign key (id_oferta) references Oferta (id) on delete cascade
    );