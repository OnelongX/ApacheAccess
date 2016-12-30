package com.ways2u

/**
  * Created by huanglong on 2016/11/10.
  */
object TestScala {
  def main(args: Array[String]): Unit = {

    val arr = Array(1, 3, 4, 5, 6, 7, 7, 444);
    val total = arr.reduce(_ + _)
    println(total)

    val p = new Student("huang", 29)
    println(p)
    println(new Person())

    println("----------------------")
    val loc = new Location(10, 20, 15);

    // 移到一个新的位置
    loc.move(10, 10, 5);
    println("----------------------")
    loc.move(10, 10)


  }
}

class Person(val name: String, val age: Int) {
  // private[Person] val this.name:String = name; //private[作用域] 如this ，Person
  //protected var this.age:Int = age;

  def this() = {
    this("onelong", 33)
  }

  override def toString: String = {
    s"""${name} : ${age}"""
  }
}

class Student(override val name: String, override val age: Int) extends Person(name, age) {
  override def toString: String = super.toString
}


class Point(val xc: Int, val yc: Int) {
  var x: Int = xc
  var y: Int = yc
  var t: Int = _ //保留

  def move(dx: Int, dy: Int) {
    x = x + dx
    y = y + dy

    println("x 的坐标点 : " + x);
    println("y 的坐标点 : " + y);
    println("t 的坐标点 : " + t);

    t = 4
  }
}

class Location(override val xc: Int, override val yc: Int,
               val zc: Int) extends Point(xc, yc) {
  var z: Int = zc

  def move(dx: Int, dy: Int, dz: Int) {
    x = x + dx
    y = y + dy
    z = z + dz

    println("x 的坐标点 : " + x);
    println("y 的坐标点 : " + y);
    println("z 的坐标点 : " + z);
  }
}




