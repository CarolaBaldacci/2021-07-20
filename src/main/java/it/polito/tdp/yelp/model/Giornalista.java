package it.polito.tdp.yelp.model;

public class Giornalista {

	private int id;
	private int numeroIntervistati;
	
	public Giornalista(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	public int getNumeroIntervistati() {
		return numeroIntervistati;
	}
	
	
	public void incrementaNumeroIntervistati() {
		this.numeroIntervistati++;
	}
}
