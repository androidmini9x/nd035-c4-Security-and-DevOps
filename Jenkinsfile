pipeline {
    agent any
    stages {
        stage('Build') { 
            steps {
                sh 'mvn -B -DskipTests -f starter_code/pom.xml clean package' 
            }
        }
    }
}