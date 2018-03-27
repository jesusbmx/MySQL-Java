package com.jx.config;

import com.jx.library.DataBase;

/**
 *
 * @author Jesus
 */
public final class DataBaseConfig {

  private static DataBase mysql;
  private static DataBase sqlserver;

  private DataBaseConfig() {
  }

  public static DataBase getDataBaseMySQL() {
    if (mysql == null) {
      mysql = new DataBase();
      // Ejemplo con base de datos MySQL
      mysql.setDriverClassName("com.mysql.jdbc.Driver");
      mysql.setUrl("jdbc:mysql://localhost:3306/punto_venta");
      mysql.setUsername(/*"usuario"*/"root");
      mysql.setPassword(/*"password"*/"");
      mysql.setDebug(Boolean.TRUE);
    }
    return mysql;
  }
  
  public static DataBase getDataBaseSQLServer() {
    if (sqlserver == null) {
      sqlserver = new DataBase();
      // Ejemplo con base de datos SqlServer
      sqlserver.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
      sqlserver.setUrl("jdbc:sqlserver://localhost;databaseName=dataBase;");
      sqlserver.setUsername(/*"usuario"*/"sa");
      sqlserver.setPassword(/*"password"*/"123456");
    }
    return sqlserver;
  }
}
