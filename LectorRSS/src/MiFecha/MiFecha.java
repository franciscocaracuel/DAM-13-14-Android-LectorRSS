package MiFecha;

/*******************************************************************************/
/**************************************MiFecha**********************************/
/*******************************************************************************/
/***Clase hecha por Francisco J. Caracuel para facilitar el uso de las fechas***/
/*******************************************************************************/

import java.io.File;
import java.util.GregorianCalendar;

public class MiFecha {
		
	private GregorianCalendar fecha;
	
	public MiFecha(){
		fecha=new GregorianCalendar();
	}
	
	public String getDia(){
		
		if(fecha.get(GregorianCalendar.DAY_OF_MONTH)<10){
			return "0"+fecha.get(GregorianCalendar.DAY_OF_MONTH);
		} else{
			return fecha.get(GregorianCalendar.DAY_OF_MONTH)+"";
		}

	}
	
	public String getMes(){
		
		if((fecha.get(GregorianCalendar.MONTH)+1)<10){
			return "0"+(fecha.get(GregorianCalendar.MONTH)+1);
		} else{
			return fecha.get(GregorianCalendar.MONTH)+1+"";
		}
		
	}
	
	public String getAño(){
		return fecha.get(GregorianCalendar.YEAR)+"";
	}
	
	public String getHora(){
		
		if(fecha.get(GregorianCalendar.HOUR_OF_DAY)<10){
			return "0"+fecha.get(GregorianCalendar.HOUR_OF_DAY);
		} else{
			return fecha.get(GregorianCalendar.HOUR_OF_DAY)+"";
		}
	
	}
		
	public String getMinuto(){
		
		if(fecha.get(GregorianCalendar.MINUTE)<10){
			return "0"+fecha.get(GregorianCalendar.MINUTE);
		} else{
			return fecha.get(GregorianCalendar.MINUTE)+"";
		}
		
	}
	
	public String getSegundos(){
		
		if(fecha.get(GregorianCalendar.SECOND)<10){
			return "0"+fecha.get(GregorianCalendar.SECOND);
		} else{
			return fecha.get(GregorianCalendar.SECOND)+"";
		}
		
	}
	
	public String getFechaFormateada(){
		return getDia()+"/"+getMes()+"/"+getAño();
	}
	
	public String getHoraFormateada(){
		return getHora()+":"+getMinuto()+":"+getSegundos();
	}
	
	public String getFechaCompletaFormateada(){
		
		return getFechaFormateada()+"_"+getHoraFormateada();
				
	}
	
	public String getFechaFormateadaFromFile(File archivo){
		
		fecha.setTimeInMillis(archivo.lastModified());
		
		return getFechaCompletaFormateada();
		
	}

}
