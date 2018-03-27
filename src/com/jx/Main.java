package com.jx;

import com.jx.config.DataBaseConfig;
import com.jx.library.DataBase;
import com.jx.model.Producto;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;

/**
 *
 * @author Jesus
 */
public class Main {

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
  
  public long count() throws SQLException {
    try (DataBase db = DataBaseConfig.getDataBaseMySQL()) {
      return db.count("producto", null);
    }
  }

  public long insert(Producto p) throws SQLException {
    try (DataBase db = DataBaseConfig.getDataBaseMySQL()) {
      LinkedHashMap<String, Object> values = new LinkedHashMap<>();
      values.put("codigo", p.getCodigo());
      values.put("nombre", p.getNombre());
      
      long id_insertado = db.insert("producto", values);
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
  
  public int delete(long id) throws SQLException {
    try (DataBase db = DataBaseConfig.getDataBaseMySQL()) {
      return db.delete("producto", "id = ?", id);
    }
  }

  public static void main(String... args)  {
    Main demo = new Main();

    try {
      System.out.println("count:" + demo.count());
      
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

      System.out.println("count:" + demo.count());
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }
}
