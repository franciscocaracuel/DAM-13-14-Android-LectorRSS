package com.izv.lectorrss;

import java.util.ArrayList;

import com.izv.lectorrss.beans.RSS;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class AdaptadorRSS extends ArrayAdapter<RSS>{
	
	private Context contexto;
	private ArrayList<RSS> lista;
	
	public AdaptadorRSS(Context c, ArrayList<RSS> l){
		super(c, R.layout.item_listview_rss, l);
		this.contexto=c;
		this.lista=l;
	}
	
	public View getView(int posicion, View vista, ViewGroup padre){
		
		if(vista==null){
			LayoutInflater i=(LayoutInflater)contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			vista=i.inflate(R.layout.item_listview_rss, null);
		}
		
		TextView tvTitular=(TextView)vista.findViewById(R.id.tvTitular);
		TextView tvPubDate=(TextView)vista.findViewById(R.id.tvPubDate);
		
		tvTitular.setText(lista.get(posicion).getTitle());
		tvPubDate.setText(lista.get(posicion).getPubDate());
				
		return vista;
		
	}

}