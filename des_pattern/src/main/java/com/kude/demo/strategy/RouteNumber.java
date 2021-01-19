package com.kude.demo.strategy;

import com.kude.demo.Vertex;

import java.util.List;
import java.util.Map;

public interface RouteNumber<T> {

    public double getRouteNumberByCondition(List<Map<Vertex<T>, List<Double>>> vertexRouteList, Integer stop, T endLabel);

}
