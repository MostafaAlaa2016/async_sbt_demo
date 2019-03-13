pipeline {
    agent {
        docker {
            image 'hseeberger/scala-sbt'
        }
    }
    stages {
        stage('Test') {
            steps {
                echo 'Building..'
                sh "sbt test"
            }
        }
    } 
}
