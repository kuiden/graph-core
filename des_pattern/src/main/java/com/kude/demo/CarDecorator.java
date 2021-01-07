package com.kude.demo;

/**
 * 装饰类
 */
public abstract class CarDecorator implements Car{

    private Car car;

    public CarDecorator(Car car) {
        this.car = car;
    }

    @Override
    public void show() {
        this.car.show();
    }
}
