package it.polito.tdp.nobel.model;

import java.util.*;

import it.polito.tdp.nobel.db.EsameDAO;

public class Model {
	private List<Esame> esami;
	private Set<Esame> migliore; // Il Set non tiene l'ordine e toglie i duplicati
	private double mediaMigliore;
	
	public Model() {
		EsameDAO dao = new EsameDAO();
		this.esami = dao.getTuttiEsami();
	}
	
	public Set<Esame> calcolaSottoinsiemeEsami(int m) { // Prepara le cose per invocare la funzione ricorsiva
		// Ripristino soluzione migliore
		migliore = new HashSet<Esame>();
		mediaMigliore = 0.0; // Sappiamo che troveremo valori maggiori dello 0 da qui in avanti
		
		Set<Esame> parziale = new HashSet<Esame>();
		cerca1(parziale, 0, m); // Permette di riempire la soluzione migliore
		
		cerca2(parziale, 0, m);
		return migliore;	
	}

	/* COMPLESSITA' N! */
	private void cerca1(Set<Esame> parziale, int L, int m) { // m = numero crediti
		// Controllare i casi terminali che permettono di fermarci prima
		int sommaCrediti = sommaCrediti(parziale);
		if(sommaCrediti > m) // E' una soluzione non valida
			return;
		
		else if(sommaCrediti == m) {
			// Potrebbe essere una soluzione valida, ma dobbiamo controllare se è la migliore
			double mediaVoti = calcolaMedia(parziale);
			
			if(mediaVoti > mediaMigliore) {
				// migliore = parziale; // E' sbagliato perchè ci stiamo copiando il riferimento a parziale che poi evolve e cambia, quindi
									    // dobbiamo fare una copia di parziale
				migliore = new HashSet<Esame>(parziale);
				mediaMigliore = mediaVoti;
			}
			
			return;
		}
		
		// Sicuramente i crediti sono < m, quindi non devo controllare se è una soluzione migliore perchè ancora non è valida, ma potrebbe 
		// diventare valida
		if(L == esami.size()) 
			return; // I crediti sono < m e non ho più niente da aggiungere 
		
		// Generiamo i sotto-problemi
		for(Esame e : esami) {
			if(!parziale.contains(e)) { // Contains funziona perchè Esame ha implementato al suo interno hashCode and Equals
				parziale.add(e);
				cerca1(parziale, L+1, m);
				
				/* BACKTRACKING */
				parziale.remove(e); // Passiamo l'oggetto da togliere perchè nei Set sono sicura che ci sia solo una volta questo oggetto
				// Nel caso in cui si voglia fare backtracking con una lista invece devo togliere l'ultimo elemento perchè possono esserci
				// duplicati, quindi devo fare parziale.remove(parziale.size() - 1)
			}
		}
	}
	
	private void cerca2(Set<Esame> parziale, int L, int m) {
		int sommaCrediti = sommaCrediti(parziale);
		if(sommaCrediti > m) // E' una soluzione non valida
			return;
		
		else if(sommaCrediti == m) {
			// Potrebbe essere una soluzione valida, ma dobbiamo controllare se è la migliore. Esploro le soluzioni mano a mano
			double mediaVoti = calcolaMedia(parziale);
			
			if(mediaVoti > mediaMigliore) {
				// migliore = parziale; // E' sbagliato perchè ci stiamo copiando il riferimento a parziale che poi evolve e cambia, quindi
									    // dobbiamo fare una copia di parziale
				migliore = new HashSet<Esame>(parziale);
				mediaMigliore = mediaVoti;
			}
			
			return;
		}
		
		// Sicuramente i crediti sono < m, quindi non devo controllare se è una soluzione migliore perchè ancora non è valida, ma potrebbe 
		// diventare valida
		if(L == esami.size()) 
			return; // I crediti sono < m e non ho più niente da aggiungere 
		
		/*
		 * Per ogni livello L  ho due strade possibili:
		 * 1) proviamo ad aggiungere esami[L]
		 * 2) provo a "non aggiungere" esami[L]
		 */
		
		// Uso il livello come indice della lista degli esami di partenza in modo da scorrerla
		
		// 1) proviamo ad aggiungere esami[L]
		parziale.add(esami.get(L));
		cerca2(parziale, L+1, m);
		
		// 2) provo a "non aggiungere" esami[L]
		parziale.remove(esami.get(L));
		cerca2(parziale, L + 1, m);
		
	}

	public double calcolaMedia(Set<Esame> esami) {
		
		int crediti = 0;
		int somma = 0;
		
		for(Esame e : esami){
			crediti += e.getCrediti();
			somma += (e.getVoto() * e.getCrediti());
		}
		
		return somma/crediti;
	}
	
	public int sommaCrediti(Set<Esame> esami) {
		int somma = 0;
		
		for(Esame e : esami)
			somma += e.getCrediti();
		
		return somma;
	}

}
