package com.ways2u

/**
  * Created by huanglong on 2016/12/30.
  */
//伴生对象
class Sinleton private(){

}

object Sinleton{

  def getInstance:Sinleton={
    new Sinleton();
  }
}