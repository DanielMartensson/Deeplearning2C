## What is this project?
This is Deeplearning2C. It's a project for generate an application for Android, Iphone, Linux, Windows and Mac OS X, that can generate a deep neural network in a .c file after being trained with Deeplearning4J.

I have been using the following dependencies

* Deeplearning4J
* GluonHQ JavaFX for Android & Iphone development
* Lombok

## Why should I use this application?
Let's say that you want to implement an deep neural network that are trained for classification for animals or other visible things. You want to implement it into a microcontroller such as STM32, PIC, AVR. Then this application can be used to generate a deep neural network in C code.

## How does it looks like?
Here are some images when I run Deeplearning2C on Linux.

Model selection

![a](https://raw.githubusercontent.com/DanielMartensson/Deeplearning2C/master/pictures/Models.png)

Menu

![a](https://raw.githubusercontent.com/DanielMartensson/Deeplearning2C/master/pictures/Menu.png)

Global configuration

![a](https://raw.githubusercontent.com/DanielMartensson/Deeplearning2C/master/pictures/Global.png)

Layer configuration

![a](https://raw.githubusercontent.com/DanielMartensson/Deeplearning2C/master/pictures/Layer.png)

Training data configuration

![a](https://raw.githubusercontent.com/DanielMartensson/Deeplearning2C/master/pictures/Data.png)

Training the model

![a](https://raw.githubusercontent.com/DanielMartensson/Deeplearning2C/master/pictures/Training.png)

## I like this project, but I missing some layers and updaters. How can I implement these?

First of all. To start with this project, you need to have OpenJDK 8 and OpenJFX 8 installed.
If you are an Ubuntu user, then follow these steps:

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

Now you can download my Deeplearning2C project and import that project into your Eclipse IDE. 
After you have done that, you need to focus on three java classes.

```
ConfigurationsPresenter.java <-- For configuration GUI
DL4JSerializableConfiguration.java <-- For creating the deep neural network
SimpleDependencyInjection.java <-- Class that make DL4JModel static and accessible for all classes
```

Those classes handle the configurations for both the GUI and file handling. Just read them and try to understand how I have made them. I allways use the same coding style for all classes and I try to make coding easy as possible so other users can understand my code. 

The idea behind Deeplearning2C is that I using Deeplearning4J and creates an interface for it. The configuration you making inside Deeplearning2C is more like a instructions how to create the deep neural network for Deeplearning4J. 


5. See the getting started guide for using GluonHQ JavaFX for mobile development. It's a very easy and excellent done graphical manual.

https://docs.gluonhq.com/getting-started/#introduction
