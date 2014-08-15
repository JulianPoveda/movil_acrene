/***********************************************************************************************************************************
 * Version 1.0
 * Fecha: Mayo 14-2014
 * News:	ExecuteArrayListQuery(String _campo, ArrayList<ContentValues> _informacion)
 * 			Funcion encargada de recibir un array List con los querys a ejecutar, se ejecuta un replace de DELETE a DELETE FROM
 * 			de esta forma se garantiza que la sentencia SQL DELETE se ejecute efectivamente en la base de datos SQLite, por ultimo
 * 			retorna la cantidad de ejecuciones que realizo.
***********************************************************************************************************************************/



package miscelanea;

import java.io.File;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLite {
	private static Archivos ArchSQL;	
	private static String N_BD = null; 	
	private static final int VERSION_BD = 1;																		
	
	private BDHelper nHelper;
	private Context nContexto;
	private String Directorio;
	private SQLiteDatabase nBD;
	
	private boolean ValorRetorno;
	
	private static class BDHelper extends SQLiteOpenHelper{
		
		public BDHelper(Context context) {
			super(context, N_BD, null, VERSION_BD);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			/**************************************************************************************************************************/
			/************************Creacion de las tablas basicas para el correcto funcionamiento del sistema************************/
			/**************************************************************************************************************************/
			
			//Tabla con los usuarios del sistema
			db.execSQL(	"CREATE TABLE amd_usuarios(	login 		VARCHAR(50) NOT NULL PRIMARY KEY,"
												+ "	password	VARCHAR(50) NOT NULL,"
												+ "	perfil 		VARCHAR(100),"
												+ "	documento	VARCHAR(100),"
												+ "	nombre		VARCHAR(100))");
			
			db.execSQL("INSERT INTO amd_usuarios('login','password','perfil','documento','nombre') VALUES ('adm','s1st3m4','A','0','Administrador del Sistema')");
			db.execSQL("INSERT INTO amd_usuarios('login','password','perfil','documento','nombre') VALUES ('OBaez','80201001','U','80201001','Tecnico')");
			
			//Tabla con los parametros del sistema
			db.execSQL("CREATE TABLE db_parametros (item TEXT PRIMARY KEY, valor TEXT NOT NULL, nivel INTEGER NOT NULL)");				
			db.execSQL("INSERT INTO db_parametros (item,valor,nivel) VALUES ('servidor','http://190.93.133.87',0) ");
			db.execSQL("INSERT INTO db_parametros (item,valor,nivel) VALUES ('puerto','8080',0) ");
			db.execSQL("INSERT INTO db_parametros (item,valor,nivel) VALUES ('modulo','Enerca/WS',0) ");
			db.execSQL("INSERT INTO db_parametros (item,valor,nivel) VALUES ('web_service','AndroidWS.php?wsdl',0)");
			db.execSQL("INSERT INTO db_parametros (item,valor,nivel) VALUES ('nombre_tecnico','Oscar Baez',1)");
			db.execSQL("INSERT INTO db_parametros (item,valor,nivel) VALUES ('impresora','sin asignar',1)");
			db.execSQL("INSERT INTO db_parametros (item,valor,nivel) VALUES ('version','1.0',0)");
			db.execSQL("INSERT INTO db_parametros (item,valor,nivel) VALUES ('NPDA','80201001',0)");		
			
			
			db.execSQL(	"CREATE TABLE amd_conexion_acometida(id_serial INTEGER PRIMARY KEY AUTOINCREMENT, descripcion VARCHAR(100) UNIQUE NOT NULL)");			
			db.execSQL("INSERT INTO amd_conexion_acometida (descripcion) VALUES ('...')");
			db.execSQL("INSERT INTO amd_conexion_acometida (descripcion) VALUES ('Abierta')");
			db.execSQL("INSERT INTO amd_conexion_acometida (descripcion) VALUES ('Concentrica')");
			
			
			db.execSQL(	"CREATE TABLE amd_cod_accion(item VARCHAR(3) PRIMARY KEY,descripcion VARCHAR(255) NOT NULL)");
			db.execSQL( "INSERT INTO  amd_cod_accion  (item,descripcion) VALUES ('20','Suspendido en Postes.')");
			db.execSQL( "INSERT INTO  amd_cod_accion  (item,descripcion) VALUES ('30','Suspendido en pin de corte.')");
			db.execSQL( "INSERT INTO  amd_cod_accion  (item,descripcion) VALUES ('40','Suspendido en cañuela.')");
			db.execSQL( "INSERT INTO  amd_cod_accion  (item,descripcion) VALUES ('50','Suspendido en cichilla.')");
			db.execSQL( "INSERT INTO  amd_cod_accion  (item,descripcion) VALUES ('60','Retiro pin de corte.')");
			db.execSQL( "INSERT INTO  amd_cod_accion  (item,descripcion) VALUES ('70','Retiro cañuela.')");
			db.execSQL( "INSERT INTO  amd_cod_accion  (item,descripcion) VALUES ('80','Retiro acometida.')");
			db.execSQL( "INSERT INTO  amd_cod_accion  (item,descripcion) VALUES ('93','Suspension en bornera.')");
			db.execSQL( "INSERT INTO  amd_cod_accion  (item,descripcion) VALUES ('100','Se encontro suspendido.')");
			db.execSQL( "INSERT INTO  amd_cod_accion  (item,descripcion) VALUES ('110','No se encontro.')");
			db.execSQL( "INSERT INTO  amd_cod_accion  (item,descripcion) VALUES ('120','Predio demolido.')");
			db.execSQL( "INSERT INTO  amd_cod_accion  (item,descripcion) VALUES ('121','Predio abandonado.')");
			db.execSQL( "INSERT INTO  amd_cod_accion  (item,descripcion) VALUES ('130','Codigo en reclamacion.')");
			db.execSQL( "INSERT INTO  amd_cod_accion  (item,descripcion) VALUES ('140','Pago o abono a factura.')");
			db.execSQL( "INSERT INTO  amd_cod_accion  (item,descripcion) VALUES ('170','Sin corte visible.')");
			db.execSQL( "INSERT INTO  amd_cod_accion  (item,descripcion) VALUES ('185','Orden publico.')");
			db.execSQL( "INSERT INTO  amd_cod_accion  (item,descripcion) VALUES ('195','Orden de Enerca S.A E.S.P.')");
			db.execSQL( "INSERT INTO  amd_cod_accion  (item,descripcion) VALUES ('216','Casos especiales.')");
			db.execSQL( "INSERT INTO  amd_cod_accion  (item,descripcion) VALUES ('230','Autorreconectado, nueva suspension.')");
			db.execSQL( "INSERT INTO  amd_cod_accion  (item,descripcion) VALUES ('240','Autorreconectado, no permite suspender.')");
			db.execSQL( "INSERT INTO  amd_cod_accion  (item,descripcion) VALUES ('066','Reconexion no autorizada por la empresa.')");
			db.execSQL( "INSERT INTO  amd_cod_accion  (item,descripcion) VALUES ('290','Se reconecto el servicio.')");
			db.execSQL( "INSERT INTO  amd_cod_accion  (item,descripcion) VALUES ('300','Se encontraba con servicio.')");
			db.execSQL( "INSERT INTO  amd_cod_accion  (item,descripcion) VALUES ('340','No se pudo reconectar.')");
			db.execSQL( "INSERT INTO  amd_cod_accion  (item,descripcion) VALUES ('320','Apertura de puentes en MT.')");		
			
			/********************************************************************************************************/
			db.execSQL( "CREATE TABLE amd_ordenes_trabajo(	 id_serial 				INTEGER NOT NULL," + 
															"id_tipo_archivo		VARCHAR(1) NOT NULL," +
															"id_proceso 			VARCHAR(1) NOT NULL," +
															"ciclo					INTEGER NOT NULL," +
															"cuenta					VARCHAR(50)," +
															"usuario				VARCHAR(255)," +
															"direccion				VARCHAR(255)," +
															"serie					VARCHAR(50)," +
															"municipio				VARCHAR(100)," +
															"dig_fecha_visita		DATE," +
															"dig_hora_visita		VARCHAR(20)," +
															"dig_lectura_actual		DOUBLE PRECISION," +
															"dig_codigo_accion 		INTEGER," +
															"dig_material_retirado 	BOOELAN NOT NULL DEFAULT FALSE," +
															"dig_num_canuelas 		INTEGER NOT NULL DEFAULT 0," +
															"dig_tipo_acometida 	VARCHAR(1)," +
															"dig_estado_acometida 	VARCHAR(1)," +
															"dig_longitud_acometida DOUBLE PRECISION NOT NULL DEFAULT 0," +
															"dig_calibre_acometida 	INTEGER," +
															"dig_color_acometida 	VARCHAR(20)," +
															"dig_pin_corte 			BOOLEAN NOT NULL DEFAULT FALSE," +
															"dig_otros 				VARCHAR(255)," +
															"dig_cancelada_factura 	BOOLEAN NOT NULL DEFAULT FALSE," +
															"dig_fecha_pago_factura DATE," +
															"dig_monto_factura 		DOUBLE PRECISION NOT NULL DEFAULT 0," +
															"dig_entidad_factura 	VARCHAR(255)," +
															"dig_observacion 		VARCHAR(500)," +
															"dig_observacion_sellos	VARCHAR(500)," +
															"dig_nombre_usuario 	VARCHAR(100)," +
															"dig_ident_usuario 		VARCHAR(50)," +
															"dig_telefono 			VARCHAR(30)," +
															"estado 				VARCHAR(1) NOT NULL DEFAULT 'P'," +
															"PRIMARY KEY(id_serial,id_tipo_archivo))");
			
			
			
			/*********************************************************************************************************************/
			/*******Tablas con la informacion respetiva de las anomalias generales y las aplicadas a la orden en particular*******/
			/*********************************************************************************************************************/
			db.execSQL(	"CREATE TABLE amd_sellos_ordenes(	id_serial 			INTEGER NOT NULL REFERENCES amd_ordenes_trabajo(id_serial),"
														+ "	id_tipo_archivo		VARCHAR(1) NOT NULL REFERENCES amd_ordenes_trabajo(id_tipo_archivo)," 
														+ " movimiento 			VARCHAR(1) NOT NULL,"					
														+ " tipo_sello 			VARCHAR(50) NOT NULL,"					
														+ "	serie				VARCHAR(20) NOT NULL,"
														+ "	PRIMARY KEY(id_serial,id_tipo_archivo,movimiento,tipo_sello,serie))");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("UPDATE db_parametros SET valor = '1.6' WHERE item = 'version'");
		}
	}
	
	
	public boolean SQLString(String Query){
		ValorRetorno = false;
		try{
			nBD.execSQL(Query);		
			ValorRetorno = true;
		}catch(Exception e){
			ValorRetorno = false;
		}
		return ValorRetorno;
	}
	
	
	public SQLite (Context c, String CurrentDirectory){
		this.nContexto = c;
		this.Directorio = CurrentDirectory;
		SQLite.N_BD = this.Directorio + File.separator + "BdAmData_Android";
		
		ArchSQL = new Archivos(this.nContexto, this.Directorio, 10);
		if(!ArchSQL.ExistFolderOrFile(this.Directorio)){		
			ArchSQL.MakeDirectory();
		}
	}
	
	
	private SQLite abrir(){
		nHelper = new BDHelper(nContexto);
		nBD = nHelper.getWritableDatabase();
		return this;
	}

	private void cerrar() {
		nHelper.close();
	}

	
	
	
	//Funcion para ejecutar un query, recibe como parametros un array list con los querys a ejecutar
	/*public int ExecuteArrayListQuery(String _campo, ArrayList<ContentValues> _informacion){
		int _retorno = 0;
		ContentValues QueryExecute = new ContentValues();
		abrir();
		for(int i=0;i< _informacion.size();i++){
			QueryExecute.clear();
			QueryExecute = _informacion.get(i);
			String _sentencia = QueryExecute.get(_campo).toString().replace("DELETE", "DELETE FROM");
			if(_sentencia.indexOf("AMD_CONTADOR_CLIENTE_ORDEN")>=0){
				_retorno +=2;
			}
			try{
				nBD.execSQL(_sentencia);  
				_retorno++;
			}catch(Exception e){			
			}	
		}
		cerrar();
		return _retorno;
	}*/
	
	
	
	
	//Funcion que inserta una serie de registros consecutivos en una tabla en particular
	/*public int InsertArrayRegistro(String Tabla, ArrayList<ContentValues> Informacion){
		int _retorno = 0;
		ContentValues RegistroInsert = new ContentValues();
		abrir();
		for(int i=0;i< Informacion.size();i++){
			RegistroInsert.clear();
			RegistroInsert = Informacion.get(i);
			try{
				if(nBD.insert(this.TablasAmData.getAsString(Tabla),null,RegistroInsert)>=0){
					_retorno++;
				}
			}catch(Exception e){			
			}	
		}
		cerrar();
		return _retorno;		
	}*/
		
	
	
	//Funcion que inserta una serie de registros consecutivos en una tabla en particular
	/*public void EjecutarArraySQL(String _campo, ArrayList<ContentValues> _informacion){
		ContentValues RegistroInsert = new ContentValues();
		abrir();
		for(int i=0;i< _informacion.size();i++){
			RegistroInsert.clear();
			RegistroInsert = _informacion.get(i);
			String _sentencia = RegistroInsert.get(_campo).toString();
			try{
				nBD.execSQL(_sentencia);  
			}catch(Exception e){			
			}	
		}
		cerrar();
	}*/
	
	
	/**Funcion para ejecutar una sentencia SQL recibida por parametro
	 * @param _sql	->Sentencia SQL a ejecutar
	 * @return		->true en caso de ejecutarse correctamente, false en otros casos
	 */
	public boolean EjecutarSQL(String _sql){
		boolean _retorno = false;
		abrir();
		try{
			nBD.execSQL(_sql);  
			_retorno = true;;
		}catch(Exception e){	
		}	
		cerrar();
		return _retorno;
	}
	
	
	/*public int EjecutarArrayStringSQL(ArrayList<String> _informacion){
		int _retorno = 0;
		String RegistroInsert;
		abrir();
		for(int i=0;i< _informacion.size();i++){
			RegistroInsert = _informacion.get(i);
			RegistroInsert = RegistroInsert.replace("DELETE", "DELETE FROM");
			try{
				nBD.execSQL(RegistroInsert);  
				_retorno++;
			}catch(Exception e){	
				Toast.makeText(this.nContexto,"Exception "+ e.toString() + "Script " + RegistroInsert, Toast.LENGTH_SHORT).show();
			}	
		}
		cerrar();
		return _retorno;
	}*/
	
	
	/**Funcion para realizar INSERT
	 * @param _tabla 		-> tabla a la cual se va a realizar el INSERT
	 * @param _informacion	-> informacion que se va a insertar 
	 * @return				-> true si se realizo el insert correctamente, false en otros casos
	 */	
	public boolean InsertRegistro(String _tabla, ContentValues _informacion){
		abrir();
		ValorRetorno = false;
		try{
			if(nBD.insert(_tabla,null, _informacion)>=0){
				ValorRetorno = true;
			}
		}catch(Exception e){			
		}			
		cerrar();
		return ValorRetorno;
	}
	
	
	/**Funcion para realizar UPDATE 
	 * @param _tabla		->tabla sobre la cual se va a realizar el UPDATE	
	 * @param _informacion	->informacion que se va a actualizar
	 * @param _condicion	->condcion que debe tener el registro para ejecutar el UPDATE
	 * @return				->true si se realizo el UPDATE correctamente, flase en otros casos
	 */
	public boolean UpdateRegistro(String Tabla, ContentValues Informacion, String Condicion){
		ValorRetorno = false;
		abrir();
		try{
			if(nBD.update(Tabla, Informacion, Condicion, null)>=0){
				ValorRetorno = true;
			}
		}catch(Exception e){
		}
		cerrar();
		return ValorRetorno;
	}
	
	
	/**Funcion para realizar un insert en caso de no existir el registro o update en caso de existir
	 * @param _tabla		->tabla sobre la cual se va a operar
	 * @param _informacion	->informacion que se va a insertar o actualizar
	 * @param _condicion	->Condicion que debe cumplirse para realizar el update y/o insert
	 * @return				->String con el mensaje de retorno, ya puede ser insert/update realizado o no realizado.
	 */
	public String InsertOrUpdateRegistro(String _tabla, ContentValues _informacion, String _condicion){
		String _retorno = "Sin acciones";
		if(!this.ExistRegistros(_tabla, _condicion)){
			if(this.InsertRegistro(_tabla, _informacion)){
				_retorno = "Registro ingresado en "+_tabla;
			}else{	
				_retorno = "Error al ingresar el registro en "+_tabla;
			}	
		}else{
			if(this.UpdateRegistro(_tabla, _informacion, _condicion)){
					_retorno = "Registro actualizado en "+_tabla;
			}else{
				_retorno = "Error al actualizar el registro en "+_tabla;
			}
		}		
		return _retorno;		
	}
	
	
	/**Funcion para eliminar un registro de una tabla en particular
	 * @param _tabla		->tabla sobre la cual se va a trabajar
	 * @param _condicion	->condicion que debe cumplirse para ejecutar el delete respectivo	
	 * @return				->true si fue ejecutado el delete correctamente, false en caso contrario
	 */
	public boolean DeleteRegistro(String _tabla, String _condicion){
		ValorRetorno = false;
		abrir();
		try{
			if(nBD.delete(_tabla, _condicion,null)>0){
				ValorRetorno = true;
			}
		}catch(Exception e){
			Log.i("Error en SQLite", e.toString());
		}
		cerrar();
		return ValorRetorno;
	}
		
	//Consulta de datos con retorno de lista de array asociativo
	/*public ArrayList<ArrayList<String>> SelectDataKeyValue(String TablaConsulta, String CamposConsulta, String CondicionConsulta) {
		abrir();
		ArrayList<ArrayList<String>> Matriz = new ArrayList<ArrayList<String>>();
		
		try{
			Cursor c = nBD.rawQuery("SELECT "+CamposConsulta+" FROM "+TablaConsulta+" WHERE "+CondicionConsulta, null);
			String[] Columnas = c.getColumnNames();
					
			for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
				ArrayList<String> Registro = new ArrayList<String>();
				for(int i=0;i<Columnas.length;i++){
					Registro.add(c.getString(i));
				}
				Matriz.add(Registro);
			}			
		}
		catch(Exception e){
			Log.i("Error en SQLite", e.toString());
		}
		cerrar();
		return Matriz;
	}*/
	
	
	/**Funcion encargada de realizar una consulta y retornarla con un array list de content values, similar a un array de diccionarios
	 * @param _tabla		->tabla sobre la cual va a correr la consulta
	 * @param _campos		->campos que se van a consultar
	 * @param _condicion	->condicion para filtrar la consulta
	 * @return				->ArrayList de ContentValues con la informacion resultado de la consulta
	 */
	public ArrayList<ContentValues> SelectData(String _tabla, String _campos, String _condicion){
		ArrayList<ContentValues> _query = new ArrayList<ContentValues>();
		_query.clear();
		this.abrir();
		try{
			Cursor c = nBD.rawQuery("SELECT DISTINCT "+_campos+" FROM "+_tabla+" WHERE "+_condicion, null);
			String[] _columnas = c.getColumnNames();
					
			for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
				ContentValues _registro = new ContentValues();
				for(int i=0;i<_columnas.length;i++){
					_registro.put(_columnas[i], c.getString(i));
				}
				_query.add(_registro);
			}			
		}
		catch(Exception e){
			Log.i("Error en SQLite", e.toString());
		}	
		this.cerrar();		
		return _query;		
	}
	
	
	/**Funcion para realizar la consulta de un registro, retorna un contentvalues con la informacion consultada
	 * @param _tabla		->tabla sobre la cual se va a ejecutar la consulta
	 * @param _campos		->campos que se quieren consultar
	 * @param _condicion	->condicion para ejecutar la consulta
	 * @return				-->ContentValues con la informacion del registro producto de la consulta
	 */
	public ContentValues SelectDataRegistro(String _tabla, String _campos, String _condicion){
		ContentValues _query = new ContentValues();
		_query.clear();
		this.abrir();
		try{
			Cursor c = nBD.rawQuery("SELECT DISTINCT "+_campos+" FROM "+_tabla+" WHERE "+_condicion+" LIMIT 1", null);
			String[] _columnas = c.getColumnNames();
						
			for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
				//ContentValues _registro = new ContentValues();
				for(int i=0;i<_columnas.length;i++){
					_query.put(_columnas[i], c.getString(i));
				}
			}			
		}
		catch(Exception e){
			Log.i("Error en SQLite", e.toString());
		}	
		this.cerrar();		
		return _query;		
	}
		
	
	//Relizar la consulta teniendo en cuenta varios JOIN a la izquierda
	public ArrayList<ContentValues> SelectNJoinLeftData(String _tabla, String _campos, String _join_left[], String _on_left[], String _condicion){
		String Query = "";
		ArrayList<ContentValues> _query = new ArrayList<ContentValues>();
		_query.clear();
		this.abrir();
		try{
			Query = "SELECT DISTINCT "+ _campos + " FROM "+ _tabla;
			for(int i=0;i<_join_left.length;i++){
				Query += " LEFT JOIN " +_join_left[i] + " ON "+_on_left[i];
			}
			Query += " WHERE "+ _condicion;
			Cursor c = nBD.rawQuery(Query, null);
			String[] _columnas = c.getColumnNames();
					
			for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
				ContentValues _registro = new ContentValues();
				for(int i=0;i<_columnas.length;i++){
					if(c.getString(i) == null){
						_registro.put(_columnas[i], "");
					}else{
						_registro.put(_columnas[i], c.getString(i));
					}
				}
				_query.add(_registro);
			}			
		}
		catch(Exception e){
			Log.i("Error en SQLite", e.toString());
		}	
		this.cerrar();		
		return _query;		
	}
	
	//Selecciona un registro
	/*public void SelectData(ArrayList<String> TxtCampos, String TablaConsulta, String CamposConsulta, String CondicionConsulta, String Base) {
		abrir();
		try{
			TxtCampos.clear();
			if (!Base.equals("")){
				TxtCampos.add(Base);
			}
			
			Cursor c = nBD.rawQuery("SELECT DISTINCT "+CamposConsulta+" FROM "+TablaConsulta+" WHERE "+CondicionConsulta, null);
			String[] Columnas = c.getColumnNames();
					
			for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
				for(int i=0;i<Columnas.length;i++){
					TxtCampos.add(c.getString(i));
				}				
			}			
		}
		catch(Exception e){
			Log.i("Error en SQLite", e.toString());
		}	
		cerrar();
	}*/
	
	
	/*public void SelectDistinctLimit(ArrayList<ArrayList<String>> Registros, String Tabla, String Campos, String Condicion, int Limite) {
		Registros.clear();
		abrir();
		try{
			Cursor c = nBD.rawQuery("SELECT "+Campos+" FROM "+Tabla+" WHERE "+Condicion+" LIMIT "+ Limite, null);
			String[] Columnas = c.getColumnNames();
			
			for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
				ArrayList<String> Registro = new ArrayList<String>();
				for(int i=0;i<Columnas.length;i++){
					Registro.add(c.getString(i));
				}
				Registros.add(Registro);
			}			
		}
		catch(Exception e){
			Log.i("Error en SQLite SelectMultiple", e.toString());
		}	
		cerrar();
	}*/
	
	/**Funcion que consulta un campo de una tabla segun la condicion recibida y retorna el resultado como un entero
	 * @param _tabla		->Tabla sobre la cual se va a trabajar
	 * @param _campo		->Campo que se quiere consultar
	 * @param _condicion	->Condicion para filtro de la consulta
	 * @return
	 */
	public int IntSelectShieldWhere(String _tabla, String _campo, String _condicion){
		int intRetorno = -1;
		abrir();
		try{
			Cursor c = nBD.rawQuery("SELECT " + _campo + " FROM " + _tabla + " WHERE " + _condicion, null);
			for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
				intRetorno = c.getInt(0);
			}			
		}
		catch(Exception e){
			intRetorno = -1;
		}
		cerrar();
		return intRetorno;
	}
	
	
	/**Funcion que consulta un campo de una tabla segun la condicion recibida y retorna el resultado como un double
	 * @param _tabla		->Tabla sobre la cual se va a trabajar
	 * @param _campo		->Campo que se quiere consultar
	 * @param _condicion	->Condicion para filtro de la consulta
	 * @return
	 */
	public double DoubleSelectShieldWhere(String _tabla, String _campo, String _condicion){
		double intRetorno = -1;
		abrir();
		try{
			Cursor c = nBD.rawQuery("SELECT " + _campo + " FROM " + _tabla + " WHERE " + _condicion, null);
			for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
				intRetorno = c.getDouble(0);
			}			
		}
		catch(Exception e){
			intRetorno = -1;
		}
		cerrar();
		return intRetorno;
	}
	
	
	/**Funcion que consulta un campo de una tabla segun la condicion recibida y retorna el resultado como un String
	 * @param _tabla		->Tabla sobre la cual se va a trabajar
	 * @param _campo		->Campo que se quiere consultar
	 * @param _condicion	->Condicion para filtro de la consulta
	 * @return
	 */
	public String StrSelectShieldWhere(String _tabla, String _campo, String _condicion){
		String strRetorno = null;
		abrir();
		try{
			Cursor c = nBD.rawQuery("SELECT " + _campo + " FROM " + _tabla + " WHERE " + _condicion, null);
			for(c.moveToFirst();!c.isAfterLast();c.moveToNext()){
				strRetorno = c.getString(0);
			}			
		}
		catch(Exception e){
			e.toString();
			strRetorno = null;
		}
		cerrar();
		return strRetorno;
	}
	
	
	/**Funcion retorna la cantidad de registros de una tabla que cumplen la condicion recibida por parametro
	 * @param _tabla		->Tabla sobre la cual se va a trabajar
	 * @param _condicion	->Condicion para filtro de la consulta
	 * @return
	 */
	public int CountRegistrosWhere(String _tabla, String _condicion){
		int ValorRetorno = 0;
		abrir();
		try{
			Cursor c = nBD.rawQuery("SELECT count(*) FROM "+ _tabla +" WHERE "+ _condicion, null);
			c.moveToFirst();
			ValorRetorno = c.getInt(0);		
		}
		catch(Exception e){
		}
		cerrar();
		return ValorRetorno;
	}
	
	
	/**Funcion que retorna true o falso segun existan o no registros que cumplan la condicion recibida por parametro
	 * @param _tabla		->Tabla sobre la cual se va trabajar
	 * @param _condicion	->Condicion de filtro
	 * @return
	 */
	public boolean ExistRegistros(String _tabla, String _condicion){
		ValorRetorno = false;
		abrir();
		try{
			Cursor c = nBD.rawQuery("SELECT count(*) as cantidad FROM " + _tabla +" WHERE " + _condicion , null);
			c.moveToFirst();
			if(c.getDouble(0)>0)
				ValorRetorno = true;
		}catch(Exception e){
			Log.v("Excepcion",e.toString());
		}
		cerrar();
		return ValorRetorno;
	}
	
	
	/**Funcion que retorna la cantidad de minutos transcurridos desde la fecha actual y la recibida por parametro
	 * @param _oldDate	->fecha anterior contra la cual se quiere calcular la diferencia en segundos
	 * @return 			->String con el resultado en minutos
	 */
	public String MinutesBetweenDateAndNow(String _oldDate){
		String _retorno = "";
		int _minutos = 0;
		abrir();
		try{
			Cursor c = nBD.rawQuery("SELECT strftime('%s','now')-strftime('%s','"+_oldDate+"') as segundos", null);
			c.moveToFirst();
			_retorno = c.getString(0);
			_minutos = Integer.parseInt(_retorno)/60;
		}catch(Exception e){
			Log.v("Excepcion",e.toString());
		}
		cerrar();		
		return String.valueOf(_minutos);		
	}
	
	
	/**Funcion que retorna la cantidad de minutos transcurridos entre las fechas recibidas por parametro
	 * @param _newDate	->fecha mas reciente contra la cual se quiere caldular la diferencia
	 * @param _oldDate	->fecha anterior contra la cual se quiere calcular la diferencia en segundos
	 * @return 			->Strig con el resultado en minutos
	 */
	public String MinutesBetweenDates(String _newDate, String _oldDate){
		String _retorno = "";
		int _minutos = 0;
		abrir();
		try{
			Cursor c = nBD.rawQuery("SELECT strftime('%s','"+_newDate+"')-strftime('%s','"+_oldDate+"') as segundos", null);
			c.moveToFirst();
			_retorno = c.getString(0);
			_minutos = Integer.parseInt(_retorno)/60;
		}catch(Exception e){
			Log.v("Excepcion",e.toString());
		}
		cerrar();		
		return String.valueOf(_minutos);		
	}
}
