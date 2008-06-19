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
    private static Random rn = new Random();        // Zufallsgenerator
                    
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
        for(int i = 0; i < numCities; i++) {
            istFrei = false;
            while(istFrei == false) {
                istFrei = true;
                platz = rn.nextInt(numCities+1);
                for(int j=0; j < numCities; j++) {
                    if(plaetze[j] == platz) {
                        istFrei = false;            // Platz vergeben
                        break;
                    }
                }
            }
            plaetze[i] = platz;
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
                chromosom[p][plaetze[j]-1] = staedte.get(j);
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
            for(int p=1; p < population; p++) {
                if(fitness(chromosom[bestes]) > fitness(chromosom[p])) {
                    bestes = p;
                    System.out.println("Änderung bestes Chromosom: " + bestes);
                }
            }
            /* Reproduktion */
            for(int p = 0; p < population; p++) {
                int mitte;
                boolean seite;
                Stadt[] new_gen = new Stadt[numCities];
                int[] staedte = new int[numCities];
                ArrayList liste = new ArrayList();
                liste = this.staedte;
                staedte = mischen();
                if(p != bestes) {
                    /* >> REKOMBINATION */
                    /* Hälfte des Chromosoms ermitteln */
                    if(chromosom[p].length%2 > 0)
                        mitte = (chromosom[p].length/2)-1;
                    else
                        mitte = chromosom[p].length/2;
                    /* Beste Hälfte in aktuelles Chromosom integrieren */
                    /* Linke oder rechte Hälfte ermitteln*/
                    seite = rn.nextBoolean();
//                    if(seite) {
                        boolean vorhanden;
                        
                        int platz = 0;
                        int nr;
                        /* Linke Seite ersetzen*/
                        for(int n=0; n < mitte; n++) {
                            new_gen[n] = chromosom[bestes][n];
                            platz++;
                        }
                        /* Rechte Hälfte auf Doppeleinträge prüfen und
                         * korrigieren */
                        for(int k=0; k < numCities; k++) {
                            nr = platz-1;
                            vorhanden = false;
                            for(int i=platz; i < numCities; i++) {
                                if(chromosom[p][k].equals(new_gen[i])) {
                                    vorhanden = true;
                                    break;
                                }
                                nr++;
                            }
                            if(!vorhanden) {
                                new_gen[nr-1] = chromosom[p][k];
                                platz++;
                            }
                        }
                        chromosom[p] = new_gen;
//                    } else {
//                        boolean vorhanden;
//                        int platz = numCities--;
//                        /* Rechte Seite ersetzen */
//                        for(int n=mitte; n <= numCities; n++) {
//                            chromosom[p][n] = chromosom[bestes][n];
//                            numCities--;
//                        }
//                        /* Linke Hälfte auf Doppeleinträge prüfen und
//                         * korrigieren */
//                        for(int k=0; k < numCities; k++) {
//                            vorhanden = false;
//                            for(int i=0; i < numCities; i++) {
//                                if(chromosom[p][k].equals(chromosom[p][i])) {
//                                    vorhanden = true;
//                                    break;
//                                }
//                            }
//                            if(!vorhanden)
//                                chromosom[p][platz--] = chromosom[p][k];
//                        }
//                    }

                    /** 
                     * >> MUTATION
                     * Zwei Elemente miteinander tauschen
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
            // TEMPORÄR: Fitness auf Konsole ausgeben
            System.out.println("Generation[" + g + "], Fitness: " 
                    + fitness(chromosom[bestes]));
        }        
    }
    /**
     * Prüft die Fitness eines Chromosoms
     */
    private float fitness(Stadt[] orte) {
        float distanz = 0;  // Distanz gesamt
        double teilstueck;  // Distanz zwischen Ort A und B
        double dx, dy;      // Delta-x/y als Katheten
        Stadt[] chromosom = new Stadt[numCities];
        chromosom = orte;

        for(int i = 0; i < numCities-1; i++) {
            /* Pythagoras summieren */
            dx = (chromosom[i].x - chromosom[i+1].x);
            dy = (chromosom[i].y - chromosom[i+1].y);
            teilstueck = Math.abs(Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2)));
            distanz += teilstueck;
        }
        System.out.println("Distanz: " + distanz);
        return distanz;
    }

    public void setGeneration(int generation) {
        this.generation = generation;
    }    
}
