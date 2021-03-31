module.exports = {

  config: {
    SchemaVersion: "1.0.0",
    Name: "toldyouso",
    Vagrant: {
      Box: 'ubuntu/xenial64',
      Install: 'maven openjdk-8-jdk-headless docker.io'
    }
  },

  versions: {
    toldyouso: {
      TestedWith: "3-jdk-11"
    },
    couchdb: {
      TestedWith: "1.7 & 2 & 3"
    },
    tomcat: {
      Docker: "tomcat9-openjdk11-openj9",
      TestedWith: "9 & 10"
    }
  },

  software: {
    toldyouso: {
      Source: "mvn",
      Artifact: "target/toldyouso.war",
      configFile: {
      	Name: "java.properties",
      	Content: [
          { Line: "toldyouso.domain=http://localhost:8080/toldyouso" },
          { Source:"couchdb", Line: "couchdb.host=$$VALUE$$" }
        ],
      	AttachAsEnvVar: ["JAVA_OPTS", "-Dtoldyouso.properties=$$SELF_NAME$$"]
      }
    },

    couchdb: {
      Source: "couchdb",
      //DockerImage: "oglimmer/pouchdb",
      DockerMemory: "200M",
    	CouchDB: {
    		Schema: "toldyouso",
    		Create: [ "src/couchdb/_design-User-view.json" ]
    	}
    },

    tomcat: {
      Source: "tomcat",
      DockerImage: "oglimmer/adoptopenjdk-tomcat",
      DockerMemory: "70M",
      Deploy: "toldyouso"
    }
  }
}
