package kitravelingsalesman;
import java.util.ArrayList;
import java.util.Random;

/**
 * @version 1.0
 * @author Alexander Richter
 *
 * Klasse: Route
 *
 * Zweck: Diese Klasse berechnet das Rundreiseproblem mithilfe des
 * genetischen Algorithmus.
 */
public class Route {
    private ArrayList<Stadt> staedte;               // Liste mit Wepunkten
    private Stadt[][] chromosom;                    // Chromosomenpopulation
    private int population, generation, numCities, mutationsrate;
    private static Random rn = new Random();        // Zufallsgenerator
    private float gesamtstrecke;
                    
    public Route(ArrayList staedte, int numPop) {
        this.staedte = staedte;
        numCities = staedte.size();
        population = numPop;
        chromosom = new Stadt[population][numCities];
        initPopulation();
    }
    /**
     * Erzeugt eine zufällig Anordnung der Wegpunkte
     * 
     * @return int[] plaetze
     */
    private int[] mischen() {
        int[] plaetze = new int[numCities];
        int platz = 0;
        boolean istFrei;                            // Platznummer frei ?
        /* Anordnungsliste leeren für neue Platzvergabe*/
        for(int i = 0; i < numCities; i++)
            plaetze[i] = 0;
        /* Anordnung der Wegpunkte zufällig generieren */
        for(int i = 1; i < numCities; i++) {
            istFrei = false;
            while(istFrei == false) {
                istFrei = true;
                platz = rn.nextInt(numCities-1);
                for(int j=1; j < numCities; j++) {
                    if(plaetze[j] == platz+1) {
                        istFrei = false;            // Platz vergeben
                        break;
                    }
                }
            }
            plaetze[i] = platz+1;
        }
        return plaetze;
    }
    /**
     * Initialisierung der Population
     */
    private void initPopulation() {
        int[] plaetze = new int[numCities];
        for(int p = 0; p < population; p++) {
            plaetze = mischen();
            /* Platzvergabe innerhalb der Population */
            for(int j=0; j < numCities; j++) {
                chromosom[p][plaetze[j]] = staedte.get(j);
            }                    
        }
    }
    /**
     * Traveling Salesman Algorithmus
     */
    public Stadt[] travelingSalesman() { 
        Stadt tmp = new Stadt();
        int bestes = 0;
        int mitte;
        /* Hälfte des Chromosoms ermitteln */
        if(chromosom[0].length%2 > 0)
            mitte = (chromosom[0].length/2)-1;
        else
            mitte = chromosom[0].length/2;
        
        /* Generationen durchlaufen */
        for(int g = 0; g < generation; g++) {
            bestes = 0;                  // Bestes Chromosom
            
            /* Bestes Chromosom finden */
            for(int p=1; p < population; p++) {
                if(fitness(chromosom[bestes]) > fitness(chromosom[p])) {
                    bestes = p;
                }
            }
            /* Reproduktion */
            for(int p = 0; p < population; p++) {
                Stadt[] new_gen = new Stadt[numCities];
                int[] staedte = new int[numCities-1];
                staedte = mischen();
                if(p != bestes) {
                    /* >> REKOMBINATION */
                    if(rn.nextBoolean()) {
                        /* Linke Hälfte des besten in 
                           aktuelles Chromosom integrieren */
                        boolean vorhanden;                        
                        int platz = 0;
                        /* Linke Seite ersetzen*/
                        for(int n=0; n < mitte; n++) {
                            new_gen[n] = chromosom[bestes][n];
                            platz++;
                        }
                        /* Rechte Hälfte auf Doppeleinträge prüfen und
                         * korrigieren */
                        for(int k=0; k < numCities; k++) {
                            vorhanden = false;
                            int i = 0;
                            while(new_gen[i] != null) {
                                    if(new_gen[i] == chromosom[p][k]) {
                                        vorhanden = true;
                                        break;
                                    } else {
                                        i++;
                                    }
                            }
                            if(!vorhanden) {
                                new_gen[i] = chromosom[p][k];
                            }
                        }
                    } else {
                        /* Rechte Hälfte des besten in
                           aktuelles Chromosom integrieren */
                        boolean vorhanden;
                        int platz = numCities-1;
                        /* Rechte Seite ersetzen */
                        for(int n=numCities-1; n > mitte; n--) {
                            new_gen[n] = chromosom[bestes][n];
                            platz--;
                        }
                        /* Linke Hälfte auf Doppeleinträge prüfen und
                           korrigieren */
                        for(int k=0; k < numCities; k++) {
                            vorhanden = false;
                            int i = numCities-1;
                            while(new_gen[i] != null) {
                                    if(new_gen[i] == chromosom[p][k]) {
                                        vorhanden = true;
                                        break;
                                    } else {
                                        i--;
                                    }
                            }
                            if(!vorhanden) {
                                new_gen[i] = chromosom[p][k];
                            }
                        }
                    }
                            chromosom[p] = new_gen;
                    /** 
                     * >> MUTATION
                     * Zwei Elemente miteinander tauschen
                     */
                    int von, zu;
                    if(rn.nextInt(99) < mutationsrate-1) {
                        do {
                            von = rn.nextInt(numCities);
                            zu = rn.nextInt(numCities);
                        } while(von == zu);

                        /* Beide vertauschen */
                        tmp = chromosom[p][von];
                        chromosom[p][von] = chromosom[p][zu];
                        chromosom[p][zu] = tmp;
                    }
                }
            }
            // Fitness auf Konsole ausgeben
            System.out.println("Generation[" + g + "], Fitness: " 
                    + fitness(chromosom[bestes]));
            if(g == generation-1)
                setGesamtstrecke(fitness(chromosom[bestes]));
        }
        return chromosom[bestes];
    }
    /**
     * Prüft die Fitness eines Chromosoms
     */
    private float fitness(Stadt[] orte) {
        float massstab = (float)2.25;   // Maßstab der Karte
        float distanz = 0;              // Distanz gesamt
        double teilstueck;              // Distanz zwischen Ort A und B
        double dx, dy;                  // Delta-x/y als Katheten
        Stadt[] chromosom = new Stadt[numCities];
        chromosom = orte;

        for(int i = 0; i < numCities-1; i++) {
            /* Pythagoras summieren */
            dx = (chromosom[i].x - chromosom[i+1].x);
            dy = (chromosom[i].y - chromosom[i+1].y);
            teilstueck = Math.abs(Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2)));
            distanz += teilstueck;
        }
        dx = (chromosom[0].x - chromosom[numCities-1].x);
        dy = (chromosom[0].y - chromosom[numCities-1].y);
        teilstueck = Math.abs(Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2)));
        distanz += teilstueck;
        return distanz*massstab;
    }
    
    public float getGesamtstrecke() {
        return gesamtstrecke;
    }
    public void setGesamtstrecke(float gesamtstrecke) {
        this.gesamtstrecke = gesamtstrecke;
    }
    public void setGeneration(int generation) {
        this.generation = generation;
    }
    public void setMutationsrate(int mutationsrate) {
    this.mutationsrate = mutationsrate;
    }
}
