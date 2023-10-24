#!groovy
def call(Map configMap){
    def application_type = configMap.get("application_type")
    def component = configMap.get("component")
pipeline{
    agent{
        node{
            label "AGENT"
        }
    }
    
    
    environment{
        version = ''
    }
    options {
        ansiColor('xterm')
    }
    //  parameters {
    //     string(name: 'component', description: 'version of the artifact to be deployed', defaultValue: '1.0.1')
    // }

    stages{
        stage("Installing dependecies"){
            steps{
            sh 'npm install'
            }
        }
        stage ("extracting the version"){
            steps{
                script {
                    def packageJson = readJSON file: 'package.json'
                    version = packageJson.version
                    
                }
            }

        }
        // stage("sonarscan"){
        //     steps{
        //         sh "sonar-scanner"
        //     }
        // }
        stage("zipping the files"){
            steps{
            sh 'echo "zipping the files"'
            sh "zip -r ${component}.zip ./*  --exclude=.git --exclude=${component}.zip "
            }  
        }
        stage("uploading the artifact"){
            steps{
                nexusArtifactUploader(
                    nexusVersion: 'nexus3',
                    protocol: 'http',
                    nexusUrl: '10.40.30.177:8081/',
                    groupId: 'com.saikiransudhireddy',
                    version: "${version}",
                    repository: "${component}",
                    credentialsId: 'nexus-auth',
                    artifacts: [
                        [artifactId: "${component}",
                            classifier: '',
                            file: "${component}.zip",
                            type: 'zip']
        ]
     )
            }
        }
        stage("deploy"){
            steps{
                sh 'echo "deploying the ${component}"'
                script {
                    // Build the downstream freestyle project
                    def parmter = [string(name:'version',value:"$version")]
                    build job: "../${component}-deploy", wait: true, parameters: parmter
                }
            }
        }
    }
    // post{
    //     always{
    //         deleteDir()
    //     }
    // }    
}

//parameters: "${version}"
}