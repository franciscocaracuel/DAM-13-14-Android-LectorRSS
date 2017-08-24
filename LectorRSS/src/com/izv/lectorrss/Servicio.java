package com.izv.lectorrss;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.izv.lectorrss.beans.RSS;

import MiFecha.MiFecha;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.util.Xml;
import android.widget.RemoteViews;
import android.widget.Toast;

public class Servicio extends Service{
	
	private RSS rss;
	private ArrayList<RSS> lista;

	//Este método permite ligar una actividad a un servicio. 
	//Esto permite a la activity tener acceso a los miembros y métodos en el interior del Service
	@Override
	public IBinder onBind(Intent i) {				
		return null;
	}
	
	
	/*@Override
	public void onCreate() {
		
	}*/
	
	
	//Este método es llamado cuando hacemos que el servicio se ejecute 
	//explícitamente mediante un método startService()
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		//Se inicia el hilo donde se actualiza el widget
		new HiloRSS().execute();
		Toast.makeText(this, getResources().getString(R.string.servicio_inicio), Toast.LENGTH_SHORT).show();
				
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, getResources().getString(R.string.servicio_fin), Toast.LENGTH_SHORT).show();
	}
	
	//Devuelve true si hay conexion a internet o false si no
		public boolean isConectado() {
		
			ConnectivityManager gesCon = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			
			if (gesCon != null) {
				NetworkInfo[] redes = gesCon.getAllNetworkInfo();
				if (redes != null) {
					for (int i = 0; i < redes.length; i++) {
						if (redes[i].getState() == NetworkInfo.State.CONNECTED) {
							return true;
						}
					}
				}
			}
			
			return false;
			
		}
	
	//Esta clase privada es la misma que la que esta en el MainActivity, pero se usa para resfrescar el widget
	private class HiloRSS extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			
			try {				
				
				lista=new ArrayList<RSS>();
				
				//url tendra la ruta del rss
				URL url = new URL("http://marca.feedsportal.com/rss/portada.xml");
				//URL url = new URL("http://elmundo.feedsportal.com/elmundo/rss/portada.xml");
				
				
				//lectorXml ira recorriendo todo el xml
				XmlPullParser lectorXml = Xml.newPullParser();
				lectorXml.setInput(new InputStreamReader(url.openStream()));
				
				//Devuelve el tipo de evento con el que empieza el xml.
				//Puede ser START_DOCUMENT, END_DOCUMENT, START_TAG, END_TAG, TEXT
				int evento = lectorXml.getEventType();				
								
				//Estara recorriendo el xml hasta que llega al final
				while(evento!=XmlPullParser.END_DOCUMENT){
					
					//Entrara cada vez que se encuentre con una etiqueta nueva
					if(evento==XmlPullParser.START_TAG){
						
						String etiqueta=lectorXml.getName();
						
						//Si coincide la etiqueta con tittle
						if(etiqueta.equals("title")){
							
							//Como es la primera etiqueta se inicia el objeto
							rss=new RSS();

							//Para poder leer se pasa al siguiente evento que debe ser text
							evento = lectorXml.next();
							
							//Si es text podemos leer el contenido y se guarda en el objeto
							if(evento==XmlPullParser.TEXT){
								
								rss.setTitle(lectorXml.getText());
								
							}
							
						} else if(etiqueta.equals("link")){
							
							evento = lectorXml.next();
							
							if(evento==XmlPullParser.TEXT){
								
								rss.setLink(lectorXml.getText());
								
							}
							
						} else if(etiqueta.equals("pubDate")){
							
							evento = lectorXml.next();
							
							if(evento==XmlPullParser.TEXT){
								
								rss.setPubDate(lectorXml.getText());
								
								//Como este es el ultimo elemento del objeto se inserta en el arraylist
								lista.add(rss);
								
							}
							
						}
				
					}
					
					//Se pasa al siguiente evento para que la lectura del xml siga
					evento = lectorXml.next();
					
				}
	        
				
			} catch (MalformedURLException e) {
				Log.v("malformed", "error");
			} catch (XmlPullParserException e){
				Log.v("XMLPULLPARSEREXCEPTION", "error");
			} catch(IOException e){
				Log.v("io", "error");
			}
			
			return null;
			
		}
		
		//Aqui se actualiza el contenido del widget. Es el mismo codigo casi que se pondria en la clase Widget 
		//si no se hace como servicio
		@Override
		protected void onPostExecute(Void v){
			
			if(isConectado()){

				// Se crea un gestor de widget
				AppWidgetManager gestor = AppWidgetManager
						.getInstance(Servicio.this);

				// Se le dice que widget es el que queremos
				ComponentName widget = new ComponentName(Servicio.this,
						Widget.class);

				// Se obtiene un array con los widgets que tenemos desplegados
				int[] idWidgets = gestor.getAppWidgetIds(widget);

				// Se recorren todos los widgets
				for (int id : idWidgets) {

					// Se utiliza RemoteViews para poder acceder a los elementos
					// que están en los widgets
					RemoteViews vistasRemotas = new RemoteViews(
							Servicio.this.getPackageName(), R.layout.widget);

					// Se crea el objeto miFecha para que nos devuelva la fecha
					// de ahora
					MiFecha miFecha = new MiFecha();

					// Si el titular supera los 43 caracteres le hacemos un
					// substring de 0 a 40 y concatenamos ...
					String titular = lista.get(1).getTitle();
					if (titular.length() > 43) {
						titular = titular.substring(0, 40) + "...";
					}

					// Se asignan los valores a los textview
					vistasRemotas.setTextViewText(R.id.tvTitularWidget, titular);
					vistasRemotas.setTextViewText(R.id.tvPubDateWidget, lista.get(1).getPubDate());
					vistasRemotas.setTextViewText(R.id.tvUltimoResfresco,	miFecha.getFechaCompletaFormateada());

					//Este intent abre la propia app rss
					/*Intent intent = new Intent(Servicio.this, MainActivity.class);

					intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

					intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
							idWidgets);*/
					
					//Este intent abre la noticia en el navegador
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(lista.get(1).getLink()));

					PendingIntent pendingIntent = PendingIntent.getActivity(
							Servicio.this, 0, intent,
							PendingIntent.FLAG_UPDATE_CURRENT);

					//Se le aplica el intent a los tres elementos
					vistasRemotas.setOnClickPendingIntent(R.id.tvTitularWidget, pendingIntent);
					vistasRemotas.setOnClickPendingIntent(R.id.tvPubDateWidget, pendingIntent);
					vistasRemotas.setOnClickPendingIntent(R.id.tvUltimoResfresco, pendingIntent);

					// Se actualiza el widget
					gestor.updateAppWidget(id, vistasRemotas);

				}

				// Termina con el servicio que hemos lanzado.
				// Esto se sigue resfrescando porque esta puesto el alarmManager
				// que
				// iniciara el servicio
				stopSelf();
				
			} else{
				Toast.makeText(Servicio.this, R.string.no_conectado, Toast.LENGTH_LONG).show();
			}
			
		}		
		
	}

}
