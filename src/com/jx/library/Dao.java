
package com.jx.library;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase que administra el origen de la informacion.
 * 
 * @author jesus
 * 
 * @param <Model> modelo o recurso
 * @param <Id> Tipo de id del modelo
 */
public abstract class Dao<Model, Id> {
  
  /**
   * Busca un registro por su identificador
   * 
   * @param id identificador
   * 
   * @return un modelo
   * 
   * @throws SQLException 
   */
  public Model findById(Id id) throws SQLException {
    String sql = "SELECT * FROM " + getTableName() + " WHERE " + whereClause(id);
    return findById(sql, id);
  }
  
  /**
   * Busca un registro.
   * 
   * @param sql query para buscar el registro
   * @param params [opcional] parametros del query
   * 
   * @return un modelo
   * 
   * @throws SQLException 
   */
  public Model findById(String sql, Object... params) throws SQLException {
    DataBase db = getDataBase();
    ResultSet rs = null;
    try {   
      rs = db.query(sql, params);
      return rs.next() ? onRead(rs) : null;
    } finally {
      db.close(rs);
    }
  }
  
  /**
   * Obtiene todos los registros
   * 
   * @return una lista de registros
   * 
   * @throws SQLException 
   */
  public List<Model> find() throws SQLException {
    return find("SELECT * FROM " + getTableName());
  }
  
  /**
   * Obtiene una lista de registros
   *
   * @param sql query para buscar los registros
   * @param params [opcional] parametros del query
   *
   * @return una lista de registros
   * 
   * @throws SQLException 
   */
  public List<Model> find(String sql, Object... params) throws SQLException {
    DataBase db = getDataBase();
    ResultSet rs = null;
    int len = 0;
    try {   
      rs = db.query(sql, params);
      
      if (rs.last()) len = rs.getRow();
      rs.beforeFirst();
      
      List<Model> list = new ArrayList<Model>(len);
      while (rs.next()) {
        list.add(onRead(rs));
      }
      return list;
      
    } finally {
      db.close(rs);
    }
  }
  
  /**
   * @return numero de registros
   * 
   * @throws SQLException 
   */
  public long count() throws SQLException {
    return getDataBase().count(getTableName(), null);
  }
  
  /**
   * Valida si ya existe un registro por medio del id.
   * 
   * @param id del modelo
   * 
   * @return numero de registros
   * 
   * @throws SQLException 
   */
  public boolean exists(Id id) throws SQLException {
    return getDataBase().count(getTableName(), whereClause(id), id) > 0;
  }
  
  /**
   * Guarda el registro
   * 
   * @param m modelo a guardar
   * 
   * @return @true si se guardaron los datos
   * 
   * @throws SQLException 
   */
  public boolean save(Model m) throws SQLException {
    return isUpdate(m) ? update(m) : insert(m);
  }
  
  /**
   * Modifica el registro
   * 
   * @param m modelo a modificar
   * 
   * @return @true si se guardaron los datos
   * 
   * @throws SQLException 
   */
  public boolean update(Model m) throws SQLException {
    Map<String, Object> values = onUpdate(m);
    if (values.isEmpty()) {
      return Boolean.FALSE;
    }
    Id id = getId(m);
    return getDataBase().update(getTableName(), values, 
            whereClause(id), id) == 1;
  }
 
  /**
   * Inserta el registro
   * 
   * @param m modelo a insertar
   * 
   * @return @true si se guardaron los datos
   * 
   * @throws SQLException 
   */
  public boolean insert(Model m) throws SQLException {
    Map<String, Object> values = onInsert(m);
    if (values.isEmpty()) {
      return Boolean.FALSE;
    }
    long id = getDataBase().insert(getTableName(), values);
    insertId(m, id);
    return id != -1;
  }
  
  /**
   * Elimina el registro
   * 
   * @param m modelo a eliminar
   * 
   * @return @true si se eliminaron los datos
   * 
   * @throws SQLException 
   */
  public boolean delete(Model m) throws SQLException {
    return deleteById(getId(m));
  }
  
  /**
   * Elimina un registro por su identificador
   * 
   * @param id identificador
   * 
   * @return @true si se eliminaron los datos
   * 
   * @throws SQLException 
   */
  public boolean deleteById(Id id) throws SQLException {
    return getDataBase().delete(getTableName(), 
            whereClause(id), id) == 1;
  }
  
  /**
   * Datos que se van a modificar
   * 
   * @param m modelo 
   * 
   * @return mapa que contiene los valores de columna iniciales para la fila.
   *      Las claves deben ser los nombres de las columnas 
   *      y los valores valores de la columna
   */
  protected Map<String, Object> onUpdate(Model m) {
    return onWrite(m);
  }
  
  /**
   * Datos que se van a insertar
   * 
   * @param m modelo 
   * 
   * @return mapa que contiene los valores de columna iniciales para la fila.
   *      Las claves deben ser los nombres de las columnas 
   *      y los valores valores de la columna
   */
  protected Map<String, Object> onInsert(Model m) {
    return onWrite(m);
  }
  
  /**
   * Al guardar un registro en la base de datos.
   * 
   * @param m modelo donde estan los datos a guardar
   * 
   * @return mapa que contiene los valores de las columnas para la fila.
   *      Las claves deben ser los nombres de las columnas 
   *      y los valores valores de la columna
   */
  protected Map<String, Object> onWrite(Model m) {
    return new HashMap<String, Object>();
  }
  
  /**
   * @return base de datos del Dao
   */
  protected abstract DataBase getDataBase();
  
  /**
   * @return Nombre de la tabla en la base de datos
   */
  protected abstract String getTableName();
  
  /**
   * AL leer un registro de la base de datos.
   * 
   * @param rs resultado obtenido del query
   * 
   * @return modelo mapeado con el resultado obtenido
   * 
   * @throws SQLException 
   */
  protected abstract Model onRead(ResultSet rs) throws SQLException;
  
  /**
   * Condición where.
   * 
   * @param id del modelo
   * 
   * @return cláusula WHERE para aplicar una actualización, eliminación y busqueda de 
   *    un registro.
   */
  protected abstract String whereClause(Id id);
  
  /**
   * Obtiene el id del modelo
   * 
   * @param m modelo
   * 
   * @return id
   */
  protected abstract Id getId(Model m);
  
  /**
   * Si el registro es editable: (true) ? Update : insert.
   * 
   * @param m modelo
   * 
   * @return @true si es editable
   */
  protected abstract boolean isUpdate(Model m) throws SQLException;
  
  /**
   * Al isertar el registro obtiene el id del registro
   * 
   * @param m modelo
   * @param id insertado
   */
  protected abstract void insertId(Model m, long id);

}
