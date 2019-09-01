# Deeplearning2C Version 1.2

## What is this project?
This is Deeplearning2C. It's a project for generate an application for Android, Iphone, Linux, Windows and Mac OS X, that can generate a deep neural network in a .c file after being trained with Deeplearning4J.

I have been using the following dependencies

* Deeplearning4J
* GluonHQ JavaFX for Android & Iphone development
* Lombok
* Logback-classic

## Why should I use this application?
Let's say that you want to implement an deep neural network that are trained for classification for animals or other visible things. You want to implement it into a microcontroller such as STM32, PIC, AVR. Then this application can be used to generate a deep neural network in C code.

## What kind of neural network can this application generate in C?
This application generate DenseLayer and OutputLayer from DL4J into C-code. 
I will focusing on LSTM layers too, but the problem is that I have no idea how to get all the weight matrices from a LSTM layer.

From this code in TrainEvalGeneratePresenter.java in method generateCCode()
```
Layer layer = dL4JModel.getMultiLayerNetwork().getLayer(i);
Map<String, INDArray> weights = layer.paramTable();
System.out.println(weights.keySet());
```
I get the output [W, b] for a DenseLayer and OutputLayer.
I get the output [W, WR, b] for an LSTM layer. 

I was given this Java class from Skymind engineers. So I assuming that the matrices [W, WR, b] contains several matrices. I just need to find its dimensions and what order they are placed to do the matrix math in C-code. 
https://github.com/eclipse/deeplearning4j/blob/master/deeplearning4j/deeplearning4j-nn/src/main/java/org/deeplearning4j/nn/layers/recurrent/LSTMHelpers.java

If you can send me a code(open an issue) how to get all weight matrices for a LSTM layer from [W, WR, b]. I will implement C-code generation for LSTM in this application.

## How does it looks like?
Here is an example when I run the IRIS example with iris.csv data file. You can download it from here:

https://github.com/deeplearning4j/oreilly-book-dl4j-examples/blob/master/datavec-examples/src/main/resources/IrisData/iris.txt

First I create my model.

![a](https://raw.githubusercontent.com/DanielMartensson/Deeplearning2C/master/pictures/Models.png)

Then I go to menu.

![a](https://raw.githubusercontent.com/DanielMartensson/Deeplearning2C/master/pictures/Menu.png)

I change my global configuration.

![a](https://raw.githubusercontent.com/DanielMartensson/Deeplearning2C/master/pictures/Global.png)

I change my layer configuration.

![a](https://raw.githubusercontent.com/DanielMartensson/Deeplearning2C/master/pictures/Layer.png)

Then I insert the training data. Notice that 50% of that data is evaluation data.

![a](https://raw.githubusercontent.com/DanielMartensson/Deeplearning2C/master/pictures/Data.png)

Now I train my model. Notice that I have a progress bar too.

![a](https://raw.githubusercontent.com/DanielMartensson/Deeplearning2C/master/pictures/Training.png)

Now I evaluate my model.

![a](https://raw.githubusercontent.com/DanielMartensson/Deeplearning2C/master/pictures/Eval.png)

Applied onto a Samsung Galaxy S3 from 2012 with Android 4.4.4. I have also tested it onto a Samsung Galaxy S5 Neo from 2015 with Android 6.0.1. Both working to start. But the S3 phone have issues for pop-up dialogs. The application shut down when a pop-up appear, but the Samsung S5 Neo works to train a neural network with and generate C-code. 

![a](https://raw.githubusercontent.com/DanielMartensson/Deeplearning2C/master/pictures/Samsung%20S3.jpeg)

This is how a C-code generation example looks like for this model.

```
/*
 * Model: ModelA
 *
 *
 *  Created on: 2019-09-01 8:48:54 PM
 *  	Generated by: Deeplearning2C
 *     		Author: Daniel Mårtensson
 */

#include "ModelA.h"
#include "BLAS/f2c.h"
#include "BLAS/functions.h"

void ModelA(float* input, float* output){

	integer m = 0; // Real row dimension of non-transpose A
	integer n = 0; // Read column dimension of non-transpose A
	real alpha = 1; // Always 1
	real beta = 1; // Always 1
	integer incx = 1; // Always 1
	integer incy = 1; // Always 1
	char trans = 'N'; // We have transpose matrix A'

	/*
	 * We are using BLAS subroutine sgemv for solving y = alpha*A*x + beta*y
	 * The BLAS subroutine is the same routine that is used in EmbeddedLapack
	 * Solve the equations like:
	 * b0 = act(W0*input + b0)
	 * b1 = act(W1*b0 + b1)
	 * b2 = act(W2*b1 + b2)
	 * b3 = act(W3*b2 + b3)
	 * b4 = act(W4*b3 + b4)
	 * ....
	 * ....
	 * output = act(Wi*b(i-1) + bi)
	 */

	real b0[1*3]={   -0.7468,   -1.3742,    0.5414};
	real W0[4*3]={   -0.0612,   -0.1257,    0.2310, 
			 -0.5216,   -0.2982,   -0.0370, 
			  1.0486,    0.4481,    0.1480, 
			  0.4767,    0.7818,   -0.1641};
	m = 3;
	n = 4;
	sgemv_(&trans, &m, &n, &alpha, W0, &m, input, &incx, &beta, b0, &incy); // Layer - first - index 0
	activation(b0, m, "TANH");

	real b1[1*3]={   -0.8399,   -0.3556,   -0.6684};
	real W1[3*3]={   -0.8822,   -0.3406,   -0.7666, 
			 -2.9184,    2.3169,    3.2221, 
			 -0.7768,   -0.3266,   -0.0527};
	m = 3;
	n = 3;
	sgemv_(&trans, &m, &n, &alpha, W1, &m, b0, &incx, &beta, b1, &incy); // Layer - middle - index 1
	activation(b1, m, "TANH");

	real b2[1*3]={    0.6751,   -0.0974,   -0.2969};
	real W2[3*3]={    3.7913,   -3.4345,   -3.1726, 
		         -1.5479,   -2.3227,    3.5600, 
			 -2.4640,   -2.6131,    2.1754};
	m = 3;
	n = 3;
	sgemv_(&trans, &m, &n, &alpha, W2, &m, b1, &incx, &beta, output, &incy); // Layer - last - index 2
	activation(output, m, "SOFTMAX");

}
```

## I like this project! How can I get the installation file?

First of all. The installation file is over 372 megabytes for Android. That's huge, but anyway it's still possible to install. Right now, I have excluded the most large dependencies which I don't use. Look in the build.gradle file. You need to have OpenJDK 8 and OpenJFX 8 installed. If you are an Ubuntu user, then follow these steps:

1. Install OpenJDK 8

```
sudo apt-get install openjdk-8-jdk
```

2. Install OpenJFX 8
```
Open sources.list file 

cd /etc/apt
sudo nano sources.list

Paste this into the file and save and close

deb http://de.archive.ubuntu.com/ubuntu/ bionic universe

Run these code inside the terminal

sudo apt-get update
sudo apt install openjfx=8u161-b12-1ubuntu2 libopenjfx-java=8u161-b12-1ubuntu2 libopenjfx-jni=8u161-b12-1ubuntu2 openjfx-source=8u161-b12-1ubuntu2
sudo apt-mark hold libopenjfx-java libopenjfx-jni openjfx openjfx-source
```

3. Install Eclipse 2018-09 (4.9.0) R (Because Eclipse 2018-09 will only work with Gluon Plugin 2.6.0)
```
  https://www.eclipse.org/downloads/packages/release/2018-09/r
```

4. Install Gluon Plugin 2.6.0 inside Eclipse
```
  Help -> Eclipse Marketplace -> Gluon 2.6.0
```

Now you can download my Deeplearning2C project and import that project into your Eclipse IDE.  Have fun and generate the .jar file for Win,Lin,Mac. 

5. (Optional) See the getting started guide for using GluonHQ JavaFX for mobile development. It's a very easy and excellent done graphical manual. It describes how to set up the Android SDK etc. https://docs.gluonhq.com/getting-started/#introduction

6. (Optional) Troubleshooting for Android can be done this way, if you got some issues with the application on the phone, but not on desktop. See selected answer https://stackoverflow.com/questions/42253794/androidinstall-task-causes-a-no-connected-devices-error

## What need to be working on?

* Generate an application for Iphone. Iphone app generation works for this project, but I haven't tested it yet because I focusing on Android at the moment. 

* Upgrade the C-code generator for LSTM networks.

## How is the project organized?
I have always like clean god written code and pedagogy explanations. So I'm going to give you an introduction what every file do. I like to keep files as short as possible. Around 250-300 lines per each java file is a suitable java file. 

* DrawerManager.java -> Handles the menu slide to the left
* Main.java -> Handles the import of new JavaFX pages
* DL4JData.java -> Handles everything that has to do with CSV handling for DL4J
* DL4JModel.java -> Hnaldes everything that has to do with the model (save, load, generate etc.)
* DL4JSerializableConfiguration.java -> Handles saving and loading layers and global config to .ser files
* DL4JThread.java -> Handles training and using the text area and progress bar inside the TrainEvalGeneratePresenter.java file
* Dialogs.java -> Handles everything that has to do with pop-up dialogs
* FileHandler.java -> Handles file system, write files, read files and create files
* SimpleDependencyInjection.java -> Create a static object of DL4JModel.java so we can have access to DL4JModel everywhere
* ConfigurationsPresenter.java -> Handles GUI view for configuration
* DataPresenter.java -> Handles the GUI view for CSV import
* ModelsPresenter.java -> Handles the GUI view for model creation and delete/save
* TrainEvalGeneratePresenter.java -> Handles the GUI view for train, eval and generate C-code

Every file inside se.danielmartensson.views package that contains the word "View" inside its name is only for importing the GUI inside Main.java file. These files never changes.


## Tutorials - If you want to change inside the code
* Tutorials -> Add seed, regularization coefficient, learning rate, momentum
* Tutorials -> Add new updater
* Tutorials -> Add new layer
* Tutorials -> Add inputs and outputs
* Tutorials -> Add new functionality to layers
