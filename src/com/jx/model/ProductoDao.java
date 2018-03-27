
package com.jx.model;

import com.jx.config.DataBaseConfig;
import com.jx.library.Dao;
import com.jx.library.DataBase;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Clase que administra el origen de la informacion.
 * 
 * @author jesus
 * 
 * @param <Producto> modelo o recurso
 * @param <Long> Tipo de id del modelo
 */
public class ProductoDao extends Dao<Producto, Long> {

  /**
   * @return Base de datos
   */
  @Override
  protected DataBase getDataBase() {
    return DataBaseConfig.getDataBaseMySQL();
  }

  /**
   * @return Nombre de la tabla en la base de datos
   */
  @Override
  protected String getTableName() {
    return "producto";
  }
  
  /**
   * AL leer un registro de la base de datos.
   * 
   * @param rs resultado obtenido del query
   * 
   * @return modelo mapeado con el resultado obtenido
   * 
   * @throws SQLException 
   */
  @Override
  protected Producto onRead(ResultSet rs) throws SQLException {
    Producto p = new Producto();
    p.setId(rs.getInt("id"));
    p.setCodigo(rs.getString("codigo"));
    p.setNombre(rs.getString("nombre"));
    return p;
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
  @Override
  protected Map<String, Object> onWrite(Producto m) {
    Map<String, Object> values = new HashMap<String, Object>(2);
    values.put("codigo", m.getCodigo());
    values.put("nombre", m.getNombre());
    return values;
  }

  /**
   * Condición where.
   * 
   * @param id del modelo
   * 
   * @return cláusula WHERE para aplicar una actualización, eliminación y busqueda de 
   *    un registro.
   */
  @Override
  protected String whereClause(Long id) {
    return "id = ?";
  }

  /**
   * Obtiene el id
   * 
   * @param m modelo
   * 
   * @return id
   */
  @Override
  protected Long getId(Producto m) {
    return m.getId();
  }
  
  /**
   * Si el registro es editable: (true) ? Update : insert.
   * 
   * @param m modelo
   * 
   * @return @true si es editable
   */
  @Override
  protected boolean isUpdate(Producto m) throws SQLException {
    return m.getId() > 0;
  }
  
  /**
   * Al isertar el registro obtiene el id del registro
   * 
   * @param m modelo
   * @param id insertado
   */
  @Override
  protected void insertId(Producto m, long id) {
    if (id > 0) {
      m.setId(id);
    }
  }
  
  public static void main(String... args) {
    Random random = new Random();
    ProductoDao dao = new ProductoDao();
    
    try {
      Producto p = new Producto();
      p.setCodigo("ABC-" + System.currentTimeMillis());
      p.setNombre("PC-Lenovo-" + random.nextInt());
      dao.insert(p);
      
      System.out.println("insert:" + p);
      
      p.setCodigo("ABCDEF-" + System.currentTimeMillis());
      dao.update(p);
      
      System.out.println("update:" + p);
      
      System.out.println("findById:" + dao.findById(p.getId()));
      
     // dao.delete(p);
      
      List<Producto> list = dao.find();
      for (Producto producto : list) {
        System.out.println(producto);
      }
      
      System.out.println("count:" + dao.count());
      
    } catch(SQLException e) {
      e.printStackTrace(System.out);
    } finally {
      dao.getDataBase().close();
    }
  }
}