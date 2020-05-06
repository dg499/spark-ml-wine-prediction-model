# CS 643 102, Cloud Computing - Spring ‘20

## Programming Assignment 2
This is the code repository for developing machine learning application to predict the quality of wine using Spark MLib on AWS Cloud Platform. It contains all the supporting project files necessary to build, train and predict the wine quality from start to finish.

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
