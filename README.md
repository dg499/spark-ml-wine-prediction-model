# CS 643 102, Cloud Computing - Spring ‘20

## Programming Assignment 2
This is the code repository for developing machine learning application to predict the quality of wine using Spark MLib on AWS Cloud Platform. It contains all the supporting project files necessary to build, train and predict the wine quality from start to finish.

## Source Control

* [Git URL](https://github.com/dg499/spark-ml-wine-prediction-model) - https://github.com/dg499/spark-ml-wine-prediction-model

## Docker Images

* [Prediction Image](https://hub.docker.com/repository/docker/dg499/spark-ml-prediction) - https://hub.docker.com/repository/docker/dg499/spark-ml-prediction
* [Training Image](https://hub.docker.com/repository/docker/dg499/winequalitydatset) - https://hub.docker.com/repository/docker/dg499/winequalitydatset

## Overview
* [Setup Instructions and Navigation](#setup-instructions-and-navigation)
* [Running Examples](#running-examples)
* [Parallel training implementation](#parallel-training-implementation)
  - [EMR Cluster Setup](#emr-cluster-setup)
  - [Submitting Spark Job For Parallel Training](#submitting-spark-job-for-parallel-training)
  - [Verifying Model Output On S3 Bucket](#submitting-spark-job-for-parallel-training)
* [Single Machine Prediction Application](#single-machine-prediction-application)
  - [EC2 Setup](#ec2-setup)
  - [Running Prediction Application](#running-prediction-application)

## Setup Instructions and Navigation

### Assumed Knowledge

To fully understand and work with the code, you will need:<br/>
•	Prior knowledge of the Java 8, Maven, Apache Spark, Docker, AWS Cloud<br/>
•	Familiarity with Git and GitHub for source control<br/>
•	configuring user and environment variables on windows os<br/>

### Technical Requirements

This project is built on following software requirements: <br/> <br/>
  •	[IntelliJ IDEA][intellij] / [Eclipse][eclipse] / [VSCode][vscode] / [STS][sts]<br/>
  •	[Java JDK][jdk]<br/>
  •	[Scala SDK][scala]<br/>
  •	[Apache Spark][spark]<br/>
  •	[Maven][maven]<br/>
  •	[Docker][docker]<br/>
  •	[AWS][aws]<br/>
  •	[Hadoop-aws][hadoop-aws]<br/>

## Getting Started

#### Sign up for AWS ####

Before you begin, you need an AWS account. Please see the [Sign Up for AWS][docs-signup] section of
the developer guide for information about how to create an AWS account and retrieve your AWS
credentials.

#### Install or update Java #### 
Install oracle jdk 8 or openjdk 8 and configure the JAVA_HOME environment variable. and update PATH variable to %JAVA_HOME%\bin;
JAVA_HOME: 
PATH:
open the command prompt and check the java version.
```
C:\>java -version
java version "1.8.0_201"
Java(TM) SE Runtime Environment (build 1.8.0_201-b09)
Java HotSpot(TM) 64-Bit Server VM (build 25.201-b09, mixed mode)  
  ```
#### Install Maven #### 
Install apache maven and configure the M2_HOME environment variable. and update PATH variable to %M2_HOME%\bin;
open the command prompt and check the mvn version.
```
C:\>mvn -version
Apache Maven 3.6.2 (40f52333136460af0dc0d7232c0dc0bcf0d9e117; 2019-08-27T11:06:16-04:00)
Maven home: C:\Programs\maven\bin\..
Java version: 1.8.0_201, vendor: Oracle Corporation, runtime: C:\Programs\Java\jdk1.8.0_201\jre
Default locale: en_US, platform encoding: Cp1252
OS name: "windows 10", version: "10.0", arch: "amd64", family: "windows"
```
#### Install SBT & Scala #### 
Install sbt scala and configure the SCALA_HOME environment variable. and update PATH variable to include %SCALA_HOME%\bin;
open the command prompt and check the scala version.
```
C:\>scala -version
Scala code runner version 2.13.1 -- Copyright 2002-2019, LAMP/EPFL and Lightbend, Inc.

C:\>sbt -version
sbt version in this project: 1.3.10
sbt script version: 1.3.10
```

#### Install Docker #### 
Install docker for windows. installation automatically configures the docker executables on PATH variable 
open the command prompt and check the docker version.

```
C:\>docker --version
Docker version 19.03.5, build 633a0ea
```

#### Install Git #### 
Install git scm for windows. installation automatically configures the git executables on PATH variable 
open the command prompt and check the git version.
```
C:\>git --version
git version 2.24.0.windows.2  
```

#### Install Spark for Standalone cluster setup #### 
Download a pre-built version of Apache Spark 2.x and extract the spark archive using 7-zip / winrar / rarlab, and copy its contents into C:\spark directory. the directory structure should look like c:\spark\bin, c:\spark\conf, etc.

Download [winutils.exe](https://github.com/steveloughran/winutils) and move it into a C:\spark\bin folder.

Create a c:\tmp\hive directory, and cd into c:\spark\bin, and run winutils.exe chmod 777 c:\tmp\hive

HADOOP_HOME=C:\spark <br/>
SPARK_HOME=c:\spark <br/>
SPARK_LOCAL_IP=localhost <br/>
SPARK_MASTER_HOST=localhost <br/>

open the command prompt and check the spark version.

```
C:\>spark-shell --version
Welcome to
      ____              __
     / __/__  ___ _____/ /__
    _\ \/ _ \/ _ `/ __/  '_/
   /___/ .__/\_,_/_/ /_/\_\   version 2.4.5
      /_/

Using Scala version 2.11.12, Java HotSpot(TM) 64-Bit Server VM, 1.8.0_201
Branch HEAD
Compiled by user centos on 2020-02-02T19:38:06Z
Revision cee4ecbb16917fa85f02c635925e2687400aa56b
Url https://gitbox.apache.org/repos/asf/spark.git
Type --help for more information.
```

This project has been tested on the following system configuration:<br/>
	•	OS: OS Name	Microsoft Windows 10 Enterprise <br/>
	•	Processor: I7 <br/>
	•	Memory: 16GB<br/>
	•	Hard Disk Space: 200GB<br/>
	•	Video Card: 8260MB Video Memory<br/>
  
## Running Examples

- Download the zip or clone the Git repository.
- Unzip the zip file (if you downloaded one)
- Open Command Prompt and Change directory (cd) to folder containing pom.xml
- run mvn package
- Open Eclipse 
   - File -> Import -> Existing Maven Project -> Navigate to the folder where you unzipped the zip
   - Select the right project
   - Finish importing into eclipse.
- Right Click on the project and select maven and update the project.

### Running with local dataset on local machine:
- Select TrainAndPersistWineQualityDataModel.java file  from package explorer and Run as Java Application
- From the eclipse menu Run -> Run configurations -> select Java Application from navigation tree -> on the right pane
select Arguments tab -> under vm arguments paste the following configuration for getting training, validation and testing dataset from local file system and saving model to dataset folder. click on Apply and Run the program.

```
-DBUCKET_NAME=dataset/
```

### Running with S3 dataset on local machine:
- From the eclipse menu Run -> Run configurations -> select Java Application from navigation tree -> select TrainAndPersistWineQualityDataModel -> right click and duplicate -> select the duplicated -> on the right pane
select Arguments tab -> under vm arguments paste the following configuration for getting training, validation and testing dataset from aws s3 bucket and saving model to s3 bucket. click on Apply and Run the program.
TrainAndPersistWineQualityDataModel
```
-DBUCKET_NAME=  -DACCESS_KEY_ID= -DSECRET_KEY=
```

[![Image](https://github.com/dg499/spark-ml-wine-prediction-model/blob/master/images/running-jar-local-and-s3.png "local instance prediction f1 score and accuracy")](https://github.com/dg499/spark-ml-wine-prediction-model/)

### note ###
- This project assumes the file names used for training , validation and testing is constant, bucket names are dynamic.
  Folder strcture should look like below.
  * dataset<br/>
    • TestDataset.csv <br/>
    • TrainingDataset.csv <br/>
    • ValidationDataset.csv<br/>
    
### Running with local dataset on docker container:

```
Open Command Prompt and Change directory (cd) to folder containing pom.xml

mvn package

verify the jar builded working properly.

java -DBUCKET_NAME=dataset/ -jar target/winequalitydatset-1.0.jar

docker build -t dg499/spark-prediction:1 . 

docker image ls

docker run --rm -m 4g -e BUCKET_NAME=dataset/ dg499/spark-prediction:1

docker login

docker push <image>

```

[![Image](https://github.com/dg499/spark-ml-wine-prediction-model/blob/master/images/docker-running-local.png "docker local instance prediction f1 score and accuracy")](https://github.com/dg499/spark-ml-wine-prediction-model/)


### Running with s3 dataset on docker container:

```
Open Command Prompt and Change directory (cd) to folder containing pom.xml

mvn package

sportify plugin in pom.xml automatically builds and creates the docker image with project-name:version 
under my user name dg499.

docker image ls

docker run --rm -m 4g -e BUCKET_NAME=s3a://wine-quality/ -e ACCESS_KEY_ID=AKI -e SECRET_KEY=kewj9g dg499/spark-prediction:1

docker login

docker push <image>

```

[![Image](https://github.com/dg499/spark-ml-wine-prediction-model/blob/master/images/docker-running-s3.png "docker local instance prediction f1 score and accuracy wihth S3 bucket data")](https://github.com/dg499/spark-ml-wine-prediction-model/)

#### useful docker commands
```
docker login
docker version
docker help
docker image ls
docker container ls -a
docker container logs c165f459e7d7
docker container rm c165f459e7d7
docker container prune
docker image remove 3094afcbdf12
docker inspect <image>
docker run -dit openjdk:8-jdk-alpine
```
## Parallel training implementation
This project is implemented with Spark data frames api and MLib libraries, with this Native spark implementation application is automatically parallelized and distributed natively.
<br />
Partitions and caching is implemented in code to speed up the process.

- Login into AWS Console.
- Create S3 Bucket
   - upload the TestDataset.csv, TrainingDataset.csv, and  ValidationDataset.csv
   
### EMR Cluster Setup
- Search for EMR Service & Create Cluster.
   - Provide the cluster name
   - Launch Mode cluster
   - Vendor Amazon
   - Release emr-5.3.1
   - Select spark application radio button
   - Hardware Configurations 
     - select the instance type
     - number of instances to train the model
   - select the ec2 key pair/ generate one to access the master node.
   - click on create cluster.
   
[![Image](https://github.com/dg499/spark-ml-wine-prediction-model/blob/master/images/emr-cluster.png "EMR Setup")](https://github.com/dg499/spark-ml-wine-prediction-model/)

### Submitting Spark Job For Parallel Training
- Verify Successful Cluster Setup process on Aws console.
   - copy the dns name fo the cluster.
   - navigate to security groups of the master node and add permissions for ssh on port 22 for your ip
   - open puttygen and create ppk file from ec2 key pair pem file created during cluster creation process.
   - connect to master node with putty session.
   - using winscp/filezilla copy the winepredictiontrainmodel.jar file to master node.
   - aws s3 cp s3://bucket-name/*.csv files to local directory on master node.
   - spark-submit winepredictiontrainmodel.jar
   - once the model training is successfully completed it prints the accuracy of model and f1 score on the console.
   - and uploads the trained model to s3 bucket.

[![Image](https://github.com/dg499/spark-ml-wine-prediction-model/blob/master/images/emr-submit-job.png "EMR Submit Job Step")](https://github.com/dg499/spark-ml-wine-prediction-model/)

### Verifying Model Output On S3 Bucket
- Verify LogicRegressionModel is created on S3 bucket on Aws console.

[![Image](https://github.com/dg499/spark-ml-wine-prediction-model/blob/master/images/emr-spark-job-history.png "EMR Job History ")](https://github.com/dg499/spark-ml-wine-prediction-model/)

[![Image](https://github.com/dg499/spark-ml-wine-prediction-model/blob/master/images/s3-bucket.png "EMR Job History ")](https://github.com/dg499/spark-ml-wine-prediction-model/)

## Single Machine Prediction Application
Open Command Prompt and Change directory (cd) to folder containing pom.xml <br />
mvn package<br />
wine prediction jar is built on with all the dependencies necessary for running the prediction application.<br />
Assumes datsets are stored on s3 bucket.

### EC2 Setup
  - Follow the [AWS EC2](https://docs.aws.amazon.com/efs/latest/ug/gs-step-one-create-ec2-resources.html) documentation, create and launch the instance.
   - copy the public ip of ec2 instance from previous step.
   - navigate to security groups of the node and add permissions for ssh on port 22 for your ip
   - open puttygen and create ppk file from ec2 key pair pem file created during cluster creation process.
   - connect to ec2 node with putty session.
   - using winscp/filezilla copy the winepredictionmodel.jar, dataset folder with trained model to ec2 instance.
   - Follow the instructions from the following blog post [Install Apache Spark on EC2 instances](https://maelfabien.github.io/bigdata/Spark/#2-configuration-of-your-master-nodes) and setup spark environment
  -
### Running Prediction Application
   - java -DBUCKET_NAME=dataset/ -jar wineprediction-1.0.jar for running the program with shipped dataset.
   - java -DBUCKET_NAME=  -DACCESS_KEY_ID= -DSECRET_KEY= -jar wineprediction-1.0.jar for running the program with s3 dataset.
   - once the program is successfully completed it prints the accuracy of model and f1 score on the console.

```
sudo yum list | grep openjdk
sudo yum install java-1.8.0-openjdk.x86_64 -y
sudo update-alternatives --config java
wget http://apache.mirrors.hoobly.com/spark/spark-2.4.5/spark-2.4.5-bin-hadoop2.7.tgz
tar -xf spark-2.4.5-bin-hadoop2.7.tgz 
rm spark-2.4.5-bin-hadoop2.7.tgz 
 sudo update-alternatives --config java
export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.242.b08-0.amzn2.0.1.x86_64/jre
export JRE_HOME=$JAVA_HOME/jre
export PATH=$PATH:$JAVA_HOME/bin:$JAVA_HOME/jre/bin
export SPARK_HOME=/home/ec2-user/
export SPARK_HOME=/home/ec2-user/spark-2.4.5-bin-hadoop2.7
vi ~/.bashrc
source ~/.bashrc
$SPARK_HOME/sbin/start-master.sh
$SPARK_HOME/sbin/start-slave.sh spark://ip-172-31-93-117.ec2.internal:7077
$SPARK_HOME/bin/spark-submit winequalityprediction-1.0.jar
$SPARK_HOME/sbin/stop-slave.sh spark://ip-172-31-93-117.ec2.internal:7077
$SPARK_HOME/sbin/stop-master.sh
```

[![Image](https://github.com/dg499/spark-ml-wine-prediction-model/blob/master/images/ec2.png "ec2 prediction f1 score and accuracy")](https://github.com/dg499/spark-ml-wine-prediction-model/)

[![Image](https://github.com/dg499/spark-ml-wine-prediction-model/blob/master/images/ec2-output.png "ec2 prediction f1 score and accuracy")](https://github.com/dg499/spark-ml-wine-prediction-model/)
  
[aws]: http://aws.amazon.com/
[awsconsole]: https://console.aws.amazon.com
[hadoop]: https://hadoop.apache.org/docs/r2.7.3/hadoop-aws/dependency-analysis.html
[hadoop-aws]: https://hadoop.apache.org/docs/stable/hadoop-aws/tools/hadoop-aws/index.html
[spark]: https://spark.apache.org/docs/latest/
[jdk]: https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html#license-lightbox
[scala]: https://downloads.lightbend.com/scala/2.13.2/scala-2.13.2.msi
[maven]: https://maven.apache.org/download.cgi
[docker]: https://hub.docker.com/editions/community/docker-ce-desktop-windows
[intellij]: https://www.jetbrains.com/idea/download/download-thanks.html?platform=windows&code=IIC
[eclipse]: https://www.eclipse.org/downloads/
[vscode]: https://code.visualstudio.com/download
[sts]: https://spring.io/tools
[docs-signup]: http://docs.aws.amazon.com/java-sdk/v1/developer-guide/signup-create-iam-user.html


