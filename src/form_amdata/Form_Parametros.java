package form_amdata;

import java.util.ArrayList;

import sistema.Bluetooth;
import sypelc.androidamdata.R;
import miscelanea.ConvertTypes;
import miscelanea.SQLite;
import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class Form_Parametros extends Activity implements OnClickListener{
	ConvertTypes Misc = new ConvertTypes();
	Bluetooth MB = new Bluetooth(this);
	SQLite SQL;
	
	ArrayList<ArrayList<String>> InfParametros;
	ArrayAdapter<String> AdapLstImpresoras;
	
	private String			FolderAplicacion = "";
	private String 			NivelUsuario = null;
	private Button 			BtnGuardar;
	private String[] 		_listaImpresoras = Misc.ArrayListToArrayString(MB.GetDeviceBluetooth());
	private ContentValues 	Informacion = new ContentValues();
	
	Spinner _impresora;
	EditText _pda, _ip_servidor, _puerto, _servicio, _web_service, _revision,_codigo_apertura;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_parametros);
		
		Bundle bundle			= getIntent().getExtras();
		NivelUsuario			= bundle.getString("NivelUsuario");
		this.FolderAplicacion	= bundle.getString("FolderAplicacion");	
				
		//Apertura, consulta y cierre de los parametros del sistema
		SQL = new SQLite(getApplicationContext(),this.FolderAplicacion);
		
		_pda 			= (EditText) findViewById(R.id.TxtParametrosPDA);
		_ip_servidor 	= (EditText) findViewById(R.id.TxtParametrosIpServidor);
		_puerto 		= (EditText) findViewById(R.id.TxtParametrosPuerto);
		_servicio 		= (EditText) findViewById(R.id.TxtParametrosServicio);
		_web_service	= (EditText) findViewById(R.id.TxtParametrosWS);		
		_impresora 		= (Spinner) findViewById(R.id.CmbParametrosImpresoras);
		
		
		AdapLstImpresoras 	= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,_listaImpresoras);
		_impresora.setAdapter(AdapLstImpresoras);
		
		_pda.setText(SQL.StrSelectShieldWhere("db_parametros", "valor", "item='NPDA'"));
				
		_ip_servidor.setText(SQL.StrSelectShieldWhere("db_parametros", "valor", "item='servidor'"));
		_puerto.setText(SQL.StrSelectShieldWhere("db_parametros", "valor", "item='puerto'"));
		_servicio.setText(SQL.StrSelectShieldWhere("db_parametros", "valor", "item='modulo'"));
		_web_service.setText(SQL.StrSelectShieldWhere("db_parametros", "valor", "item='web_service'"));
		
		
		if (NivelUsuario.equals("A")){
			_pda.setEnabled(true);
			_ip_servidor.setEnabled(true);
			_puerto.setEnabled(true);
			_servicio.setEnabled(true);
		}else{
			_pda.setEnabled(false);
			_ip_servidor.setEnabled(false);
			_puerto.setEnabled(false);
			_servicio.setEnabled(false);	
		}
		BtnGuardar = (Button) findViewById(R.id.BtnParametrosGuardar);
		
		BtnGuardar.setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.BtnParametrosGuardar:
				this.Informacion.clear();
            	Informacion.put("valor", _pda.getText().toString());
            	SQL.UpdateRegistro("db_parametros", Informacion, "item='NPDA'");
            	           	
            	Informacion.clear();
            	Informacion.put("valor", _impresora.getSelectedItem().toString());
            	SQL.UpdateRegistro("db_parametros", Informacion, "item='impresora'");      	
            	
            	Informacion.clear();
            	Informacion.put("valor", _ip_servidor.getText().toString());
            	SQL.UpdateRegistro("db_parametros", Informacion, "item='servidor'");
            	
            	Informacion.clear();
            	Informacion.put("valor", _puerto.getText().toString());
            	SQL.UpdateRegistro("db_parametros", Informacion, "item='puerto'");
            	
            	Informacion.clear();
            	Informacion.put("valor", _servicio.getText().toString());
            	SQL.UpdateRegistro("db_parametros", Informacion, "item='servicio'");
            	          	
            	Informacion.clear();
            	Informacion.put("valor", _web_service.getText().toString());
            	SQL.UpdateRegistro("db_parametros", Informacion, "item='web_service'");
            	Toast.makeText(getApplicationContext(),"Parametros Actualizados.", Toast.LENGTH_SHORT).show();    
				break;
		}
	}
}