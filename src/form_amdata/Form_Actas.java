package form_amdata;


import java.io.File;
import java.util.ArrayList;
import sypelc.androidamdata.R;
import miscelanea.DateTime;
import miscelanea.FormatosActas;
import miscelanea.SQLite;
import miscelanea.Util;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

public class Form_Actas extends Activity implements OnClickListener, OnItemSelectedListener{
	DateTime			FcnDate;
	FormatosActas 		ActaImpresa;
	SQLite 				ActasSQL;
	Util   				ActasUtil;
	
	private String		NombreUsuario	= "";
	private String		CedulaUsuario	= "";
	private String 		NivelUsuario	= "";
	private String 		OrdenTrabajo 	= "";
	private String 		CuentaCliente 	= "";
	private String 		FolderAplicacion= "";
	
	//Variable utilizada para realizar las operaciones en la base de datos 
	private ContentValues				_tempRegistro	= new ContentValues();
	private ArrayList<ContentValues> 	_tempTabla		= new ArrayList<ContentValues>();
	
	private ArrayList<String> strCodigoAccion = new ArrayList<String>();
	
	private String[] strMaterialRetirado= {"...","No","Si"};
	private String[] strTipoAcometida	= {"...","Abierta","Concentrica"};
	private String[] strEstadoAcometida = {"...","Mala","Buena"};
	private String[] strCalibreAcometida= {"...","0","2","4","6","8","10"};
	private String[] strNumeroCanuelas	= {"...","0","1","2","3"};
	private String[] strPinCorte		= {"...","No","Si"}; 
	private String[] strFacturaCancelada= {"...","No","Si"};
	
	
	//Variables para consultar los tipos de conexiones de medidores que hay y hacer el adapter respectivo
	private ArrayAdapter<String> AdaptadorCodigoAccion;
	private ArrayAdapter<String> AdaptadorMaterialRetirado;
	private ArrayAdapter<String> AdaptadorTipoAcometida;
	private ArrayAdapter<String> AdaptadorEstadoAcometida;
	private ArrayAdapter<String> AdaptadorCalibreAcometida;
	private ArrayAdapter<String> AdaptadorNumeroCanuelas;	
	private ArrayAdapter<String> AdaptadorPinCorte;
	private ArrayAdapter<String> AdaptadorFacturaCancelada;
	
	TextView 	_lblOrden, _lblActa, _lblCuenta;
	EditText	_txtLectura, _txtNombreUsuario, _txtDocUsuario, _txtTelUsuario, _txtLongAcometida, _txtOtros, _txtFechaPago, _txtMontoPago, _txtEntidadPago, _txtObservacion, _txtColorAcometida;
	Spinner 	_cmbCodigoAccion, _cmbMaterialRetirado, _cmbNumCanuelas, _cmbTipoAcometida, _cmbEstadoAcometida, _cmbCalibreAcometida, _cmbPinCorte, _cmbFacturaCancelada;
	Button 		_btnAddItem, _btnRemoveItem, _btnGuardarDatos;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_actas);
		
		Bundle bundle 	= getIntent().getExtras();
		this.NombreUsuario		= bundle.getString("NombreUsuario");
		this.CedulaUsuario		= bundle.getString("CedulaUsuario");
		this.NivelUsuario		= bundle.getString("NivelUsuario");
		this.OrdenTrabajo		= bundle.getString("OrdenTrabajo");
		this.CuentaCliente 		= bundle.getString("CuentaCliente");		
		this.FolderAplicacion	= bundle.getString("FolderAplicacion");
		
		FcnDate 	= new DateTime();
		ActasSQL 	= new SQLite(this, this.FolderAplicacion);
		ActasUtil 	= new Util();
		ActaImpresa	= new FormatosActas(this, this.FolderAplicacion,true);
			
		_lblOrden 			= (TextView) findViewById(R.id.ActaLblNumOrden);
		_lblCuenta 			= (TextView) findViewById(R.id.ActaLblNumCuenta);
		_lblActa 			= (TextView) findViewById(R.id.ActaLblNumActa);
		
		_lblOrden.setText(OrdenTrabajo);
		_lblActa.setText(ActasSQL.StrSelectShieldWhere("amd_ordenes_trabajo", "acta", "id_serial='"+OrdenTrabajo+"'"));
		_lblCuenta.setText(CuentaCliente);	
		
		_txtLectura			= (EditText) findViewById(R.id.ActasTxtLectura2);
		_txtNombreUsuario 	= (EditText) findViewById(R.id.ActaTxtNombreUsuario);
		_txtDocUsuario	 	= (EditText) findViewById(R.id.ActaTxtDocUsuario);
		_txtTelUsuario		= (EditText) findViewById(R.id.ActaTxtTelUsuario);
		_txtLongAcometida	= (EditText) findViewById(R.id.ActaTxtLongAcometida);
		_txtFechaPago		= (EditText) findViewById(R.id.ActaTxtFechaPago);
		_txtMontoPago		= (EditText) findViewById(R.id.ActaTxtMontoPago);
		_txtEntidadPago		= (EditText) findViewById(R.id.ActaTxtEntidadPago);
		_txtOtros			= (EditText) findViewById(R.id.ActaTxtOtros);
		_txtObservacion		= (EditText) findViewById(R.id.ActaTxtObservacion);
		_txtColorAcometida	= (EditText) findViewById(R.id.ActaTxtColorAcometida); 
		
		_cmbCodigoAccion	= (Spinner) findViewById(R.id.ActaCmbCodigoAccion);
		_cmbMaterialRetirado= (Spinner) findViewById(R.id.ActaCmbMaterialRetirado);
		_cmbNumCanuelas		= (Spinner) findViewById(R.id.ActaCmbNoCanuelas);
		_cmbTipoAcometida	= (Spinner) findViewById(R.id.ActaCmbTipoAcometida);
		_cmbEstadoAcometida	= (Spinner) findViewById(R.id.ActaCmbEstadoAcometida);
		_cmbCalibreAcometida= (Spinner) findViewById(R.id.ActaCmbCalibreAcometida);
		_cmbPinCorte		= (Spinner) findViewById(R.id.ActaCmbPinCorte);
		_cmbFacturaCancelada= (Spinner) findViewById(R.id.ActaCmbFacturaCancelada);
		
		_tempTabla = ActasSQL.SelectData("amd_cod_accion", "descripcion", "descripcion IS NOT NULL");
		ActasUtil.ArrayContentValuesToString(this.strCodigoAccion, this._tempTabla, "descripcion");		
		AdaptadorCodigoAccion = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,this.strCodigoAccion);
		_cmbCodigoAccion.setAdapter(AdaptadorCodigoAccion);
		
		AdaptadorMaterialRetirado= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,this.strMaterialRetirado);
		_cmbMaterialRetirado.setAdapter(AdaptadorMaterialRetirado);
		
		AdaptadorTipoAcometida= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,this.strTipoAcometida);
		_cmbTipoAcometida.setAdapter(AdaptadorTipoAcometida);
		
		AdaptadorEstadoAcometida= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,this.strEstadoAcometida);
		_cmbEstadoAcometida.setAdapter(AdaptadorEstadoAcometida);
		
		AdaptadorCalibreAcometida= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,this.strCalibreAcometida);
		_cmbCalibreAcometida.setAdapter(AdaptadorCalibreAcometida);
		
		AdaptadorNumeroCanuelas= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,this.strNumeroCanuelas);
		_cmbNumCanuelas.setAdapter(AdaptadorNumeroCanuelas);
		
		AdaptadorPinCorte = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,this.strPinCorte);
		_cmbPinCorte.setAdapter(AdaptadorPinCorte);
		
		AdaptadorFacturaCancelada= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,this.strFacturaCancelada);
		_cmbFacturaCancelada.setAdapter(AdaptadorFacturaCancelada);
		
		CargarInformacion();
		
		this._cmbFacturaCancelada.setOnItemSelectedListener(this);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_actas, menu);
		return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent k;
		switch (item.getItemId()) {		
			case R.id.Guardar:
				_tempRegistro.clear();
				_tempRegistro.put("dig_fecha_visita", FcnDate.GetFecha());
				_tempRegistro.put("dig_hora_visita", FcnDate.GetHora());
				_tempRegistro.put("dig_lectura_actual", _txtLectura.getText().toString());
				_tempRegistro.put("dig_codigo_accion", _cmbCodigoAccion.getSelectedItem().toString());					
				_tempRegistro.put("dig_num_canuelas", _cmbNumCanuelas.getSelectedItem().toString());
				_tempRegistro.put("dig_tipo_acometida", _cmbTipoAcometida.getSelectedItem().toString());
				_tempRegistro.put("dig_estado_acometida", _cmbEstadoAcometida.getSelectedItem().toString());
				_tempRegistro.put("dig_longitud_acometida", _txtLongAcometida.getText().toString());
				_tempRegistro.put("dig_calibre_acometida", _cmbCalibreAcometida.getSelectedItem().toString());
				_tempRegistro.put("dig_color_acometida", _txtColorAcometida.getText().toString());
				_tempRegistro.put("dig_otros", _txtOtros.getText().toString());		
				_tempRegistro.put("dig_observacion", _txtObservacion.getText().toString());
				_tempRegistro.put("dig_nombre_usuario", _txtNombreUsuario.getText().toString());
				_tempRegistro.put("dig_ident_usuario", _txtDocUsuario.getText().toString());
				_tempRegistro.put("dig_telefono", _txtTelUsuario.getText().toString());
				
				if(_cmbMaterialRetirado.getSelectedItem().toString().equals("Si")){
					_tempRegistro.put("dig_material_retirado","true");
				}else{
					_tempRegistro.put("dig_material_retirado", "false");
				}	
				
				if(_cmbPinCorte.getSelectedItem().toString().equals("Si")){
					_tempRegistro.put("dig_pin_corte", "true");
				}else{
					_tempRegistro.put("dig_pin_corte", "false");
				}
				
				if(_cmbFacturaCancelada.getSelectedItem().toString().equals("Si")){
					_tempRegistro.put("dig_cancelada_factura", "true");
					_tempRegistro.put("dig_fecha_pago_factura", _txtFechaPago.getText().toString());
					_tempRegistro.put("dig_monto_factura", _txtMontoPago.getText().toString());
					_tempRegistro.put("dig_entidad_factura", _txtEntidadPago.getText().toString());
				}else{
					_tempRegistro.put("dig_cancelada_factura", "false");
					_tempRegistro.put("dig_fecha_pago_factura", "");
					_tempRegistro.put("dig_monto_factura", "");
					_tempRegistro.put("dig_entidad_factura", "");
				}	
					
				
				if(this.ActasSQL.UpdateRegistro("amd_ordenes_trabajo", _tempRegistro, "id_serial="+this.OrdenTrabajo+" AND cuenta="+this.CuentaCliente)){
					Toast.makeText(this,"Informacion actualizada correctamente.", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(this,"Error al actualizar la informacion.", Toast.LENGTH_SHORT).show();
				}				
				return true;
		
			case R.id.Sellos:
				finish();
				k = new Intent(this, Form_Sellos.class);
				k.putExtra("NombreUsuario", this.NombreUsuario);
				k.putExtra("CedulaUsuario", CedulaUsuario);
				k.putExtra("Nivel", NivelUsuario);
				k.putExtra("OrdenTrabajo", OrdenTrabajo);
				k.putExtra("CuentaCliente",CuentaCliente);
				k.putExtra("FolderAplicacion", Environment.getExternalStorageDirectory() + File.separator + "Enerca");
				startActivity(k);
				return true;
				
			case R.id.ImpresionOriginal:
				ActaImpresa.FormatoVerificacion(this.OrdenTrabajo, this.CuentaCliente,1);
				return true;
				
			case R.id.ImpresionCopia:
				ActaImpresa.FormatoVerificacion(this.OrdenTrabajo, this.CuentaCliente,2);
				return true;
				
			case R.id.ImpresionArchivo:
				ActaImpresa.FormatoVerificacion(this.OrdenTrabajo, this.CuentaCliente,3);
				return true;	
				
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
      
		}		
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		switch(parent.getId()){
			case R.id.ActaCmbFacturaCancelada:
				if(this._cmbFacturaCancelada.getSelectedItem().toString().equals("Si")){
					this._txtFechaPago.setVisibility(View.VISIBLE);
					this._txtMontoPago.setVisibility(View.VISIBLE);
					this._txtEntidadPago.setVisibility(View.VISIBLE);
				}else{
					this._txtFechaPago.setVisibility(View.INVISIBLE);
					this._txtMontoPago.setVisibility(View.INVISIBLE);
					this._txtEntidadPago.setVisibility(View.INVISIBLE);
				}
				break;
			
			default:
				break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub		
	}
	
	
	public void CargarInformacion(){
		this._tempRegistro = ActasSQL.SelectDataRegistro(	"amd_ordenes_trabajo", 
															"dig_lectura_actual,dig_codigo_accion,dig_num_canuelas," +
															"dig_tipo_acometida,dig_estado_acometida,dig_longitud_acometida,dig_calibre_acometida," +
															"dig_color_acometida,dig_otros,dig_observacion,dig_nombre_usuario,dig_ident_usuario," +
															"dig_telefono,dig_material_retirado,dig_pin_corte,dig_cancelada_factura," +
															"dig_fecha_pago_factura,dig_monto_factura,dig_entidad_factura", 
															"id_serial="+this.OrdenTrabajo+" AND cuenta="+this.CuentaCliente);
		
		if(this._tempRegistro.getAsString("dig_lectura_actual") != null){
			_txtLectura.setText(this._tempRegistro.getAsString("dig_lectura_actual").toString());				
			_cmbCodigoAccion.setSelection(AdaptadorCodigoAccion.getPosition(this._tempRegistro.getAsString("dig_codigo_accion")));
			_cmbNumCanuelas.setSelection(AdaptadorNumeroCanuelas.getPosition(this._tempRegistro.getAsString("dig_num_canuelas")));
			_cmbTipoAcometida.setSelection(AdaptadorTipoAcometida.getPosition(this._tempRegistro.getAsString("dig_tipo_acometida")));
			_cmbEstadoAcometida.setSelection(AdaptadorEstadoAcometida.getPosition(this._tempRegistro.getAsString("dig_estado_acometida")));
			_txtLongAcometida.setText(this._tempRegistro.getAsString("dig_longitud_acometida").toString());
			_cmbCalibreAcometida.setSelection(AdaptadorCalibreAcometida.getPosition(this._tempRegistro.getAsString("dig_calibre_acometida")));
			_txtColorAcometida.setText(this._tempRegistro.getAsString("dig_color_acometida").toString());
			_txtOtros.setText(this._tempRegistro.getAsString("dig_otros").toString());
			_txtObservacion.setText(this._tempRegistro.getAsString("dig_observacion".toString()));
			_txtNombreUsuario.setText(this._tempRegistro.getAsString("dig_nombre_usuario").toString());
			_txtDocUsuario.setText(this._tempRegistro.getAsString("dig_ident_usuario").toString());
			_txtTelUsuario.setText(this._tempRegistro.getAsString("dig_telefono").toString());
			
			if(this._tempRegistro.getAsBoolean("dig_material_retirado")==true){
				_cmbMaterialRetirado.setSelection(AdaptadorMaterialRetirado.getPosition("Si"));
			}else if(this._tempRegistro.getAsBoolean("dig_material_retirado")==false){
				_cmbMaterialRetirado.setSelection(AdaptadorMaterialRetirado.getPosition("No"));
			}else{
				_cmbMaterialRetirado.setSelection(AdaptadorMaterialRetirado.getPosition("..."));
			}
			
			if(this._tempRegistro.getAsBoolean("dig_pin_corte")==true){
				_cmbPinCorte.setSelection(AdaptadorPinCorte.getPosition("Si"));
			}else if(this._tempRegistro.getAsBoolean("dig_pin_corte")==false){
				_cmbPinCorte.setSelection(AdaptadorPinCorte.getPosition("No"));
			}else{
				_cmbPinCorte.setSelection(AdaptadorMaterialRetirado.getPosition("..."));
			}
			
			if(this._tempRegistro.getAsBoolean("dig_cancelada_factura")==true){
				_cmbFacturaCancelada.setSelection(AdaptadorFacturaCancelada.getPosition("Si"));
			}else if(this._tempRegistro.getAsBoolean("dig_cancelada_factura")==false){
				_cmbFacturaCancelada.setSelection(AdaptadorFacturaCancelada.getPosition("No"));
			}else{
				_cmbFacturaCancelada.setSelection(AdaptadorFacturaCancelada.getPosition("..."));
			}
			
			_txtFechaPago.setText(this._tempRegistro.getAsString("dig_fecha_pago_factura").toString());
			_txtMontoPago.setText(this._tempRegistro.getAsString("dig_monto_factura").toString());
			_txtEntidadPago.setText(this._tempRegistro.getAsString("dig_entidad_factura").toString());				
		}
	}
}