package td1.commandes;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import td1.paires.Paire;

public class Commande {
    static final Function<Paire<Produit, Integer>, String> formatteurLigne = ligne -> String.format("%s %d\n", ligne.fst(), ligne.snd());

    private Function<Paire<Produit, Integer>, String> formatteur = formatteurLigne;
    private List<Paire<Produit, Integer>> lignes;

    public Commande() {
        this.lignes = new ArrayList<>();
    }

    public Commande(Function<Paire<Produit, Integer>, String> formatteur) {
        this.lignes = new ArrayList<>();
        if(formatteur != null) this.formatteur = formatteur;
    }


    public Commande ajouter(Produit p, int q) {
        lignes.add(new Paire<>(p, q));
        return this;
    }

    public List<Paire<Produit, Integer>> lignes() {
        return lignes;
    }

    /*@Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Commande\n");
        for (Paire<Produit, Integer> ligne : lignes) {
            str.append(String.format("%s x%d\n", ligne.fst(), ligne.snd()));
        }
        return str.toString();
    }
    */

    /**
     * cumule les lignes en fonction des produits
     */
    public Commande normaliser() {
    /*    Map<Produit, Integer> lignesCumulees = new HashMap<>();
        for (Paire<Produit, Integer> ligne : lignes) {
            Produit p = ligne.fst();
            int qte = ligne.snd();
            if (lignesCumulees.containsKey(ligne.fst())) {
                lignesCumulees.put(p, lignesCumulees.get(p) + qte);
            } else {
                lignesCumulees.put(p, qte);
            }
        }
        Commande commandeNormalisee = new Commande();
        for (Produit p : lignesCumulees.keySet()) {
            commandeNormalisee.ajouter(p, lignesCumulees.get(p));
        }
        return commandeNormalisee;
        */
        Map<Produit, Integer> lignesCumulees = new HashMap<>();

        Commande cn = new Commande();
        regrouper(lignes)
                .forEach((produit, qtes) -> cn.ajouter(produit, qtes.stream().reduce(0, Integer::sum)));
        return cn;
    }

    public Double cout(Function<Paire<Produit, Integer>, Double> calculLigne) {
        /*double rtr = 0;
        for (Paire<Produit, Integer> l : normaliser().lignes) {
            rtr += calculLigne.apply(l);
        }
        return rtr;
        */
        return normaliser()
                .lignes
                .stream()
                .map(calculLigne)
                .reduce(0., (a, b) -> a + b);
    }

    public String affiche(Function<Paire<Produit, Integer>, Double> calculLigne) {
        Commande c = this.normaliser();
        final String HLINE = "+------------+------------+-----+------------+--------+------------+\n";
        StringBuilder str = new StringBuilder();
        str.append("\n\nCommande\n");
        str.append(HLINE);
        str.append("+ nom        + prix       + qté + prix ht    + tva    + prix ttc   +\n");
        str.append(HLINE);
        for (Paire<Produit, Integer> ligne : c.lignes) {
            str.append(String.format("+ %10s + %10.2f + %3d + %10.2f + %5.2f%% + %10.2f +\n", ligne.fst(), // nom
                    ligne.fst().prix(), // prix unitaire
                    ligne.snd(), // qté
                    ligne.fst().prix() * ligne.snd(), // prix ht
                    ligne.fst().cat().tva() * 100, // tva
                    calculLigne.apply(ligne)));
        }
        str.append(HLINE);
        str.append(String.format("Total : %10.2f", c.cout(calculLigne)));
        return str.toString();
    }

    /*
    default <V> Function<V, R> compose(Function<? super V, ? extends T> before) {
        Objects.requireNonNull(before);
        return (V v) -> apply(before.apply(v));
    }*/


    /*
    public String formatteurLigne(String ){
        String rtr = "";
        for (Paire<Produit,Integer> l : lignes
             ) {
            rtr+=l.fst().toString()+"\n";
        }
        return rtr;
    }*/


    // réécrire la méthode toString en utilisant formatteurLigne, map et collect
    /*
      • faire en sorte que l’on puisse utiliser différents formatteurs de ligne pour différentes
        commandes (un même formatteur étant utilisé pour toutes les lignes d’une même commande),
                formatteurLigne étant utilisé par défaut si l’on ne précise rien
     */
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (Paire<Produit,Integer> l : lignes
             ) {
            str.append(String.format("%s x%d\n", l.fst(), l.snd()));
            str.append(formatteurLigne.apply(l));
        }
        return str.toString();
    }

    /*
        écrire une méthode générique regrouper permettant de regrouper des lignes (quelque soient les
types dans les paires). Comparer à Collectors::groupingBy
     */

    public static <A, B> Map<A, List<B>> regrouper(List<Paire <A, B>> commandes){
        Map<A, List<B>> rtr = new HashMap<>();
        commandes.forEach(p ->
                rtr.computeIfAbsent(p.fst(), array -> new ArrayList<>()).add(p.snd()));
        for (Paire<A, B> p : commandes) {
            if (!rtr.containsKey(p.fst())) {
                 rtr.put(p.fst(), new ArrayList<>());
            }
             rtr.get(p.fst()).add(p.snd());
        }
        return rtr;
    }

    public static <E> void display(E[] tab) {
        for(E e : tab) {
            System.out.printf("%s ", e);
        }
        System.out.println();
    }

}
