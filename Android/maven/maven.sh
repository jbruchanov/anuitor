#!/bin/bash

gpg -ab anuitor-0.9.1.pom
gpg -ab anuitor-0.9.1.jar
gpg -ab anuitor-0.9.1-sources.jar   
gpg -ab anuitor-0.9.1-javadoc.jar 
jar -cvf anuitor-0.9.1-bundle.jar anuitor-0.9.1.pom anuitor-0.9.1.pom.asc anuitor-0.9.1.jar anuitor-0.9.1.jar.asc anuitor-0.9.1-javadoc.jar anuitor-0.9.1-javadoc.jar.asc anuitor-0.9.1-sources.jar anuitor-0.9.1-sources.jar.asc