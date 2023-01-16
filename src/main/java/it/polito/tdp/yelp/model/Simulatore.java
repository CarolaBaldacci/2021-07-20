package it.polito.tdp.yelp.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.yelp.model.Event.EventType;

public class Simulatore {
	
//dati in INGRESSO
	private int x1;
	private int x2;
	
//dati in USCITA
	//i giornalisti sono rappresentati con numeri tra 0 e x1-1
	private List<Giornalista> giornalisti;
	private int numeroGiorni;
	
//MODELLO del Mondo
	private Set<User> intervistati;
	private Graph<User, DefaultWeightedEdge>grafo;
	
//CODA degli eventi
	private PriorityQueue<Event> queue;
	
	
//SIMULAZIONE
	public Simulatore(Graph<User, DefaultWeightedEdge> grafo) {
		this.grafo=grafo;
	}
	
	public void init(int x1, int x2) {
		this.x1=x1;
		this.x2=x2;
		this.intervistati=new HashSet<User>();
		this.numeroGiorni=0;
		
		//creo i giornalisti
		this.giornalisti=new ArrayList<>();
		for(int id=0;id<this.x1;id++) 
			this.giornalisti.add(new Giornalista(id));
		
		this.queue = new PriorityQueue<>();
		//precarico la coda
		for(Giornalista g:this.giornalisti) {
			User intervistato = selezionaIntervistato(this.grafo.vertexSet());
			this.intervistati.add(intervistato);
			g.incrementaNumeroIntervistati();
			this.queue.add(new Event(1, EventType.DA_INTERVISTARE, intervistato, g));
		}
	}
	
//ELABORAZIONE CODA e CONDIZIONI DI TERMINAZIONE	
	public void run() {
		while(this.queue.isEmpty() && this.intervistati.size()<x2) {
			Event e=this.queue.poll();
			this.numeroGiorni=e.getGiorno();
			
			processEvent(e);
		}
	}
	
	
	private void processEvent(Event e) {
		switch(e.getType()) {
		case DA_INTERVISTARE:
			double caso=Math.random();
			if(caso<0.6) {
				//caso I: 
				User vicino = this.selezionaAdiacente(e.getIntervistato());
				if(vicino==null)
					vicino=selezionaIntervistato(this.grafo.vertexSet());
				this.queue.add(new Event(
						e.getGiorno()+1, EventType.DA_INTERVISTARE,
						vicino,e.getGiornalista()));
				
			}else if(caso<0.8){
				//caso II: mantengo gli stessi parametri , perchè non c'è un nuovo intervistato
				//ma cambio il tipo di evento in FERIE
				this.queue.add(new Event(
						e.getGiorno()+1, EventType.FERIE,
						e.getIntervistato(),e.getGiornalista()));
			}else {
				//caso III :continuo con lo stesso il giorno dopo
				this.queue.add(new Event(
						e.getGiorno()+1, EventType.DA_INTERVISTARE,
						e.getIntervistato(),e.getGiornalista()));
			}
			break;
			
		case FERIE:
			User vicino= this.selezionaAdiacente(e.getIntervistato());
			if(vicino==null)
				vicino= this.selezionaIntervistato(this.grafo.vertexSet());
			
			this.queue.add(new Event(
					e.getGiorno()+1, EventType.DA_INTERVISTARE,
					vicino,e.getGiornalista()));
			this.intervistati.add(vicino);
			e.getGiornalista().incrementaNumeroIntervistati();
			break;
		}
	}

	//Seleziona un intervistato random dalla lista , 
	//evitando di selezionare quelli già intervistati
	private User selezionaIntervistato(Collection<User> lista) {
		Set <User> candidati = new HashSet<User>(lista);
		candidati.removeAll(this.intervistati);
		int scelto = (int)Math.random()*candidati.size();
		return (new ArrayList<User>(candidati).get(scelto));
	}

	private User selezionaAdiacente(User u) {
		List<User> vicini=Graphs.neighborListOf(this.grafo, u);
		vicini.removeAll(intervistati);
		
		//non ci sono vicini da intervistare
		if(vicini.size()==0)
			return null;
		//trovo il peso massimo
		double max=0;
		for(User v: vicini) {
			double peso=this.grafo.getEdgeWeight(this.grafo.getEdge( u, v));
			if(peso>max)
				max=peso;
		}
		//raggruppo i migliori in un unica lista
		List <User> migliori=new ArrayList<>();
		for(User v:vicini) {
			double peso=this.grafo.getEdgeWeight(this.grafo.getEdge( u, v));
			if(peso==max)
				migliori.add(v);
		}
		//ne scelgo uno a caso tra i migliori
		int scelto=(int) (Math.random()*migliori.size());
		return migliori.get(scelto);
	}
	
//GETTER E SETTER
	public int getX1() {
		return x1;
	}

	public void setX1(int x1) {
		this.x1 = x1;
	}

	public int getX2() {
		return x2;
	}

	public void setX2(int x2) {
		this.x2 = x2;
	}

	public List<Giornalista> getGiornalisti() {
		return giornalisti;
	}

	public int getNumeroGiorni() {
		return numeroGiorni;
	}

	public Set<User> getIntervistati() {
		return intervistati;
	}

	public PriorityQueue<Event> getQueue() {
		return queue;
	}
	
	
	
}
