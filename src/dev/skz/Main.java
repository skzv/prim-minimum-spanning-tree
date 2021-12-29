package dev.skz;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        Graph graph = Graph.loadGraph();
        List<Graph.Edge> mst = findMinimumSpanningTreePrim(graph, PrimMSTAlgorithm.HEAP_OPTIMIZED);
        int cost = calculateCostOfSpanningTree(mst);
        System.out.println(cost);
    }

    public static int calculateCostOfSpanningTree(List<Graph.Edge> tree) {
        return tree.stream().map(edge -> edge.weight).reduce(0, Integer::sum);
    }

    public enum PrimMSTAlgorithm {
        SIMPLE,
        HEAP_OPTIMIZED
    }

    public static List<Graph.Edge> findMinimumSpanningTreePrim(Graph graph, PrimMSTAlgorithm primMSTAlgorithm) {
        List<Graph.Edge> mst = new ArrayList<>();
        Set<Graph.Vertex> x = new HashSet<>(graph.vertices.size());

        if (graph.vertices.isEmpty()) {
            return mst;
        }

        Graph.Vertex s = graph.vertices.values().iterator().next();
        x.add(s);

        if (primMSTAlgorithm == PrimMSTAlgorithm.SIMPLE) {
            while (x.size() < graph.vertices.size()) {
                int minWeight = Integer.MAX_VALUE;
                Graph.Vertex wStar = null;
                Graph.Edge eStar = null;
                for (Graph.Vertex v : x) {
                    for (Graph.Edge e : v.edges) {
                        Graph.Vertex w = e.getOtherVertex(v);
                        if (!x.contains(w)) {
                            if (e.weight < minWeight) {
                                minWeight = e.weight;
                                wStar = w;
                                eStar = e;
                            }
                        }
                    }
                }

                if (eStar != null) {
                    x.add(wStar);
                    mst.add(eStar);
                }
            }
        } else {
            // initialize
            Map<Graph.Vertex, ComparableVertex> mapVertexToComparableVertex = new HashMap<>();
            Queue<ComparableVertex> h = new PriorityQueue<>();
            for (Graph.Vertex v : graph.vertices.values()) {
                if (v == s) {
                    continue;
                }

                // find winning edge
                Graph.Edge winningEdge = null;
                int winningWeight = Integer.MAX_VALUE;

                for (Graph.Edge e : v.edges) {
                    if (e.getOtherVertex(v) == s) {
                        if (e.weight < winningWeight) {
                            winningWeight = e.weight;
                            winningEdge = e;
                        }
                    }
                }

                ComparableVertex keyedV = new ComparableVertex(winningWeight, v, winningEdge);
                mapVertexToComparableVertex.put(keyedV.vertex, keyedV);
                h.add(keyedV);
            }

            // Main loop
            while (!h.isEmpty()) {
                ComparableVertex wStar = h.poll();
                x.add(wStar.vertex);
                mst.add(wStar.winningEdge);
                // update keys to maintain invariant
                for (Graph.Edge e : wStar.vertex.edges) {
                    Graph.Vertex y = e.getOtherVertex(wStar.vertex);
                    if (!x.contains(y)) {
                        ComparableVertex keyedY = mapVertexToComparableVertex.get(y);
                        if (e.weight < keyedY.weight) {
                            // replace
                            h.remove(keyedY);
                            ComparableVertex newKeyedY = new ComparableVertex(e.weight, y, e);
                            mapVertexToComparableVertex.replace(y, newKeyedY);
                            h.add(newKeyedY);
                        }
                    }
                }
            }
        }

        return mst;
    }

    public static class ComparableVertex implements Comparable<ComparableVertex> {

        private int weight;
        public Graph.Vertex vertex;
        public Graph.Edge winningEdge;

        public ComparableVertex(int weight, Graph.Vertex vertex, Graph.Edge winningEdge) {
            this.weight = weight;
            this.vertex = vertex;
            this.winningEdge = winningEdge;
        }

        @Override
        public int compareTo(ComparableVertex o) {
            return (int) (((long) weight - (long) o.weight) % Integer.MAX_VALUE);
        }
    }

    public static class Graph {
        Map<Integer, Vertex> vertices;
        List<Edge> edges;

        public static Graph loadGraph() {
            String file = "input.txt";
            Graph graph = null;

            try (
                    BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line = reader.readLine();
                if (line != null) {
                    String[] tokens = line.split(" ");
                    int numVertices = Integer.parseInt(tokens[0]);
                    int numEdges = Integer.parseInt(tokens[1]);
                    graph = new Graph(numVertices, numEdges);
                }

                while ((line = reader.readLine()) != null) {
                    String[] tokens = line.split(" ");
                    int u = Integer.parseInt(tokens[0]);
                    int v = Integer.parseInt(tokens[1]);
                    int weight = Integer.parseInt(tokens[2]);
                    graph.addVertexIfItDoesNotExist(u);
                    graph.addVertexIfItDoesNotExist(v);
                    graph.addEdge(u, v, weight);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return graph;
        }

        private Graph(int numVertices, int numEdges) {
            vertices = new HashMap<>(numVertices);
            edges = new ArrayList<>(numEdges);
        }

        public void addVertexIfItDoesNotExist(int value) {
            if (vertices.containsKey(value)) {
                return;
            }

            vertices.put(value, new Vertex(value));
        }

        public void addEdge(int uValue, int vValue, int weight) {
            Vertex u = vertices.get(uValue);
            Vertex v = vertices.get(vValue);
            Edge e = new Edge(u, v, weight);
            u.addEdge(e);
            v.addEdge(e);
            edges.add(e);
        }

        public static class Vertex {
            public int value;
            public List<Edge> edges = new ArrayList<>();

            public Vertex(int value) {
                this.value = value;
            }

            public void addEdge(Edge e) {
                edges.add(e);
            }
        }

        public static class Edge {
            public Vertex u;
            public Vertex v;
            public int weight;

            public Edge(Vertex u, Vertex v, int weight) {
                this.u = u;
                this.v = v;
                this.weight = weight;
            }

            public Vertex getOtherVertex(Vertex u) {
                if (this.u == u) {
                    return v;
                } else {
                    return this.u;
                }
            }
        }
    }
}
