package com.kude.demo;

//火车停站参数
public class StopModel<T> {

    //图节点
    private Vertex<T> vertex;

    //停站数
    private Integer stop;

    public Vertex<T> getVertex() {
        return vertex;
    }

    public void setVertex(Vertex<T> vertex) {
        this.vertex = vertex;
    }

    public Integer getStop() {
        return stop;
    }

    public void setStop(Integer stop) {
        this.stop = stop;
    }

}
