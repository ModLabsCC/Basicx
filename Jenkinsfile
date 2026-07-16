pipeline {
    agent any

    tools {
        jdk 'jdk25'
    }

    options {
        timeout(time: 20, unit: 'MINUTES')
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '15'))
    }

    // Nur der main-Branch baut; andere Branches werden von Jenkins übersprungen (Stage skipped).
    stages {
        stage('main') {
            when {
                branch 'main'
            }
            stages {
                stage('Checkout') {
                    steps {
                        checkout scm
                    }
                }

                stage('Build') {
                    steps {
                        sh 'chmod +x gradlew'
                        sh './gradlew build --warning-mode all --stacktrace'
                    }
                }
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: 'build/test-results/test/*.xml'
        }
        success {
            archiveArtifacts artifacts: 'build/libs/Basicx-*.jar', fingerprint: true
        }
    }
}
