package miscelanea;


import impresora.zebra_QL420;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

import sistema.Bluetooth;
import miscelanea.SQLite;
import android.content.ContentValues;
import android.content.Context;
//import android.widget.Toast;
import android.widget.Toast;
	
public class FormatosActas {
	private Context 					context;
	private String 						_folderAplicacion;
	private ArrayList<ContentValues> 	_infTabla		= new ArrayList<ContentValues>();
	private ContentValues 				_infRegistro1	= new ContentValues();
	private ContentValues				_infRegistro2	= new ContentValues();
	private String 						_tempStr1[];
	private String 						_tempStr2[];
	
	private Archivos 			ImpArchivos;
	private Bluetooth 			MnBt;
	private SQLite 				ImpSQL;
	private DateTime 			DT;
	private zebra_QL420			FcnZebra;
	
	private String Impresora = null;
	private boolean _copiaSistema;
	   
    DecimalFormat decimales = new DecimalFormat("0.000"); 
	
	public FormatosActas(Context context, String FolderAplicacion, boolean _copiaSistema){
		this.context = context;
		this._folderAplicacion 	= FolderAplicacion;
		this._copiaSistema 		= _copiaSistema;
		
		ImpSQL = new SQLite(this.context,this._folderAplicacion);
		this.ImpArchivos = new Archivos(this.context,this._folderAplicacion, 10);
		
		this.Impresora 			= ImpSQL.StrSelectShieldWhere("db_parametros", "valor", "item='impresora'");
	    
		MnBt 	= new Bluetooth(this.context);
		ImpSQL 	= new SQLite(this.context, this._folderAplicacion);
		DT 		= new DateTime();
		FcnZebra= new zebra_QL420(590, 40, 0, 10, 80, _copiaSistema);
	}
	
	
	/********************************************************************************************************************
	 * 
	 * @param ordenTrabajo
	 * @param tipoImpresion
	 * @param copiaImpresion
	 * @param CedulaTecnico
	 *******************************************************************************************************************/
	public void FormatoVerificacion(String idSerial, String cuentaCliente, int copiaImpresion){	
		FcnZebra.clearInformacion();
		FcnZebra.DrawImage("LOGOSINS.PCX", 0, 10);
		FcnZebra.DrawImage("LOGOSYPE.PCX", 370, 0);		
		FcnZebra.WrTitulo("ENERCA S.A. E.S.P.", 2, 0);
		FcnZebra.WrTitulo("ACTA DE SUSPENSION, CORTE Y RECONEXION", 2, 2);
		 
		 
		 
		/********************************Validaciones necesarias para el encabezado del acta**************************************/
		FcnZebra.WrLabel("Acta No.  ",ImpSQL.StrSelectShieldWhere("db_parametros", "valor", "item='NPDA'") +ImpSQL.StrSelectShieldWhere("amd_ordenes_trabajo", "acta", "id_serial="+idSerial+" AND cuenta="+cuentaCliente), 200, 0, 1.2);
		
		
		this._infRegistro1 = ImpSQL.SelectDataRegistro(	"amd_ordenes_trabajo", 
														"ciclo, cuenta, usuario, direccion, serie, municipio, dig_fecha_visita, dig_hora_visita," +
														"dig_lectura_actual, dig_codigo_accion, dig_material_retirado, dig_num_canuelas, dig_tipo_acometida," +
														"dig_estado_acometida, dig_longitud_acometida, dig_calibre_acometida, dig_color_acometida, dig_pin_corte," +
														"dig_otros, dig_cancelada_factura, dig_fecha_pago_factura, dig_monto_factura, dig_entidad_factura," +
														"dig_observacion, dig_observacion_sellos, dig_nombre_usuario, dig_ident_usuario, dig_telefono", 
														"id_serial="+idSerial+" AND cuenta="+cuentaCliente);		 
		
		/**Datos inicio visita**/
		FcnZebra.WrLabel("Fecha: ", this._infRegistro1.getAsString("dig_fecha_visita"), 10, 0, 0);
		FcnZebra.WrLabel("Hora: ", this._infRegistro1.getAsString("dig_hora_visita"), 300, 0, 1);
		
		
		//FcnZebra.WrLabel("Lectura: ", this._infRegistro1.getAsString("dig_lectura_actual"), 400, 0, 1);
		
		/**Informacion general del cliente**/
		FcnZebra.WrSubTitulo("DATOS GENERALES DEL SUSCRIPTOR",10,1,1);
		FcnZebra.WrLabel("Cuenta:   ", this._infRegistro1.getAsString("cuenta"), 10, 0, 0);
		FcnZebra.WrLabel("Ciclo:   	", this._infRegistro1.getAsString("ciclo"), 300, 0, 1);
		
		FcnZebra.WrLabel("Medidor No:", this._infRegistro1.getAsString("serie"), 10, 0, 0);
		FcnZebra.WrLabel("Lectura: ", this._infRegistro1.getAsString("dig_lectura_actual"), 300, 0, 1);
		
		FcnZebra.WrLabel("Usuario:  ", this._infRegistro1.getAsString("usuario"), 10, 0, 1);
		FcnZebra.WrLabel("Direccion:", this._infRegistro1.getAsString("direccion"), 10, 0, 1);
		FcnZebra.WrLabel("Municipio:", this._infRegistro1.getAsString("municipio"), 10, 0, 2);
		
		FcnZebra.JustInformation("ESTIMADO CLIENTE: De conformidad en lo establecido en la ley 142 de 1994 y en el contrato de prestacion de servicio publicos con condiciones uniformes vigentes, personal autorizado por ENERCA S.A. E.S.P. procedio a:", 10, 0, 1);
		FcnZebra.WrLabel("Cod. Accion:", this._infRegistro1.getAsString("dig_codigo_accion"), 10, 1, 2);
		
		/**Sellos retirados e instalados**/
		if(ImpSQL.ExistRegistros("amd_sellos_ordenes", "id_serial="+idSerial+" AND cuenta="+cuentaCliente)){
			this._infTabla = ImpSQL.SelectData(	"amd_sellos_ordenes", 
												"movimiento,tipo_sello,serie", 
												"id_serial="+idSerial+" AND cuenta="+cuentaCliente+ " ORDER BY movimiento,tipo_sello,serie");
			
			FcnZebra.WrLabel("MOVIMIENTO","", 10, 0, 0);
			FcnZebra.WrLabel("TIPO","", 200, 0, 0);
			FcnZebra.WrLabel("SERIE","", 400, 0, 1);
			
			for(int i=0; i<this._infTabla.size();i++){
				FcnZebra.WrLabel("",this._infTabla.get(i).getAsString("movimiento"), 10, 0, 0);
				FcnZebra.WrLabel("",this._infTabla.get(i).getAsString("tipo_sello"), 200, 0, 0);
				FcnZebra.WrLabel("",this._infTabla.get(i).getAsString("serie"), 400, 0, 1);
			}			
		}
		
		/**Materiales retirados**/
		if(this._infRegistro1.getAsBoolean("dig_material_retirado")){
			FcnZebra.WrLabel("Se retira material","Si(x)   No( )", 10, 1, 1);
		}else{
			FcnZebra.WrLabel("Se retira material","Si( )   No(x)", 10, 1, 1);
		}
		FcnZebra.WrLabel("No Canuelas",this._infRegistro1.getAsString("dig_num_canuelas"), 10, 0, 1);
		
		
		/**Pin de corte**/
		if(this._infRegistro1.getAsBoolean("dig_pin_corte")){
			FcnZebra.WrLabel("Pin Corte","Si(x)   No( )", 10, 0, 1);
		}else{
			FcnZebra.WrLabel("Pin Corte","Si( )   No(x)", 10, 0, 1);
		}
		FcnZebra.WrLabel("Otros",this._infRegistro1.getAsString("dig_otros"), 10, 0, 1);
		
		
		/**Acometida**/
		FcnZebra.WrSubTitulo("ACOMETIDA",10,1,1);
		FcnZebra.WrLabel("Tipo:",this._infRegistro1.getAsString("dig_tipo_acometida"), 10, 0, 0);
		FcnZebra.WrLabel("Estado:",this._infRegistro1.getAsString("dig_estado_acometida"), 200, 0, 0);
		FcnZebra.WrLabel("Longitud:",this._infRegistro1.getAsString("dig_longitud_acometida"), 400, 0, 1);
		FcnZebra.WrLabel("Calibre:",this._infRegistro1.getAsString("dig_calibre_acometida"), 10, 0, 0);
		FcnZebra.WrLabel("Color:",this._infRegistro1.getAsString("dig_color_acometida"), 200, 0, 1);
		
		/**Datos factura cancelada**/
		FcnZebra.WrSubTitulo("FACTURA",10,1,1);
		if(this._infRegistro1.getAsBoolean("dig_cancelada_factura")){
			FcnZebra.WrLabel("Factura Cancelada","Si(x)   No( )", 10, 0, 1);
			FcnZebra.WrLabel("Fecha Pago:",this._infRegistro1.getAsString("dig_fecha_pago_factura"), 10, 0, 1);
			FcnZebra.WrLabel("Monto Cancelado",this._infRegistro1.getAsString("dig_monto_factura"), 10, 0, 1);
			FcnZebra.WrLabel("Entidad:",this._infRegistro1.getAsString("dig_entidad_factura"), 10, 0, 1);
		}else{
			FcnZebra.WrLabel("Factura Cancelada","Si( )   No(x)", 10, 0, 1);
			FcnZebra.WrLabel("Fecha Pago:","--", 10, 0, 1);
			FcnZebra.WrLabel("Monto Cancelado","--", 10, 0, 1);
			FcnZebra.WrLabel("Entidad:","--", 10, 0, 1);
		}
		
		/**Datos observaciones**/
		FcnZebra.WrSubTitulo("OBSERVACIONES",10,1,1);
		if(this._infRegistro1.getAsString("dig_observacion") != null){
			FcnZebra.JustInformation(this._infRegistro1.getAsString("dig_observacion"), 10, 0, 2);
		}
		
		if(this._infRegistro1.getAsString("dig_observacion_sellos") != null){
			FcnZebra.JustInformation(this._infRegistro1.getAsString("dig_observacion_sellos"), 10, 0, 2);
		}
			
		/**Datos de quien atendio la visita**/
		FcnZebra.WrSubTitulo("ATENCION VISITA",10,1,5);
		FcnZebra.WrRectangle(10, FcnZebra.getCurrentLine(), 600, FcnZebra.getCurrentLine()+5, 1, 0);
		FcnZebra.WrLabel(this._infRegistro1.getAsString("dig_nombre_usuario"),"", 10, 0, 1);
		FcnZebra.WrLabel("CC.", this._infRegistro1.getAsString("dig_ident_usuario"), 10, 0, 1);
		FcnZebra.WrLabel("Firma Usuario.", "", 10, 0, 5);		
		
		FcnZebra.WrRectangle(10, FcnZebra.getCurrentLine(), 600, FcnZebra.getCurrentLine()+5, 1, 0);
		FcnZebra.WrLabel(ImpSQL.StrSelectShieldWhere("db_parametros", "valor", "item='nombre_tecnico'"),"", 10, 0, 1);
		FcnZebra.WrLabel("CC.", ImpSQL.StrSelectShieldWhere("db_parametros", "valor", "item='cedula_tecnico'"), 10, 0, 1);
		FcnZebra.WrLabel("Firma Tecnico Electricista.", "", 10, 0, 2);	
		
		if(copiaImpresion==1){
			FcnZebra.WrLabel("Impresion Original", "", 10, 0, 1);	
		}else if(copiaImpresion==2){
			FcnZebra.WrLabel("Impresion Copia Usuario", "", 10, 0, 1);	
		}else if(copiaImpresion==3){
			FcnZebra.WrLabel("Impresion Copia Archivo", "", 10, 0, 1);	
		}
		
		MnBt.IntentPrint(this.Impresora,FcnZebra.getDoLabel());
		FcnZebra.resetEtiqueta();
	}
}
