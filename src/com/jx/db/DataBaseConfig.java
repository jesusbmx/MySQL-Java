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
}
