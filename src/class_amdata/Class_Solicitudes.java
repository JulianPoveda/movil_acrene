package class_amdata;


import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import miscelanea.SQLite;

public class Class_Solicitudes {
	private Context _contextSolicitudes;
	private String  _folderSolicitudes;  
	private SQLite 	SolicitudesSQL; 
	
	private ArrayList<ContentValues>_tempTabla		= new ArrayList<ContentValues>();
	//private ContentValues			_tempRegistro	= new ContentValues();
	
	
	public Class_Solicitudes(Context _ctx, String _folderAplicacion){
		this._contextSolicitudes= _ctx;
		this._folderSolicitudes	= _folderAplicacion;
		SolicitudesSQL	= new SQLite(this._contextSolicitudes,this._folderSolicitudes);
	}
	
	/*************************Funcion que retorna el estado que tiene la orden que se recibe por parametro*************************/
	public String getEstadoOrden(String _orden){
		return this.SolicitudesSQL.StrSelectShieldWhere("amd_ordenes_trabajo", "estado", "id_serial='"+_orden+"'");
	}
	
	/***************Funcion para validar si se permite el ingreso de informacion a la orden recibida por parametro****************/
	public boolean IniciarOrden(String _id_serial, String _id_tipo_archivo){
		boolean _retorno = false;
		if(this.SolicitudesSQL.ExistRegistros("amd_ordenes_trabajo", "estado='E'")){
			if(this.SolicitudesSQL.ExistRegistros("amd_ordenes_trabajo", "id_serial='"+_id_serial+"' AND id_tipo_archivo='"+_id_tipo_archivo+"' AND estado='E'")){
				_retorno = true;
			}else{
				_retorno = false;
			}
		}else if(this.SolicitudesSQL.ExistRegistros("amd_ordenes_trabajo", "id_serial='"+_id_serial+"' AND id_tipo_archivo='"+_id_tipo_archivo+"' AND estado='P'")){
			_retorno = true;
		}else{
			_retorno = false;
		}	
		return _retorno;
	}
	
	
	/**Funcion que valida si es posible abrir una con el codigo de apertura, para esto no debe haber ninguno otra 
	 * orden abierta y la seleccionada debe estar ya cerrada**/
	public boolean IniciarAperturaOrden(String _orden){
		boolean _retorno = false;
		if(this.SolicitudesSQL.ExistRegistros("amd_ordenes_trabajo", "estado='E'")){
			_retorno = false;
		}else if(this.SolicitudesSQL.ExistRegistros("amd_ordenes_trabajo", "id_orden='"+_orden+"' AND estado='T'")){
			_retorno = true;
		}	
		return _retorno;
	}
	
	/**Funcion para verificar el codigo de apertura de la orden seleccionada y el codigo ingresado por el usuario**/
	public boolean verificarCodigoApertura(String _orden, String _codigoApertura){
		return this.SolicitudesSQL.ExistRegistros("amd_ordenes_trabajo", "id_orden='"+_orden+"' AND codigo_apertura='"+_codigoApertura+"'");
	}
	
	/**Funcion que retorna el nodo de la orden que se reciba como parametro**/
	public String getNodo(String _orden){
		return this.SolicitudesSQL.StrSelectShieldWhere("amd_ordenes_trabajo", "id_nodo", "id_orden='"+_orden+"'");
	}
	
	
	/**Funcion que retorna el id_trabajo_contrato segun la ordenq ue se reciba como parametro**/
	public int getIdTrabajoContrato(String _orden){
		return this.SolicitudesSQL.IntSelectShieldWhere("vista_ordenes_trabajo", "id_trabajo", "id_orden='"+_orden+"'");
	}
	
	
	public ArrayList<String> getMunicipiosSolicitudes(){
		ArrayList<String> _listaNodos = new ArrayList<String>();
		_listaNodos.add("Todos");
		this._tempTabla = this.SolicitudesSQL.SelectData("amd_ordenes_trabajo","municipio","estado in ('P','E','T','TA')");
		for(int i=0;i<this._tempTabla.size();i++){
			_listaNodos.add(this._tempTabla.get(i).getAsString("municipio"));
		}	
		return _listaNodos;
	}
	
	
	public ArrayList<String> getActividadesSolicitudes(){
		ArrayList<String> _listaActividades = new ArrayList<String>();
		_listaActividades.add("Todos");
		this._tempTabla = this.SolicitudesSQL.SelectData("amd_ordenes_trabajo","id_tipo_archivo","estado in ('P','E','T','TA')");
		for(int i=0;i<this._tempTabla.size();i++){
			if(this._tempTabla.get(i).getAsString("id_tipo_archivo").equals("N")){
				_listaActividades.add("N-Seguimiento");
			}else if(this._tempTabla.get(i).getAsString("id_tipo_archivo").equals("S")){
				_listaActividades.add("S-Suspension");
			}else if(this._tempTabla.get(i).getAsString("id_tipo_archivo").equals("R")){
				_listaActividades.add("R-Reconexion");
			}else if(this._tempTabla.get(i).getAsString("id_tipo_archivo").equals("C")){
				_listaActividades.add("C-Corte");
			}			
		}	
		return _listaActividades;
	}	
}
