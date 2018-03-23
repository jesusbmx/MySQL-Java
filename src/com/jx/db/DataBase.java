package com.jx.db;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

/**
 *
 * @author Jesus
 */
public class DataBase implements Closeable {

// Variables
  
  /**
   * Driver de coneccion. 
   * MySQL      : com.mysql.jdbc.Driver 
   * Oracle     : oracle.jdbc.driver.OracleDriver 
   * PostgreSQL : org.postgresql.Driver
   * SQLServer  : com.microsoft.sqlserver.jdbc.SQLServerDriver
   */
  private String driverClassName = "com.mysql.jdbc.Driver";

  private String url;
  private String username;
  private String password;

  private Connection con;

// Costructor
  
  public DataBase() {
  }

// Funciones  
  
  /**
   * Establece la coneccion con la base de datos.
   *
   * @return la coneccion
   *
   * @throws SQLException
   */
  public Connection getConnection() throws SQLException {
    if (con == null || con.isClosed()) {
      try {
        Class.forName(driverClassName);
      } catch (ClassNotFoundException ex) {
        throw new SQLException(ex);
      }
      con = DriverManager.getConnection(url, username, password);
    }
    return con;
  }

  /**
   * Prepara sentencias sql.
   *
   * @param sql instruccion a preparar
   * @param i [opcional] flags de configuracion
   *
   * @return PreparedStatement setencia preparada
   *
   * @throws SQLException
   */
  public PreparedStatement prepareStatement(String sql, int... i) throws SQLException {
    return getConnection().prepareStatement(sql, i);
  }

  /**
   * Inserta un registro en la base de datos.
   *
   * @param tabla donde se va a insertar el registro
   * @param datos a guardar
   *
   * @return int id isertado
   *
   * @throws SQLException
   */
  public int insert(String tabla, Map<String, Object> datos) throws SQLException {
    StringBuilder sql = new StringBuilder();
    sql.append("INSERT INTO ");
    sql.append(tabla);
    sql.append('(');

    int size = (datos != null && datos.size() > 0) ? datos.size() : 0;
    Object[] bindArgs = new Object[size];
    int i = 0;
    for (String colName : datos.keySet()) {
      sql.append((i > 0) ? "," : "");
      sql.append(colName);
      bindArgs[i++] = datos.get(colName);
    }

    sql.append(')');
    sql.append(" VALUES (");
    for (i = 0; i < size; i++) {
      sql.append((i > 0) ? ",?" : "?");
    }
    sql.append(')');

    return executeInsert(sql.toString(), bindArgs);
  }

  /**
   * Actualiza un registro en la base de datos.
   *
   * @param tabla donde se va a actualizar el registro
   * @param datos a guardar
   * @param whereClause condicion
   * @param whereArgs parametros del whereClause
   *
   * @return int id del registro
   *
   * @throws SQLException
   */
  public int update(String tabla, Map<String, Object> datos, String whereClause, Object... whereArgs) throws SQLException {
    StringBuilder sql = new StringBuilder();
    sql.append("UPDATE ");
    sql.append(tabla);
    sql.append(" SET ");

    int setValuesSize = datos.size();
    int bindArgsSize = (whereArgs == null) ? setValuesSize : (setValuesSize + whereArgs.length);
    Object[] bindArgs = new Object[bindArgsSize];
    int i = 0;
    for (String colName : datos.keySet()) {
      sql.append((i > 0) ? "," : "");
      sql.append(colName);
      bindArgs[i++] = datos.get(colName);
      sql.append("=?");
    }

    if (whereArgs != null) {
      for (i = setValuesSize; i < bindArgsSize; i++) {
        bindArgs[i] = whereArgs[i - setValuesSize];
      }
    }
    if (whereClause != null && !whereClause.isEmpty()) {
      sql.append(" WHERE ");
      sql.append(whereClause);
    }

    return executeUpdate(sql.toString(), bindArgs);
  }
  
  public int delete(String tabla, String whereClause, Object... whereArgs) throws SQLException {
    String sql = "DELETE FROM " + tabla + " WHERE " + whereClause;
    return executeUpdate(sql, whereArgs);
  }
  
  /**
   * Ejecuta consultas a la base de datos.
   *
   * @param sql query a ejecutar
   * @param params [opcional] parametros del query
   *
   * @return ResultSet con el resultado obtenido
   *
   * @throws SQLException
   */
  public ResultSet query(String sql, Object... params) throws SQLException {
    PreparedStatement ps = prepareStatement(sql);
    try {
      for (int i = 0; i < params.length; i++) {
        ps.setObject(i + 1, params[i]);
      }
      return ps.executeQuery();
    } finally {
      //ps.closeOnCompletion();
    }
  }

  public int executeInsert(String sql, Object... params) throws SQLException {
    try (PreparedStatement ps = prepareStatement(sql,
            Statement.RETURN_GENERATED_KEYS)) {
      for (int i = 0; i < params.length; i++) {
        ps.setObject(i + 1, params[i]);
      }
      if (ps.executeUpdate() == 1) {
        try (ResultSet rs = ps.getGeneratedKeys()) { //obtengo las ultimas llaves generadas
          if (rs.next()) {
            return rs.getInt(1);
          }
        }
      }
      return -1;
    }
  }

  public int executeUpdate(String sql, Object... params) throws SQLException {
    try (PreparedStatement ps = prepareStatement(sql)) {
      for (int i = 0; i < params.length; i++) {
        ps.setObject(i + 1, params[i]);
      }
      return ps.executeUpdate();
    }
  }

  /**
   * Ejecuta sentencias a la base de datos.
   *
   * @param sql sentencia a ejecutar
   * @param params [opcional] parametros del query
   *
   * @return @true resultado obtenido
   *
   * @throws SQLException
   */
  public boolean execute(String sql, Object... params) throws SQLException {
    return executeUpdate(sql, params) == 1;
  }

  @Override
  public void close() {
    try {
      if (con != null) {
        con.close();
      }
    } catch (SQLException ex) {
    }
  }

  public String getDriverClassName() {
    return driverClassName;
  }

  public void setDriverClassName(String driverClassName) {
    this.driverClassName = driverClassName;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
