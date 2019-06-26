package it.polito.tdp.flight.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.KosarajuStrongConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.GraphIterator;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import it.polito.tdp.flight.db.FlightDAO;

public class Model {
	
	private Graph<Airport,DefaultWeightedEdge> grafo;
	private Map<Integer,Airport> idMapAirport;
	private FlightDAO dao;
	private List<Adiacenza> adiacenzeCorrette;
	
	public Model() {
		this.dao = new FlightDAO();
	}
	
	

	public void creaGrafo(Integer km) {
		this.grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		this.idMapAirport = new HashMap<Integer,Airport>();
		
		
		//carico i vertici
		this.dao.getAllAirports(idMapAirport);
		Graphs.addAllVertices(grafo, this.idMapAirport.values());
		System.out.println("Vertici: "+this.grafo.vertexSet().size());
		
		
		//carico gli archi
		
		//doppio ciclo fo, controllo se la distanza è inferiore
		//a quel punto controllo con un check se esiste una rotta
		
		List<Adiacenza> adiacenze = new ArrayList<Adiacenza>();
		
		for(Airport a1 : this.idMapAirport.values()) {
			for(Airport a2: this.idMapAirport.values()) {
				if(!a1.equals(a2)) {
					
					LatLng l1 = new LatLng(a1.getLatitude(),a1.getLongitude());
					LatLng l2 = new LatLng(a2.getLatitude(),a2.getLongitude());
					
					Double distance = LatLngTool.distance(l1, l2, LengthUnit.KILOMETER);
					
					if(distance<=km) {
						
						Double peso = (distance/800); //km/km/h = h
						Adiacenza a = new Adiacenza(a1,a2,peso);
						adiacenze.add(a);
						
					}
				}
			}
		}
		
		 adiacenzeCorrette = new ArrayList<Adiacenza>();
		
		//definiti i pesi controllo esista una rotta
		for(Adiacenza a : adiacenze) {
			boolean trovato = this.dao.checkAdiacenza(a);
			
			if(trovato) {
				adiacenzeCorrette.add(a);
			}
			
		}
		
		
		for(Adiacenza a : adiacenzeCorrette) {
			Graphs.addEdge(grafo, a.getA1(), a.getA2(), a.getPeso());
		}
		
		System.out.println("Archi: "+this.grafo.edgeSet().size());
		
		
	}
	
	
	public boolean possibileRaggiungere() {
		
		KosarajuStrongConnectivityInspector<Airport,DefaultWeightedEdge> c = 
				new KosarajuStrongConnectivityInspector<>(this.grafo);
		
		boolean connesso = c.isStronglyConnected();

		return connesso;
	}
	
	
	public Airport lontanoDa() {
		Airport fiumicino = idMapAirport.get(1555);
		
		//visita in ampiezza
		List<Airport> result = new ArrayList<Airport>();
		GraphIterator<Airport, DefaultWeightedEdge> it = new BreadthFirstIterator<>(this.grafo, fiumicino);
		
		//aggiunta del tizio alla lista
		while(it.hasNext()) {
			result.add(it.next());
		}
		
		LatLng l1 = new LatLng(fiumicino.getLatitude(),fiumicino.getLongitude());
		
		List<Adiacenza> lontano = new ArrayList<Adiacenza>();
		//controllo chi è il più lontano
		for(Airport a : result) {
			
			LatLng l2 = new LatLng(a.getLatitude(),a.getLongitude());
			Double distance = LatLngTool.distance(l1, l2, LengthUnit.KILOMETER);
			
			Double peso = distance/800;
			
			Adiacenza aa = new Adiacenza(fiumicino,a,peso);
			lontano.add(aa);

		}
		
		Collections.sort(lontano);
		
		
		return lontano.get(0).getA2();

	}

}
