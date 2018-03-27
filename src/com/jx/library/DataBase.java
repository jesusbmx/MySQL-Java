package com.jx.library;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import javax.swing.JOptionPane;

/**
 *
 * @author Jesus
 */
public class DataBase implements AutoCloseable {

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
  private boolean debug;

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
    if (isClosed()) {
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
  public PreparedStatement prepareStatement(String sql, int... i) 
  throws SQLException {
    return getConnection().prepareStatement(sql, i);
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
      if (debug) System.out.println(ps);
      return ps.executeQuery();
    } finally {
      //ps.closeOnCompletion();
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
  
  /**
   * Ejecuta sentencias insert y obtiene el id del registro insertado.
   * 
   * @param sql sentencia insert
   * @param params [opcional] parametros de la sentencia
   * 
   * @return el id del registro
   * 
   * @throws SQLException 
   */
  public long executeInsert(String sql, Object... params) throws SQLException {
    PreparedStatement ps = null;
    try {
      ps = prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      for (int i = 0; i < params.length; i++) {
        ps.setObject(i + 1, params[i]);
      }
      if (debug) System.out.println(ps);
      if (ps.executeUpdate() == 1) {
        ResultSet rs = null;
        try {
          //obtengo las ultimas llaves generadas
          rs = ps.getGeneratedKeys();
          return rs.next() ? rs.getLong(1) : 0;
        } finally {
          close(rs);
        }
      } else {
        return -1;
      }
    } finally {
      close(ps);
    }
  }
  
//  public List<Map<String, Object>> resultSetToList(ResultSet rs) throws SQLException {
//    ResultSetMetaData md = rs.getMetaData();
//    
//    int columns = md.getColumnCount();
//    int rows = 0;
//    if (rs.last()) rows = rs.getRow();
//    rs.beforeFirst();
//    
//    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(rows);
//    while (rs.next()) {
//      Map<String, Object> row = new HashMap<String, Object>(columns);
//      for (int i = 1; i <= columns; ++i) {
//        row.put(md.getColumnName(i), rs.getObject(i));
//      }
//      list.add(row);
//    }
//
//    return list;
//  }

  public int executeUpdate(String sql, Object... params) throws SQLException {
    PreparedStatement ps = null;
    try {
      ps = prepareStatement(sql);
      for (int i = 0; i < params.length; i++) {
        ps.setObject(i + 1, params[i]);
      }
      if (debug) System.out.println(ps);
      return ps.executeUpdate();
    } finally {
      close(ps);
    }
  }

  /**
   * Valida si un registro existe.
   *
   * @param tabla donde se buscaran las existencias
   * @param whereClause condicion
   * @param whereArgs [opcional] parametros del whereClause
   *
   * @return numero de existencia
   *
   * @throws SQLException
   */
  public long count(String tabla, String whereClause, Object... whereArgs) 
  throws SQLException {
    String sql = "SELECT COUNT(*) AS COUNT FROM " + tabla;
    if (whereClause != null && !whereClause.isEmpty()) {
      sql += " WHERE " + whereClause;
    }
    ResultSet rs = null;
    try {
      rs = query(sql, whereArgs);
      return rs.next() ? rs.getLong("COUNT") : -1;
    } finally {
      close(rs);
    }
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
  public long insert(String tabla, Map<String, Object> datos) throws SQLException {
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
   * @param whereArgs [opcional] parametros del whereClause
   *
   * @return int id del registro
   *
   * @throws SQLException
   */
  public int update(String tabla, Map<String, Object> datos, String whereClause, Object... whereArgs)
  throws SQLException {
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
  
  /**
   * Elimina un registro de la base de datos.
   * 
   * @param tabla donde se eliminara
   * @param whereClause condicion
   * @param whereArgs [opcional] parametros del whereClause
   * 
   * @return
   * 
   * @throws SQLException 
   */
  public int delete(String tabla, String whereClause, Object... whereArgs) 
  throws SQLException {
    String sql = "DELETE FROM " + tabla;
    if (whereClause != null && !whereClause.isEmpty()) {
      sql += " WHERE " + whereClause;
    }
    return executeUpdate(sql, whereArgs);
  }

  /**
   * @return @true si la base de datos esta cerrada.
   *
   * @throws SQLException
   */
  public boolean isClosed() throws SQLException {
    return con == null || con.isClosed();
  }

  @Override
  public void close() {
    close(con);
  }
  
  public void close(AutoCloseable ac) {
    try {
      if (ac != null) {
        ac.close();
      }
    } catch (Exception e) {
      // TODO Auto-generated catch block
    }
  }
  
  public boolean log(SQLException e) {
    String msg = e.getMessage();
    if (e.getErrorCode() == 1049) {
      msg = "La base de datos: " + url + " no existe.";
    } else if (e.getErrorCode() == 1044) {
      msg = "El usuario: " + username + " no existe.";
    } else if (e.getErrorCode() == 1045) {
      msg = "Contraseña incorrecta.";
    } else if (e.getErrorCode() == 0) {
      msg = "La conexión con la base de datos no se puede realizar.\n Parece que el servidor de base de datos no esta activo.";
    }
    displayError(msg);
    return Boolean.FALSE;
  }
  
   private void displayError(final String msg) {
    java.awt.EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
      }
    });
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

  public boolean isDebug() {
    return debug;
  }

  public void setDebug(boolean debug) {
    this.debug = debug;
  }
}
