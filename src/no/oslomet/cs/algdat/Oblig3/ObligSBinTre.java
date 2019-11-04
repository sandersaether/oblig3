package no.oslomet.cs.algdat.Oblig3;

////////////////// ObligSBinTre /////////////////////////////////

import java.util.*;


public class ObligSBinTre<T> implements Beholder<T> {

    public static void main(String[]args) {
        int[] a = {4,7,2,9,4,10,8,7,4,6,1};
        ObligSBinTre<Integer> tre = new ObligSBinTre<>(Comparator.naturalOrder());
        for (int verdi : a) tre.leggInn(verdi);

        System.out.println(tre);


    }

    private static final class Node<T>   // en indre nodeklasse
    {
        private T verdi;                   // nodens verdi
        private Node<T> venstre, høyre;    // venstre og høyre barn
        private Node<T> forelder;          // forelder

        // konstruktør
        private Node(T verdi, Node<T> v, Node<T> h, Node<T> forelder)
        {
            this.verdi = verdi;
            venstre = v; høyre = h;
            this.forelder = forelder;
        }

        private Node(T verdi, Node<T> forelder)  // konstruktør
        {
            this(verdi, null, null, forelder);
        }

        @Override
        public String toString(){ return "" + verdi;}

    } // class Node

    private Node<T> rot;                            // peker til rotnoden
    private int antall;                             // antall noder
    private int endringer;                          // antall endringer

    private final Comparator<? super T> comp;       // komparator

    public ObligSBinTre(Comparator<? super T> c)    // konstruktør
    {
        rot = null;
        antall = 0;
        comp = c;
    }

    @Override
    public boolean leggInn(T verdi) {
        Objects.requireNonNull(verdi, "Ulovlig med nullverdier!");

        Node<T> p = rot, q = null;               // p starter i roten
        int cmp = 0;                             // hjelpevariabel
        while (p != null){
            q = p;                                 // q er forelder til p
            cmp = comp.compare(verdi,p.verdi);     // bruker komparatoren
            p = cmp < 0 ? p.venstre : p.høyre;     // flytter p
        }
        p = new Node<>(verdi, q);

        if (q == null) rot = p;                  // p blir rotnode
        else if (cmp < 0) q.venstre = p;         // venstre barn til q
        else q.høyre = p;                        // høyre barn til q

        antall++;                                // én verdi mer i treet
        return true;                             // vellykket innlegging
    }

    @Override
    public boolean inneholder(T verdi)
    {
        if (verdi == null) return false;

        Node<T> p = rot;

        while (p != null)
        {
            int cmp = comp.compare(verdi, p.verdi);
            if (cmp < 0) p = p.venstre;
            else if (cmp > 0) p = p.høyre;
            else return true;
        }

        return false;
    }

    @Override
    public boolean fjern(T verdi)
    {
        if (tom()) throw new NoSuchElementException("Treet er tomt!");

        if (rot.venstre == null) rot = rot.høyre;  // rotverdien er minst
        else
        {
            Node<T> p = rot.venstre, q = rot;
            while (p.venstre != null)
            {
                q = p;  // q er forelder til p
                p = p.venstre;
            }
            // p er noden med minst verdi
            q.venstre = p.høyre;
        }
        antall--;  // det er nå én node mindre i treet
        return true;
    }

    public int fjernAlle(T verdi)
    {
        int antall = 0;
        while (fjern(verdi)) antall++;
        return antall;
    }

    @Override
    public int antall()
    {
        return antall;
    }

    public int antall(T verdi)
    {
        if (verdi == null) return 0;

        Node<T> p = rot;
        int antallverdier = 0;

        while (p != null)
        {
            int cmp = comp.compare(verdi, p.verdi);
            if (cmp < 0) p = p.venstre;
            else
            {
                if (cmp == 0) antallverdier++;
                p = p.høyre;
            }
        }

        return antallverdier;

    }

    @Override
    public boolean tom()
    {
        return antall == 0;
    }

    @Override
    public void nullstill()
    {
        if (!tom()) nullstill(rot);
        rot = null;
        antall = 0;
        endringer++;
    }
    private static <T> void nullstill(Node<T> p)
    {
        //Postorden, nuller subtrær før vi så nuller noden
        if (p.venstre != null) { // Vi nuller venstre
            nullstill(p.venstre);
            p.venstre = null;
        }
        if (p.høyre != null) { // Vi nuller høyre
            nullstill(p.høyre);
            p.høyre = null;
        }
        p.verdi = null;
    }

    private static <T> Node<T> nesteInorden(Node<T> p) {

        //Finne den nederste noden til venstre i høyre-subtre til p
        if (p.høyre != null) {
            p = p.høyre;
            while (p.venstre != null) {
                p = p.venstre;
            }
            return p;

        }

        else { //p har ikke høyrebarn og vi må oppover i treet
            while (p.forelder != null && p.forelder.høyre == p) {
                p = p.forelder;
            }
            return p.forelder;
        }
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        Node<T> p = rot;

        if (p == null) { //tomt tre
            sb.append("]");
            return sb.toString();
        }

        while (p.venstre != null) {
            p = p.venstre;
        }

        sb.append(p.verdi);

        for (int i = 1; i < antall; i++) {
            sb.append(", ");
            p = nesteInorden(p);
            sb.append(p.verdi);
        }

        sb.append("]");
        return sb.toString();
    }


    public String omvendtString()
    {
        if (tom()) return "[]";

        Stakk<Node<T>> stakk = new TabellStakk<>();
        StringJoiner s = new StringJoiner(", ", "[", "]");

        Node<T> p = rot;
        while (p.høyre != null)
        {
            stakk.leggInn(p);
            p = p.høyre;
        }

        s.add(p.verdi.toString());

        while (true)
        {
            if (p.venstre != null)
            {
                p = p.venstre;
                while (p.høyre != null)
                {
                    stakk.leggInn(p);
                    p = p.høyre;
                }
            }
            else if (!stakk.tom()) p = stakk.taUt();
            else break;

            s.add(p.verdi.toString());
        }

        return s.toString();
    }

    public String høyreGren()
    {
        StringJoiner s = new StringJoiner(", ", "[", "]");

        if (!tom())
        {
            Node<T> p = rot;
            while (true)
            {
                s.add(p.verdi.toString());
                if (p.høyre != null) p = p.høyre;
                else if (p.venstre != null) p = p.venstre;
                else break;
            }
        }
        return s.toString();
    }

    public String lengstGren()
    {
        if (tom()) return "[]";

        Kø<Node<T>> kø = new TabellKø<>();
        kø.leggInn(rot);

        Node<T> p = null;

        while (!kø.tom())
        {
            p = kø.taUt();
            if (p.høyre != null) kø.leggInn(p.høyre);
            if (p.venstre != null) kø.leggInn(p.venstre);
        }

        return gren(p);
    }
    private static <T> String gren(Node<T> p)
    {
        Stakk<T> s = new TabellStakk<>();
        while (p != null)
        {
            s.leggInn(p.verdi);
            p = p.forelder;
        }
        return s.toString();
    }

    public String[] grener()
    {
        throw new UnsupportedOperationException("Ikke kodet ennå!");
    }

    public String bladnodeverdier() {
        if (tom()) return "[]";
        StringJoiner s = new StringJoiner(", ", "[", "]");
        bladnodeverdier(rot, s);
        return s.toString();
    }
    private static <T> void bladnodeverdier(Node<T> p, StringJoiner s)
    {
        if (p.venstre == null && p.høyre == null) s.add(p.verdi.toString());
        if (p.venstre != null) bladnodeverdier(p.venstre, s);
        if (p.høyre != null) bladnodeverdier(p.høyre, s);
    }

    public String postString()
    {
        throw new UnsupportedOperationException("Ikke kodet ennå!");
    }

    @Override
    public Iterator<T> iterator()
    {
        return new BladnodeIterator();
    }

    private class BladnodeIterator implements Iterator<T>
    {
        private Node<T> p = rot, q = null;
        private boolean removeOK = false;
        private int iteratorendringer = endringer;

        private BladnodeIterator()  // konstruktør
        {
            throw new UnsupportedOperationException("Ikke kodet ennå!");
        }

        @Override
        public boolean hasNext()
        {
            return p != null;  // Denne skal ikke endres!
        }

        @Override
        public T next()
        {
            throw new UnsupportedOperationException("Ikke kodet ennå!");
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException("Ikke kodet ennå!");
        }

    } // BladnodeIterator

} // ObligSBinTre
