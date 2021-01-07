package com.kude.demo;

public class ConcreteCarDecorator extends CarDecorator{
    public ConcreteCarDecorator(Car car) {
        super(car);
    }

    private void print() {
        System.out.println("给车绘上颜色");
    }

    private void setGps() {
        System.out.println("给车装上gdp");
    }

    public void show() {
        super.show();
        this.print();
        this.setGps();
    }
}
