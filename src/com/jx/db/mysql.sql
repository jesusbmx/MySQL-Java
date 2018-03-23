
create database if not exists punto_venta;
use punto_venta;

create table producto(
  id int not null auto_increment,
  codigo varchar(25),
  nombre varchar(255),
  primary key(id)
);