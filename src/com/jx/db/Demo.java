package com.jx.db;

import java.util.LinkedHashMap;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Jesus
 */
public class Demo {

  public void select() throws SQLException {
    try (DataBase db = DataBaseConfig.getDataBaseMySQL()) {
      String sql = "SELECT * FROM producto";

      try (ResultSet rs = db.query(sql)) {
        while (rs.next()) {
          int idProducto = rs.getInt("id");
          String codigo = rs.getString("codigo");
          String nombre = rs.getString("nombre");

          System.out.format("%1$-10s %2$-20s %3$20s\n",
                  idProducto, codigo, nombre);
        }
      }
    }
  }

  public int insert(Producto p) throws SQLException {
    try (DataBase db = DataBaseConfig.getDataBaseMySQL()) {
      LinkedHashMap<String, Object> values = new LinkedHashMap<>();
      values.put("codigo", p.getCodigo());
      values.put("nombre", p.getNombre());
      
      int id_insertado = db.insert("producto", values);
      p.setId(id_insertado);
      return id_insertado;
    }
  }
  
  public int update(Producto p) throws SQLException {
    try (DataBase db = DataBaseConfig.getDataBaseMySQL()) {
      LinkedHashMap<String, Object> values = new LinkedHashMap<>();
      values.put("codigo", p.getCodigo());
      values.put("nombre", p.getNombre());
      
      return db.update("producto", values, "id = ?", p.getId());
    }
  }
  
  public int delete(int id) throws SQLException {
    try (DataBase db = DataBaseConfig.getDataBaseMySQL()) {
      return db.delete("producto", "id = ?", id);
    }
  }

  public static void main(String... args) throws SQLException {
    Demo demo = new Demo();
    
    Producto p = new Producto();
    p.setCodigo("PC-" + System.currentTimeMillis());
    p.setNombre("PC");
    
    demo.insert(p);
    System.out.println("insert:" + p);
    
    
    p.setCodigo("PC-" + System.currentTimeMillis());
    
    demo.update(p);
    System.out.println("update:" + p);
    
    demo.delete(4);
    
    demo.select();
  }

}
