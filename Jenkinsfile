#!groovy

// https://github.com/feedhenry/fh-pipeline-library
@Library('fh-pipeline-library') _

final String COMPONENT = 'unifiedpush'
final String DOCKER_HUB_ORG = 'rhmap'
final String DOCKER_HUB_REPO = 'unifiedpush-eap'
String version = null
String build = null

node('java8-centos7') {
    cleanWs()

    stage ('Checkout') {
        checkout scm
    }

    env.BUILD_NUMBER = sh(returnStdout: true, script: "git rev-parse --short HEAD").trim()
    build = env.BUILD_NUMBER
    final String CHANGE_URL = env.CHANGE_URL

    ansiColor('xterm') {
        stage('Setup') {
            if (env.NEXUS_SERVER_ENABLED) {
                String m2Config =
                    """<?xml version="1.0" encoding="UTF-8"?>
                        <settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation= "http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
                        <mirrors>
                            <mirror>
                                <id>nexus</id>
                                <mirrorOf>central</mirrorOf>
                                <name>Nexus Mirror</name>
                                <url>${env.NEXUS_SERVER_URL}/repository/maven-all-public/</url>
                            </mirror>
                        </mirrors>
               </settings>"""
                writeFile file: "m2settings.xml", text: m2Config
                env.M2_SETTINGS = 'm2settings.xml'
                print "NEXUS SERVER ENABLED: ${env.NEXUS_SERVER_URL}"
            } else {
                env.M2_SETTINGS = '/usr/local/apache-maven-3.1.1/conf/settings.xml'
                print "NEXUS SERVER NOT ENABLED"
            }
        }

        stage('Build') {
            sh "mvn -s ${env.M2_SETTINGS} clean verify -Ptest,dist -Dups.ddl_value=update"
            version = sh(returnStdout: true, script: "mvn help:evaluate -Dexpression=project.version | grep -v \"^\\[\" | tail -1 | cut -f1 -d\"-\"").trim()

            sh "cp servers/auth-server/target/auth-server.war ./dist/unifiedpush-auth-server-${version}-${build}.war"
            sh "cp servers/ups-as7/target/ag-push.war ./dist/unifiedpush-server-as7-${version}-${build}.war"

            dir('dist') {
                archiveArtifacts writeBuildInfo('unifiedpush', version)
            }

            sh "mkdir -p docker/unifiedpush-eap/artifacts"
            sh "cp dist/target/*.tar.gz docker/unifiedpush-eap/artifacts/"
            stash name: "docker-ups", includes: "docker/"
        }
    }

    stage('Platform Update') {
        final Map updateParams = [
                componentName: COMPONENT,
                componentVersion: version,
                componentBuild: build,
                changeUrl: CHANGE_URL
        ]
        fhCoreOpenshiftTemplatesComponentUpdate(updateParams)
    }

}

node('master') {
    stage('Build Image') {
        unstash "docker-ups"

        final Map params = [
                fromDir: './docker/unifiedpush-eap',
                buildConfigName: 'aerogear-ups',
                imageRepoSecret: 'dockerhub',
                outputImage: "docker.io/${DOCKER_HUB_ORG}/${DOCKER_HUB_REPO}:${version}-${build}"
        ]

        try {
            buildWithDockerStrategy params
        } finally {
            sh "rm -rf ./docker/"
        }
    }
}
