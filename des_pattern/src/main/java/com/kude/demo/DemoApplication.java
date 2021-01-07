package com.kude.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

//@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
//        SpringApplication.run(DemoApplication.class, args);

//        Car car = new Benz();
//        CarDecorator carDecorator = new ConcreteCarDecorator(car);
//        carDecorator.show();

        Graph<String> graph=new Graph<>(true);
        graph.addVertex("first", 0);
        graph.addVertex("second", 0);
        graph.addVertex("third", 1);
        graph.addEdge("first", "second", 1);
        graph.addEdge("first", "third", 2);


        System.out.println("完成");
//        graph.printGraph();
//
//        graph.removeEdge("first", "second");
//        graph.printGraph();

    }

}
