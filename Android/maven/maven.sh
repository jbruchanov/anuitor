#!/bin/bash

gpg -ab anuitor-0.9.3.pom
gpg -ab anuitor-0.9.3.jar
gpg -ab anuitor-0.9.3-sources.jar   
gpg -ab anuitor-0.9.3-javadoc.jar 
jar -cvf anuitor-0.9.3-bundle.jar anuitor-0.9.3.pom anuitor-0.9.3.pom.asc anuitor-0.9.3.jar anuitor-0.9.3.jar.asc anuitor-0.9.3-javadoc.jar anuitor-0.9.3-javadoc.jar.asc anuitor-0.9.3-sources.jar anuitor-0.9.3-sources.jar.asc