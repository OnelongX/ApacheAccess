package com.ways2u

class OrderingUtils extends scala.Ordering[(String, Int)] {
  override def compare(x: (String, Int), y: (String, Int)): Int = {
    x._2.compare(y._2)
  }
}

