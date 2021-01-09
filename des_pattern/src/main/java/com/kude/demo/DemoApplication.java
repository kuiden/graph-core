package com.kude.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;

//@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
//        SpringApplication.run(DemoApplication.class, args);

//        Car car = new Benz();
//        CarDecorator carDecorator = new ConcreteCarDecorator(car);
//        carDecorator.show();

        Graph<String> graph=new Graph<>(true);
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

        try {
            double totalWeight = graph.getDirectRouteWeight(Arrays.asList("A", "B", "C"));
            int count = graph.getRouteNumberByStops("C", "C", 1);
            graph.getSmallestDistanceDijkstra("A");
//            graph.getSmallestDistanceFloyd();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        System.out.println(count);
//        graph.printGraph();
//
//        graph.removeEdge("first", "second");
//        graph.printGraph();

    }

}
