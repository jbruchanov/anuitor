#~/bin/sh

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
FILE=`date -r ../service/src/main/res/raw/anuitor.zip +%Y%m%d%H%M`
NOW=`date +%Y%m%d%H%M`
declare -i DIFF_MINS
DIFF_MINS=$((NOW-FILE))

if [ $DIFF_MINS -gt $LIMIT ]; 
then
	echo "Webfile ../service/src/main/res/raw/anuitor.zip is OLDER than 60mins"
	read -p "Are you sure? [Y/N]" -n 1 -r
	echo
	if [[ ! $REPLY =~ ^[Yy]$ ]]
	then
	    echo "Quit..."
	    exit
	fi	
fi

echo Version:$VERSION

sed -e 's/{VERSION}/'$VERSION'/g' template.pom > anuitor-$VERSION.pom
sed -e 's/{VERSION}/'$VERSION'/g' template-groovy.pom > anuitor-groovy-$VERSION.pom

#generate anuitor.aar
pushd ..
chmod +x gradlew
gradlew clean assembleRelease service-groovy:assembleRelease
popd
cp ../service/build/outputs/aar/service-release.aar anuitor-$VERSION.aar

#generate anuitor-groovy.aar
cp ../service-groovy/build/outputs/aar/service-groovy-release.aar anuitor-groovy-$VERSION.aar

#generate anuitor-sources.jar
pushd ../service/src/main/java
jar cf anuitor-$VERSION-sources.jar .
popd
mv ../service/src/main/java/anuitor-$VERSION-sources.jar .

#generate anuitor-groovy-sources.jar
pushd ../service-groovy/src/main/java
jar cf anuitor-groovy-$VERSION-sources.jar .
popd
mv ../service-groovy/src/main/java/anuitor-groovy-$VERSION-sources.jar .

#generate anuitor-javadoc.jar
pushd ../service/src/main/java/com/scurab/android/anuitor
find . -type f -name "*.java" | xargs javadoc -d jdoc 
pushd jdoc
jar cvf ../anuitor-$VERSION-javadoc.jar *
popd
rm -R jdoc
popd
mv ../service/src/main/java/com/scurab/android/anuitor/anuitor-$VERSION-javadoc.jar .

#generate anuitor-grovy-javadoc.jar
pushd ../service-groovy/src/main/java/com/scurab/android/anuitor
find . -type f -name "*.java" | xargs javadoc -d jdoc 
pushd jdoc
jar cvf ../anuitor-groovy-$VERSION-javadoc.jar *
popd
rm -R jdoc
popd
mv ../service-groovy/src/main/java/com/scurab/android/anuitor/anuitor-groovy-$VERSION-javadoc.jar .


FILES=(anuitor-$VERSION.pom anuitor-$VERSION.aar anuitor-$VERSION-sources.jar anuitor-$VERSION-javadoc.jar anuitor-groovy-$VERSION.pom anuitor-groovy-$VERSION.aar anuitor-groovy-$VERSION-sources.jar anuitor-groovy-$VERSION-javadoc.jar)
for file in "${FILES[@]}"
do
	if [ ! -f $file ]; then
		echo "$file not generated"
		exit 1
	fi
done

set -e
#generate final bundle
gpg -ab anuitor-$VERSION.pom
gpg -ab anuitor-$VERSION.aar
gpg -ab anuitor-$VERSION-sources.jar
gpg -ab anuitor-$VERSION-javadoc.jar
jar -cvf anuitor-$VERSION-bundle.jar anuitor-$VERSION.pom anuitor-$VERSION.pom.asc anuitor-$VERSION.aar anuitor-$VERSION.aar.asc anuitor-$VERSION-javadoc.jar anuitor-$VERSION-javadoc.jar.asc anuitor-$VERSION-sources.jar anuitor-$VERSION-sources.jar.asc

gpg -ab anuitor-groovy-$VERSION.pom
gpg -ab anuitor-groovy-$VERSION.aar
gpg -ab anuitor-groovy-$VERSION-sources.jar
gpg -ab anuitor-groovy-$VERSION-javadoc.jar
jar -cvf anuitor-groovy-$VERSION-bundle.jar anuitor-groovy-$VERSION.pom anuitor-groovy-$VERSION.pom.asc anuitor-groovy-$VERSION.aar anuitor-groovy-$VERSION.aar.asc anuitor-groovy-$VERSION-javadoc.jar anuitor-groovy-$VERSION-javadoc.jar.asc anuitor-groovy-$VERSION-sources.jar anuitor-groovy-$VERSION-sources.jar.asc