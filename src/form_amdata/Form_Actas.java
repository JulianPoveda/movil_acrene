package form_amdata;


import java.io.File;
import java.util.ArrayList;
import sypelc.androidamdata.R;
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

public class Form_Actas extends Activity implements OnClickListener, OnItemSelectedListener{
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
	private ArrayList<ContentValues> 	_tempTabla	= new ArrayList<ContentValues>();
	
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
		
		ActasSQL 	= new SQLite(this, this.FolderAplicacion);
		ActasUtil 	= new Util();
		ActaImpresa	= new FormatosActas(this, this.FolderAplicacion,true);
			
		_lblOrden 			= (TextView) findViewById(R.id.ActaLblNumOrden);
		_lblCuenta 			= (TextView) findViewById(R.id.ActaLblNumCuenta);
		_lblActa 			= (TextView) findViewById(R.id.ActaLblNumActa);
		
		_lblOrden.setText(OrdenTrabajo);
		_lblActa.setText(ActasSQL.StrSelectShieldWhere("amd_ordenes_trabajo", "num_acta", "id_orden='"+OrdenTrabajo+"'"));
		_lblCuenta.setText(CuentaCliente);	
		
		_txtNombreUsuario 	= (EditText) findViewById(R.id.ActaTxtNombreUsuario);
		_txtDocUsuario	 	= (EditText) findViewById(R.id.ActaTxtDocUsuario);
		_txtTelUsuario		= (EditText) findViewById(R.id.ActaTxtTelUsuario);
		_txtLongAcometida	= (EditText) findViewById(R.id.ActaTxtLongAcometida);
		_txtFechaPago		= (EditText) findViewById(R.id.ActaTxtFechaPago);
		_txtMontoPago		= (EditText) findViewById(R.id.ActaTxtMontoPago);
		_txtEntidadPago		= (EditText) findViewById(R.id.ActaTxtEntidadPago);
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
							
			case R.id.Volver:
				finish();
				k = new Intent(this, Form_Solicitudes.class);
				k.putExtra("NombreUsuario", this.NombreUsuario);
				k.putExtra("CedulaUsuario", CedulaUsuario);
				k.putExtra("NivelUsuario", 	NivelUsuario);
				k.putExtra("OrdenTrabajo", OrdenTrabajo);
				k.putExtra("CuentaCliente",CuentaCliente);
				k.putExtra("FolderAplicacion",  Environment.getExternalStorageDirectory() + File.separator + "Enerca");
				startActivity(k);
				return true;	
				
			case R.id.ImpresionOriginal:
				ActaImpresa.FormatoVerificacion(OrdenTrabajo,"Desviacion",1, CedulaUsuario);
				return true;
				
			case R.id.ImpresionCopia:
				ActaImpresa.FormatoVerificacion(OrdenTrabajo,"Desviacion",2, CedulaUsuario);
				return true;
				
			case R.id.ImpresionArchivo:
				ActaImpresa.FormatoVerificacion(OrdenTrabajo,"Desviacion",3, CedulaUsuario);
				return true;	
				
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
        	/*case R.id.ActaBtnAddItem:
        		if(ActasUtil.FindStringIntoArrayString(_cmbItems.getSelectedItem().toString(), _lblItemAplicados.getText().toString(), " ")==-1){
        			_lblItemAplicados.setText(_lblItemAplicados.getText()+" "+_cmbItems.getSelectedItem().toString());
        		}else{
        			Toast.makeText(this,"El item de pago ya se encuentra registrado.", Toast.LENGTH_SHORT).show();
        		}        		
        		break;
        	
        	case R.id.ActaBtnRemoveItem:
        		if(ActasUtil.FindStringIntoArrayString(_cmbItems.getSelectedItem().toString(), _lblItemAplicados.getText().toString(), " ")>=0){
        			_lblItemAplicados.setText(ActasUtil.RemoveStringIntoArrayString(_cmbItems.getSelectedItem().toString(), _lblItemAplicados.getText().toString(), " "));
        		}else{
        			Toast.makeText(this,"El item de pago no se encuentra registrado.", Toast.LENGTH_SHORT).show();
        		}        		
        		break;
        		
        	case R.id.ActaBtnGuardar:
        		if(_lblItemAplicados.getText().length()==0){
        			Toast.makeText(this,"No ha seleccionado los items de pago a aplicar.", Toast.LENGTH_SHORT).show();
        		}else if(_cmbUbicacionMedidor.getSelectedItem().toString().equals("...")){
        			Toast.makeText(this,"No ha seleccionado la ubicacion del medidor.", Toast.LENGTH_SHORT).show();        			
        		}else if(_cmbUsoDerecho.getSelectedItem().toString().equals("...")){
        			Toast.makeText(this,"No ha seleccionado si el usuario hace uso del derecho.", Toast.LENGTH_SHORT).show();        			
        		}else{
        			
        			
        			Registro.clear();		
           			if(ActasSQL.ExistRegistros("amd_impresiones_inf", "id_orden='"+OrdenTrabajo+"'")){
        				Registro.put("diagrama", _cmbTipoMedidores.getSelectedItem().toString());
            			Registro.put("acometida", _cmbAcometidaMedidor.getSelectedItem().toString());
            			Registro.put("ubicacion", _cmbUbicacionMedidor.getSelectedItem().toString());
            			Registro.put("uso_derecho", _cmbUsoDerecho.getSelectedItem().toString());
            			Registro.put("items", _lblItemAplicados.getText().toString());
            			
        				if(ActasSQL.UpdateRegistro("amd_impresiones_inf", Registro, "id_orden='"+OrdenTrabajo+"'")){
        					Toast.makeText(this,"Registro actualizado correctamente en amd_impresiones_inf.", Toast.LENGTH_SHORT).show();
        				}else{
        					Toast.makeText(this,"Error al actualizar el registro en amd_impresiones_inf.", Toast.LENGTH_SHORT).show();
        				}
        			}else{
        				Registro.put("id_orden", OrdenTrabajo);
        				Registro.put("diagrama", _cmbTipoMedidores.getSelectedItem().toString());
            			Registro.put("acometida", _cmbAcometidaMedidor.getSelectedItem().toString());
            			Registro.put("ubicacion", _cmbUbicacionMedidor.getSelectedItem().toString());
            			Registro.put("uso_derecho", _cmbUsoDerecho.getSelectedItem().toString());
            			Registro.put("items", _lblItemAplicados.getText().toString());
            			
            			if(ActasSQL.InsertRegistro("amd_impresiones_inf", Registro)){
        					Toast.makeText(this,"Registro ingresado correctamente en amd_impresiones_inf.", Toast.LENGTH_SHORT).show();
        				}else{
        					Toast.makeText(this,"Error al ingresar el registro en amd_impresiones_inf.", Toast.LENGTH_SHORT).show();
        				}    			
            		} 
           			
           			
        			Registro.clear();    		
        			if(ActasSQL.ExistRegistros("amd_actas", "id_orden='"+OrdenTrabajo+"'")){
        				Registro.put("cedula_enterado",_txtDocEnterado.getText().toString());
        				Registro.put("nombre_enterado",_txtNombreEnterado.getText().toString());
        				Registro.put("tipo_enterado",_cmbTipoEnterado.getSelectedItem().toString());
        				Registro.put("cedula_testigo",_txtDocTestigo.getText().toString());
        				Registro.put("nombre_testigo",_txtNombreTestigo.getText().toString());
        				
        				if(ActasSQL.UpdateRegistro("amd_actas", Registro, "id_orden='"+OrdenTrabajo+"'")){
        					Toast.makeText(this,"Registro actualizado correctamente en amd_actas.", Toast.LENGTH_SHORT).show();
        				}else{
        					Toast.makeText(this,"Error al ingresar el registro en amd_actas.", Toast.LENGTH_SHORT).show();
        				}
        			}else{
        				Registro.put("id_orden",OrdenTrabajo);
        				Registro.put("id_acta",ActasSQL.IntSelectShieldWhere("amd_actas", "max(cast(id_acta AS INTEGER)) as id_acta", "id_acta IS NOT NULL")+1);
        				Registro.put("id_revision","0");
        				Registro.put("codigo_trabajo",ActasSQL.StrSelectShieldWhere("amd_param_trabajos_orden", "id_trabajo", "id_orden='"+OrdenTrabajo+"'"));
        				Registro.put("cedula_enterado",_txtDocEnterado.getText().toString());
        				Registro.put("nombre_enterado",_txtNombreEnterado.getText().toString());
        				Registro.put("evento","");
        				Registro.put("tipo_enterado",_cmbTipoEnterado.getSelectedItem().toString());
        				Registro.put("cedula_testigo",_txtDocTestigo.getText().toString());
        				Registro.put("nombre_testigo",_txtNombreTestigo.getText().toString());
        				Registro.put("estado","E");
        				Registro.put("usuario_ins",CedulaUsuario);
        				
        				if(ActasSQL.InsertRegistro("amd_actas", Registro)){
        					Toast.makeText(this,"Registro ingresado correctamente en amd_actas.", Toast.LENGTH_SHORT).show();
        				}else{
        					Toast.makeText(this,"Error al ingresar el registro en amd_actas.", Toast.LENGTH_SHORT).show();
        				}        				
        			}
        			
        			
        			if(_cmbRtaPQR.getSelectedItem() != null){
            			Registro.clear();
            			Registro.put("id_inconsistencia", ActasSQL.IntSelectShieldWhere("amd_inconsistencias","max(cast(id_inconsistencia AS INTEGER)) as id_inconsistencia","id_inconsistencia IS NOT NULL")+1);
            			Registro.put("id_orden", OrdenTrabajo);
            			Registro.put("id_nodo", ActasSQL.StrSelectShieldWhere("amd_ordenes_trabajo", "id_nodo", "id_orden='"+OrdenTrabajo+"'"));
        				Registro.put("valor", ActasSQL.StrSelectShieldWhere("vista_respuesta_pqr", "recomendacion", "id_orden='"+OrdenTrabajo+"' AND descripcion_tipo_recomendacion LIKE '%"+_cmbRtaPQR.getSelectedItem().toString()+"'"));	
        				Registro.put("cod_inconsistencia", "PQR1");
        				Registro.put("cuenta",CuentaCliente);
        				Registro.put("usuario_ins", CedulaUsuario);
        				Registro.put("tipo","A");        			
        				ActasSQL.InsertOrUpdateRegistro("amd_inconsistencias", Registro, "id_orden='"+OrdenTrabajo+"' AND cod_inconsistencia='PQR1'");		
        			}
        		}
        	default:
        		break;*/
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
}