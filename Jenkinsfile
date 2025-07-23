pipeline {
    agent any

  tools {
    jdk 'OpenJDK'
    maven 'mvn' 
  }

    stages {
        stage('SCM') {
            steps {
                echo '🛠️ [DEBUG] Starting SCM stage: Cloning repository...'
                git credentialsId: 'Private-Repo-SCA-Demo',
                    branch: 'main', 
                    changelog: false, 
                    poll: false, 
                    url: 'https://github.com/rueben-hytech/jenkins-pipeline-sca-demo.git'
                echo '✅ [DEBUG] SCM stage completed.'
            }
        }

        stage('Build') {
            steps {
                echo '🏗️ [DEBUG] Starting Build stage: Running Maven to build the project...'
                sh 'mvn clean package -DskipTests'
                sh  'mvn dependency:copy-dependencies'
                echo '✅ [DEBUG] Build stage completed.'
            }
        }

        stage('ODC') {
            steps {
                echo '🔍 [DEBUG] Starting ODC stage: Running OWASP Dependency-Check...'
                dependencyCheck additionalArguments: '--format XML --format HTML', 
                                 nvdCredentialsId: 'nvd-api-key', 
                                 odcInstallation: 'ODC'
                echo '✅ [DEBUG] ODC stage completed.'
            }
        }

        stage('Snyk SCA') {
            steps {
                echo '🧪 [DEBUG] Running Snyk scan on pom.xml...'
                script {
                    def snykTool = tool name: 'snyk@latest', type: 'com.synopsys.integration.jenkins.extensions.tools.SynkInstallation'
                    env.PATH = "${snykTool}/bin:${env.PATH}"
                }
                withCredentials([string(credentialsId: 'snyk-api-token', variable: 'SNYK_TOKEN')]) {
                    sh 'snyk auth $SNYK_TOKEN'
                    sh 'snyk test --file=pom.xml --all-projects || true'
                }
                echo '✅ [DEBUG] Snyk scan completed.'
            }
        }          
        
        stage('Publish SCA Report') {
            steps {
                echo '📄 [DEBUG] Publishing SCA report using dependencyCheckPublisher...'
                dependencyCheckPublisher pattern: '**/dependency-check-report.xml'
                echo '✅ [DEBUG] SCA report published.'
            }
        }

        stage('Publish HTML Report') {
            steps {
                echo '🌐 [DEBUG] Publishing HTML report to Jenkins UI...'
                publishHTML(target: [
                    allowMissing: false,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: '.',
                    reportFiles: 'dependency-check-report.html',
                    reportName: 'Dependency-Check Report'
                ])
                echo '✅ [DEBUG] HTML report published.'
            }
        }
    }

    post {
        always {
            echo '📦 [DEBUG] Archiving dependency-check report artifacts...'
            archiveArtifacts artifacts: '**/dependency-check-report.*', fingerprint: true
            echo '✅ [DEBUG] Artifact archiving completed.'
        }
    }
}
