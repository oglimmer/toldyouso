#!/bin/sh

trap cleanup 2
set -e

# returns the JDK version.
# 8 for 1.8.0_nn, 9 for 9-ea etc, and "no_java" for undetected
# from https://stackoverflow.com/questions/7334754/correct-way-to-check-java-version-from-bash-script
jdk_version() {
  local result
  local java_cmd
  if [[ -n $(type -p java) ]]
  then
    java_cmd=java
  elif [[ (-n "$JAVA_HOME") && (-x "$JAVA_HOME/bin/java") ]]
  then
    java_cmd="$JAVA_HOME/bin/java"
  fi
  local IFS=$'\n'
  # remove \r for Cygwin
  local lines=$("$java_cmd" -Xms32M -Xmx32M -version 2>&1 | tr '\r' '\n')
  if [[ -z $java_cmd ]]
  then
    result=no_java
  else
    for line in $lines; do
      if [[ (-z $result) && ($line = *"version \""*) ]]
      then
        local ver=$(echo $line | sed -e 's/.*version "\(.*\)"\(.*\)/\1/; 1q')
        # on macOS, sed doesn't support '?'
        if [[ $ver = "1."* ]]
        then
          result=$(echo $ver | sed -e 's/1\.\([0-9]*\)\(.*\)/\1/; 1q')
        else
          result=$(echo $ver | sed -e 's/\([0-9]*\)\(.*\)/\1/; 1q')
        fi
      fi
    done
  fi
  echo "$result"
}

cleanup()
{
	echo "****************************************************************"
	echo "Stopping CouchDB and Tomcat.....please wait...."
	echo "****************************************************************"
	docker rm -f $containerID
	./apache-tomcat-$TOMCAT_VERSION/bin/shutdown.sh
	exit 0
}

#
# SECTION: HELP / USAGE
#

usage="$(basename "$0") [-c] [-s] [-f] [-j java_version] [-t tomcat_version] - builds, deploys and runs toldyouso
where:
    -h  shows this help text
    -s  skip build
    -c  clean: cleans local run directory, when a build is scheduled for execution it also does a full build
    -f  tails the apache catalina log at the end
    -j  sets/overwrites JAVA_HOME to version X, X needs to be compatible with java_home (e.g. 1.8, 9, 10)
    -t  defines the major Tomcat version, 7, 8 or 9, default is 9
"

#
# SECTION: RESOLVE PARAMETER
#

cd ${0%/*}

while getopts ':hscfj:t:' option; do
  case "$option" in
    h) echo "$usage"
       exit
       ;;
	s) SKIP_BUILD=YES
	   ;;
	c) CLEAN=YES
	   ;;
	f) TAIL=YES
	   ;;
	j) JAVA_VERSION=$OPTARG
	   ;;
	t) TOMCAT_MAJ_VERSION=$OPTARG
	   ;;
    :) printf "missing argument for -%s\n" "$OPTARG" >&2
       echo "$usage" >&2
       exit 1
       ;;
   \?) printf "illegal option: -%s\n" "$OPTARG" >&2
       echo "$usage" >&2
       exit 1
       ;;
  esac
done
shift $((OPTIND - 1))
TYPE_PARAM="$1"


# vars
if [ -n "$JAVA_VERSION" ]; then
	export JAVA_HOME=$(/usr/libexec/java_home -v $JAVA_VERSION)
fi
if [ -z "$TOMCAT_MAJ_VERSION" ]; then
	TOMCAT_MAJ_VERSION="9"
fi
# find latest tomcat version for $TOMCAT_MAJ_VERSION
TOMCAT_BASE_URL="http://mirror.vorboss.net/apache/tomcat"
TOMCAT_VERSION_PRE=$(curl -s "$TOMCAT_BASE_URL/tomcat-$TOMCAT_MAJ_VERSION/"|grep -m1 -o "<a href=\"v\d*.\d*.\d*")
TOMCAT_VERSION=${TOMCAT_VERSION_PRE:10}
TOMCAT_URL=$TOMCAT_BASE_URL/tomcat-$TOMCAT_MAJ_VERSION/v$TOMCAT_VERSION/bin/apache-tomcat-$TOMCAT_VERSION.tar.gz

# check for dependencies
mvn --version 1>/dev/null || exit 1
java -version 2>/dev/null || exit 1
curl --version 1>/dev/null || exit 1
docker --version 1>/dev/null || exit 1

# clean if requested
if [ "$CLEAN" == "YES" ]; then
	rm -rf localrun
	MVN_CLEAN=clean
fi

#build
if [ "$SKIP_BUILD" != "YES" ]; then

	if [ "$(jdk_version)" -lt 9 ]; then
		cp pom.xml pom.xml.bak
		sed '/<!-- needed since Java 9 - START -->/,/<!-- needed since Java 9 - END -->/d' pom.xml.bak > pom.xml
	fi

	mvn $MVN_CLEAN package


	if [ "$(jdk_version)" -lt 9 ]; then
		mv -f pom.xml.bak pom.xml
	fi

fi

# prepare env
mkdir -p localrun
cd localrun

# set up the couchdb views
containerID=$(docker run -d -p 5984:5984 couchdb:1.7)
while [ "$(curl --write-out %{http_code} --silent --output /dev/null http://localhost:5984)" != "200" ]; do
	echo "waiting for couchdb..."
	sleep 1
done
curl -X PUT http://localhost:5984/toldyouso
curl -X POST -H "Content-Type: application/json" -d @../src/couchdb/_design-User-view.json http://localhost:5984/toldyouso

# download tomcat
if [ ! -f "/$TMPDIR/apache-tomcat-$TOMCAT_VERSION.tar" ]; then
	curl -s $TOMCAT_URL | gzip -d >/$TMPDIR/apache-tomcat-$TOMCAT_VERSION.tar
fi
# extract tomcat
if [ ! -d "./apache-tomcat-$TOMCAT_VERSION" ]; then
	tar -xf /$TMPDIR/apache-tomcat-$TOMCAT_VERSION.tar -C ./
fi
cp ../target/toldyouso.war apache-tomcat-$TOMCAT_VERSION/webapps/

# properties
echo "toldyouso.domain = http://localhost:8080/toldyouso" > java.properties
export JAVA_OPTS=-Dtoldyouso.properties=./java.properties

# start tomcat
./apache-tomcat-$TOMCAT_VERSION/bin/startup.sh

# waiting for ctrl-c
if [ "$TAIL" == "YES" ]; then
	tail -f ./apache-tomcat-$TOMCAT_VERSION/logs/catalina.out
else
	echo "Press ctrl-c to stop CouchDB and Tomcat"
	read -r -d '' _ </dev/tty
fi
