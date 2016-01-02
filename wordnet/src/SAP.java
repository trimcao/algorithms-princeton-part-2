/**
 * Shortest Ancestral Path class
 * Name: Tri Minh Cao
 * Email: trimcao@gmail.com
 * Date: December 2015
 */
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import java.util.ArrayList;

public class SAP {
    private Digraph graph;
    private CustomBFS bfsV;
    private CustomBFS bfsW;

    // helper class to return two values: distance and vertex
    private class Result {
        private int distance;
        private int ancestor;

        public Result(int distance, int ancestor) {
            this.distance = distance;
            this.ancestor = ancestor;
        }
        public int getDist() {
            return distance;
        }
        public int getAncestor() {
            return ancestor;
        }
    }

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        // make a clone of G to make SAP class immutable
        graph = new Digraph(G);
        //BreadthFirstDirectedPaths(Digraph G, int s)
    }

    private Result computeSAP(CustomBFS bfsV, CustomBFS bfsW) {
        ArrayList<Integer> listV = bfsV.getVertexOrder();
        ArrayList<Integer> listW = bfsW.getVertexOrder();
        // find minDist and vertex of V
        int minDist = Integer.MAX_VALUE;
        int ancestor = -1;
        for (int i = 0; i < listV.size(); i++) {
            int currentV = listV.get(i);
            int distV = bfsV.distTo(currentV);
            if (listW.contains(currentV)) {
                int distW = bfsW.distTo(currentV);
                int length = distV + distW;
                if (length < minDist) {
                    minDist = length;
                    ancestor = currentV;
                }
            }
        }
        // find minDist and vertex of W
        for (int i = 0; i < listW.size(); i++) {
            int currentW = listW.get(i);
            int distW = bfsW.distTo(currentW);
            if (listV.contains(currentW)) {
                int distV = bfsV.distTo(currentW);
                int length = distV + distW;
                if (length < minDist) {
                    minDist = length;
                    ancestor = currentW;
                }
            }
        }
        // if minDist does not change, then two bfs don't have a common ancestor
        if (minDist == Integer.MAX_VALUE) {
            minDist = -1;
        }
        Result result = new Result(minDist, ancestor);
        return result;
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        bfsV = new CustomBFS(this.graph, v);
        bfsW = new CustomBFS(this.graph, w);
        Result result = computeSAP(bfsV, bfsW);
        return result.getDist();
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        bfsV = new CustomBFS(this.graph, v);
        bfsW = new CustomBFS(this.graph, w);
        Result result = computeSAP(bfsV, bfsW);
        return result.getAncestor();
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        bfsV = new CustomBFS(this.graph, v);
        bfsW = new CustomBFS(this.graph, w);
        Result result = computeSAP(bfsV, bfsW);
        return result.getDist();
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        bfsV = new CustomBFS(this.graph, v);
        bfsW = new CustomBFS(this.graph, w);
        Result result = computeSAP(bfsV, bfsW);
        return result.getAncestor();
    }

    // do unit testing of this class
    public static void main(String[] args) {
        System.out.println("hello");
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        /*
        System.out.println(sap.length(3, 11));
        System.out.println(sap.length(9, 12));
        System.out.println(sap.length(7, 2));
        System.out.println(sap.length(1, 6));
        */
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }


    }
}
