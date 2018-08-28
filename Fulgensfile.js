module.exports = {

  config: {
    Name: "toldyouso",
    Vagrant: {
      Box: 'ubuntu/xenial64',
      Install: 'maven openjdk-8-jdk-headless docker.io'
    }
  },

  software: {
    "toldyouso": {
      Source: "mvn",
      Artifact: "target/toldyouso.war",
      configFile: {
      	Name: "java.properties",
        Connections: [ { Source:"couchdb", Var: "couchdb.host" } ],
      	Content: [ "toldyouso.domain=http://localhost:8080/toldyouso" ],
      	AttachAsEnvVar: ["JAVA_OPTS", "-Dtoldyouso.properties=$$SELF_NAME$$"]
      },
      BeforeBuild: [ "if [ \"$(jdk_version)\" -lt 9 ]; then cp pom.xml pom.xml.bak; sed '/<!-- needed since Java 9 - START -->/,/<!-- needed since Java 9 - END -->/d' pom.xml.bak > pom.xml; fi" ],
      AfterBuild: [ "if [ \"$(jdk_version)\" -lt 9 ]; then mv -f pom.xml.bak pom.xml; fi" ]
    },

    "couchdb": {
    	Source: "couchdb",
    	CouchDB: {
    		Schema: "toldyouso",
    		Create: [ "src/couchdb/_design-User-view.json" ]
    	}
    },

    "tomcat": {
      Source: "tomcat",
      Deploy: "toldyouso"
    }
  }
}
