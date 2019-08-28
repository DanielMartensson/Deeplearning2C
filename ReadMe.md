## What is this project?
This is Deeplearning2C. It's a project for generate an application for Android, Iphone, Linux, Windows and Mac OS X, that can generate a deep neural network in a .c file after being trained with Deeplearning4J.

I have been using the following dependencies

* Deeplearning4J
* GluonHQ JavaFX for Android & Iphone development
* Lombok
* Logback-classic

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


Applied onto a Samsung Galaxy S3 from 2012

![a](https://raw.githubusercontent.com/DanielMartensson/Deeplearning2C/master/pictures/Samsung%20S3.jpeg)

## I like this project! How can I get the installation file?

First of all. The installation file is over 600 megabytes. That's huge, but anyway it's still possible to install. You need to have OpenJDK 8 and OpenJFX 8 installed. If you are an Ubuntu user, then follow these steps:

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

* Make so C-code generation works. I have just getting header files to work.  Have a look at TrainEvalGeneratePresenter.java file 
* Generate an application for Iphone. Iphone app generation works for this project, but I haven't tested it yet because I focusing on Android at the moment. 
* Search for bugs. If you find any...please open an issue or a pull request. 
* Scale down dependencies inside the build.gradle file, and only use the most necessary for training the deep neural network
* The view need to be better done. Sometimes, I can't even see the progress bar. Please use SceneBulilder 8 for that. 

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
