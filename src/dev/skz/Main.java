package dev.skz;

import java.util.*;

public class Main {

    public static void main(String[] args) {
	// write your code here
    }

    public static class Graph {
        Map<Integer, Vertex> vertices;
        List<Edge> edges;

        public static Graph loadGraph() {

        }

        private Graph(int numVertices, int numEdges) {
            vertices = new HashMap<>(numVertices);
            edges = new ArrayList<>(numEdges);
        }

        public void addVertex(int value) {
            vertices.put(value, new Vertex(value));
        }

        public void addEdge(int uValue, int vValue) {
            Vertex u = vertices.get(uValue);
            Vertex v = vertices.get(vValue);
            Edge e = new Edge(u, v);
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

            public Edge(Vertex u, Vertex v) {
                this.u = u;
                this.v = v;
            }
        }
    }
}
