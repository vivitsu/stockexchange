Project Description
===================

Stock Exchange Server
---------------------

This is a program I developed as part of an assignment in the course of Distributed Computing, taken in Spring 2013 at the
University of Florida. The goals of this project were:

1. Becoming familiar with group communication
2. Dealing with multiple clients and multiple shared distributed objects
3. Designing and implementing a distributed application using existing middleware
4. Handling process failures by means of using virtual synchrony

This program uses the [JGroups](http://www.jgroups.org/) toolkit to keep multiple replicas of a Stock Exchange Server in synchrony, using
a technique called [Virtual Synchrony](http://en.wikipedia.org/wiki/Virtual_synchrony).

Building the program
====================

This project includes a makefile to build and clean the project. The makefile assumes you have the JGroups 3.2.7 jar in your present directory.
This program has been tested to run under JGroups 3.2.7 only. To build the project, copy all the .java files in one directory which has the JGroups
jar archive and run `make` in that directory.
This project also includes the protocol.xml file which defines the protocol configuration for JGroups.

Running the program
===================

To run the project, navigate to the directory where you have the compiled class files and run the following at the terminal:

> java -cp .:jgroups-3.2.7.Final.jar VSynchrony [process id] [number of clients] [server port number]

Again, the assumption is that you have the JGroups jar archive in the same directory from where you are trying to run the program.

Caveats
=======

This program requires an 'index.properties' file which the stock exchange server will read upon starting, which is not included here. You will have
to include the file for the program to run correctly. The format of the 'index.properties' file is as follows:

> [symbol] [name]
> GOOG Google
> AMZN Amazon
> AAPL Apple

Wiki
====

Additional details about the project, including implementation details, class descriptions, etc. are included in the project wiki.