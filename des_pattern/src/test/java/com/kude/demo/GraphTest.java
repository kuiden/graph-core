package com.kude.demo;

import com.kude.demo.strategy.RouteNumber;
import com.kude.demo.strategy.RouteNumberByDistance;
import com.kude.demo.strategy.RouteNumberByStop;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GraphTest {

    Graph<String> graph=new Graph<>(true);

    @BeforeEach
    public void setUp() {
        graph.addVertex("A", 0, "A");
        graph.addVertex("B", 0, "B");
        graph.addVertex("C", 0, "C");
        graph.addVertex("D", 0, "D");
        graph.addVertex("E", 0, "E");
        graph.addEdge("A", "B", 5);
        graph.addEdge("B", "C", 4);
        graph.addEdge("C", "D", 8);
        graph.addEdge("D", "C", 8);
        graph.addEdge("D", "E", 6);
        graph.addEdge("A", "D", 5);
        graph.addEdge("C", "E", 2);
        graph.addEdge("E", "B", 3);
        graph.addEdge("A", "E", 7);
    }

//    @AfterEach
//    public void tearDown() {
//    }
//
//    @Test
//    void getVertexCount() {
//    }
//
//    @Test
//    void getVertexIterator() {
//    }
//
//    @Test
//    void addVertex() {
//    }

    @Test
    public void getDirectEdgeWight() {
        try {
            double totalWeight = graph.getDirectRouteWeight(Arrays.asList("A", "B", "C"));
            assertEquals(9.0, totalWeight, "获取路径距离错误");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Test
//    public void getEdgeCount() {
//    }
//
//    @Test
//    public void testGetEdgeCount() {
//    }
//
//    @Test
//    public void getEdgeIterator() {
//    }
//
//    @Test
//    public void addEdge() {
//    }
//
//    @Test
//    public void removeEdge() {
//    }
//
//    @Test
//    void printGraph() {
//    }
//
//    @Test
//    public void getDirectRouteWeight() {
//    }

    @Test
    public void getRouteNumberByStopsFromCToC() {
        double count = 0.0;
        List<Map<Vertex<String>, List<Double>>> vertexRouteList;
        RouteNumber<String> routeNumber = new RouteNumberByStop<>();
        for (int i = 2; i < 4; i++) {
            vertexRouteList = graph.getRouteNumberByCondition("C", "C", i);
            count += routeNumber.getRouteNumberByCondition(vertexRouteList, i, "C");
        }
        assertEquals(2.0, count);
    }

    @Test
    public void getRouteNumberByStopFromAToC() {
        List<Map<Vertex<String>, List<Double>>> vertexRouteList = graph.getRouteNumberByCondition("A", "C", 4);
        RouteNumber<String> routeNumber = new RouteNumberByStop<>();
        double count = routeNumber.getRouteNumberByCondition(vertexRouteList, 4, "C");
        assertEquals(3.0, count);
    }

    @Test
    public void getRouteNumberByPath() {
        double count = 0.0;
        RouteNumber<String> routeNumber = new RouteNumberByDistance<>();
        List<Map<Vertex<String>, List<Double>>> vertexRouteList;
        for (int i = 2; i < 30; i++) {
            vertexRouteList = graph.getRouteNumberByCondition("C", "C", i);
            count += routeNumber.getRouteNumberByCondition(vertexRouteList, i, "C");
        }
        assertEquals(7.0, count);
    }

//    @Test
//    public void getVertexSet() {
//    }

    @Test
    public void getSmallestDistanceDijkstra() {
        Map<Vertex<String>, Double> distanceMap = graph.getSmallestDistanceDijkstra("A");
        Double result = distanceMap.get(graph.getVertexMap().get("C"));
        assertEquals(9.0, result);
    }

    @Test
    public void getSmallestDistanceFloyd() {
    }
}