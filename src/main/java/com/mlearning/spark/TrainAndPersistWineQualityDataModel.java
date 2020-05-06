package com.mlearning.spark;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.spark.ml.Model;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.classification.*;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.ml.evaluation.RegressionEvaluator;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.linalg.Vectors;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.ml.regression.DecisionTreeRegressor;
import org.apache.spark.ml.regression.LinearRegression;
import org.apache.spark.ml.regression.LinearRegressionModel;
import org.apache.spark.ml.regression.LinearRegressionTrainingSummary;
import org.apache.spark.ml.tuning.*;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.io.IOException;


public class TrainAndPersistWineQualityDataModel {

    public static final Logger logger =
            LogManager.getLogger(TrainAndPersistWineQualityDataModel.class);

    private static final String BUCKET_NAME = System.getProperty("BUCKET_NAME");

    private static final String ACCESS_KEY_ID = System.getProperty("ACCESS_KEY_ID");
    private static final String SECRET_KEY = System.getProperty("SECRET_KEY");

    private static final String TRAINING_DATASET = BUCKET_NAME + "TrainingDataset.csv";
    private static final String VALIDATION_DATASET = BUCKET_NAME + "ValidationDataset.csv";
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


        TrainAndPersistWineQualityDataModel parser = new TrainAndPersistWineQualityDataModel();
        // parser.getAWSCredentials();
        parser.logisticRegression(spark);
    }

    public AWSCredentials getAWSCredentials() {
        AWSCredentials credentials = new EnvironmentVariableCredentialsProvider().getCredentials();
        System.out.println(credentials.getAWSAccessKeyId());
        System.out.println(credentials.getAWSSecretKey());
        return credentials;
    }

    public void logisticRegression(SparkSession spark) {
        Dataset<Row> lblFeatureDf = getDataFrame(spark, true, TRAINING_DATASET).cache();
        LogisticRegression logReg = new LogisticRegression().setMaxIter(100).setRegParam(0.0);

        Pipeline pl1 = new Pipeline();
        pl1.setStages(new PipelineStage[]{logReg});

        PipelineModel model1 = pl1.fit(lblFeatureDf);


        LogisticRegressionModel lrModel = (LogisticRegressionModel) (model1.stages()[0]);
        // System.out.println("Learned LogisticRegressionModel:\n" + lrModel.summary().accuracy());
        LogisticRegressionTrainingSummary trainingSummary = lrModel.summary();
        double accuracy = trainingSummary.accuracy();
        double falsePositiveRate = trainingSummary.weightedFalsePositiveRate();
        double truePositiveRate = trainingSummary.weightedTruePositiveRate();
        double fMeasure = trainingSummary.weightedFMeasure();
        double precision = trainingSummary.weightedPrecision();
        double recall = trainingSummary.weightedRecall();

        System.out.println();
        System.out.println("Training DataSet Metrics ");

        System.out.println("Accuracy: " + accuracy);
        System.out.println("FPR: " + falsePositiveRate);
        System.out.println("TPR: " + truePositiveRate);
        System.out.println("F-measure: " + fMeasure);
        System.out.println("Precision: " + precision);
        System.out.println("Recall: " + recall);


        Dataset<Row> testingDf1 = getDataFrame(spark, true, VALIDATION_DATASET).cache();

        Dataset<Row> results = model1.transform(testingDf1);


        System.out.println("\n Validation Training Set Metrics");
        results.select("features", "label", "prediction").show(5, false);
        printMertics(results);

        try {
            model1.write().overwrite().save(MODEL_PATH);
        } catch (IOException e) {
            logger.error(e);
        }
    }

    public void decisionTreeClassificationModel(SparkSession spark) {

        Dataset<Row> lblFeatureLRDf = getDataFrame(spark, true, TRAINING_DATASET).cache();
        Dataset<Row> testingDf1 = getDataFrame(spark, true, VALIDATION_DATASET).cache();

        DecisionTreeClassifier dtClassifier = new DecisionTreeClassifier();
        dtClassifier.setMaxDepth(3);

        org.apache.spark.ml.classification.DecisionTreeClassificationModel model =
                dtClassifier.fit(lblFeatureLRDf);

        Dataset<Row> predictions = model.transform(testingDf1);
        predictions.show();
        System.out.println(model.toDebugString());
        printMertics(predictions);

        RandomForestClassifier rfClassifier = new RandomForestClassifier();
        rfClassifier.setNumTrees(10).setImpurity("gini");
        RandomForestClassificationModel rfModel = rfClassifier.fit(lblFeatureLRDf);
        Dataset<Row> predictions2 = rfModel.transform(testingDf1);
        predictions2.show();
        System.out.println(rfModel.toDebugString());
        printMertics(predictions2);


        MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator()
                .setLabelCol("label").setPredictionCol("prediction").setMetricName("accuracy");

        double acc = evaluator.evaluate(predictions2);
        System.out.println("Accuracy = " + acc);

        CrossValidator crossval =
                new CrossValidator().setEstimator(rfClassifier).setEvaluator(evaluator);
        ParamMap[] paramgrid = new ParamGridBuilder().addGrid(rfClassifier.numTrees(), new int[]{100})
                .addGrid(rfClassifier.maxDepth(), new int[]{30})
                .addGrid(rfClassifier.maxBins(), new int[]{10}).build();

        crossval.setEstimatorParamMaps(paramgrid);
        crossval.setNumFolds(10);

        CrossValidatorModel cv_model = crossval.fit(lblFeatureLRDf);
        Model<?> rf_model_best = cv_model.bestModel();
        Dataset<Row> prediction_best = rf_model_best.transform(testingDf1);
        printMertics(prediction_best);

        prediction_best.select("label", "prediction", "features").show();

        double acc_best = evaluator.evaluate(prediction_best);
        System.out.println("Accuracy Best = " + acc_best);
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

    public void decisionTreeRegressor(SparkSession spark) {
        Dataset<Row> lblFeatureLRDf = getDataFrame(spark, true, TRAINING_DATASET).cache();

        DecisionTreeRegressor dt = new DecisionTreeRegressor();

        Pipeline pipeline = new Pipeline().setStages(new PipelineStage[]{dt});

        PipelineModel model = pipeline.fit(lblFeatureLRDf);

        Dataset<Row> testData = getDataFrame(spark, true, VALIDATION_DATASET).cache();
        Dataset<Row> predictions = model.transform(testData);

        predictions.select("label", "features").show(5);

        RegressionEvaluator evaluator = new RegressionEvaluator().setLabelCol("label")
                .setPredictionCol("prediction").setMetricName("r2");

        System.out.println("The r2 " + evaluator.evaluate(predictions));
        evaluator.setMetricName("rmse");
        double rmse = evaluator.evaluate(predictions);
        System.out.println("Root Mean Squared Error (RMSE) on test data = " + rmse);
    }

    public void linearRegression(SparkSession spark) {
        Dataset<Row> lblFeatureLRDf = getDataFrame(spark, true, TRAINING_DATASET).cache();
        LinearRegression lr = new LinearRegression();
        // lr.setMaxIter(100).setRegParam(0.01).setElasticNetParam(0.0);

        LinearRegressionModel lrm = lr.fit(lblFeatureLRDf);
        System.out.println(
                "The model has intercept " + lrm.intercept() + "\n and coefficients " + lrm.coefficients());

        lrm.summary().rootMeanSquaredError();
        lrm.summary().r2();

        LinearRegressionTrainingSummary trainingSummary = lrm.summary();
        System.out.println("r2 squared: " + trainingSummary.r2());
        System.out.println("RMSE: " + trainingSummary.rootMeanSquaredError());
        System.out.println("numIterations: " + trainingSummary.totalIterations());
        System.out.println("objectiveHistory: " + Vectors.dense(trainingSummary.objectiveHistory()));

        Pipeline pl = new Pipeline().setStages(new PipelineStage[]{lrm});
        PipelineModel model = pl.fit(lblFeatureLRDf);


        LinearRegressionModel lrModel = (LinearRegressionModel) (model.stages()[0]);

        System.out.println(
                "Coefficients: " + lrModel.coefficients() + "\n Intercept: " + lrModel.intercept());


        LinearRegressionTrainingSummary summary = lrModel.summary();

        System.out.println("numIterations: " + summary.totalIterations());

        summary.residuals().show();

        System.out.println("r2: " + summary.r2());
        System.out.println("RMSE: " + summary.rootMeanSquaredError());
        System.out.println("Number of Features: " + lrModel.numFeatures());
        System.out.println("Coefficeints: " + lrModel.coefficients());
        System.out.println("Interceptor: " + lrModel.intercept());

        Dataset<Row> testData = getDataFrame(spark, true, VALIDATION_DATASET).cache();
        Dataset<Row> result = model.transform(testData);
        result.select("features", "label", "prediction").show(5, false);
        ParamGridBuilder paramGridBuilder = new ParamGridBuilder();

        ParamMap[] paramMap = paramGridBuilder.addGrid(lr.regParam(), new double[]{0.1, 0.5, 1.0})
                .addGrid(lr.elasticNetParam(), new double[]{0, 0.5, 1}).build();

        TrainValidationSplit trainValidationSplit = new TrainValidationSplit().setEstimator(lr)
                .setEvaluator(new RegressionEvaluator().setMetricName("r2"))
                .setEstimatorParamMaps(paramMap);

        TrainValidationSplitModel tsvm = trainValidationSplit.fit(lblFeatureLRDf);
        LinearRegressionModel lrmModel = (LinearRegressionModel) tsvm.bestModel();

        System.out.println("The training data r2 value is " + lrmModel.summary().r2()
                + " and the RMSE is " + lrmModel.summary().rootMeanSquaredError());

        System.out.println("The test data r2 value is " + lrmModel.evaluate(testData).r2()
                + " and the RMSE is " + lrmModel.evaluate(testData).rootMeanSquaredError());

        System.out.println(
                "coefficients : " + lrmModel.coefficients() + " intercept : " + lrmModel.intercept());
        System.out.println("reg param : " + lrmModel.getRegParam() + " elastic net param : "
                + lrmModel.getElasticNetParam());
    }

    public Dataset<Row> getDataFrame(SparkSession spark, boolean transform, String name) {

        Dataset<Row> validationDf = spark.read().format("csv").option("header", "true")
                .option("multiline", true).option("sep", ";").option("quote", "\"")
                .option("dateFormat", "M/d/y").option("inferSchema", true).load(name);


        validationDf = validationDf.withColumnRenamed("fixed acidity", "fixed_acidity")
                .withColumnRenamed("volatile acidity", "volatile_acidity")
                .withColumnRenamed("citric acid", "citric_acid")
                .withColumnRenamed("residual sugar", "residual_sugar")
                .withColumnRenamed("chlorides", "chlorides")
                .withColumnRenamed("free sulfur dioxide", "free_sulfur_dioxide")
                .withColumnRenamed("total sulfur dioxide", "total_sulfur_dioxide")
                .withColumnRenamed("density", "density").withColumnRenamed("pH", "pH")
                .withColumnRenamed("sulphates", "sulphates").withColumnRenamed("alcohol", "alcohol")
                .withColumnRenamed("quality", "label");

        validationDf.show(5);


        Dataset<Row> lblFeatureDf = validationDf.select("label", "alcohol", "sulphates", "pH",
                "density", "free_sulfur_dioxide", "total_sulfur_dioxide", "chlorides", "residual_sugar",
                "citric_acid", "volatile_acidity", "fixed_acidity");

        lblFeatureDf = lblFeatureDf.na().drop().cache();

        VectorAssembler assembler =
                new VectorAssembler().setInputCols(new String[]{"alcohol", "sulphates", "pH", "density",
                        "free_sulfur_dioxide", "total_sulfur_dioxide", "chlorides", "residual_sugar",
                        "citric_acid", "volatile_acidity", "fixed_acidity"}).setOutputCol("features");

        if (transform)
            lblFeatureDf = assembler.transform(lblFeatureDf).select("label", "features");


        return lblFeatureDf;
    }
}
