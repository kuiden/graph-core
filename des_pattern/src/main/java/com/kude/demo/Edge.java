package com.kude.demo;

/**
 * 连接两个顶点的边
 *
 * @author xusy
 */
public class Edge<T> {

    /**
     * beginVertex是边的起始顶点<br>
     * 普通情况是不用显示地存储beginVertex，但是生成最小生成树时需要
     */
    private Vertex<T> beginVertex;

    /**
     * 由于Edge是存储在Vertex中的，所以包含这个边的vertex是开始点
     * endVertex是结束点
     */
    private Vertex<T> endVertex;

    /**
     * 边的权值
     */
    private double weight;

    /**
     * @Description:
     * @Date: 2021/1/13
     * @Param beginVertex:
     * @Param endVertex:
     * @Param weight:
     * @return: null
     **/
    public Edge(Vertex<T> beginVertex, Vertex<T> endVertex, double weight) {
        this.beginVertex = beginVertex;
        this.endVertex = endVertex;
        this.weight = weight;
    }

    public Vertex<T> getBeginVertex() {
        return beginVertex;
    }

    public void setBeginVertex(Vertex<T> beginVertex) {
        this.beginVertex = beginVertex;
    }

    public void setEndVertex(Vertex<T> endVertex) {
        this.endVertex = endVertex;
    }

    /**
     * 返回边的结束点
     *
     * @return Vertex<T>
     */
    public Vertex<T> getEndVertex() {
        return endVertex;
    }

    /**
     * 返回边的权值
     *
     * @return double
     */
    public double getWeight() {
        return weight;
    }

    /**
     * 设置边的权值
     * weight
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

}