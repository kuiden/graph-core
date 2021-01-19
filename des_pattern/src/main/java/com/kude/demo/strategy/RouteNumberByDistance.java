package com.kude.demo.strategy;

import com.kude.demo.Edge;
import com.kude.demo.Vertex;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class RouteNumberByDistance<T> implements RouteNumber<T> {

    @Override
    public double getRouteNumberByCondition(List<Map<Vertex<T>, List<Double>>> vertexRouteList, Integer stop, T endLabel) {
        AtomicInteger count = new AtomicInteger();
        vertexRouteList.get(stop - 2).forEach((vertex, weightList) -> {
            Iterator<Edge<T>> mediateVertexEdgeIterator = vertex.getEdgeIterator();
            while (mediateVertexEdgeIterator.hasNext()) {
                Edge<T> endEdge = mediateVertexEdgeIterator.next();
                if (endLabel.equals(endEdge.getEndVertex().getLabel())) {
                    weightList.forEach(weight -> {
                        if (weight + endEdge.getWeight() < 30) {
                            count.incrementAndGet();
                        }
                    });
                }
            }
        });
        return count.get();
    }

}
