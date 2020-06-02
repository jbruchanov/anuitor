#~/bin/sh
#get output of publishToMavenLocal

if [ -z $1 ]; then 
	echo "Missing version argument"
	exit
fi

VERSION=$1
echo Version:$VERSION

set -e

#generate final bundles

#anuitor
gpg -ab anuitor-$VERSION.pom
gpg -ab anuitor-$VERSION.aar
gpg -ab anuitor-$VERSION-sources.jar
#gpg -ab anuitor-$VERSION-javadoc.jar
jar -cvf anuitor-$VERSION-bundle.jar anuitor-$VERSION.pom anuitor-$VERSION.pom.asc anuitor-$VERSION.aar anuitor-$VERSION.aar.asc anuitor-$VERSION-sources.jar anuitor-$VERSION-sources.jar.asc

#anuitor-groovy
gpg -ab anuitor-groovy-$VERSION.pom
gpg -ab anuitor-groovy-$VERSION.aar
gpg -ab anuitor-groovy-$VERSION-sources.jar
#gpg -ab anuitor-groovy-$VERSION-javadoc.jar
jar -cvf anuitor-groovy-$VERSION-bundle.jar anuitor-groovy-$VERSION.pom anuitor-groovy-$VERSION.pom.asc anuitor-groovy-$VERSION.aar anuitor-groovy-$VERSION.aar.asc anuitor-groovy-$VERSION-sources.jar anuitor-groovy-$VERSION-sources.jar.asc

#anuitor-client
gpg -ab anuitor-client-$VERSION.pom
gpg -ab anuitor-client-$VERSION.aar
jar -cvf anuitor-client-$VERSION-bundle.jar anuitor-client-$VERSION.pom anuitor-client-$VERSION.pom.asc anuitor-client-$VERSION.aar anuitor-client-$VERSION.aar.asc
