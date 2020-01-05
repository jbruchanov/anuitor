#~/bin/sh
#get output of publishToMavenLocal

if [ -z $ANDROID_HOME ]; then 
	echo "ANDROID_HOME env variable not defined!"
	exit
fi

if [ -z $1 ]; then 
	echo "Missing version argument"
	exit
fi

VERSION=$1
LIMIT=60
FILE=`date -r ../service/src/main/res/raw/uitor_webapp.zip +%Y%m%d%H%M`
NOW=`date +%Y%m%d%H%M`
declare -i DIFF_MINS
DIFF_MINS=$((NOW-FILE))

if [ $DIFF_MINS -gt $LIMIT ]; 
then
	echo "Webfile ../service/src/main/res/raw/uitor_webapp.zip is OLDER than 60mins"
	read -p "Are you sure? [Y/N]" -n 1 -r
	echo
	if [[ ! $REPLY =~ ^[Yy]$ ]]
	then
	    echo "Quit..."
	    exit
	fi	
fi

echo Version:$VERSION

set -e
#generate final bundle
gpg -ab anuitor-$VERSION.pom
gpg -ab anuitor-$VERSION.aar
gpg -ab anuitor-$VERSION-sources.jar
#gpg -ab anuitor-$VERSION-javadoc.jar
jar -cvf anuitor-$VERSION-bundle.jar anuitor-$VERSION.pom anuitor-$VERSION.pom.asc anuitor-$VERSION.aar anuitor-$VERSION.aar.asc anuitor-$VERSION-sources.jar anuitor-$VERSION-sources.jar.asc

gpg -ab anuitor-groovy-$VERSION.pom
gpg -ab anuitor-groovy-$VERSION.aar
gpg -ab anuitor-groovy-$VERSION-sources.jar
#gpg -ab anuitor-groovy-$VERSION-javadoc.jar
jar -cvf anuitor-groovy-$VERSION-bundle.jar anuitor-groovy-$VERSION.pom anuitor-groovy-$VERSION.pom.asc anuitor-groovy-$VERSION.aar anuitor-groovy-$VERSION.aar.asc anuitor-groovy-$VERSION-sources.jar anuitor-groovy-$VERSION-sources.jar.asc