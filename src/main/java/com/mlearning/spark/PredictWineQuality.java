package com.mlearning.spark;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;


public class PredictWineQuality {

    public static final Logger logger =
            LogManager.getLogger(PredictWineQuality.class);


    private static final String BUCKET_NAME = System.getProperty("BUCKET_NAME", "dataset/");

    private static final String ACCESS_KEY_ID = System.getProperty("ACCESS_KEY_ID");
    private static final String SECRET_KEY = System.getProperty("SECRET_KEY");

    private static final String TESTING_DATASET = BUCKET_NAME + "TestDataset.csv";
    private static final String MODEL_PATH = BUCKET_NAME + "LogisticRegression";

    private static final String MASTER_URI = "local[*]";

    public static void main(String[] args) {

        Logger.getLogger("org").setLevel(Level.ERROR);
        Logger.getLogger("akka").setLevel(Level.ERROR);
        Logger.getLogger("breeze.optimize").setLevel(Level.ERROR);
        Logger.getLogger("com.amazonaws.auth").setLevel(Level.DEBUG);

        SparkSession spark = SparkSession.builder()
                .appName("Wine-Quality-Dataset-Model-Training-And-Prediction").master(MASTER_URI)
                .config("spark.executor.memory", "3g").config("spark.driver.memory", "3g")
                // .config("fs.s3a.awsAccessKeyId", ACCESS_KEY_ID)
                // .config("fs.s3a.awsSecretAccessKey", SECRET_KEY)
                // .config("fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem")
                .getOrCreate();

        if (StringUtils.isNotEmpty(ACCESS_KEY_ID) && StringUtils.isNotEmpty(SECRET_KEY)) {

            spark.sparkContext().hadoopConfiguration().set("fs.s3a.access.key", ACCESS_KEY_ID);
            spark.sparkContext().hadoopConfiguration().set("fs.s3a.secret.key", SECRET_KEY);

        }

        PredictWineQuality parser = new PredictWineQuality();
        parser.logisticRegression(spark);
    }


    public void logisticRegression(SparkSession spark) {
        System.out.println("TestingDataSet Metrics \n");
        PipelineModel pipelineModel = PipelineModel.load(MODEL_PATH);
        Dataset<Row> testDf = getDataFrame(spark, true, TESTING_DATASET).cache();
        Dataset<Row> predictionDF = pipelineModel.transform(testDf).cache();
        predictionDF.select("features", "label", "prediction").show(5, false);
        printMertics(predictionDF);

    }

    public Dataset<Row> getDataFrame(SparkSession spark, boolean transform, String name) {

        Dataset<Row> validationDf = spark.read().format("csv").option("header", "true")
                .option("multiline", true).option("sep", ";").option("quote", "\"")
                .option("dateFormat", "M/d/y").option("inferSchema", true).load(name);

        Dataset<Row> lblFeatureDf = validationDf.withColumnRenamed("quality", "label").select("label",
                "alcohol", "sulphates", "pH", "density", "free sulfur dioxide", "total sulfur dioxide",
                "chlorides", "residual sugar", "citric acid", "volatile acidity", "fixed acidity");

        lblFeatureDf = lblFeatureDf.na().drop().cache();

        VectorAssembler assembler =
                new VectorAssembler().setInputCols(new String[]{"alcohol", "sulphates", "pH", "density",
                        "free sulfur dioxide", "total sulfur dioxide", "chlorides", "residual sugar",
                        "citric acid", "volatile acidity", "fixed acidity"}).setOutputCol("features");

        if (transform)
            lblFeatureDf = assembler.transform(lblFeatureDf).select("label", "features");


        return lblFeatureDf;
    }


    public void printMertics(Dataset<Row> predictions) {
        System.out.println();
        MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator();
        evaluator.setMetricName("accuracy");
        System.out.println("The accuracy of the model is " + evaluator.evaluate(predictions));

        evaluator.setMetricName("accuracy");
        double accuracy1 = evaluator.evaluate(predictions);
        System.out.println("Test Error = " + (1.0 - accuracy1));

        evaluator.setMetricName("f1");
        double f1 = evaluator.evaluate(predictions);

        evaluator.setMetricName("weightedPrecision");
        double weightedPrecision = evaluator.evaluate(predictions);

        evaluator.setMetricName("weightedRecall");
        double weightedRecall = evaluator.evaluate(predictions);

        System.out.println("Accuracy: " + accuracy1);
        System.out.println("F1: " + f1);
        System.out.println("Precision: " + weightedPrecision);
        System.out.println("Recall: " + weightedRecall);

    }
}
