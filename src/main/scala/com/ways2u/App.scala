package com.ways2u

import org.apache.spark.{SparkConf, SparkContext}

case class ApacheAccessLog(
                            ipAddress: String,
                            clientIdentd: String,
                            userId: String,
                            dataTime: String,
                            method: String,
                            endPoint: String,
                            protocol: String,
                            responseCode: Int,
                            contentSize: String
                          )

object ApacheAccessLog {
  // regex
  //单行日志格式
  // 1.1.1.1 - - [21/Jul/2014:10:00:00 -0800] "GET /apps/logs/LogAnalyzer HTTP/1.1" 200 1234
  private val LINE_PARTTERN =
  """^(\S+) (\S+) (\S+) \[([\w:/]+\s[+\-]\d{4})\] "(\S+) (\S+) (\S+)" (\d{3}) (\S+)""".r

  def isValidateLogLine(log: String): Boolean = {
    val res = LINE_PARTTERN.findFirstMatchIn(log)
    if (res.isEmpty) {
      false
    } else {
      true
    }
  }

  def parseLogLine(log: String): ApacheAccessLog = {
    val res = LINE_PARTTERN.findFirstMatchIn(log)
    if (res.isEmpty) {
      throw new RuntimeException("解析失败: " + log)
    }
    val m = res.get
    ApacheAccessLog(
      m.group(1), m.group(2), m.group(3),
      m.group(4), m.group(5), m.group(6),
      m.group(7),
      m.group(8).toInt,
      m.group(9))
  }
}


object App {

  def isAllDigits(x: String) = x forall Character.isDigit

  def main(args: Array[String]): Unit = {
    val start = System.currentTimeMillis();
    val logFile = "file:///Users/huanglong/Desktop/HadoopHello/access_05_30.log"

    val conf = new SparkConf().setAppName("Log App").setMaster("local[*]")//.setSparkHome("spark")
    val sc = new SparkContext(conf)
    val logData = sc.textFile(logFile, 4)

    val filterData = logData.filter(line => ApacheAccessLog.isValidateLogLine(line))
    //val filterData = logData.filter(line => (line.contains("GET") || line.contains("POST"))/* && !line.contains("/static/")*/);
    val accessLogs = filterData.map(line => ApacheAccessLog.parseLogLine(line))
    /**
      * cache ,如果某个RDD反复的被使用，可以考虑将其进行cache
      */
    accessLogs.cache()
    //总记录数
    println("总记录数 : " + accessLogs.count())

    val contentSiezes = accessLogs
      .filter(_.contentSize.matches("^\\d*$"))
      .map(_.contentSize.toLong)

    contentSiezes.cache()

    val totalContentSize = contentSiezes.reduce(_ + _)
    val avgContentSize = totalContentSize / contentSiezes.count()
    val minContentSize = contentSiezes.min()
    val maxContextSize = contentSiezes.max()

    contentSiezes.unpersist()

    println("页面总流量: %s MB, 平均: %s B, 最小: %s B, 最大: %s B".format(
      totalContentSize / (1024 * 1024), avgContentSize, minContentSize, maxContextSize
    ))
    //统计状态码
    val responseCodeToCount = accessLogs
      .map(log => (log.responseCode, 1))
      .reduceByKey(_ + _)
      .sortBy(_._1, false)
      .take(10)
    println(s"""状态码: ${responseCodeToCount.mkString("[", ",", "]")}""")
    //ip 地址
    val ipAddresses = accessLogs
      .map(log => (log.ipAddress, 1))
      .reduceByKey(_ + _)
      .sortBy(_._2, false)
      .take(20)
    //存储到本地
    //ipAddresses.saveAsTextFile("file:///Users/huanglong/Desktop/HadoopHello/iss.txt")
    println(s"""IP地址 : ${ipAddresses.mkString("[", ",", "]")}""")

    //网址
    val topEndpoints = accessLogs
      .filter(!_.endPoint.contains("/static/"))
      .map(log => (log.endPoint, 1))
      .reduceByKey(_ + _)
      .top(10)(new OrderingUtils())
    println(s"""最热链接 : ${topEndpoints.mkString("[", ",", "]")}""")
    //http 版本
    val protocols = accessLogs
      .map(line => (line.protocol, 1))
      .reduceByKey(_ + _)
      .sortBy(_._1, false)
      .take(10);
    println(s"""协议 : ${protocols.mkString("[", ",", "]")}""")

    accessLogs.unpersist()

    sc.stop()

    println(s"""耗时:${System.currentTimeMillis() -start}""")
  }
}

