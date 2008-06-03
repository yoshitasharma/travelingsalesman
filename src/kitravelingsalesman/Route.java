package kitravelingsalesman;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Alexander Richter, Sebastian Losch
 */
public class Route {
    private ArrayList<Stadt> staedte;               // Liste mit Wepunkten
    private Stadt[][] chromosom;                    // Chromosomenpopulation
    private int population, generation, numCities;
    private static Random rn = new Random();        //Zufallsgenerator
                    
    public Route(ArrayList staedte) {
        this.staedte = staedte;
        numCities = staedte.size();
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
        int platz;
        boolean istFrei;                            // Platznummer frei ?
        /* Anordnungsliste leeren für neue Platzvergabe*/
        for(int i = 0; i < numCities; i++)
            plaetze[i] = 0;
        /* Anordnung der Wegpunkte zufällig generieren */
        for(int i = 0; i < numCities; i++) {
            istFrei = false;
            while(istFrei == false) {
                platz = rn.nextInt(numCities);
                if(plaetze[i] != platz) {
                    plaetze[i] = platz;
                    istFrei = true;
                }
            }
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
                for(int nr=0; nr < plaetze.length; nr++)
                    chromosom[p][plaetze[nr]] = staedte.get(j);
            }                    
        }
    }
    /**
     * Traveling Salesman Algorithmus
     */
    public void travelingSalesman() { 
        Stadt tmp = new Stadt();             // Für spätere Verarbeitung
        for(int g = 0; g < generation; g++) {
            int bestes = 0;                  // Bestes Chromosom
            
            /* Bestes Chromosom finden */
            for(int p=0; p < population; p++) {
                if(fitness(chromosom[bestes]) > fitness(chromosom[p]))
                    bestes = p;
            }
            /* Reproduktion */
            for(int p = 0; p < population; p++) {
                int[] staedte = new int[numCities];
                staedte = mischen();
                if(p != bestes) {
                    for(int n=0; n < numCities; n++) {
                        if(rn.nextBoolean())
                            chromosom[p][n] = chromosom[bestes][n];
                        /** 
                         * Mutation 
                         * 
                         * Sieht momentan so aus, dass wir zwei Elemente
                         * miteinander tauschen.
                         */
                        int von, zu;
                        if(rn.nextInt() % 100 < 4) {
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
            }
            // TEMPORÄR: Fitness auf Konsole ausgeben
            System.out.println("Generation[" + g + "], Fitness: " 
                    + fitness(chromosom[bestes]));
        }        
    }
    /**
     * Prüft die Fitness eines Chromosoms
     */
    private float fitness(Stadt[] chromosom) {
        float distanz = 0;    // Distanz gesamt
        double teilstueck;  // Distanz zwischen Ort A und B
        double dx, dy;      // Delta-x/y als Katheten

        for(int i = 0; i < numCities-1; i++) {
            /* Pythagoras summieren */
            dx = (chromosom[i].x - chromosom[i+1].x);
            dy = (chromosom[i].y - chromosom[i+1].y);
            teilstueck = Math.abs(Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2)));
            distanz += teilstueck;
            System.out.println(i + " - " + teilstueck);
        }
        System.out.println("Distanz: " + distanz);
        return distanz;
    }

    public void setGeneration(int generation) {
        this.generation = generation;
    }

    public void setPopulation(int population) {
        this.population = population;
    }
    
}
