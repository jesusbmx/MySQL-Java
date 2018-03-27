package com.jx.model;

import java.util.LinkedHashMap;

/**
 *
 * @author Jesus
 */
public class Producto {
  
  private long id;
  private String codigo;
  private String nombre;

  public Producto() {
    this(0, "", "");
  }

  public Producto(long id, String codigo, String nombre) {
    this.id = id;
    this.codigo = codigo;
    this.nombre = nombre;
  }

  public long getId() {
    return id;
  }
  public void setId(long id) {
    this.id = id;
  }
  public String getCodigo() {
    return codigo;
  }
  public void setCodigo(String codigo) {
    this.codigo = codigo;
  }
  public String getNombre() {
    return nombre;
  }
  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  @Override
  public String toString() {
    LinkedHashMap<String, Object> values = new LinkedHashMap<String, Object>();
    values.put("id", id);
    values.put("codigo", codigo);
    values.put("nombre", nombre);
    return values.toString();
  }
  
  
}
