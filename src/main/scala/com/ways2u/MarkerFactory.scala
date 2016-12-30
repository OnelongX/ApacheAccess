package com.ways2u

/**
  * Created by huanglong on 2016/12/30.
  */
class Marker{

  def getMap: Map[String, Int] ={
    Map("a"->1)
  }

}

object MarkerFactory
{
  def getMarker:Marker={
    new Marker()
  }
}