package com.jx.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;

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
          int idProducto = rs.getInt("idProducto");
          String codigo = rs.getString("codigo");
          String nombre = rs.getString("nombre");

          System.out.format("%1$-10s %2$-20s %3$20s\n",
                  idProducto, codigo, nombre);
        }
      }
    }
  }

  public void insert() throws SQLException {
    try (DataBase db = DataBaseConfig.getDataBaseMySQL()) {
      LinkedHashMap<String, Object> values = new LinkedHashMap<>();
      values.put("codigo", "PC-89273912");
      values.put("nombre", "PC");
      int idProducto = db.insert("producto", values);
    }
  }
  
  public void upadte(int idProducto) throws SQLException {
    try (DataBase db = DataBaseConfig.getDataBaseMySQL()) {
      LinkedHashMap<String, Object> values = new LinkedHashMap<>();
      values.put("codigo", "PC-89273912");
      values.put("nombre", "PC");
      db.update("producto", values, 
              "idProducto = ?", idProducto);
    }
  }

  public static void main(String... args) throws SQLException {
    Demo demo = new Demo();
  }

}
