package com.jx.db;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Jesus
 */
public final class DataBaseConfig {

  private static DataBase mysql;

  private DataBaseConfig() {
  }

  public static DataBase getDataBaseMySQL() {
    if (mysql == null) {
      mysql = new DataBase();
      // Ejemplo con base de datos MySQL
      mysql.setDriverClassName("com.mysql.jdbc.Driver");
      mysql.setUrl("jdbc:mysql://localhost:3306/database_name");
      mysql.setUsername("usuario");
      mysql.setPassword("password");
    }
    return mysql;
  }

  public static void main(String... args) throws SQLException {
    DataBase db = DataBaseConfig.getDataBaseMySQL();

    String sql = "SELECT * FROM producto";

    try (ResultSet rs = db.query(sql)) {
      while (rs.next()) {
        int idProducto = rs.getInt("idProducto");
        String codigo = rs.getString("codigo");
        String nombre = rs.getString("nombre");
        
        System.out.format("%1$-10s %2$-20s %3$20s\n", 
                idProducto, codigo, nombre);
      }
    } finally {
      db.close();
    }
  }
}
