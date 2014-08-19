package form_amdata;

import java.io.File;
import java.util.ArrayList;

import class_amdata.Class_Solicitudes;
import dialogos.DialogConfirm;
import dialogos.DialogInformacion;
import dialogos.DialogSingleTxt;
import sypelc.androidamdata.R;
import miscelanea.SQLite;
import miscelanea.Util;
import adaptador_download_trabajo.AdaptadorListaTrabajo;
import adaptador_download_trabajo.InformacionSolicitudes;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;


public class Form_Solicitudes extends Activity implements OnItemSelectedListener, OnItemClickListener{
	Intent DialogConfirmacion; 	
	Intent DialogInformacion; 	
	Intent DialogApertura;
		
	Class_Solicitudes 	FcnSolicitudes;
	
	Util			SolicitudesUtil;
	SQLite 			SolicitudesSQL; 
	private String 	FolderAplicacion = "";
	
	private static int    	CONFIRMACION_INICIO_ORDEN	= 1;
	private static int 		CONFIRMACION_INFORMACION 	= 2;
	private static int		CONFIRMACION_CERRAR_ORDEN	= 3;
	private static int 		CONFIRMACION_COD_APERTURA	= 4;
	
	private boolean enabledMenu = false;
	
	
	/**Variables para del usuario que inicio sesion, lo cuales son utilizados para registrar todos los movimientos realizados**/
	private String 	NombreUsuario = "";
	private String 	CedulaUsuario= "";
	private String 	NivelUsuario = "";
	
	//Variable para consulta general de la base de datos
	ArrayList<ContentValues> _tempTabla = new ArrayList<ContentValues>();
	ContentValues _tempRegistro = new ContentValues(); 
	
	
	//Variable para consulta general de la base de datos
	ArrayList<ContentValues> TablaQuery = new ArrayList<ContentValues>();
	ContentValues RegistroQuery = new ContentValues(); 
	
	//Variables para control del combo de nodos y adaptadores
	private ArrayAdapter<String> AdaptadorNodos;
	private ArrayList<String> Nodos = new ArrayList<String>();
	private Spinner ListaNodos;
	
	
	//Variables para control del combo de solicitudes por nodo y adaptadores
	AdaptadorListaTrabajo AdaptadorSolicitudes;
	ArrayList<InformacionSolicitudes> ArraySolicitudes = new ArrayList<InformacionSolicitudes>();
	ListView ListaSolicitudes;
	
	
	//Instancias a objetos graficos
	TextView _txtMunicipio, _txtSerie, _txtDireccion, _txtPropietario, _txtCuenta, _txtIdSerial, _txtProceso, _txtTipo, _txtObservacion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_solicitudes);
		
		Bundle bundle = getIntent().getExtras();
		this.FolderAplicacion= bundle.getString("FolderAplicacion");		
		this.NombreUsuario	= bundle.getString("NombreUsuario");
		this.CedulaUsuario	= bundle.getString("CedulaUsuario");
		this.NivelUsuario	= bundle.getString("NivelUsuario");
		
		/*************************Instacias a dialogos para mensajes de confirmacion e informacion********************/
		DialogConfirmacion 	= new Intent(this,DialogConfirm.class);
		DialogInformacion	= new Intent(this,DialogInformacion.class);
		DialogApertura		= new Intent(this,DialogSingleTxt.class);
		/*************************************Instancias a clases para las funciones *********************************/
		FcnSolicitudes	= new Class_Solicitudes(this,this.FolderAplicacion);
		SolicitudesSQL	= new SQLite(this,FolderAplicacion);
		SolicitudesUtil = new Util();
		
		//Adapter para los nodos	
		this.Nodos = FcnSolicitudes.getNodosSolicitudes();
		this.AdaptadorNodos = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,this.Nodos);
		this.ListaNodos = (Spinner) findViewById(R.id.CmbNodoSolicitud);
		this.ListaNodos.setAdapter(this.AdaptadorNodos);
		
		//AdaptadorListaTrabajo adapter = new AdaptadorListaTrabajo(this,ArraySolicitudes);
		AdaptadorSolicitudes = new AdaptadorListaTrabajo(this,ArraySolicitudes);
		ListaSolicitudes = (ListView) findViewById(R.id.LstSolicitudes);
		ListaSolicitudes.setAdapter(AdaptadorSolicitudes);
		
		_txtMunicipio 		= (TextView) findViewById(R.id.SolicitudesTxtMunicipio);
		_txtSerie		= (TextView) findViewById(R.id.TxtSerie);
		_txtDireccion	= (TextView) findViewById(R.id.TxtDireccion);
		_txtCuenta		= (TextView) findViewById(R.id.SolicitudesTxtCuenta); 
		_txtIdSerial	= (TextView) findViewById(R.id.SolicitudesTxtIdSerial);
		_txtProceso		= (TextView) findViewById(R.id.SolicitudesTxtIdProceso); 
		_txtTipo		= (TextView) findViewById(R.id.SolicitudesTxtTipo);
		
		_txtMunicipio.setEnabled(false);
		_txtSerie.setEnabled(false);
		_txtDireccion.setEnabled(false);
		_txtCuenta.setEnabled(false);
		_txtIdSerial.setEnabled(false);
		_txtProceso.setEnabled(false);
		_txtTipo.setEnabled(false);
		
		ListaNodos.setOnItemSelectedListener(this);
		ListaSolicitudes.setOnItemClickListener(this);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_solicitudes, menu);
		return true;
	}
	
	
	@Override
	 public boolean onPrepareOptionsMenu(Menu menu) {
		if(this.enabledMenu){
			menu.findItem(R.id.InicioOrden).setEnabled(true);
			menu.findItem(R.id.AperturaOrdenes).setEnabled(true);
			menu.findItem(R.id.CerrarOrden).setEnabled(true);
			menu.findItem(R.id.TrasladarOrden).setEnabled(true);
		}else{
			menu.findItem(R.id.InicioOrden).setEnabled(false);
			menu.findItem(R.id.AperturaOrdenes).setEnabled(false);
			menu.findItem(R.id.CerrarOrden).setEnabled(false);
			menu.findItem(R.id.TrasladarOrden).setEnabled(false);
		}    	
		return true;  
	}
	
		
	//Control de eventos al momento de seleccionar una opcion del menu del formulario
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {				
			case R.id.InicioOrden:
				/***********************Validacion de si existe una solicitud previamente abierta*****************/
				if(FcnSolicitudes.IniciarOrden(_txtIdSerial.getText().toString(), _txtTipo.getText().toString().substring(0, 1))){
					DialogConfirmacion.putExtra("informacion", "Desea Iniciar La Orden "+ _txtIdSerial.getText().toString());
					startActivityForResult(DialogConfirmacion, CONFIRMACION_INICIO_ORDEN);
				}else{
					DialogInformacion.putExtra("informacion", "No se puede iniciar la actividad, comprobar:\n->Que no exista otra orden abierta\n->Que la orden seleccionada no este en estado 'TERMINADA'");
					startActivityForResult(DialogInformacion, CONFIRMACION_INFORMACION);
				}
				return true;
			
			
			case R.id.CerrarOrden:
				if(!_txtIdSerial.getText().toString().isEmpty()){
					if(FcnSolicitudes.getEstadoOrden(_txtIdSerial.getText().toString()).equals("E")){
						DialogConfirmacion.putExtra("informacion", "Desea Cerrar La Orden "+_txtIdSerial.getText().toString());
						startActivityForResult(DialogConfirmacion, CONFIRMACION_CERRAR_ORDEN);	
					}
				}else{
					DialogInformacion.putExtra("informacion","No ha seleccionado una orden para cerrar.");
					startActivityForResult(DialogInformacion, CONFIRMACION_INFORMACION);
				}
				return true;
			
			case R.id.AperturaOrdenes:
				if(FcnSolicitudes.IniciarAperturaOrden(_txtIdSerial.getText().toString())){
					DialogApertura.putExtra("titulo","INGRESE EL CODIGO DE APERTURA");
					DialogApertura.putExtra("lbl1", "Codigo:");
					DialogApertura.putExtra("txt1", "");
					startActivityForResult(DialogApertura, CONFIRMACION_COD_APERTURA);
				}else{
					DialogInformacion.putExtra("informacion", "No es posible abrir la orden "+_txtIdSerial.getText().toString()+", verifique que:\n->Este en estado terminada.\n->No exista otra orden abierta.");
					startActivityForResult(DialogInformacion, CONFIRMACION_INFORMACION);
				}
				return true;
			
				
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	
	public void CargarOrdenesTrabajo(){
		ArraySolicitudes.clear();
		if(ListaNodos.getSelectedItem().toString().equals("Todos")){
			TablaQuery = SolicitudesSQL.SelectData("amd_ordenes_trabajo", "direccion,cuenta,serie,estado", "estado in ('P','E','T','TA') ORDER BY cuenta");
			for(int i=0;i<TablaQuery.size();i++){
				RegistroQuery = TablaQuery.get(i);
				ArraySolicitudes.add(new InformacionSolicitudes(RegistroQuery.getAsString("cuenta"),RegistroQuery.getAsString("serie"),RegistroQuery.getAsString("direccion"),RegistroQuery.getAsString("estado")));
			}
		}else{
			TablaQuery = SolicitudesSQL.SelectData("amd_ordenes_trabajo", "direccion,cuenta,serie,estado", "municipio ='"+ListaNodos.getSelectedItem().toString()+"' AND estado in ('P','E','T','TA') ORDER BY cuenta");
			for(int i=0;i<TablaQuery.size();i++){
				RegistroQuery = TablaQuery.get(i);
				ArraySolicitudes.add(new InformacionSolicitudes(RegistroQuery.getAsString("cuenta"),RegistroQuery.getAsString("serie"),RegistroQuery.getAsString("direccion"),RegistroQuery.getAsString("estado")));
			}
		}
		AdaptadorSolicitudes.notifyDataSetChanged();
	}
	
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK && requestCode == CONFIRMACION_INICIO_ORDEN) {
			if(data.getExtras().getBoolean("accion")){
				/**procedimiento para generar el numero del acta y guardarlo en la base de datos**/
				_tempRegistro.put("estado", "E");
				SolicitudesSQL.UpdateRegistro("amd_ordenes_trabajo", _tempRegistro, "id_serial='"+_txtIdSerial.getText().toString()+"' AND id_tipo_archivo='"+_txtTipo.getText().toString().substring(0,1)+"'");
				/**Fin de la generacion del numero de acta y actualizacion en la base de datos**/
				
				finish();
				Intent k;
				k = new Intent(this, Form_Actas.class);
				k.putExtra("NombreUsuario", NombreUsuario);
				k.putExtra("CedulaUsuario", CedulaUsuario);
				k.putExtra("NivelUsuario", NivelUsuario);
				k.putExtra("OrdenTrabajo",_txtIdSerial.getText().toString());
				k.putExtra("CuentaCliente", _txtCuenta.getText().toString());
				k.putExtra("FolderAplicacion", this.FolderAplicacion);
				startActivity(k);
			}
		}else if(resultCode == RESULT_OK && requestCode == CONFIRMACION_CERRAR_ORDEN){
			if(data.getExtras().getBoolean("accion")){
				_tempRegistro.clear();
				_tempRegistro.put("estado","T");
				SolicitudesSQL.UpdateRegistro("amd_ordenes_trabajo", _tempRegistro, "id_orden='"+_txtIdSerial.getText().toString()+"'");	
				CargarOrdenesTrabajo();
			}			
		}else if(resultCode == RESULT_OK && requestCode == CONFIRMACION_COD_APERTURA){
			if(data.getExtras().getBoolean("response")){
				if(FcnSolicitudes.verificarCodigoApertura(_txtIdSerial.getText().toString(),data.getExtras().getString("txt1"))){
					_tempRegistro.clear();
					_tempRegistro.put("estado","E");
					SolicitudesSQL.UpdateRegistro("amd_ordenes_trabajo", _tempRegistro, "id_orden='"+_txtIdSerial.getText().toString()+"'");	
					CargarOrdenesTrabajo();
				}else{
					DialogInformacion.putExtra("informacion", "Codigo De Apertura Erroneo");
					startActivityForResult(DialogInformacion, CONFIRMACION_INFORMACION);
				}
			}			
		}
    }


	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		switch(parent.getId()){
			case R.id.CmbNodoSolicitud:
				CargarOrdenesTrabajo();
				break;
		}
	}


	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
		switch(parent.getId()){
			case R.id.LstSolicitudes:
				RegistroQuery 	= SolicitudesSQL.SelectDataRegistro("amd_ordenes_trabajo", 
																	"id_serial,id_tipo_archivo,id_proceso,ciclo,cuenta,direccion,serie,municipio", 
																	"cuenta='"+ArraySolicitudes.get(position).getSolicitud()+"'");
				
				_txtMunicipio.setText(RegistroQuery.get("municipio").toString());
				_txtSerie.setText(RegistroQuery.get("serie").toString());
				_txtDireccion.setText(RegistroQuery.get("direccion").toString());
				_txtCuenta.setText(RegistroQuery.get("cuenta").toString());
				_txtIdSerial.setText(RegistroQuery.get("id_serial").toString());
					
				if(RegistroQuery.get("id_proceso").toString().equals("E")){
					_txtProceso.setText("ENERGIA");
				}else if(RegistroQuery.get("id_proceso").toString().equals("G")){
					_txtProceso.setText("GAS");
				}
					
				if(RegistroQuery.get("id_tipo_archivo").toString().equals("S")){
					_txtTipo.setText("Suspension");
				}else if(RegistroQuery.get("id_tipo_archivo").toString().equals("C")){
					_txtTipo.setText("Corte");
				}else if(RegistroQuery.get("id_tipo_archivo").toString().equals("R")){
					_txtTipo.setText("Reconexion");
				}
					
				this.enabledMenu = true;
				invalidateOptionsMenu(); 
				break;
		}		
	}
}
