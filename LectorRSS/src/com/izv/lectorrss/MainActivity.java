package com.izv.lectorrss;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.izv.lectorrss.beans.RSS;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	//Mis variables
	private ArrayList<RSS> lista;
	private ListView lvRss;
	private RSS rss;
	private ProgressBar pbLoad;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		inicio();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void inicio(){
		
		lista=new ArrayList<RSS>();
		pbLoad=(ProgressBar)findViewById(R.id.pbLoad);
		
		lvRss=(ListView)findViewById(R.id.lvRss);
		lvRss.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> av, View v, int pos,	long id) {
				
				//Se lanza un intent con la ruta del item seleccionado
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(lista.get(pos).getLink()));
				startActivity(i);
				
			}
		});
		
		//Si hay conexion a internet se lanzara el hilo que carga el listview
		//Si no se muestra un mensaje
		if(isConectado()){
			HiloRSS hr=new HiloRSS();
			hr.execute();
		}else{
			Toast.makeText(this, R.string.no_conectado, Toast.LENGTH_LONG).show();
			pbLoad.setVisibility(View.INVISIBLE);
		}
		
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

	private class HiloRSS extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			
			try {				
				
				//url tendra la ruta del rss
				URL url = new URL("http://marca.feedsportal.com/rss/portada.xml");
				
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
		
		@Override
		protected void onPostExecute(Void v){
						
			pbLoad.setVisibility(View.INVISIBLE);
			
			//Se asigna al listview el arraylist creado
			AdaptadorRSS ar=new AdaptadorRSS(MainActivity.this, lista);
			registerForContextMenu(lvRss);
			lvRss.setAdapter(ar);
			
		}		
		
	}

}
