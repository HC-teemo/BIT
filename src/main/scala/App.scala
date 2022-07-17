import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.functions._
/**
 * @ClassName App
 * @Description
 * @Author Hu Chuan
 * @Date 2022/6/20
 * @Version 0.1
 */
object App {
  //屏蔽日志
  Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
  Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)

  def main(args: Array[String]): Unit = {

    val path = "H:\\TaxonomyTree\\dataset\\nodes.dmp"
//    val conf = new SparkConf()
//      .setAppName("TaxonomyTree")
//      .setMaster("local[*]")
//    val sc = new SparkContext(conf)
    val spark = SparkSession.builder()
      .appName("TaxonomyTree")
      .master("local[*]")
      .getOrCreate()

    val nodes = spark.read.csv(path).toDF("tax_id","parent_id")

  }
}
