package form_amdata;

import java.util.ArrayList;

import class_amdata.Class_Sellos;
import sypelc.androidamdata.R;
import dialogos.DialogSingleTxt;
import miscelanea.SQLite;
import miscelanea.Tablas;
import miscelanea.Util;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

public class Form_Sellos extends Activity implements OnClickListener, OnItemSelectedListener{
	private static int 		CONFIRM_SELLOS = 1;
	Intent 					DialogoSimple; 
	
	SQLite 				SellosSQL;
	Util   				SellosUtil;
	Tablas				GraphSellosTabla;
	
	private Class_Sellos FcnSellos;
	
	//String CamposDialogoSellos[] = {"Serie", "-1","Confirmacion de Sellos"};
	
	private TableLayout		TablaSellos;
	private LinearLayout 	FilaTablaSellos;
	
	private String 	NombreUsuario	= "";
	private String  CedulaUsuario	= "";
	private String 	NivelUsuario	= "";
	private String 	OrdenTrabajo 	= "";
	private String 	CuentaCliente 	= "";
	private String 	FolderAplicacion= "";
	
	ContentValues 			_tempRegistro 	= new ContentValues();
	ArrayList<ContentValues>_tempTabla 		= new ArrayList<ContentValues>();	
		
	private String _strTipoMovimiento[] = {"...","RETIRADO","INSTALADO"};
	private String _strTipoSello[] 		= {"...","ADHESIVO","ANCLA"};
		
	ArrayAdapter<String> AdaptadorTipoMovimiento;
		
	ArrayList<String> strTipoSello 			= new ArrayList<String>();
	ArrayAdapter<String> AdaptadorTipoSello;
	
	
	EditText	_txtSerie, _txtObservacion;
	Spinner 	_cmbTipoMovimiento, _cmbTipoSello;
	Button		_btnRegistrarSello, _btnEliminarSello, _btnGuardarObservacion;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sellos);
		
		Bundle bundle 	= getIntent().getExtras();
		NombreUsuario	= bundle.getString("NombreUsuario");
		CedulaUsuario	= bundle.getString("CedulaUsuario");
		NivelUsuario	= bundle.getString("NivelUsuario");
		OrdenTrabajo	= bundle.getString("OrdenTrabajo");
		CuentaCliente 	= bundle.getString("CuentaCliente");		
		FolderAplicacion= bundle.getString("FolderAplicacion");
		
		SellosSQL 		= new SQLite(this, FolderAplicacion);
		SellosUtil 		= new Util();
		
		DialogoSimple 	= new Intent(this,DialogSingleTxt.class); 
		FcnSellos		= new Class_Sellos(this, this.FolderAplicacion, this.CedulaUsuario, this.OrdenTrabajo, this.CuentaCliente);
		
		_txtSerie			= (EditText) findViewById(R.id.SellosTxtSerie);	
		_txtObservacion		= (EditText) findViewById(R.id.SellosTxtObservacion);
		
		_cmbTipoMovimiento 	= (Spinner) findViewById(R.id.SellosCmbTipoIngreso);
		_cmbTipoSello		= (Spinner) findViewById(R.id.SellosCmbTipoSello);
				
		_btnRegistrarSello 		= (Button) findViewById(R.id.SellosBtnRegistrar);
		_btnEliminarSello		= (Button) findViewById(R.id.SellosBtnEliminar);
		_btnGuardarObservacion	= (Button) findViewById(R.id.SellosBtnGuardarObservacion);
		
		AdaptadorTipoMovimiento = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_strTipoMovimiento);
		_cmbTipoMovimiento.setAdapter(AdaptadorTipoMovimiento);
		
		AdaptadorTipoSello 	= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, _strTipoSello); 
		_cmbTipoSello.setAdapter(AdaptadorTipoSello);
		
		GraphSellosTabla= new Tablas(this, "tipo_ingreso,tipo_sello,serie", "165,165,165,165,165,415", 1, "#74BBEE", "#A9CFEA" ,"#EE7474");
		FilaTablaSellos	= (LinearLayout) findViewById(R.id.TablaSellos);
		
		_txtObservacion.setText(SellosSQL.StrSelectShieldWhere("amd_ordenes_trabajo", "dig_observacion_sellos",  "id_serial="+this.OrdenTrabajo+" AND cuenta="+this.CuentaCliente));
		
		
		this.VerSellosRegistrados();
		_btnEliminarSello.setOnClickListener(this);
		_btnRegistrarSello.setOnClickListener(this);
		_btnGuardarObservacion.setOnClickListener(this);
		_cmbTipoMovimiento.setOnItemSelectedListener(this);		
	
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_sellos, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent k;
		switch (item.getItemId()) {	
			case R.id.Acta:
				finish();
				k = new Intent(this, Form_Actas.class);
				k.putExtra("NombreUsuario", this.NombreUsuario);
				k.putExtra("CedulaUsuario", CedulaUsuario);
				k.putExtra("Nivel", NivelUsuario);
				k.putExtra("OrdenTrabajo", OrdenTrabajo);
				k.putExtra("CuentaCliente",CuentaCliente);
				k.putExtra("FolderAplicacion", this.FolderAplicacion);
				startActivity(k);
				return true;
				
			default:
				return super.onOptionsItemSelected(item);
		}
	}


	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		switch(parent.getId()){
			case R.id.SellosCmbTipoIngreso:
				/*if(_cmbTipoMovimiento.getSelectedItem().toString().equals("INSTALADO")){
					_cmbEstado.setEnabled(false);
					_cmbEstado.setSelection(AdaptadorEstado.getPosition("N-NORMAL"));
				}else{
					_cmbEstado.setEnabled(true);
				}*/
				break;				
		}		
	}


	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.SellosBtnRegistrar:
				DialogoSimple.putExtra("titulo", "CONFIRMACION DE LA SERIE DEL SELLO");
				DialogoSimple.putExtra("lbl1","Serie:");
				DialogoSimple.putExtra("txt1","");
		        startActivityForResult(DialogoSimple, CONFIRM_SELLOS);
				break;
				
			case R.id.SellosBtnEliminar:
				if(!this.FcnSellos.eliminarSello(_cmbTipoMovimiento.getSelectedItem().toString(), _cmbTipoSello.getSelectedItem().toString(), _txtSerie.getText().toString())){
					Toast.makeText(getApplicationContext(),"Error al tratar de eliminar el sello.", Toast.LENGTH_SHORT).show();
				}
				this.VerSellosRegistrados();
				break;
				
			case R.id.SellosBtnGuardarObservacion:
				this._tempRegistro.clear();
				this._tempRegistro.put("dig_observacion_sellos",_txtObservacion.getText().toString());
				if(SellosSQL.UpdateRegistro("amd_ordenes_trabajo", _tempRegistro, "id_serial="+this.OrdenTrabajo+" AND cuenta="+this.CuentaCliente)){
					Toast.makeText(getApplicationContext(),"Observacion de sellos registrada correctamente..", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(getApplicationContext(),"Error al registrar la observacion de sellos.", Toast.LENGTH_SHORT).show();
				}
				break;
		}		
	}

	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try{
			if (resultCode == RESULT_OK && requestCode == CONFIRM_SELLOS) {
				if(data.getExtras().getBoolean("response")){
					if(FcnSellos.getConfirmacionSerie(_txtSerie.getText().toString(), data.getExtras().getString("txt1"))){
						this.FcnSellos.registrarSello(_cmbTipoMovimiento.getSelectedItem().toString(), _cmbTipoSello.getSelectedItem().toString(), _txtSerie.getText().toString());
						this.VerSellosRegistrados();
					}
				}
			}
		}catch(Exception e){
			
		}
    }
	
	private void VerSellosRegistrados(){
		TablaSellos 	= GraphSellosTabla.CuerpoTabla(FcnSellos.getSellosRegistrados());
		FilaTablaSellos.removeAllViews();
		FilaTablaSellos.addView(TablaSellos);
	}
}
